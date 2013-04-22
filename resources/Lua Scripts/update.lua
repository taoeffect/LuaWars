-- this file updates every frame


function runTopPriority()
    local tp = removeTopPriority()
    if (tp) then
        local func = tp[1]
        local parameters = tp[2]
        local priority = tp[3]
--        print('calling: ' .. tostring(func))
--        print('pTable.size = '..tostring(CallLua:numPrios()))
        if func(table.unpack(parameters)) == nil then
            runTopPriority()
            addPriority(func, parameters, priority)
        end
    end
end

runTopPriority()

--print('building panel 0' ..getGlobal('buildingPanel0'))
--print('building panel ready 0' ..getGlobal('buildingPanel0'))