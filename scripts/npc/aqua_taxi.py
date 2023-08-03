# Dolphin (2060009)

destinationDict = {
    230000000: [230030200, 251000100], # Aquarium
    251000100: [230000000] # Pier on the Beach
}

currentMap = sm.getFieldID()
optionList = destinationDict[currentMap]

if len(optionList) > 1:
    destString = ["Where would you like to go?\r\n"]
    for index, option in enumerate(optionList):
        destString.append(''.join(["#L", repr(index), "##m", repr(option), "##l\r\n"]))
    destIndex = sm.sendNext(''.join(destString))
    sm.warp(optionList[destIndex])
else:
    destination = optionList[0]
    response = sm.sendAskYesNo("Would you like to go to #m" + repr(destination) + "#?")
    if response:
        sm.warp(destination)
