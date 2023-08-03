package net.swordie.ms.handlers.user;

import net.swordie.ms.client.Client;
import net.swordie.ms.client.character.Char;
import net.swordie.ms.client.character.skills.info.AttackInfo;
import net.swordie.ms.client.character.skills.info.MobAttackInfo;
import net.swordie.ms.client.character.skills.info.SkillInfo;
import net.swordie.ms.client.jobs.Job;
import net.swordie.ms.client.jobs.adventurer.Magician;
import net.swordie.ms.client.jobs.cygnus.BlazeWizard;
import net.swordie.ms.client.jobs.cygnus.NightWalker;
import net.swordie.ms.client.jobs.resistance.Mechanic;
import net.swordie.ms.client.jobs.sengoku.Kanna;
import net.swordie.ms.client.party.PartyMember;
import net.swordie.ms.connection.InPacket;
import net.swordie.ms.connection.packet.*;
import net.swordie.ms.constants.GameConstants;
import net.swordie.ms.constants.SkillConstants;
import net.swordie.ms.enums.ChatType;
import net.swordie.ms.enums.FieldOption;
import net.swordie.ms.handlers.Handler;
import net.swordie.ms.handlers.header.InHeader;
import net.swordie.ms.handlers.header.OutHeader;
import net.swordie.ms.life.Life;
import net.swordie.ms.life.Summon;
import net.swordie.ms.life.mob.Mob;
import net.swordie.ms.loaders.SkillData;
import net.swordie.ms.util.Rect;
import net.swordie.ms.world.event.InGameEvent;
import net.swordie.ms.world.event.InGameEventManager;
import net.swordie.ms.world.event.PinkZakumEvent;
import net.swordie.ms.world.field.Field;
import net.swordie.ms.world.field.fieldeffect.FieldEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

import static net.swordie.ms.handlers.header.InHeader.*;
import static net.swordie.ms.handlers.header.OutHeader.REMOTE_BODY;
import static net.swordie.ms.handlers.header.OutHeader.REMOTE_MELEE_ATTACK;
import static net.swordie.ms.handlers.header.OutHeader.REMOTE_SHOOT_ATTACK;
import static net.swordie.ms.handlers.header.OutHeader.REMOTE_MAGIC_ATTACK;

public class AttackHandler {

    private static final Logger log = LogManager.getLogger(AttackHandler.class);


