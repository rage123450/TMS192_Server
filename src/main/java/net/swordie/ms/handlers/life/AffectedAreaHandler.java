package net.swordie.ms.handlers.life;

import net.swordie.ms.client.character.Char;
import net.swordie.ms.connection.InPacket;
import net.swordie.ms.handlers.Handler;
import net.swordie.ms.handlers.header.InHeader;
import static net.swordie.ms.handlers.header.InHeader.*;
import net.swordie.ms.world.field.Field;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AffectedAreaHandler {

    private static final Logger log = LogManager.getLogger(AffectedAreaHandler.class);


    @Handler(op = USER_AFFECTED_AREA_REMOVE_BY_TIME)
    public static void handleUserAffectedAreaRemoveByTime(Char chr, InPacket inPacket) {
        int skillID = inPacket.decodeInt();

        Field field = chr.getField();
        field.getAffectedAreas().stream().filter(aa -> aa.getOwner() == chr && aa.getSkillID() == skillID).findFirst().ifPresent(field::removeLife);
    }
}
