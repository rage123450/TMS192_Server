package net.swordie.ms.handlers.user;

import net.swordie.ms.Server;
import net.swordie.ms.client.Account;
import net.swordie.ms.client.Client;
import net.swordie.ms.client.User;
import net.swordie.ms.client.character.Char;
import net.swordie.ms.client.character.HyperTPRock;
import net.swordie.ms.client.character.damage.DamageSkinType;
import net.swordie.ms.client.character.skills.TownPortal;
import net.swordie.ms.client.friend.result.LoadFriendResult;
import net.swordie.ms.client.jobs.JobManager;
import net.swordie.ms.client.party.Party;
import net.swordie.ms.connection.InPacket;
import net.swordie.ms.connection.packet.*;
import net.swordie.ms.constants.GameConstants;
import net.swordie.ms.constants.JobConstants;
import net.swordie.ms.enums.FieldOption;
import net.swordie.ms.enums.MapTransferType;
import net.swordie.ms.handlers.ClientSocket;
import net.swordie.ms.handlers.Handler;
import net.swordie.ms.handlers.header.InHeader;
import static net.swordie.ms.handlers.header.InHeader.*;
import net.swordie.ms.life.npc.Npc;
import net.swordie.ms.loaders.NpcData;
import net.swordie.ms.scripts.ScriptType;
import net.swordie.ms.util.container.Tuple;
import net.swordie.ms.world.event.InGameEventManager;
import net.swordie.ms.world.field.Field;
import net.swordie.ms.world.field.FieldInstanceType;
import net.swordie.ms.world.field.Portal;
import net.swordie.ms.world.shop.cashshop.CashShop;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;

public class MigrationHandler {

    private static final Logger log = LogManager.getLogger(MigrationHandler.class);


    @Handler(op = MIGRATE_IN)
    public static void handleMigrateIn(Client c, InPacket inPacket) {
        int worldId = inPacket.decodeInt();
        int charId = inPacket.decodeInt();
        byte[] machineID = inPacket.decodeArr(16);
        Tuple<Byte, Client> info = Server.getInstance().getChannelFromTransfer(charId, worldId);
        byte channel = info.getLeft();
        Client oldClient = info.getRight();
        if (!oldClient.hasCorrectMachineID(machineID)) {
            c.write(WvsContext.returnToTitle());
            return;
        }
        c.setMachineID(machineID);
        c.setOldChannel(oldClient.getOldChannel());
        User user = oldClient.getUser();
        c.setUser(user);
        Account acc = oldClient.getAccount();
        c.setAccount(acc);
        Server.getInstance().getWorldById(worldId).getChannelById(channel).removeClientFromTransfer(charId);
        c.setChannel(channel);
        c.setWorldId((byte) worldId);
        c.setChannelInstance(Server.getInstance().getWorldById(worldId).getChannelById(channel));
        Char chr = oldClient.getChr();
        if (chr == null || chr.getId() != charId) {
            chr = acc.getCharById(charId);
        }
        user.setCurrentChr(chr);
        user.setCurrentAcc(acc);
        chr.setUser(user);
        chr.setClient(c);
        chr.setAccount(acc);
        acc.setCurrentChr(chr);
        acc.setUser(user);
        chr.initEquips();
        chr.initAndroid(false);
        c.setChr(chr);
        c.getChannelInstance().addChar(chr);
        chr.setJobHandler(JobManager.getJobById(chr.getJob(), chr));
        chr.setFieldInstanceType(FieldInstanceType.CHANNEL);
        Server.getInstance().addUser(user);
        Field field = chr.getOrCreateFieldByCurrentInstanceType(chr.getFieldID() <= 0 ? 100000000 : chr.getFieldID());
        if (chr.getHP() <= 0) { // automatically revive when relogging
            chr.heal(chr.getMaxHP() / 2, true);
        }
        if (chr.getPartyID() != 0) {
            Party party = c.getWorld().getPartybyId(chr.getPartyID());
            if (party == null) {
                chr.setPartyID(0);
            } else {
                chr.setParty(party);
            }
        }
        // blessing has to be split up, as adding skills before SetField is send will crash the client
        chr.initBlessingSkillNames();
        chr.warp(field, true);

        chr.initBlessingSkills();
        c.write(WvsContext.temporaryStats_Reset());
        c.write(WvsContext.updateEventNameTag(new int[]{}));
        if (chr.getGuild() != null) {
            chr.setGuild(chr.getClient().getWorld().getGuildByID(chr.getGuild().getId()));
        }
        if (JobConstants.isBeastTamer(chr.getJob())) {
            c.write(FieldPacket.beastTamerFuncKeyMappedManInit(chr.getFuncKeyMaps()));
        } else {
            c.write(FieldPacket.funcKeyMappedManInit(chr));
//            c.write(FieldPacket.funcKeyMappedManInit(chr.getFuncKeyMap()));
        }
        chr.setBulletIDForAttack(chr.calculateBulletIDForAttack(1));
        c.write(WvsContext.friendResult(new LoadFriendResult(chr.getAllFriends())));
        c.write(WvsContext.macroSysDataInit(chr.getMacros()));
        c.write(UserLocal.damageSkinSaveResult(DamageSkinType.Req_SendInfo, null, chr));
        c.write(WvsContext.mapTransferResult(MapTransferType.RegisterListSend, (byte) 5, chr.getHyperRockFields()));
        acc.getMonsterCollection().init(chr);
        chr.checkAndRemoveExpiredItems();
        chr.initBaseStats();
        chr.setOnline(true); // v195+: respect 'invisible login' setting
        chr.getOffenseManager().setChr(chr);

        c.write(WvsContext.setMaplePoint(acc.getNxCredit()));
    }

