package net.swordie.ms.client.character.skills.temp;

import org.apache.logging.log4j.LogManager;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created on 1/2/2018.
 */
public enum CharacterTemporaryStat implements Comparator<CharacterTemporaryStat> {
    IndiePAD(0),
    IndieMAD(1),
    IndiePDD(2),
    IndieMDD(3),
    IndieMHP(4),
    IndieMHPR(5),
    IndieMMP(6),
    IndieMMPR(7),

    IndieACC(8),
    IndieEVA(9),
    IndieJump(10),
    IndieSpeed(11),
    IndieAllStat(12),
    IndieDodgeCriticalTime(13),
    IndieEXP(14),
    IndieBooster(15),

    IndieFixedDamageR(16),
    PyramidStunBuff(17),
    PyramidFrozenBuff(18),
    PyramidFireBuff(19),
    PyramidBonusDamageBuff(20),
    IndieRelaxEXP(21),
    IndieSTR(22),
    IndieDEX(23),

    IndieINT(24),
    IndieLUK(25),
    IndieDamR(26),
    IndieScriptBuff(27),
    IndieMDF(28),
    IndieMaxDamageOver(29),
    IndieAsrR(30),
    IndieTerR(31),

    IndieCr(32),
    IndiePDDR(33),
    IndieCrMax(34),
    IndieBDR(35),
    IndieStatR(36),
    IndieStance(37),
    IndieIgnoreMobpdpR(38),
    IndieEmpty(39),

    IndiePADR(40),
    IndieMADR(41),
    IndieCrMaxR(42),
    IndieEVAR(43),
    IndieMDDR(44),
    IndieDrainHP(45),
    IndiePMdR(46),
    IndieMaxDamageOverR(47),

    IndieForceJump(48),
    IndieForceSpeed(49),
    IndieQrPointTerm(50),
    IndieUNK1(51),
    IndieUNK2(52),
    IndieUNK3(53),
    IndieUNK4(54),
    IndieUNK5(),
    IndieStatCount(55),

    PAD(56),
    PDD(PAD, 1),
    MAD(PAD, 2),
    MDD(PAD, 3),
    ACC(PAD, 4),
    EVA(PAD, 5),
    Craft(PAD, 6),

    Speed(PAD, 7),
    Jump(PAD, 8),
    MagicGuard(PAD, 9),
    DarkSight(PAD, 10),
    Booster(PAD, 11),
    PowerGuard(PAD, 12),
    MaxHP(PAD, 13),
    MaxMP(PAD, 14),

    Invincible(PAD, 15),
    SoulArrow(PAD, 16),
    Stun(73),
    Poison(Stun, 1),
    Seal(Stun, 2),
    Darkness(Stun, 3),
    ComboCounter(Stun, 4),
    WeaponCharge(Stun, 5),

    HolySymbol(Stun, 6),
    MesoUp(Stun, 7),
    ShadowPartner(81),
    PickPocket(ShadowPartner, 1),
    MesoGuard(ShadowPartner, 2),
    Thaw(ShadowPartner, 3),
    Weakness(ShadowPartner, 4),
    Curse(ShadowPartner, 5),

    Slow(ShadowPartner, 6),
    Morph(ShadowPartner, 7),
    Regen(ShadowPartner, 8),
    BasicStatUp(ShadowPartner, 9),
    Stance(ShadowPartner, 10),
    SharpEyes(92),
    ManaReflection(SharpEyes, 1),
    Attract(SharpEyes, 2),

    NoBulletConsume(SharpEyes, 3),
    Infinity(SharpEyes, 4),
    AdvancedBless(SharpEyes, 5),
    IllusionStep(SharpEyes, 6),
    Blind(SharpEyes, 7),
    Concentration(SharpEyes, 8),
    BanMap(SharpEyes, 9),
    MaxLevelBuff(102),
    MesoUpByItem(103),
    Ghost(104),
    Barrier(105),
    ReverseInput(106),
    ItemUpByItem(107),
    RespectPImmune(108),
    RespectMImmune(109),
    DefenseAtt(110),
    DefenseState(111),
    DojangBerserk(112),
    DojangInvincible(113),
    DojangShield(114),
    SoulMasterFinal(115),
    WindBreakerFinal(116),
    ElementalReset(117),
    HideAttack(118),
    EventRate(119),
    ComboAbilityBuff(120),
    ComboDrain(121),
    ComboBarrier(122),
    BodyPressure(123),
    RepeatEffect(124),
    ExpBuffRate(125),
    StopPortion(126),
    StopMotion(127),
    Fear(128),
    HiddenPieceOn(129),
    MagicShield(130),
    MagicResistance(131),
    SoulStone(132),
    Flying(133),
    Frozen(134),
    AssistCharge(135),
    Enrage(136),
    DrawBack(137),
    NotDamaged(138),
    FinalCut(139),
    HowlingAttackDamage(140),
    BeastFormDamageUp(141),
    Dance(142),

