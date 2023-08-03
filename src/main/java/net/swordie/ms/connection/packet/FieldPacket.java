package net.swordie.ms.connection.packet;

import net.swordie.ms.client.character.Char;
import net.swordie.ms.client.character.CharacterStat;
import net.swordie.ms.client.character.MarriageRecord;
import net.swordie.ms.client.character.items.BodyPart;
import net.swordie.ms.client.character.items.Equip;
import net.swordie.ms.client.character.items.PetItem;
import net.swordie.ms.client.character.items.ScrollUpgradeInfo;
import net.swordie.ms.client.character.keys.FuncKeyMap;
import net.swordie.ms.client.character.runestones.RuneStone;
import net.swordie.ms.client.character.skills.PsychicArea;
import net.swordie.ms.client.character.skills.TownPortal;
import net.swordie.ms.client.character.skills.info.ForceAtomInfo;
import net.swordie.ms.client.jobs.resistance.OpenGate;
import net.swordie.ms.client.trunk.TrunkDlg;
import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.constants.BossConstants;
import net.swordie.ms.constants.ItemConstants;
import net.swordie.ms.constants.SkillConstants;
import net.swordie.ms.enums.*;
import net.swordie.ms.handlers.PsychicLock;
import net.swordie.ms.life.AffectedArea;
import net.swordie.ms.life.mob.Mob;
import net.swordie.ms.loaders.containerclasses.MakingSkillRecipe;
import net.swordie.ms.util.FileTime;
import net.swordie.ms.util.Position;
import net.swordie.ms.util.Rect;
import net.swordie.ms.util.container.Triple;
import net.swordie.ms.world.field.ClockPacket;
import net.swordie.ms.world.field.fieldeffect.FieldEffect;
import net.swordie.ms.world.field.obtacleatom.ObtacleAtomInfo;
import net.swordie.ms.world.field.obtacleatom.ObtacleInRowInfo;
import net.swordie.ms.world.field.obtacleatom.ObtacleRadianInfo;

import java.util.*;
import java.util.stream.IntStream;

import static net.swordie.ms.handlers.header.OutHeader.*;

public class FieldPacket {

    public static OutPacket funcKeyMappedManInit(Char chr) {
        var outPacket = new OutPacket(FUNC_KEY_MAPPED_MAN_INIT);

        if (chr.getFuncKeyMap().getKeymap() == null || chr.getFuncKeyMap().getKeymap().size() == 0) {
            outPacket.encodeByte(true);
        } else {
            outPacket.encodeByte(false);
            chr.getFuncKeyMap().encode(outPacket);
        }

        return outPacket;
    }

/*    public static OutPacket funcKeyMappedManInit(FuncKeyMap funcKeyMap) {
        var outPacket = new OutPacket(FUNC_KEY_MAPPED_MAN_INIT);

        if (funcKeyMap.getKeymap() == null || funcKeyMap.getKeymap().size() == 0) {
            outPacket.encodeByte(true);
        } else {
            outPacket.encodeByte(false);
            funcKeyMap.encode(outPacket);
        }

        return outPacket;
    }*/

    public static OutPacket beastTamerFuncKeyMappedManInit(List<FuncKeyMap> funcKeyMaps) {
        var outPacket = new OutPacket(FUNC_KEY_MAPPED_MAN_INIT);
        outPacket.encodeByte(false);
        funcKeyMaps.forEach(funcKeyMap -> funcKeyMap.encode(outPacket));

        return outPacket;
    }

    public static OutPacket affectedAreaCreated(AffectedArea aa) {
        var outPacket = new OutPacket(AFFECTED_AREA_CREATED);

        outPacket.encodeInt(aa.getObjectId());
        outPacket.encodeByte(aa.getMobOrigin());
        if (aa.getMobOrigin() > 0) {
            outPacket.encodeInt(aa.getMobOwnerOID());
        } else {
            outPacket.encodeInt(aa.getOwner().getId());
        }
        outPacket.encodeInt(aa.getSkillID());
        outPacket.encodeByte(aa.getSlv());
        outPacket.encodeShort(aa.getDelay());
        aa.getRect().encode(outPacket);
        outPacket.encodeInt(aa.getElemAttr());
        outPacket.encodeInt(aa.getElemAttr()); // ?
        outPacket.encodePosition(aa.getPosition());
        outPacket.encodeInt(aa.getForce());
        outPacket.encodeInt(aa.getOption());
        outPacket.encodeByte(aa.getOption() != 0);
        outPacket.encodeInt(aa.getDuration());
        if (SkillConstants.isFlipAffectAreaSkill(aa.getSkillID())) {
            outPacket.encodeByte(aa.isFlip());
        }
        outPacket.encodeInt(0); // ?
        return outPacket;
    }

    public static OutPacket affectedAreaRemoved(AffectedArea aa, boolean mistEruption) {
        var outPacket = new OutPacket(AFFECTED_AREA_REMOVED);

        outPacket.encodeInt(aa.getObjectId());
        if (aa.getSkillID() == 2111003) {
            outPacket.encodeByte(mistEruption);
        }

        return outPacket;
    }

