package net.swordie.ms.connection.packet;

import net.swordie.ms.client.alliance.AllianceResult;
import net.swordie.ms.client.character.*;
import net.swordie.ms.client.character.cards.CharacterCard;
import net.swordie.ms.client.character.info.ExpIncreaseInfo;
import net.swordie.ms.client.character.info.ZeroInfo;
import net.swordie.ms.client.character.items.Equip;
import net.swordie.ms.client.character.items.Item;
import net.swordie.ms.client.character.items.MemorialCubeInfo;
import net.swordie.ms.client.character.potential.CharacterPotential;
import net.swordie.ms.client.character.quest.Quest;
import net.swordie.ms.client.character.skills.Skill;
import net.swordie.ms.client.character.skills.TownPortal;
import net.swordie.ms.client.character.skills.temp.TemporaryStatManager;
import net.swordie.ms.client.friend.Friend;
import net.swordie.ms.client.friend.result.FriendResult;
import net.swordie.ms.client.guild.Guild;
import net.swordie.ms.client.guild.bbs.GuildBBSPacket;
import net.swordie.ms.client.guild.result.GuildResult;
import net.swordie.ms.client.jobs.resistance.WildHunterInfo;
import net.swordie.ms.client.party.Party;
import net.swordie.ms.client.party.PartyResult;
import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.enums.*;
import net.swordie.ms.util.AntiMacro;
import net.swordie.ms.util.FileTime;
import net.swordie.ms.util.Position;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.stream.IntStream;

import static net.swordie.ms.enums.InvType.EQUIPPED;
import static net.swordie.ms.enums.MessageType.*;
import static net.swordie.ms.handlers.header.OutHeader.*;

/**
 * Created on 12/22/2017.
 */
public class WvsContext {
    private static final org.apache.logging.log4j.Logger log = LogManager.getRootLogger();

    public static void dispose(Char chr) {
        chr.dispose();
    }

    public static OutPacket exclRequest() {
        return new OutPacket(EXCL_REQUEST);
    }

    public static OutPacket statChanged(Map<Stat, Object> stats) {
        return statChanged(stats, false, (byte) -1, (byte) 0, (byte) 0, (byte) 0, false, 0, 0);
    }

    public static OutPacket statChanged(Map<Stat, Object> stats, boolean exclRequestSent, byte mixBaseHairColor,
                                        byte mixAddHairColor, byte mixHairBaseProb, byte charmOld, boolean updateCovery,
                                        int hpRecovery, int mpRecovery) {
        var outPacket = new OutPacket(STAT_CHANGED);

        outPacket.encodeByte(exclRequestSent);

        // GW_CharacterStat::DecodeChangeStat
        int mask = stats.keySet().stream().mapToInt(Stat::getVal).reduce(0, (a, b) -> a | b);
        outPacket.encodeLong(mask);

        Comparator statComper = Comparator.comparingInt(o -> ((Stat) o).getVal());
        TreeMap<Stat, Object> sortedStats = new TreeMap<>(statComper);
        sortedStats.putAll(stats);
        sortedStats.entrySet().forEach(entry -> {
            Stat stat = entry.getKey();
            Object value = entry.getValue();
            switch (stat) {
                case skin, level, fatigue -> outPacket.encodeByte((Byte) value);
                case face, hair, hp, mhp, mp, mmp, pop, charismaEXP, insightEXP, willEXP, craftEXP, senseEXP, charmEXP, eventPoints -> {
                    outPacket.encodeInt((Integer) value);
                }
                case str, dex, inte, luk, ap -> outPacket.encodeShort((Short) value);
                case sp -> {
                    if (value instanceof ExtendSP) {
                        ((ExtendSP) value).encode(outPacket);
                    } else {
                        outPacket.encodeShort((Short) value);
                    }
                }
                case exp, money -> outPacket.encodeLong((Long) value);
                case dayLimit -> ((NonCombatStatDayLimit) value).encode(outPacket);
                case albaActivity -> {//TODO
                }
                case characterCard -> ((CharacterCard) value).encode(outPacket);
                case pvp1, pvp2 -> {//TODO
                }
                case subJob -> {
                    outPacket.encodeShort((Short) value);
                    outPacket.encodeShort(0);
                }
            }
        });

        outPacket.encodeByte(mixBaseHairColor);
        outPacket.encodeByte(mixAddHairColor);
        outPacket.encodeByte(mixHairBaseProb);
        outPacket.encodeByte(charmOld > 0);
        if (charmOld > 0) {
            outPacket.encodeByte(charmOld);
        }
        outPacket.encodeByte(updateCovery);
        if (updateCovery) {
            outPacket.encodeInt(hpRecovery);
            outPacket.encodeInt(mpRecovery);
        }
        return outPacket;
    }

