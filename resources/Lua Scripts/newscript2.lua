print("running newscript2.lua")

local Global = luajava.bindClass("org.luawars.LuaJScripting.LuaJGlobal")

--print(Global.money)
--print(Global.base.x)
--print(Global.base.y)
print(Global.panelBuilding[0])
print(Global.panelBuilding[1])
print(Global.panelBuilding[2])

--print(Global.panelBuilding[5])
--print(Global.panelBuilding[6])
--print(Global.panelBuilding[7])


createUnit(1,1)