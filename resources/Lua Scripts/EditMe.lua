print ('ZZZZZZZ')

deselectUnits()
addPriority(setUpBase, {}, 300)
addPriority(createUnit, {0, 0}, 250)
addPriority(placeBuilding, {{x=35, y=42}, 0,0}, 200)
addPriority(createUnit, {0, 0}, 150)
addPriority(selectUnits, {{x = 0, y = 0}, "Scout"}, 100)
addPriority(moveOrSpecialAction, {{x = 40, y = 40}}, 50)
