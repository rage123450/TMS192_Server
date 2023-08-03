# Trait Boost Potion (2434921)

from net.swordie.ms.enums import Stat

traitDict = {
    0: Stat.charismaEXP, # Ambition
    1: Stat.insightEXP, # Insight
    2: Stat.willEXP, # Willpower
    3: Stat.craftEXP, # Diligence
    4: Stat.senseEXP, # Empathy
    5: Stat.charmEXP, # Charm
}

admin = 2007

sm.setSpeakerID(admin)
traitSelection = sm.sendNext("Select the trait you'd like to increase. #b\r\n"
"#L0# Ambition #l\r\n"
"#L1# Insight #l\r\n"
"#L2# Willpower #l\r\n"
"#L3# Diligence #l\r\n"
"#L4# Empathy #l\r\n"
"#L5# Charm #l\r\n")

sm.consumeItem()
chr.addTraitExp(traitDict[traitSelection], 11040)