    public static OutPacket createForceAtom(boolean byMob, int userOwner, int targetID, int forceAtomType, boolean toMob,
                                            int targets, int skillID, ForceAtomInfo fai, Rect rect, int arriveDir, int arriveRange,
                                            Position forcedTargetPos, int bulletID, Position pos) {
        List<Integer> integers = new ArrayList<>();
        integers.add(targets);
        List<ForceAtomInfo> forceAtomInfos = new ArrayList<>();
        forceAtomInfos.add(fai);
        return createForceAtom(byMob, userOwner, targetID, forceAtomType, toMob, integers, skillID, forceAtomInfos,
                rect, arriveDir, arriveRange, forcedTargetPos, bulletID, pos);
    }

    public static OutPacket curNodeEventEnd(boolean enable) {
        var outPacket = new OutPacket(CUR_NODE_EVENT_END);

        outPacket.encodeByte(enable);

        return outPacket;
    }

    public static OutPacket createForceAtom(boolean byMob, int userOwner, int charID, int forceAtomType, boolean toMob,
                                            List<Integer> targets, int skillID, List<ForceAtomInfo> faiList, Rect rect, int arriveDir, int arriveRange,
                                            Position forcedTargetPos, int bulletID, Position pos) {
        var outPacket = new OutPacket(CREATE_FORCE_ATOM);

        outPacket.encodeByte(byMob);
        if (byMob) {
            outPacket.encodeInt(userOwner);
        }
        outPacket.encodeInt(charID);
        outPacket.encodeInt(forceAtomType);
        if (forceAtomType != 0 && forceAtomType != 9 && forceAtomType != 14) {
            outPacket.encodeByte(toMob);
            switch (forceAtomType) {
                case 2:
                case 3:
                case 6:
                case 7:
                case 11:
                case 12:
                case 13:
                case 17:
                case 19:
                case 20:
                case 23:
                case 24:
                case 25:
                    outPacket.encodeInt(targets.size());
                    targets.forEach(outPacket::encodeInt);
                    break;
                default:
                    outPacket.encodeInt(targets.get(0));
                    break;
            }
            outPacket.encodeInt(skillID);
        }
        for (ForceAtomInfo fai : faiList) {
            outPacket.encodeByte(1);
            fai.encode(outPacket);
        }
        outPacket.encodeByte(0);
        switch (forceAtomType) {
            case 11:
                outPacket.encodeRectInt(rect);
                outPacket.encodeInt(bulletID);
                break;
            case 9:
            case 15:
                outPacket.encodeRectInt(rect);
                break;
            case 16:
                outPacket.encodePositionInt(pos);
                break;
            case 17:
                outPacket.encodeInt(arriveDir);
                outPacket.encodeInt(arriveRange);
                break;
            case 18:
                outPacket.encodePositionInt(forcedTargetPos);
                break;
        }

        return outPacket;
    }

    public static OutPacket finalAttackRequest(Char chr, int skillID, int finalSkillID, int delay, int mobID,
                                               int userRequestTime) {
        return finalAttackRequest(chr, skillID, finalSkillID, delay, mobID, userRequestTime, false, null);
    }

    public static OutPacket finalAttackRequest(Char chr, int skillID, int finalSkillID, int delay, int mobID,
                                               int userRequestTime, boolean left, Position base) {
        var outPacket = new OutPacket(FINAL_ATTACK_REQUEST);

        int wt = ItemConstants.getWeaponType(chr.getEquippedItemByBodyPart(BodyPart.Weapon).getItemId());

        outPacket.encodeInt(skillID);
        outPacket.encodeInt(finalSkillID);
        outPacket.encodeInt(wt);
        outPacket.encodeInt(delay);
        outPacket.encodeInt(mobID);
        outPacket.encodeInt(userRequestTime);
        if (skillID == 101000102) { // Air Riot
            outPacket.encodeByte(left);
            outPacket.encodePosition(base);
        }

        return outPacket;
    }

    public static OutPacket setAmmo(int ammo) {
        var outPacket = new OutPacket(SET_AMMO);

        outPacket.encodeInt(ammo);

        return outPacket;
    }

    public static OutPacket createPsychicArea(int charID, PsychicArea pa) {
        var outPacket = new OutPacket(CREATE_PSYCHIC_AREA);

        outPacket.encodeInt(charID);

        outPacket.encodeByte(pa.success);
        outPacket.encodeInt(pa.action);
        outPacket.encodeInt(pa.actionSpeed);
        outPacket.encodeInt(pa.psychicAreaKey);
        outPacket.encodeInt(pa.skillID);
        outPacket.encodeShort(pa.slv);
        outPacket.encodeInt(pa.localPsychicAreaKey);
        outPacket.encodeInt(pa.duration);
        outPacket.encodeByte(pa.isLeft);
        outPacket.encodeShort(pa.skeletonFilePathIdx);
        outPacket.encodeShort(pa.skeletonAniIdx);
        outPacket.encodeShort(pa.skeletonLoop);
        outPacket.encodePositionInt(pa.start);

        return outPacket;
    }

