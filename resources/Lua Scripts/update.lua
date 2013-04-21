-- this file updates every frame

if(getTopPriority()) then
    priorityFunction = getTopPriority()[1]
    priorityParameters = getTopPriority()[2]
    --print(priorityFunction)
    --print(table.unpack(priorityParameters))
    priorityFunction(table.unpack(priorityParameters))
    --print('has top priority')
    --removeTopPriority()
else
    --print('no top priority')
end

--print('building panel 0' ..getGlobal('buildingPanel0'))
--print('building panel ready 0' ..getGlobal('buildingPanel0'))