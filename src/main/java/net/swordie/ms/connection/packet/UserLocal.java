package net.swordie.ms.connection.packet;

import net.swordie.ms.client.character.Char;
import net.swordie.ms.client.character.damage.DamageSkinType;
import net.swordie.ms.client.character.skills.LarknessManager;
import net.swordie.ms.client.character.skills.PsychicArea;
import net.swordie.ms.client.character.skills.PsychicLockBall;
import net.swordie.ms.client.character.skills.Skill;
import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.enums.*;
import net.swordie.ms.handlers.PsychicLock;
import net.swordie.ms.handlers.header.OutHeader;
import static net.swordie.ms.handlers.header.OutHeader.*;
import net.swordie.ms.life.Familiar;
import net.swordie.ms.life.mob.Mob;
import net.swordie.ms.life.movement.MovementInfo;
import net.swordie.ms.life.pet.Pet;
import net.swordie.ms.util.Position;
import net.swordie.ms.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created on 1/2/2018.
 */
public class UserLocal {
    public static OutPacket noticeMsg(String msg, boolean autoSeparated) {
        var outPacket = new OutPacket(NOTICE_MSG);

        outPacket.encodeString(msg);
        outPacket.encodeByte(autoSeparated);

        return outPacket;
    }

    public static OutPacket chatMsg(ChatType colour, String msg) {
        var outPacket = new OutPacket(CHAT_MSG);

        outPacket.encodeShort(colour.getVal());
        outPacket.encodeString(msg);

        return outPacket;
    }

    public static OutPacket videoByScript(String videoPath, boolean isMuted){
        var outPacket = new OutPacket(VIDEO_BY_SCRIPT);

        outPacket.encodeString(videoPath);
        outPacket.encodeByte(isMuted);

        return outPacket;
    }

    public static OutPacket jaguarActive(boolean active) {
        var outPacket = new OutPacket(JAGUAR_ACTIVE);

        outPacket.encodeByte(active);

        return outPacket;
    }

    public static OutPacket jaguarSkill(int skillID) {
        var outPacket = new OutPacket(JAGUAR_SKILL);

        outPacket.encodeInt(skillID);

        return outPacket;
    }

    public static OutPacket addPopupSay(int npcID, int duration, String message, String effect) {
        var outPacket = new OutPacket(ADD_POPUP_SAY);

        outPacket.encodeInt(npcID);
        outPacket.encodeInt(duration);
        outPacket.encodeString(message);
        outPacket.encodeString(effect);
        return outPacket;
    }

    public static OutPacket incLarknessReponse(LarknessManager larknessManager) {
        var outPacket = new OutPacket(INC_LARKNESS_RESPONSE);

        larknessManager.encode(outPacket);

        return outPacket;
    }

    public static OutPacket royalGuardAttack(boolean attack) {
        var outPacket = new OutPacket(ROYAL_GUARD_ATTACK);

        outPacket.encodeByte(attack);

        return outPacket;
    }

    public static OutPacket rwMultiChargeCancelRequest(byte unk, int skillID) {
        var outPacket = new OutPacket(SKILL_USE_RESULT);

        outPacket.encodeByte(unk);
        outPacket.encodeInt(skillID);

        return outPacket;
    }

    public static OutPacket setOffStateForOffSkill(int skillID) {
        var outPacket = new OutPacket(SET_OFF_STATE_FOR_OFF_SKILL);

        outPacket.encodeInt(skillID);

        return outPacket;
    }

    public static OutPacket resetStateForOffSkill() {
        var outPacket = new OutPacket(RESET_STATE_FOR_OFF_SKILL);

        outPacket.encodeInt(0);

        return outPacket;
    }

    public static OutPacket modHayatoCombo(int amount) {
        var outPacket = new OutPacket(MOD_HAYATO_COMBO);

        outPacket.encodeInt(amount);

        return outPacket;
    }