    public static OutPacket inventoryOperation(boolean exclRequestSent, boolean notRemoveAddInfo, InventoryOperation type, short oldPos, short newPos,
                                               int bagPos, Item item) {
        // logic like this in packets :(
        InvType invType = item.getInvType();
        if ((oldPos > 0 && newPos < 0 && invType == EQUIPPED) || (invType == EQUIPPED && oldPos < 0)) {
            invType = InvType.EQUIP;
        }

        var outPacket = new OutPacket(INVENTORY_OPERATION);

        outPacket.encodeByte(exclRequestSent);
        outPacket.encodeByte(1); // size
        outPacket.encodeByte(notRemoveAddInfo);

        outPacket.encodeByte(type.getVal());
        outPacket.encodeByte(invType.getVal());
        outPacket.encodeShort(oldPos);
        switch (type) {
            case Add -> item.encode(outPacket);
            case UpdateQuantity -> outPacket.encodeShort(item.getQuantity());
            case Move -> {
                outPacket.encodeShort(newPos);
                if (invType == InvType.EQUIP && (oldPos < 0 || newPos < 0)) {
                    outPacket.encodeByte(item.getCashItemSerialNumber() > 0);
                }
            }
            case Remove -> outPacket.encodeByte(0);
            case ItemExp -> outPacket.encodeLong(((Equip) item).getExp());
            case UpdateBagPos -> outPacket.encodeInt(bagPos);
            case UpdateBagQuantity -> outPacket.encodeShort(newPos);
            case BagNewItem -> item.encode(outPacket);
            case BagRemoveSlot -> {
            }
        }
        return outPacket;
    }

    public static OutPacket updateEventNameTag(int[] tags) {
        var outPacket = new OutPacket(EVENT_NAME_TAG);

        IntStream.range(0, 5).forEach(i -> {
            outPacket.encodeString("");
            if (i >= tags.length) {
                outPacket.encodeByte(-1);
            } else {
                outPacket.encodeByte(tags[i]);
            }
        });

        return outPacket;
    }

    public static OutPacket changeSkillRecordResult(Skill skill) {
        List<Skill> skills = new ArrayList<>();
        skills.add(skill);
        return changeSkillRecordResult(skills, true, false, false, false);
    }

    public static OutPacket changeSkillRecordResult(List<Skill> skills, boolean exclRequestSent, boolean showResult,
                                                    boolean removeLinkSkill, boolean sn) {
        var outPacket = new OutPacket(CHANGE_SKILL_RECORD_RESULT);

        outPacket.encodeByte(exclRequestSent);
        outPacket.encodeByte(showResult);
        outPacket.encodeByte(removeLinkSkill);
        outPacket.encodeShort(skills.size());
        skills.forEach(skill -> {
            outPacket.encodeInt(skill.getSkillId());
            outPacket.encodeInt(skill.getCurrentLevel());
            outPacket.encodeInt(skill.getMasterLevel());
            outPacket.encodeFT(FileTime.fromType(FileTime.Type.PLAIN_ZERO));
        });
        outPacket.encodeByte(sn);

        return outPacket;
    }

    public static OutPacket temporaryStatSet(TemporaryStatManager tsm) {
        var outPacket = new OutPacket(TEMPORARY_STAT_SET);

        boolean hasMovingAffectingStat = tsm.hasNewMovingEffectingStat(); // encoding flushes new stats
        tsm.encodeForLocal(outPacket);

//        outPacket.encodeInt(0); // can't find this is IDA, but crashes without. Not sure what it stands for
        outPacket.encodeShort(0);
        outPacket.encodeByte(0);
        outPacket.encodeByte(0);
        outPacket.encodeByte(0);
        if (hasMovingAffectingStat) {
            outPacket.encodeByte(0);
        }
        outPacket.encodeInt(0);

        outPacket.encodeArr(new byte[50]);//防38用

        return outPacket;
    }

    public static OutPacket temporaryStatReset(TemporaryStatManager temporaryStatManager, boolean demount, boolean isMigrate) {
        var outPacket = new OutPacket(TEMPORARY_STAT_RESET);

        Arrays.stream(temporaryStatManager.getRemovedMask()).forEach(outPacket::encodeInt);

//        temporaryStatManager.getRemovedStats().forEach((cts, option) -> outPacket.encodeInt(0));
        temporaryStatManager.encodeRemovedIndieTempStat(outPacket);

        if (temporaryStatManager.hasRemovedMovingEffectingStat()) {
            outPacket.encodeByte(0);
        }
        outPacket.encodeByte(0); // ?
        outPacket.encodeByte(demount);

        if (isMigrate) {
            temporaryStatManager.getToBroadcastAfterMigrate().add(outPacket);
        }

        temporaryStatManager.getRemovedStats().clear();
        return outPacket;
    }

