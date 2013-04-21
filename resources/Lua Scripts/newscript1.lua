print ('ZZZZZZZ')
require 'myscript'

deselectUnits()
--addPriority(setUpBase, {}, 300)
--addPriority(createUnit, {0, 0}, 250)
--addPriority(placeBuilding, {35, 42}, 200)
--addPriority(createUnit, {0, 0}, 150)
addPriority(selectUnits, {{x = 0, y = 0}, "Scout"}, 100)
addPriority(moveOrSpecialAction, {{x = 40, y = 40}}, 50)

myTable = {x = 1, y = 2}
print("myTable.x " .. myTable.x)
print("myTable.y " .. myTable.y)