    public static OutPacket incJudgementStack(byte amount) {
        var outPacket = new OutPacket(INC_JUDGEMENT_STACK_RESPONSE);

        outPacket.encodeByte(amount);

        return outPacket;
    }

    public static OutPacket changeStealMemoryResult(byte type, int stealManagerJobID, int position, int skillid, int stealSkillLv, int stealSkillMaxLv) {
        var outPacket = new OutPacket(CHANGE_STEAL_MEMORY_RESULT);
        StealMemoryType smType = StealMemoryType.getByVal(type);

        outPacket.encodeByte(1); //Set Excl Request
        outPacket.encodeByte(smType.getVal());    //Type

        switch (smType) {
            case STEAL_SKILL:
                outPacket.encodeInt(stealManagerJobID); //jobId  1~5 | 1 = 1stJob , 2 = 2ndJob ... ..
                outPacket.encodeInt(position); //impecMemSkillID // nPOS  0,1,2,3
                outPacket.encodeInt(skillid); //skill
                outPacket.encodeInt(stealSkillLv);   //StealSkill Lv
                outPacket.encodeInt(stealSkillMaxLv);   //StealSkill Max Lv
                break;
            case NO_TARGETS:
            case FAILED_UNK_REASON:
                break;
            case REMOVE_STEAL_MEMORY:
                outPacket.encodeInt(stealManagerJobID);
                outPacket.encodeInt(position);
                outPacket.encodeByte(0);
                break;
            case REMOVE_MEMORY_ALL_SLOT:
                outPacket.encodeInt(skillid);
                break;
            case REMOVE_ALL_MEMORY:
                break;
        }

        return outPacket;
    }

    public static OutPacket resultSetStealSkill(boolean set, int impecMemSkilLID, int skillId) {
        var outPacket = new OutPacket(RESULT_SET_STEAL_SKILL);

        outPacket.encodeByte(1); //Set Excl Request
        outPacket.encodeByte(set); //bSet
        outPacket.encodeInt(impecMemSkilLID); //impecMemSkilLID
        if(set) {
            outPacket.encodeInt(skillId); //skill Id
        }
        return outPacket;
    }

    public static OutPacket resultStealSkillList(Set<Skill> targetSkillsList, int phantomStealResult, int targetChrId, int targetJobId) {
        var outPacket = new OutPacket(RESULT_STEAL_SKILL_LIST);
        outPacket.encodeByte(0); //Set Excl Request
        outPacket.encodeInt(targetChrId);
        outPacket.encodeInt(phantomStealResult); //   Gets a check  if == 4,   else:   nPhantomStealWrongResult
        if(phantomStealResult == 4) {
            outPacket.encodeInt(targetJobId);
            outPacket.encodeInt(targetSkillsList.size());

            for (Skill skills : targetSkillsList) {
                // if v9 (index??) > 0
                outPacket.encodeInt(skills.getSkillId());
            }
        }

        return outPacket;
    }

    public static OutPacket setFuncKeyByScript(boolean add, int action, int key) {
        var outPacket = new OutPacket(FUNCKEY_SET_BY_SCRIPT);
        outPacket.encodeByte(add);
        outPacket.encodeInt(action);
        outPacket.encodeInt(key);

        return outPacket;
    }

    public static OutPacket damageSkinSaveResult(DamageSkinType req, DamageSkinType res, Char chr) {
        var outPacket = new OutPacket(DAMAGE_SKIN_SAVE_RESULT);

        outPacket.encodeByte(req.getVal());
        if (req.getVal() <= 2) {
            outPacket.encodeByte(res.getVal());
            if (res == DamageSkinType.Res_Success) {
                chr.encodeDamageSkins(outPacket);
            }
        } else if (req == DamageSkinType.Req_SendInfo) {
            chr.encodeDamageSkins(outPacket);
        }
        return outPacket;
    }

    public static OutPacket explosionAttack(int skillID, Position position, int mobID, int count) {
        var outPacket = new OutPacket(EXPLOSION_ATTACK);

        outPacket.encodeInt(skillID);
        outPacket.encodePositionInt(position);
        outPacket.encodeInt(mobID);
        outPacket.encodeInt(count);

        return outPacket;
    }

