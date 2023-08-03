package net.swordie.ms.connection.packet;

import net.swordie.ms.client.character.avatar.AvatarLook;
import net.swordie.ms.client.character.skills.info.AttackInfo;
import net.swordie.ms.client.character.skills.info.MobAttackInfo;
import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.enums.LeaveType;
import net.swordie.ms.life.Summon;
import net.swordie.ms.life.movement.MovementInfo;
import net.swordie.ms.util.Position;

import static net.swordie.ms.handlers.header.OutHeader.*;

/**
 * Created on 5/21/2018.
 */
public class Summoned {

    public static OutPacket summonedAssistAttackRequest(Summon summon) {
        var outPacket = new OutPacket(SUMMONED_ASSIST_ATTACK_REQUEST);

        outPacket.encodeInt(summon.getChr().getId());
        outPacket.encodeInt(summon.getObjectId());

        return outPacket;
    }

    public static OutPacket summonedSummonAttackActive(Summon summon) {
        var outPacket = new OutPacket(SUMMONED_SUMMON_ATTACK_ACTIVE);

        outPacket.encodeInt(summon.getChr().getId());
        outPacket.encodeInt(summon.getObjectId());
        outPacket.encodeByte(summon.isAttackActive());

        return outPacket;
    }

    public static OutPacket summonedSkill(Summon summon, int summonSkillID) {
        var outPacket = new OutPacket(SUMMONED_SKILL);

        outPacket.encodeInt(summon.getChr().getId());
        outPacket.encodeInt(summon.getObjectId());
        outPacket.encodeByte(summonSkillID);

        return outPacket;
    }

    public static OutPacket summonBeholderRevengeAttack(Summon summon, int mob) {
        var outPacket = new OutPacket(SUMMONED_BEHOLDER_REVENGE_ATTACK);

        outPacket.encodeInt(summon.getChr().getId());//char ID
        outPacket.encodeInt(summon.getObjectId());//summon
        outPacket.encodeInt(mob);//mob

        return outPacket;
    }

    public static OutPacket summonedCreated(int charID, Summon summon) {
        var outPacket = new OutPacket(SUMMONED_CREATED);

        outPacket.encodeInt(charID);
        outPacket.encodeInt(summon.getObjectId());
        outPacket.encodeInt(summon.getSkillID());
        outPacket.encodeByte(summon.getCharLevel());
        outPacket.encodeByte(summon.getSlv());
        // CSummoned::Init
        outPacket.encodePosition(summon.getPosition());
        outPacket.encodeByte(summon.getMoveAction());
        outPacket.encodeShort(summon.getCurFoothold());
        outPacket.encodeByte(summon.getMoveAbility().getVal());
        outPacket.encodeByte(summon.getAssistType().getVal());
        outPacket.encodeByte(summon.getEnterType().getVal());
        outPacket.encodeInt(summon.getObjectId());
        outPacket.encodeByte(summon.isFlyMob());
        outPacket.encodeByte(summon.isBeforeFirstAttack());
        outPacket.encodeInt(summon.getTemplateId());
        outPacket.encodeInt(summon.getBulletID());
        AvatarLook al = summon.getAvatarLook();
        outPacket.encodeByte(al != null);
        if(al != null) {
            al.encode(outPacket);
        }
        if(summon.getSkillID() == 35111002) { // Tesla Coil
            outPacket.encodeByte(summon.getTeslaCoilState());
            for(Position pos : summon.getTeslaCoilPositions()) {
                outPacket.encodePosition(pos);
            }
        }
        if(summon.getSkillID() == 42111003) { // Kishin Shoukan
            for(Position pos : summon.getKishinPositions()) {
                outPacket.encodePosition(pos);
            }
        }
        outPacket.encodeByte(summon.isJaguarActive());
        outPacket.encodeInt(summon.getSummonTerm());
        outPacket.encodeByte(summon.isAttackActive());

        return outPacket;
    }

    public static OutPacket summonedRemoved(Summon summon, LeaveType leaveType) {
        var outPacket = new OutPacket(SUMMONED_REMOVED);

        outPacket.encodeInt(summon.getChr().getId());
        outPacket.encodeInt(summon.getObjectId());
        outPacket.encodeByte(leaveType.getVal());

        return outPacket;
    }

    public static OutPacket summonedAttack(int charID, AttackInfo ai, boolean counter) {
        var outPacket = new OutPacket(SUMMONED_ATTACK);

        outPacket.encodeInt(charID);
        outPacket.encodeInt(ai.summon.getObjectId());

        outPacket.encodeByte(ai.summon.getCharLevel());
        byte left = (byte) (ai.left ? 1 : 0);
        outPacket.encodeByte((left << 7) | ai.attackActionType);
        byte attackCount = (byte) (ai.mobAttackInfo.size() > 0 ? ai.mobAttackInfo.get(0).damages.length : 0);
        outPacket.encodeByte((ai.mobCount << 4) | (attackCount & 0xF));
        for (MobAttackInfo mai : ai.mobAttackInfo) {
            outPacket.encodeInt(mai.mobId);
            outPacket.encodeByte(mai.byteIdk1);
            for (int dmg : mai.damages) {
                outPacket.encodeInt(dmg);
            }
        }
        outPacket.encodeByte(counter); // bCounterAttack
        outPacket.encodeByte(ai.attackAction == 0);
        outPacket.encodeShort(ai.attackAction); // ?
        outPacket.encodeShort(ai.attackAction); // ? TODO, one of these is probably attackAction

        return outPacket;
    }

    public static OutPacket summonedMove(int charID, int summonID, MovementInfo movementInfo) {
        var outPacket = new OutPacket(SUMMONED_MOVE);

        outPacket.encodeInt(charID);

        outPacket.encodeInt(summonID);
        outPacket.encode(movementInfo);

        return outPacket;
    }

    public static OutPacket summonedUpdateHPTag(Summon summon) {
        var outPacket = new OutPacket(SUMMONED_UPDATE_HP_TAG);

        outPacket.encodeInt(summon.getObjectId());
        outPacket.encodeInt(summon.getHp());

        return outPacket;
    }
}
