package net.swordie.ms.constants;

import net.swordie.ms.ServerConstants;
import net.swordie.ms.client.character.items.*;
import net.swordie.ms.connection.db.DatabaseManager;
import net.swordie.ms.enums.*;
import net.swordie.ms.life.drop.DropInfo;
import net.swordie.ms.life.pet.PetSkill;
import net.swordie.ms.loaders.EtcData;
import net.swordie.ms.loaders.ItemData;
import net.swordie.ms.loaders.containerclasses.EquipDrop;
import net.swordie.ms.loaders.containerclasses.ItemInfo;
import net.swordie.ms.util.Util;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.stream.Collectors;

import static net.swordie.ms.enums.InvType.EQUIP;
import static net.swordie.ms.enums.ItemGrade.*;

/**
 * Created on 12/12/2017.
 */
public class ItemConstants {
    public static final int EMPTY_SOCKET_ID = 3;
    public static final short INACTIVE_SOCKET = 0;
    public static final short MIN_LEVEL_FOR_SOUL_SOCKET = 75;
    public static final int SOUL_ENCHANTER_BASE_ID = 2590000;
    public static final int SOUL_ITEM_BASE_ID = 2591000;
    public static final int MAX_SOUL_CAPACITY = 1000;
    public static final int MOB_DEATH_SOUL_MP_COUNT = 150;
    public static final int MOB_CARD_BASE_ID = 2380000;
    public static final int FAMILIAR_PREFIX = 996;
    public static final int SPELL_TRACE_ID = 4001832;
    public static final int RAND_CHAOS_MAX = 5;
    public static final int INC_RAND_CHAOS_MAX = 10;

    public static final byte MAX_SKIN = 13;
    public static final int MIN_HAIR = 30000;
    public static final int MAX_HAIR = 49999;
    public static final int MIN_FACE = 20000;
    public static final int MAX_FACE = 29999;

    static final org.apache.logging.log4j.Logger log = LogManager.getRootLogger();

    public static final int HYPER_TELEPORT_ROCK = 5040004;

    //CUBING CONSTANTS BELOW
    public static int GLOBAL_TIER_UP_MULTIPLIER = 1; //For DMT uses? 2 = 2x, 3 = 3x, etc etc
    public static final int THIRD_LINE_CHANCE = 50;

    //Chances are out of 1k (10* typical 100 values so we don't have to use doubles)
    public static final int DEFAULT_PRIME_LINE_2_CHANCE = 125;
    public static final int DEFAULT_PRIME_LINE_3_CHANCE = 100;

    public static final int BOOSTED_PRIME_LINE_2_CHANCE = 220;
    public static final int BOOSTED_PRIME_LINE_3_CHANCE = 100;

    public static final int RED_CUBE = 5062009;
    public static final int BLACK_CUBE = 5062010;

    public static final int VIOLET_CUBE_PRIME_LINE_CHANCE = 85;
    public static final int VIOLET_CUBE = 5062024; //NEVER!
    //If you plan to implement, you must store the grade to the item and change Equip#getBaseGrade and similar methods behave appropriately
    //This is because you can select a non-current tier line as the primary line, thus making the current methods believe the item is actually a tier lower!
    //By storing the tier in the Equip object you can allow for the list of potentials to be completely independent

    public static final int BONUS_POTENTIAL_CUBE = 5062500;
    public static final int SPECIAL_BONUS_POTENTIAL_CUBE = 5062501;
    public static final int WHITE_BONUS_POTENTIAL_CUBE = 5062503;

    public static final int OCCULT_CUBE = 2711000;
    public static final int REBOOT_OCCULT_CUBE = 2711001; //Later Versions 2711011(?)
    public static final List<Integer> OCCULT_CUBES = Arrays.asList(2711000, 2711001, 2711009, 2711011); //2711000 = Tradable Stackable, 2711001 = Untradable NonStackable, 2711009 = Untradable Stackable, >v176 2711011 = Account Movement only

    public static final int MASTER_CRAFTSMANS_CUBE = 2711005;
    public static final int REBOOT_MASTER_CRAFTSMANS_CUBE = 2710002; //Later Versions 2711012(?)
    public static final List<Integer> MASTER_CRAFTSMANS_CUBES = Arrays.asList(2711003, 2710002, 2711005, 2711012); //2711003 = Tradable, 2710002 = Account Movement only  -  Later Versions Untradable, 2711005 = Untradable, >v176 2711012 = Account Movement only

    public static final int MEISTERS_CUBE = 2711004;
    public static final int REBOOT_MEISTERS_CUBE = 2710003; //Later Versions 2711013(?)
    public static final List<Integer> MEISTERS_CUBES = Arrays.asList(2711004, 2710003, 2711006, 2711013); //2711004 = Tradable, 2710003 = Account Movement only  -  Later Versions Untradable, 2711006 = Untradable, >v176 2711013 = Account Movement only

    public static final int BONUS_OCCULT_CUBE = 2730002; // > v176
    public static final List<Integer> BONUS_OCCULT_CUBES = Arrays.asList(2730000, 2730002, 2730004); //2730000 = Untradable Stackable, 2730002 = Tradable Stackable, 2730004 = Account Movement only Stackable

    public static final int SYSTEM_DEFAULT_CUBE_INDICATOR = -1; //Cause default int value is 0 and we want to differentiate when it is intentional to not have a cube source

    //Chances are out of 1k (10* typical 100 values so we don't have to use doubles)

    public static final int[] OCCULT_CUBE_TIER_UP_RATES = new int[]{10, 0, 0}; //1%, 0%, 0%
    public static final int[] MASTER_CRAFTSMANS_CUBE_TIER_UP_RATES = new int[]{120, 40, 0}; //12%, 4%, 0%
    public static final int[] MEISTERS_CUBE_TIER_UP_RATES = new int[]{120, 80, 50}; //12%, 8%, 5%

    public static final int[] RED_CUBE_TIER_UP_RATES = new int[]{150, 60, 25}; //15%, 6%, 2.5%
    public static final int[] BLACK_CUBE_TIER_UP_RATES = new int[]{150, 120, 50}; //15%, 12%, 5%

    public static final int[] VIOLET_CUBE_TIER_UP_RATES = new int[]{30, 12, 5}; //3%, 1.2%, 0.5% //No data to support this, but it's confirmed to be the worst cube to tier with

    public static final int[] BONUS_OCCULT_CUBE_TIER_UP_RATES = new int[]{20, 0, 0}; //2%, 0%, 0%
    public static final int[] BONUS_POT_CUBE_TIER_UP_RATE = new int[]{200, 100, 50}; //20%, 10%, 5%
    public static final int[] WHITE_BONUS_POT_CUBE_TIER_UP_RATE = new int[]{200, 150, 75}; //20%, 15%, 7.5%

    public static final int[] DEFAULT_CUBE_TIER_UP_RATES = new int[]{100, 50, 25}; //10%, 5%, 2.5%

    public static final int NEBILITE_BASE_ID = 3060000;

    public static final int HORNTAIL_NECKLACE[] = {
            1122000, // Horntail Necklace
            1122076, // Chaos Horntail Necklace
            1122151, // Chaos Horntail Necklace (+2)
            1122249, // Dream Horntail Necklace
            1122278, // Mystic Horntail Necklace
    };

    public static final int EXP_2X_COUPON[] = {
            5680484,
            5680342,
            5680275,
            5211122,
            5211121,
            5211120,
            5211049,
            5211048,
            5211047,
            5211046,
            5211000
    };

    public static final short MAX_HAMMER_SLOTS = 2;

    private static final Integer[] soulPotList = new Integer[]{32001, 32002, 32003, 32004, 32005, 32006, 32011, 32012, // flat
            32041, 32042, 32043, 32044, 32045, 32046, 32051, 32052}; // rate

    private static final int TUC_IGNORE_ITEMS[] = {
            1113231, // Master Ring SS
            1114301, // Reboot Vengeful Ring
            1114302, // Synergy Ring
            1114303, // Cosmos Ring
            1114304, // Reboot Cosmos Ring
            1114305, // Chaos Ring
    };

    public static final int NON_KMS_BOSS_SETS[] = {
        127, // Amaterasu
        128, // Oyamatsumi
        129, // Ame-no-Uzume
        130, // Tsukuyomi
        131, // Susano-o
        315, // Cracked Gollux
        316, // Solid Gollux
        317, // Reinforced Gollux
        318, // Superior Gollux
        328, // Sweetwater
    };

    public static final int NON_KMS_BOSS_ITEMS[] = {
        1032224, // Sweetwater Earrings
        1022211, // Sweetwater Monocle
        1012438, // Sweetwater Tattoo
        1152160, // Sweetwater Shoulder
        1132247, // Sweetwater Belt
        1122269, // Sweetwater Pendant
    };

    // Spell tracing
    private static final int BASE_ST_COST = 30;
    private static final int INNOCENCE_ST_COST = 1337;
    private static final int CLEAN_SLATE_ST_COST = 200;

    // Flames
    public static final double WEAPON_FLAME_MULTIPLIER[] = { 1.0, 2.2, 3.65, 5.35, 7.3, 8.8, 10.25 };
    public static final double WEAPON_FLAME_MULTIPLIER_BOSS_WEAPON[] = { 1.0, 1.0, 3.0, 4.4, 6.05, 8.0, 10.25 }; // Boss weapons do not ever roll stat level 1/2.
    public static final short EQUIP_FLAME_LEVEL_DIVIDER = 40;
    public static final short EQUIP_FLAME_LEVEL_DIVIDER_EXTENDED = 20;