    @Handler(op = USER_TRANSFER_FIELD_REQUEST)
    public static void handleUserTransferFieldRequest(Client c, InPacket inPacket) {
        Char chr = c.getChr();
        if (inPacket.getUnreadAmount() == 0) {
            // Coming back from the cash shop
//            chr.warp(chr.getOrCreateFieldByCurrentInstanceType(chr.getFieldID()));
            c.getChannelInstance().addClientInTransfer(c.getChannel(), chr.getId(), c);
            c.write(ClientSocket.migrateCommand(true, (short) c.getChannelInstance().getPort()));
            return;
        }
        byte fieldKey = inPacket.decodeByte();
        int targetField = inPacket.decodeInt();//-1
        if (targetField == 106020100 || targetField == 106020400) {
            // same issue as below, its in mushroom kingdom so maybe the maps are just outdated or w/e
            targetField = 106020403;
        } else if (targetField == 106020402) {
            // warping from portal in 106020403 is unable to find defined portal (not a scripted portal)
            targetField = 106020000;
        }
//        int x = inPacket.decodeShort();
//        int y = inPacket.decodeShort();
        String portalName = inPacket.decodeString();
        if (portalName != null && !"".equals(portalName)) {
            Field field = chr.getField();
            Portal portal = field.getPortalByName(portalName);
            if (portal.getScript() != null && !portal.getScript().equals("")) {
                chr.getScriptManager().startScript(portal.getId(), portal.getScript(), ScriptType.Portal);
            } else {
                Field toField = chr.getOrCreateFieldByCurrentInstanceType(portal.getTargetMapId());
                if (toField == null) {
                    return;
                }
                Portal toPortal = toField.getPortalByName(portal.getTargetPortalName());
                if (toPortal == null) {
                    toPortal = toField.getPortalByName("sp");
                }
                chr.warp(toField, toPortal);
            }
        } else if (chr.getHP() <= 0) {
            if (InGameEventManager.getInstance().charInEventMap(chr.getId())) {
                InGameEventManager.getInstance().getActiveEvent().onMigrateDeath(chr);
                chr.heal(chr.getMaxHP(), true);
                chr.healMP(chr.getMaxMP());
                return;
            }

            // Character is dead, respawn request
            inPacket.decodeByte(); // always 0
            byte tarfield = inPacket.decodeByte(); // ?
            byte reviveType = inPacket.decodeByte();

            int returnMap = chr.getField().getReturnMap() == 999999999
                    ? chr.getPreviousFieldID()
                    : chr.getField().getReturnMap();

            switch (reviveType) {
                // so far only got 0?
            }
            if (!chr.hasBuffProtector()) {
                chr.getTemporaryStatManager().removeAllStats();
            }
            int deathcount = chr.getDeathCount();
            if (deathcount != 0) {
                if (deathcount > 0) {
                    deathcount--;
                    chr.setDeathCount(deathcount);
                    chr.write(UserLocal.deathCountInfo(deathcount));
                }
                chr.setNearestReturnPortal(); // attempt to respawn them where they died.. maybe portal 0 is better tho?
                chr.warp(chr.getFieldID(), chr.getPreviousPortalID(), false);
                chr.healHPMP();
                return;

            } else if (chr.getInstance() != null) {
                chr.getInstance().removeChar(chr);
            } else if (chr.getTransferField() == targetField && chr.getTransferFieldReq() == chr.getField().getId()) {
                Field toField = chr.getOrCreateFieldByCurrentInstanceType(chr.getTransferField());
                if (toField != null && chr.getTransferField() > 0) {
                    chr.warp(toField);
                }
                chr.setTransferField(0);
                return;
            } else {
                chr.warp(chr.getOrCreateFieldByCurrentInstanceType(chr.getField().getForcedReturn()));
            }
            chr.healHPMP();
            chr.setBuffProtector(false);
        } else if (chr.getTransferField() == targetField && chr.getTransferFieldReq() == chr.getField().getId()) {
            Field toField = chr.getOrCreateFieldByCurrentInstanceType(chr.getTransferField());
            if (toField != null && chr.getTransferField() > 0) {
                chr.warp(toField);
            }
            chr.setTransferField(0);
        }
    }

