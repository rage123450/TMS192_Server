package net.swordie.ms.connection.packet;

import net.swordie.ms.client.character.Char;
import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.constants.BossConstants;
import net.swordie.ms.life.DeathType;
import net.swordie.ms.life.mob.*;
import net.swordie.ms.life.mob.skill.BurnedInfo;
import net.swordie.ms.life.mob.skill.MobSkillID;
import net.swordie.ms.life.mob.skill.MobSkillStat;
import net.swordie.ms.life.movement.MovementInfo;
import net.swordie.ms.loaders.containerclasses.MobSkillInfo;
import net.swordie.ms.util.Position;
import net.swordie.ms.util.Rect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static net.swordie.ms.handlers.header.OutHeader.*;

/**
 * Created on 2/28/2018.
 */
public class MobPool {
    public static OutPacket enterField(Mob mob, boolean hasBeenInit) {
        var outPacket = new OutPacket(MOB_ENTER_FIELD);

        outPacket.encodeByte(mob.isSealedInsteadDead());
        outPacket.encodeInt(mob.getObjectId());
        outPacket.encodeByte(mob.getCalcDamageIndex());
        outPacket.encodeInt(mob.getTemplateId());

        ForcedMobStat fms = mob.getForcedMobStat();
        outPacket.encodeByte(fms != null);
        if (fms != null) {
            fms.encode(outPacket);
        }

        mob.getTemporaryStat().encode(outPacket);

        if (!hasBeenInit) {
            // CMob::Init
            mob.encodeInit(outPacket);
        }

        return outPacket;
    }

    public static OutPacket changeController(Mob mob, boolean hasBeenInit, boolean isController) {
        var outPacket = new OutPacket(MOB_CHANGE_CONTROLLER);
        outPacket.encodeByte(isController);
        outPacket.encodeInt(mob.getObjectId());
        if (isController) {
            outPacket.encodeByte(mob.getCalcDamageIndex());
            outPacket.encodeInt(mob.getTemplateId());

            ForcedMobStat fms = mob.getForcedMobStat();
            outPacket.encodeByte(fms != null);
            if (fms != null) {
                fms.encode(outPacket);
            }

            mob.getTemporaryStat().encode(outPacket);

            if (!hasBeenInit) {
                mob.encodeInit(outPacket);
            }
        }

        return outPacket;
    }

    public static OutPacket leaveField(int id, DeathType deadType) {
        var outPacket = new OutPacket(MOB_LEAVE_FIELD);

        outPacket.encodeInt(id);
        outPacket.encodeByte(deadType.getVal());

        return outPacket;
    }

    public static OutPacket forceChase(int mobID, boolean chase) {
        var outPacket = new OutPacket(MOB_FORCE_CHASE);

        outPacket.encodeInt(mobID);
        outPacket.encodeByte(chase);

        return outPacket;
    }

    public static OutPacket damaged(int mobID, long damage, int templateID, byte type, int hp, int maxHp) {
        var outPacket = new OutPacket(MOB_DAMAGED);

        outPacket.encodeInt(mobID);
        outPacket.encodeByte(type);
        damage = damage > Integer.MAX_VALUE ? Integer.MAX_VALUE : damage;
        outPacket.encodeInt((int) damage);
        if (templateID / 10000 == 250 || templateID / 10000 == 251) {
            outPacket.encodeInt(hp);
            outPacket.encodeInt(maxHp);
        }

        return outPacket;
    }

    public static OutPacket hpIndicator(int objectId, byte percDamage) {
        var outPacket = new OutPacket(MOB_HP_INDICATOR);

        outPacket.encodeInt(objectId);
        outPacket.encodeByte(percDamage);

        return outPacket;
    }

    public static OutPacket ctrlAck(Mob mob, boolean nextAttackPossible, short mobCtrlSN, int skillID, byte slv, int forcedAttack) {
        var outPacket = new OutPacket(MOB_CONTROL_ACK);

        outPacket.encodeInt(mob.getObjectId());
        outPacket.encodeShort(mobCtrlSN);
        outPacket.encodeByte(nextAttackPossible);
        outPacket.encodeInt((int) mob.getMp());
        outPacket.encodeInt(skillID);
        outPacket.encodeByte(slv);
        outPacket.encodeInt(forcedAttack);

        return outPacket;
    }

