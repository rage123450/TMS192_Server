# Dark Hero

medal = 1142344

if sm.canHold(medal):
    sm.chatScript("You obtained the <Dark Hero> medal.")
    sm.startQuest(parentID)
    sm.completeQuest(parentID)