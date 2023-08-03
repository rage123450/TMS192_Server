package net.swordie.ms.client.party;

import net.swordie.ms.client.character.Char;
import net.swordie.ms.connection.Encodable;
import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.connection.packet.WvsContext;
import net.swordie.ms.world.World;
import net.swordie.ms.world.field.Field;
import net.swordie.ms.world.field.Instance;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on 3/19/2018.
 */
public class Party implements Encodable {
    private int id;
    private final PartyMember[] partyMembers = new PartyMember[6];
    private boolean appliable;
    private String name;
    private int partyLeaderID;
    private World world;
    private Char applyingChar;
    private Instance instance;

    public boolean isAppliable() {
        return appliable;
    }

    public void setAppliable(boolean appliable) {
        this.appliable = appliable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PartyMember[] getPartyMembers() {
        return partyMembers;
    }

    public boolean isFull() {
        return Arrays.stream(getPartyMembers()).noneMatch(Objects::isNull);
    }

    public boolean isEmpty() {
        return Arrays.stream(getPartyMembers()).allMatch(Objects::isNull);
    }

    public void encode(OutPacket outPacket) {//PARTYDATA::Decode
        //238(234+4)
        Arrays.stream(partyMembers).forEach(pm -> outPacket.encodeInt(pm != null ? pm.getCharID() : 0));
        Arrays.stream(partyMembers).forEach(pm -> outPacket.encodeString(pm != null ? pm.getCharName() : "", 15));
        Arrays.stream(partyMembers).forEach(pm -> outPacket.encodeInt(pm != null ? pm.getJob() : 0));
        Arrays.stream(partyMembers).forEach(pm -> outPacket.encodeInt(pm != null ? pm.getSubSob() : 0));
        Arrays.stream(partyMembers).forEach(pm -> outPacket.encodeInt(pm != null ? pm.getLevel() : 0));
        Arrays.stream(partyMembers).forEach(pm -> outPacket.encodeInt(pm != null ? pm.getChannel() - 1 : -1));
        Arrays.stream(partyMembers).forEach(pm -> outPacket.encodeInt(pm != null && pm.isOnline() ? 1 : 0));
        outPacket.encodeInt(getPartyLeaderID());
        // end PARTYMEMBER struct

        //24
        Arrays.stream(partyMembers).forEach(pm -> outPacket.encodeInt(pm != null ? pm.getFieldID() : 0));
        //120
        Arrays.stream(partyMembers).forEach(pm -> {
            if (pm != null && pm.getTownPortal() != null) {
                pm.getTownPortal().encode(outPacket);
            } else {
                new TownPortal().encode(outPacket);
            }
        });

        outPacket.encodeByte(isAppliable() && !isFull());
        outPacket.encodeString(getName());
//        outPacket.encodeArr(new byte[50]);
    }

    public int getPartyLeaderID() {
        return partyLeaderID;
    }

    public void setPartyLeaderID(int partyLeaderID) {
        this.partyLeaderID = partyLeaderID;
    }

    /**
     * Adds a {@link Char} to this Party. Will do nothing if this Party is full.
     *
     * @param chr The Char to add.
     */
    public void addPartyMember(Char chr) {
        if (isFull()) {
            return;
        }
        PartyMember pm = new PartyMember(chr);
        if (isEmpty()) {
            setPartyLeaderID(chr.getId());
        }
        PartyMember[] partyMembers = getPartyMembers();
        boolean added = false;
        for (int i = 0; i < partyMembers.length; i++) {
            if (partyMembers[i] == null) {
                partyMembers[i] = pm;
                chr.setParty(this);
                added = true;
                break;
            }
        }
        if (added) {
            broadcast(WvsContext.partyResult(PartyResult.joinParty(this, chr.getName())));
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TownPortal getTownPortal() {
        PartyMember pm = Arrays.stream(getPartyMembers()).filter(Objects::nonNull)
                .filter(p -> p.getTownPortal() != null)
                .findFirst().orElse(null);
        return pm != null ? pm.getTownPortal() : new TownPortal();
    }

    public PartyMember getPartyLeader() {
        return Arrays.stream(getPartyMembers()).filter(p -> p != null && p.getCharID() == getPartyLeaderID()).findFirst().orElse(null);
    }

    public boolean hasCharAsLeader(Char chr) {
        return getPartyLeaderID() == chr.getId();
    }

    public void disband() {
        broadcast(WvsContext.partyResult(PartyResult.withdrawParty(this, getPartyLeader(), false, false)));
        for (Char chr : getOnlineChars()) {
            chr.setParty(null);
        }
        Arrays.fill(getPartyMembers(), null);
        getWorld().removeParty(this);
        setWorld(null);
    }

    public List<Char> getOnlineChars() {
        return getOnlineMembers().stream().map(PartyMember::getChr).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<PartyMember> getOnlineMembers() {
        return Arrays.stream(getPartyMembers()).filter(pm -> pm != null && pm.isOnline()).collect(Collectors.toList());
    }

    public List<PartyMember> getMembers() {
        return Arrays.stream(getPartyMembers()).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public void updateFull() {
        broadcast(WvsContext.partyResult(PartyResult.loadParty(this)));
    }

    public PartyMember getPartyMemberByID(int charID) {
        return Arrays.stream(getPartyMembers()).filter(p -> p != null && p.getCharID() == charID).findFirst().orElse(null);
    }

    public void broadcast(OutPacket outPacket) {
        for (PartyMember pm : getOnlineMembers()) {
            pm.getChr().write(outPacket);
        }
    }

    public void broadcast(OutPacket outPacket, Char exceptChar) {
        for (PartyMember pm : getOnlineMembers()) {
            if (!pm.getChr().equals(exceptChar)) {
                pm.getChr().write(outPacket);
            }
        }
    }

    public void removePartyMember(PartyMember partyMember) {
        for (int i = 0; i < getPartyMembers().length; i++) {
            PartyMember pm = getPartyMembers()[i];
            if (pm != null && pm.equals(partyMember)) {
                pm.getChr().setParty(null);
                getPartyMembers()[i] = null;
                break;
            }
        }
    }

    public void expel(int expelID) {
        PartyMember leaver = getPartyMemberByID(expelID);
        broadcast(WvsContext.partyResult(PartyResult.withdrawParty(this, leaver, true, true)));
        removePartyMember(leaver);
        updateFull();
    }

    public static Party createNewParty(boolean appliable, String name, World world) {
        Party party = new Party();
        party.setAppliable(appliable);
        party.setName(name);
        party.setWorld(world);
        world.addParty(party);
        return party;
    }

    public int getAvgLevel() {
        Collection<PartyMember> partyMembers = getMembers();
        return partyMembers.stream()
                .mapToInt(pm -> pm.getChr().getLevel())
                .sum() / partyMembers.size();
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public Char getApplyingChar() {
        return applyingChar;
    }

    public void setApplyingChar(Char applyingChar) {
        this.applyingChar = applyingChar;
    }

    public boolean isPartyMember(Char chr) {
        return getPartyMemberByID(chr.getId()) != null;
    }

    public void updatePartyMemberInfoByChr(Char chr) {
        if (!isPartyMember(chr)) {
            return;
        }
        getPartyMemberByID(chr.getId()).updateInfoByChar(chr);
        updateFull();
    }

    /**
     * Returns the average party member's level, according to the given Char's field.
     *
     * @param chr the chr to get the map to
     * @return the average level of the party in the Char's field
     */
    public int getAvgPartyLevel(Char chr) {
        Field field = chr.getField();
        return (int) getOnlineMembers().stream().filter(om -> om.getChr().getField() == field)
                .mapToInt(PartyMember::getLevel).average().orElse(chr.getLevel());
    }

    /**
     * Gets a list of party members in the same Field instance as the given Char, excluding the given Char.
     *
     * @param chr the given Char
     * @return a set of Characters that are in the same field as the given Char
     */
    public Set<Char> getPartyMembersInSameField(Char chr) {
        return getOnlineMembers().stream()
                .filter(pm -> pm.getChr() != null && pm.getChr() != chr && pm.getChr().getField() == chr.getField())
                .map(PartyMember::getChr)
                .collect(Collectors.toSet());
    }

    /**
     * Checks if this Party has a member with the given character id.
     *
     * @param charID the charID to look for
     * @return if the corresponding char is in the party
     */
    public boolean hasPartyMember(int charID) {
        return getPartyMemberByID(charID) != null;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }
}