    public static final int EXCEPTIONAL_EX_ALLOWED[] = {
            1152155, // Scarlet Shoulder
            1113015, // Secret Ring
    };

    // Self-made drops per mob
    public static final Map<Integer, Set<DropInfo>> consumableDropsPerLevel = new HashMap<>();
    public static final Map<ItemJob, Map<Integer, Set<DropInfo>>> equipDropsPerLevel = new HashMap<>();

    static {
        initConsumableDrops();
        initEquipDrops();
    }

    private static void initConsumableDrops() {
        consumableDropsPerLevel.put(0, Util.makeSet(
                new DropInfo(2000000, 200), // Red Potion
                new DropInfo(2000003, 200)  // Blue Potion
        ));
        consumableDropsPerLevel.put(20, Util.makeSet(
                new DropInfo(2000002, 200), // White Potion
                new DropInfo(2000006, 200)  // Mana Elixir
        ));
        consumableDropsPerLevel.put(40, Util.makeSet(
                new DropInfo(2022000, 200),  // Pure Water
                new DropInfo(2022003, 200)   // Unagi
        ));
        consumableDropsPerLevel.put(60, Util.makeSet(
                new DropInfo(2001001, 200), // Ice Cream Pop
                new DropInfo(2001002, 200)  // Very Special Sundae
        ));
        consumableDropsPerLevel.put(80, Util.makeSet(
                new DropInfo(2001001, 200), // Ice Cream Pop
                new DropInfo(2001002, 200)  // Very Special Sundae
        ));
        consumableDropsPerLevel.put(100, Util.makeSet(
                new DropInfo(2020012, 200), // Melting Cheese
                new DropInfo(2020014, 200)  // Sunrise Dew
        ));
        consumableDropsPerLevel.put(120, Util.makeSet(
                new DropInfo(2020012, 200), // Melting Cheese
                new DropInfo(2020014, 200)  // Sunrise Dew
        ));
        consumableDropsPerLevel.put(140, Util.makeSet(
                new DropInfo(2020013, 100), // Reindeer Milk
                new DropInfo(2020015, 100), // Sunset Dew
                new DropInfo(2050004, 10)   // All Cure
        ));
    }

    private static void initEquipDrops() {
        List<EquipDrop> drops = (List<EquipDrop>) DatabaseManager.getObjListFromDB(EquipDrop.class);
        for (EquipDrop drop : drops) {
            ItemJob job = drop.getJob();
            int level = drop.getLevel();
            if (!equipDropsPerLevel.containsKey(job)) {
                equipDropsPerLevel.put(job, new HashMap<>());
            }
            Map<Integer, Set<DropInfo>> jobMap = equipDropsPerLevel.get(job);
            if (!jobMap.containsKey(level)) {
                jobMap.put(level, new HashSet<>());
            }
            Set<DropInfo> set = jobMap.get(level);
            set.add(new DropInfo(drop.getId(), 100));
        }
    }

    public static int getGenderFromId(int nItemID) {
        int result;

        if (nItemID / 1000000 != 1 && getItemPrefix(nItemID) != 254 || getItemPrefix(nItemID) == 119 || getItemPrefix(nItemID) == 168)
            return 2;
        switch (nItemID / 1000 % 10) {
            case 0:
                result = 0;
                break;
            case 1:
                result = 1;
                break;
            default:
                return 2;
        }
        return result;
    }

    public static int getBodyPartFromItem(int nItemID, int gender) {
        List<Integer> arr = getBodyPartArrayFromItem(nItemID, gender);
        int result = arr.size() > 0 ? arr.get(0) : 0;
        return result;
    }

    public static List<Integer> getBodyPartArrayFromItem(int itemID, int genderArg) {
        int gender = getGenderFromId(itemID);
        EquipPrefix prefix = EquipPrefix.getByVal(getItemPrefix(itemID));
        List<Integer> bodyPartList = new ArrayList<>();
        if (prefix != EquipPrefix.Emblem && prefix != EquipPrefix.Bit &&
                gender != 2 && genderArg != 2 && gender != genderArg) {
            return bodyPartList;
        }
        if(prefix != null) {
            switch (prefix) {
                case Hat:
                    bodyPartList.add(BodyPart.Hat.getVal());
                    bodyPartList.add(BodyPart.EvanHat.getVal());
                    bodyPartList.add(BodyPart.APHat.getVal());
                    bodyPartList.add(BodyPart.DUHat.getVal());
                    bodyPartList.add(BodyPart.ZeroHat.getVal());
                    break;
                case FaceAccessory:
                    bodyPartList.add(BodyPart.FaceAccessory.getVal());
                    bodyPartList.add(BodyPart.APFaceAccessory.getVal());
                    bodyPartList.add(BodyPart.DUFaceAccessory.getVal());
                    bodyPartList.add(BodyPart.ZeroFaceAccessory.getVal());
                    break;
                case EyeAccessory:
                    bodyPartList.add(BodyPart.EyeAccessory.getVal());
                    bodyPartList.add(BodyPart.ZeroEyeAccessory.getVal());
                    break;
                case Earrings:
                    bodyPartList.add(BodyPart.Earrings.getVal());
                    bodyPartList.add(BodyPart.ZeroEarrings.getVal());
                    break;
                case Top:
                case Overall:
                    bodyPartList.add(BodyPart.Top.getVal());
                    bodyPartList.add(BodyPart.APTop.getVal());
                    bodyPartList.add(BodyPart.DUTop.getVal());
                    bodyPartList.add(BodyPart.ZeroTop.getVal());
                    break;
                case Bottom:
                    bodyPartList.add(BodyPart.Bottom.getVal());
                    bodyPartList.add(BodyPart.APBottom.getVal());
                    bodyPartList.add(BodyPart.ZeroBottom.getVal());
                    break;
                case Shoes:
                    bodyPartList.add(BodyPart.Shoes.getVal());
                    bodyPartList.add(BodyPart.APShoes.getVal());
                    bodyPartList.add(BodyPart.ZeroShoes.getVal());
                    break;
                case Gloves:
                    bodyPartList.add(BodyPart.Gloves.getVal());
                    bodyPartList.add(BodyPart.APGloves.getVal());
                    bodyPartList.add(BodyPart.DUGloves.getVal());
                    bodyPartList.add(BodyPart.ZeroGloves.getVal());
                    break;
                case Shield:
                case Katara:
                case SecondaryWeapon:
                case Lapis:
                    bodyPartList.add(BodyPart.Shield.getVal());
                    break;
                case Lazuli:
                    bodyPartList.add(BodyPart.Weapon.getVal());
                    break;
                case Cape:
                    bodyPartList.add(BodyPart.Cape.getVal());
                    bodyPartList.add(BodyPart.APCape.getVal());
                    bodyPartList.add(BodyPart.DUCape.getVal());
                    bodyPartList.add(BodyPart.ZeroCape.getVal());
                    break;
                case Ring:
                    bodyPartList.add(BodyPart.Ring1.getVal());
                    bodyPartList.add(BodyPart.Ring2.getVal());
                    bodyPartList.add(BodyPart.Ring3.getVal());
                    bodyPartList.add(BodyPart.Ring4.getVal());
                    bodyPartList.add(BodyPart.ZeroRing1.getVal());
                    bodyPartList.add(BodyPart.ZeroRing2.getVal());
                    break;
                case Pendant:
                    bodyPartList.add(BodyPart.Pendant.getVal());
                    bodyPartList.add(BodyPart.ExtendedPendant.getVal());
                    break;
                case Belt:
                    bodyPartList.add(BodyPart.Belt.getVal());
                    break;
                case Medal:
                    bodyPartList.add(BodyPart.Medal.getVal());
                    break;
                case Shoulder:
                    bodyPartList.add(BodyPart.Shoulder.getVal());
                    break;
                case PocketItem:
                    bodyPartList.add(BodyPart.PocketItem.getVal());
                    break;
                case MonsterBook:
                    bodyPartList.add(BodyPart.MonsterBook.getVal());
                    break;
                case Badge:
                    bodyPartList.add(BodyPart.Badge.getVal());
                    break;
                case Emblem:
                    bodyPartList.add(BodyPart.Emblem.getVal());
                    break;
                case Totem:
                    bodyPartList.add(BodyPart.Totem1.getVal());
                    bodyPartList.add(BodyPart.Totem2.getVal());
                    bodyPartList.add(BodyPart.Totem3.getVal());
                    break;
                case MachineEngine:
                    bodyPartList.add(BodyPart.MachineEngine.getVal());
                    break;
                case MachineArm:
                    bodyPartList.add(BodyPart.MachineArm.getVal());
                    break;
                case MachineLeg:
                    bodyPartList.add(BodyPart.MachineLeg.getVal());
                    break;
                case MachineFrame:
                    bodyPartList.add(BodyPart.MachineFrame.getVal());
                    break;
                case MachineTransistor:
                    bodyPartList.add(BodyPart.MachineTransistor.getVal());
                    break;
                case Android:
                    bodyPartList.add(BodyPart.Android.getVal());
                    break;
                case MechanicalHeart:
                    bodyPartList.add(BodyPart.MechanicalHeart.getVal());
                    break;
                case Bit:
                    for (int id = BodyPart.BitsBase.getVal(); id <= BodyPart.BitsEnd.getVal(); id++) {
                        bodyPartList.add(id);
                    }
                    break;
                case PetWear:
                    bodyPartList.add(BodyPart.PetWear1.getVal());
                    bodyPartList.add(BodyPart.PetWear2.getVal());
                    bodyPartList.add(BodyPart.PetWear3.getVal());
                    break;
                // case 184: // unknown, equip names are untranslated and google search results in hekaton screenshots
                // case 185:
                // case 186:
                // case 187:
                // case 188:
                // case 189:
                case TamingMob:
                    bodyPartList.add(BodyPart.TamingMob.getVal());
                    break;
                case Saddle:
                    bodyPartList.add(BodyPart.Saddle.getVal());
                    break;
                case EvanHat:
                    bodyPartList.add(BodyPart.EvanHat.getVal());
                    break;
                case EvanPendant:
                    bodyPartList.add(BodyPart.EvanPendant.getVal());
                    break;
                case EvanWing:
                    bodyPartList.add(BodyPart.EvanWing.getVal());
                    break;
                case EvanShoes:
                    bodyPartList.add(BodyPart.EvanShoes.getVal());
                    break;
                default:
                    if (ItemConstants.isLongOrBigSword(itemID) || ItemConstants.isWeapon(itemID)) {
                        bodyPartList.add(BodyPart.Weapon.getVal());
                        if (ItemConstants.isFan(itemID)) {
                            bodyPartList.add(BodyPart.HakuFan.getVal());
                        } else {
                            bodyPartList.add(BodyPart.ZeroWeapon.getVal());
                        }
                    } else {
                        log.debug("Unknown type? id = " + itemID);
                    }
                    break;
            }
        }
        else
        {
            log.debug("Unknown type? id = " + itemID);
        }
        return bodyPartList;

    }

