package net.swordie.ms.connection.packet;

import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.life.mob.skill.BurnedInfo;
import net.swordie.ms.util.Util;

import static net.swordie.ms.handlers.header.OutHeader.DOT_DAMAGE_INFO;
import static net.swordie.ms.handlers.header.OutHeader.SERVER_ON_CALC_REQUEST_RESULT;

/**
 * Created on 2/10/2018.
 */
public class BattleRecordMan {
    public static OutPacket serverOnCalcRequestResult(boolean on) {
        var outPacket = new OutPacket(SERVER_ON_CALC_REQUEST_RESULT);

        outPacket.encodeByte(true);

        return outPacket;
    }

    public static OutPacket dotDamageInfo(BurnedInfo burnedInfo, int count) {
        var outPacket = new OutPacket(DOT_DAMAGE_INFO);

        outPacket.encodeInt(Util.maxInt(burnedInfo.getDamage()));
        outPacket.encodeInt(count);

        outPacket.encodeByte(burnedInfo.getDotTickDamR() > 0);
        if (burnedInfo.getDotTickDamR() > 0) {
            outPacket.encodeInt(burnedInfo.getDotTickDamR());
        }
        outPacket.encodeInt(burnedInfo.getSkillId());

        return outPacket;
    }
}