    public static OutPacket userRandAreaAttackRequest(Mob mob, int skillID) {
        var outPacket = new OutPacket(USER_RAND_AREA_ATTACK_REQUEST);

        outPacket.encodeInt(skillID);
        outPacket.encodeInt(1); //# of mobs to attack

        outPacket.encodePositionInt(mob.getPosition());
        outPacket.encodeInt(mob.getObjectId());

        return outPacket;
    }

    public static OutPacket petActivateChange(Pet pet, boolean active, byte removedReason) {
        var outPacket = new OutPacket(PET_ACTIVATED);

        outPacket.encodeInt(pet.getOwnerID());
        outPacket.encodeInt(pet.getIdx());
        outPacket.encodeByte(active);
        if(active) {
            outPacket.encodeByte(true); // init
            pet.encode(outPacket);
        } else {
            outPacket.encodeByte(removedReason);
        }

        return outPacket;
    }

    public static OutPacket comboCounter(byte type, int comboCount, int mobID) {
        var outPacket = new OutPacket(MESSAGE);
        outPacket.encodeByte(MessageType.STYLISH_KILL_MESSAGE.getVal());
        StylishKillType smType = StylishKillType.getByVal(type);

        outPacket.encodeByte(type); //1 for Combo   |  0 for MultiKill

        switch (smType) {
            case COMBO: //Combo Kill Message
                outPacket.encodeInt(comboCount); //count
                outPacket.encodeInt(mobID); //mobID
                break;

            case MULTI_KILL: //MultiKill Pop-up
                outPacket.encodeLong(comboCount); //nBonus
                outPacket.encodeInt(mobID); //count
                break;
        }

        return outPacket;
    }

    public static OutPacket collectionRecordMessage(int collectionIndex, String value) {
        var outPacket = new OutPacket(MESSAGE);

        outPacket.encodeByte(MessageType.COLLECTION_RECORD_MESSAGE.getVal());
        outPacket.encodeInt(collectionIndex);
        outPacket.encodeString(value);

        return outPacket;
    }

    public static OutPacket setDead(boolean tremble) {
        var outPacket = new OutPacket(SET_DEAD);

        outPacket.encodeByte(tremble);

        return outPacket;
    }

    public static OutPacket openUIOnDead(boolean onDeadRevive, boolean onDeadProtectForBuff, boolean onDeadProtectExpMaplePoint,
                                         boolean onDeadProtectBuffMaplePoint, boolean anniversary, int reviveType, int protectType) {
        var outPacket = new OutPacket(OPEN_UI_DEAD);

        int reviveMask = 0;
        if (onDeadRevive) {
            reviveMask |= 0x1;
        }
        if (onDeadProtectForBuff) {
            reviveMask |= 0x2;
        }
        if (onDeadProtectBuffMaplePoint) {
            reviveMask |= 0x4;
        }
        if (onDeadProtectExpMaplePoint) {
            reviveMask |= 0x8;
        }
        outPacket.encodeInt(reviveMask);
        outPacket.encodeByte(anniversary);
        outPacket.encodeInt(reviveType);
        if (onDeadProtectForBuff || onDeadProtectExpMaplePoint) {
            outPacket.encodeInt(protectType);
        }

        return outPacket;
    }

    public static OutPacket skillCooltimeSetM(int skillID, int cdMS) {
        Map<Integer, Integer> cds = new HashMap<>();
        cds.put(skillID, cdMS);
        return skillCooltimeSetM(cds);
    }

    public static OutPacket skillCooltimeSetM(Map<Integer, Integer> cooldowns) {
        var outPacket = new OutPacket(SKILL_COOLTIME_SET_M);

        outPacket.encodeInt(cooldowns.size());
        cooldowns.forEach((skillID, cooldown) -> {
            outPacket.encodeInt(skillID);
            outPacket.encodeInt(cooldown);
        });

        return outPacket;
    }