    public static OutPacket skillUseResult(boolean stillGoing) {
        var outPacket = new OutPacket(SKILL_USE_RESULT);
        // 2221011 - Frozen Breath
        outPacket.encodeByte(stillGoing);

        return outPacket;
    }

    public static OutPacket dropPickupMessage(int money, short internetCafeExtra, short smallChangeExtra) {
        return dropPickupMessage(money, (byte) 1, internetCafeExtra, smallChangeExtra, (short) 0);
    }

    public static OutPacket dropPickupMessage(Item item, short quantity) {
        return dropPickupMessage(item.getItemId(), (byte) 0, (short) 0, (short) 0, quantity);
    }

    public static OutPacket dropPickupMessage(int i, byte type, short internetCafeExtra, short smallChangeExtra, short quantity) {
        var outPacket = new OutPacket(MESSAGE);

        outPacket.encodeByte(DROP_PICKUP_MESSAGE.getVal());
        outPacket.encodeByte(type);
        // also error (?) codes -2, ,-3, -4, -5, <default>
        switch (type) {
            case 1 -> { // Mesos
                outPacket.encodeByte(false); // boolean: portion was lost after falling to the ground
                outPacket.encodeInt(i); // Mesos
                outPacket.encodeShort(internetCafeExtra); // Internet cafe
                outPacket.encodeShort(smallChangeExtra); // Spotting small change
            }
            case 0 -> { // item
                outPacket.encodeInt(i);
                outPacket.encodeInt(quantity); // ?
            }
            case 2 -> // ?
                    outPacket.encodeInt(100);
        }

        return outPacket;
    }

    public static OutPacket questRecordMessageAddValidCheck(int qrKey, byte state) {
        var outPacket = new OutPacket(MESSAGE);

        outPacket.encodeByte(QUEST_RECORD_MESSAGE_ADD_VALID_CHECK.getVal());
        outPacket.encodeInt(qrKey);
        outPacket.encodeByte(true);
        outPacket.encodeByte(state);
        // TODO probably missing something here

        return outPacket;
    }

    public static OutPacket questRecordMessage(Quest quest) {
        var outPacket = new OutPacket(MESSAGE);

        outPacket.encodeByte(QUEST_RECORD_MESSAGE.getVal());
        outPacket.encodeInt(quest.getQRKey());
        QuestStatus state = quest.getStatus();
        outPacket.encodeByte(state.getVal());

        switch (state) {
            case NotStarted -> outPacket.encodeByte(0); // If quest is completed, but should never be true?
            case Started -> outPacket.encodeString(quest.getQRValue());
            case Completed -> outPacket.encodeFT(quest.getCompletedTime());
        }

        return outPacket;
    }

    public static OutPacket questRecordExMessage(Quest quest) {
        var outPacket = new OutPacket(MESSAGE);

        outPacket.encodeByte(QUEST_RECORD_EX_MESSAGE.getVal());
        outPacket.encodeInt(quest.getQRKey());
        outPacket.encodeString(quest.getQRValue());

        return outPacket;
    }

    public static OutPacket incExpMessage(ExpIncreaseInfo eii) {
        var outPacket = new OutPacket(MESSAGE);

        outPacket.encodeByte(INC_EXP_MESSAGE.getVal());
        eii.encode(outPacket);

        return outPacket;
    }

    public static OutPacket incSpMessage(short job, byte amount) {
        var outPacket = new OutPacket(MESSAGE);

        outPacket.encodeByte(INC_SP_MESSAGE.getVal());
        outPacket.encodeShort(job);
        outPacket.encodeByte(amount);

        return outPacket;
    }

    public static OutPacket incMoneyMessage(int amount) {
        var outPacket = new OutPacket(MESSAGE);

        outPacket.encodeByte(INC_MONEY_MESSAGE.getVal());
        outPacket.encodeInt(amount);
        outPacket.encodeInt(amount > 0 ? 1 : -1);

        return outPacket;
    }

    /**
     * Returns a net.swordie.ms.connection.packet for messages with the following {@link MessageType}:<br>
     * GENERAL_ITEM_EXPIRE_MESSAGE<br>
     * ITEM_PROTECT_EXPIRE_MESSAGE<br>
     * ITEM_ABILITY_TIME_LIMITED_EXPIRE_MESSAGE<br>
     * SKILL_EXPIRE_MESSAGE
     *
     * @param mt    The message type.
     * @param items The list of ints that should be encoded.
     * @return The message OutPacket.
     */
    public static OutPacket message(MessageType mt, List<Integer> items) {
        var outPacket = new OutPacket(MESSAGE);

        outPacket.encodeByte(mt.getVal());
        switch (mt) {
            case GENERAL_ITEM_EXPIRE_MESSAGE:
            case ITEM_PROTECT_EXPIRE_MESSAGE:
            case ITEM_ABILITY_TIME_LIMITED_EXPIRE_MESSAGE:
            case SKILL_EXPIRE_MESSAGE:
                outPacket.encodeByte(items.size());
                items.forEach(outPacket::encodeInt);
                break;
        }

        return outPacket;
    }