    public static int getItemPrefix(int nItemID) {
        return nItemID / 10000;
    }

    public static boolean isLongOrBigSword(int nItemID) {
        return getItemPrefix(nItemID) == EquipPrefix.Lapis.getVal() || getItemPrefix(nItemID) == EquipPrefix.Lazuli.getVal();
    }

    private static boolean isFan(int nItemID) {
        return getItemPrefix(nItemID) == EquipPrefix.Fan.getVal();
    }

    public static int getWeaponType(int itemID) {
        if (itemID / 1000000 != 1) {
            return 0;
        }
        return getItemPrefix(itemID) % 100;
    }

    public static boolean isThrowingItem(int itemID) {
        return isThrowingStar(itemID) || isBullet(itemID) || isBowArrow(itemID);
    }

    public static boolean isThrowingStar(int itemID) {
        return getItemPrefix(itemID) == 207;
    }

    public static boolean isBullet(int itemID) {
        return getItemPrefix(itemID) == 233;
    }

    public static boolean isBowArrow(int itemID) {
        return itemID / 1000 == 2060;
    }

    public static boolean isFamiliar(int itemID) {
        return getItemPrefix(itemID) == 287;
    }

    public static boolean isEnhancementScroll(int scrollID) {
        return scrollID / 100 == 20493;
    }

    public static boolean isHat(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Hat.getVal();
    }

    public static boolean isWeapon(int itemID) {
        return itemID >= 1210000 && itemID < 1600000;
    }

    public static boolean isSecondary(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.SecondaryWeapon.getVal();
    }

    public static boolean isShield(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Shield.getVal();
    }

    public static boolean isAccessory(int itemID) {
        return (itemID >= 1010000 && itemID < 1040000) || (itemID >= 1122000 && itemID < 1153000) ||
                (itemID >= 1112000 && itemID < 1113000) || (itemID >= 1670000 && itemID < 1680000);
    }

    public static boolean isFaceAccessory(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.FaceAccessory.getVal();
    }

    public static boolean isEyeAccessory(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.EyeAccessory.getVal();
    }

    public static boolean isEarrings(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Earrings.getVal();
    }

    public static boolean isTop(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Top.getVal();
    }

    public static boolean isOverall(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Overall.getVal();
    }

    public static boolean isBottom(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Bottom.getVal();
    }

    public static boolean isShoe(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Shoes.getVal();
    }

    public static boolean isGlove(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Gloves.getVal();
    }

    public static boolean isCape(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Cape.getVal();
    }

    public static boolean isArmor(int itemID) {
        return !isAccessory(itemID) && !isWeapon(itemID) && !isBadge(itemID) && !isMechanicalHeart(itemID);
    }

    public static boolean isRing(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Ring.getVal();
    }

    public static boolean isPendant(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Pendant.getVal();
    }

    public static boolean isBelt(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Belt.getVal();
    }

    public static boolean isMedal(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Medal.getVal();
    }

    public static boolean isShoulder(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Shoulder.getVal();
    }

    public static boolean isPocketItem(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.PocketItem.getVal();
    }

    public static boolean isMonsterBook(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.MonsterBook.getVal();
    }

    public static boolean isBadge(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Badge.getVal();
    }

    public static boolean isEmblem(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Emblem.getVal();
    }

    public static boolean isTotem(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Totem.getVal();
    }

    public static boolean isAndroid(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Android.getVal();
    }

    public static boolean isMechanicalHeart(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.MechanicalHeart.getVal();
    }

    public static boolean isRebirthFlame(int itemId) { return itemId >= 2048700 && itemId < 2048800; }

    public static boolean isNebulite(int itemId) { return getItemPrefix(itemId) == 306; }

    public static boolean canEquipTypeHavePotential(int itemid) {
        return isRing(itemid) ||
                isPendant(itemid) ||
                isWeapon(itemid) ||
                isBelt(itemid) ||
                isHat(itemid) ||
                isFaceAccessory(itemid) ||
                isEyeAccessory(itemid) ||
                isOverall(itemid) ||
                isTop(itemid) ||
                isBottom(itemid) ||
                isShoe(itemid) ||
                isEarrings(itemid) ||
                isShoulder(itemid) ||
                isGlove(itemid) ||
                isEmblem(itemid) ||
                isBadge(itemid) ||
                isShield(itemid) ||
                isCape(itemid) ||
                isMechanicalHeart(itemid);
    }

    public static boolean canEquipHavePotential(Equip equip) {
        return !equip.isCash() &&
                canEquipTypeHavePotential(equip.getItemId()) &&
                !equip.isNoPotential() &&
                (ItemData.getEquipById(equip.getItemId()).getTuc() >= 1 || isTucIgnoreItem(equip.getItemId()));
    }

    public static boolean canEquipHaveFlame(Equip equip) {
        return !equip.isCash() && (isPendant(equip.getItemId()) ||
                (isWeapon(equip.getItemId()) && !isSecondary((equip.getItemId())) && !isShield((equip.getItemId()))) ||
                isBelt(equip.getItemId()) ||
                isHat(equip.getItemId()) ||
                isFaceAccessory(equip.getItemId()) ||
                isEyeAccessory(equip.getItemId()) ||
                isOverall(equip.getItemId()) ||
                isTop(equip.getItemId()) ||
                isBottom(equip.getItemId()) ||
                isShoe(equip.getItemId()) ||
                isEarrings(equip.getItemId()) ||
                Objects.equals(EXCEPTIONAL_EX_ALLOWED, equip.getItemId()) ||
                isGlove(equip.getItemId()) ||
                isCape(equip.getItemId()) ||
                isPocketItem(equip.getItemId()));
    }

    public static boolean canEquipGoldHammer(Equip equip) {
        Equip defaultEquip = ItemData.getEquipById(equip.getItemId());
        return !(Objects.equals(HORNTAIL_NECKLACE, equip.getItemId()) ||
                equip.getIuc() >= defaultEquip.getIUCMax() ||
                defaultEquip.getTuc() <= 0); // No upgrade slots by default
    }

    public static boolean isGoldHammer(Item item) {
        return getItemPrefix(item.getItemId()) == 247;
    }

    /**
     * Gets potential tier for a line.
     * Accounts prime lines too.
     * @param line Potential line.
     * @param grade Our current potential grade.
     * @param additionalPrimes How many extra prime lines this item has;
     */
    public static ItemGrade getLineTier(int line, ItemGrade grade, int additionalPrimes) {
        if (line == 0) {
            return grade;
        }
        if(additionalPrimes >= line){ //If cube has > 3 lines, still supports
            return grade;
        } else {
            return getOneTierLower(grade.getVal());
        }
    }

