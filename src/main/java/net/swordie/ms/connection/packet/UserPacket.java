package net.swordie.ms.connection.packet;

import net.swordie.ms.client.character.Char;
import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.enums.ChatUserType;
import net.swordie.ms.handlers.PsychicLock;
import net.swordie.ms.life.Summon;
import net.swordie.ms.life.mob.Mob;
import net.swordie.ms.loaders.containerclasses.MobSkillInfo;
import net.swordie.ms.util.Position;

import java.util.List;

import static net.swordie.ms.handlers.header.OutHeader.*;

/**
 * Created on 2/3/2018.
 */
public class UserPacket {

    public static OutPacket chat(int charID, ChatUserType type, String msg, boolean onlyBalloon, int idk, int worldID) {
        var outPacket = new OutPacket(CHAT);

        outPacket.encodeInt(charID);
        outPacket.encodeByte(type.getVal());
        outPacket.encodeString(msg);
        outPacket.encodeByte(onlyBalloon);
        outPacket.encodeByte(idk);
        outPacket.encodeByte(worldID);

        return outPacket;
    }

    public static OutPacket setDamageSkin(Char chr) {
        var outPacket = new OutPacket(SET_DAMAGE_SKIN);

        outPacket.encodeInt(chr.getId());
        outPacket.encodeInt(chr.getDamageSkin().getDamageSkinID());

        return outPacket;
    }

    public static OutPacket setPremiumDamageSkin(Char chr) {
        var outPacket = new OutPacket(SET_PREMIUM_DAMAGE_SKIN);

        outPacket.encodeInt(chr.getId());
        outPacket.encodeInt(chr.getPremiumDamageSkin().getDamageSkinID());

        return outPacket;
    }

    public static OutPacket showItemSkillSocketUpgradeEffect(int charID, boolean success) {
        var outPacket = new OutPacket(SHOW_ITEM_SKILL_SOCKET_UPGRADE_EFFECT);

        outPacket.encodeInt(charID);
        outPacket.encodeByte(success);

        return outPacket;
    }

    public static OutPacket showItemSkillOptionUpgradeEffect(int charID, boolean success, boolean boom) {
        var outPacket = new OutPacket(SHOW_ITEM_SKILL_OPTION_UPGRADE_EFFECT);

        outPacket.encodeInt(charID);
        outPacket.encodeByte(success);
        outPacket.encodeByte(boom);

        return outPacket;
    }

    public static OutPacket SetSoulEffect(int charID, boolean set) {
        var outPacket = new OutPacket(SET_SOUL_EFFECT);

        outPacket.encodeInt(charID);
        outPacket.encodeByte(set);

        return outPacket;
    }

    public static OutPacket setStigmaEffect(Char chr) {
        var outPacket = new OutPacket(STIGMA_EFFECT);

        outPacket.encodeInt(chr.getId());
        outPacket.encodeByte(true);

        return outPacket;
    }

    public static OutPacket setGachaponEffect(Char chr) {
        var outPacket = new OutPacket(GACHAPON_EFFECT);

        outPacket.encodeInt(chr.getId());

        return outPacket;
    }
    
    public static OutPacket scriptProgressMessage(String string) {
        var outPacket = new OutPacket(SCRIPT_PROGRESS_MESSAGE);

        outPacket.encodeString(string);

        return outPacket;
    }

    public static OutPacket progressMessageFont(int fontNameType, int fontSize, int fontColorType, int fadeOutDelay, String message) {
        var outPacket = new OutPacket(PROGRESS_MESSAGE_FONT);
        
        outPacket.encodeInt(fontNameType);
        outPacket.encodeInt(fontSize);
        outPacket.encodeInt(fontColorType);
        outPacket.encodeInt(fadeOutDelay);
        outPacket.encodeString(message);
 
        return outPacket;
    }
    
    public static OutPacket effect(Effect effect) {
        var outPacket = new OutPacket(EFFECT);

        effect.encode(outPacket);

        return outPacket;
    }

    public static OutPacket showItemMemorialEffect(int charID, boolean success, int itemID) {
        var outPacket = new OutPacket(SHOW_ITEM_MEMORIAL_EFFECT);

        outPacket.encodeInt(charID);
        outPacket.encodeByte(success);
        outPacket.encodeInt(itemID);

        return outPacket;
    }

    public static OutPacket createPsychicLock(int charID, PsychicLock pl) {
        var outPacket = new OutPacket(CREATE_PSYCHIC_LOCK);

        outPacket.encodeInt(charID);
        outPacket.encodeByte(pl.success);
        pl.encode(outPacket);


        return outPacket;
    }

    public static OutPacket followCharacter(int driverChrId, boolean transferField, Position position) {
        var outPacket = new OutPacket(FOLLOW_CHARACTER);

        outPacket.encodeInt(driverChrId);
        if (driverChrId < 0) {
            outPacket.encodeByte(transferField);
            if (transferField) {
                outPacket.encodePositionInt(position);
            }
        }

        return outPacket;
    }

    public static OutPacket userHitByCounter(int charID, int damage) {
        var outPacket = new OutPacket(USER_HIT_BY_COUNTER);

        outPacket.encodeInt(charID);
        outPacket.encodeInt(damage);

        return outPacket;
    }

    public static OutPacket tossedByMobSkill(int charId, Mob mob, MobSkillInfo msi, int impact) {
        var outPacket = new OutPacket(TOSSED_BY_MOB_SKILL);

        outPacket.encodeInt(charId);

        outPacket.encodeInt(mob.getObjectId());
        outPacket.encodeInt(msi.getId());
        outPacket.encodeInt(msi.getLevel());
        outPacket.encodeInt(impact);

        return outPacket;
    }

    public static OutPacket teslaTriangle(List<Summon> rockNshockLifes, int chrId) {
        var outPacket = new OutPacket(TESLA_TRIANGLE);


        outPacket.encodeInt(chrId);
        for (int i = 0; i < 3 ; i++) {
            outPacket.encodeInt(rockNshockLifes.get(i).getObjectId());
        }
        return outPacket;
    }

    public static OutPacket checkUpgradeItemResult(int index, boolean show) {
        var outPacket = new OutPacket(CHECK_UPGRADE_ITEM_RESULT);

        outPacket.encodeByte(show);
        outPacket.encodeString(""); //does nothing and not named in kmst idb.
        outPacket.encodeInt(index);
        return outPacket;
    }

    public static OutPacket gatherResult(int chrId, boolean success) {
        var outPacket = new OutPacket(GATHER_RESULT);
        outPacket.encodeInt(chrId);

        outPacket.encodeByte(success);

        return outPacket;
    }
}