    public static OutPacket releasePsychicArea(int charID, int localAreaKey) {
        var outPacket = new OutPacket(RELEASE_PSYCHIC_AREA);

        outPacket.encodeInt(charID);
        outPacket.encodeInt(localAreaKey);

        return outPacket;
    }

    public static OutPacket createPsychicLock(boolean approved, PsychicLock pl) {
        var outPacket = new OutPacket(CREATE_PSYCHIC_LOCK);

        outPacket.encodeByte(approved);
        if (approved) {
            pl.encode(outPacket);
        }

        return outPacket;
    }

    public static OutPacket releasePsychicLock(int id) {
        var outPacket = new OutPacket(RELEASE_PSYCHIC_LOCK);

        outPacket.encodeInt(id);

        return outPacket;
    }

    public static OutPacket releasePsychicLockMob(List<Integer> ids) {
        var outPacket = new OutPacket(RELEASE_PSYCHIC_LOCK_MOB);

        for (int i : ids) {
            outPacket.encodeByte(1);
            outPacket.encodeInt(i);
        }
        outPacket.encodeByte(0);

        return outPacket;
    }

    public static OutPacket characterInfo(Char chr) {
        var outPacket = new OutPacket(CHARACTER_INFO);

        CharacterStat cs = chr.getAvatarData().getCharacterStat();
        outPacket.encodeInt(chr.getId());

        outPacket.encodeByte(false); // Star Planet -> 1 1 4

        outPacket.encodeByte(chr.getStat(Stat.level));
        outPacket.encodeShort(chr.getJob());
        outPacket.encodeShort(chr.getStat(Stat.subJob));
        outPacket.encodeByte(cs.getPvpGrade());
        outPacket.encodeInt(cs.getPop()); //Fame
        MarriageRecord marriage = chr.getMarriageRecord();
        outPacket.encodeByte(marriage != null);
        if (marriage != null) {
            marriage.encode(outPacket);
        }

        List<Short> makingSkills = new ArrayList<>();
        for (short i = 9200; i <= 9204; i++) {
            if (chr.getMakingSkillLevel(i * 10000) > 0) {
                makingSkills.add(i);
            }
        }
        outPacket.encodeByte(makingSkills.size());
        makingSkills.forEach(outPacket::encodeShort);

        outPacket.encodeString(chr.getGuild() == null ? "-" : chr.getGuild().getName());
        outPacket.encodeString(chr.getGuild() == null || chr.getGuild().getAlliance() == null ? "-" :
                chr.getGuild().getAlliance().getName());
        outPacket.encodeByte(-1); // Forced pet IDx
        outPacket.encodeByte(0); // User state (?)

        outPacket.encodeString("");
        outPacket.encodeByte(0);
        outPacket.encodeByte(0);
        outPacket.encodeByte(0);
        outPacket.encodeByte(0);
        outPacket.encodeByte(0);

        outPacket.encodeByte(!chr.getPets().isEmpty()); // pet activated
        chr.getPets().forEach(pet -> {
            PetItem pi = pet.getItem();
            outPacket.encodeByte(1);
            outPacket.encodeInt(pet.getIdx());
            outPacket.encodeInt(pi.getItemId());
            outPacket.encodeString(pet.getName());
            outPacket.encodeByte(pi.getLevel());
            outPacket.encodeShort(pi.getTameness());
            outPacket.encodeByte(pi.getRepleteness());
            outPacket.encodeShort(pi.getPetSkill());
            outPacket.encodeInt(0); // equip 1
            outPacket.encodeInt(0); // equip 2
        });
        outPacket.encodeByte(0); // CUIUserInfo::SetPetInfo end

        outPacket.encodeByte(0); // old Wish list
        // MedalAchievementInfo::Decode
        Equip medal = (Equip) chr.getEquippedItemByBodyPart(BodyPart.Medal);
        outPacket.encodeInt(medal == null ? 0 : medal.getItemId());
        outPacket.encodeShort(0); // medal size
        // for each medal, encode int (itemID) and complete time (FT)
        // End MedalAchievementInfo::Decode
        chr.encodeDamageSkins(outPacket);
        outPacket.encodeByte(cs.getNonCombatStatDayLimit().getCharisma());
        outPacket.encodeByte(cs.getNonCombatStatDayLimit().getInsight());
        outPacket.encodeByte(cs.getNonCombatStatDayLimit().getWill());
        outPacket.encodeByte(cs.getNonCombatStatDayLimit().getCraft());
        outPacket.encodeByte(cs.getNonCombatStatDayLimit().getSense());
        outPacket.encodeByte(cs.getNonCombatStatDayLimit().getCharm());
        outPacket.encodeInt(chr.getAccId());
        // FarmUserInfo::Decode
        outPacket.encodeString("Best farm eu");
        outPacket.encodeInt(13); // nFarmPoint
        outPacket.encodeInt(13); // nFarmLevel
        outPacket.encodeInt(13); // nFarmExp
        outPacket.encodeInt(13); // nFarmPoint
        outPacket.encodeInt(13); // nFarmCash
        outPacket.encodeByte(13); // nFarmGender
        outPacket.encodeInt(13); // nFarmTheme
        outPacket.encodeInt(13); // nFarmSlotExtend
        outPacket.encodeInt(13); // nFarmLockerSlotCount
        // End FarmUserInfo::Decode
        outPacket.encodeInt(0);
        outPacket.encodeInt(0);
        //Chairs
        outPacket.encodeInt(0); //chair amount(size)
        outPacket.encodeInt(0);
        outPacket.encodeInt(30);
        outPacket.encodeInt(0);


        return outPacket;
    }