    //

    EMHP(144),
    EMMP(145),
    EPAD(146),
    EMAD(147),
    EPDD(148),
    EMDD(149),
    Guard(150),
    Cyclone(151),

    HowlingCritical(152),
    HowlingMaxMP(153),
    HowlingDefence(154),
    HowlingEvasion(155),
    Conversion(156),
    Revive(157),
    PinkbeanMinibeenMove(158),
    Sneak(159),

    Mechanic(160),
    BeastFormMaxHP(161),
    //
    Dice(163),
    BlessingArmor(),
    DamR(),
    TeleportMasteryOn(),
    CombatOrders(),
    Beholder(),

    DispelItemOption(),
    Inflation(),
    OnixDivineProtection(),
    Web(),
    Bless(),
    TimeBomb(),
    DisOrder(),
    Thread(),

    Team(),
    Explosion(),
    BuffLimit(),

    STR(180),
    INT(STR, 1),
    DEX(STR, 2),
    LUK(STR, 3),

    DispelItemOptionByField(184),

    DarkTornado(185), // Cygnus Attack
    PVPDamage(186),
    PvPScoreBonus(PVPDamage, 1),
    PvPInvincible(PVPDamage, 2),
    PvPRaceEffect(PVPDamage, 3),
    WeaknessMdamage(190),
    Frozen2(191),
    PVPDamageSkill(192),

    AmplifyDamage(193),
    IceKnight(194),
    Shock(195),
    InfinityForce(196),
    IncMaxHP(197),
    IncMaxMP(IncMaxHP, 1),
    HolyMagicShell(199),
    KeyDownTimeIgnore(200),

    ArcaneAim(201),
    MasterMagicOn(ArcaneAim, 1),
    AsrR(203),
    TerR(AsrR, 1),
    DamAbsorbShield(205),
    DevilishPower(206),
    Roulette(207),
    SpiritLink(208),

    AsrRByItem(209),
    Event(210),
    CriticalBuff(211),
    DropRate(212),
    PlusExpRate(213),
    ItemInvincible(214),
    Awake(215),
    ItemCritical(),

    ItemEvade(),
    Event2(),
    VampiricTouch(),
    DDR(),
    IncCriticalDamMin(),
    IncCriticalDamMax(),
    IncTerR(),
    IncAsrR(),

    DeathMark(),
    UsefulAdvancedBless(),
    Lapidification(),
    VenomSnake(),
    CarnivalAttack(),
    CarnivalDefence(),
    CarnivalExp(),
    SlowAttack(),

    PyramidEffect(233/*232*/),
    KillingPoint(),
    HollowPointBullet(),
    KeyDownMoving(),
    IgnoreTargetDEF(),
    ReviveOnce(),
    Invisible(),
    EnrageCr(),

    EnrageCrDamMin(),
    Judgement(),
    DojangLuckyBonus(),
    PainMark(),
    Magnet(),
    MagnetArea(),
    VampDeath(),
    BlessingArmorIncPAD(),

    KeyDownAreaMoving(),
    Larkness(250),
    StackBuff(),
    BlessOfDarkness(),
    AntiMagicShell(),
    AntiMagicShellBool(),
    LifeTidal(),
    HitCriDamR(),
    SmashStack(256/*255*/),

    PartyBarrier(),
    ReshuffleSwitch(),
    SpecialAction(),
    VampDeathSummon(),
    StopForceAtomInfo(261),
    SoulGazeCriDamR(),
    SoulRageCount(),
    PowerTransferGauge(),

    AffinitySlug(),
    Trinity(),
    IncMaxDamage(),
    BossShield(),
    MobZoneState(),
    GiveMeHeal(),
    TouchMe(),
    Contagion(),