    public static OutPacket itemExpireReplaceMessage(List<String> strings) {
        var outPacket = new OutPacket(MESSAGE);

        outPacket.encodeByte(ITEM_EXPIRE_REPLACE_MESSAGE.getVal());
        outPacket.encodeByte(strings.size());
        strings.forEach(outPacket::encodeString);

        return outPacket;
    }

    public static OutPacket incNonCombatStatEXPMessage(Stat trait, int amount) {
        var outPacket = new OutPacket(MESSAGE);

        outPacket.encodeByte(INC_NON_COMBAT_STAT_EXP_MESSAGE.getVal());
        long mask = 0;
        mask |= trait.getVal();
        outPacket.encodeLong(mask);
        outPacket.encodeInt(amount);

        return outPacket;
    }

    /**
     * Returns a net.swordie.ms.connection.packet for messages with the following {@link MessageType}:<br>
     * int: <br>
     * CASH_ITEM_EXPIRE_MESSAGE<br>
     * INC_POP_MESSAGE<br>
     * INC_GP_MESSAGE<br>
     * GIVE_BUFF_MESSAGE<br><br>
     * int + byte: <br>
     * INC_COMMITMENT_MESSAGE<br><br>
     * String: <br>
     * SYSTEM_MESSAGE<br><br>
     * int + String: <br>
     * QUEST_RECORD_EX_MESSAGE<br>
     * WORLD_SHARE_RECORD_MESSAGE<br>
     *
     * @param mt     The message type.
     * @param i      The integer to encode.
     * @param string The String to encode.
     * @param type   The type (byte) to encode.
     * @return The message OutPacket.
     */
    public static OutPacket message(MessageType mt, int i, String string, byte type) {
        var outPacket = new OutPacket(MESSAGE);

        outPacket.encodeByte(mt.getVal());
        switch (mt) {
            case CASH_ITEM_EXPIRE_MESSAGE, INC_POP_MESSAGE, INC_GP_MESSAGE, GIVE_BUFF_MESSAGE -> {
                outPacket.encodeInt(i);
            }
            case INC_COMMITMENT_MESSAGE -> {
                outPacket.encodeInt(i);
                outPacket.encodeByte(i < 0 ? 1 : i == 0 ? 2 : 0); // gained = 0, lost = 1, cap = 2
            }
            case SYSTEM_MESSAGE -> outPacket.encodeString(string);
            case QUEST_RECORD_EX_MESSAGE, WORLD_SHARE_RECORD_MESSAGE, COLLECTION_RECORD_MESSAGE -> {
                outPacket.encodeInt(i);
                outPacket.encodeString(string);
            }
            case INC_HARDCORE_EXP_MESSAGE -> {
                outPacket.encodeInt(i); //You have gained x EXP
                outPacket.encodeInt(i); //Field Bonus Exp
            }
            case BARRIER_EFFECT_IGNORE_MESSAGE -> outPacket.encodeByte(type); //protection/shield scroll pop-up Message
        }

        return outPacket;
    }

    public static OutPacket flipTheCoinEnabled(byte enabled) {
        var outPacket = new OutPacket(SET_FLIP_THE_COIN_ENABLED);

        outPacket.encodeByte(enabled);

        return outPacket;
    }

    public static OutPacket modComboResponse(int combo) {
        var outPacket = new OutPacket(MOD_COMBO_RESPONSE);

        outPacket.encodeInt(combo);

        return outPacket;
    }

    public static OutPacket wildHunterInfo(WildHunterInfo whi) {
        var outPacket = new OutPacket(WILD_HUNTER_INFO);

        whi.encode(outPacket);

        return outPacket;
    }

    public static OutPacket zeroInfo(ZeroInfo currentInfo) {
        var outPacket = new OutPacket(ZERO_INFO);

        currentInfo.encode(outPacket);

        return outPacket;
    }

    public static OutPacket gatherItemResult(byte type) {
        var outPacket = new OutPacket(GATHER_ITEM_RESULT);

        outPacket.encodeByte(0); // doesn't get used
        outPacket.encodeByte(type);

        return outPacket;
    }

    public static OutPacket sortItemResult(byte type) {
        var outPacket = new OutPacket(SORT_ITEM_RESULT);

        outPacket.encodeByte(0); // doesn't get used
        outPacket.encodeByte(type);

        return outPacket;
    }