    // No handler, gets called from other handlers
    private static void handleAttack(Client c, AttackInfo attackInfo) {
        Char chr = c.getChr();
        chr.dbgChatMsg(attackInfo.skillId + "");
        int skillID = attackInfo.skillId;
        Field field = chr.getField();
        if ((field.getFieldLimit() & FieldOption.SkillLimit.getVal()) > 0 ||
                (field.getFieldLimit() & FieldOption.MoveSkillOnly.getVal()) > 0) {
            chr.dispose();
            return;
        }
        boolean summonedAttack = attackInfo.attackHeader == OutHeader.SUMMONED_ATTACK;
        boolean multiAttack = SkillConstants.isMultiAttackCooldownSkill(skillID);
        if (chr.applyBulletCon(attackInfo.skillId, attackInfo.slv) && !summonedAttack && !multiAttack && !chr.applyMpCon(attackInfo.skillId, attackInfo.slv)) {
            return;
        }
        if (summonedAttack || chr.checkAndSetSkillCooltime(skillID) || chr.hasSkillCDBypass() || multiAttack || (SkillConstants.isKeyDownSkill(skillID))) {
            byte slv = attackInfo.slv;
            chr.dbgChatMsg("SkillID: " + skillID);
            Job sourceJobHandler = chr.getJobHandler();
            SkillInfo si = SkillData.getSkillInfoById(skillID);
            if (si != null && !si.getExtraSkillInfo().isEmpty()) {
                chr.getField().broadcastPacket(FieldPacket.registerExtraSkill(chr.getPosition(), skillID, si.getExtraSkillInfo().keySet(), attackInfo.left));
            }
            if (si != null && si.isMassSpell() && sourceJobHandler.isBuff(skillID) && chr.getParty() != null) {
                Rect r = si.getFirstRect();
                if (r != null) {
                    Rect rectAround = chr.getRectAround(r);
                    chr.getParty().getOnlineMembers().stream().filter(pm -> pm.getChr() != null && pm.getChr().getField() == chr.getField()
                            && rectAround.hasPositionInside(pm.getChr().getPosition())).map(PartyMember::getChr).forEach(ptChr -> {
                        Effect effect = Effect.skillAffected(skillID, slv, 0);
                        if (ptChr != chr) {  // Caster shouldn't get the Affected Skill Effect
                            chr.getField().broadcastPacket(
                                    UserRemote.effect(ptChr.getId(), effect)
                                    , ptChr);
                            ptChr.write(UserPacket.effect(effect));
                            sourceJobHandler.handleAttack(c, attackInfo);
                        }
                    });
                }
            }

            sourceJobHandler.handleAttack(c, attackInfo);

            if (attackInfo.attackHeader != null) {
                switch (attackInfo.attackHeader) {
                    case SUMMONED_ATTACK -> {
                        chr.getField().broadcastPacket(Summoned.summonedAttack(chr.getId(), attackInfo, false), chr);
                    }
                    case FAMILIAR_ATTACK -> {
                        chr.getField().broadcastPacket(CFamiliar.familiarAttack(chr.getId(), attackInfo), chr);
                    }
                    default -> chr.getField().broadcastPacket(UserRemote.attack(chr, attackInfo), chr);
                }
            }

            int multiKillMessage = 0;
            long mobexp = 0;

            for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                var mob = (Mob) field.getLifeByObjectID(mai.mobId);
                if (mob == null) {
                    chr.chatMessage(ChatType.Expedition, String.format("Wrong attack info parse (probably)! SkillID = %d, Mob ID = %d", skillID, mai.mobId));
                } else { // this check was making the sponge not die because it skips the last bit of hp from the dead mob
                    long totalDamage = Arrays.stream(mai.damages).asLongStream().sum();
                    if (mob.getHp() > 0) {
                        mob.damage(chr, totalDamage);
                        mob.handleDamageReflect(chr, skillID, totalDamage);
                    }

                    if (mob.getTemplateId() == 9400902) { // pink zakum third body
                        InGameEvent event = InGameEventManager.getInstance().getActiveEvent();

                        if (event instanceof PinkZakumEvent) {
                            ((PinkZakumEvent) event).win();
                        }
                    }

                    if ((mob.getTemplateId() >= 8810202 && mob.getTemplateId() <= 8810209)) {
                        handleHorntailHPBar(field, chr, totalDamage, (byte) 2); // easy horntail
                    } else if ((mob.getTemplateId() >= 8810002 && mob.getTemplateId() <= 8810009)) {
                        handleHorntailHPBar(field, chr, totalDamage, (byte) 0); // normal horntail
                    } else if ((mob.getTemplateId() >= 8810102 && mob.getTemplateId() <= 8810109)) {
                        handleHorntailHPBar(field, chr, totalDamage, (byte) 1); // chaos horntail
                    } else if (mob.getTemplateId() >= 8820001 && mob.getTemplateId() <= 8820006) {
                        handlePinkBeanHPBar(field, chr, totalDamage, false);
                    } else if (mob.getTemplateId() >= 8820101 && mob.getTemplateId() <= 8820106) {
                        handlePinkBeanHPBar(field, chr, totalDamage, true);
                    } else if ((mob.getTemplateId() >= 8830000 && mob.getTemplateId() <= 8830002)) {
                        handleBalrogHPBar(field, chr, totalDamage, false);
                    } else if ((mob.getTemplateId() >= 8830007 && mob.getTemplateId() <= 8830009)) {
                        handleBalrogHPBar(field, chr, totalDamage, true);
                    }
                }

                if (mob != null && mob.getHp() < 0) {
                    mob.onKilledByChar(chr);
                    // MultiKill +1,  per killed mob
                    multiKillMessage++;
                    mobexp = mob.getForcedMobStat().getExp();
                }
            }

            // MultiKill Message Popup & Exp
            if (multiKillMessage > 2) {
                int bonusExpMultiplier = (multiKillMessage - 2) * 5;
                long totalBonusExp = (long) (mobexp * (bonusExpMultiplier * GameConstants.MULTI_KILL_BONUS_EXP_MULTIPLIER));
                chr.write(UserLocal.comboCounter((byte) 0, (int) totalBonusExp, Math.min(multiKillMessage, 10)));
                chr.addExpNoMsg(totalBonusExp);
            }
        }
    }

    private static void handleHorntailHPBar(Field field, Char chr, long totalDamage, byte type) {
        var options = new int[][]{
                {8810018}, // normal has one dmg sink
                {8810118, 8810119, 8810120, 8810121, 8810122}, // chaos has five dmg sinks
                {8810214}, // easy has one dmg sink
        };

        for (int i = 0; i < options[type].length; i++) {
            Life htDmgSoak = field.getLifeByTemplateId(options[type][i]);
            if (htDmgSoak != null) {
                Mob ht = (Mob) htDmgSoak;
                ht.damage(chr, totalDamage);
                field.broadcastPacket(FieldPacket.fieldEffect(FieldEffect.mobHPTagFieldEffect(ht)));
                return;
            }
        }
    }

    private static void handlePinkBeanHPBar(Field field, Char chr, long totalDamage, boolean chaos) {
        int pbDmgSoakId = chaos ? 8820110 : 8820010;
        for (int i = 0; i < 5; i++) { // pb has one dmg sink for each 'round' of the statues spawning so we need to loop thru the possibilities
            Life cpbDmgSoak = field.getLifeByTemplateId(pbDmgSoakId + i);
            if (cpbDmgSoak != null) {
                Mob cpb = (Mob) cpbDmgSoak;
                cpb.damage(chr, totalDamage);
                field.broadcastPacket(FieldPacket.fieldEffect(FieldEffect.mobHPTagFieldEffect(cpb)));
                return;
            }
        }
    }

    private static void handleBalrogHPBar(Field field, Char chr, long totalDamage, boolean easy) {
        Life balrogDmgSoak = field.getLifeByTemplateId(easy ? 8830003 : 8830010);
        if (balrogDmgSoak != null) {
            Mob balrog = (Mob) balrogDmgSoak;
            balrog.damage(chr, totalDamage);
            field.broadcastPacket(FieldPacket.fieldEffect(FieldEffect.mobHPTagFieldEffect(balrog)));
        }
    }

    @Handler(op = USER_BODY_ATTACK)
    public static void handleBodyAttack(Client c, InPacket inPacket) {
        AttackInfo ai = new AttackInfo();
        ai.attackHeader = REMOTE_BODY;
        ai.fieldKey = inPacket.decodeByte();
        byte mask = inPacket.decodeByte();
        ai.hits = (byte) (mask & 0xF);
        ai.mobCount = (mask >>> 4) & 0xF;
        ai.skillId = inPacket.decodeInt();
        ai.slv = inPacket.decodeByte();

        inPacket.decodeInt(); // crc

        ai.areaPAD = inPacket.decodeByte() >>> 3;
        byte nul = inPacket.decodeByte(); // encoded as 0
        ai.left = ((inPacket.decodeShort() >>> 15) & 1) != 0;
        ai.attackCount = inPacket.decodeInt();
        ai.attackSpeed = inPacket.decodeByte(); // encoded as 0
        ai.wt = inPacket.decodeInt();
        ai.ar01Mad = inPacket.decodeInt(); // only done if mage skill
        byte idk2 = inPacket.decodeByte();

        if (ai.skillId > 0) {
            for (int i = 0; i < ai.mobCount; i++) {
                MobAttackInfo mai = new MobAttackInfo();
                mai.mobId = inPacket.decodeInt();
                mai.idkInt = inPacket.decodeInt();
                mai.calcDamageStatIndex = inPacket.decodeByte();
                mai.templateID = inPacket.decodeInt();
                mai.rect = new Rect(inPacket.decodePosition(), inPacket.decodePosition());
                mai.idk6 = inPacket.decodeShort();
                mai.hitAction = inPacket.decodeByte();
                int[] damages = new int[ai.hits];
                for (int j = 0; j < ai.hits; j++) {
                    damages[j] = inPacket.decodeInt();
                }
                mai.damages = damages;
                mai.mobUpDownYRange = inPacket.decodeInt();
                inPacket.decodeInt(); // crc

                // Begin PACKETMAKER::MakeAttackInfoPacket
                mai.type = inPacket.decodeByte();
                mai.currentAnimationName = "";
                if (mai.type == 1) {
                    mai.currentAnimationName = inPacket.decodeString();
                    mai.animationDeltaL = inPacket.decodeInt();
                    mai.hitPartRunTimesSize = inPacket.decodeInt();
                    mai.hitPartRunTimes = new String[mai.hitPartRunTimesSize];
                    for (int j = 0; j < mai.hitPartRunTimesSize; j++) {
                        mai.hitPartRunTimes[j] = inPacket.decodeString();
                    }
                } else if (mai.type == 2) {
                    mai.currentAnimationName = inPacket.decodeString();
                    mai.animationDeltaL = inPacket.decodeInt();
                }
                // End PACKETMAKER::MakeAttackInfoPacket

                inPacket.decodeInt();//AA D6 0A ED

                ai.mobAttackInfo.add(mai);
            }
        }
        ai.pos = inPacket.decodePosition();
        handleAttack(c, ai);
    }


    @Handler(op = SUMMONED_ATTACK)
    public static void handleSummonedAttack(Client c, InPacket inPacket) {
        Char chr = c.getChr();
        Field field = chr.getField();
        AttackInfo ai = new AttackInfo();
        int summonedID = inPacket.decodeInt();
        ai.attackHeader = OutHeader.SUMMONED_ATTACK;
        ai.summon = (Summon) field.getLifeByObjectID(summonedID);
        ai.updateTime = inPacket.decodeInt();
        ai.skillId = inPacket.decodeInt();
        inPacket.decodeInt(); // hardcoded 0
        byte leftAndAction = inPacket.decodeByte();
        ai.attackActionType = (byte) (leftAndAction & 0x7F);
        ai.left = (byte) (leftAndAction >>> 7) != 0;
        byte mask = inPacket.decodeByte();
        ai.hits = (byte) (mask & 0xF);
        ai.mobCount = (mask >>> 4) & 0xF;
        inPacket.decodeByte(); // hardcoded 0
        if (ai.skillId == Mechanic.ROCK_N_SHOCK) {
            for (int i = 0; i < 3; i++) {
                inPacket.decodeInt(); //rock n shock life ids, not needed
            }
        }
        ai.attackAction = inPacket.decodeShort();
        ai.attackCount = inPacket.decodeShort();
        ai.pos = inPacket.decodePosition();
        inPacket.decodeInt(); // hardcoded -1
        short idk3 = inPacket.decodeShort();
        int idk4 = inPacket.decodeInt();
        inPacket.decodeInt(); // hardcoded 0
        ai.bulletID = inPacket.decodeInt();
        for (int i = 0; i < ai.mobCount; i++) {
            MobAttackInfo mai = new MobAttackInfo();
            mai.mobId = inPacket.decodeInt();
            mai.templateID = inPacket.decodeInt();
            mai.byteIdk1 = inPacket.decodeByte();
            mai.byteIdk2 = inPacket.decodeByte();
            mai.byteIdk3 = inPacket.decodeByte();
            mai.byteIdk4 = inPacket.decodeByte();
            mai.byteIdk5 = inPacket.decodeByte();
            int idk5 = inPacket.decodeInt(); // another template id, same as the one above
            byte byteIdk6 = inPacket.decodeByte();
            mai.rect = inPacket.decodeShortRect();
            short idk6 = inPacket.decodeShort();
            int[] damages = new int[ai.hits];
            for (int j = 0; j < ai.hits; j++) {
                damages[j] = inPacket.decodeInt();
            }
            mai.damages = damages;
            mai.mobUpDownYRange = inPacket.decodeInt();
            inPacket.decodeInt(); // crc

            // Begin PACKETMAKER::MakeAttackInfoPacket
            mai.type = inPacket.decodeByte();
            mai.currentAnimationName = "";
            if (mai.type == 1) {
                mai.currentAnimationName = inPacket.decodeString();
                mai.animationDeltaL = inPacket.decodeInt();
                mai.hitPartRunTimesSize = inPacket.decodeInt();
                mai.hitPartRunTimes = new String[mai.hitPartRunTimesSize];
                for (int j = 0; j < mai.hitPartRunTimesSize; j++) {
                    mai.hitPartRunTimes[j] = inPacket.decodeString();
                }
            } else if (mai.type == 2) {
                mai.currentAnimationName = inPacket.decodeString();
                mai.animationDeltaL = inPacket.decodeInt();
            }
            // End PACKETMAKER::MakeAttackInfoPacket

            inPacket.decodeInt();//AA D6 0A ED

            ai.mobAttackInfo.add(mai);
        }
        handleAttack(c, ai);
    }

    @Handler(ops = {USER_MELEE_ATTACK, USER_SHOOT_ATTACK, USER_MAGIC_ATTACK,
            USER_NON_TARGET_FORCE_ATOM_ATTACK, USER_AREA_DOT_ATTACK})
    public static void handleAttack(Char chr, InPacket inPacket, InHeader header) {
        AttackInfo ai = new AttackInfo();
        switch (header) {
            case USER_MELEE_ATTACK:
                ai.attackHeader = REMOTE_MELEE_ATTACK;
                break;
            case USER_SHOOT_ATTACK:
                ai.boxAttack = inPacket.decodeByte() != 0; // hardcoded 0
                ai.attackHeader = REMOTE_SHOOT_ATTACK;
                break;
            case USER_NON_TARGET_FORCE_ATOM_ATTACK:
                inPacket.decodeArr(12); // id/crc/something else
            case USER_MAGIC_ATTACK:
                ai.attackHeader = REMOTE_MAGIC_ATTACK;
                break;

        }
        ai.bulletID = chr.getBulletIDForAttack();
        ai.fieldKey = inPacket.decodeByte();
        byte mask = inPacket.decodeByte();
        ai.hits = (byte) (mask & 0xF);
        ai.mobCount = (mask >>> 4) & 0xF;
        ai.skillId = inPacket.decodeInt();
        ai.slv = inPacket.decodeByte();
        if (header == InHeader.USER_MELEE_ATTACK || header == InHeader.USER_SHOOT_ATTACK
                || header == InHeader.USER_NON_TARGET_FORCE_ATOM_ATTACK) {
            ai.addAttackProc = inPacket.decodeByte();
        }

        inPacket.decodeInt(); // crc

        int skillID = ai.skillId;
        if (SkillConstants.isKeyDownSkill(skillID) || SkillConstants.isSuperNovaSkill(skillID)) {
            ai.keyDown = inPacket.decodeInt();
        }
        if (SkillConstants.isRushBombSkill(skillID) || skillID == 5300007 || skillID == 27120211 || skillID == 14111023) {
            ai.grenadeId = inPacket.decodeInt();
        }
        if (SkillConstants.isZeroSkill(skillID)) {
            ai.zero = inPacket.decodeByte();
        }
        if (SkillConstants.isUsercloneSummonedAbleSkill(skillID)) {
            ai.bySummonedID = inPacket.decodeInt();
        }
        ai.buckShot = inPacket.decodeByte();
        ai.someMask = inPacket.decodeByte();
        if (header == InHeader.USER_SHOOT_ATTACK) {
            int idk3 = inPacket.decodeInt();
            ai.isJablin = inPacket.decodeByte() != 0;
            if (ai.boxAttack) {
                int boxIdk1 = inPacket.decodeInt();
                short boxIdk2 = inPacket.decodeShort();
                short boxIdk3 = inPacket.decodeShort();
            }
        }
        short maskie = inPacket.decodeShort();
        ai.left = ((maskie >>> 15) & 1) != 0;
        ai.attackAction = (short) (maskie & 0x7FFF);
        ai.requestTime = inPacket.decodeInt();
        ai.attackActionType = inPacket.decodeByte();
        if (skillID == 23111001 || skillID == 80001915 || skillID == 36111010) {
            int idk5 = inPacket.decodeInt();
            int x = inPacket.decodeInt(); // E0 6E 1F 00
            int y = inPacket.decodeInt();
        }
        if (SkillConstants.isEvanForceSkill(skillID)) {
            ai.idk0 = inPacket.decodeByte();
        }
        ai.attackSpeed = inPacket.decodeByte();
        ai.tick = inPacket.decodeInt();

/*        if (header == InHeader.USER_AREA_DOT_ATTACK) {
            ai.affectedAreaObjId = inPacket.decodeInt();
        }*/

        if (header != InHeader.USER_MAGIC_ATTACK && header != InHeader.USER_NON_TARGET_FORCE_ATOM_ATTACK) {
            int bulletSlot = inPacket.decodeInt();
        }

        if (header == InHeader.USER_MELEE_ATTACK || header == InHeader.USER_SHOOT_ATTACK) {
            ai.finalAttackLastSkillID = inPacket.decodeInt();
            if (ai.finalAttackLastSkillID > 0) {
                ai.finalAttackByte = inPacket.decodeByte();
            }
        } else if (header == InHeader.USER_MAGIC_ATTACK || header == InHeader.USER_NON_TARGET_FORCE_ATOM_ATTACK) {
            int idk = inPacket.decodeInt();
        } else {
            short idk8 = inPacket.decodeShort();
            short idk9 = inPacket.decodeShort();
        }

        if (header == InHeader.USER_NON_TARGET_FORCE_ATOM_ATTACK) {
            inPacket.decodeInt(); // hardcoded 0
        }

        if (header == InHeader.USER_SHOOT_ATTACK) {
            int bulletSlot = inPacket.decodeInt();
            // this looks correct in ida, but completely wrong when comparing it to the actual packet
/*            ai.shootRange = inPacket.decodeByte();
            if (!SkillConstants.isShootSkillNotConsumingBullets(skillID)
                    || chr.getTemporaryStatManager().hasStat(CharacterTemporaryStat.SoulArrow)) {
                ai.bulletCount = inPacket.decodeInt();
            }*/
            byte idk = inPacket.decodeByte();
            if ((bulletSlot == 0 || idk == 0) && (ai.buckShot & 0x40) != 0 && !SkillConstants.isFieldAttackObjSkill(skillID)) {
                int maybeID = inPacket.decodeInt();
            }
            ai.rect = inPacket.decodeShortRect();
        }

        if (skillID == 5111009) {
            ai.ignorePCounter = inPacket.decodeByte() != 0;
        }
        /*if ( v1756 )
          {
            COutPacket::Encode2(&a, v1747);
            if ( v674 || is_noconsume_usebullet_melee_attack(v669) )
              COutPacket::Encode4(&a, v1748);
          }*/
        if (SkillConstants.isNoConsumeBullet(skillID) && header == InHeader.USER_MELEE_ATTACK) {
            inPacket.decodeShort();
            if (skillID == 14121052 || skillID == NightWalker.SHADOW_BAT_ATOM) {
                inPacket.decodeInt();
            }
        }
        if (skillID == 25111005) {
            ai.spiritCoreEnhance = inPacket.decodeInt();
        }

        for (int i = 0; i < ai.mobCount; i++) {
            MobAttackInfo mai = new MobAttackInfo();
            mai.mobId = inPacket.decodeInt();
            mai.hitAction = inPacket.decodeByte();//07
            mai.left = inPacket.decodeByte();
            mai.idk3 = inPacket.decodeByte();
            mai.forceActionAndLeft = inPacket.decodeByte();
            mai.frameIdx = inPacket.decodeByte();
            mai.templateID = inPacket.decodeInt();
            mai.calcDamageStatIndexAndDoomed = inPacket.decodeByte(); // 1st bit for bDoomed, rest for calcDamageStatIndex
            mai.hitX = inPacket.decodeShort();
            mai.hitY = inPacket.decodeShort();
            mai.oldPosX = inPacket.decodeShort(); // ?
            mai.oldPosY = inPacket.decodeShort(); // ?
            if (header == InHeader.USER_MAGIC_ATTACK) {
                mai.hpPerc = inPacket.decodeByte();
                if (skillID == 80001835 || skillID == Kanna.SOUL_SHEAR) {
                    mai.magicInfo = inPacket.decodeByte();
                    ai.hits = (byte) mai.magicInfo;
                } else {
                    mai.magicInfo = inPacket.decodeShort();
                }
            } else {
                mai.idk6 = inPacket.decodeShort();
            }

            mai.damages = new int[ai.hits];
            for (int j = 0; j < ai.hits; j++) {
                mai.damages[j] = inPacket.decodeInt();
            }

            mai.mobUpDownYRange = inPacket.decodeInt();
            inPacket.decodeInt(); // crc
            if (skillID == 37111005) {
                mai.isResWarriorLiftPress = inPacket.decodeByte() != 0;
            }
            if (SkillConstants.isKinesisPsychicLockSkill(skillID)) {
                int idfk = inPacket.decodeInt();
                int idfk1 = inPacket.decodeInt();
            }

            // Begin PACKETMAKER::MakeAttackInfoPacket
            mai.type = inPacket.decodeByte();
            mai.currentAnimationName = "";
            if (mai.type == 1) {
                mai.currentAnimationName = inPacket.decodeString();
                mai.animationDeltaL = inPacket.decodeInt();
                mai.hitPartRunTimesSize = inPacket.decodeInt();
                mai.hitPartRunTimes = new String[mai.hitPartRunTimesSize];
                for (int j = 0; j < mai.hitPartRunTimesSize; j++) {
                    mai.hitPartRunTimes[j] = inPacket.decodeString();
                }
            } else if (mai.type == 2) {
                mai.currentAnimationName = inPacket.decodeString();
                mai.animationDeltaL = inPacket.decodeInt();
            }
            // End PACKETMAKER::MakeAttackInfoPacket

            inPacket.decodeInt();//C0 61 CD 53

            ai.mobAttackInfo.add(mai);
        }

        if (skillID == 61121052 || skillID == 36121052 || SkillConstants.isScreenCenterAttackSkill(skillID)) {
            ai.ptTarget.setX(inPacket.decodeShort());
            ai.ptTarget.setY(inPacket.decodeShort());
        } else {
            if (skillID == 27121052 || skillID == 80001837) {
                ai.x = inPacket.decodeShort();
                ai.y = inPacket.decodeShort();
            }
            if (header == InHeader.USER_MAGIC_ATTACK && skillID != 32111016 && !SkillConstants.isKinesisPsychicAreaSkill(skillID)) {
                short forcedX = inPacket.decodeShort();
                short forcedY = inPacket.decodeShort();
                boolean dragon = inPacket.decodeByte() != 0;
                ai.forcedX = forcedX;
                ai.forcedY = forcedY;
                if (dragon) {
                    short rcDstRight = inPacket.decodeShort();
                    short rectRight = inPacket.decodeShort();
                    short x = inPacket.decodeShort();
                    short y = inPacket.decodeShort();
                    inPacket.decodeByte(); // always 0
                    inPacket.decodeByte(); // -1
                    inPacket.decodeByte(); // 0
                    ai.rcDstRight = rcDstRight;
                    ai.rectRight = rectRight;
                    ai.x = x;
                    ai.y = y;
                }
            }
            if (skillID == BlazeWizard.IGNITION_EXPLOSION) {
                ai.option = inPacket.decodeInt();
            }
            if (skillID == Magician.MIST_ERUPTION) {
                byte size = inPacket.decodeByte();
                int[] mists = new int[size];
                for (int i = 0; i < size; i++) {
                    mists[i] = inPacket.decodeInt();
                }
                ai.mists = mists;
            }
            if (skillID == Magician.POISON_MIST) {
                byte force = inPacket.decodeByte();
                short forcedXSh = inPacket.decodeShort();
                short forcedYSh = inPacket.decodeShort();
                ai.force = force;
                ai.forcedXSh = forcedXSh;
                ai.forcedYSh = forcedYSh;
            }
            if (skillID == 80001835) { // Soul Shear
                byte sizeB = inPacket.decodeByte();
                int[] idkArr2 = new int[sizeB];
                short[] shortArr2 = new short[sizeB];
                for (int i = 0; i < sizeB; i++) {
                    idkArr2[i] = inPacket.decodeInt();
                    shortArr2[i] = inPacket.decodeShort();
                }
                short delay = inPacket.decodeShort();
                ai.mists = idkArr2;
                ai.shortArr = shortArr2;
                ai.delay = delay;
            }
            if (SkillConstants.isSuperNovaSkill(skillID)) {
                ai.ptAttackRefPoint.setX(inPacket.decodeShort());
                ai.ptAttackRefPoint.setY(inPacket.decodeShort());
            }
            if (skillID == 101000102) { // Air Riot
                ai.idkPos = inPacket.decodePosition();
            }
            if (header == InHeader.USER_AREA_DOT_ATTACK) {
                ai.pos.setX(inPacket.decodeShort());
                ai.pos.setY(inPacket.decodeShort());
            }
            if (SkillConstants.isAranFallingStopSkill(skillID)) {
                ai.fh = inPacket.decodeByte();
            }
            if (header == InHeader.USER_SHOOT_ATTACK && skillID / 1000000 == 33) {
                ai.bodyRelMove = inPacket.decodePosition();
            }
            if (skillID == 21120019 || skillID == 37121052) {
                ai.teleportPt.setX(inPacket.decodeInt());
                ai.teleportPt.setY(inPacket.decodeInt());
            }
            if (header == InHeader.USER_SHOOT_ATTACK && SkillConstants.isKeydownSkillRectMoveXY(skillID)) {
                ai.keyDownRectMoveXY = inPacket.decodePosition();
            }
            if (skillID == 61121105 || skillID == 61121222 || skillID == 24121052) {
                ai.Vx = inPacket.decodeShort();
                short x, y;
                for (int i = 0; i < ai.Vx; i++) {
                    x = inPacket.decodeShort();
                    y = inPacket.decodeShort();
                }
            }
            if (skillID == 101120104) {
                // CUser::EncodeAdvancedEarthBreak
                // TODO
            }
            if (skillID == 14111006 && ai.grenadeId != 0) {
                ai.grenadePos.setX(inPacket.decodeShort());
                ai.grenadePos.setY(inPacket.decodeShort());
            }
            if (/*skillID == 23121002 ||*/ skillID == 80001914) { // first skill is Spikes Royale, not needed?
                ai.fh = inPacket.decodeByte();
            }
        }
        handleAttack(chr.getClient(), ai);
    }

    @Handler(op = FAMILIAR_ATTACK)
    public static void handleFamiliarAttack(Char chr, InPacket inPacket) {
        inPacket.decodeByte(); // ?
        int familiarID = inPacket.decodeInt();
        if (chr.getActiveFamiliar() == null || chr.getActiveFamiliar().getFamiliarID() != familiarID) {
            return;
        }
        AttackInfo ai = new AttackInfo();
        ai.attackHeader = OutHeader.FAMILIAR_ATTACK;
        ai.fieldKey = inPacket.decodeByte();
        ai.skillId = inPacket.decodeInt();
        ai.idk = inPacket.decodeByte();
        ai.slv = 1;
        ai.mobCount = inPacket.decodeByte();
        for (int i = 0; i < ai.mobCount; i++) {
            MobAttackInfo mai = new MobAttackInfo();
            mai.mobId = inPacket.decodeInt();
            mai.byteIdk1 = inPacket.decodeByte();
            mai.byteIdk2 = inPacket.decodeByte();
            mai.byteIdk3 = inPacket.decodeByte();
            mai.byteIdk4 = inPacket.decodeByte();
            mai.byteIdk5 = inPacket.decodeByte();
            int idk1 = inPacket.decodeInt();
            byte idk2 = inPacket.decodeByte();
            int idk3 = inPacket.decodeInt();
            mai.damages = new int[inPacket.decodeByte()];
            for (int j = 0; j < mai.damages.length; j++) {
                mai.damages[j] = inPacket.decodeInt();
            }
            ai.mobAttackInfo.add(mai);
        }
        handleAttack(chr.getClient(), ai);
        // 4 more bytes after this, not sure what it is
    }

}
