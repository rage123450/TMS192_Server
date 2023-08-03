package net.swordie.ms.connection.packet;

import net.swordie.ms.Server;
import net.swordie.ms.ServerConstants;
import net.swordie.ms.ServerStatus;
import net.swordie.ms.client.Account;
import net.swordie.ms.client.User;
import net.swordie.ms.client.character.Char;
import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.constants.GameConstants;
import net.swordie.ms.constants.JobConstants;
import net.swordie.ms.enums.LoginType;
import net.swordie.ms.util.FileTime;
import net.swordie.ms.util.Position;
import net.swordie.ms.util.container.Tuple;
import net.swordie.ms.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

import static net.swordie.ms.handlers.header.OutHeader.*;

/**
 * Created by Tim on 2/28/2017.
 */
public class Login {

    public static OutPacket sendConnect(byte[] siv, byte[] riv) {
        var oPacket = new OutPacket();

        // version (short) + MapleString (short + char array size) + local IV (int) + remote IV (int) + locale (byte)
        // 0xE
        oPacket.encodeShort((short) 15/*16*/);
        oPacket.encodeShort(ServerConstants.VERSION);
        oPacket.encodeString(ServerConstants.MINOR_VERSION);
        oPacket.encodeArr(siv);
        oPacket.encodeArr(riv);
        oPacket.encodeByte(ServerConstants.LOCALE);
        oPacket.encodeByte(0);
//        oPacket.encodeByte(false);

        return oPacket;
    }

    public static OutPacket sendAliveReq() {
        return new OutPacket(ALIVE_REQ);
    }

    public static OutPacket sendAuthServer(boolean useAuthServer) {
        var outPacket = new OutPacket(AUTH_SERVER);
        outPacket.encodeByte(useAuthServer);
        return outPacket;
    }

    public static OutPacket setHotFix(boolean incorrectHotFix) {
        var outPacket = new OutPacket(SET_HOT_FIX);
        outPacket.encodeByte(incorrectHotFix);
        return outPacket;
    }

    public static OutPacket setHotFix(ArrayList<Byte> encryptedHotFixLen, byte[] dataWzHash, byte[] hotFix) {
        var outPacket = new OutPacket(SET_HOT_FIX);
        for(Byte lenByte : encryptedHotFixLen)  {
            outPacket.encodeByte(lenByte);
        }
        outPacket.encodeArr(dataWzHash);
        outPacket.encodeArr(hotFix);
        return outPacket;
    }

    public static OutPacket getLoginBackground() {
        var outPacket = new OutPacket(MAPLOGIN);
//        String[] bg = {"MapLogin", "MapLogin1", "MapLogin2"};
        outPacket.encodeString("MapLogin"/*bg[(int) (Math.random() * bg.length)]*/);
        outPacket.encodeInt(GameConstants.getTime());

        return outPacket;
    }

    public static OutPacket checkPasswordResult(boolean success, LoginType msg, User user) {
        var outPacket = new OutPacket(CHECK_PASSWORD_RESULT);

        if (success) {
            outPacket.encodeByte(LoginType.Success.getValue());
//            outPacket.encodeByte(0);
//            outPacket.encodeInt(0);
            outPacket.encodeString(user.getName());
            outPacket.encodeInt(user.getId());
            outPacket.encodeByte(user.getGender());
            outPacket.encodeByte(user.getMsg2());
            outPacket.encodeInt(0/*user.getAccountType().getVal()*/);
            outPacket.encodeInt(0);
            outPacket.encodeInt(0);
            outPacket.encodeInt(user.getAge());
            outPacket.encodeByte(user.getpBlockReason());
            outPacket.encodeByte(false);
            outPacket.encodeLong(user.getChatUnblockDate());
            outPacket.encodeByte(false);
            outPacket.encodeLong(user.getChatUnblockDate());
            outPacket.encodeByte(!user.hasCensoredNxLoginID());
            if (user.hasCensoredNxLoginID()) {
                outPacket.encodeString(user.getCensoredNxLoginID());
            }
            outPacket.encodeString(user.getName());
//            outPacket.encodeInt(user.getCharacterSlots() + 3);
            JobConstants.encode(outPacket);
            outPacket.encodeByte(user.getGradeCode());
            outPacket.encodeInt(-1);
            outPacket.encodeByte(0); // idk
//            outPacket.encodeByte(0); // ^
//            outPacket.encodeFT(user.getCreationDate());
        } else if (msg == LoginType.Blocked) {
            outPacket.encodeByte(msg.getValue());
//            outPacket.encodeByte(0);
            outPacket.encodeInt(0);
            outPacket.encodeByte(0); // nReason
            outPacket.encodeFT(user.getBanExpireDate());
        } else {
            outPacket.encodeByte(msg.getValue());
//            outPacket.encodeByte(0); // these two aren't in ida, wtf
            outPacket.encodeInt(0);
        }

        return outPacket;
    }