    ComboUnlimited(),
    SoulExalt(),
    IgnorePCounter(),
    IgnoreAllCounter(),
    IgnorePImmune(),
    IgnoreAllImmune(),
    FinalJudgement(),
    IceAura(279),

    FireAura(280),
    VengeanceOfAngel(281),
    HeavensDoor(282),
    Preparation(283),
    BullsEye(284),
    IncEffectHPPotion(285),
    IncEffectMPPotion(286),
    BleedingToxin(287),

    IgnoreMobDamR(288),
    Asura(290/*289*/),
    FlipTheCoin(290),
    UnityOfPower(291),
    Stimulate(292),
    ReturnTeleport(293),
    DropRIncrease(294),
    IgnoreMobpdpR(295),

    BdR(296),
    CapDebuff(297),
    Exceed(298),
    DiabolikRecovery(299),
    FinalAttackProp(300),
    ExceedOverload(301),
    OverloadCount(302),
    BuckShot(303),

    FireBomb(304),
    HalfstatByDebuff(305),
    SurplusSupply(306),
    SetBaseDamage(307),
    EVAR(308),
    NewFlying(309),
    AmaranthGenerator(310),
    OnCapsule(311),

    CygnusElementSkill(312),
    StrikerHyperElectric(313),
    EventPointAbsorb(314),
    EventAssemble(315),
    StormBringer(316),
    ACCR(317),
    DEXR(318),
    Albatross(319),

    Translucence(320),
    PoseType(321),
    PoseTypeBool(321),
    LightOfSpirit(322),
    ElementSoul(323),
    GlimmeringTime(324),
    TrueSight(325),
    SoulExplosion(326),
    SoulMP(328),

    FullSoulMP(SoulMP, 1),
    SoulSkillDamageUp(329),
    ElementalCharge(330),
    Restoration(331),
    CrossOverChain(332),
    ChargeBuff(333),
    Reincarnation(334),
    KnightsAura(335),

    ChillingStep(336),
    DotBasedBuff(337),
    BlessEnsenble(338),
    ComboCostInc(339),
    ExtremeArchery(340),
    NaviFlying(341),
    QuiverCatridge(342),
    AdvancedQuiver(343),

    UserControlMob(344),
    ImmuneBarrier(345),
    ArmorPiercing(346),
    ZeroAuraStr(347),
    ZeroAuraSpd(348),
    CriticalGrowing(349),
    QuickDraw(350),
    BowMasterConcentration(351),

    TimeFastABuff(352),
    TimeFastBBuff(353),
    GatherDropR(354),
    AimBox2D(355),
    IncMonsterBattleCaptureRate(356),
    CursorSniping(357),
    DebuffTolerance(358),
    DotHealHPPerSecond(359),

    SpiritGuard(360),
    PreReviveOnce(361),
    SetBaseDamageByBuff(362),
    LimitMP(363),
    ReflectDamR(364),
    ComboTempest(365),
    MHPCutR(366),
    MMPCutR(367),

    SelfWeakness(368),
    ElementDarkness(369),
    FlareTrick(370),
    Ember(371),
    Dominion(372),
    SiphonVitality(373),
    DarknessAscension(374),
    BossWaitingLinesBuff(375),

    DamageReduce(376),
    ShadowServant(377),
    ShadowIllusion(378),
    KnockBack(379),
    AddAttackCount(380),
    ComplusionSlant(381),
    JaguarSummoned(382),
    JaguarCount(383),

    SSFShootingAttack(384),
    DevilCry(385),
    ShieldAttack(386),
    BMageAura(387),
    DarkLighting(388),
    AttackCountX(389),
    BMageDeath(390),
    BombTime(391),

    NoDebuff(392),
    BattlePvPMikeShield(393),
    BattlePvPMikeBugle(394),
    XenonAegisSystem(395),
    AngelicBursterSoulSeeker(396),
    HiddenPossession(397),
    NightWalkerBat(398),
    NightLordMark(399),

    WizardIgnite(400),
    FireBarrier(401),
    ChangeFoxMan(402),
    BattlePvPHelenaMark(403),
    BattlePvPHelenaWindSpirit(404),
    BattlePvPLangEProtection(405),
    BattlePvPLeeMalNyunScaleUp(406),
    BattlePvPRevive(407),

    PinkbeanAttackBuff(408),
    PinkbeanRelax(409),
    PinkbeanRollingGrade(410),
    PinkbeanYoYoStack(411),
    RandAreaAttack(412),
    NextAttackEnhance(413),
    AranBeyonderDamAbsorb(414),
    AranCombotempastOption(415),