    /**
     * Gets prime line count based on cubeId
     * Used to manage the position of lines on the item throughout the cube method chain
     * @param cubeId used to grab which rate will be used;
     */
    public static int getAdditionalPrimeCountForCube(int cubeId) {
        int addedPrimeCount = 0;
        switch (cubeId){
            case VIOLET_CUBE: //Max of 5 additional since default has 1 prime
                //Independent Probability
                for(int j = 0; j < 5; j++){ //Max of 5 additional at lower chance
                    if(Util.succeedProp(VIOLET_CUBE_PRIME_LINE_CHANCE, 1000)){
                        addedPrimeCount++;
                    }
                }
                break;
            case BLACK_CUBE:
            case WHITE_BONUS_POTENTIAL_CUBE:
                if(Util.succeedProp(BOOSTED_PRIME_LINE_2_CHANCE, 1000)){ //Dependent Probability for 2 additional primes
                    addedPrimeCount++;
                    if(Util.succeedProp(BOOSTED_PRIME_LINE_3_CHANCE, 1000)){
                        addedPrimeCount++;
                    }
                }
                break;
            default:
                if(Util.succeedProp(DEFAULT_PRIME_LINE_2_CHANCE, 1000)){
                    addedPrimeCount++;
                    if(Util.succeedProp(DEFAULT_PRIME_LINE_3_CHANCE, 1000)){
                        addedPrimeCount++;
                    }
                }
                break;
        }

        return addedPrimeCount;
    }

    /**
     * Determines whether a nebulite can be mounted on an equip.
     * @param equip Equip item.
     * @param nebulite The nebulite to mount on the equip.
     */
    public static boolean nebuliteFitsEquip(Equip equip, Item nebulite) {
        int nebuliteId = nebulite.getItemId();
        Map<ScrollStat, Integer> vals = ItemData.getItemInfoByID(nebuliteId).getScrollStats();
        if (vals.size() == 0) {
            return false;
        }
        ItemOptionType type = ItemOptionType.getByVal(vals.getOrDefault(ScrollStat.optionType, 0));
        int equipId = equip.getItemId();
        switch(type) {
            case AnyEquip:
                return true;
            case Weapon: // no emblems for nebs here
                return isWeapon(equipId) || isShield(equipId);
            case AnyExceptWeapon:
                return !isWeapon(equipId) && !isShield(equipId);
            case Armor:
                return isBelt(equipId) || isHat(equipId) || isOverall(equipId) || isTop(equipId) || isBottom(equipId) || isShoe(equipId) || isCape(equipId);
            case Accessory:
                return isRing(equipId) || isPendant(equipId) || isFaceAccessory(equipId) || isEyeAccessory(equipId) || isEarrings(equipId) || isShoulder(equipId);
            case Hat:
                return isHat(equipId);
            case Top:
                return isTop(equipId) || isOverall(equipId);
            case Bottom:
                return isBottom(equipId) || isOverall(equipId);
            case Glove:
                return isGlove(equipId);
            case Shoes:
                return isShoe(equipId);
            default:
                return false;
        }
    }

    public static List<ItemOption> getOptionsByEquip(Equip equip, boolean bonus, int line, int cubeId, int additionalPrimes) {
        int id = equip.getItemId();
        Collection<ItemOption> data = ItemData.getFilteredItemOptions();
        ItemGrade grade = getLineTier(line, getGradeByVal(bonus ? equip.getBonusGrade() : equip.getBaseGrade()), additionalPrimes);
        // need a list, as we take a random item from it later on
        List<ItemOption> res = data.stream().filter(
                io -> io.getOptionType() == ItemOptionType.AnyEquip.getVal() &&
                io.hasMatchingGrade(grade.getVal()) && io.isBonus() == bonus
        ).collect(Collectors.toList());

        if (isShield(id) || isSecondary(id)) {
            res.addAll(data.stream().filter(
                    io -> (io.getOptionType() == ItemOptionType.Armor.getVal() || io.getOptionType() == ItemOptionType.Weapon.getVal())
                            &&  io.hasMatchingGrade(grade.getVal())
                            && io.isBonus() == bonus
                            && io.getId() != 42060 //Armor's Crit Damage (Secondary Specific Filter)
            ).collect(Collectors.toList()));
        } else if (isWeapon(id)) {
            res.addAll(data.stream().filter(
                    io -> io.getOptionType() == ItemOptionType.Weapon.getVal()
                            &&  io.hasMatchingGrade(grade.getVal())
                            && io.isBonus() == bonus
            ).collect(Collectors.toList()));
        } else if(isEmblem(id)){
            res.addAll(data.stream().filter(
                    io -> io.getOptionType() == ItemOptionType.Weapon.getVal()
                            &&  io.hasMatchingGrade(grade.getVal())
                            && io.isBonus() == bonus
                            && !io.getString().contains("Boss") //(Emblem Specific Filter)
            ).collect(Collectors.toList()));
        }

        else {
            res.addAll(data.stream().filter(
                    io -> io.getOptionType() == ItemOptionType.AnyExceptWeapon.getVal()
                            && io.hasMatchingGrade(grade.getVal())
                            && io.isBonus() == bonus
            ).collect(Collectors.toList()));

            if (isRing(id) || isPendant(id) || isFaceAccessory(id) || isEyeAccessory(id) || isEarrings(id)) {
                res.addAll(data.stream().filter(
                        io -> io.getOptionType() == ItemOptionType.Accessory.getVal()
                                && io.hasMatchingGrade(grade.getVal())
                                && io.isBonus() == bonus
                ).collect(Collectors.toList()));
            } else {
                if (isHat(id)) {
                    res.addAll(data.stream().filter(
                            io -> io.getOptionType() == ItemOptionType.Hat.getVal()
                                    && io.hasMatchingGrade(grade.getVal())
                                    && io.isBonus() == bonus
                    ).collect(Collectors.toList()));
                }
                if (isTop(id) || isOverall(id)) {
                    res.addAll(data.stream().filter(
                            io -> io.getOptionType() == ItemOptionType.Top.getVal()
                                    && io.hasMatchingGrade(grade.getVal())
                                    && io.isBonus() == bonus
                    ).collect(Collectors.toList()));
                }
                if (isBottom(id)) {
                    res.addAll(data.stream().filter(
                            io -> io.getOptionType() == ItemOptionType.Bottom.getVal()
                                    && io.hasMatchingGrade(grade.getVal())
                                    && io.isBonus() == bonus
                    ).collect(Collectors.toList()));
                }
                if (isShoe(id)) {
                    res.addAll(data.stream().filter(
                            io -> io.getOptionType() == ItemOptionType.Shoes.getVal()
                                    && io.hasMatchingGrade(grade.getVal())
                                    && io.isBonus() == bonus
                    ).collect(Collectors.toList()));
                }
                if(isGlove(id)){
                    if(grade == HiddenUnique || grade == Unique || grade == HiddenLegendary || grade == Legendary || grade == UniqueBonusHidden || grade == LegendaryBonusHidden){
                        res.addAll(data.stream().filter(io ->
                                (io.getOptionType() == ItemOptionType.Glove.getVal() || io.getOptionType() == ItemOptionType.Armor.getVal())
                                        && io.hasMatchingGrade(grade.getVal()) && io.isBonus() == bonus
                                        && io.getId() != 42060 //Bonus - Armor's 1% Crit Damage (Glove Specific Filter)
                        ).collect(Collectors.toList()));
                        if(!MEISTERS_CUBES.contains(cubeId) && !MASTER_CRAFTSMANS_CUBES.contains(cubeId) && (cubeId != SYSTEM_DEFAULT_CUBE_INDICATOR)){
                            res = res.stream().filter(
                                    io -> !io.getString().contains("Auto Steal")) //(Glove Specific Filter)
                                    .collect(Collectors.toList());
                        }
                    } else {
                        res.addAll(data.stream().filter(
                                io -> io.getOptionType() == ItemOptionType.Glove.getVal()
                                        && io.hasMatchingGrade(grade.getVal())
                                        && io.isBonus() == bonus
                        ).collect(Collectors.toList()));
                    }
                } else if (isArmor(id) || isShoulder(id) || isBelt(id)) {
                    res.addAll(data.stream().filter(
                            io -> io.getOptionType() == ItemOptionType.Armor.getVal()
                                    && io.hasMatchingGrade(grade.getVal())
                                    && io.isBonus() == bonus
                    ).collect(Collectors.toList()));
                }
            }
        }
        return res.stream().filter(io -> io.getReqLevel() <= equip.getrLevel() + equip.getiIncReq()).collect(Collectors.toList());
    }

    public static List<Integer> getWeightedOptionsByEquip(Equip equip, boolean bonus, int line, int cubeId, int additionalPrimes) {
        List<Integer> res = new ArrayList<>();
        List<ItemOption> data = getOptionsByEquip(equip, bonus, line, cubeId, additionalPrimes);
        for(ItemOption io : data) {
            for (int i = 0; i < io.getWeight(); i++) {
                res.add(io.getId());
            }
        }
        return res;
    }

    public static int getRandomOption(Equip equip, boolean bonus, int line, int cubeId, int additionalPrimes) {
        List<Integer> data = getWeightedOptionsByEquip(equip, bonus, line, cubeId, additionalPrimes);
        return data.get(Util.getRandom(data.size()));
    }

