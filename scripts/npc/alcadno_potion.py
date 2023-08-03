# Russellon's Desk (2111015) | Lab - Area B-1 (261020200)

lifeAlchemyMissing = 3314
russellonPill = 2022198

if sm.hasQuest(lifeAlchemyMissing):
    if sm.canHold(russellonPill) and not sm.hasItem(russellonPill):
        sm.giveItem(russellonPill)
        sm.sendSayOkay("There seems to be a number of small pills on the desk. "
        "Just take one from there...")
    elif sm.hasItem(russellonPill):
        sm.sendSayOkay("You already have a pill. Use it and return to Russellon.")
    else:
        sm.sendSayOkay("Please make room in your Use inventory.")