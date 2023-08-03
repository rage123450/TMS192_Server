package net.swordie.ms.handlers;

import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.handlers.header.OutHeader;
import static net.swordie.ms.handlers.header.OutHeader.*;

/**
 * Created on 1/10/2018.
 */
public class ClientSocket {

    public static OutPacket migrateCommand(boolean succeed, short port) {
        var outPacket = new OutPacket(MIGRATE_COMMAND);

        outPacket.encodeByte(succeed); // will disconnect if false
        if(succeed) {
            byte[] server = new byte[]{127, 0, 0, ((byte) 1)};
            outPacket.encodeArr(server);
            outPacket.encodeShort(port);
            outPacket.encodeInt(0); // ?
        }

        return outPacket;
    }
}