    public static OutPacket clearAnnouncedQuest() {
        return new OutPacket(CLEAR_ANNOUNCED_QUEST);
    }

    public static OutPacket partyResult(PartyResult pri) {
        var outPacket = new OutPacket(PARTY_RESULT);

        outPacket.encode(pri);

        return outPacket;
    }

    public static OutPacket partyMemberCandidateResult(Set<Char> chars) {
        var outPacket = new OutPacket(PARTY_MEMBER_CANDIDATE_RESULT);

        outPacket.encodeByte(chars.size());
        chars.forEach(chr -> {
            outPacket.encodeInt(chr.getId());
            outPacket.encodeString(chr.getName());
            outPacket.encodeShort(chr.getJob());
            outPacket.encodeShort(chr.getAvatarData().getCharacterStat().getSubJob());
            outPacket.encodeByte(chr.getLevel());
        });

        return outPacket;
    }

    public static OutPacket partyCandidateResult(Set<Party> parties) {
        var outPacket = new OutPacket(PARTY_CANDIDATE_RESULT);

        outPacket.encodeByte(parties.size());
        parties.forEach(party -> {
            Char leader = party.getPartyLeader().getChr();
            outPacket.encodeInt(party.getId());
            outPacket.encodeString(leader.getName());
            outPacket.encodeByte(party.getAvgLevel());
            outPacket.encodeByte(party.getMembers().size());
            outPacket.encodeString(party.getName());
            outPacket.encodeByte(party.getMembers().size());
            party.getMembers().forEach(pm -> {
                outPacket.encodeInt(pm.getCharID());
                outPacket.encodeString(pm.getCharName());
                outPacket.encodeShort(pm.getJob());
                outPacket.encodeShort(pm.getSubSob());
                outPacket.encodeByte(pm.getLevel());
                outPacket.encodeByte(pm.equals(party.getPartyLeader()));
            });
        });
        outPacket.encodeArr(new byte[40]);

        return outPacket;
    }

    public static OutPacket guildResult(GuildResult gri) {
        var outPacket = new OutPacket(GUILD_RESULT);

        gri.encode(outPacket);

        return outPacket;
    }

    public static OutPacket guildSearchResult(Collection<Guild> guilds) {
        var outPacket = new OutPacket(GUILD_SEARCH_RESULT);

        outPacket.encodeInt(guilds.size());
        guilds.forEach(g -> {
            outPacket.encodeInt(g.getId());
            outPacket.encodeInt(g.getLevel());
            outPacket.encodeString(g.getName());
            outPacket.encodeString(g.getGuildLeader().getName());
            outPacket.encodeInt(g.getMembers().size());
            outPacket.encodeInt(g.getAverageMemberLevel());
        });

        return outPacket;
    }


    public static OutPacket allianceResult(AllianceResult ar) {
        var outPacket = new OutPacket(ALLIANCE_RESULT);

        outPacket.encode(ar);

        return outPacket;
    }

    public static OutPacket guildBBSResult(GuildBBSPacket gbp) {
        var outPacket = new OutPacket(GUILD_BBS_RESULT);

        outPacket.encode(gbp);

        return outPacket;
    }

    public static OutPacket flameWizardFlareBlink(Char chr, Position newPosition, boolean used) {
        var outPacket = new OutPacket(FLAME_WIZARD_FLARE_BLINK);

        Position zero = new Position(0, 0);
        outPacket.encodeInt(chr.getId()); //chr?
        outPacket.encodeByte(used); //used?

        if (used) {

            //Blink - Clear + Teleport
            chr.write(FieldPacket.teleport(newPosition, chr));

        } else {

            //Blink - Set Position
            outPacket.encodeByte(used);
            outPacket.encodeShort(1);
            outPacket.encodePosition(newPosition); //2x encode Short (x/y)
            outPacket.encodePosition(zero); //2x encode Short (x/y)
        }
        return outPacket;
    }

    public static OutPacket friendResult(FriendResult friendResult) {
        var outPacket = new OutPacket(FRIEND_RESULT);

        outPacket.encodeByte(friendResult.getType().getVal());
        friendResult.encode(outPacket);

        return outPacket;
    }


    public static OutPacket loadAccountIDOfCharacterFriendResult(Set<Friend> friends) {
        var outPacket = new OutPacket(LOAD_ACCOUNT_ID_OF_CHARACTER_FRIEND_RESULT);

        outPacket.encodeInt(friends.size());
        friends.forEach(fr -> {
            outPacket.encodeInt(fr.getFriendID());
            outPacket.encodeInt(fr.getFriendAccountID());
        });

        return outPacket;
    }

