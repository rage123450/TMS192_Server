package net.swordie.ms.connection.packet;

import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.handlers.header.OutHeader;
import static net.swordie.ms.handlers.header.OutHeader.*;
import net.swordie.ms.life.FieldAttackObj;

/**
 * @author Sjonnie
 * Created on 8/19/2018.
 */
public class FieldAttackObjPool {

    public static OutPacket objCreate(FieldAttackObj fieldAttackObj) {
        var outPacket = new OutPacket(FIELD_ATTACK_CREATE);

        outPacket.encode(fieldAttackObj);

        return outPacket;
    }

    public static OutPacket objRemoveByKey(int objectID) {
        var outPacket = new OutPacket(FIELD_ATTACK_REMOVE_BY_KEY);

        outPacket.encodeInt(objectID);

        return outPacket;
    }

    public static OutPacket setAttack(int objectId, int attackIdx) {
        var outPacket = new OutPacket(FIELD_ATTACK_SET_ATTACK);

        outPacket.encodeInt(objectId);
        outPacket.encodeInt(attackIdx);

        return outPacket;
    }
}
