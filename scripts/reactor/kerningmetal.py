# Kerning City_QuestUse (1032002)

pickall = 2855
scrapIron = 4033037

if sm.hasQuest(pickall):
    sm.dropItem(scrapIron, sm.getPosition(objectID).getX(), sm.getPosition(objectID).getY())
sm.invokeAfterDelay(200, "removeReactor")