    @Handler(op = USER_PORTAL_SCRIPT_REQUEST)
    public static void handleUserPortalScriptRequest(Client c, InPacket inPacket) {
        Char chr = c.getChr();

        if (chr.getHP() <= 0) {
            chr.dispose();
            return;
        }

        byte portalID = inPacket.decodeByte();
        String portalName = inPacket.decodeString();
        Portal portal = chr.getField().getPortalByName(portalName);
        String script = portalName;
        if (portal != null) {
            portalID = (byte) portal.getId();
            script = "".equals(portal.getScript()) ? portalName : portal.getScript();
            chr.getScriptManager().startScript(portalID, script, ScriptType.Portal);
        } else {
            chr.chatMessage("Could not find that portal.");
        }
    }


    @Handler(op = USER_TRANSFER_CHANNEL_REQUEST)
    public static void handleUserTransferChannelRequest(Client c, InPacket inPacket) {
        Char chr = c.getChr();
        byte channelId = (byte) (inPacket.decodeByte() + 1);
        if (c.getWorld().getChannelById(channelId) == null) {
            chr.chatMessage("Could not find that world.");
            return;
        }

        if (chr.getHP() <= 0) {
            chr.dispose();
            return;
        }

        Field field = chr.getField();
        if ((field.getFieldLimit() & FieldOption.MigrateLimit.getVal()) > 0
                || channelId < 1 || channelId > c.getWorld().getChannels().size()) {
            chr.dispose();
            return;
        }

        chr.changeChannel(channelId);
    }

