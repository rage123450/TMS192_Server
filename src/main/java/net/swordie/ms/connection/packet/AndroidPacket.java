package net.swordie.ms.connection.packet;

import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.life.Android;
import net.swordie.ms.life.movement.MovementInfo;

import static net.swordie.ms.handlers.header.OutHeader.*;

/**
 * @author Sjonnie
 * Created on 2/12/2019.
 */
public class AndroidPacket {

    public static OutPacket created(Android android) {
        var outPacket = new OutPacket(ANDROID_CREATED);

        outPacket.encodeInt(android.getChrId());
        outPacket.encode(android);

        return outPacket;
    }

    public static OutPacket removed(Android android) {
        var outPacket = new OutPacket(ANDROID_REMOVED);

        outPacket.encodeInt(android.getChrId());

        return outPacket;
    }

    public static OutPacket move(Android android, MovementInfo mi) {
        var outPacket = new OutPacket(ANDROID_MOVE);

        outPacket.encodeInt(android.getChrId());
        mi.encode(outPacket);

        return outPacket;
    }

    public static OutPacket actionSet(Android android, int action, int randomKey) {
        var outPacket = new OutPacket(ANDROID_ACTION_SET);

        outPacket.encodeInt(android.getChrId());
        outPacket.encodeByte(action);
        outPacket.encodeByte(randomKey);

        return outPacket;
    }

    public static OutPacket modified(Android android) {
        var outPacket = new OutPacket(ANDROID_MODIFIED);

        outPacket.encodeInt(android.getChrId());

        // from the right: 1st 7 bits for each equip, 8th bit for face+eye+hair+name
        // 0xFF is a full update
        outPacket.encodeByte(0xFF);

        android.getItems().forEach(outPacket::encodeInt);

        android.encodeAndroidInfo(outPacket);

        return outPacket;
    }
}
