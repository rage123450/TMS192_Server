package net.swordie.ms.handlers.item;

import net.swordie.ms.client.Client;
import net.swordie.ms.client.character.BroadcastMsg;
import net.swordie.ms.client.character.Char;
import net.swordie.ms.client.character.items.*;
import net.swordie.ms.client.character.quest.Quest;
import net.swordie.ms.client.character.quest.QuestManager;
import net.swordie.ms.connection.InPacket;
import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.connection.packet.FieldPacket;
import net.swordie.ms.connection.packet.UserPacket;
import net.swordie.ms.connection.packet.WvsContext;
import net.swordie.ms.constants.ItemConstants;
import net.swordie.ms.constants.QuestConstants;
import net.swordie.ms.enums.*;
import net.swordie.ms.handlers.Handler;
import net.swordie.ms.handlers.header.InHeader;
import static net.swordie.ms.handlers.header.InHeader.*;
import net.swordie.ms.handlers.header.OutHeader;
import static net.swordie.ms.handlers.header.OutHeader.*;
import net.swordie.ms.loaders.ItemData;
import net.swordie.ms.loaders.QuestData;
import net.swordie.ms.loaders.containerclasses.ItemInfo;
import net.swordie.ms.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static net.swordie.ms.enums.ChatType.Mob;
import static net.swordie.ms.enums.ChatType.SystemNotice;
import static net.swordie.ms.enums.EquipBaseStat.iuc;
import static net.swordie.ms.enums.EquipBaseStat.tuc;
import static net.swordie.ms.enums.InvType.*;

public class ItemUpgradeHandler {

    private static final Logger log = LogManager.getLogger(ItemUpgradeHandler.class);


    @Handler(op = USER_EX_ITEM_UPGRADE_ITEM_USE_REQUEST)
    public static void handleUserExItemUpgradeItemUseRequest(Client c, InPacket inPacket) {
        inPacket.decodeInt(); //tick
        short usePosition = inPacket.decodeShort(); //Use Position
        short eqpPosition = inPacket.decodeShort(); //Equip Position
        byte echantSkill = inPacket.decodeByte(); //boolean

        Char chr = c.getChr();
        Item flame = chr.getInventoryByType(InvType.CONSUME).getItemBySlot(usePosition);
        InvType invType = eqpPosition < 0 ? EQUIPPED : EQUIP;
        Equip equip = (Equip) chr.getInventoryByType(invType).getItemBySlot(eqpPosition);

        if (flame == null || equip == null) {
            chr.chatMessage(SystemNotice, "Could not find flame or equip.");
            chr.dispose();
            return;
        }

        if (!ItemConstants.isRebirthFlame(flame.getItemId())) {
            chr.chatMessage(SystemNotice, "This item is not a rebirth flame.");
            chr.dispose();
            return;
        }

        Map<ScrollStat, Integer> vals = ItemData.getItemInfoByID(flame.getItemId()).getScrollStats();
        if (vals.size() > 0) {
            int reqEquipLevelMax = vals.getOrDefault(ScrollStat.reqEquipLevelMax, 250);

            if (equip.getrLevel() + equip.getiIncReq() > reqEquipLevelMax) {
                c.write(WvsContext.broadcastMsg(BroadcastMsg.popUpMessage("Equipment level does not meet scroll requirements.")));
                chr.dispose();
                return;
            }

            boolean success = Util.succeedProp(vals.getOrDefault(ScrollStat.success, 100));

            if (success) {
                boolean eternalFlame = vals.getOrDefault(ScrollStat.createType, 6) >= 7;
                equip.randomizeFlameStats(eternalFlame); // Generate high stats if it's an eternal/RED flame only.
                //apply flame to zero's weapon
                if (ItemConstants.isLongOrBigSword(equip.getItemId())) {
                    Equip zeroWeapon = (Equip) chr.getEquippedInventory().getItemBySlot(11);
                    if (zeroWeapon != null) {
                        for (EquipBaseStat stat : EquipBaseStat.values()) {
                            zeroWeapon.setBaseStatFlame(stat, (int) equip.getBaseStatFlame(stat));
                        }
                        zeroWeapon.updateToChar(chr);
                    }
                }
            }

            c.write(FieldPacket.showItemUpgradeEffect(chr.getId(), success, false, flame.getItemId(), equip.getItemId(), false));
            equip.updateToChar(chr);
            chr.consumeItem(flame);
        }

        chr.dispose();
    }

