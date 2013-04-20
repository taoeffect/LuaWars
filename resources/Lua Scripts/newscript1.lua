print ('ZZZZZZZ')
require 'myscript'

addPriority(setUpBase, {}, 300)
addPriority(createUnit, {0, 0}, 250)
addPriority(placeBuilding, {35, 42}, 200)
addPriority(createUnit, {0, 0}, 150)
addPriority(selectUnits, {0, 0}, 100)
addPriority(moveOrSpecialAction, {25, 25}, 50)