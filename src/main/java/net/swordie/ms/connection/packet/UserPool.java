package net.swordie.ms.connection.packet;

import net.swordie.ms.client.character.Char;
import net.swordie.ms.client.character.CharacterStat;
import net.swordie.ms.client.character.avatar.AvatarLook;
import net.swordie.ms.client.character.skills.temp.CharacterTemporaryStat;
import net.swordie.ms.client.character.skills.temp.TemporaryStatManager;
import net.swordie.ms.client.guild.Guild;
import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.constants.JobConstants;
import net.swordie.ms.enums.TSIndex;

import java.util.stream.IntStream;

import static net.swordie.ms.handlers.header.OutHeader.USER_ENTER_FIELD;
import static net.swordie.ms.handlers.header.OutHeader.USER_LEAVE_FIELD;

/**
 * Created on 3/18/2018.
 */
public class UserPool {

    public static OutPacket userEnterField(Char chr) {
        CharacterStat cs = chr.getAvatarData().getCharacterStat();
        AvatarLook al = chr.getAvatarData().getAvatarLook();
        TemporaryStatManager tsm = chr.getTemporaryStatManager();

        var outPacket = new OutPacket(USER_ENTER_FIELD);

        outPacket.encodeInt(chr.getId());

        outPacket.encodeByte(chr.getLevel());
        outPacket.encodeString(chr.getName());
        outPacket.encodeString(""); // parent name, deprecated
        if (chr.getGuild() != null) {
            chr.getGuild().encodeForRemote(outPacket);
        } else {
            Guild.defaultEncodeForRemote(outPacket);//8
        }
        outPacket.encodeByte(cs.getGender());
        outPacket.encodeInt(cs.getPop());
//        outPacket.encodeInt(10); // nFarmLevel
        outPacket.encodeInt(0); // nNameTagMark

        tsm.encodeForRemote(outPacket, tsm.getCurrentStats());

        outPacket.encodeShort(chr.getJob());
        outPacket.encodeShort(cs.getSubJob());
        outPacket.encodeInt(chr.getTotalChuc());
        al.encode(outPacket);
        if (JobConstants.isZero(chr.getJob())) {
            chr.getAvatarData().getZeroAvatarLook().encode(outPacket);
        }

        int v7 = 2;
        do {
            outPacket.encodeInt(0);
            while (true) {
                int res = 255;
                outPacket.encodeByte(res);
                if (res == 255) break;
                outPacket.encodeInt(0);
            }
            v7 += 36;
        } while (v7 < 74);

        outPacket.encodeInt(chr.getDriverID());
        outPacket.encodeInt(chr.getPassengerID()); // dwPassenserID
        // new 176: sub_191E2D0
        outPacket.encodeInt(0);
        outPacket.encodeInt(0);
        int size = 0;
        outPacket.encodeInt(size);
        IntStream.range(0, size).forEach(i -> {
            outPacket.encodeInt(0);
            outPacket.encodeInt(0);
        });
        // end sub_191E2D0
        outPacket.encodeInt(chr.getChocoCount());
        outPacket.encodeInt(chr.getActiveEffectItemID());
        outPacket.encodeInt(chr.getMonkeyEffectItemID());
        outPacket.encodeInt(chr.getActiveNickItemID());
        outPacket.encodeInt(chr.getDamageSkin().getDamageSkinID());
        outPacket.encodeInt(0); // ptPos.x?
        outPacket.encodeInt(al.getDemonWingID());
        outPacket.encodeInt(al.getKaiserWingID());
        outPacket.encodeInt(al.getKaiserTailID());
        outPacket.encodeInt(chr.getCompletedSetItemID());

        outPacket.encodeShort(chr.getFieldSeatID());
        outPacket.encodeStr("");
        outPacket.encodeStr("");
        outPacket.encodeShort(-1);
        outPacket.encodeShort(-1);
        outPacket.encodeByte(1);// PVP開關

        outPacket.encodeInt(chr.getPortableChairID());

        var hasPortableChairMsg = chr.getPortableChairMsg() != null;
        outPacket.encodeInt(hasPortableChairMsg ? 1 : 0); // why is this an int
        if (hasPortableChairMsg) {
            outPacket.encodeString(chr.getPortableChairMsg());
        }

        var towerIDSize = 0;
        outPacket.encodeInt(towerIDSize);
        IntStream.range(0, towerIDSize).map(i -> 0).forEach(outPacket::encodeInt);        // towerChairID

        outPacket.encodeInt(0); // some other position? new
        outPacket.encodeInt(0); // some other position? new
        outPacket.encodePosition(chr.getPosition());
        outPacket.encodeByte(chr.getMoveAction());
        outPacket.encodeShort(chr.getFoothold());
//        outPacket.encodeByte(0); // Item/Install/0301.img/%08d/info/customChair timeChairInfo

        chr.getPets().stream().filter(pet -> pet.getId() != 0).forEach(pet -> {
            outPacket.encodeByte(1);
            outPacket.encodeInt(pet.getIdx());
            pet.encode(outPacket);
        });
        outPacket.encodeByte(0); // indicating that pets are no longer being encoded

        outPacket.encodeByte(0); // 花狐 (haku)

//        outPacket.encodeByte(1); // new, having a 0 will 38 (familiar?)
//        outPacket.encodeByte(chr.getMechanicHue());

        outPacket.encodeInt(chr.getTamingMobLevel());
        outPacket.encodeInt(chr.getTamingMobExp());
        outPacket.encodeInt(chr.getTamingMobFatigue());

        byte miniRoomType = chr.getMiniRoom() != null ? chr.getMiniRoom().getType() : 0;
        outPacket.encodeByte(miniRoomType);
        if (miniRoomType > 0) {
            chr.getMiniRoom().encode(outPacket);
        }

        outPacket.encodeByte(chr.getADBoardRemoteMsg() != null);
        if (chr.getADBoardRemoteMsg() != null) {
            outPacket.encodeString(chr.getADBoardRemoteMsg());
        }

        outPacket.encodeByte(chr.isInCouple());
        if (chr.isInCouple()) {
            chr.getCouple().encodeForRemote(outPacket);//4 buffer16 4
        }
        outPacket.encodeByte(chr.hasFriendshipItem());
        if (chr.hasFriendshipItem()) {
            chr.getFriendshipRingRecord().encode(outPacket);//4 buffer16 4
        }
        outPacket.encodeByte(chr.isMarried());
        if (chr.isMarried()) {
            chr.getMarriageRecord().encodeForRemote(outPacket);// 4 4 4
        }

        var unk = false;
        outPacket.encodeByte(unk);
        if (unk) {
            outPacket.encodeInt(size);
            IntStream.range(0, size).map(o -> 0).forEach(outPacket::encodeInt);
        }

        var flag = 0;
        outPacket.encodeByte(flag); // some flag that shows uninteresting things for now
        if ((flag & 8) != 0) {
            outPacket.encodeInt(0);
        } else if ((flag & 10) != 0) {
            outPacket.encodeInt(0);
        } else if ((flag & 20) != 0) {
            outPacket.encodeInt(0);
        }

        outPacket.encodeInt(chr.getEvanDragonGlide());

        if (JobConstants.isKaiser(chr.getJob())) {
            outPacket.encodeInt(chr.getKaiserMorphRotateHueExtern());
            outPacket.encodeInt(chr.getKaiserMorphPrimiumBlack());
            outPacket.encodeByte(chr.getKaiserMorphRotateHueInnner());
        }

        outPacket.encodeInt(chr.getMakingMeisterSkillEff());

//        chr.getFarmUserInfo().encode(outPacket);

        IntStream.range(0, 5).map(i -> -1).forEach(outPacket::encodeByte);// activeEventNameTag

        outPacket.encodeInt(chr.getCustomizeEffect());
        if (chr.getCustomizeEffect() > 0) {
            outPacket.encodeString(chr.getCustomizeEffectMsg());
        }

        outPacket.encodeByte(chr.getSoulEffect());

        if (tsm.hasStat(CharacterTemporaryStat.RideVehicle)) {
            var vehicleID = tsm.getTSBByTSIndex(TSIndex.RideVehicle).getNOption();
            if (vehicleID == 1932249) { // is_mix_vehicle
                outPacket.encodeInt(size); // ???
                IntStream.range(0, size).forEach(i -> outPacket.encodeInt(0));
            }
        }

        /*
         Flashfire (12101025) info
         not really interested in encoding this
         structure is:
         if(bool)
            if(bool)
                slv = int
                notused = int
                x = short
                y = short
         */
        outPacket.encodeByte(0);

        outPacket.encodeByte(0); // CUser::StarPlanetRank::Decode

        // CUser::DecodeStarPlanetTrendShopLook not interesting, will break REMOTE_AVATAR_MODIFIED if 1st int is != 0
        outPacket.encodeInt(0);
//        outPacket.encodeInt(0);
        // ~CUser::DecodeStarPlanetTrendShopLook

        outPacket.encodeInt(0); // CUser::DecodeTextEquipInfo

        chr.getFreezeHotEventInfo().encode(outPacket);//CUser::DecodeFreezeHotEventInfo
        outPacket.encodeInt(chr.getEventBestFriendAID());//CUser::DecodeEventBestFriendInfo

        outPacket.encodeByte(tsm.hasStat(CharacterTemporaryStat.KinesisPsychicEnergeShield));

        outPacket.encodeByte(chr.isBeastFormWingOn());

        outPacket.encodeInt(chr.getMesoChairCount());
        // end kmst

        outPacket.encodeInt(0);
        outPacket.encodeInt(0);

        //sub
        size = 0;
        outPacket.encodeInt(size);
        IntStream.range(0, size).forEach(i -> {
            outPacket.encodeInt(0);
            outPacket.encodeInt(0);
        });
        //~sub

        //sub
        size = 0;
        outPacket.encodeInt(size);
        IntStream.range(0, size).forEach(outPacket::encodeInt);
        //~sub

        //sub
        int familiar_size = 0;
        outPacket.encodeInt(familiar_size);
        //~sub

        outPacket.encodeInt(0);
        // end

        if (chr.getFieldID() / 100000 == 9600) {
            outPacket.encodeByte(1);
            if (chr.getFieldID() / 10000 == 96002) {
                outPacket.encodeByte(1);
            }
        } else if (chr.getFieldID() / 100000 == 9800 || chr.getFieldID() == 109080000) {
            outPacket.encodeByte(1);
        }

/*        if (chr.getMapId() / 100000 == 9600) {
            mplew.write(chr.inPVP() ? Integer.valueOf(chr.getEventInstance().getProperty("type")) : 0);
            if (chr.inPVP() && Integer.valueOf(chr.getEventInstance().getProperty("type")) > 0) {
                mplew.write(chr.getTeam() + 1);
            }
        } else if (chr.getMapId() / 100000 == 9800 || chr.getMapId() == 109080000) {
            mplew.write(chr.getCarnivalParty() != null ? chr.getCarnivalParty().getTeam() : 0);
        }*/

        return outPacket;
    }

    public static OutPacket userLeaveField(Char chr) {
        var outPacket = new OutPacket(USER_LEAVE_FIELD);

        outPacket.encodeInt(chr.getId());

        return outPacket;
    }
}