    @Handler(op = USER_MIGRATE_TO_CASH_SHOP_REQUEST)
    public static void handleUserMigrateToCashShopRequest(Client c, InPacket inPacket) {
        Char chr = c.getChr();
        Field field = chr.getField();
        if ((field.getFieldLimit() & FieldOption.MigrateLimit.getVal()) > 0 || chr.getHP() <= 0) {
            chr.dispose();
            return;
        }

        if (chr.getHP() <= 0) {
            chr.dispose();
            return;
        }

        chr.setInCashShop(true);
        chr.punishLieDetectorEvasion();
        CashShop cs = Server.getInstance().getCashShop();
        c.write(Stage.setCashShop(chr, cs));
        c.write(Stage.setCashShopInfo(chr, cs));
        c.write(CCashShop.loadLockerDone(chr.getAccount()));
        c.write(CCashShop.queryCashResult(chr));
        c.write(CCashShop.bannerInfo(cs));
        c.write(CCashShop.cartInfo(cs));
        c.write(CCashShop.featuredItemInfo(cs));
        c.write(CCashShop.specialItemInfo(cs));
        c.write(CCashShop.specialSaleInfo(cs));
        c.write(CCashShop.topSellerInfo(cs));
        c.write(CCashShop.categoryInfo(cs));
        c.write(CCashShop.bannerMsg(cs, new ArrayList<>(Arrays.asList("Welcome to SwordieMS!", "Enjoy your time here."))));
        c.write(CCashShop.oneTen(cs));
    }

    @Handler(op = USER_MAP_TRANSFER_REQUEST)
    public static void handleUserMapTransferRequest(Char chr, InPacket inPacket) {
        chr.punishLieDetectorEvasion();

        if (chr.getHP() <= 0) {
            chr.dispose();
            return;
        }

        byte mtType = inPacket.decodeByte();
        byte itemType = inPacket.decodeByte();

        MapTransferType mapTransferType = MapTransferType.getByVal(mtType);
        switch (mapTransferType) {
            case DeleteListRecv: //Delete request that's received
                int targetFieldID = inPacket.decodeInt();
                HyperTPRock.removeFieldId(chr, targetFieldID);
                chr.write(WvsContext.mapTransferResult(MapTransferType.DeleteListSend, itemType, chr.getHyperRockFields()));
                break;

            case RegisterListRecv: //Register request that's received
                targetFieldID = chr.getFieldID();
                Field field = chr.getField();
                if (field == null || (field.getFieldLimit() & FieldOption.TeleportItemLimit.getVal()) > 0) {
                    chr.chatMessage("You may not warp to that map.");
                    chr.dispose();
                    return;
                }
                HyperTPRock.addFieldId(chr, targetFieldID);
                chr.write(WvsContext.mapTransferResult(MapTransferType.RegisterListSend, itemType, chr.getHyperRockFields()));
                break;

        }
    }

    @Handler(op = USER_FIELD_TRANSFER_REQUEST)
    public static void handleUserFieldTransferRequest(Char chr, InPacket inPacket) {
        Field field = chr.getField();
        if ((field.getFieldLimit() & FieldOption.TeleportItemLimit.getVal()) > 0
                || (field.getFieldLimit() & FieldOption.MigrateLimit.getVal()) > 0
                || (field.getFieldLimit() & FieldOption.PortalScrollLimit.getVal()) > 0
                || !field.isChannelField()) {
            chr.chatMessage("You may not warp to that map.");
            chr.dispose();
            return;
        }

        if (chr.getHP() <= 0) {
            chr.dispose();
            return;
        }

        int fieldID = inPacket.decodeInt();
        if (fieldID == 7860) {
            Field ardentmill = chr.getOrCreateFieldByCurrentInstanceType(GameConstants.ARDENTMILL);
            chr.warp(ardentmill);
        }
    }