    public static OutPacket showItemUpgradeEffect(int charID, boolean success, boolean enchantDlg, int uItemID, int eItemID, boolean boom) {
        var outPacket = new OutPacket(SHOW_ITEM_UPGRADE_EFFECT);

        outPacket.encodeInt(charID);

        outPacket.encodeByte(boom ? 2 : success ? 1 : 0);
        outPacket.encodeByte(enchantDlg);
        outPacket.encodeInt(uItemID);
        outPacket.encodeInt(eItemID);

        outPacket.encodeInt(0);
        outPacket.encodeByte(0);
        outPacket.encodeByte(0);

        return outPacket;
    }

    public static OutPacket showItemReleaseEffect(int charID, short pos, boolean bonus) {
        var outPacket = new OutPacket(SHOW_ITEM_RELEASE_EFFECT);

        outPacket.encodeInt(charID);

        outPacket.encodeShort(pos);
        outPacket.encodeByte(bonus);

        return outPacket;
    }

    public static OutPacket hyperUpgradeDisplay(Equip equip, boolean downgradeable, long meso, long beforeMVP, int successChance,
                                                int destroyChance, boolean chanceTime) {
        var outPacket = new OutPacket(EQUIPMENT_ENCHANT);

        outPacket.encodeByte(EquipmentEnchantType.HyperUpgradeDisplay.getVal());
        outPacket.encodeByte(downgradeable);
        outPacket.encodeLong(meso);
        outPacket.encodeLong(beforeMVP);
        outPacket.encodeInt(successChance);
        outPacket.encodeInt(destroyChance);
        outPacket.encodeByte(chanceTime);
        TreeMap<EnchantStat, Integer> vals = equip.getHyperUpgradeStats();
        int mask = 0;
        for (EnchantStat es : vals.keySet()) {
            mask |= es.getVal();
        }
        outPacket.encodeInt(mask);
        vals.forEach((es, val) -> outPacket.encodeInt(val));

        return outPacket;
    }

    public static OutPacket miniGameDisplay(EquipmentEnchantType eeType) {
        var outPacket = new OutPacket(EQUIPMENT_ENCHANT);

        outPacket.encodeByte(eeType.getVal());
        outPacket.encodeByte(0);
        outPacket.encodeInt(2000); // TODO nSeed

        return outPacket;
    }

    public static OutPacket showUpgradeResult(Equip oldEquip, Equip equip, boolean succeed, boolean boom, boolean canDegrade) {
        var outPacket = new OutPacket(EQUIPMENT_ENCHANT);

        outPacket.encodeByte(EquipmentEnchantType.ShowHyperUpgradeResult.getVal());
        outPacket.encodeInt(boom ? 2 : succeed ? 1 : canDegrade ? 0 : 3);
        outPacket.encodeByte(0);
        oldEquip.encode(outPacket);
        equip.encode(outPacket);

        return outPacket;
    }

    public static OutPacket showUnknownEnchantFailResult(byte msg) {
        var outPacket = new OutPacket(EQUIPMENT_ENCHANT);

        outPacket.encodeByte(EquipmentEnchantType.ShowUnknownFailResult.getVal());
        outPacket.encodeByte(msg);

        return outPacket;
    }

    public static OutPacket scrollUpgradeDisplay(boolean feverTime, List<ScrollUpgradeInfo> infos) {
        var outPacket = new OutPacket(EQUIPMENT_ENCHANT);

        outPacket.encodeByte(EquipmentEnchantType.ScrollUpgradeDisplay.getVal());
        outPacket.encodeByte(feverTime);

        outPacket.encodeByte(infos.size());
        infos.forEach(outPacket::encode);

        return outPacket;
    }

    public static OutPacket showScrollUpgradeResult(boolean feverAfter, int result, String desc, Equip prevEquip,
                                                    Equip newEquip) {
        var outPacket = new OutPacket(EQUIPMENT_ENCHANT);

        outPacket.encodeByte(EquipmentEnchantType.ShowScrollUpgradeResult.getVal());

        outPacket.encodeByte(feverAfter);
        outPacket.encodeInt(result);
        outPacket.encodeString(desc);
        outPacket.encode(prevEquip);
        outPacket.encode(newEquip);

        return outPacket;
    }

    public static OutPacket showTranmissionResult(Equip fromEq, Equip toEq) {
        var outPacket = new OutPacket(EQUIPMENT_ENCHANT);

        outPacket.encodeByte(EquipmentEnchantType.ShowTransmissionResult.getVal());
        fromEq.encode(outPacket);
        toEq.encode(outPacket);

        return outPacket;
    }

