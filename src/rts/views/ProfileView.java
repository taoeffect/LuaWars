package rts.views;

/**
 * Created with IntelliJ IDEA.
 * User: Maximus
 * Date: 4/5/13
 * Time: 5:27 PM
 * To change this template use File | Settings | File Templates.
 */
import de.matthiasmann.twl.*;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.TextArea;
import de.matthiasmann.twl.model.*;
import de.matthiasmann.twl.textarea.HTMLTextAreaModel;
import de.matthiasmann.twl.utils.TintAnimator;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import rts.core.Game;
import rts.utils.Configuration;
import rts.utils.ResourceManager;
import de.matthiasmann.twl.textarea.HTMLTextAreaModel;

import java.awt.*;
import java.awt.Event;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created with IntelliJ IDEA.
 * User: Maximus
 * Date: 4/5/13
 * Time: 5:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProfileView extends View
{
    //GUI
    private Image background;
    //Table
    private Table table;
    private SimpleTableModel simpleTable;
    private TableSingleSelectionModel selectionModel;

    //Label
    private Label L1;

    //Text Area
    private HTMLTextAreaModel chatModel;
    private EditField textField;

    //Panel
    private Widget panel;
    private Widget createpanel;
    private Widget selectPanel;

    //Buttons
    //private ToggleButton checkbox;
    private Button startButton;
    private Button backButton;
    private Button createButton;
    private Button deleteButton;

    @Override
    public void initResources()
    {
        //To change body of implemented methods use File | Settings | File Templates.
        background = ResourceManager.getImage("profile_view_background");
    }

    @Override
    public void initTwlComponent()
    {
        int x = container.getWidth() / 2 - 370;// 25
        int y = container.getHeight() / 2 - 250;// 50

        //Panel
        //L2 = new Label("Profiles");
        //L2.setPosition(20,20);
        panel = new Widget();
        panel.setPosition(300,100);
        panel.setSize(400,380);

        //Selected Panel
        selectPanel = new Widget();
        selectPanel.setSize(200,80);
        selectPanel.setPosition(60, 400);
        selectPanel.setVisible(false);
        root.add(selectPanel);

        //Create Panel/Profile
        String Label2[] = new String[1];
        Label2[0] = "Profile Names";
        simpleTable = new SimpleTableModel(Label2);
        table = new Table();
        table.setSize(380, 300);
        table.setPosition(10, 9);
        table.setModel(simpleTable);
        selectionModel = new TableSingleSelectionModel();
        table.setSelectionManager(new TableRowSelectionManager(selectionModel));
        selectionModel.setLeadIndex(0);
        selectionModel.setAnchorIndex(2);
        selectionModel.rowsInserted(0, 1);
        selectionModel.rowsInserted(1,1);
        selectionModel.rowsInserted(2, 1);
        table.setColumnWidth(0, 75);
        panel.add(table);
        root.add(panel);

        createpanel = new Widget();
        createpanel.setPosition(60,100);
        createpanel.setSize(200,80);
        createpanel.setVisible(false);

        L1 = new Label("Enter Profile Name");
        L1.setPosition(40,15);
        createpanel.add(L1);

        TextArea textArea = new TextArea();
        chatModel = new HTMLTextAreaModel("");
        textArea.setSize(160,50);
        textArea.setModel(chatModel);

        textField = new EditField();
        textField.setPosition(10,25);
        textField.setSize(175, 40);
        textField.setVisible(false);

        textField.addCallback(new EditField.Callback() {
            public void callback(int key) {
                if (key == Keyboard.KEY_RETURN) {
                    if (!textField.getText().trim().isEmpty())
                    {
                        try
                        {
                            simpleTable.addRow(textField.getText());
                            Configuration.setName1(textField.getText());
                            selectionModel.setSelection(0, 1);
                            createpanel.setVisible(false);
                            textField.setVisible(false);
                            Configuration.saveNewConfig();
                            game.applyCurrentConfiguration();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (SlickException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        createpanel.add(textField);
        root.add(createpanel);

        if (!Configuration.getName1().equals("Player"))
        {
           simpleTable.addRow(Configuration.getName1());
        }
        if (!Configuration.getName2().equals("Player"))
        {
            simpleTable.addRow(Configuration.getName2());
        }
        if (!Configuration.getName3().equals("Player"))
        {
            simpleTable.addRow(Configuration.getName3());
        }
        //Buttons
        deleteButton = new Button("Delete");
        deleteButton.setPosition(60,300);
        deleteButton.setSize(150,40);
        deleteButton.addCallback(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if(selectionModel.isSelected(0))
                    {
                         Configuration.setName1("Player");
                         Configuration.setProfile1("Default");
                         simpleTable.deleteRow(0);
                    }
                    else if(selectionModel.isSelected(1))
                    {
                         simpleTable.getCell(0,1);
                            simpleTable.deleteRow(1);
                    }
                    else if(selectionModel.isSelected(0))
                    {
                        simpleTable.getCell(0,2);
                        simpleTable.deleteRow(2);
                    }
                    Configuration.saveNewConfig();
                    game.applyCurrentConfiguration();
                    } catch (IOException e) {
					e.printStackTrace();
				    } catch (SlickException e) {
					e.printStackTrace();
				    }
            }
      });
        root.add(deleteButton);

        createButton = new Button("Create");
        createButton.setPosition(60,200);
        createButton.setSize(150,40);
        createButton.addCallback(new Runnable()
        {
          @Override
          public void run()
          {
              //try
              //{
                //createpanel.setVisible(true);
                //simpleTable.addRow(textField.getText());
                //Configuration.setName1(textField.getText());
                //selectionModel.setSelection(0, 1);
                //Configuration.saveNewConfig();
                //game.applyCurrentConfiguration();
                createpanel.setVisible(true);
                textField.setVisible(true);
              //} catch (IOException e) {
               //   e.printStackTrace();
              //} catch (SlickException e) {
               //   e.printStackTrace();
              //}
          }
        });
        root.add(createButton);

        startButton = new Button("Start");
        startButton.setPosition(300,500);
        startButton.setSize(150,40);
        startButton.addCallback(new Runnable()
        {
            @Override
            public void run()
            {
               if(!Configuration.getName1().equals("Player") && Configuration.getName2().equals("Player") && Configuration.getName3().equals("Player"))
                {
                    try {
                    game.getNetworkManager().createServer();
                    game.getNetworkManager().joinServer("localhost");
                    game.enterState(Game.CREATE_VIEW_ID, new FadeOutTransition(), new FadeInTransition());
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if(!Configuration.getName1().equals("Player") && !Configuration.getName2().equals("PLayer") || !Configuration.getName3().equals("PLayer"))
                {
                    if (selectionModel.isSelected(0))
                    try {
                        game.getNetworkManager().createServer();
                        game.getNetworkManager().joinServer("localhost");
                        game.enterState(Game.CREATE_VIEW_ID, new FadeOutTransition(), new FadeInTransition());
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    else if (selectionModel.isSelected(1))
                        try {
                                game.getNetworkManager().createServer();
                                game.getNetworkManager().joinServer("localhost");
                                game.enterState(Game.CREATE_VIEW_ID, new FadeOutTransition(), new FadeInTransition());
                            } catch (IOException e)
                        {
                            e.printStackTrace();
                    }
                    else if (selectionModel.isSelected(2))
                    try {
                            game.getNetworkManager().createServer();
                            game.getNetworkManager().joinServer("localhost");
                            game.enterState(Game.CREATE_VIEW_ID, new FadeOutTransition(), new FadeInTransition());
                        } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        root.add(startButton);

        backButton = new Button("Back");
        backButton.setPosition(500, 500);
        backButton.setSize(150,40);
        backButton.addCallback(new Runnable ()
        {
            @Override
            public void run()
            {
                game.enterState(Game.MAIN_MENU_VIEW_ID, new FadeOutTransition(), new FadeInTransition());
            }

        });
        root.add(backButton);
    }

    @Override
    public void render(GameContainer container, StateBasedGame sbgame, Graphics g) throws SlickException {
        g.drawImage(background, 0, 0);
        super.render(container, sbgame, g);
    }

    @Override
    public int getID()
    {
        return Game.PROFILE_VIEW_ID;
    }
}
