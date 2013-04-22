-- this file updates every frame

local tp = getTopPriority()

if (tp) then
    priorityFunction = tp[1]
    priorityParameters = tp[2]
    print(priorityParameters)
    print(table.unpack(priorityParameters))
    priorityFunction(table.unpack(priorityParameters))
    --print('has top priority')
    --removeTopPriority()
else
    --print('no top priority')
end

--print('building panel 0' ..getGlobal('buildingPanel0'))
--print('building panel ready 0' ..getGlobal('buildingPanel0'))