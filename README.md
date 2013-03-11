LuaWars
=======

March 10, 2013 Notes
Author: Greg Slepak

- There are two kryo(net) versions in the 'lib' folder. The reason for this is because the old STKRTS only works with the old kryo-1.01.jar and kryonet-1.01.jar files. It will fail to serialize the objects properly if you use the latest versions. We do not need this stuff unless we want to to support network play anyway. If we decide to support network play, we'll of course use the latest versions.

- To support the latest twl.jar version I had to edit these resource files: gui.xml, guiTheme.xml, and Eforen.xml. I had to replace hvsplit/vsplit/hsplit/texture with 'area' tags, and modify the 'cursor' tags. I also had to add extra parameters to some of the stuff in guiTheme.xml to stop it from bitching. It all seems to work just as it did before, although now I get this warning in the console:

		Mar 10, 2013 10:18:35 PM de.matthiasmann.twl.utils.XMLParser warnUnusedAttributes
		WARNING: Unused attribute 'nocenter' on 'area' at  START_TAG seen ...button.hoverframe" xywh="80,80,20,20" border="5" nocenter="true"/>... @15:87 in jar:file:/Users/gslepak/Documents/Gregs%20Documents/School%20Stuff/UF/UF%20Senior/GamesAI.noindex/LuaWars.git/lib/resources.jar!/resources/themes/gui.xml

- I manually updated the xml UI files, guessing based on these links as to how to do it:
-- http://twl.l33tlabs.org/themeformat.html
-- http://slick.javaunlimited.net/viewtopic.php?f=18&t=2558&p=15015

- Note that the TWL Theme Editor is supposed to be able to update the files automatically, but because it didn't run on my laptop (OS X 10.8.2), I ended up doing it by hand, and might have made some mistakes (but I haven't noticed anything...).

- I modified some of the source to get things running, but no major changes (mostly just giving HTMLTextAreaModel the getHtml method back).

- I also created our own logging class... because somehow all the existing popular logging classes for Java are complete shit. Please use it to write your logging stuff, it's org.luawars.Log. I also created two configurations for the project: Debug and Release. So far the only difference between the two is that Debug will show Debug messages as well as all the others.