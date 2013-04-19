-- This lua script pretty much acts as our Lua Library for Lua Wars
print("running myScript.lua");

-- note that i didn't have to say CallLua = "XXX"
-- but if i had just had the require statement, then i would have to call
-- org.luawars.LuaJScripting.CallLua.createEntity(x1, x2), at least i think this
CallLua = require 'org.luawars.LuaJScripting.CallLua'
print(CallLua)

function createUnit(panelId, buttonNum)
    CallLua.createUnit(panelId, buttonNum)
end

function selectUnits(tileX, tileY, radius, numUnits, unitType)
    unitType = unitType or NIL; -- not sure if this will work or not
    print("running lua.selectUnits() function");
    CallLua.selectUnits(tileX, tileY, radius, numUnits, unitType)
end

function deselectUnits()
    CallLua.deselectUnits()
end

function moveOrSpecialAction(tileX, tileY)
    print("running lua.moveUnits() function");

    CallLua.moveOrSpecialAction(tileX, tileY)
end

function getGlobal(globalVarName)
    -- note that the function names are not the same as the other functions are
    -- i thought that this name (getLuaJGlobal) provided more clarity for the name
    -- but that it was redundant in lua code
    print(CallLua.getLuaJGlobal(globalVarName))
    return CallLua.getLuaJGlobal(globalVarName)
end

function placeBuilding(tileX, tileY)
    CallLua.placeBuilding(tileX, tileY)
end

function setUpBase()
    CallLua.setUpBase()
end

function selectUnitsAttack(tileX, tileY)
    CallLua.setUpBase(tileX, tileY)
end