    NautilusFinalAttack(416),
    ViperTimeLeap(417),
    RoyalGuardState(418),
    RoyalGuardPrepare(419),
    MichaelSoulLink(420),
    MichaelStanceLink(421),
    TriflingWhimOnOff(422),
    AddRangeOnOff(423),

    KinesisPsychicPoint(424),
    KinesisPsychicOver(425),
    KinesisPsychicShield(426),
    KinesisIncMastery(427),
    KinesisPsychicEnergeShield(428),
    BladeStance(429),
    DebuffActiveSkillHPCon(430),
    DebuffIncHP(431),

    BowMasterMortalBlow(432),
    AngelicBursterSoulResonance(433),
    Fever(434),
    IgnisRore(435),
    RpSiksin(436),
    TeleportMasteryRange(437),
    FixCoolTime(438),
    IncMobRateDummy(439),

    AdrenalinBoost(440),
    AranSmashSwing(441),
    AranDrain(442),
    AranBoostEndHunt(443),
    HiddenHyperLinkMaximization(444),
    RWCylinder(445),
    RWCombination(446),
    RWMagnumBlow(447),

    RWBarrier(448),
    RWBarrierHeal(449),
    RWMaximizeCannon(450),
    RWOverHeat(451),
    UsingScouter(452),
    RWMovingEvar(453),
    Stigma(480),
    Unk455(455),

    Unk456(456),
    Unk457(457),
    Unk458(458),
    Unk459(459),
    Unk460(460),
    HayatoStance(461),
    HayatoStanceBonus(462),
    EyeForEye(463),

    WillowDodge(464),
    Unk465(465),
    HayatoPAD(466),
    HayatoHPR(467),
    HayatoMPR(468),
    HayatoBooster(469),
    Unk470(470),
    Unk471(471),

    Jinsoku(472),
    HayatoCr(473),
    HakuBlessing(474),
    HayatoBoss(475),
    BattoujutsuAdvance(476),
    Unk477(477),
    Unk478(478),
    BlackHeartedCurse(479),

    BeastMode(480),
    TeamRoar(481),
    Unk482(482),
    Unk483(483),
    Unk484(484),
    Unk485(485),
    Unk486(486),
    Unk487(487),

    Unk488(488),
    Unk489(489),
    Unk490(490),
    Unk491(),
    EnergyCharged(491),
    DashSpeed(EnergyCharged, 1),
    DashJump(EnergyCharged, 2),
    RideVehicle(EnergyCharged, 3),
    PartyBooster(EnergyCharged, 4),
    GuidedBullet(EnergyCharged, 5),
    Undead(EnergyCharged, 6),
    RideVehicleExpire(EnergyCharged, 7),
    DefaultBuff(EnergyCharged, 8),
    ;

    int bitPos;
    int val;
    int pos;

    public static final int length = 16;
    //    public static final int length = 17;
    private static final org.apache.logging.log4j.Logger log = LogManager.getRootLogger();