    public static OutPacket ctrlChange(Char chr, Mob mob, boolean isController) {
        var outPacket = new OutPacket(MOB_CHANGE_CONTROLLER);

        outPacket.encodeByte(isController);
        outPacket.encodeInt(mob.getObjectId());
        if (isController) {
            outPacket.encodeByte(1); // controlling type
        }

        return outPacket;

    }

    public static OutPacket statSet(Mob mob, short delay) {
        var outPacket = new OutPacket(MOB_STAT_SET);
        MobTemporaryStat mts = mob.getTemporaryStat();
        var hasMovementStat = mts.hasNewMovementAffectingStat();
        outPacket.encodeInt(mob.getObjectId());
        mts.encode(outPacket);
        outPacket.encodeShort(delay);
        outPacket.encodeByte(1); // nCalcDamageStatIndex
        if (hasMovementStat) {
            outPacket.encodeByte(0); // ?
        }

        return outPacket;
    }

    public static OutPacket statReset(Mob mob, byte byteCalcDamageStatIndex, boolean sn) {
        return statReset(mob, byteCalcDamageStatIndex, sn, null);
    }

    public synchronized static OutPacket statReset(Mob mob, byte calcDamageStatIndex, boolean sn, List<BurnedInfo> biList) {
        var outPacket = new OutPacket(MOB_STAT_RESET);
        outPacket.encodeInt(mob.getObjectId());

        MobTemporaryStat resetStats = mob.getTemporaryStat();
        var mask = resetStats.getRemovedMask();
        Arrays.stream(mask, 0, 3).forEach(outPacket::encodeInt);

        if (resetStats.hasRemovedMobStat(MobStat.BurnedInfo)) {
            if (biList == null) {
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
            } else {
                var dotCount = biList.stream().mapToInt(BurnedInfo::getDotCount).sum();
                outPacket.encodeInt(dotCount);
                outPacket.encodeInt(biList.size());
                biList.forEach(bi -> {
                    outPacket.encodeInt(bi.getCharacterId());
                    outPacket.encodeInt(bi.getSuperPos());
                });
            }
            resetStats.getBurnedInfos().clear();
        }
        outPacket.encodeByte(calcDamageStatIndex);
        if (resetStats.hasRemovedMovementAffectingStat()) {
            outPacket.encodeByte(sn);
        }
        resetStats.getRemovedStatVals().clear();
        return outPacket;
    }

    public static OutPacket specialEffectBySkill(Mob mob, int skillID, int charId, short hit) {
        var outPacket = new OutPacket(MOB_SPECIAL_EFFECT_BY_SKILL);

        outPacket.encodeInt(mob.getObjectId());
        outPacket.encodeInt(skillID);
        outPacket.encodeInt(charId);
        outPacket.encodeShort(hit);

        return outPacket;
    }

    public static OutPacket effectByItem(Mob mob, int itemID, boolean success) {
        var outPacket = new OutPacket(MOB_EFFECT_BY_ITEM);

        outPacket.encodeInt(mob.getObjectId());
        outPacket.encodeInt(itemID);
        outPacket.encodeByte(success);

        return outPacket;
    }

    public static OutPacket affected(Mob mob, int skillID, int slv, boolean userSkill, short delay) {
        var outPacket = new OutPacket(MOB_AFFECTED);

        outPacket.encodeInt(mob.getObjectId());
        outPacket.encodeInt(skillID);
        outPacket.encodeShort(delay);
        outPacket.encodeByte(userSkill);
        outPacket.encodeInt(slv);

        return outPacket;
    }

