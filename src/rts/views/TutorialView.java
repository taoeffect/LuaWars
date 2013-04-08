package rts.views;

import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;
import rts.core.Game;
import rts.utils.ResourceManager;

/**
 * Created with IntelliJ IDEA.
 * User: Maximus
 * Date: 4/5/13
 * Time: 5:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class TutorialView extends View
{
    private Image background;

    @Override
    public void initResources()
    {
        //To change body of implemented methods use File | Settings | File Templates.
        background = ResourceManager.getImage("network_view_background");
    }

    @Override
    public void initTwlComponent()
    {

    }

    @Override
    public void render(GameContainer container, StateBasedGame sbgame, Graphics g) throws SlickException {
        g.drawImage(background, 0, 0);
        super.render(container, sbgame, g);
    }

    @Override
    public int getID()
    {
        return Game.TUTORIAL_VIEW_ID;
    }
}