    private static final List<CharacterTemporaryStat> ORDER = Arrays.asList(
            STR, INT, DEX, LUK, PAD, PDD, MAD, MDD, ACC, EVA, EVAR, Craft, Speed, Jump, EMHP, EMMP, EPAD, EMAD, EPDD,
            EMDD, MagicGuard, DarkSight, Booster, PowerGuard, Guard, MaxHP, MaxMP, Invincible, SoulArrow, Stun, Shock,
            Poison, Seal, Darkness, ComboCounter, WeaponCharge, ElementalCharge, HolySymbol, MesoUp, ShadowPartner,
            PickPocket, MesoGuard, Thaw, Weakness, WeaknessMdamage, Curse, Slow, TimeBomb, BuffLimit, Team, DisOrder,
            Thread, Morph, Ghost, Regen, BasicStatUp, Stance, SharpEyes, ManaReflection, Attract, Magnet, MagnetArea,
            NoBulletConsume, StackBuff, Trinity, Infinity, AdvancedBless, IllusionStep, Blind, Concentration, BanMap,
            MaxLevelBuff, Barrier, DojangShield, ReverseInput, MesoUpByItem, ItemUpByItem, RespectPImmune,
            RespectMImmune, DefenseAtt, DefenseState, DojangBerserk, DojangInvincible, SoulMasterFinal,
            WindBreakerFinal, ElementalReset, HideAttack, EventRate, ComboAbilityBuff, ComboDrain, ComboBarrier,
            PartyBarrier, BodyPressure, RepeatEffect, ExpBuffRate, StopPortion, StopMotion, Fear, MagicShield,
            MagicResistance, SoulStone, Flying, NewFlying, NaviFlying, Frozen, Frozen2, Web, Enrage, NotDamaged,
            FinalCut, HowlingAttackDamage, BeastFormDamageUp, Dance, Cyclone, OnCapsule, HowlingCritical,
            HowlingMaxMP, HowlingDefence, HowlingEvasion, Conversion, Revive, PinkbeanMinibeenMove, Sneak, Mechanic,
            DrawBack, BeastFormMaxHP, Dice, BlessingArmor, BlessingArmorIncPAD, DamR, TeleportMasteryOn, CombatOrders,
            Beholder, DispelItemOption, DispelItemOptionByField, Inflation, OnixDivineProtection, Bless, Explosion,
            DarkTornado, IncMaxHP, IncMaxMP, PVPDamage, PVPDamageSkill, PvPScoreBonus, PvPInvincible, PvPRaceEffect,
            IceKnight, HolyMagicShell, InfinityForce, AmplifyDamage, KeyDownTimeIgnore, MasterMagicOn, AsrR,
            AsrRByItem, TerR, DamAbsorbShield, Roulette, Event, SpiritLink, CriticalBuff, DropRate, PlusExpRate,
            ItemInvincible, ItemCritical, ItemEvade, Event2, VampiricTouch, DDR, IncCriticalDamMin, IncCriticalDamMax,
            IncTerR, IncAsrR, DeathMark, PainMark, UsefulAdvancedBless, Lapidification, VampDeath, VampDeathSummon,
            VenomSnake, CarnivalAttack, CarnivalDefence, CarnivalExp, SlowAttack, PyramidEffect, HollowPointBullet,
            KeyDownMoving, KeyDownAreaMoving, CygnusElementSkill, IgnoreTargetDEF, Invisible, ReviveOnce,
            AntiMagicShell, EnrageCr, EnrageCrDamMin, BlessOfDarkness, LifeTidal, Judgement, DojangLuckyBonus,
            HitCriDamR, Larkness, SmashStack, ReshuffleSwitch, SpecialAction, ArcaneAim, StopForceAtomInfo,
            SoulGazeCriDamR, SoulRageCount, PowerTransferGauge, AffinitySlug, SoulExalt, HiddenPieceOn,
            BossShield, MobZoneState, GiveMeHeal, TouchMe, Contagion, ComboUnlimited, IgnorePCounter,
            IgnoreAllCounter, IgnorePImmune, IgnoreAllImmune, FinalJudgement, KnightsAura, IceAura, FireAura,
            VengeanceOfAngel, HeavensDoor, Preparation, BullsEye, IncEffectHPPotion, IncEffectMPPotion, SoulMP,
            FullSoulMP, SoulSkillDamageUp, BleedingToxin, IgnoreMobDamR, Asura, FlipTheCoin, UnityOfPower,
            Stimulate, ReturnTeleport, CapDebuff, DropRIncrease, IgnoreMobpdpR, BdR, Exceed, DiabolikRecovery,
            FinalAttackProp, ExceedOverload, DevilishPower, OverloadCount, BuckShot, FireBomb, HalfstatByDebuff,
            SurplusSupply, SetBaseDamage, AmaranthGenerator, StrikerHyperElectric, EventPointAbsorb, EventAssemble,
            StormBringer, ACCR, DEXR, Albatross, Translucence, PoseType, LightOfSpirit, ElementSoul, GlimmeringTime,
            Restoration, ComboCostInc, ChargeBuff, TrueSight, CrossOverChain, ChillingStep, Reincarnation, DotBasedBuff,
            BlessEnsenble, ExtremeArchery, QuiverCatridge, AdvancedQuiver, UserControlMob, ImmuneBarrier, ArmorPiercing,
            ZeroAuraStr, ZeroAuraSpd, CriticalGrowing, QuickDraw, BowMasterConcentration, TimeFastABuff, TimeFastBBuff,
            GatherDropR, AimBox2D, CursorSniping, IncMonsterBattleCaptureRate, DebuffTolerance, DotHealHPPerSecond,
            SpiritGuard, PreReviveOnce, SetBaseDamageByBuff, LimitMP, ReflectDamR, ComboTempest, MHPCutR, MMPCutR,
            SelfWeakness, ElementDarkness, FlareTrick, Ember, Dominion, SiphonVitality, DarknessAscension,
            BossWaitingLinesBuff, DamageReduce, ShadowServant, ShadowIllusion, AddAttackCount, ComplusionSlant,
            JaguarSummoned, JaguarCount, SSFShootingAttack, DevilCry, ShieldAttack, BMageAura, DarkLighting,
            AttackCountX, BMageDeath, BombTime, NoDebuff, XenonAegisSystem, AngelicBursterSoulSeeker, HiddenPossession,
            NightWalkerBat, NightLordMark, WizardIgnite, BattlePvPHelenaMark, BattlePvPHelenaWindSpirit,
            BattlePvPLangEProtection, BattlePvPLeeMalNyunScaleUp, BattlePvPRevive, PinkbeanAttackBuff, RandAreaAttack,
            BattlePvPMikeShield, BattlePvPMikeBugle, PinkbeanRelax, PinkbeanYoYoStack, NextAttackEnhance,
            AranBeyonderDamAbsorb, AranCombotempastOption, NautilusFinalAttack, ViperTimeLeap, RoyalGuardState,
            RoyalGuardPrepare, MichaelSoulLink, MichaelStanceLink, TriflingWhimOnOff, AddRangeOnOff,
            KinesisPsychicPoint, KinesisPsychicOver, KinesisPsychicShield, KinesisIncMastery,
            KinesisPsychicEnergeShield, BladeStance, DebuffActiveSkillHPCon, DebuffIncHP, BowMasterMortalBlow,
            AngelicBursterSoulResonance, Fever, IgnisRore, RpSiksin, TeleportMasteryRange, FireBarrier, ChangeFoxMan,
            FixCoolTime, IncMobRateDummy, AdrenalinBoost, AranSmashSwing, AranDrain, AranBoostEndHunt,
            HiddenHyperLinkMaximization, RWCylinder, RWCombination, RWMagnumBlow, RWBarrier, RWBarrierHeal,
            RWMaximizeCannon, RWOverHeat, RWMovingEvar, Stigma, Unk455, IncMaxDamage, Unk456, Unk457, Unk458, Unk459,
            Unk460, PyramidFireBuff /*not sure*/, HayatoStance, HayatoBooster, HayatoStanceBonus, WillowDodge, Unk465,
            HayatoPAD, HayatoHPR, HayatoMPR, Jinsoku, HayatoCr, HakuBlessing, HayatoBoss, BattoujutsuAdvance, Unk477,
            Unk478, BlackHeartedCurse, EyeForEye, BeastMode, TeamRoar, Unk482, Unk483, Unk487, Unk488, Unk489, Unk490,
            Unk491
    );