    public static OutPacket redCubeResult(int charID, boolean upgrade, int cubeID, int ePos, Equip equip) {
        var outPacket = new OutPacket(RED_CUBE_RESULT);

        outPacket.encodeInt(charID);

        outPacket.encodeByte(upgrade);
        outPacket.encodeInt(cubeID);
        outPacket.encodeInt(ePos);
        equip.encode(outPacket);

        return outPacket;
    }

    public static OutPacket inGameCubeResult(int charID, boolean upgrade, int cubeID, int ePos, Equip equip) {
        var outPacket = new OutPacket(IN_GAME_CUBE_RESULT);

        outPacket.encodeInt(charID);
        outPacket.encodeByte(upgrade);
        outPacket.encodeInt(cubeID);
        outPacket.encodeInt(ePos);
        equip.encode(outPacket);

        return outPacket;
    }

    public static OutPacket sitResult(int chrId, int fieldSeatId) {
        var outPacket = new OutPacket(SIT_RESULT);

        outPacket.encodeInt(chrId);
        if (fieldSeatId == -1) {
            outPacket.encodeByte(0);
        } else {
            outPacket.encodeByte(1);
            outPacket.encodeShort(fieldSeatId);
        }

        return outPacket;
    }


    public static OutPacket questClear(int qrKey) {
        var outPacket = new OutPacket(QUEST_CLEAR);

        outPacket.encodeInt(qrKey);

        return outPacket;
    }

    public static OutPacket setQuestTime(List<Triple<Integer, FileTime, FileTime>> questTimes) {
        var outPacket = new OutPacket(SET_QUEST_TIME);

        outPacket.encodeByte(questTimes.size());
        for (Triple<Integer, FileTime, FileTime> times : questTimes) {
            outPacket.encodeInt(times.getLeft());
            outPacket.encodeFT(times.getMiddle());
            outPacket.encodeFT(times.getRight());
        }

        return outPacket;
    }

    public static OutPacket addWreckage(Char chr, Mob mob, int skillID, int debrisCount) {
        var outPacket = new OutPacket(ADD_WRECKAGE);

        outPacket.encodeInt(chr.getId());  //v2
        outPacket.encodePositionInt(mob.getPosition());
        outPacket.encodeInt(chr.getFieldID());  //v4
        outPacket.encodeInt(1);  //evanWreckage.nIDx
        outPacket.encodeInt(skillID);  //nSkillID
        outPacket.encodeInt(1);  //nType

        outPacket.encodeInt(debrisCount);  //Number on Skill Icon, # of Wreckages on map

        return outPacket;
    }

    public static OutPacket delWreckage(Char chr) {
        var outPacket = new OutPacket(DEL_WRECKAGE);

        outPacket.encodeInt(chr.getId()); //Char ID
        outPacket.encodeInt(1); //Count
        outPacket.encodeByte(true); //Unk Boolean

        outPacket.encodeInt(1); //Unk


        return outPacket;
    }

    public static OutPacket enterFieldFoxMan(Char chr) {
        var outPacket = new OutPacket(FOX_MAN_ENTER_FIELD);

        Position position = chr.getPosition();

        outPacket.encodeInt(chr.getId());
        outPacket.encodeShort(0);   // 1 = Haku Old Form,  0 = Haku New Form
        outPacket.encodePosition(position);
        outPacket.encodeByte(4); //MoveAction
        outPacket.encodeShort(0); //Foothold
        outPacket.encodeInt(0); //nUpgrade
        outPacket.encodeInt(0); //FanID Equipped by Haku

        return outPacket;
    }

    public static OutPacket whisper(String sourceName, byte channelIdx, boolean gm, String msg, boolean notFound) {
        var outPacket = new OutPacket(WHISPER);

        if (notFound) {
            outPacket.encodeByte(9);
            outPacket.encodeString(sourceName);
            outPacket.encodeByte(4);
            outPacket.encodeInt(channelIdx);
        } else {
            outPacket.encodeByte(18);
            outPacket.encodeString(sourceName);
            outPacket.encodeByte(channelIdx);
            outPacket.encodeByte(gm);
            outPacket.encodeString(msg);
        }

        return outPacket;
    }

    public static OutPacket teleport(Position position, Char chr) {
        var outPacket = new OutPacket(TELEPORT);
        outPacket.encodeByte(false);
        outPacket.encodeByte(6);

        outPacket.encodeInt(chr.getId());
        outPacket.encodePosition(position);

        return outPacket;
    }

    public static OutPacket fieldEffect(FieldEffect fieldEffect) {
        var outPacket = new OutPacket(FIELD_EFFECT);

        fieldEffect.encode(outPacket);

        return outPacket;
    }

    public static OutPacket removeBlowWeather() {
        return blowWeather(0, null);
    }

    public static OutPacket blowWeather(int itemID, String message) {
        var outPacket = new OutPacket(BLOW_WEATHER);

        outPacket.encodeByte(0);
        outPacket.encodeInt(itemID);
        if (itemID == 5320000) {
            outPacket.encodeInt(0);
        }
        if (itemID > 0) {
            outPacket.encodeString(message);
            outPacket.encodeByte(0);// boolean if true send PackedCharacterLook
        }
        return outPacket;
    }

