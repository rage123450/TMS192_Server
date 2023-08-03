package net.swordie.ms.handlers;

import net.swordie.ms.Server;
import net.swordie.ms.ServerConfig;
import net.swordie.ms.ServerConstants;
import net.swordie.ms.client.Account;
import net.swordie.ms.client.Client;
import net.swordie.ms.client.User;
import net.swordie.ms.client.character.BroadcastMsg;
import net.swordie.ms.client.character.Char;
import net.swordie.ms.client.character.CharacterStat;
import net.swordie.ms.client.character.items.Equip;
import net.swordie.ms.client.character.skills.temp.CharacterTemporaryStat;
import net.swordie.ms.client.jobs.JobManager;
import net.swordie.ms.connection.InPacket;
import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.connection.db.DatabaseManager;
import net.swordie.ms.connection.packet.Login;
import net.swordie.ms.connection.packet.WvsContext;
import net.swordie.ms.constants.GameConstants;
import net.swordie.ms.constants.ItemConstants;
import net.swordie.ms.constants.JobConstants;
import net.swordie.ms.enums.CashItemType;
import net.swordie.ms.enums.CharNameResult;
import net.swordie.ms.enums.LoginType;
import net.swordie.ms.handlers.header.OutHeader;
import net.swordie.ms.loaders.ItemData;
import net.swordie.ms.util.Util;
import net.swordie.ms.world.Channel;
import net.swordie.ms.world.World;
import org.apache.logging.log4j.LogManager;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

import static java.util.stream.IntStream.range;
import static net.swordie.ms.enums.InvType.EQUIPPED;
import static net.swordie.ms.handlers.header.InHeader.*;
import static net.swordie.ms.handlers.header.OutHeader.ISSUE_RELOGIN_COOKIE;
import static net.swordie.ms.handlers.header.OutHeader.PRIVATE_SERVER_PACKET_SEND;

/**
 * Created on 4/28/2017.
 */
public class LoginHandler {

    private static final org.apache.logging.log4j.Logger log = LogManager.getRootLogger();

    @Handler(op = PERMISSION_REQUEST)
    public static void handlePermissionRequest(Client client, InPacket inPacket) {
        byte locale = inPacket.decodeByte();
        short version = inPacket.decodeShort();
        String minorVersion = inPacket.decodeString(1);
        if (locale != ServerConstants.LOCALE || version != ServerConstants.VERSION) {
            log.info(String.format("Client %s has an incorrect version.", client.getIP()));
            client.close();
        }
    }

    @Handler(op = USE_AUTH_SERVER)
    public static void handleAuthServer(Client client, InPacket inPacket) {
        client.write(Login.sendAuthServer(false));
    }

