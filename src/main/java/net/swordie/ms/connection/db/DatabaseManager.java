package net.swordie.ms.connection.db;

import net.swordie.ms.client.Account;
import net.swordie.ms.client.LinkSkill;
import net.swordie.ms.client.User;
import net.swordie.ms.client.alliance.Alliance;
import net.swordie.ms.client.anticheat.Offense;
import net.swordie.ms.client.anticheat.OffenseManager;
import net.swordie.ms.client.character.*;
import net.swordie.ms.client.character.avatar.AvatarData;
import net.swordie.ms.client.character.avatar.AvatarLook;
import net.swordie.ms.client.character.cards.CharacterCard;
import net.swordie.ms.client.character.cards.MonsterBookInfo;
import net.swordie.ms.client.character.damage.DamageSkinSaveData;
import net.swordie.ms.client.character.items.Equip;
import net.swordie.ms.client.character.items.Inventory;
import net.swordie.ms.client.character.items.Item;
import net.swordie.ms.client.character.items.PetItem;
import net.swordie.ms.client.character.keys.FuncKeyMap;
import net.swordie.ms.client.character.keys.Keymapping;
import net.swordie.ms.client.character.potential.CharacterPotential;
import net.swordie.ms.client.character.quest.Quest;
import net.swordie.ms.client.character.quest.QuestManager;
import net.swordie.ms.client.character.quest.progress.*;
import net.swordie.ms.client.character.skills.ChosenSkill;
import net.swordie.ms.client.character.skills.Skill;
import net.swordie.ms.client.character.skills.StolenSkill;
import net.swordie.ms.client.friend.Friend;
import net.swordie.ms.client.guild.Guild;
import net.swordie.ms.client.guild.GuildMember;
import net.swordie.ms.client.guild.GuildRequestor;
import net.swordie.ms.client.guild.GuildSkill;
import net.swordie.ms.client.guild.bbs.BBSRecord;
import net.swordie.ms.client.guild.bbs.BBSReply;
import net.swordie.ms.client.trunk.Trunk;
import net.swordie.ms.life.Familiar;
import net.swordie.ms.life.Merchant.EmployeeTrunk;
import net.swordie.ms.life.Merchant.MerchantItem;
import net.swordie.ms.life.drop.DropInfo;
import net.swordie.ms.loaders.containerclasses.EquipDrop;
import net.swordie.ms.loaders.containerclasses.MonsterCollectionGroupRewardInfo;
import net.swordie.ms.loaders.containerclasses.MonsterCollectionMobInfo;
import net.swordie.ms.loaders.containerclasses.MonsterCollectionSessionRewardInfo;
import net.swordie.ms.util.FileTime;
import net.swordie.ms.util.SystemTime;
import net.swordie.ms.world.shop.NpcShopItem;
import net.swordie.ms.world.shop.cashshop.CashItemInfo;
import net.swordie.ms.world.shop.cashshop.CashShopCategory;
import net.swordie.ms.world.shop.cashshop.CashShopItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.stream;
import static net.swordie.ms.handlers.EventManager.addEvent;

/**
 * Created on 12/12/2017.
 */
public class DatabaseManager {
    private static final Logger log = LogManager.getLogger(DatabaseManager.class);
    private static final int KEEP_ALIVE_MS = 10 * 60 * 1000; // 10 minutes

    private static SessionFactory sessionFactory;
//    private static List<Session> sessions;

    public static void init() {
        Configuration configuration = new Configuration().configure();
        configuration.setProperty("autoReconnect", "true");
        var dbClasses = new Class[]{
                User.class,
                FileTime.class,
                SystemTime.class,
                NonCombatStatDayLimit.class,
                CharacterCard.class,
                Item.class,
                Equip.class,
                Inventory.class,
                Skill.class,
                FuncKeyMap.class,
                Keymapping.class,
                SPSet.class,
                ExtendSP.class,
                CharacterStat.class,
                AvatarLook.class,
                AvatarData.class,
                Char.class,
                Account.class,
                QuestManager.class,
                Quest.class,
                QuestProgressRequirement.class,
                QuestProgressLevelRequirement.class,
                QuestProgressItemRequirement.class,
                QuestProgressMobRequirement.class,
                QuestProgressMoneyRequirement.class,
                Guild.class,
                GuildMember.class,
                GuildRequestor.class,
                GuildSkill.class,
                BBSRecord.class,
                BBSReply.class,
                Friend.class,
                Macro.class,
                DamageSkinSaveData.class,
                Trunk.class,
                PetItem.class,
                MonsterBookInfo.class,
                CharacterPotential.class,
                LinkSkill.class,
                Familiar.class,
                StolenSkill.class,
                ChosenSkill.class,
                CashItemInfo.class,
                CashShopItem.class,
                CashShopCategory.class,
                MonsterCollectionSessionRewardInfo.class,
                MonsterCollectionGroupRewardInfo.class,
                MonsterCollectionMobInfo.class,
                MonsterCollection.class,
                MonsterCollectionReward.class,
                MonsterCollectionExploration.class,
                Alliance.class,
                DropInfo.class,
                Offense.class,
                OffenseManager.class,
                NpcShopItem.class,
                EquipDrop.class,
                EmployeeTrunk.class,
                MerchantItem.class,
        };
        stream(dbClasses).forEach(configuration::addAnnotatedClass);

        sessionFactory = configuration.buildSessionFactory();
//        sessions = new ArrayList<>();

        sendHeartBeat();
    }

