package net.swordie.ms.handlers.social;

import net.swordie.ms.client.Client;
import net.swordie.ms.client.character.Char;
import net.swordie.ms.client.party.Party;
import net.swordie.ms.client.party.PartyMember;
import net.swordie.ms.client.party.PartyResult;
import net.swordie.ms.client.party.PartyType;
import net.swordie.ms.connection.InPacket;
import net.swordie.ms.connection.packet.UserRemote;
import net.swordie.ms.connection.packet.WvsContext;
import net.swordie.ms.handlers.Handler;
import net.swordie.ms.handlers.header.InHeader;
import static net.swordie.ms.handlers.header.InHeader.*;
import net.swordie.ms.world.field.Field;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static net.swordie.ms.enums.ChatType.SystemNotice;

public class PartyHandler {

    private static final Logger log = LogManager.getLogger(PartyHandler.class);


    @Handler(op = PARTY_INVITABLE_SET)
    public static void handlePartyInvitableSet(Client c, InPacket inPacket) {
        c.getChr().setPartyInvitable(inPacket.decodeByte() != 0);
    }

    @Handler(op = PARTY_REQUEST)
    public static void handlePartyRequest(Client c, InPacket inPacket) {
        Char chr = c.getChr();
        byte type = inPacket.decodeByte();
        PartyType prt = PartyType.getByVal(type);
        Party party = chr.getParty();
        if (prt == null) {
            log.error(String.format("Unknown party request type %d", type));
            return;
        }
        switch (prt) {
            case PartyReq_CreateNewParty:
                if (party != null) {
                    chr.chatMessage("You are already in a party.");
                    return;
                }
                boolean appliable = inPacket.decodeByte() != 0;
                String name = inPacket.decodeString();
                party = Party.createNewParty(appliable, name, chr.getClient().getWorld());
                party.addPartyMember(chr);
                party.broadcast(WvsContext.partyResult(PartyResult.createNewParty(party)));
                break;
            case PartyReq_WithdrawParty:
                if (party.hasCharAsLeader(chr)) {
                    party.disband();
                } else {
                    PartyMember leaver = party.getPartyMemberByID(chr.getId());
                    party.broadcast(WvsContext.partyResult(PartyResult.withdrawParty(party, leaver, true, false)));
                    party.removePartyMember(leaver);
                    party.updateFull();
                }
                break;
            case PartyReq_InviteParty:
                String invitedName = inPacket.decodeString();
                Char invited = chr.getField().getCharByName(invitedName);
                if (invited == null) {
                    chr.chatMessage("Could not find that player.");
                    return;
                }
                if (party == null) {
                    party = Party.createNewParty(true, "Party with me!", chr.getClient().getWorld());
                    PartyMember pm = new PartyMember(chr);
                    party.setPartyLeaderID(pm.getCharID());
                    party.getPartyMembers()[0] = pm;
                    chr.setParty(party);
                    chr.write(WvsContext.partyResult(PartyResult.createNewParty(party)));
                }
                PartyMember inviter = party.getPartyLeader();
                if (!invited.isPartyInvitable()) {
                    chr.chatMessage(SystemNotice, String.format("%s is currently not accepting party invites.", invitedName));
                } else if (invited.getParty() == null) {
                    invited.write(WvsContext.partyResult(PartyResult.applyParty(party, inviter)));
                    chr.chatMessage(SystemNotice, String.format("You invited %s to your party.", invitedName));
                } else {
                    chr.chatMessage(SystemNotice, String.format("%s is already in a party.", invitedName));
                }
                break;
            case PartyReq_KickParty:
                int expelID = inPacket.decodeInt();
                party.expel(expelID);
                break;
            case PartyReq_ChangePartyBoss:
                int newLeaderID = inPacket.decodeInt();
                PartyMember leader = new PartyMember(chr.getWorld().getCharByID(newLeaderID));
                party.setPartyLeaderID(newLeaderID);
                party.broadcast(WvsContext.partyResult(PartyResult.changePartyBoss(party, 0, leader)));
                break;
            case PartyReq_ApplyParty:
                int partyID = inPacket.decodeInt();
                party = chr.getField().getChars().stream()
                        .filter(ch -> ch.getParty() != null && ch.getParty().getId() == partyID)
                        .map(Char::getParty)
                        .findFirst()
                        .orElse(null);
                if (party.getApplyingChar() == null) {
                    party.setApplyingChar(chr);
                    party.getPartyLeader().getChr().write(WvsContext.partyResult(PartyResult.inviteIntrusion(party, chr)));
                } else {
                    chr.chatMessage(SystemNotice, "That party already has an applier. Please wait until the applier is accepted or denied.");
                }
                break;
            default:
                log.error(String.format("Unhandled party request type %s", prt));
                break;
        }
        chr.dispose();
    }