    @Handler(op = APPLIED_HOT_FIX)
    public static void handleAppliedHotFix(Client client, InPacket inPacket) {
        // First 4 bytes of a SHA1 hash of Data.wz, 0's if does not exist
        byte[] appliedHotFix = inPacket.decodeArr(4);
        boolean incorrectHotFix = true;
        File dataWz = new File("resources/Data.wz");
        if (dataWz.exists()) {
            try {
                byte[] hotFix = Files.readAllBytes(dataWz.toPath());
                byte[] dataWzHash = Util.sha1Hash(hotFix);
                if (dataWzHash == null) {
                    log.error("Data.wz hashing has failed.");
                    client.write(Login.setHotFix(incorrectHotFix));
                    return;
                }
                // Only care about the first 4 bytes of the hash
                dataWzHash = Arrays.copyOfRange(dataWzHash, 0, 4);
                if (Arrays.equals(dataWzHash, appliedHotFix)) {
                    incorrectHotFix = false;
                    client.write(Login.setHotFix(incorrectHotFix));
                    return;
                }
                ArrayList<Byte> encryptedHotFixLen = new ArrayList<>();
                int cryptHotFixLen = hotFix.length << 1;
                while (cryptHotFixLen > 0x80) {
                    encryptedHotFixLen.add((byte) ((cryptHotFixLen & 0x7F) | 0x80));
                    cryptHotFixLen = cryptHotFixLen >> 7;
                }
                encryptedHotFixLen.add((byte) cryptHotFixLen);
                client.write(Login.setHotFix(encryptedHotFixLen, dataWzHash, hotFix));
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        client.write(Login.setHotFix(incorrectHotFix));
    }

    @Handler(op = PONG)
    public static void handlePong(Client c, InPacket inPacket) {
    }

    @Handler(op = MAPLOGIN_REQUEST)
    public static void handleMapLoginRequest(Client client, InPacket inPacket) {
        client.write(Login.getLoginBackground());
    }

    @Handler(op = CHECK_LOGIN_AUTH_INFO)
    public static void handleCheckLoginAuthInfo(Client c, InPacket inPacket) {
//        byte sid = inPacket.decodeByte();
        byte[] machineID = inPacket.decodeArr(16);
        inPacket.decodeArr(6);
        String username = inPacket.decodeString();
        String password = inPacket.decodeString();
//        byte[] machineID = inPacket.decodeArr(16);

        boolean success;
        LoginType result;
        User user = User.getFromDBByName(username);
        if (user != null) {
            if ("helphelp".equalsIgnoreCase(password)) {
                user.unstuck();
                c.write(WvsContext.broadcastMsg(BroadcastMsg.popUpMessage("Your account is now logged out.")));
            }
            String dbPassword = user.getPassword();
            boolean hashed = Util.isStringBCrypt(dbPassword);
            if (hashed) {
                try {
                    success = BCrypt.checkpw(password, dbPassword);
                } catch (IllegalArgumentException e) { // if password hashing went wrong
                    log.error("bcrypt check in login has failed! dbPassword: " + dbPassword + "; stack trace: " + Arrays.toString(e.getStackTrace()));
                    success = false;
                }
            } else {
                success = password.equals(dbPassword);
            }
            result = success ? LoginType.Success : LoginType.IncorrectPassword;
            if (success) {
                if (Server.getInstance().isUserLoggedIn(user)) {
                    success = false;
                    result = LoginType.AlreadyConnected;
                } else if (user.getBanExpireDate() != null && !user.getBanExpireDate().isExpired()) {
                    success = false;
                    result = LoginType.Blocked;
                    String banMsg = String.format("You have been banned. \nReason: %s. \nExpire date: %s",
                            user.getBanReason(), user.getBanExpireDate().toLocalDateTime());
                    c.write(WvsContext.broadcastMsg(BroadcastMsg.popUpMessage(banMsg)));
                } else {
                    if (!hashed) {
                        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(ServerConstants.BCRYPT_ITERATIONS)));
                        // if a user has an assigned pic, hash it
                        if (user.getPic() != null && user.getPic().length() >= 6 && !Util.isStringBCrypt(user.getPic())) {
                            user.setPic(BCrypt.hashpw(user.getPic(), BCrypt.gensalt(ServerConstants.BCRYPT_ITERATIONS)));
                        }
                    }
                    Server.getInstance().addUser(user);
                    c.setUser(user);
                    c.setMachineID(machineID);
                    DatabaseManager.saveToDB(user);
                }
            }
        } else {
            result = LoginType.NotRegistered;
            success = false;
        }

        c.write(Login.checkPasswordResult(success, result, user));

        if (result == LoginType.Success) {
            handleWorldInfoRequest(c, inPacket);
        }
    }

    @Handler(op = WORLD_LIST_REQUEST)
    public static void handleWorldListRequest(Client c, InPacket packet) {
        for (World world : Server.getInstance().getWorlds()) {
            c.write(Login.sendWorldInformation(world, null));
        }
        c.write(Login.sendWorldInformationEnd());
        c.write(Login.sendRecommendWorldMessage(ServerConfig.WORLD_ID, ServerConfig.RECOMMEND_MSG));
    }

