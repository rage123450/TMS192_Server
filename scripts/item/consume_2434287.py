# Mu Gong's Honor EXP Guarantee (2434287)

medal = 2434290

honor = chr.getHonorExp()

if honor >= 10000:
    if sm.canHold(medal):
        sm.chat("You extracted 10,000 Honor EXP. Check your Use tab.")
        chr.addHonorExp(-10000)
        sm.giveItem(medal)
        sm.consumeItem()
    else:
        sm.chat("Please make room in your Use inventory.")
else:
    sm.chat("You need at least 10,000 Honor EXP to use this.")