    public static OutPacket skillCooltimeSet(Map<Integer, Integer> cooldowns) {
        var outPacket = new OutPacket(SKILL_COOLTIME_SET_M);

        outPacket.encodeInt(cooldowns.size());
        cooldowns.forEach((skillID, cooldown) -> {
            outPacket.encodeInt(skillID);
            outPacket.encodeInt(cooldown);
        });

        return outPacket;
    }

    public static OutPacket setBuffProtector(int itemID, boolean active) {
        var outPacket = new OutPacket(SET_BUFF_PROTECTOR);

        outPacket.encodeInt(itemID);
        outPacket.encodeByte(active);

        return outPacket;
    }

    public static OutPacket deathCountInfo(int deathCount) {
        var outPacket = new OutPacket(DEATH_COUNT_INFO);

        outPacket.encodeInt(deathCount);

        return outPacket;
    }

    public static OutPacket familiarAddResult(Familiar familiar, boolean showInfoChanged, boolean adminMob) {
        var outPacket = new OutPacket(FAMILIAR_ADD_RESULT);

        outPacket.encodeLong(familiar.getId());
        familiar.encode(outPacket);
        outPacket.encodeByte(showInfoChanged);
        outPacket.encodeByte(adminMob);

        return outPacket;
    }

    public static OutPacket petMove(int id, int petID, MovementInfo movementInfo) {
        var outPacket = new OutPacket(PET_MOVE);

        outPacket.encodeInt(id);
        outPacket.encodeInt(petID);
        outPacket.encode(movementInfo);

        return outPacket;
    }

    public static OutPacket setDressChanged(boolean on, boolean dressInfinity) {
        var outPacket = new OutPacket(SET_DRESS_CHANGED);

        outPacket.encodeByte(on);
        outPacket.encodeByte(dressInfinity);

        return outPacket;
    }

    public static OutPacket setInGameDirectionMode(boolean lockUI, boolean blackFrame, boolean forceMouseOver) {
        var outPacket = new OutPacket(SET_IN_GAME_DIRECTION_MODE);

        outPacket.encodeByte(lockUI); // Locks User's UI        - Is 'showUI' in IDA
        outPacket.encodeByte(blackFrame); // Usually 1 in gms? (@aviv)
        if(lockUI) {
            outPacket.encodeByte(forceMouseOver);
            outPacket.encodeByte(!lockUI); // showUI
        }

        return outPacket;
    }

    public static OutPacket inGameDirectionEvent(InGameDirectionEvent igdr) {
        var outPacket = new OutPacket(IN_GAME_DIRECTION_EVENT);

        outPacket.encode(igdr);

        return outPacket;
    }

    public static OutPacket hireTutor(boolean set) {
        var outPacket = new OutPacket(HIRE_TUTOR);

        outPacket.encodeByte(set);

        return outPacket;
    }

    public static OutPacket tutorMsg(int id, int duration) {
        var outPacket = new OutPacket(TUTOR_MSG);

        boolean automated = true;
        outPacket.encodeByte(automated);
        outPacket.encodeInt(id);
        outPacket.encodeInt(duration);

        return outPacket;
    }

    public static OutPacket tutorMsg(String message, int width, int duration) {
        var outPacket = new OutPacket(TUTOR_MSG);

        boolean automated = false;
        outPacket.encodeByte(automated);
        outPacket.encodeString(message);
        outPacket.encodeInt(width);
        outPacket.encodeInt(duration);

        return outPacket;
    }

    public static OutPacket emotion(int emotion, int duration, boolean byItemOption) {
        var outPacket = new OutPacket(EMOTION);

        outPacket.encodeInt(emotion);
        outPacket.encodeInt(duration);
        outPacket.encodeByte(byItemOption);

        return outPacket;
    }