    @Handler(op = WORLD_INFO_REQUEST)
    public static void handleWorldInfoRequest(Client c, InPacket packet) {
        for (World world : Server.getInstance().getWorlds()) {
            c.write(Login.sendWorldInformation(world, null));
        }
        c.write(Login.sendWorldInformationEnd());
    }


    @Handler(op = SERVERSTATUS_REQUEST)
    public static void handleServerStatusRequest(Client c, InPacket inPacket) {
//        c.write(Login.sendWorldInformation(null));
        c.write(Login.sendWorldInformationEnd());
    }

    @Handler(op = WORLD_STATUS_REQUEST)
    public static void handleWorldStatusRequest(Client c, InPacket inPacket) {
        byte worldId = inPacket.decodeByte();
        c.write(Login.sendServerStatus(worldId));
    }

    @Handler(op = SELECT_WORLD)
    public static void handleSelectWorld(Client c, InPacket inPacket) {
        byte code = 0; // success code
        String authInfo = null;
        byte[] machineID = new byte[16];

        byte type = inPacket.decodeByte();//1=重新登入
        byte worldId = inPacket.decodeByte();
        byte channel = (byte) (type == 0 ? inPacket.decodeByte() + 1 : 1);
        boolean RE_LOGIN = type == 1;
        if (RE_LOGIN) {
            authInfo = inPacket.decodeString();
            machineID = inPacket.decodeArr(16);
            //00 00 00 00 02 01 00
            //31 10 48 62 玩家外網IP
        }

        User user = c.getUser();

        if (RE_LOGIN) {
            System.out.println("auth token info: " + authInfo);
            int userID = Server.getInstance().getUserIdFromAuthToken(authInfo);
            user = User.getFromDBById(userID);
        }

        if (user == null) {
            c.write(WvsContext.broadcastMsg(BroadcastMsg.popUpMessage("Cannot find user or your token is expired. restart the client and try again.")));
            return;
        }

        Account account = user.getAccountByWorldId(worldId);
        World world = Server.getInstance().getWorldById(worldId);

        if (world != null && world.getChannelById(channel) != null) {
            if (account == null) {
                account = new Account(user, worldId);
                DatabaseManager.saveToDB(account); // assign id
                user.addAccount(account);
                DatabaseManager.saveToDB(user); // add to user's list of accounts
            }

            c.setUser(user);
            c.setAccount(account);
            account.setUser(user);
            user.setCurrentAcc(account);

            c.write(Login.SET_CLIENT_KEY(new SecureRandom().nextLong()));
            if (RE_LOGIN) {
                c.setMachineID(machineID);
                c.write(Login.sendAccountInfo(user));
            }

            c.setWorldId(worldId);
            c.setChannel(channel);

            c.write(Login.SET_PHYSICAL_WORLD_ID(worldId));
            c.write(Login.selectWorldResult(c.getUser(), c.getAccount(), code,
                    Server.getInstance().getWorldById(worldId).isReboot() ? "reboot" : "normal", false));
        } else {
            c.write(Login.selectCharacterResult(LoginType.UnauthorizedUser, (byte) 0, 0, 0));
        }
    }

    @Handler(op = CHECK_DUPLICATE_ID)
    public static void handleCheckDuplicatedID(Client c, InPacket inPacket) {
        String name = inPacket.decodeString();
        CharNameResult code;
        if (!GameConstants.isValidName(name)) {
            code = CharNameResult.Unavailable_Invalid;
        } else {
            code = Char.getFromDBByNameAndWorld(name, c.getAccount().getWorldId()) == null ? CharNameResult.Available : CharNameResult.Unavailable_InUse;
        }
        c.write(Login.checkDuplicatedIDResult(name, code.getVal()));
    }