    public static OutPacket trunkDlg(TrunkDlg trunkDlg) {
        var outPacket = new OutPacket(TRUNK_DLG);

        outPacket.encodeByte(trunkDlg.getType().getVal());
        trunkDlg.encode(outPacket);

        return outPacket;
    }

    public static OutPacket openUI(UIType uiType) {
        return openUI(uiType.getVal());
    }

    public static OutPacket openUI(int uiID) {
        var outPacket = new OutPacket(OPEN_UI);
        outPacket.encodeInt(uiID);
        return outPacket;
    }

    public static OutPacket closeUI(UIType uiType) {
        return closeUI(uiType.getVal());
    }

    public static OutPacket closeUI(int uiID) {
        var outPacket = new OutPacket(CLOSE_UI);
        outPacket.encodeInt(uiID);
        return outPacket;
    }

    public static OutPacket socketCreateResult(boolean success) {
        var outPacket = new OutPacket(SOCKET_CREATE_RESULT);

        outPacket.encodeByte(success ? 2 : 3);

        return outPacket;
    }

    public static OutPacket changeMobZone(int mobID, int dataType) {
        var outPacket = new OutPacket(CHANGE_MOB_ZONE);

        outPacket.encodeInt(mobID);
        outPacket.encodeInt(dataType);

        return outPacket;
    }

    public static OutPacket createObtacle(ObtacleAtomCreateType oact, ObtacleInRowInfo oiri, ObtacleRadianInfo ori,
                                          Set<ObtacleAtomInfo> atomInfos) {
        var outPacket = new OutPacket(CREATE_OBTACLE);

        outPacket.encodeInt(0); // ? gets used in 1 function, which forwards it to another, which does nothing with it
        outPacket.encodeInt(atomInfos.size());
        outPacket.encodeByte(oact.getVal());
        if (oact == ObtacleAtomCreateType.IN_ROW) {
            oiri.encode(outPacket);
        } else if (oact == ObtacleAtomCreateType.RADIAL) {
            ori.encode(outPacket);
        }
        for (ObtacleAtomInfo atomInfo : atomInfos) {
            outPacket.encodeByte(true); // false -> no encode
            atomInfo.encode(outPacket);
            if (oact == ObtacleAtomCreateType.DIAGONAL) {
                atomInfo.getObtacleDiagonalInfo().encode(outPacket);
            }
        }

        return outPacket;
    }

    public static OutPacket runeStoneAppear(RuneStone runeStone) { //Spawn in RuneStone
        var outPacket = new OutPacket(RUNE_STONE_APPEAR);

        outPacket.encodeInt(0); // object id ??
        outPacket.encodeInt(runeStone.getRuneType().getVal()); // Rune Type
        outPacket.encodePositionInt(runeStone.getPosition()); // Position
        outPacket.encodeByte(runeStone.isFlip()); // flip

        return outPacket;
    }

    public static OutPacket completeRune(Char chr) { //RuneStone Disappears
        var outPacket = new OutPacket(COMPLETE_RUNE);

        outPacket.encodeInt(0);
        outPacket.encodeInt(chr.getId());

        return outPacket;
    }

    public static OutPacket runeStoneUseAck(int type) {
        var outPacket = new OutPacket(RUNE_STONE_USE_ACK);

        outPacket.encodeInt(type);
        switch (type) {
            case 2:// Rune Delay time
                outPacket.encodeInt(0);
                break;
            case 4://That rune is to strong for you to handle
                break;
            case 5://Shows arrows
                break;
        }

        return outPacket;
    }

    public static OutPacket runeStoneDisappear() { //RuneStone is Used
        var outPacket = new OutPacket(RUNE_STONE_DISAPPEAR);

        outPacket.encodeInt(0); // Has to be 0
        outPacket.encodeInt(0); // Doesn't matter what number this is

        return outPacket;
    }

    public static OutPacket runeActSuccess(RuneType runeType, int time) {
        var outPacket = new OutPacket(RUNE_ACT_SUCCESS);

        outPacket.encodeInt(runeType.getVal());
        outPacket.encodeInt(time);

        return outPacket;
    }

    public static OutPacket runeStoneSkillAck(RuneType runeType) {
        var outPacket = new OutPacket(RUNE_STONE_SKILL_ACK);

        outPacket.encodeInt(runeType.getVal());

        return outPacket;
    }

    public static OutPacket runeStoneClearAndAllRegister() {
        var outPacket = new OutPacket(RUNE_STONE_CLEAR_AND_ALL_REGISTER);
        int count = 0;
        outPacket.encodeInt(count); // count
        // not sure, but whatever
        IntStream.range(0, count).map(i -> 0).forEach(outPacket::encodeInt);

        return outPacket;
    }

