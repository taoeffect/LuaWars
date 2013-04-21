-- This lua script pretty much acts as our Lua Library for Lua Wars
print("running myScript.lua");

-- note that i didn't have to say CallLua = "XXX"
-- but if i had just had the require statement, then i would have to call
-- org.luawars.LuaJScripting.CallLua.createEntity(x1, x2), at least i think this
CallLua = require 'org.luawars.LuaJScripting.CallLua'

-- On the right side of the screen, it has the 4 panels and each panel has buttons on it
-- creates a unit (this includes buildings too) based on which panel/button you choose
-- note that buttons go from topleft to bottom right
-- for example, if you build (panelId = 0 and buttonNum = 0) it will create a barracks
-- or if you build (panelId = 1, and buttonNum = 1) it will create a soldierXXX?
-- @param: panelId - takes an integer (0 to 3 inclusive)
-- @param: buttonNum - takes an integer (0 to X, where X is dependent on the panel)
function createUnit(panelId, buttonNum, ...)
    --print("HELLO")
    --print(getGlobal('buildingPanel' .. panelId))
    if(panelId and buttonNum and getGlobal('buildingPanel' .. panelId) == -1) then
        --print("creating script")
        CallLua.createUnit(panelId, buttonNum)
        removeTopPriority()
        return true
    end
end

-- allows you to select units that are closest to a point
-- @param: tileX - x tile coordinate where you want to select units closest to (the coordinates are on the game)
-- @param: tileY - y tile coordinate where you want to select units closest to (the coordinates are on the game)
-- @optional param : unitType - type of unit you want to select. if NIL, selects all types
-- @optional param : numUnits - number of units. if nil, selects up to 1000 units (basically all units)
-- @optional param : radius - get units within radius units away from (tileX, tileY) point
function selectUnits(tileCoordinate, unitType, numUnits, radius, ...)
    radius = radius or 1000
    numUnits = numUnits or 1000
    if(tileCoordinate) then
        CallLua.selectUnits(tileCoordinate.x, tileCoordinate.y, radius, numUnits, unitType)
        removeTopPriority()
        return true
    end
end

-- deselects all units
function deselectUnits(...)
    CallLua.deselectUnits()
    removeTopPriority()
    return true
end

-- makes the selected units move to the specified location
-- or does a special action if you call moveOrSpecialAction on the tile the unit is at
-- @param: tileX - x tile coordinate where you want to move or do the special action
-- @param: tileY - y tile coordinate where you want to move or do the special action
function moveOrSpecialAction(tileCoordinate, ...)
    if(tileCoordinate) then
        CallLua.moveOrSpecialAction(tileCoordinate.x, tileCoordinate.y)
        removeTopPriority()
    end
    return true
end

-- when a building is ready, you can place a building with this function
-- @param: tileX - x tile coordinate where you want to place the building
-- @param: tileY - y tile coordinate where you want to place the building
function placeBuilding(tileCoordinate, ...)
    if(tileCoordinate and (getGlobal('buildingPanelReady0') > -1 or getGlobal('buildingPanelReady2') > -1)) then
        CallLua.placeBuilding(tileCoordinate.x, tileCoordinate.y)
        removeTopPriority()
        return true
    -- if you can't place the building at tileX, tileY, then just remove the top priority
    end
end

-- tells your builder to set up your base where it is
-- if you want to set up your base at a specific location, move your builder there
-- then call this function
function setUpBase(...)
    CallLua.setUpBase()
    removeTopPriority()
    return true
end


-- UTILITY FUNCTIONS
-- retrieves important global information like getting the location of your base
-- @param globalVarName - string of the global variable name (you can see a list at XXX)
function getGlobal(globalVarName, ...)
    -- note that the function names are not the same as the other functions are
    -- i thought that this name (getLuaJGlobal) provided more clarity for the name
    -- but that it was redundant in lua code
    if(globalVarName) then
        return CallLua.getLuaJGlobal(globalVarName)
    end
end

-- Allows the user to draw text on the screen
-- @param :
function drawText(screenCoordinate, text, ...)
    if(screenCoordinate and text) then
        CallLua.drawText(screenCoordinate.x, screenCoordinate.y, text)
        return true
    end
end

-- PRIORITY FUNCTIONS
function addPriority(functionName, parameterTable, priority)
    return CallLua.addPriority(functionName, parameterTable, priority)
end

function getTopPriority(...)
    return CallLua.getTopPriority()
end

function removeTopPriority(...)
    print('removing top priority')
    return CallLua.removeTopPriority()
end

function getAllPriorities(...)
    return CallLua.getAllPriorities()
end


function clearPriorities(...)
    CallLua.clearPriorities()
    return true
end