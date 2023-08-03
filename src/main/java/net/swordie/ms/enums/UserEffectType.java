package net.swordie.ms.enums;

import java.util.Arrays;

/**
 * Created on 6/7/2018.
 */
public enum UserEffectType {
    LevelUp(0),
    SkillUse(1),
    SkillUseBySummoned(2),
    SkillAffected(3),
    SkillAffected_Ex(4),
    SkillAffected_Select(5),
    SkillSpecialAffected(6),
    Quest(7),
    Pet(8),
    SkillSpecial(9),
    Resist(10),
    ProtectOnDieItemUse(11),
    PlayPortalSE(12),
    //13
    JobChanged(14),
    QuestComplete(15),
    IncDecHPEffect(16),
    BuffItemEffect(17),
    SquibEffect(18),
    MonsterBookCardGet(19),
    LotteryUse(20),
    ItemLevelUp(21),
    ItemMaker(22),
    ExpItemConsumed(23),
    FieldExpItemConsumed(24),
    ReservedEffect(25),
    UnkAtm1(),
    UpgradeTombItemUse(26),//原地復活術
    BattlefieldItemUse(27),
    UnkAtm2(),
    AvatarOriented(28),
    AvatarOrientedRepeat(29),
    AvatarOrientedMultipleRepeat(30),
    IncubatorUse(31),
    PlaySoundWithMuteBGM(32),
    PlayExclSoundWithDownBGM(33),
    SoulStoneUse(34),
    IncDecHPEffect_EX(35),
    IncDecHPRegenEffect(36),//更新到這
    EffectUOL(37),
    PvPRage(39),
    PvPChampion(40),
    PvPGradeUp(48),
    PvPRevive(42),
    JobEffect(43),
    FadeInOut(44),
    MobSkillHit(45),
    AswanSiegeAttack(46),
    BlindEffect(47),
    BossShieldCount(48),
    ResetOnStateForOnOffSkill(49),
    JewelCraft(50),
    ConsumeEffect(51),
    PetBuff(52),
    LotteryUIResult(53),
    LeftMonsterNumber(54),
    ReservedEffectRepeat(55),
    RobbinsBomb(56),
    SkillMode(57),
    ActQuestComplete(58),
    Point(59),
    SpeechBalloon(60),
    TextEffect(61),
    SkillPreLoopEnd(62),
    Aiming(63),
    PickUpItem(64),
    BattlePvP_IncDecHp(65),
    BiteAttack_ReceiveSuccess(66),
    BiteAttack_ReceiveFail(67),
    IncDecHPEffect_Delayed(68),
    Lightness(69),
    SetUsed(70),
    ;

    private byte val;

    UserEffectType() {
        this.val = (byte) -2;
    }

    UserEffectType(int val) {
        this.val = (byte) val;
    }

    public byte getVal() {
        return val;
    }

    public static UserEffectType getTypeByVal(int val) {
        return Arrays.stream(values()).filter(uet -> uet.getVal() == val).findAny().orElse(null);
    }
}