    @Handler(op = USER_MEMORIAL_CUBE_OPTION_REQUEST)
    public static void handleUserMemorialCubeOptionRequest(Char chr, InPacket inPacket) {
        inPacket.decodeInt(); // tick
        MemorialCubeInfo mci = chr.getMemorialCubeInfo();
        boolean chooseBefore = inPacket.decodeByte() == 7;
        if (mci != null && chooseBefore) {
            Inventory equipInv = chr.getEquipInventory();
            Equip equip = mci.getOldEquip();
            Equip invEquip = (Equip) equipInv.getItemBySlot(equip.getBagIndex());
            equipInv.removeItem(invEquip);
            equipInv.addItem(equip);
            equip.updateToChar(chr);
        }
        chr.setMemorialCubeInfo(null);
    }

    @Handler(op = GOLD_HAMMER_REQUEST)
    public static void handleGoldHammerRequest(Char chr, InPacket inPacket) {
        if (chr.getClient().getWorld().isReboot()) {
            chr.write(WvsContext.goldHammerItemUpgradeResult(GoldHammerResult.Error, 1, 0));
            chr.getOffenseManager().addOffense(String.format("Character %d attempted to hammer in reboot world.", chr.getId()));
            return;
        }

        inPacket.decodeInt(); // tick
        int iPos = inPacket.decodeInt(); // hammer slot
        int hammerID = inPacket.decodeInt(); // hammer item id
        inPacket.decodeInt(); // use hammer? useless though
        int ePos = inPacket.decodeInt(); // equip slot

        Equip equip = (Equip) chr.getInventoryByType(EQUIP).getItemBySlot((short) ePos);
        Item hammer = chr.getInventoryByType(CONSUME).getItemBySlot((short) iPos);
        short maxHammers = ItemConstants.MAX_HAMMER_SLOTS;

        if (equip != null) {
            Equip defaultEquip = ItemData.getEquipById(equip.getItemId());
            if (defaultEquip.isHasIUCMax()) {
                maxHammers = defaultEquip.getIUCMax();
            }
        }

        if (equip == null || !ItemConstants.canEquipGoldHammer(equip) ||
                hammer == null || !ItemConstants.isGoldHammer(hammer) || hammerID != hammer.getItemId()) {
            chr.write(WvsContext.goldHammerItemUpgradeResult(GoldHammerResult.Error, 1, 0));
            chr.getOffenseManager().addOffense(String.format("Character %d tried to use hammer (id %d) on an invalid equip (id %d)",
                    chr.getId(), hammer == null ? 0 : hammer.getItemId(), equip == null ? 0 : equip.getItemId()));
            return;
        }

        Map<ScrollStat, Integer> vals = ItemData.getItemInfoByID(hammer.getItemId()).getScrollStats();

        if (vals.size() > 0) {
            if (equip.getIuc() >= maxHammers) {
                chr.getOffenseManager().addOffense(String.format("Character %d tried to use hammer (id %d) an invalid equip (id %d)",
                        chr.getId(), hammerID, equip.getItemId()));
                chr.write(WvsContext.goldHammerItemUpgradeResult(GoldHammerResult.Error, 2, 0));
                return;
            }

            boolean success = Util.succeedProp(vals.getOrDefault(ScrollStat.success, 100));

            if (success) {
                equip.addStat(iuc, 1); // +1 hammer used
                equip.addStat(tuc, 1); // +1 upgrades available
                equip.updateToChar(chr);
                chr.write(WvsContext.goldHammerItemUpgradeResult(GoldHammerResult.Success, 0, equip.getIuc()));
            } else {
                chr.write(WvsContext.goldHammerItemUpgradeResult(GoldHammerResult.Fail, 1, equip.getIuc()));
            }

            chr.consumeItem(hammer.getItemId(), 1);
        }
    }