    public static int getTierUpChance(int id, ItemGrade grade) {
        int[] rateArray;
        if(id == ItemConstants.RED_CUBE){
            rateArray = ItemConstants.RED_CUBE_TIER_UP_RATES;
        }
        else if(id == ItemConstants.BLACK_CUBE){
            rateArray = ItemConstants.BLACK_CUBE_TIER_UP_RATES;
        }
        else if(id == ItemConstants.VIOLET_CUBE){
            rateArray = ItemConstants.VIOLET_CUBE_TIER_UP_RATES;
        }
        else if(MEISTERS_CUBES.contains(id)){
            rateArray = ItemConstants.MEISTERS_CUBE_TIER_UP_RATES;
        }
        else if(MASTER_CRAFTSMANS_CUBES.contains(id)){
            rateArray = ItemConstants.MASTER_CRAFTSMANS_CUBE_TIER_UP_RATES;
        }
        else if(OCCULT_CUBES.contains(id)){
            rateArray = ItemConstants.OCCULT_CUBE_TIER_UP_RATES;
        }

        else if(BONUS_OCCULT_CUBES.contains(id)){
            rateArray = ItemConstants.BONUS_OCCULT_CUBE_TIER_UP_RATES;
        }
        else if(id == ItemConstants.BONUS_POTENTIAL_CUBE || id == ItemConstants.SPECIAL_BONUS_POTENTIAL_CUBE){
            rateArray = ItemConstants.BONUS_POT_CUBE_TIER_UP_RATE;
        }

        else if(id == ItemConstants.WHITE_BONUS_POTENTIAL_CUBE){
            rateArray = ItemConstants.WHITE_BONUS_POT_CUBE_TIER_UP_RATE;
        }
        else{
            rateArray = ItemConstants.DEFAULT_CUBE_TIER_UP_RATES;
        }

        switch (grade){
            case Rare:
            case HiddenRare:
            case RareBonusHidden:
                return rateArray[0] * GLOBAL_TIER_UP_MULTIPLIER;
            case Epic:
            case HiddenEpic:
            case EpicBonusHidden:
                return rateArray[1] * GLOBAL_TIER_UP_MULTIPLIER;
            case Unique:
            case HiddenUnique:
            case UniqueBonusHidden:
                return rateArray[2] * GLOBAL_TIER_UP_MULTIPLIER;
            case Hidden:
            case None:
            case RareSecondary:
            case LegendaryBonusHidden:
            case HiddenLegendary:
            case Legendary:
            default:
                return 0;
        }
    }


    public static boolean isEquip(int id) {
        return id / 1000000 == 1;
    }

    public static boolean isClaw(int id) {
        return getItemPrefix(id) == 147;
    }

    public static boolean isBow(int id) {
        return getItemPrefix(id) == 145;
    }

    public static boolean isXBow(int id) {
        return getItemPrefix(id) == 146;
    }

    public static boolean isGun(int id) {
        return getItemPrefix(id) == 149;
    }

    public static boolean isXBowArrow(int id) {
        return id / 1000 == 2061;
    }

    public static InvType getInvTypeByItemID(int itemID) {
        if(isEquip(itemID)) {
            return EQUIP;
        } else {
            ItemInfo ii = ItemData.getItemInfoByID(itemID);
            if(ii == null) {
                return null;
            }
            return ii.getInvType();
        }
    }

    public static Set<Integer> getRechargeablesList() {
        Set<Integer> itemList = new HashSet<>();
        // all throwing stars
        for(int i = 2070000; i <= 2070016; i++) {
            itemList.add(i);
        }
//        itemList.add(2070018);
        itemList.add(2070019);//手裡劍-魔
        itemList.add(2070023);
        itemList.add(2070024);
        itemList.add(2070026);
        // all bullets
        for(int i = 2330000; i <= 2330006; i++) {
            itemList.add(i);
        }
        itemList.add(2330007);//貫穿裝甲的特製子彈
        itemList.add(2330008);
        itemList.add(2330016);
        itemList.add(2331000);
        itemList.add(2332000);
        return itemList;
    }

    public static boolean isRechargable(int itemId) {
        return isThrowingStar(itemId) || isBullet(itemId);
    }

