print("running myScript.lua");

-- note that i didn't have to say CallLua = "XXX"
-- but if i had just had the require statement, then i would have to call
-- org.luawars.LuaJScripting.CallLua.createEntity(x1, x2), at least i think this
CallLua = require 'org.luawars.LuaJScripting.CallLua'
print(CallLua)

function createUnit(panelId, buttonNum)
    CallLua.createUnit(panelId, buttonNum)
end

function selectUnits(xLoc, yLoc, numUnits)
    CallLua.selectUnits(xLoc, yLoc, numUnits)
end

function moveOrSpecialAction(xLoc, yLoc)
    CallLua.moveOrSpecialAction(xLoc, yLoc)
end

function getGlobal(globalVarName)
    -- note that the function names are not the same as the other functions are
    -- i thought that this name (getLuaJGlobal) provided more clarity for the name
    -- but that it was redundant in lua code
    print(CallLua.getLuaJGlobal(globalVarName))
    return CallLua.getLuaJGlobal(globalVarName)
end

function placeBuilding(xLoc, yLoc)
    CallLua.placeBuilding(xLoc, yLoc)
end