    public static OutPacket move(Mob mob, MobSkillAttackInfo msai, MovementInfo movementInfo) {
        var outPacket = new OutPacket(MOB_MOVE);

        outPacket.encodeInt(mob.getObjectId());
        outPacket.encodeByte(msai.actionAndDirMask);
        outPacket.encodeByte(msai.action);
        outPacket.encodeInt(msai.targetInfo);

        outPacket.encodeByte(msai.multiTargetForBalls.size());
        msai.multiTargetForBalls.forEach(outPacket::encodePosition);

        outPacket.encodeByte(msai.randTimeForAreaAttacks.size());
        msai.randTimeForAreaAttacks.forEach(outPacket::encodeShort);

        outPacket.encode(movementInfo);

        return outPacket;
    }

    public static OutPacket castingBarSkillStart(int gaugeType, int castingTime, boolean reverseGauge, boolean notShowUI) {
        var outPacket = new OutPacket(MOB_CASTING_BAR_SKILL);

        outPacket.encodeInt(gaugeType);
        outPacket.encodeInt(castingTime);
        outPacket.encodeByte(reverseGauge);
        outPacket.encodeByte(notShowUI);

        return outPacket;
    }

    public static OutPacket createBounceAttackSkill(Mob mob, MobSkillInfo msi, boolean afterConvexSkill) {
        // 0s are the ones where the wz property to take is unknown
        var outPacket = new OutPacket(MOB_BOUNCE_ATTACK_SKILL);

        outPacket.encodeInt(mob.getObjectId());
        outPacket.encodeInt(msi.getId());
        outPacket.encodeInt(msi.getLevel());
        outPacket.encodeByte(afterConvexSkill);
        if (afterConvexSkill) { // save for when an afterConvexSkill is found
            int count = 0;
            outPacket.encodeInt(count); // nCount
            outPacket.encodeByte(false); // bDelayedSkill
            outPacket.encodeInt(0); // nCreateY
            outPacket.encodeInt(0); // nDensity
            outPacket.encodeInt(0); // nFriction
            outPacket.encodeInt(0); // nRestitution
            outPacket.encodeInt(0); // tDestroyDelay
            IntStream.range(0, count).map(i -> mob.getObjectId() + i + 1).forEach(outPacket::encodeInt);
        } else {
            Position pos = mob.getPosition();
            outPacket.encodeInt(pos.getX() + msi.getSkillStatIntValue(MobSkillStat.x));
            outPacket.encodeInt(pos.getY() + msi.getSkillStatIntValue(MobSkillStat.y));
            int count = msi.getSkillStatIntValue(MobSkillStat.count);
            outPacket.encodeInt(count);
            IntStream.range(0, count).forEach(i -> {
                outPacket.encodeInt(mob.getObjectId() + i + 1); // nObjectSN
                outPacket.encodePositionInt(msi.getLt());
            });
            outPacket.encodeInt(msi.getSkillStatIntValue(MobSkillStat.z)); // nFriction
            outPacket.encodeInt(msi.getSkillStatIntValue(MobSkillStat.w)); // nRestitution
            outPacket.encodeInt(BossConstants.LOTUS_BOUNCING_BALL_DURATION); // tDestroyDelay
            outPacket.encodeInt(100); // tStartDelay
            outPacket.encodeByte(msi.getSkillStatIntValue(MobSkillStat.noGravity)); // bNoGravity

            var notDestroyByCollide = msi.getSkillStatIntValue(MobSkillStat.notDestroyByCollide) != 0;
            outPacket.encodeByte(notDestroyByCollide); // bNotDestroyByCollide
            if (msi.getId() == MobSkillID.BounceAttack.getVal() && (msi.getLevel() == 3 || msi.getLevel() == 4)) {
                outPacket.encodePositionInt(msi.getRb2());
            }
            if (notDestroyByCollide) {
                outPacket.encodeInt(5); // nIncScale
                outPacket.encodeInt(200); // nMaxScale
                outPacket.encodeInt(40); // nDecRadius
                outPacket.encodeInt(60); // fAngle
            }
        }


        return outPacket;
    }