    public static int getDamageSkinIDByItemID(int itemID) {
        switch(itemID) {
            case 2431965: // Basic Damage Skin:
                return 0;
            case 2431966: // Digitalized Damage Skin:
            case 2432084: // Digitalized Damage Skin
                return 1;
            case 2431967: // Kritias Damage Skin:
                return 2;
            case 2432131: // Party Quest Damage Skin:
                return 3;
            case 2432153: // Hard-Hitting Damage Skin:
            case 2432638: // Hard-Hitting Damage Skin
            case 2432659: // Hard-Hitting Damage Skin
                return 4;
            case 2432154: // Sweet Tea Cake Damage Skin:
            case 2432637: // Sweet Tea Cake Damage Skin
            case 2432658: // Sweet Tea Cake Damage Skin
                return 5;
            case 2432207: // Club Henesys Damage Skin:
                 return 6;
            case 2432354: // Merry Christmas Damage Skin:
                 return 7;
            case 2432355: // Snowflake Damage Skin:
            case 2432972: // Snowflake Damage Skin
                 return 8;
            case 2432465: // Alicia's Damage Skin:
                 return 9;
            case 2432479: // Dorothy Damage Skin:
                 return 10;
            case 2432526: // Keyboard Warrior Damage Skin:
            case 2432639: // Keyboard Warrior Damage Skin
            case 2432660: // Keyboard Warrior Damage Skin
                 return 11;
            case 2432532: // Gentle Springtime Breeze Damage Skin:
                 return 12;
            case 2432592: // Singles Army Damage Skin:
                 return 13;
            case 2432640: // Reminiscence Damage Skin:
            case 2432661: // Reminiscence Damage Skin
                 return 14;
            case 2432710: // Orange Mushroom Damage Skin:
                 return 15;
            case 2432836: // Crown Damage Skin:
                 return 16;
            case 2432973: // Monotone Damage Skin:
                 return 17;
            case 2433063: // Star Planet Damage Skin:
                 return 18;
            case 2433456: // Hangul Day Damage Skin:
                 return 19;
            case 2433178: // Halloween Damage Skin (Bones):
                 return 20;
            // case 2433456: // Hangul Day Damage Skin (already in): 21 - (19)
            case 2433631: // Nene Chicken Damage Skin:
                 return 22;
            case 2433715: // Striped Damage Skin:
                 return 23;
            case 2433804: // Couples Army Damage Skin:
                 return 24;
            case 5680343: // Star Damage Skin:
                 return 25;
            case 2433913: // Yeti and Pepe Damage Skin:
                 return 26;
            case 2433980: // Slime and Mushroom Damage Skin:
                 return 27;
            case 2433981: // Pink Bean Damage Skin:
                 return 28;
            case 2433990: // Pig Bar Damage Skin:
                 return 29;
            // case 2432659: // Hard-Hitting Damage Skin (already in): 30 - (4)
            // case 2432526: // Keyboard Warrior Damage Skin (already in): 31 - (11)
            // case 2432710: // Orange Mushroom Damage Skin (already in): 32 - (15)
            // case 2432355: // Snowflake Damage Skin (already in): 33 - (8)
            case 2434248: // Rainbow Boom Damage Skin:
                 return 34;
            case 2433362: // Night Sky Damage Skin:
            case 2433900: // Night Sky Damage Skin
                 return 35;
            case 2434274: // Marshmallow Damage Skin:
                 return 36;
            case 2434289: // Mu Lung Dojo Damage Skin:
                 return 37;
            case 2434390: // Teddy Damage Skin:
                 return 38;
            case 2434391: // Mighty Ursus Damage Skin:
                 return 39;
            case 5680395: // Scorching Heat Damage Skin:
                 return 40;
            case 2434528: // USA Damage Skin:
                 return 41;
            case 2434529: // Churro Damage Skin:
                 return 42;
            case 2434530: // Singapore Night Damage Skin:
                 return 43;
            case 2433571: // Scribble Crush Damage Skin:
                 return 44;
            case 2434374: // Autumn Festival Full Moon Damage Skin:
            case 2434574: // Full Moon Damage Skin
                 return 45;
            case 2433828: // White Heaven Sun Damage Skin:
            case 2434575: // Sunny Damage Skin
            case 2435171: // Sunny Damage Skin
                 return 46;
            case 2432804: // Princess No Damage Skin:
                 return 47;
            case 2434654: // Murgoth Damage Skin:
                 return 48;
            case 2435326: // Nine-Tailed Fox Damage Skin:
                 return 49;
            case 2432749: // Zombie Damage Skin:
                 return 50;
            case 2434710: // MVP Special Damage Skin:
                 return 51;
            case 2433777: // Black Heaven Damage Skin:
                 return 52;
            case 2434824: // Monster Park Damage Skin:
                 return 53;
            // case 2431966: // Digital Damage Skin (already in): 54 - (1)
            // case 2431967: // Kritias Damage Skin (already in): 55 - (2)
            // case 2432154: // Sweet Tea Cake Damage Skin (already in): 56 - (5)
            // case 2432354: // Merry Christmas Damage Skin (already in): 57 - (7)
            // case 2432532: // Gentle Springtime Breeze Damage skin (already in): 58 - (12)
            // case 2433715: // Striped Damage Skin (already in): 59 - (23)
            // case 5680343: // Star Damage Skin (already in): 60 - (25)
            // case 2433913: // Yeti and Pepe Damage Skin (already in): 61 - (26)
            // case 2433980: // Slime and Mushroom Damage Skin (already in): 62 - (27)
            // case 2434248: // Rainbow Boom Damage Skin (already in): 63 - (34)
            // case 2433362: // Night Sky Damage Skin (already in): 64 - (35)
            // case 2434274: // Marshmallow Damage Skin (already in): 65 - (36)
            // case 2434390: // Teddy Damage Skin (already in): 66 - (38)
            // case 5680395: // Scorching Heat Damage Skin (already in): 67 - (40)
            // case 2434528: // USA Damage Skin (already in): 68 - (41)
            // case 2434529: // Churro Damage Skin (already in): 69 - (42)
            // case 2434530: // Singapore Night Damage Skin (already in): 70 - (43)
            // case 2433571: // Scribble Crush Damage Skin (already in): 71 - (44)
            // case 2434574: // Full Moon Damage Skin (already in): 72 - (45)
            // case 2433828: // White Heaven Sun Damage Skin (already in): 73 - (46)
            case 2434662: // Jelly Bean Damage Skin:
                return 74;
            case 2434664: // Soft Serve Damage Skin:
                return 75;
            case 2434868: // Christmas Lights Damage skin:
                return 76;
            case 2435043: // Heroes Phantom Damage Skin:
            case 2435665: // Heroes Phantom Damage Skin
            case 2436041: // Heroes Phantom Damage Skin
                return 77;
            case 2435044: // Heroes Mercedes Damage Skin:
            case 2435666: // Heroes Mercedes Damage Skin
            case 2436042: // Heroes Mercedes Damage Skin
                return 78;
            case 2435046: // Fireworks Damage Skin:
                return 79;
            case 2435047: // Heart Balloon Damage Skin:
                return 80;
            case 2435140: // Neon Sign Damage Skin:
            case 2435836: // Neon Sign Damage Skin
                return 81;
            case 2435141: // Freeze Tag Damage Skin:
                return 82;
            case 2435179: // Candy Damage Skin:
                return 83;
            case 2435162: // Antique Gold Damage Skin:
                return 84;
            case 2435157: // Calligraphy Damage Skin:
                return 85;
            case 2435158: // Explosion Damage Skin:
            case 2435835: // Explosion Damage Skin
                return 86;
            case 2435159: // Snow-wing Damage Skin:
                return 87;
            case 2435160: // Miho Damage Skin:
            case 2436044: // Miho Damage Skin
                return 88;
            case 2434663: // Donut Damage Skin:
                return 89;
            case 2435182: // Musical Score Damage Skin:
                return 90;
            case 2435166: // Moon Bunny Damage Skin:
                return 91;
            case 2435184: // Forest of Tenacity Damage Skin:
                return 92;
            case 2435222: // Festival Tortoise Damage Skin:
                return 93;
            // Damage skins past this point until 1000 don't exist on v176
            case 2435293: // April Fools' Damage Skin:
                return 94;
            case 2435313: // Blackheart Day Damage Skin:
                return 95;
            case 2435331: // Bubble April Fools' Damage Skin:
                return 96;
            case 2435332: // Retro April Fools' Damage Skin:
                return 97;
            case 2435333: // Monochrome April Fools' Damage Skin:
                return 98;
            case 2435334: // Sparkling April Fools' Damage Skin:
                return 99;
            case 2435316: // Haste Damage Skin:
                return 100;
            case 2435408: // 13th Anniversary Maple Leaf Damage Skin:
                return 101;
            case 2435427: // Cyber Damage Skin:
                return 102;
            case 2435428: // Cosmic Damage Skin:
                return 103;
            case 2435429: // Choco Donut Damage Skin:
                return 104;
            // case 2435456: // Lovely Damage Skin (already in): 105 - (1028)
            case 2435493: // Monster Balloon Damage Skin:
                return 106;
            // case 2435331: // Bubble April Fools' Damage Skin (already in): 107 - (96)
            // case 2435334: // Sparkling April Fools' Damage Skin (already in): 108 - (99)
            case 2435424: // Henesys Damage Skin:
                return 109;
            case 2435425: // Leafre Damage Skin:
                return 110;
            case 2435431: // Algebraic Damage Skin:
                return 111;
            case 2435430: // Blue Fire Damage Skin:
                return 112;
            case 2435432: // Purple Damage Skin:
                return 113;
            case 2435433: // Nanopixel Damage Skin:
                return 114;
            // case 2434601: // Invisible Damage Skin (already in): 115 - (1050)
            case 2435521: // Crystal Damage Skin:
                return 116;
            // case 2435196: // Crow Damage Skin: (already in): 117 - (1079)
            case 2435523: // Chocolate Damage Skin:
                return 118;
            case 2435524: // Spark Damage Skin:
                return 119;
            case 2435538: // Royal Damage Skin:
                return 120;
            case 2435832: // Chrome Damage Skin (Ver.1):
                return 121;
            case 2435833: // Neon Lights Damage Skin:
                return 122;
            case 2435839: // Cosmic Damage Skin (Cards):
                return 123;
            case 2435840: // Gilded Damage Skin:
                return 124;
            case 2435841: // Batty Damage Skin:
                return 125;
            case 2435849: // Monochrome April Fools' Damage Skin:
                return 126;
            case 2435972: // Vanishing Journey Damage Skin:
                return 127;
            case 2436023: // Chu Chu Damage Skin:
                return 128;
            case 2436024: // Lachelein Damage Skin:
                return 129;
            case 2436026: // Poison flame Damage Skin:
                return 130;
            case 2436027: // Blue Strike Damage Skin:
                return 131;
            case 2436028: // Music Power Damage Skin:
                return 132;
            case 2436029: // Collage Power Damage Skin:
                return 133;
            case 2436045: // Starlight Aurora Damage Skin:
                return 134;
            // End of post v176 native damage skins; begin 1000+/overseas damage skins
            case 2432591: // Cherry Blossoms Damage Skin:
                return 1000;
            case 2433269: // Golden Damage Skin:
                return 1001;
            case 2433267: // Blood Damage Skin:
                return 1002;
            case 2433268: // Zombie Damage Skin:
                return 1003;
            // case 2432803: // Princess No Damage Skin (already in): 1004 - (47)
            case 2433270: // Jett Damage Skin:
                return 1005;
            case 2433081: // Halloween Damage Skin (Blood):
                return 1006;
            case 2433901: // Beasts of Fury Damage Skin:
                return 1007;
            case 2433113: // Chinese Marshmallow Damage Skin:
                return 1008;
            case 2433236: // Chalk Damage Skin:
            case 2433899: // Chalk Damage Skin
                return 1010;
            case 2433252: // Dragon's Fire Damage Skin:
                return 1011;
            case 2433251: // Violetta's Charming Damage Skin:
                return 1012;
            case 2433183: // Super Spooky Damage Skin:
                return 1015;
            case 2433184: // Wicked Witch Damage Skin:
                return 1016;
            case 2433214: // Black and White Damage Skin:
                return 1017;
            case 2433182: // Jack o' Lantern Damage Skin:
                return 1018;
            // case 2433236: // Chalk Damage Skin (already in): 1019 - (1010)
            // case 2433900: // Night Sky Damage Skin (already in): 1020 - (35)
            // case 2433902: // Beasts of Fury Damage Skin (already in): 1021 - (1007)
            // case 2433588: // Chinese Fireworks Damage Skin (already in): 1023 - (79)
            case 2433907: // Antique Fantasy Damage Skin:
                return 1024;
            // case 2433906: // Scribble Crush Damage Skin (already in): 1025 - (44)
            // case 2433905: // Heart Balloon Damage Skin (already in): 1026 - (80)
            case 2433904: // Dried Out Damage Skin:
                return 1027;
            case 2433903: // Lovely Damage Skin:
                return 1028;
            case 2435511: // Remnant of the Goddess Damage Skin:
                return 1029;
            case 2433709: // Moon Bunny Damage Skin (Overseas):
                return 1030;
            // case 2433777: // Black Heaven Damage Skin (already in): 1031 - (52)
            case 2433775: // Orchid Damage Skin:
                return 1032;
            case 2433776: // Lotus Damage Skin:
                return 1033;
            // case 2433828: // White Heaven Sun Damage Skin (already in): 1034 - (46)
            case 2433829: // White Heaven Rain Damage Skin:
                return 1035;
            case 2433830: // White Heaven Rainbow Damage Skin:
                return 1036;
            case 2433831: // White Heaven Snow Damage Skin:
                return 1037;
            case 2433832: // White Heaven Lightning Damage Skin:
                return 1038;
            case 2433833: // White Heaven Wind Damage Skin:
                return 1039;
            case 2433883: // Earth Day Damage Skin:
                return 1040;
            case 2434004: // Alishan Damage Skin:
                return 1041;
            case 2434147: // Irena's Band Damage Skin:
                return 1042;
            case 2434157: // Damien's Band Damage Skin:
                return 1043;
            case 2434375: // Bonfire Damage Skin:
                return 1045;
            // case 2434574: // Full Moon Damage Skin (already in): 1048 - (45)
            case 2434499: // Autumn Festival Fireworks Damage Skin:
                return 1049;
            case 2434601: // Invisible Damage Skin:
                return 1050;
            // case 2434533: // Blood Damage Skin (already in): 1051 - (1002)
            // case 2434534: // Zombie Damage Skin (already in): 1052 - (1003)
            case 2434544: // Kanna Damage Skin:
                return 1053;
            case 2434545: // Hayato Damage Skin:
                return 1054;
            case 2434600: // Gourmet Damage Skin:
                return 1055;
            case 2434570: // Tot's Damage Skin:
                return 1056;
            // case 2434619: // Nine-Tailed Fox Damage Skin (already in): 1057 - (88)
            // case 2434662: // Jelly Beans Damage Skin (already in): 1058 - (74)
            // case 2434663: // Donut Damage Skin (already in): 1059 - (89)
            // case 2434664: // Soft Serve Damage Skin (already in): 1060 - (75)
            // case 2434868: // Christmas Lights Damage Skin (already in): 1062 - (76)
            case 2434871: // Chess Damage Skin:
                return 1063;
            // case 2435182: // Musical Score Damage Skin (already in): 1064 - (90)
            case 2435380: // Math Symbol Damage Skin:
                return 1065;
            case 2435381: // Secret Sign Damage Skin:
            case 2435638: // Secret Sign Damage Skin
            case 2435918: // Secret Damage Skin(Special character)
                return 1066;
            case 2435382: // Secret Question Mark Damage Skin:
                return 1067;
            case 2434817: // Cube Damage Skin:
                return 1068;
            case 2434818: // One Winter Night Damage Skin:
                return 1069;
            case 2435374: // Monkey Damage Skin
                return 1070;
            case 2435194: // Crimson Knight Damage Skin
                return 1075;
            case 2435195: // Corrupted Magician Damage Skin
                return 1076;
            case 2435193: // Krakian Damage Skin
                return 1077;
            case 2435196: // Crow Damage Skin
                return 1079;
            case 2435213: // Antellion Damage Skin
                return 1080;
            case 2435335: // Candles Damage Skin
                return 1081;
            case 2435336: // Cupcakes Damage Skin
                return 1082;
            case 2435486: // TuTu Damage Skin
                return 1083;
            case 2435487: // Nene Damage Skin
                return 1084;
            case 2435488: // Lingling Damage Skin
                return 1085;
            case 2435489: // Sheriff Damage Skin
                return 1086;
            case 2435548: // kkk Damage Skin
                return 1087;
            case 2435549: // Hahaha Damage Skin
                return 1088;
            case 2435542: // www Damage Skin
                return 1089;
            case 2435543: // Epic Lulz Damage Skin
                return 1090;
            case 2435544: // Kekeke Damage Skin
                return 1091;
            case 2435545: // Summer Damage Skin
                return 1092;
            case 2435546: // Blaster Damage Skin
                return 1093;
            case 2435568: // Heroes Shade Damage Skin
                return 1094;
            case 2435565: // Heroes Aran Damage Skin
                return 1095;
            case 2435566: // Heroes Luminous Damage Skin
                return 1096;
            case 2435567: // Heroes Evan Damage Skin
                return 1097;
            case 2435673: // Cygnus Water Warrior Damage Skin
                return 1098;
            case 2435674: // Resistance Water Warrior Damage Skin
                return 1099;
            case 2435802: // Dragon Fireworks Damage Skin
                return 1100;
            case 2435905: // Cat Paw Damage Skin
                return 1114;
            case 2435906: // Cat Face Damage Skin
                return 1115;
            // case 2435907: // Cat Paw Damage Skin (already in): 1116 - (1114)
            // case 2435908: // Cat Face Damage Skin (already in): 1117 - (1115)
            case 2435948: // Halloween Town Damage Skin
                return 1118;
            case 2435949: // Too Spooky Damage Skin
                return 1119;
            case 2435950: // Floofy Bichon Damage Skin
                return 1120;
            // case 2435951: // Halloween Town Damage Skin (already in): 1121 - (1118)
            // case 2435952: // Too Spooky Damage Skin (already in): 1122 - (1119)
            // case 2435953: // Floofy Bichon Damage Skin (already in): 1123 - (1120)
            case 2436089: // Highlighter Damage Skin
                return 1132;
            default:
                return 0;
        }
    }