    @Handler(op = CREATE_NEW_CHARACTER)
    public static void handleCreateNewCharacter(Client c, InPacket inPacket) {
        Account acc = c.getAccount();
        String name = inPacket.decodeString();
        int keySettingType = inPacket.decodeInt();
        int eventNewCharSaleJob = inPacket.decodeInt();
        int curSelectedRace = inPacket.decodeInt();
        JobConstants.JobEnum job = JobConstants.LoginJob.getLoginJobById(curSelectedRace).getBeginJob();
        short curSelectedSubJob = inPacket.decodeShort();
        byte gender = inPacket.decodeByte();
        byte skin = inPacket.decodeByte();

        byte itemLength = inPacket.decodeByte();
        int[] items = range(0, itemLength).map(i -> inPacket.decodeInt()).toArray(); //face, hair, markings, skin, overall, top, bottom, cape, boots, weapon
        int face = items[0];
        int hair = items[1];
        CharNameResult code = null;
        if (!ItemData.isStartingItems(items) || skin > ItemConstants.MAX_SKIN || skin < 0
                || face < ItemConstants.MIN_FACE || face > ItemConstants.MAX_FACE
                || hair < ItemConstants.MIN_HAIR || hair > ItemConstants.MAX_HAIR) {
            c.getUser().getOffenseManager().addOffense("Tried to add items unavailable on char creation.");
            code = CharNameResult.Unavailable_CashItem;
        }

        if (!GameConstants.isValidName(name)) {
            code = CharNameResult.Unavailable_Invalid;
        } else if (Char.getFromDBByNameAndWorld(name, acc.getWorldId()) != null) {
            code = CharNameResult.Unavailable_InUse;
        }
        if (code != null) {
            c.write(Login.checkDuplicatedIDResult(name, code.getVal()));
            return;
        }

        Char chr = new Char(acc.getId(), name, keySettingType, eventNewCharSaleJob, job.getJobId(),
                curSelectedSubJob, gender, skin, face, hair, items);
        JobManager.getJobById(job.getJobId(), chr).setCharCreationStats(chr);

        chr.initFuncKeyMaps(keySettingType, JobConstants.isBeastTamer(chr.getJob()));
        DatabaseManager.saveToDB(chr);
        acc.addCharacter(chr);

        CharacterStat cs = chr.getAvatarData().getCharacterStat();
        if (curSelectedRace == JobConstants.LoginJob.DUAL_BLADE.getJobType()) {
            cs.setSubJob(1);
        }
        cs.setCharacterId(chr.getId());
        cs.setCharacterIdForLog(chr.getId());
        cs.setWorldIdForLog(acc.getWorldId());
        for (int i : chr.getAvatarData().getAvatarLook().getHairEquips()) {
            Equip equip = ItemData.getEquipDeepCopyFromID(i, false);
            if (equip != null && equip.getItemId() >= 1000000) {
                equip.setBagIndex(ItemConstants.getBodyPartFromItem(
                        equip.getItemId(), chr.getAvatarData().getAvatarLook().getGender()));
                chr.addItemToInventory(EQUIPPED, equip, true);
            }
        }

//        Equip codex = ItemData.getEquipDeepCopyFromID(1172000, false);
//        codex.setInvType(EQUIPPED);
//        codex.setBagIndex(BodyPart.MonsterBook.getVal());
//        chr.addItemToInventory(EQUIPPED, codex, true);

        if (curSelectedRace == JobConstants.LoginJob.ZERO.getJobType()) { // Zero hack for adding 2nd weapon (removing it in hairequips for zero look)
//        if (curSelectedRace == 15) { // Zero hack for adding 2nd weapon (removing it in hairequips for zero look)
            Equip equip = ItemData.getEquipDeepCopyFromID(1562000, false);
            equip.setBagIndex(ItemConstants.getBodyPartFromItem(
                    equip.getItemId(), chr.getAvatarData().getAvatarLook().getGender()));
            chr.addItemToInventory(EQUIPPED, equip, true);
        }
        DatabaseManager.saveToDB(acc);
        c.write(Login.createNewCharacterResult(LoginType.Success, chr));
    }