    @Handler(op = PARTY_RESULT)
    public static void handlePartyResult(Client c, InPacket inPacket) {
        Char chr = c.getChr();
        byte type = inPacket.decodeByte();
        int partyID = inPacket.decodeInt();
        PartyType pt = PartyType.getByVal(type);
        if (pt == null) {
            log.error(String.format("Unknown party request result type %d", type));
            return;
        }
        switch (pt) {
            case PartyRes_ApplyParty_Accepted:
                Char leader = chr.getField().getChars().stream()
                        .filter(l -> l.getParty() != null
                                && l.getParty().getId() == partyID).findFirst().orElse(null);
                Party party = leader.getParty();
                if (!party.isFull()) {
                    party.addPartyMember(chr);
                    for (Char onChar : party.getOnlineMembers().stream().map(PartyMember::getChr).collect(Collectors.toList())) {
                        onChar.write(WvsContext.partyResult(PartyResult.joinParty(party, chr.getName())));
                        chr.write(UserRemote.receiveHP(onChar));
                    }
                    party.broadcast(UserRemote.receiveHP(chr));
                } else {
                    chr.write(WvsContext.partyResult(PartyResult.msg(PartyType.PartyRes_JoinParty_AlreadyFull)));
                }
                break;
            case PartyRes_ApplyParty_Rejected:
                leader = chr.getField().getChars().stream()
                        .filter(l -> l.getParty() != null
                                && l.getParty().getId() == partyID).findFirst().orElse(null);
                leader.chatMessage(SystemNotice, String.format("%s has declined your invite.", chr.getName()));
                break;
            case PartyRes_InviteIntrusion_Accepted:
                party = chr.getClient().getWorld().getPartybyId(partyID);
                Char applier = party.getApplyingChar();
                if (applier.getParty() != null) {
                    party.getPartyLeader().getChr().chatMessage(SystemNotice, String.format("%s is already in a party.", applier.getName()));
                } else if (!party.isFull()) {
                    party.addPartyMember(applier);
                    for (Char onChar : party.getOnlineMembers().stream().map(PartyMember::getChr).collect(Collectors.toList())) {
                        onChar.write(WvsContext.partyResult(PartyResult.joinParty(party, applier.getName())));
                    }
                } else {
                    applier.write(WvsContext.partyResult(PartyResult.msg(PartyType.PartyRes_JoinParty_AlreadyFull)));
                }
                party.setApplyingChar(null);
                break;
            case PartyRes_InviteIntrusion_Rejected:
                party = chr.getClient().getWorld().getPartybyId(partyID);
                applier = party.getApplyingChar();
                if (applier != null) {
                    applier.chatMessage(SystemNotice, "Your party apply request has been denied.");
                    party.setApplyingChar(null);
                }
                break;
            case PartyRes_InviteIntrusion_Sent:
                // Basically act like acks
                break;
            default:
                log.error(String.format("Unhandled party request result type %s", pt));
                break;
        }
        chr.dispose();
    }

    @Handler(op = PARTY_MEMBER_CANDIDATE_REQUEST)
    public static void handlePartyMemberCandidateRequest(Client c, InPacket inPacket) {
        Char chr = c.getChr();
        Field field = chr.getField();
        chr.write(WvsContext.partyMemberCandidateResult(field.getChars().stream()
                .filter(ch -> ch.isPartyInvitable() && !ch.equals(chr) && ch.getParty() == null)
                .collect(Collectors.toSet())));
    }

    @Handler(op = PARTY_CANDIDATE_REQUEST)
    public static void handlePartyCandidateRequest(Client c, InPacket inPacket) {
        Char chr = c.getChr();
        if (chr.getParty() != null) {
            chr.write(WvsContext.partyCandidateResult(new HashSet<>()));
            return;
        }
        Field field = chr.getField();
        Set<Party> parties = new HashSet<>();
        for (Char ch : field.getChars()) {
            if (ch.getParty() != null && ch.getParty().hasCharAsLeader(ch)) {
                parties.add(ch.getParty());
            }
        }
        chr.write(WvsContext.partyCandidateResult(parties));
    }

}