    @Handler(op = GOLD_HAMMER_COMPLETE)
    public static void handleGoldHammerComplete(Char chr, InPacket inPacket) {
        int returnResult = inPacket.decodeInt();
        int result = inPacket.decodeInt();
        if (returnResult == GoldHammerResult.Success.getVal() || returnResult == GoldHammerResult.Fail.getVal()) {
            //I think its ok to just send back the result given.
            chr.write(WvsContext.goldHammerItemUpgradeResult(GoldHammerResult.Done, result, 0));
        } else {
            chr.getOffenseManager().addOffense(String.format("Character %d have invalid gold hammer complete returnResult %d",
                    chr.getId(), returnResult));
        }
    }
    @Handler(op = USER_UPGRADE_ASSIST_ITEM_USE_REQUEST)
    public static void handleUserUpgradeAssistItemUseRequest(Client c, InPacket inPacket) {

        Char chr = c.getChr();
        if (c.getWorld().isReboot()) {
            log.error(String.format("Character %d attempted to scroll in reboot world.", chr.getId()));
            chr.dispose();
            return;
        }
        inPacket.decodeInt(); //tick
        short uPos = inPacket.decodeShort(); //Use Position
        short ePos = inPacket.decodeShort(); //Eqp Position
        byte bEnchantSkill = inPacket.decodeByte(); //no clue what this means exactly
//        short idk = inPacket.decodeShort(); //No clue what this is, stayed  00 00  throughout different tests
        Item scroll = chr.getInventoryByType(InvType.CONSUME).getItemBySlot(uPos);
        InvType invType = ePos < 0 ? EQUIPPED : EQUIP;
        Equip equip = (Equip) chr.getInventoryByType(invType).getItemBySlot(ePos);
        if (scroll == null || equip == null) {
            chr.chatMessage(SystemNotice, "Could not find scroll or equip.");
            return;
        }
        int scrollID = scroll.getItemId();
        switch (scrollID) {
            case 2532000: // Safety Scroll
            case 2532001: // Pet Safety Scroll
            case 2532002: // Safety Scroll
            case 2532003: // Safety Scroll
            case 2532004: // Pet Safety Scroll
            case 2532005: // Safety Scroll
                equip.addAttribute(EquipAttribute.UpgradeCountProtection);
                break;
            case 2530000: // Lucky Day
            case 2530002: // Lucky Day
            case 2530003: // Pet Lucky Day
            case 2530004: // Lucky Day
            case 2530006: // Pet Lucky Day
                equip.addAttribute(EquipAttribute.LuckyDay);
                break;
            case 2531000: // Protection Scroll
            case 2531001:
            case 2531004:
            case 2531005:
                equip.addAttribute(EquipAttribute.ProtectionScroll);
                break;
        }
        c.write(FieldPacket.showItemUpgradeEffect(chr.getId(), true, false, scrollID, equip.getItemId(), false));
        equip.updateToChar(chr);
        chr.consumeItem(scroll);
    }