    private static final List<CharacterTemporaryStat> REMOTE_ORDER = Arrays.asList(
            Speed, ComboCounter, WeaponCharge, ElementalCharge, Stun, Shock, Darkness, Seal, Weakness, WeaknessMdamage,
            Curse, Slow, PvPRaceEffect, IceKnight, TimeBomb, Team, DisOrder, Thread, Poison, ShadowPartner, Morph,
            Ghost, Attract, Magnet, MagnetArea, NoBulletConsume, BanMap, Barrier, DojangShield, ReverseInput,
            RespectPImmune, RespectMImmune, DefenseAtt, DefenseState, DojangBerserk, RepeatEffect, StopPortion,
            StopMotion, Fear, MagicShield, Frozen, Frozen2, Web, DrawBack, FinalCut, Cyclone, OnCapsule, Mechanic,
            Inflation, Explosion, DarkTornado, AmplifyDamage, HideAttack, DevilishPower, SpiritGuard, Event, Event2,
            DeathMark, PainMark, Lapidification, VampDeath, VampDeathSummon, VenomSnake, PyramidEffect, KillingPoint,
            PinkbeanRollingGrade, IgnoreTargetDEF, Invisible, Judgement, KeyDownAreaMoving, StackBuff, BlessOfDarkness,
            Larkness, ReshuffleSwitch, SpecialAction, StopForceAtomInfo, SoulGazeCriDamR, PowerTransferGauge,
            AffinitySlug, SoulExalt, HiddenPieceOn, SmashStack, MobZoneState, GiveMeHeal, TouchMe, Contagion,
            ComboUnlimited, IgnorePCounter, IgnoreAllCounter, IgnorePImmune, IgnoreAllImmune, FinalJudgement,
            KnightsAura, IceAura, FireAura, HeavensDoor, DamAbsorbShield, AntiMagicShell, NotDamaged, BleedingToxin,
            WindBreakerFinal, IgnoreMobDamR, Asura, UnityOfPower, Stimulate, ReturnTeleport, CapDebuff, OverloadCount,
            FireBomb, SurplusSupply, NewFlying, NaviFlying, AmaranthGenerator, CygnusElementSkill, StrikerHyperElectric,
            EventPointAbsorb, EventAssemble, Albatross, Translucence, PoseType, LightOfSpirit, ElementSoul,
            GlimmeringTime, Reincarnation, Beholder, QuiverCatridge, ArmorPiercing, ZeroAuraStr, ZeroAuraSpd,
            ImmuneBarrier, FullSoulMP, AntiMagicShellBool, Dance, SpiritGuard, ComboTempest, HalfstatByDebuff,
            ComplusionSlant, JaguarSummoned, BMageAura, DarkLighting, AttackCountX, FireBarrier, KeyDownMoving,
            MichaelSoulLink, KinesisPsychicEnergeShield, BladeStance, Fever, AdrenalinBoost, RWBarrier, RWMagnumBlow,
            Stigma, Unk456, BeastMode, TeamRoar, HayatoStance, HayatoBooster, HayatoStanceBonus, HayatoPAD, HayatoHPR,
            HayatoMPR, HayatoCr, HayatoBoss, Stance, BattoujutsuAdvance, Unk478, BlackHeartedCurse, EyeForEye, Unk458,
            Unk483, Unk487, Unk488, Unk489, Unk491, Unk460, PoseTypeBool
    );



/*    CharacterTemporaryStat(int val, int pos) {
        this.val = val;
        this.pos = pos;
    }*/