    public static boolean isMasteryBook(int itemId) {
        return getItemPrefix(itemId) == 229;
    }

    public static boolean isPet(int itemId) {
        return getItemPrefix(itemId) == 500;
    }

    public static boolean isSoulEnchanter(int itemID) {
        return itemID / 1000 == 2590;
    }

    public static boolean isSoul(int itemID) {
        return itemID / 1000 == 2591;
    }

    public static short getSoulOptionFromSoul(int itemId) {
        short id = 0;
        switch(itemId) {

        }
        return id;
    }

    public static int getRandomSoulOption() {
        return Util.getRandomFromCollection(soulPotList);
    }

    public static int getSoulSkillFromSoulID(int soulID) {
        int itemId = soulID + SOUL_ITEM_BASE_ID - 1;
        return EtcData.getSkillBySoulItem(itemId);
    }

    public static boolean isMobCard(int itemID) {
        return getItemPrefix(itemID) == 238;
    }

    public static boolean isCollisionLootItem(int itemID) {
        switch (itemID) {
            case 2023484: // Blue
            case 2023494: // Purple
            case 2023495: // Red
            case 2023669: // Gold
                return true;

            default:
                return false;
        }
    }

    public static boolean isUpgradable(int itemID) {
        BodyPart bodyPart = BodyPart.getByVal(getBodyPartFromItem(itemID, 0));
        if (bodyPart == null || getItemPrefix(itemID) == EquipPrefix.SecondaryWeapon.getVal()) {
            return false;
        }
        switch (bodyPart) {
            case Ring1:
            case Ring2:
            case Ring3:
            case Ring4:
            case Pendant:
            case ExtendedPendant:
            case Weapon:
            case Belt:
            case Hat:
            case FaceAccessory:
            case EyeAccessory:
            case Top:
            case Bottom:
            case Shoes:
            case Earrings:
            case Shoulder:
            case Gloves:
            case Badge:
            case Shield:
            case Cape:
            case MechanicalHeart:
                return true;
            default:
                return false;
        }
    }