    @Handler(op = USER_UPGRADE_ITEM_USE_REQUEST)
    public static void handleUserUpgradeItemUseRequest(Client c, InPacket inPacket) {
        Char chr = c.getChr();
        if (c.getWorld().isReboot()) {
            log.error(String.format("Character %d attempted to scroll in reboot world.", chr.getId()));
            chr.dispose();
            return;
        }
        inPacket.decodeInt(); //tick
        short uPos = inPacket.decodeShort(); //Use Position
        short ePos = inPacket.decodeShort(); //Eqp Position
        byte bEnchantSkill = inPacket.decodeByte(); //no clue what this means exactly
        short idk = inPacket.decodeShort(); //No clue what this is, stayed  00 00  throughout different tests
        Item scroll = chr.getInventoryByType(InvType.CONSUME).getItemBySlot(uPos);
        InvType invType = ePos < 0 ? EQUIPPED : EQUIP;
        Equip equip = (Equip) chr.getInventoryByType(invType).getItemBySlot(ePos);
        ItemInfo ii = ItemData.getItemInfoByID(scroll.getItemId());
        Equip zeroWeapon = null;
        if (ItemConstants.isLongOrBigSword(equip.getItemId())) {
            zeroWeapon = (Equip) chr.getEquippedInventory().getItemBySlot(11);
        }
        if (scroll == null || equip == null || equip.hasSpecialAttribute(EquipSpecialAttribute.Vestige)) {
            chr.chatMessage(SystemNotice, "Could not find scroll or equip.");
            chr.dispose();
            return;
        }
        if(ii != null && !ii.getReqItemIds().contains(equip.getItemId())) {
            chr.chatMessage(SystemNotice, "You may not scroll the selected equip with this scroll.");
            chr.dispose();
            return;
        }
        int scrollID = scroll.getItemId();
        boolean success = true;
        Map<ScrollStat, Integer> vals = ItemData.getItemInfoByID(scrollID).getScrollStats();
        if (vals.size() > 0) {
            int chance = vals.getOrDefault(ScrollStat.success, 100);
            success = Util.succeedProp(chance);
            boolean chaos = vals.containsKey(ScrollStat.randStat);
            if (success && chaos && zeroWeapon != null) {
                boolean noNegative = vals.containsKey(ScrollStat.noNegative);
                int max = vals.containsKey(ScrollStat.incRandVol) ? ItemConstants.INC_RAND_CHAOS_MAX : ItemConstants.RAND_CHAOS_MAX;
                for (EquipBaseStat ebs : ScrollStat.getRandStats()) {
                    int cur = (int) equip.getBaseStat(ebs);
                    if (cur == 0) {
                        continue;
                    }
                    int randStat = Util.getRandom(max);
                    randStat = !noNegative && Util.succeedProp(50) ? -randStat : randStat;
                    equip.addStat(ebs, randStat);
                    zeroWeapon.addStat(ebs, randStat);
                }
                equip.addStat(EquipBaseStat.tuc, -1);
                zeroWeapon.addStat(EquipBaseStat.tuc, -1);
                equip.addStat(EquipBaseStat.cuc, 1);
                zeroWeapon.addStat(EquipBaseStat.cuc, 1);
                equip.recalcEnchantmentStats();
                zeroWeapon.recalcEnchantmentStats();
                equip.updateToChar(chr);
                zeroWeapon.updateToChar(chr);
                chr.write(FieldPacket.showItemUpgradeEffect(chr.getId(), success, false, scrollID, equip.getItemId(), false));
                //chr.write(FieldPacket.showItemUpgradeEffect(chr.getId(), success, false, scrollID, zeroWeapon.getItemId(), false));
                chr.consumeItem(scroll);
            } else {
                equip.applyScroll(scroll, chr, success);
                if (zeroWeapon != null) {
                    zeroWeapon.applyScroll(scroll, chr, success);
                }
            }
        }
    }

    @Handler(op = USER_ITEM_OPTION_UPGRADE_ITEM_USE_REQUEST)
    public static void handleUserItemOptionUpgradeItemUseRequest(Client c, InPacket inPacket) {
        Char chr = c.getChr();
        inPacket.decodeInt(); //tick
        short uPos = inPacket.decodeShort();
        short ePos = inPacket.decodeShort();
        byte bEnchantSkill = inPacket.decodeByte(); // bool or byte?
        Item scroll = chr.getInventoryByType(InvType.CONSUME).getItemBySlot(uPos);
        InvType invType = ePos < 0 ? EQUIPPED : EQUIP;
        Equip equip = (Equip) chr.getInventoryByType(invType).getItemBySlot(ePos);
        if (scroll == null || equip == null) {
            chr.chatMessage(SystemNotice, "Could not find scroll or equip.");
            chr.dispose();
            return;
        } else if (!ItemConstants.canEquipHavePotential(equip)) {
            chr.getOffenseManager().addOffense(String.format("Character %d tried to add potential an eligible item (id %d)", chr.getId(), equip.getItemId()));
            chr.dispose();
            return;
        }
        int scrollID = scroll.getItemId();
        Map<ScrollStat, Integer> vals = ItemData.getItemInfoByID(scrollID).getScrollStats();
        int chance = vals.getOrDefault(ScrollStat.success, 100);
        int curse = vals.getOrDefault(ScrollStat.cursed, 0);
        boolean success = Util.succeedProp(chance);
        if (success) {
            short val;
            int thirdLineChance = ItemConstants.THIRD_LINE_CHANCE;
            switch (scrollID / 10) {
                case 204940: // Rare Pot
                case 204941:
                case 204942:
                case 204943:
                case 204944:
                case 204945:
                case 204946:
                    val = ItemGrade.HiddenRare.getVal();
                    equip.setHiddenOptionBase(val, thirdLineChance);
                    break;
                case 204970: // Epic pot
                case 204971:
                    val = ItemGrade.HiddenEpic.getVal();
                    equip.setHiddenOptionBase(val, thirdLineChance);
                    break;
                case 204974: // Unique Pot
                case 204975:
                case 204976:
                case 204979:
                    val = ItemGrade.HiddenUnique.getVal();
                    equip.setHiddenOptionBase(val, thirdLineChance);
                    break;
                case 204978: // Legendary Pot
                    val = ItemGrade.HiddenLegendary.getVal();
                    equip.setHiddenOptionBase(val, thirdLineChance);
                    break;

                default:
                    chr.chatMessage(Mob, "Unhandled scroll " + scrollID);
                    chr.dispose();
                    log.error("Unhandled scroll " + scrollID);
                    return;
            }
        }
        c.write(FieldPacket.showItemUpgradeEffect(chr.getId(), success, false, scrollID, equip.getItemId(), false));
        equip.updateToChar(chr);
        chr.consumeItem(scroll);
    }