    CharacterTemporaryStat() {
        this(Counter.next);
    }

    CharacterTemporaryStat(int bitPos) {
        this.bitPos = bitPos;
        this.val = 1 << (31 - bitPos % 32);
        this.pos = bitPos / 32;

        if (bitPos != 0) {
            Counter.next = bitPos + 1;
        }
    }

    CharacterTemporaryStat(CharacterTemporaryStat s, int offset) {
        this(s.bitPos + offset);
    }

    private static class Counter {
        private static int next;
    }

    public static CharacterTemporaryStat getByBitPos(int parseInt) {
        return Arrays.stream(values())
                .filter(stat -> stat.getBitPos() == parseInt)
                .toList().get(0);
    }

    public static CharacterTemporaryStat getByName(String n) {
        return Arrays.stream(values())
                .filter(stat -> stat.name().equals(n))
                .toList().get(0);
    }

    public void SetBitPos(int bitPos) {
        this.bitPos = bitPos;
        this.val = 1 << (31 - bitPos % 32);
        this.pos = bitPos / 32;
    }

    public boolean isEncodeInt() {
        switch (this) {
            case CarnivalDefence:
            case SpiritLink:
            case DojangLuckyBonus:
            case SoulGazeCriDamR:
            case PowerTransferGauge:
            case ReturnTeleport:
            case ShadowPartner:
            case IncMaxDamage:
            case Unk487:
            case SetBaseDamage:
            case QuiverCatridge:
            case ImmuneBarrier:
            case NaviFlying:
            case Dance:
            case SetBaseDamageByBuff:
            case DotHealHPPerSecond:
            case MagnetArea:
                return true;
            default:
                return false;
        }
    }

    public boolean isIndie() {
        return toString().toLowerCase().contains("indie");
    }

    public boolean isMovingEffectingStat() {
        switch (this) {
            case Speed:
            case Jump:
            case Stun:
            case Weakness:
            case Slow:
            case Morph:
            case Ghost:
            case BasicStatUp:
            case Attract:
            case DashSpeed:
            case DashJump:
            case Flying:
            case Frozen:
            case Frozen2:
            case Lapidification:
            case IndieSpeed:
            case IndieJump:
            case KeyDownMoving:
            case Mechanic:
            case Magnet:
            case MagnetArea:
            case VampDeath:
            case VampDeathSummon:
            case GiveMeHeal:
            case DarkTornado:
            case NewFlying:
            case NaviFlying:
            case UserControlMob:
            case Dance:
            case SelfWeakness:
            case BattlePvPHelenaWindSpirit:
            case BattlePvPLeeMalNyunScaleUp:
            case TouchMe:
            case IndieForceSpeed:
            case IndieForceJump:
            case RideVehicle:
            case RideVehicleExpire:
                return true;
            default:
                return false;
        }
    }