    public static OutPacket macroSysDataInit(List<Macro> macros) {
        var outPacket = new OutPacket(MACRO_SYS_DATA_INIT);

        outPacket.encodeByte(macros.size());
        macros.forEach(macro -> macro.encode(outPacket));

        return outPacket;
    }

    public static OutPacket monsterBookSetCard(int id) {
//        var outPacket = new OutPacket(MONSTER_LIFE_INVITE_ITEM_RESULT);
        var outPacket = new OutPacket(MONSTER_BOOK_SET_CARD);

        outPacket.encodeByte(id > 0); // false -> already added msg
        if (id > 0) {
            outPacket.encodeInt(id);
            outPacket.encodeInt(1); // card count, but we're just going to stuck with 1.
        }

        return outPacket;
    }

    public static OutPacket characterPotentialReset(PotentialResetType prt, int arg) {
        var outPacket = new OutPacket(CHARACTER_POTENTIAL_RESET);

        outPacket.encodeByte(prt.ordinal());
        switch (prt) {
            case Pos:
                outPacket.encodeShort(arg);
                break;
            case Skill:
                outPacket.encodeInt(arg);
                break;
            case All:
                break;
        }
        return outPacket;
    }

    public static OutPacket characterPotentialSet(CharacterPotential cp) {
        return characterPotentialSet(true, true, cp.getKey(), cp.getSkillID(), cp.getSlv(), cp.getGrade(), true);
    }

    public static OutPacket characterPotentialSet(boolean exclRequest, boolean changed, short pos, int skillID,
                                                  short skillLevel, short grade, boolean updatePassive) {
        var outPacket = new OutPacket(CHARACTER_POTENTIAL_SET);

        outPacket.encodeByte(exclRequest);
        outPacket.encodeByte(changed);
        if (changed) {
            outPacket.encodeShort(pos);
            outPacket.encodeInt(skillID);
            outPacket.encodeShort(skillLevel);
            outPacket.encodeShort(grade);
            outPacket.encodeByte(updatePassive);
        }

        return outPacket;
    }

    public static OutPacket characterHonorExp(int exp) {
        var outPacket = new OutPacket(CHARACTER_HONOR_EXP);

        outPacket.encodeInt(exp);

        return outPacket;
    }

    public static OutPacket bridleMobCatchFail(int itemID, boolean elementRock) {
        var outPacket = new OutPacket(BRIDLE_MOB_CATCH_FAIL);

        outPacket.encodeByte(elementRock);//rock?
        outPacket.encodeInt(itemID);
        outPacket.encodeInt(0); //ignored

        return outPacket;
    }

    public static OutPacket cashPetPickUpOnOffResult(boolean changed, boolean on) {
        var outPacket = new OutPacket(CASHPET_PICK_UP_ON_OFF_RESULT);

        outPacket.encodeByte(on);
        outPacket.encodeByte(changed);

        return outPacket;
    }

    public static OutPacket setSonOfLinkedSkillResult(LinkedSkillResultType lsrt, int sonID, String sonName,
                                                      int originalSkillID, String existingParentName) {
        var outPacket = new OutPacket(SET_SON_OF_LINKED_SKILL_RESULT);

        outPacket.encodeInt(lsrt.getVal());
        outPacket.encodeInt(originalSkillID);
        switch (lsrt) {
            case SetSonOfLinkedSkillResult_Success -> {
                outPacket.encodeInt(sonID);
                outPacket.encodeString(sonName);
            }
            case SetSonOfLinkedSkillResult_Fail_ParentAlreadyExist -> {
                outPacket.encodeString(existingParentName);
                outPacket.encodeString(sonName);
            }
            case SetSonOfLinkedSkillResult_Fail_Unknown -> {
            }
            case SetSonOfLinkedSkillResult_Fail_MaxCount -> outPacket.encodeString(existingParentName);
            case SetSonOfLinkedSkillResult_Fail_DBRequestFail -> {
            }
        }

        return outPacket;
    }

    public static OutPacket memorialCubeResult(Equip equip, MemorialCubeInfo mci) {
        var outPacket = new OutPacket(MEMORIAL_CUBE_RESULT);

        outPacket.encodeLong(equip.getSerialNumber());
        mci.encode(outPacket);

        return outPacket;
    }

    public static OutPacket blackCubeResult(Equip equip, MemorialCubeInfo mci) {
        var outPacket = new OutPacket(BLACK_CUBE_RESULT);

        outPacket.encodeLong(equip.getSerialNumber());
        mci.encode(outPacket);
        outPacket.encodeInt(equip.getBagIndex());

        return outPacket;
    }