    /**
     * Creates a Clock on a Field.
     *
     * @param clockPacket the clock to display
     * @return packet for the client
     */
    public static OutPacket clock(ClockPacket clockPacket) {
        var outPacket = new OutPacket(CLOCK);

        clockPacket.encode(outPacket);

        return outPacket;
    }

    /**
     * Creates a packet for changing the elite state of a field.
     *
     * @param eliteState             The new elite state
     * @param notShowPopup           whether or not the popup should show up (warning message for boss spawn, countdown for bonus)
     * @param bgm                    The new bgm if the state is ELITE_BOSS
     * @param propSpecialEliteEffect special elite effect
     * @param backUOL                back uol
     * @return packet for the client
     */
    public static OutPacket eliteState(EliteState eliteState, boolean notShowPopup, String bgm, String propSpecialEliteEffect,
                                       String backUOL) {

        var outPacket = new OutPacket(ELITE_STATE);

        outPacket.encodeInt(eliteState.getVal()); // elite state
        outPacket.encodeInt(notShowPopup ? 1 : 0); // ?
        if (eliteState == EliteState.EliteBoss) {
            outPacket.encodeString(bgm); // bgm
        } else {
            outPacket.encodeString(propSpecialEliteEffect);
            outPacket.encodeString(backUOL);
        }

        return outPacket;
    }

    public static OutPacket setQuickMoveInfo(List<QuickMoveInfo> quickMoveInfos) {
        var outPacket = new OutPacket(SET_QUICK_MOVE_INFO);

        outPacket.encodeByte(quickMoveInfos.size());
        quickMoveInfos.forEach(qmi -> qmi.encode(outPacket));

        return outPacket;
    }

    public static OutPacket groupMessage(GroupMessageType gmt, String from, String msg) {

        var outPacket = new OutPacket(GROUP_MESSAGE);

        outPacket.encodeByte(gmt.ordinal());
        outPacket.encodeString(from);
        outPacket.encodeString(msg);

        return outPacket;
    }

    public static OutPacket openGateCreated(OpenGate openGate) {
        var outPacket = new OutPacket(OPEN_GATE_CREATED);

        outPacket.encodeByte(1); // Animation
        outPacket.encodeInt(openGate.getChr().getId()); // Character Id
        outPacket.encodePosition(openGate.getPosition()); // Position
        outPacket.encodeByte(openGate.getGateId()); // Gate Id
        outPacket.encodeInt(openGate.getParty() != null ? openGate.getParty().getId() : 0); // Party Id

        return outPacket;
    }

    public static OutPacket openGateRemoved(OpenGate openGate) {
        var outPacket = new OutPacket(OPEN_GATE_REMOVED);

        outPacket.encodeByte(1); // Animation
        outPacket.encodeInt(openGate.getChr().getId()); // Character Id
        outPacket.encodeByte(openGate.getGateId()); // Gate Id

        return outPacket;
    }

    public static OutPacket createMirrorImage(Position position, int alpha, int red, int green, int blue, boolean left) {
        var outPacket = new OutPacket(CREATE_MIRROR_IMAGE);

        outPacket.encodePositionInt(position);
        outPacket.encodeInt(alpha); // nAlpha   out of 1,000 (?)
        outPacket.encodeInt(red); // R  out of 100,000 (?)

        outPacket.encodeInt(green); // G  out of 100,000 (?)
        outPacket.encodeInt(blue); // B  out of 100,000 (?)
        outPacket.encodeInt(left ? 1 : 0); // bLeft

        return outPacket;
    }

    public static OutPacket townPortalCreated(TownPortal townPortal, boolean noAnimation) {
        var outPacket = new OutPacket(TOWN_PORTAL_CREATED);

        outPacket.encodeByte(noAnimation); // No Animation  (false = Animation : true = No Animation)
        outPacket.encodeInt(townPortal.getChr().getId());
        outPacket.encodePosition(townPortal.getFieldPosition()); // as this doesn't need to be initialised yet.
        outPacket.encodePosition(townPortal.getFieldPosition()); //

        return outPacket;
    }

    public static OutPacket townPortalRemoved(TownPortal townPortal, boolean animation) {
        var outPacket = new OutPacket(TOWN_PORTAL_REMOVED);

        outPacket.encodeByte(animation); // Animation
        outPacket.encodeInt(townPortal.getChr().getId());

        return outPacket;
    }

    public static OutPacket setOneTimeAction(int charID, int action, int duration) {
        var outPacket = new OutPacket(SET_ONE_TIME_ACTION);

        outPacket.encodeInt(charID);
        outPacket.encodeInt(action);
        outPacket.encodeInt(duration);

        return outPacket;
    }

    public static OutPacket makingSkillResult(int charID, int recipeCode, MakingSkillResult result, MakingSkillRecipe.TargetElem target, int incSkillProficiency) {
        var outPacket = new OutPacket(MAKING_SKILL_RESULT);

        outPacket.encodeInt(charID);

        outPacket.encodeInt(recipeCode);
        outPacket.encodeInt(result.getVal());
        if (result == MakingSkillResult.SUCESS_SOSO || result == MakingSkillResult.SUCESS_GOOD || result == MakingSkillResult.SUCESS_COOL) {
            outPacket.encodeInt(target.getItemID());
            outPacket.encodeInt(target.getCount());
        }
        outPacket.encodeInt(incSkillProficiency);

        return outPacket;
    }

