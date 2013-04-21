-- this file updates every frame
require 'myScript'
if(getTopPriority()) then
    priorityFunction = getTopPriority()[0]
    priorityParameters = getTopPriority()[1]
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