    @Handler(op = USER_ITEM_SLOT_EXTEND_ITEM_USE_REQUEST)
    public static void handleUserItemSlotExtendItemUseRequest(Char chr, InPacket inPacket) {
        inPacket.decodeInt(); // tick
        short uPos = inPacket.decodeShort();
        short ePos = inPacket.decodeShort();
        Item item = chr.getConsumeInventory().getItemBySlot(uPos);
        Item equipItem = chr.getEquipInventory().getItemBySlot(ePos);
        if (item == null || equipItem == null) {
            chr.chatMessage("Could not find either the use item or the equip.");
            return;
        }
        int itemID = item.getItemId();
        Equip equip = (Equip) equipItem;
        int successChance = ItemData.getItemInfoByID(itemID).getScrollStats().getOrDefault(ScrollStat.success, 100);
        boolean success = Util.succeedProp(successChance);
        if (success) {
            switch (itemID) {
                case 2049505: // Gold Potential Stamp
                case 2049517:
                    equip.setOption(2, equip.getRandomOption(false, 2, ItemConstants.SYSTEM_DEFAULT_CUBE_INDICATOR,
                            ItemConstants.getAdditionalPrimeCountForCube(ItemConstants.SYSTEM_DEFAULT_CUBE_INDICATOR)), false);
                    break;
                default:
                    log.error("Unhandled slot extend item " + itemID);
                    chr.chatMessage("Unhandled slot extend item " + itemID);
                    return;
            }
            equip.updateToChar(chr);
        }
        chr.consumeItem(item);
        chr.write(FieldPacket.showItemUpgradeEffect(chr.getId(), success, false, itemID, equip.getItemId(), false));
    }

    @Handler(op = USER_ADDITIONAL_OPT_UPGRADE_ITEM_USE_REQUEST)
    public static void handleUserAdditionalOptUpgradeItemUseRequest(Client c, InPacket inPacket) {
        Char chr = c.getChr();
        if (c.getWorld().isReboot()) {
            log.error(String.format("Character %d attempted to use bonus potential in reboot world.", chr.getId()));
            chr.dispose();
            return;
        }
        inPacket.decodeInt(); //tick
        short uPos = inPacket.decodeShort();
        short ePos = inPacket.decodeShort();
        byte bEnchantSkill = inPacket.decodeByte();
        Item scroll = chr.getInventoryByType(InvType.CONSUME).getItemBySlot(uPos);
        InvType invType = ePos < 0 ? EQUIPPED : EQUIP;
        Equip equip = (Equip) chr.getInventoryByType(invType).getItemBySlot(ePos);
        if (scroll == null || equip == null) {
            chr.chatMessage(SystemNotice, "Could not find scroll or equip.");
            return;
        }
        int scrollID = scroll.getItemId();
        boolean success;
        Map<ScrollStat, Integer> vals = ItemData.getItemInfoByID(scrollID).getScrollStats();
        int chance = vals.getOrDefault(ScrollStat.success, 100);
        int curse = vals.getOrDefault(ScrollStat.cursed, 0);
        success = Util.succeedProp(chance);
        if (success) {
            short val;
            int thirdLineChance = ItemConstants.THIRD_LINE_CHANCE;
            switch (scrollID) {
                case 2048305: // Bonus Pot
                case 2048308:
                case 2048309:
                case 2048310:
                case 2048311:
                case 2048313:
                case 2048314:
                case 2048316:
                case 2048329:
                    val = ItemGrade.HiddenRare.getVal();
                    equip.setHiddenOptionBonus(val, thirdLineChance);
                    break;
                case 2048306: // Special Bonus Pot
                case 2048307:
                case 2048315:
                case 2048331:
                    val = ItemGrade.HiddenRare.getVal();
                    equip.setHiddenOptionBonus(val, 100);
                    break;
                default:
                    chr.chatMessage(Mob, "Unhandled scroll " + scrollID);
                    break;
            }
        }
        c.write(FieldPacket.showItemUpgradeEffect(chr.getId(), success, false, scrollID, equip.getItemId(), false));
        equip.updateToChar(chr);
        chr.consumeItem(scroll);
    }

