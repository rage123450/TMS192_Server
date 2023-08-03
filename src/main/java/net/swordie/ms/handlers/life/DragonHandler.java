package net.swordie.ms.handlers.life;

import net.swordie.ms.client.character.Char;
import net.swordie.ms.connection.InPacket;
import net.swordie.ms.connection.packet.DragonPool;
import net.swordie.ms.handlers.Handler;
import net.swordie.ms.handlers.header.InHeader;
import static net.swordie.ms.handlers.header.InHeader.*;
import net.swordie.ms.life.Dragon;
import net.swordie.ms.life.movement.MovementInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DragonHandler {

    private static final Logger log = LogManager.getLogger(DragonHandler.class);


    @Handler(op = DRAGON_MOVE)
    public static void handleDragonMove(Char chr, InPacket inPacket) {
        Dragon dragon = chr.getDragon();
        if (dragon != null && dragon.getOwner() == chr) {
            MovementInfo movementInfo = new MovementInfo(inPacket);
            movementInfo.applyTo(dragon);
            chr.getField().broadcastPacket(DragonPool.moveDragon(dragon, movementInfo), chr);
        }
    }
}