    public static OutPacket checkPasswordResultForBan(User user) {
        var outPacket = new OutPacket(CHECK_PASSWORD_RESULT);

        outPacket.encodeByte(LoginType.BlockedNexonID.getValue());
        outPacket.encodeByte(0);
        outPacket.encodeInt(0);
        outPacket.encodeByte(0);
        outPacket.encodeFT(user.getBanExpireDate());

        return outPacket;
    }

    public static OutPacket sendWorldInformation(World world, Set<Tuple<Position, String>> stringInfos) {
        // CLogin::OnWorldInformation
        var outPacket = new OutPacket(WORLD_INFORMATION);

        outPacket.encodeByte(world.getWorldId());
        outPacket.encodeString(world.getName());
        outPacket.encodeByte(world.getWorldState());
        outPacket.encodeString(world.getWorldEventDescription());
        outPacket.encodeShort(world.getWorldEventEXP_WSE());
        outPacket.encodeShort(world.getWorldEventDrop_WSE());
//        outPacket.encodeByte(world.isCharCreateBlock());

        outPacket.encodeByte(world.getChannels().size());
        world.getChannels().forEach(c -> {
            outPacket.encodeString(c.getName());
            outPacket.encodeInt(c.getGaugePx());
            outPacket.encodeByte(c.getWorldId());
            outPacket.encodeByte(c.getChannelId());
            outPacket.encodeByte(c.isAdultChannel());
        });
        if (stringInfos == null) {
            outPacket.encodeShort(0);
        } else {
            outPacket.encodeShort(stringInfos.size());
            stringInfos.forEach(stringInfo -> {
                outPacket.encodePosition(stringInfo.getLeft());
                outPacket.encodeString(stringInfo.getRight());
            });
        }
        outPacket.encodeInt(0); // some offset
        outPacket.encodeByte(false); // connect with star planet stuff, not interested

        return outPacket;
    }

    public static OutPacket sendWorldInformationEnd() {
        var outPacket = new OutPacket(WORLD_INFORMATION);

        outPacket.encodeByte(-1);
        outPacket.encodeByte(0);
        outPacket.encodeByte(0);
        outPacket.encodeByte(0);

        return outPacket;
    }

    public static OutPacket sendAccountInfo(User user) {
        var outPacket = new OutPacket(ACCOUNT_INFO_RESULT);

        outPacket.encodeByte(0); // succeed
        outPacket.encodeInt(user.getId());
        outPacket.encodeByte(user.getGender());
//        outPacket.encodeByte(user.getGradeCode());
        outPacket.encodeByte(user.getMsg2());
        outPacket.encodeInt(0/*user.getAccountType().getVal()*/);
        outPacket.encodeInt(0);
        outPacket.encodeInt(0);
        outPacket.encodeInt(user.getAge());
        outPacket.encodeByte(user.getpBlockReason());
        outPacket.encodeByte(false);// 1 = 帳號禁止說話
        outPacket.encodeLong(user.getChatUnblockDate());// 禁止說話期限
        outPacket.encodeByte(false);
        outPacket.encodeLong(user.getChatUnblockDate());
        outPacket.encodeString(user.getName());
        outPacket.encodeString(user.getName());
        outPacket.encodeString("");
        JobConstants.encode(outPacket);
        outPacket.encodeByte(user.getGradeCode());
        outPacket.encodeInt(-1);
        outPacket.encodeByte(0); // idk

        return outPacket;
    }

    public static OutPacket sendServerStatus(byte worldId) {
        var outPacket = new OutPacket(SERVER_STATUS);
        World world = null;
        for (World w : Server.getInstance().getWorlds()) {
            if (w.getWorldId() == worldId) {
                world = w;
            }
        }
        if (world != null && !world.isFull()) {
            outPacket.encodeByte(world.getStatus());
//            outPacket.encodeByte(world.getStatus().getValue());
        } else {
            outPacket.encodeByte(ServerStatus.BUSY);
//            outPacket.encodeByte(ServerStatus.BUSY.getValue());
        }
        outPacket.encodeByte(0); // ?

        return outPacket;
    }

    public static OutPacket SET_CLIENT_KEY(long key) {
        var outPacket = new OutPacket(SET_CLIENT_KEY);
        outPacket.encodeLong(key);

        return outPacket;
    }

    public static OutPacket SET_PHYSICAL_WORLD_ID(int worldId) {
        var outPacket = new OutPacket(SET_PHYSICAL_WORLD_ID);
        outPacket.encodeInt(worldId);

        return outPacket;
    }