    @Handler(op = USER_ITEM_RELEASE_REQUEST)
    public static void handleUserItemReleaseRequest(Client c, InPacket inPacket) {
        Char chr = c.getChr();
        inPacket.decodeInt(); //tick
        short uPos = inPacket.decodeShort();
        short ePos = inPacket.decodeShort();
        Item item = chr.getInventoryByType(InvType.CONSUME).getItemBySlot(uPos); // old system with magnifying glasses
        InvType invType = ePos < 0 ? EQUIPPED : EQUIP;
        Equip equip = (Equip) chr.getInventoryByType(invType).getItemBySlot(ePos);
        if (equip == null) {
            chr.chatMessage(SystemNotice, "Could not find equip.");
            return;
        }
        boolean base = equip.getOptionBase(0) < 0;
        boolean bonus = equip.getOptionBonus(0) < 0;
        if (base && bonus) {
            equip.releaseOptions(true);
            equip.releaseOptions(false);
        } else {
            equip.releaseOptions(bonus);
        }
        c.write(FieldPacket.showItemReleaseEffect(chr.getId(), ePos, bonus));
        equip.updateToChar(chr);
    }

    @Handler(op = EGO_EQUIP_CHECK_UPDATE_ITEM_REQUEST)
    public static void handleEgoEquipCheckUpdateItemRequest(Client c, InPacket inpacket) {
        Char chr = c.getChr();
        int inv = inpacket.decodeInt();
        int itemSlot = inpacket.decodeInt();
        int idk1 = inpacket.decodeInt();
        int idk2 = inpacket.decodeInt();
        int weaponWindowSlot = inpacket.decodeInt();
        chr.write(UserPacket.checkUpgradeItemResult(weaponWindowSlot, true));
    }

    @Handler(op = EGO_EQUIP_GAUGE_COMPLETE_RETURN)
    public static void handleEgoEquipGaugeCompleteReturn(Client c, InPacket inPacket) {
        var outPacket = new OutPacket(GAUGE_COMPLETE);
        c.write(outPacket);
    }

    @Handler(op = USER_LUCKY_ITEM_USE_REQUEST)
    public static void handleUserLuckyItemUseRequest(Client c, InPacket inPacket) {
        Char chr = c.getChr();
        inPacket.decodeInt(); //idk...
        byte slot = inPacket.decodeByte();
        Item scroll = chr.getConsumeInventory().getItemBySlot(slot);
        if(scroll == null) {
            return;
        }
        ItemInfo scrollInfo = ItemData.getItemInfoByID(scroll.getItemId());
        int setId = scrollInfo.getScrollStats().getOrDefault(ScrollStat.setItemCategory, 0);
        boolean success = Util.succeedProp(scrollInfo.getScrollStats().getOrDefault(ScrollStat.success, 100), 100);
        if (setId != 0 && success) {
            QuestManager qm = chr.getQuestManager();
            if(qm.hasQuestInProgress(QuestConstants.ZERO_SET_QUEST) || qm.hasQuestCompleted(QuestConstants.ZERO_SET_QUEST)){
                qm.removeQuest(QuestConstants.ZERO_SET_QUEST);
            }
            Quest q = QuestData.createQuestFromId(QuestConstants.ZERO_SET_QUEST);
            q.setQrValue(String.valueOf(setId));
            qm.addQuest(q);
        }
        chr.consumeItem(scroll);
        chr.dispose();
    }
}