    @Handler(op = DELETE_CHARACTER)
    public static void handleDeleteCharacter(Client c, InPacket inPacket) {
        if (c.getAccount() != null && handleCheckSpwRequest(c, inPacket)) {
            int charId = inPacket.decodeInt();
            Account acc = c.getAccount();
            Char chr = acc.getCharById(charId);
            if (chr != null) {
                acc.removeLinkSkillByOwnerID(chr.getId());
                acc.getCharacters().remove(chr);
                DatabaseManager.saveToDB(acc);
                c.write(Login.sendDeleteCharacterResult(charId, LoginType.Success));
            } else {
                c.write(Login.sendDeleteCharacterResult(charId, LoginType.UnauthorizedUser));
            }
        }
    }

    @Handler(op = CLIENT_ERROR)
    public static void handleClientError(Client c, InPacket inPacket) {
        c.close();
        if (inPacket.getData().length < 8) {
            log.error(String.format("Error: %s", inPacket));
            return;
        }
        short type = inPacket.decodeShort();
        String type_str = switch (type) {
            case 0x01 -> "SendBackupPacket";
            case 0x02 -> "Crash Report";
            case 0x03 -> "Exception";
            default -> "Unknown?!";
        };
        int errortype = inPacket.decodeInt();
        short data_length = inPacket.decodeShort();

        int idk = inPacket.decodeInt();

        short op = inPacket.decodeShort();

        OutHeader opcode = OutHeader.getOutHeaderByOp(op);
        log.error(String.format("[Error %s] (%s / %d) Data: %s", errortype, opcode, op, inPacket));
        if (opcode == OutHeader.TEMPORARY_STAT_SET) {
            range(0, CharacterTemporaryStat.length).forEach(i -> {
                int mask = inPacket.decodeInt();
                Arrays.stream(CharacterTemporaryStat.values()).filter(cts -> cts.getPos() == i && (cts.getVal() & mask) != 0).map(cts -> String.format("[Error %s] Contained stat %s", errortype, cts.toString())).forEach(log::error);
            });
        } else if (opcode == OutHeader.CASH_SHOP_CASH_ITEM_RESULT) {
            byte cashType = inPacket.decodeByte();
            CashItemType cit = CashItemType.getResultTypeByVal(cashType);
            log.error(String.format("[Error %s] CashItemType %s", errortype, cit == null ? "Unknown" : cit.toString()));
        }
    }

    @Handler(op = PRIVATE_SERVER_PACKET)
    public static void handlePrivateServerPacket(Client c, InPacket inPacket) {
        c.write(Login.sendAuthResponse(((int) PRIVATE_SERVER_PACKET_SEND.getValue()) ^ inPacket.decodeInt()));
    }

    @Handler(ops = {SELECT_CHARACTER, CREATE_CHAR_SELECT})
    public static void handleCharSelect_(Client c, InPacket inPacket) {
        int characterId = inPacket.decodeInt();
        inPacket.decodeByte();

        byte worldId = c.getWorldId();
        byte channelId = c.getChannel();
        Channel channel = Server.getInstance().getWorldById(worldId).getChannelById(channelId);
        Server.getInstance().getWorldById(worldId).getChannelById(channelId).addClientInTransfer(channelId, characterId, c);
        c.write(Login.selectCharacterResult(LoginType.Success, (byte) 0, channel.getPort(), characterId));
    }

    @Handler(op = CHAR_SELECT_NO_PIC)
    public static void handleCharSelectNoPic(Client c, InPacket inPacket) {
        inPacket.decodeArr(2);
        int characterId = inPacket.decodeInt();
        String mac = inPacket.decodeString();
        String somethingElse = inPacket.decodeString();
        String pic = BCrypt.hashpw(inPacket.decodeString(), BCrypt.gensalt(ServerConstants.BCRYPT_ITERATIONS));
        c.getUser().setPic(pic);

        DatabaseManager.saveToDB(c.getUser());
        if (c.getUser().getCharById(characterId) == null) {
            c.write(Login.selectCharacterResult(LoginType.UnauthorizedUser, (byte) 0, 0, 0));
            return;
        }
        byte worldId = c.getWorldId();
        byte channelId = c.getChannel();
        Channel channel = Server.getInstance().getWorldById(worldId).getChannelById(channelId);
        c.write(Login.selectCharacterResult(LoginType.Success, (byte) 0, channel.getPort(), characterId));
    }