    public static OutPacket selectWorldResult(User user, Account account, byte code, String specialServer,
                                              boolean burningEventBlock) {
        var outPacket = new OutPacket(SELECT_WORLD_RESULT);

        outPacket.encodeByte(code);
        outPacket.encodeString(specialServer);
        outPacket.encodeInt(account.getTrunk().getSlotCount());
        outPacket.encodeByte(burningEventBlock); // bBurningEventBlock
        int reserved = 0;
        outPacket.encodeInt(reserved); // Reserved size
        outPacket.encodeFT(FileTime.fromType(FileTime.Type.ZERO_TIME)); //Reserved timestamp
        for (int i = 0; i < reserved; i++) {
            // not really interested in this
            FileTime ft = FileTime.fromType(FileTime.Type.ZERO_TIME);
            outPacket.encodeInt(ft.getLowDateTime());
            ft.encode(outPacket);
        }
        boolean isEdited = false;
        outPacket.encodeByte(isEdited); // edited characters

//        List<Char> chars = new ArrayList<>(account.getCharacters());
        var chars = new ArrayList<>(account.getCharacters());

        // order of chars
        chars.sort(Comparator.comparingInt(Char::getId));
        int orderSize = chars.size();
        outPacket.encodeInt(orderSize);
        chars.stream().mapToInt(Char::getId).forEach(outPacket::encodeInt);

        outPacket.encodeByte(chars.size());
        chars.forEach(chr -> {
            boolean hasRanking = chr.getRanking() != null && !JobConstants.isGmJob(chr.getJob());
            chr.getAvatarData().encode(outPacket);
            outPacket.encodeByte(false); // family stuff, deprecated (v61 = &v2->m_abOnFamily.a[v59];)
            outPacket.encodeByte(hasRanking);
            if (hasRanking) {
                chr.getRanking().encode(outPacket);
            }
        });
        outPacket.encodeByte(3/*user.getPicStatus().getVal()*/); // bLoginOpt
        outPacket.encodeByte(false); // bQuerySSNOnCreateNewCharacter
        outPacket.encodeInt(user.getCharacterSlots());
        outPacket.encodeInt(0); // buying char slots
        outPacket.encodeInt(-1); // nEventNewCharJob
        outPacket.encodeByte(false);
        outPacket.encodeFT(FileTime.fromType(FileTime.Type.ZERO_TIME));
        outPacket.encodeByte(0); // nRenameCount
        outPacket.encodeByte(0);
        outPacket.encodeInt(0);
        outPacket.encodeFT(FileTime.fromType(FileTime.Type.ZERO_TIME));

        return outPacket;
    }

    public static OutPacket checkDuplicatedIDResult(String name, byte code) {
        var outPacket = new OutPacket(CHECK_DUPLICATED_ID_RESULT);

        outPacket.encodeString(name);
        outPacket.encodeByte(code);

        return outPacket;
    }

    public static OutPacket createNewCharacterResult(LoginType type, Char c) {
        var outPacket = new OutPacket(CREATE_NEW_CHARACTER_RESULT);

        outPacket.encodeByte(type.getValue());
        if (type == LoginType.Success) {
            c.getAvatarData().encode(outPacket);
        }

        return outPacket;
    }

    public static OutPacket sendAuthResponse(int response) {
        var outPacket = new OutPacket(PRIVATE_SERVER_PACKET_SEND);

        outPacket.encodeInt(response);

        return outPacket;
    }

    public static OutPacket selectCharacterResult(LoginType loginType, byte errorCode, int port, int characterId) {
        var outPacket = new OutPacket(SELECT_CHARACTER_RESULT);

        outPacket.encodeByte(loginType.getValue());
        outPacket.encodeByte(errorCode);

        if (loginType == LoginType.Success) {
            byte[] server = new byte[]{127, 0, 0, ((byte) 1)};
            outPacket.encodeArr(server);
            outPacket.encodeShort(port);

            byte[] chatServer = new byte[]{127, 0, 0, ((byte) 1)};
            // chat stuff
            outPacket.encodeArr(chatServer);
            outPacket.encodeShort(ServerConstants.CHAT_PORT);

            outPacket.encodeInt(characterId);
            outPacket.encodeByte(0);
            outPacket.encodeInt(0); // ulArgument
            outPacket.encodeByte(0);
            outPacket.encodeInt(0);
            outPacket.encodeInt(0);
            outPacket.encodeByte(0);
        }

        return outPacket;
    }

    public static OutPacket sendDeleteCharacterResult(int charId, LoginType loginType) {
        var outPacket = new OutPacket(DELETE_CHARACTER_RESULT);

        outPacket.encodeInt(charId);
        outPacket.encodeByte(loginType.getValue());


        return outPacket;
    }

    public static OutPacket sendRecommendWorldMessage(int nWorldID, String nMsg) {
        var oPacket = new OutPacket(RECOMMENDED_WORLD_MESSAGE);
        oPacket.encodeByte(1);
        oPacket.encodeInt(nWorldID);
        oPacket.encodeString(nMsg);
        return oPacket;
    }

    public static OutPacket changePicResponse(LoginType result) {
        var outPacket = new OutPacket(CHANGE_SPW_RESULT);
        outPacket.encodeByte(result.getValue());
        return outPacket;
    }

    public static OutPacket secondPasswordWindows(User user) {
        var outPacket = new OutPacket(CHECK_SPW_EXIST_RESPONSE);
        outPacket.encodeByte(3/*user.getPicStatus().getVal()*/); // bLoginOpt
        outPacket.encodeByte(false); // bQuerySSNOnCreateNewCharacter
        return outPacket;
    }
}
