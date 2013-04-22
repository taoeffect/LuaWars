-- This lua script pretty much acts as our Lua Library for Lua Wars
print("running myScript");

-- note that i didn't have to say CallLua = "XXX"
-- but if i had just had the require statement, then i would have to call
-- org.luawars.LuaJScripting.CallLua:createEntity(x1, x2), at least i think this
CallLua = luajava.bindClass('org.luawars.LuaJScripting.CallLua')

pCount = 0
pTable = {}


for k,v in pairs(_G) do
    print("Global key", k, "value", v)
end

-- On the right side of the screen, it has the 4 panels and each panel has buttons on it
-- creates a unit (this includes buildings too) based on which panel/button you choose
-- note that buttons go from topleft to bottom right
-- for example, if you build (panelId = 0 and buttonNum = 0) it will create a barracks
-- or if you build (panelId = 1, and buttonNum = 1) it will create a soldierXXX?
-- @param: panelId - takes an integer (1 to 5 inclusive)
-- @param: buttonNum - takes an integer (1 to X, where X is dependent on the panel)
function createUnit(panelId, buttonNum, ...)
	print("createUnit: " .. tostring(panelId) .. "," .. tostring(buttonNum))
    CallLua:createUnit(panelId, buttonNum)
    return true
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
    CallLua:selectUnits(tileCoordinate.x, tileCoordinate.y, radius, numUnits, unitType)
    return true
end

-- deselects all units
function deselectUnits(...)
    CallLua:deselectUnits()
    return true
end

-- makes the selected units move to the specified location
-- or does a special action if you call moveOrSpecialAction on the tile the unit is at
-- @param: tileX - x tile coordinate where you want to move or do the special action
-- @param: tileY - y tile coordinate where you want to move or do the special action
function moveOrSpecialAction(tileCoordinate, ...)
	CallLua:moveOrSpecialAction(tileCoordinate.x, tileCoordinate.y)
	return true
end

-- when a building is ready, you can place a building with this function
-- @param: tileX - x tile coordinate where you want to place the building
-- @param: tileY - y tile coordinate where you want to place the building
function placeBuilding(tileCoordinate, panel, button, ...)
    if CallLua:isUnitReady(panel, button) then
        CallLua:placeBuilding(tileCoordinate.x, tileCoordinate.y, panel, button)
        return true
    end
end

-- tells your builder to set up your base where it is
-- if you want to set up your base at a specific location, move your builder there
-- then call this function
function setUpBase(...)
    CallLua:setUpBase()
    return true
end


-- UTILITY FUNCTIONS
-- retrieves important global information like getting the location of your base
-- @param globalVarName - string of the global variable name (you can see a list at XXX)
function getGlobal(globalVarName, ...)
    -- note that the function names are not the same as the other functions are
    -- i thought that this name (getLuaJGlobal) provided more clarity for the name
    -- but that it was redundant in lua code
    return CallLua:getLuaJGlobal(globalVarName)
end

-- Allows the user to draw text on the screen
-- @param :
function drawText(screenCoordinate, text, ...)
    CallLua:drawText(screenCoordinate.x, screenCoordinate.y, text)
    return true
end

function selectUnitsAttack(tileX, tileY)
    CallLua:setUpBase(tileX, tileY)
    return true
end

-- PRIORITY FUNCTIONS
function addPriority(functionName, parameterTable, priority)
    pCount = pCount + 1
    pTable[tostring(pCount)] = {functionName, parameterTable, priority}
    CallLua:addPriority(functionName, parameterTable, priority, pCount)
    return true
end

function getTopPriority(...)
    return pTable[CallLua:getTopPriority()]
end

function removeTopPriority(...)
    local idx = tostring(CallLua:removeTopPriority())
    if (idx ~= "0") then
        local v = pTable[idx]
--        print('removing top priority: ' .. idx)
--        print('table: ' .. table.show(pTable, 'pTable'))
        pTable[idx] = nil
        return v
    end
end

function getAllPriorities(...)
    return CallLua:getAllPriorities()
end


function clearPriorities(...)
    CallLua:clearPriorities()
    return true
end

-- see: http://lua-users.org/wiki/LuaTableSize
function getnEx (t)
    local max = 0
    for i, _ in pairs(t) do
        if type(i) == "number" and i>max then max=i end
    end
    return max
end



--[[
    from: http://lua-users.org/wiki/TableSerialization

	Author: Julio Manuel Fernandez-Diaz
	Date:	January 12, 2007
	(For Lua 5.1)

	Modified slightly by RiciLake to avoid the unnecessary table traversal in tablecount()

	Formats tables with cycles recursively to any depth.
	The output is returned as a string.
	References to other tables are shown as values.
	Self references are indicated.

	The string returned is "Lua code", which can be procesed
	(in the case in which indent is composed by spaces or "--").
	Userdata and function keys and values are shown as strings,
	which logically are exactly not equivalent to the original code.

	This routine can serve for pretty formating tables with
	proper indentations, apart from printing them:

		print(table.show(t, "t"))	-- a typical use

	Heavily based on "Saving tables with cycles", PIL2, p. 113.

	Arguments:
		t is the table.
		name is the name of the table (optional)
		indent is a first indentation (optional).
--]]
function table.show(t, name, indent)
    local cart	  -- a container
    local autoref  -- for self references

    --[[ counts the number of elements in a table
    local function tablecount(t)
        local n = 0
        for _, _ in pairs(t) do n = n+1 end
        return n
    end
    ]]
    -- (RiciLake) returns true if the table is empty
    local function isemptytable(t) return next(t) == nil end

    local function basicSerialize (o)
        local so = tostring(o)
        if type(o) == "function" then
            return so
--            local info = debug.getinfo(o, "S")
--            -- info.name is nil because o is not a calling level
--            if info.what == "C" then
--                return string.format("%q", so .. ", C function")
--            else
--                -- the information is defined through lines
--                return string.format("%q", so .. ", defined in (" ..
--                        info.linedefined .. "-" .. info.lastlinedefined ..
--                        ")" .. info.source)
--            end
        elseif type(o) == "number" or type(o) == "boolean" then
            return so
        else
            return string.format("%q", so)
        end
    end

    local function addtocart (value, name, indent, saved, field)
        indent = indent or ""
        saved = saved or {}
        field = field or name

        cart = cart .. indent .. field

        if type(value) ~= "table" then
            cart = cart .. " = " .. basicSerialize(value) .. ";\n"
        else
            if saved[value] then
                cart = cart .. " = {}; -- " .. saved[value]
                        .. " (self reference)\n"
                autoref = autoref ..  name .. " = " .. saved[value] .. ";\n"
            else
                saved[value] = name
                --if tablecount(value) == 0 then
                if isemptytable(value) then
                    cart = cart .. " = {};\n"
                else
                    cart = cart .. " = {\n"
                    for k, v in pairs(value) do
                        k = basicSerialize(k)
                        local fname = string.format("%s[%s]", name, k)
                        field = string.format("[%s]", k)
                        -- three spaces between levels
                        addtocart(v, fname, indent .. "	", saved, field)
                    end
                    cart = cart .. indent .. "};\n"
                end
            end
        end
    end

    name = name or "__unnamed__"
    if type(t) ~= "table" then
        return name .. " = " .. basicSerialize(t)
    end
    cart, autoref = "", ""
    addtocart(t, name, indent)
    return cart .. autoref
end