    public static OutPacket makingSkillResult(int charID, int recipeCode, int result, int createdItemID, int itemCount, int incSkillProficiency) {
        var outPacket = new OutPacket(MAKING_SKILL_RESULT);

        outPacket.encodeInt(charID);

        outPacket.encodeInt(recipeCode);
        outPacket.encodeInt(result);
        if (result == 25 || result == 26 || result == 27) {
            outPacket.encodeInt(createdItemID);
            outPacket.encodeInt(itemCount);
        }
        outPacket.encodeInt(incSkillProficiency);

        return outPacket;
    }

    public static OutPacket registerExtraSkill(Char chr, int mainSkillId, Set<Integer> extraSkillIds) {
        return registerExtraSkill(chr.getPosition(), mainSkillId, extraSkillIds, chr.isLeft());
    }

    public static OutPacket registerExtraSkill(Position position, int mainSkilId, Set<Integer> extraSkillIds, boolean isLeft) {
        var outPacket = new OutPacket(REGISTER_EXTRA_SKILL);

        outPacket.encodePositionInt(position);
        outPacket.encodeShort(isLeft ? -1 : 1);
        outPacket.encodeInt(mainSkilId);
        outPacket.encodeShort(extraSkillIds.size());
        extraSkillIds.forEach(outPacket::encodeInt);

        return outPacket;
    }

    public static OutPacket playSound(String dir) {
        var outPacket = new OutPacket(PLAY_SOUND);

        outPacket.encodeString(dir);

        return outPacket;
    }

    public static OutPacket giveSpecialSkillBar(int skillID) {
        var outPacket = new OutPacket(GIVE_SPECIAL_SKILL_BAR);

        if (skillID == 0) {
            outPacket.encodeInt(0);
        } else {
            // Unknown Packet Structure..
            // This is entirely from a sniff.
            // Can't find this packet anywhere in KMST idb nor v206 idb
            outPacket.encodeInt(13); // unknown, from sniff
            outPacket.encodeInt(13); // unknown, from sniff
            outPacket.encodeByte(1); // unknown, from sniff
            outPacket.encodeInt(0); // unknown, from sniff

            outPacket.encodeInt(skillID);
            outPacket.encodeInt(1); // slv (?)

            outPacket.encodeArr(new byte[22]); // unknown, from sniff
        }

        return outPacket;
    }

    public static OutPacket golluxOpenPortal(Char chr, String action, int show) {
        var outPacket = new OutPacket(GOLLUX_PORTAL_OPEN);

        outPacket.encodeString(action);
        outPacket.encodeInt(show);

        return outPacket;
    }

    public static OutPacket golluxUpdateMiniMap(Char chr) {
        var outPacket = new OutPacket(GOLLUX_MINIMAP);

        Map<String, Object> golluxMaps = chr.getOrCreateFieldByCurrentInstanceType(BossConstants.GOLLUX_FIRST_MAP).getProperties();
        outPacket.encodeInt(golluxMaps.size());
        golluxMaps.forEach((key, value) -> {
            outPacket.encodeString(key);
            outPacket.encodeString(String.valueOf(value));
        })
        ;
        return outPacket;
    }

    public static OutPacket footholdAppear(String footholdName, boolean show, Position pos) {
        var outPacket = new OutPacket(FOOT_HOLD_APPEAR);

        int loopSize = 1;

        outPacket.encodeInt(loopSize);
        IntStream.range(0, loopSize).forEach(i -> {
            outPacket.encodeString(footholdName);
            outPacket.encodeByte(0);
            outPacket.encodeInt(show ? 1 : 0);
            outPacket.encodePositionInt(pos);
        });

        return outPacket;
    }

    public static OutPacket createFallingCatcher(String name, int index, int count, List<Position> positions) {
        var outPacket = new OutPacket(CREATE_FALLING_CATCHER);

        outPacket.encodeString(name);
        outPacket.encodeInt(index);

        outPacket.encodeInt(count);

        IntStream.range(0, count).mapToObj(positions::get).forEach(outPacket::encodePositionInt);

        return outPacket;
    }

    public static OutPacket createFallingCatcherGollux(int mobId, Position position) {
        ArrayList<Position> pos = new ArrayList<Position>();
        pos.add(position);
        switch (mobId) {
            case 9390610:
                return createFallingCatcher("palmAttackGiantBossL", 50, 1, pos);
            case 9390611:
                return createFallingCatcher("palmAttackGiantBossR", 50, 1, pos);
            default:
                Random ran = new Random();
                int x = ran.nextInt(3) + 1;
                return createFallingCatcher("DropStoneGiantBoss" + String.valueOf(x), 25, 1, pos);
        }

    }

    public static OutPacket setObjectState(String name, int mode) {
        var outPacket = new OutPacket(SET_OBJECT_STATE);

        outPacket.encodeString(name);
        outPacket.encodeInt(mode);

        return outPacket;
    }
}
