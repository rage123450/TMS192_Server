package net.swordie.ms.connection.packet;

import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.world.shop.NpcShopDlg;
import net.swordie.ms.world.shop.result.ShopResult;

import static net.swordie.ms.handlers.header.OutHeader.SHOP_OPEN;
import static net.swordie.ms.handlers.header.OutHeader.SHOP_RESULT;

/**
 * Created on 3/28/2018.
 */
public class ShopDlg {

    public static OutPacket openShop(int petTemplateID, NpcShopDlg nsd) {

        var outPacket = new OutPacket(SHOP_OPEN);
        outPacket.encodeByte(petTemplateID != 0);
        if (petTemplateID != 0) {
            outPacket.encodeInt(petTemplateID);
        }
        nsd.encode(outPacket);

        return outPacket;
    }

    public static OutPacket shopResult(ShopResult shopResult) {
        var outPacket = new OutPacket(SHOP_RESULT);

        outPacket.encodeByte(shopResult.getType().getVal());
        shopResult.encode(outPacket);

        return outPacket;
    }
}