    public int getVal() {
        return val;
    }

    public int getPos() {
        return pos;
    }

    public int getOrder() {
        return ORDER.indexOf(this);
    }

    public int getRemoteOrder() {
        return REMOTE_ORDER.indexOf(this);
    }

    public boolean isRemoteEncode4() {
        switch (this) {
            case NoBulletConsume:
            case RespectPImmune:
            case RespectMImmune:
            case DefenseAtt:
            case DefenseState:
            case MagicShield:
            case PyramidEffect:
            case BlessOfDarkness:
            case ImmuneBarrier:
            case Dance:
            case SpiritGuard:
            case KinesisPsychicEnergeShield:
            case AdrenalinBoost:
            case RWBarrier:
            case RWMagnumBlow:
            case HayatoStance:
            case Unk487:
            case Unk488:
            case Unk489:
                return true;
            default:
                return false;
        }
    }

    public boolean isRemoteEncode1() {
        switch (this) {
            case Speed:
            case Shock:
            case Team:
            case Cyclone:
            case OnCapsule:
            case KillingPoint:
            case PinkbeanRollingGrade:
            case ReturnTeleport:
            case FireBomb:
            case SurplusSupply:
            case Unk460:
                return true;
            default:
                return false;
        }
    }

    public boolean isNotEncodeReason() {
        switch (this) {
            case Speed:
            case ElementalCharge:
            case Shock:
            case Team:
            case Ghost:
            case NoBulletConsume:
            case RespectPImmune:
            case RespectMImmune:
            case DefenseAtt:
            case DefenseState:
            case MagicShield:
            case Cyclone:
            case OnCapsule:
            case PyramidEffect:
            case KillingPoint:
            case PinkbeanRollingGrade:
            case StackBuff:
            case BlessOfDarkness:
            case SurplusSupply:
            case ImmuneBarrier:
            case AdrenalinBoost:
            case RWBarrier:
            case RWMagnumBlow:
            case HayatoStance:
            case Unk488:
            case Unk489:
            case Unk460:
                return true;
            default:
                return false;
        }
    }

    public boolean isNotEncodeAnything() {
        return switch (this) {
            case FullSoulMP, AntiMagicShellBool, PoseTypeBool -> true;
            default -> false;
        };
    }

    public static void main(String[] args) {
        int a = Stigma.bitPos;
//        int val = 1 << (31 - (a & 0x1f));
//        int pos = a >> 5;
        int val = 0x1000;
        int pos = 0;
        log.debug(String.format("value 0x%04x, pos %d", val, pos));
        Arrays.stream(values()).filter(cts -> cts.getVal() == val && cts.getPos() == pos).map(cts -> "Corresponds to " + cts).forEach(log::debug);
//        for (CharacterTemporaryStat cts : values()) {
//            val = cts.getVal();
//            for (int i = 0; i < 32; i++) {
//                if (1 << i == val) {
//                    val = 31 - i;
//                }
//            }
//            if (val % 8 == 0) {
//                System.out.println();
//            }
//            System.out.println(String.format("%s(%d),", cts.toString(), (cts.getPos() * 32) + val));
//        }
    }

    @Override
    public int compare(CharacterTemporaryStat o1, CharacterTemporaryStat o2) {
        if (o1.getPos() < o2.getPos()) {
            return -1;
        } else if (o1.getPos() > o2.getPos()) {
            return 1;
        }
        // hacky way to circumvent java not having unsigned ints
        int o1Val = o1.getVal();
        if (o1Val == 0x8000_0000) {
            o1Val = Integer.MAX_VALUE;
        }
        int o2Val = o2.getVal();
        if (o2Val == 0x8000_0000) {
            o2Val = Integer.MAX_VALUE;
        }

        if (o1Val > o2Val) {
            // bigger value = earlier in the int => smaller
            return -1;
        } else if (o1Val < o2Val) {
            return 1;
        }
        return 0;
    }

    public int getBitPos() {
        return bitPos;
    }
}