    public static OutPacket teleportRequest(int skillAfter, Position position) {
        var outPacket = new OutPacket(MOB_TELEPORT_REQUEST);

        outPacket.encodeByte(skillAfter == 0);
        if (skillAfter != 0) {
            outPacket.encodeInt(skillAfter);
            switch (skillAfter) {
                case 3, 5, 6, 7, 8, 9, 10 -> outPacket.encodePositionInt(position); // possible position?
                case 4 -> outPacket.encodeInt(0); // possible x?
            }
        }

        return outPacket;
    }

    public static OutPacket nextAttack(int mobID, int forcedAttackIdx) {
        var outPacket = new OutPacket(MOB_NEXT_ATTACK);

        outPacket.encodeInt(mobID);
        outPacket.encodeInt(forcedAttackIdx);

        return outPacket;
    }

    public static OutPacket setAfterAttack(int mobID, short afterAttack, int serverAction, boolean left) {
        var outPacket = new OutPacket(MOB_SET_AFTER_ATTACK);

        outPacket.encodeInt(mobID);
        outPacket.encodeShort(afterAttack);
        outPacket.encodeInt(serverAction);
        outPacket.encodeByte(left);

        return outPacket;
    }

    public static OutPacket setSkillDelay(int mobID, int skillAfter, int skillID, int slv, int sequenceDelay, Rect rect) {
        var outPacket = new OutPacket(MOB_SKILL_DELAY);

        outPacket.encodeInt(mobID);
        outPacket.encodeInt(skillAfter);
        outPacket.encodeInt(skillID);
        outPacket.encodeInt(slv);
        outPacket.encodeInt(sequenceDelay);
        if (rect != null) {
            outPacket.encodeRectInt(rect);
        } else {
            outPacket.encodeArr(new byte[16]); // (0,0),(0,0)
        }

        return outPacket;
    }

    public static OutPacket escortFullPath(Mob mob, int oldAttr, boolean stopEscort) {
        var outPacket = new OutPacket(ESCORT_FULL_PATH);

        outPacket.encodeInt(mob.getObjectId());
        outPacket.encodeInt(mob.getEscortDest().size());
        outPacket.encodeShort(mob.getPosition().getX());
        outPacket.encodeShort(oldAttr);
        outPacket.encodeInt(mob.getPosition().getY());
        mob.getEscortDest().forEach(escortDest -> {
            outPacket.encodeShort(escortDest.getDestPos().getX());
            outPacket.encodeShort(escortDest.getAttr());
            outPacket.encodeInt(escortDest.getDestPos().getY());
            outPacket.encodeInt(escortDest.getMass());
            if (escortDest.getMass() == 2) {
                outPacket.encodeInt(escortDest.getStopDuration());
            }
        });
        outPacket.encodeInt(mob.getCurrentDestIndex());
        int stopDuration = mob.getEscortStopDuration();
        outPacket.encodeByte(stopDuration > 0);
        if (stopDuration > 0) {
            outPacket.encodeInt(stopDuration);
        }
        outPacket.encodeByte(stopEscort);
        return outPacket;
    }

    public static OutPacket mobAttackBlock(Mob mob, ArrayList<Integer> skillsIDS) {
        var outPacket = new OutPacket(MOB_ATTACK_BLOCK);

        outPacket.encodeInt(mob.getObjectId());
        outPacket.encodeInt(skillsIDS.size());

        skillsIDS.forEach(outPacket::encodeInt);

        return outPacket;
    }

    public static OutPacket setSkillDelay(Mob mob, int skillAfter, MobSkillInfo msi, int sequenceDelay, List<Rect> rects) {
        var outPacket = new OutPacket(MOB_SKILL_DELAY);

        outPacket.encodeInt(mob.getObjectId());

        outPacket.encodeInt(skillAfter);
        outPacket.encodeInt(msi.getId());
        outPacket.encodeInt(msi.getLevel());
        outPacket.encodeInt(sequenceDelay);
        if (msi.getId() != 0) {
            outPacket.encodeInt(rects.size());
            rects.forEach(outPacket::encodeRectInt);
        }
        return outPacket;
    }
}
