# 2432092 - Gollux Left-Shoulder Teleport Rock
from net.swordie.ms.constants import BossConstants

sm.setSpeakerID(9390124) #Heart Tree Guardian
if not chr.getInstance() == None and chr.getFieldID() in BossConstants.GOLLUX_MAPS:
    if chr.getFieldID() == 863010430:
        sm.sendSayOkay("You are already within the map requested.\r\nYou must enter another map if you wish to use the item.")
    else:
        sm.warp(863010430)
        sm.consumeItem(2432092)
else:
    sm.sendSayOkay("You're not currently fighting the Gollux Boss.\r\nThe item has been maintained and you have not been teleported.")