    @Handler(op = CHAR_SELECT)
    public static void handleCharSelect(Client c, InPacket inPacket) {
        int characterId = inPacket.decodeInt();
        String name = inPacket.decodeString();
        byte worldId = c.getWorldId();
        byte channelId = c.getChannel();
        Channel channel = Server.getInstance().getWorldById(worldId).getChannelById(channelId);
        if (c.isAuthorized() && c.getAccount().hasCharacter(characterId)) {
            Server.getInstance().getWorldById(worldId).getChannelById(channelId).addClientInTransfer(channelId, characterId, c);
            c.write(Login.selectCharacterResult(LoginType.Success, (byte) 0, channel.getPort(), characterId));
        }
        // if anything is wrong, the 2nd pwd authorizer should return an error
    }


    @Handler(op = CHANGE_PIC_REQUEST)
    public static void handleChangePicRequest(Client c, InPacket inPacket) {
        String currentPic = inPacket.decodeString();

        if (BCrypt.checkpw(currentPic, c.getUser().getPic())) {
            String unencryptedPic = inPacket.decodeString();
            if (unencryptedPic.length() < 6) {
                c.write(Login.changePicResponse(LoginType.InsufficientSPW));
            } else if (BCrypt.checkpw(unencryptedPic, c.getUser().getPassword())) {
                c.write(Login.changePicResponse(LoginType.SamePasswordAndSPW));
            } else {
                String pic = BCrypt.hashpw(unencryptedPic, BCrypt.gensalt(ServerConstants.BCRYPT_ITERATIONS));
                c.getUser().setPic(pic);
                // Update in DB
                DatabaseManager.saveToDB(c.getUser());
                c.write(Login.changePicResponse(LoginType.Success));
            }
        } else {
            c.write(Login.changePicResponse(LoginType.IncorrectSPW));
        }
    }

    @Handler(op = CHECK_SPW_REQUEST)
    public static boolean handleCheckSpwRequest(Client c, InPacket inPacket) {
        boolean success = false;
        String pic = inPacket.decodeString();
//        int userId = inPacket.decodeInt();
        // after this: 2 strings indicating pc info. Not interested in that rn
        if (BCrypt.checkpw(pic, c.getUser().getPic())) {
            success = true;
        } else {
            c.write(Login.selectCharacterResult(LoginType.IncorrectPassword, (byte) 0, 0, 0));
        }
        c.setAuthorized(success);
        return success;
    }

    @Handler(op = CHECK_SPW_EXIST_REQUEST)
    public static void handleCheckSndPasswordExistRequest(Client c, InPacket inPacket) {
        c.write(Login.secondPasswordWindows(c.getUser()));
    }

    @Handler(op = EXCEPTION_LOG)
    public static void handleExceptionLog(Client c, InPacket inPacket) {
        String str = inPacket.decodeString();
        log.error("Exception log: " + str);
    }

    @Handler(op = WVS_CRASH_CALLBACK)
    public static void handleWvsCrashCallback(Client c, InPacket inPacket) {
        if (c != null && c.getChr() != null) {
            c.getChr().setChangingChannel(false);
            c.getChr().logout();
        }
    }

    @Handler(op = REQUEST_RELOGIN_COOKIE)
    public static void handleReuestRelogin(Client c, InPacket inPacket) {
        if (c.getChr() == null) { //
            return;
        }

        byte[] token = new byte[216];
        for (int i = 0; i < token.length; i++) {
            token[i] = (byte) Util.getRandom('0', '~');
        }

        var tokenStr = new String(token);
        Server.getInstance().addAuthToken(tokenStr, c.getUser().getId());

//        System.out.println("tokenStr: " + tokenStr);

        var outPacket = new OutPacket(ISSUE_RELOGIN_COOKIE);
        outPacket.encodeString(tokenStr);
        c.write(outPacket);
    }
}