    public static OutPacket whiteCubeResult(Equip equip, MemorialCubeInfo mci) {
        var outPacket = new OutPacket(WHITE_ADDTIONAL_CUBE_RESULT);

        outPacket.encodeLong(equip.getSerialNumber());
        mci.encode(outPacket);
        outPacket.encodeInt(equip.getBagIndex());

        return outPacket;
    }

    public static OutPacket broadcastMsg(BroadcastMsg broadcastMsg) {
        var outPacket = new OutPacket(BROADCAST_MSG);

        broadcastMsg.encode(outPacket);

        return outPacket;
    }

    public static OutPacket setAvatarMegaphone(Char chr, int megaItemId, List<String> lineList, boolean whisperIcon) {
        var outPacket = new OutPacket(SET_AVATAR_MEGAPHONE);

        outPacket.encodeInt(megaItemId); // Avatar Megaphone Item ID
        outPacket.encodeString(chr.getName());

        lineList.forEach(outPacket::encodeString);

        outPacket.encodeInt(chr.getClient().getChannel() - 1);
        outPacket.encodeByte(whisperIcon);

        chr.getAvatarData().getAvatarLook().encode(outPacket); // encode AvatarLook

        return outPacket;
    }

    public static OutPacket receiveHyperStatSkillResetResult(int charID, boolean exclRequest, boolean success) {
        var outPacket = new OutPacket(RECEIVE_HYPER_STAT_SKILL_RESET_RESULT);

        outPacket.encodeByte(exclRequest);
        outPacket.encodeInt(charID);
        outPacket.encodeByte(success);

        return outPacket;
    }

    public static OutPacket mapTransferResult(MapTransferType mapTransferType, byte itemType, List<Integer> hyperrockfields) {
        var outPacket = new OutPacket(MAP_TRANSFER_RESULT);

        outPacket.encodeByte(mapTransferType.getVal()); // Map Transfer Type
        outPacket.encodeByte(itemType); // Item Type (5 = Cash)
        if (mapTransferType == MapTransferType.DeleteListSend || mapTransferType == MapTransferType.RegisterListSend) {
            // Target Field ID
            hyperrockfields.forEach(outPacket::encodeInt);
        }

        return outPacket;
    }

    public static OutPacket monsterCollectionResult(MonsterCollectionResultType mcrt, InvType invType, int fullSlots) {
        var outPacket = new OutPacket(MONSTER_COLLECTION_RESULT);

        outPacket.encodeInt(mcrt.ordinal());
        if (invType != null) {
            outPacket.encodeInt(invType.getVal());
        } else {
            outPacket.encodeInt(0);
        }
        outPacket.encodeInt(fullSlots);

        return outPacket;
    }

    public static OutPacket weatherEffectNotice(WeatherEffNoticeType type, String text, int duration) {
        var outPacket = new OutPacket(WEATHER_EFFECT_NOTICE);

        outPacket.encodeString(text); // Text
        outPacket.encodeInt(type.getVal()); // Weather Notice Type
        outPacket.encodeInt(duration); // Duration in ms
        outPacket.encodeByte(1); // Forced Notice

        return outPacket;
    }

    public static OutPacket resultInstanceTable(String name, int type, int subType, boolean rightResult, int value) {
        var outPacket = new OutPacket(RESULT_INSTANCE_TABLE);

        outPacket.encodeString(name);
        outPacket.encodeInt(type); // nCount
        outPacket.encodeInt(subType);
        outPacket.encodeByte(rightResult);
        outPacket.encodeInt(value);

        return outPacket;
    }

    public static OutPacket resultInstanceTable(InstanceTableType ritt, boolean rightResult, int value) {
        return resultInstanceTable(ritt.getTableName(), ritt.getType(), ritt.getSubType(), rightResult, value);
    }

    /**
     * Creates a packet to indicate the golden hammer is finished.
     *
     * @param returnResult See below
     * @param result       when returnResult is:
     *                     0 or 1:
     *                     Anything: Golden hammer refinement applied
     *                     2:
     *                     0: Increased available upgrade by 1
     *                     1: Refining using golden hammer failed
     *                     3:
     *                     1: Item is not upgradable
     *                     2: 2 upgrade increases have been used already
     *                     3: You can't vicious hammer non-horntail necklace
     * @param upgradesLeft amount of upgrades left. NOTE: ((v9 >> 8) & 0xFF) - v9 + 2) (where v9 = upgradesLeft)
     * @return the created packet
     */
    public static OutPacket goldHammerItemUpgradeResult(GoldHammerResult returnResult, int result, int upgradesLeft) {
        // result shit seems random based on ^ notes so no enum
        var outPacket = new OutPacket(GOLD_HAMMER_ITEM_UPGRADE_RESULT);

        outPacket.encodeByte(returnResult.getVal());
        outPacket.encodeInt(result);
        if (returnResult.equals(GoldHammerResult.Success)) {
            outPacket.encodeInt(upgradesLeft);
        }

        return outPacket;
    }