    public static OutPacket questResult(QuestType type, int questID, int npcTemplateID, int secondQuestID, boolean startNavigation) {
        var outPacket = new OutPacket(QUEST_RESULT);

        outPacket.encodeByte(type.getVal());
        outPacket.encodeInt(questID);
        outPacket.encodeInt(npcTemplateID);

        outPacket.encodeInt(secondQuestID); // starts a second quest
        outPacket.encodeByte(startNavigation);

        return outPacket;
    }

    public static OutPacket medalReissueResult(MedalReissueResultType medalReissueResultType, int itemId) {
        var outPacket = new OutPacket(MEDAL_REISSUE_RESULT);

        outPacket.encodeByte(medalReissueResultType.getVal());
        outPacket.encodeInt(itemId);

        return outPacket;
    }

    public static OutPacket moveParticleEff(String type, Position startPoint, Position endPoint, int moveTime, int totalCount, int oneSprayMin, int oneSprayMax) {
        var outPacket = new OutPacket(MOVE_PARTICLE_EFF);

        outPacket.encodeString(type);
        outPacket.encodePosition(startPoint);
        outPacket.encodePosition(endPoint);
        outPacket.encodeShort(moveTime);
        outPacket.encodeShort(totalCount);
        outPacket.encodeShort(oneSprayMin);
        outPacket.encodeShort(oneSprayMax);

        return outPacket;
    }

    public static OutPacket balloonMsg(String message, int width, int timeOut, Position position) {
        var outPacket = new OutPacket(BALLOON_MSG);

        outPacket.encodeString(message);
        outPacket.encodeShort(width);// 100
        outPacket.encodeShort(timeOut);// 3
        outPacket.encodeByte(position == null);
        if (position != null) {
            outPacket.encodePosition(position);
        }
        return outPacket;
    }

    public static OutPacket doActivePsychicArea(PsychicArea pa) {
        var outPacket = new OutPacket(DO_ACTIVE_PSYCHIC_AREA);

        outPacket.encodeInt(pa.localPsychicAreaKey);
        outPacket.encodeInt(pa.psychicAreaKey);

        return outPacket;
    }

    public static OutPacket enterFieldPsychicInfo(int userID, PsychicLock pl, List<PsychicArea> psychicAreas) {
        var outPacket = new OutPacket(ENTER_FIELD_PSYCHIC_INFO);

        outPacket.encodeByte(true);

        outPacket.encodeInt(userID);
        if (pl == null) {
            outPacket.encodeInt(0);
        } else {
            outPacket.encodeInt(pl.psychicLockBalls.size());
            for (PsychicLockBall plb : pl.psychicLockBalls) {
                boolean hasMob = plb.mob != null;
                outPacket.encodeByte(plb.success);
                outPacket.encodeInt(plb.localKey);
                outPacket.encodeInt(plb.psychicLockKey);
                outPacket.encodeInt(pl.skillID);
                outPacket.encodeShort(pl.slv);
                outPacket.encodeInt(hasMob ? plb.mob.getObjectId() : 0);
                outPacket.encodeShort(plb.stuffID);
                outPacket.encodeInt(hasMob ? Util.maxInt(plb.mob.getMaxHp()) : 0);
                outPacket.encodeInt(hasMob ? Util.maxInt(plb.mob.getHp()) : 0);
                outPacket.encodeByte(plb.posRelID);
                outPacket.encodePositionInt(plb.start);
                outPacket.encodePositionInt(plb.rel);
            }
        }
        if (psychicAreas == null) {
            outPacket.encodeInt(0);
        } else {
            outPacket.encodeInt(psychicAreas.size());
            for (PsychicArea pa : psychicAreas) {
                pa.encode(outPacket);
            }
        }
        // indicate end
        outPacket.encodeByte(false);

        return outPacket;
    }

    public static OutPacket gatherRequestResult(int lifeId, boolean success) {
        var outPacket = new OutPacket(GATHER_REQUEST_RESULT);
        outPacket.encodeInt(lifeId);
        outPacket.encodeInt(success ? 11 : 0);
        return outPacket;
    }
}