    @Handler(op = MAKE_ENTER_FIELD_PACKET_FOR_QUICK_MOVE)
    public static void handleMakeEnterFieldPacketForQuickMove(Char chr, InPacket inPacket) {
        int templateID = inPacket.decodeInt();
        if (chr == null) {
            return;
        }
        Field field = chr.getField();
        QuickMoveInfo qmi = GameConstants.getQuickMoveInfos().stream().filter(info -> info.getTemplateID() == templateID).findFirst().orElseGet(null);
        if (qmi == null) {
            chr.dispose();
            chr.getOffenseManager().addOffense(String.format("Attempted to use non-existing quick move NPC (%d).", templateID));
            return;
        }
        if (qmi.isNoInstances() && field.isChannelField()) {
            chr.dispose();
            chr.getOffenseManager().addOffense(String.format("Attempted to use quick move (%s) in illegal map (%d).", qmi.getMsg(), field.getId()));
            return;
        }

        if (chr.getHP() <= 0) {
            chr.dispose();
            return;
        }

        Npc npc = NpcData.getNpcDeepCopyById(templateID);
        String script = npc.getScripts().get(0);
        if (script == null) {
            script = String.valueOf(npc.getTemplateId());
        }
        chr.getScriptManager().startScript(npc.getTemplateId(), templateID, script, ScriptType.Npc);

    }

    @Handler(op = ENTER_TOWN_PORTAL_REQUEST)
    public static void handleEnterTownPortalRequest(Char chr, InPacket inPacket) {
        int chrId = inPacket.decodeInt(); // Char id
        boolean town = inPacket.decodeByte() != 0;

        if (chr.getHP() <= 0) {
            chr.dispose();
            return;
        }

        Field field = chr.getField();
        TownPortal townPortal = field.getTownPortalByChrId(chrId);
        if (townPortal != null) {       // TODO Using teleports, as grabbing the TownPortalPoint portal id is not working
            if (town) {
                // townField -> fieldField
                Field fieldField = townPortal.getChannel().getField(townPortal.getFieldFieldId());

                chr.warp(fieldField); // Back to the original Door
                chr.write(FieldPacket.teleport(townPortal.getFieldPosition(), chr)); // Teleports player to the position of the TownPortal
            } else {
                // fieldField -> townField
                Field returnField = townPortal.getChannel().getField(townPortal.getTownFieldId()); // Initialise the Town Map,

                chr.warp(returnField); // warp Char
                chr.write(FieldPacket.teleport(townPortal.getTownPosition(), chr));
                if (returnField.getTownPortalByChrId(chrId) == null) { // So that every re-enter into the TownField doesn't spawn another TownPortal
                    returnField.broadcastPacket(WvsContext.townPortal(townPortal)); // create the TownPortal
                    returnField.addTownPortal(townPortal);
                }
            }
        } else {
            chr.dispose();
            log.warn("Character {" + chrId + "} tried entering a Town Portal in field {" + field.getId() + "} which does not exist."); // Potential Hacking Log
        }
    }

    @Handler(op = USER_TRANSFER_FREE_MARKET_REQUEST)
    public static void handleTransferFreeMarketRequest(Char chr, InPacket inPacket) {
        byte toChannelID = (byte) (inPacket.decodeByte() + 1);
        int fieldID = inPacket.decodeInt();

        if (chr.getHP() <= 0) {
            chr.dispose();
            return;
        }

        if (chr.getWorld().getChannelById(toChannelID) != null && GameConstants.isFreeMarketField(fieldID)
                && GameConstants.isFreeMarketField(chr.getField().getId())) {
            Field toField = chr.getClient().getChannelInstance().getField(fieldID);
            if (toField == null) {
                chr.dispose();
                return;
            }
            int currentChannelID = chr.getClient().getChannel();
            if (currentChannelID != toChannelID) {
                chr.changeChannelAndWarp(toChannelID, fieldID);
            } else {
                chr.warp(toField);
            }
        }

        inPacket.decodeInt(); // tick
    }

    @Handler(op = GOLLUX_OUT_REQUEST)
    public static void handleGolluxOutReqeust(Char chr, InPacket inPacket) {
        if (chr.getFieldID() / 1000000 != 863) {
            return;
        }
        String script = "GolluxOutReqeust";
        int npcId = 9010000; //admin npc
        chr.getScriptManager().startScript(npcId, npcId, script, ScriptType.Npc);
    }

}