    /**
     * Sends a simple query to the DB to ensure that the connection stays alive.
     */
    private static void sendHeartBeat() {
        Session session = getSession();
        Transaction t = Objects.requireNonNull(session).beginTransaction();
//        var q = session.createQuery("from Char where id = 1");
        var q = session.createQuery("from Char where id = 1", Char.class);
        q.list();
        t.commit();
        session.close();
        addEvent(DatabaseManager::sendHeartBeat, KEEP_ALIVE_MS);
    }

    public synchronized static Session getSession() {
        if (sessionFactory == null) {
            return null;
        }
        return sessionFactory.openSession();
    }

/*    public static void cleanUpSessions() {
        sessions.removeAll(sessions.stream().filter(s -> !s.isOpen()).toList());
    }*/

    public static void saveToDB(Object obj) {
        log.info(String.format("%s: Trying to save obj %s.", LocalDateTime.now(), obj.getClass()));
//        log.info(String.format("%s: Trying to save obj %s.", LocalDateTime.now(), obj));
        try (Session session = getSession()) {
            Transaction t = Objects.requireNonNull(session).beginTransaction();
            session.saveOrUpdate(obj);
            t.commit();
        }
//        cleanUpSessions();
    }

    public static void deleteFromDB(Object obj) {
        log.info(String.format("%s: Trying to delete obj %s.", LocalDateTime.now(), obj));
        try (Session session = getSession()) {
            Transaction t = Objects.requireNonNull(session).beginTransaction();
            session.delete(obj);
            t.commit();
        }
//        cleanUpSessions();
    }

    public static Object getObjFromDB(Class clazz, int id) {
        log.info(String.format("%s: Trying to get obj %s with id %d.", LocalDateTime.now(), clazz, id));
        Object o;
        try (Session session = getSession()) {
            Transaction transaction = Objects.requireNonNull(session).beginTransaction();
            // String.format for query, just to fill in the class
            // Can't set the FROM clause with a parameter it seems
            // session.get doesn't work for Chars for whatever reason

/*            jakarta.persistence.Query query = session.createQuery(String.format("FROM %s WHERE id = :val", clazz.getName()));
            query.setParameter("val", id);
            List l = ((org.hibernate.query.Query) query).list();
            if (l != null && l.size() > 0) {
                o = l.get(0);
            }*/
            o = session.get(clazz, id);
            transaction.commit();
        }
        return o;
    }

    public static Object getObjFromDB(Class clazz, String name) {
        return getObjFromDB(clazz, "name", name);
    }

    public static Object getObjFromDB(Class clazz, String columnName, Object value) {
        log.info(String.format("%s: Trying to get obj %s with value %s.", LocalDateTime.now(), clazz, value));
        Object o = null;
        try (Session session = getSession()) {
            Transaction transaction = Objects.requireNonNull(session).beginTransaction();
            // String.format for query, just to fill in the class
            // Can't set the FROM clause with a parameter it seems

//            jakarta.persistence.Query query = session.createQuery(String.format("FROM %s WHERE %s = :val", clazz.getName(), columnName));
            jakarta.persistence.Query query = session.createQuery(String.format("FROM %s WHERE %s = :val", clazz.getName(), columnName), clazz);
            query.setParameter("val", value);
            var l = ((org.hibernate.query.Query) query).list();
            if (l != null && !l.isEmpty()) {
                o = l.get(0);
            }
            transaction.commit();
//            session.close();
        }
        return o;
    }

    public static Object getObjListFromDB(Class clazz) {
        List list;
        try (Session session = getSession()) {
            Transaction transaction = Objects.requireNonNull(session).beginTransaction();
            // String.format for query, just to fill in the class
            // Can't set the FROM clause with a parameter it seems

//            jakarta.persistence.Query query = session.createQuery(String.format("FROM %s", clazz.getName()));
            jakarta.persistence.Query query = session.createQuery("FROM " + clazz.getName(), clazz);
            list = ((org.hibernate.query.Query) query).list();
            transaction.commit();
//            session.close();
        }
        return list;
    }

    public static Object getObjListFromDB(Class clazz, String columnName, Object value) {
        List list;
        try (Session session = getSession()) {
            Transaction transaction = Objects.requireNonNull(session).beginTransaction();
            // String.format for query, just to fill in the class
            // Can't set the FROM clause with a parameter it seems

//            jakarta.persistence.Query query = session.createQuery(String.format("FROM %s WHERE %s = :val", clazz.getName(), columnName));
            jakarta.persistence.Query query = session.createQuery("FROM " + clazz.getName() + " WHERE " + columnName + " = :val", clazz);
            query.setParameter("val", value);
            list = ((org.hibernate.query.Query) query).list();
            transaction.commit();
//            session.close();
        }
        return list;
    }

    public static void modifyObjectFromDB(Class clazz, int id, String columnName, Object value) {
        Session session = null;
        try {
            session = getSession();
            Transaction transaction = Objects.requireNonNull(session).beginTransaction();
            // String.format for query, just to fill in the class
            // Can't set the FROM clause with a parameter it seems

//            jakarta.persistence.Query query = session.createQuery(String.format("UPDATE %s SET %s = :val WHERE id = :objid", clazz.getName(), columnName));
            jakarta.persistence.Query query = session.createQuery("UPDATE " + clazz.getName() + " SET " + columnName + " = :val WHERE id = :objid", clazz);
            query.setParameter("objid", id);
            query.setParameter("val", value);
            query.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }
}
