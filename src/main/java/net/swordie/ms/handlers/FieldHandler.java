package net.swordie.ms.handlers;

import net.swordie.ms.client.character.Char;
import net.swordie.ms.connection.InPacket;
import net.swordie.ms.connection.packet.Effect;
import net.swordie.ms.connection.packet.FieldPacket;
import net.swordie.ms.connection.packet.UserPacket;
import net.swordie.ms.connection.packet.UserRemote;
import net.swordie.ms.handlers.header.InHeader;
import static net.swordie.ms.handlers.header.InHeader.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FieldHandler {

    private static final Logger log = LogManager.getLogger(FieldHandler.class);

    @Handler(op = BROADCAST_EFFECT_TO_SPLIT)
    public static void handleBroadcastEffectToSplit(Char chr, InPacket inPacket) {
        String effectPath = inPacket.decodeString();
        int duration = inPacket.decodeInt();
        int option = inPacket.decodeInt();
        chr.write(UserPacket.effect(Effect.effectFromWZ(effectPath, true, duration, option, 0)));
        chr.getField().broadcastPacket(UserRemote.effect(chr.getId(), Effect.effectFromWZ(effectPath, true, duration, option, 0)), chr);
    }

    @Handler(op = BROADCAST_ONE_TIME_ACTION_TO_SPLIT)
    public static void handleBroadcastOneTimeActionToSplit(Char chr, InPacket inPacket) {
        int action = inPacket.decodeInt();
        int duration = inPacket.decodeInt();
        chr.getField().broadcastPacket(FieldPacket.setOneTimeAction(chr.getId(), action, duration));
    }
}