    public static OutPacket returnToCharacterSelect() {
        return new OutPacket(RETURN_TO_CHARACTER_SELECT);
    }

    public static OutPacket returnToTitle() {
        return new OutPacket(RETURN_TO_TITLE);
    }

    public static OutPacket townPortal(TownPortal townPortal) {
        var outPacket = new OutPacket(TOWN_PORTAL); // As a response to Enter_TP_Request, creates the Door in the TownField

        outPacket.encodeInt(townPortal.getTownFieldId()); // townFieldId
        outPacket.encodeInt(townPortal.getFieldFieldId()); // field FieldId
        outPacket.encodeInt(townPortal.getSkillid()); // Skill Id
        outPacket.encodePosition(new Position()); // fieldField TownPortal Position

        return outPacket;
    }

    public static OutPacket givePopularityResult(PopularityResultType prType, Char targetChr, int newFame, boolean inc) {
        var outPacket = new OutPacket(GIVE_POPULARITY_RESULT);

        outPacket.encodeByte(prType.getVal());

        switch (prType) {
            case Success -> {
                outPacket.encodeString(targetChr.getName());
                outPacket.encodeByte(inc); // true = fame  |  false = defame
                outPacket.encodeInt(newFame);
            }
            case InvalidCharacterId, LevelLow, AlreadyDoneToday, AlreadyDoneTarget -> {
            }
            case Notify -> {
                outPacket.encodeString(targetChr.getName());
                outPacket.encodeByte(inc); // true = fame  |  false = defame
            }
        }

        return outPacket;
    }

    public static OutPacket randomPortalNotice(RandomPortal randomPortal) {
        var outPacket = new OutPacket(RANDOM_PORTAL_NOTICE);

        outPacket.encodeByte(randomPortal.getAppearType().ordinal());
        outPacket.encodeInt(randomPortal.getField().getId());

        return outPacket;
    }

    public static OutPacket randomMissionResult(RandomMissionType type, int arg1, int arg2) {
        var outPacket = new OutPacket(RANDOM_MISSION_RESULT);

        outPacket.encodeInt(type.getVal());
        outPacket.encodeInt(arg1);
        outPacket.encodeInt(arg2);

        return outPacket;
    }

    public static OutPacket antiMacroResult(final byte[] image, byte notificationType, byte antiMacroType) {
        return antiMacroResult(image, notificationType, antiMacroType, (byte) 0, (byte) 1);
    }

    public static OutPacket antiMacroResult(final byte[] image, byte notificationType, byte antiMacroType, byte first, byte refreshAntiMacroCount) {
        var outPacket = new OutPacket(ANTI_MACRO_RESULT);

        outPacket.encodeByte(notificationType);
        outPacket.encodeByte(antiMacroType);

        if (notificationType == AntiMacro.AntiMacroResultType.AntiMacroRes.getVal()) {
            outPacket.encodeByte(first);
            outPacket.encodeByte(refreshAntiMacroCount);

            if (image == null) {
                outPacket.encodeInt(0);
            } else {
                outPacket.encodeInt(image.length);
                outPacket.encodeArr(image);
            }
        } else if (notificationType == AntiMacro.AntiMacroResultType.AntiMacroRes_Fail.getVal() ||
                notificationType == AntiMacro.AntiMacroResultType.AntiMacroRes_Success.getVal()) {
            outPacket.encodeString(""); // unused?
        }

        return outPacket;
    }

    public static OutPacket setPassenserRequest(int requestorChrId) {
        var outPacket = new OutPacket(SET_PASSENSER_REQUEST);

        outPacket.encodeInt(requestorChrId);

        return outPacket;
    }

    public static OutPacket platformarEnterResult(boolean wrap) {
        var outPacket = new OutPacket(PLATFORMAR_ENTER_RESULT);

        outPacket.encodeByte(wrap);

        return outPacket;
    }

    public static OutPacket platformarOxyzen(int oxyzen) {
        var outPacket = new OutPacket(PLATFORMAR_OXYZEN);

        outPacket.encodeInt(oxyzen); // casted to long in client side

        return outPacket;
    }

    public static OutPacket merchantResult() {
        var outPacket = new OutPacket(ENTRUSTED_SHOP_CHECK_RESULT);
        outPacket.encodeInt(7);
        return outPacket;
    }

    public static OutPacket setMaplePoint(int maplePoint) {
        var outPacket = new OutPacket(SET_MAPLE_POINT);
        outPacket.encodeInt(maplePoint);
        return outPacket;
    }

    public static OutPacket temporaryStats_Reset() {
        return new OutPacket(FORCED_STAT_RESET);
    }
}