    public static List<ScrollUpgradeInfo> getScrollUpgradeInfosByEquip(Equip equip) {
        // not the most beautiful way to do this, but I'd like to think that it's pretty easy to understand
        BodyPart bp = BodyPart.getByVal(ItemConstants.getBodyPartFromItem(equip.getItemId(), 0));
        List<ScrollUpgradeInfo> scrolls = new ArrayList<>();
        int rLevel = equip.getrLevel() + equip.getiIncReq();
        int rJob = equip.getrJob();
        Set<EnchantStat> possibleStat = new HashSet<>();
        int plusFromLevel;
        int[] chances;
        int[] attStats = new int[0];
        int[] stat;
        int[] armorHp = new int[]{5, 20, 30, 70, 120};
        int[] armorDef = new int[]{1, 2, 4, 7, 10};
        boolean armor = false;
        if (bp == BodyPart.Weapon) {
            plusFromLevel = rLevel >= 120 ? 2 : rLevel >= 60 ? 1 : 0;
            if ((rJob & RequiredJob.Warrior.getVal()) > 0) { // warrior
                possibleStat.add(EnchantStat.PAD);
                possibleStat.add(EnchantStat.STR);
                possibleStat.add(EnchantStat.MHP);
            } else if ((rJob & RequiredJob.Magician.getVal()) > 0) { // mage
                possibleStat.add(EnchantStat.MAD);
                possibleStat.add(EnchantStat.INT);
            } else if ((rJob & RequiredJob.Bowman.getVal()) > 0) { // bowman
                possibleStat.add(EnchantStat.PAD);
                possibleStat.add(EnchantStat.DEX);
            } else if ((rJob & RequiredJob.Thief.getVal()) > 0 || (rJob & RequiredJob.Pirate.getVal()) > 0) { // thief/pirate
                possibleStat.add(EnchantStat.PAD);
                possibleStat.add(EnchantStat.STR);
                possibleStat.add(EnchantStat.DEX);
                possibleStat.add(EnchantStat.LUK);
            } else {
                possibleStat.add(EnchantStat.PAD);
                possibleStat.add(EnchantStat.MAD);
                possibleStat.add(EnchantStat.STR);
                possibleStat.add(EnchantStat.DEX);
                possibleStat.add(EnchantStat.INT);
                possibleStat.add(EnchantStat.LUK);
                possibleStat.add(EnchantStat.MHP);
            }
            chances = new int[]{100, 70, 30, 15};
            attStats = new int[]{1, 2, 3, 5, 7, 9};
            stat = new int[]{0, 0, 1, 2, 3, 4};
        } else if (bp == BodyPart.Gloves) {
            plusFromLevel = rLevel <= 70 ? 0 : 1;
            if ((rJob & RequiredJob.Magician.getVal()) > 0) {
                possibleStat.add(EnchantStat.MAD);
            } else {
                possibleStat.add(EnchantStat.PAD);
            }
            possibleStat.add(EnchantStat.PDD);
            possibleStat.add(EnchantStat.MDD);
            chances = new int[]{100, 70, 30};
            attStats = new int[]{0, 1, 2, 3};
            stat = new int[]{3, 0, 0, 0};
        } else if (ItemConstants.isAccessory(equip.getItemId())) {
            plusFromLevel = rLevel >= 120 ? 2 : rLevel >= 60 ? 1 : 0;
            if ((rJob & RequiredJob.Warrior.getVal()) > 0) { // warrior
                possibleStat.add(EnchantStat.STR);
                possibleStat.add(EnchantStat.MHP);
            } else if ((rJob & RequiredJob.Magician.getVal()) > 0) { // mage
                possibleStat.add(EnchantStat.INT);
            } else if ((rJob & RequiredJob.Bowman.getVal()) > 0) { // bowman
                possibleStat.add(EnchantStat.DEX);
            } else if ((rJob & RequiredJob.Thief.getVal()) > 0 || (rJob & RequiredJob.Pirate.getVal()) > 0) { // thief/pirate
                possibleStat.add(EnchantStat.STR);
                possibleStat.add(EnchantStat.DEX);
                possibleStat.add(EnchantStat.LUK);
            } else {
                possibleStat.add(EnchantStat.STR);
                possibleStat.add(EnchantStat.DEX);
                possibleStat.add(EnchantStat.INT);
                possibleStat.add(EnchantStat.LUK);
                possibleStat.add(EnchantStat.MHP);
            }
            chances = new int[]{100, 70, 30};
            stat = new int[]{1, 1, 2, 3, 5};
        } else {
            armor = true;
            plusFromLevel = rLevel >= 120 ? 2 : rLevel >= 60 ? 1 : 0;
            if ((rJob & RequiredJob.Warrior.getVal()) > 0) { // warrior
                possibleStat.add(EnchantStat.STR);
                possibleStat.add(EnchantStat.MHP);
            } else if ((rJob & RequiredJob.Magician.getVal()) > 0) { // mage
                possibleStat.add(EnchantStat.INT);
            } else if ((rJob & RequiredJob.Bowman.getVal()) > 0) { // bowman
                possibleStat.add(EnchantStat.DEX);
            } else if ((rJob & RequiredJob.Thief.getVal()) > 0 || (rJob & RequiredJob.Pirate.getVal()) > 0) { // thief/pirate
                possibleStat.add(EnchantStat.STR);
                possibleStat.add(EnchantStat.DEX);
                possibleStat.add(EnchantStat.LUK);
            } else {
                possibleStat.add(EnchantStat.STR);
                possibleStat.add(EnchantStat.DEX);
                possibleStat.add(EnchantStat.INT);
                possibleStat.add(EnchantStat.LUK);
                possibleStat.add(EnchantStat.MHP);
            }
            chances = new int[]{100, 70, 30};
            stat = new int[]{1, 2, 3, 5, 7};
        }
        for (int i = 0; i < chances.length; i++) { // 4 scroll tiers for weapons
            int tier = i + plusFromLevel;
            TreeMap<EnchantStat, Integer> stats = new TreeMap<>();
            for (EnchantStat es : possibleStat) {
                int val;
                if (es.isAttackType()) {
                    val = attStats[tier];
                } else if (es.isHpOrMp()){
                    val = stat[tier] * 50;
                } else {
                    val = stat[tier];
                }
                if (val != 0) {
                    stats.put(es, val);
                }
            }
            if (armor) {
                stats.put(EnchantStat.PDD, armorDef[tier] + stats.getOrDefault(EnchantStat.PDD, 0));
                stats.put(EnchantStat.MDD, armorDef[tier] + stats.getOrDefault(EnchantStat.MDD, 0));
                stats.put(EnchantStat.MHP, armorHp[tier] + stats.getOrDefault(EnchantStat.MHP, 0));
            }
            String title = chances[i] + "% ";
            title += bp == BodyPart.Weapon ? "Attack" : "Stat";
            ScrollUpgradeInfo sui = new ScrollUpgradeInfo(i, title, SpellTraceScrollType.Normal, 0, stats,
                    BASE_ST_COST + rLevel * (tier + 1), chances[i]);
            scrolls.add(sui);
        }
        if (equip.hasUsedSlots()) {
            scrolls.add(new ScrollUpgradeInfo(4, "Innocence Scroll 30%",
                    SpellTraceScrollType.Innocence, 0, new TreeMap<>(), INNOCENCE_ST_COST, 30));
            scrolls.add(new ScrollUpgradeInfo(5, "Clean Slate Scroll 5%",
                    SpellTraceScrollType.CleanSlate, 0, new TreeMap<>(), CLEAN_SLATE_ST_COST, 5));
        }
        return scrolls;
    }

    // is_tuc_ignore_item(int nItemID)
    static boolean isTucIgnoreItem(int itemID) {
        return (isSecondary(itemID) || isEmblem(itemID) || Objects.equals(TUC_IGNORE_ITEMS, itemID));
    }

    public static PetSkill getPetSkillFromID(int itemID) {
        switch (itemID) {
            case 5190000:
                return PetSkill.ITEM_PICKUP;
            case 5190001:
                return PetSkill.AUTO_HP;
            case 5190002:
                return PetSkill.EXPANDED_AUTO_MOVE;
            case 5190003:
                return PetSkill.AUTO_MOVE;
            case 5190004:
                return PetSkill.EXPIRED_PICKUP;
            case 5190005:
                return PetSkill.IGNORE_ITEM;
            case 5190006:
                return PetSkill.AUTO_MP;
            case 5190007:
                return PetSkill.RECALL;
            case 5190008:
                return PetSkill.AUTO_SPEAKING;
            case 5190009:
                return PetSkill.AUTO_ALL_CURE;
            case 5190010:
                return PetSkill.AUTO_BUFF;
            case 5190011:
                return PetSkill.AUTO_FEED;
            case 5190012:
                return PetSkill.FATTEN_UP;
            case 5190013:
                return PetSkill.PET_SHOP;
            case 5190014:
                return PetSkill.FATTEN_UP;
            case 5191000:
                return PetSkill.ITEM_PICKUP;
            case 5191001:
                return PetSkill.AUTO_HP;
            case 5191002:
                return PetSkill.EXPANDED_AUTO_MOVE;
            case 5191003:
                return PetSkill.ITEM_PICKUP;
        }
        return null;
    }

    // Gets the hardcoded starforce capacities Nexon introduced for equips above level 137.
    // The cap for stars is in GetHyperUpgradeCapacity (E8 ? ? ? ? 0F B6 CB 83 C4 0C, follow `call`),
    // therefore it needs to be manually implemented on the server side.
    // Nexon's decision was very poor, but will require client edits to revert.
    static int getItemStarLimit(int itemID) {
        switch (itemID) {
            case 1072870: // Sweetwater Shoes
            case 1082556: // Sweetwater Gloves
            case 1102623: // Sweetwater Cape
            case 1132247: // Sweetwater Belt
                if (ServerConstants.VERSION >= 197) {
                    return 15;
                }
            case 1182060: // Ghost Ship Exorcist
            case 1182273: // Sengoku Hakase Badge
                if (ServerConstants.VERSION >= 199) {
                    return 22;
                }
        }
        return ServerConstants.VERSION >= 197 ? 25 : 15;
    }

    public static int getEquippedSummonSkillItem(int itemID, short job) {
        switch (itemID) {
            case 1112585:// Angelic Blessing
                return (SkillConstants.getNoviceSkillRoot(job) * 10000) + 1085;
            case 1112586:// Dark Angelic Blessing
                return (SkillConstants.getNoviceSkillRoot(job) * 10000) + 1087;
            case 1112594:// Snowdrop Angelic Blessing
                return (SkillConstants.getNoviceSkillRoot(job) * 10000) + 1090;
            case 1112663:// White Angelic Blessing
                return (SkillConstants.getNoviceSkillRoot(job) * 10000) + 1179;
            case 1112735:// White Angelic Blessing 2
                return 80001154;
            case 1113020:// Lightning God Ring
                return 80001262;
            case 1113173:// Lightning God Ring 2
                return 80011178;
            // Heaven Rings
            case 1112932:// Guard Ring
                return 80011149;
            case 1114232:// Sun Ring
                return 80010067;
            case 1114233:// Rain Ring
                return 80010068;
            case 1114234:// Rainbow Ring
                return 80010069;
            case 1114235:// Snow Ring
                return 80010070;
            case 1114236:// Lightning Ring
                return 80010071;
            case 1114237:// Wind Ring
                return 80010072;
        }
        return 0;
    }

    public static boolean isRecipeOpenItem(int itemID) {
        return itemID / 10000 == 251;
    }

    public static boolean isBridleItem(int itemID) {
        return itemID / 10000 == 227;
    }

    public static Set<DropInfo> getConsumableMobDrops(int level) {
        level = Math.min(140, (level / 20) * 20); // round it to the nearest 20th level + max of level 140
        return consumableDropsPerLevel.getOrDefault(level, new HashSet<>());
    }

    public static Set<DropInfo> getEquipMobDrops(short job, int level) {
        level = Math.min(140, (level / 10) * 10); // round it to the nearest 10th level + max of level 140
        ItemJob itemJob = GameConstants.getItemJobByJob(job);
        if (itemJob == null) {
            itemJob = ItemJob.BEGINNER;
        }
        return equipDropsPerLevel.getOrDefault(itemJob, new HashMap<>()).getOrDefault(level, new HashSet<>());
    }

    public static boolean isMiuMiuMerchant(int itemID) {
        return itemID == 5450000 || itemID == 5450003 || itemID == 5450007 || itemID == 5450012 || itemID == 5450013 || itemID == 5450004;
    }

    public static boolean isPortableStorage(int itemID) {
        return itemID == 5450009 || itemID == 5450008 || itemID == 5450005;
    }

    public static boolean is2XDropCoupon(int itemId) {
        return itemId <= 5360057 && itemId >= 5360000;
    }

    public static boolean isFusionAnvil(int itemId) {
        return itemId / 100 == 50624;
    }
}

