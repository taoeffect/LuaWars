package rts.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.ComboBox;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ToggleButton;
import de.matthiasmann.twl.ValueAdjusterInt;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.SimpleChangableListModel;
import de.matthiasmann.twl.model.SimpleIntegerModel;

import rts.core.Game;
import rts.utils.Configuration;
import rts.utils.Resolution;
import rts.utils.ResourceManager;

/**
 * Menu associated to the options.
 * 
 * @author Vince
 * 
 */
public class OptionsView extends View {

	private Image background;
	private Image title;

	// TWL
	private Widget w;
	private EditField pseudoField;
	private ToggleButton checkBox;
	private ComboBox<Resolution> resolutionCombo;
	private SimpleIntegerModel musicModel;
	private SimpleIntegerModel soundModel;
	private Button exitButton;

	@Override
	public void initResources() {
		background = ResourceManager.getImage("options_view_background");
		title = ResourceManager.getSpriteSheet("menutitles").getSprite(0, 3);
	}

	@Override
	public void initTwlComponent() {

		int x = container.getWidth() / 2;
		int y = container.getHeight() / 2;

		w = new Widget();
		w.setSize(300, 285);
		w.setPosition(x - 150, y - 140);

		Label label = new Label("Pseudo:");
		label.setPosition(20, 30);
		w.add(label);

		pseudoField = new EditField();
		pseudoField.setMaxTextLength(18);
		pseudoField.setText(Configuration.getPseudo());
		pseudoField.setSize(160, 16);
		pseudoField.setPosition(110, 20);
		w.add(pseudoField);

		label = new Label("Resolution:");
		label.setPosition(20, 82);
		w.add(label);

		resolutionCombo = new ComboBox<Resolution>();
		resolutionCombo.setSize(110, 20);
		resolutionCombo.setPosition(110, 72);
		SimpleChangableListModel<Resolution> model = new SimpleChangableListModel<Resolution>();

		ArrayList<Resolution> array = new ArrayList<Resolution>();
		try {
			DisplayMode[] modes = Display.getAvailableDisplayModes();
			for (int i = 0; i < modes.length; i++) {
				DisplayMode d = modes[i];
				if (d.getWidth() >= 800 && d.getWidth() <= 1280 && d.getHeight() >= 600 && d.getHeight() <= 1024) {
					Resolution r = new Resolution(d.getWidth(), d.getHeight());
					if (!array.contains(r)) {
						array.add(r);
					}
				}
			}
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		Collections.sort(array);

		int selected = 0;
		for (int i = 0; i < array.size(); i++) {
			model.addElement(array.get(i));
			if (array.get(i).getWidth() == Configuration.getWidth() && array.get(i).getHeight() == Configuration.getHeight()) {
				selected = i;
			}
		}
		resolutionCombo.setModel(model);
		resolutionCombo.setSelected(selected);
		w.add(resolutionCombo);

		label = new Label("FS:");
		label.setPosition(228, 82);
		w.add(label);

		checkBox = new ToggleButton();
		checkBox.setTheme("checkbox");
		checkBox.setActive(Configuration.isFullScreen());
		checkBox.setSize(20, 20);
		checkBox.setPosition(256, 72);
		w.add(checkBox);

		label = new Label("Music:");
		label.setPosition(20, 135);
		w.add(label);

		musicModel = new SimpleIntegerModel(0, 100, (int) (Configuration.getMusicVolume() * 100));
		ValueAdjusterInt vai = new ValueAdjusterInt(musicModel);
		vai.setSize(168, 20);
		vai.setPosition(108, 125);
		w.add(vai);

		label = new Label("Sound:");
		label.setPosition(20, 185);
		w.add(label);

		soundModel = new SimpleIntegerModel(0, 100, (int) (Configuration.getSoundVolume() * 100));
		vai = new ValueAdjusterInt(soundModel);
		vai.setSize(168, 20);
		vai.setPosition(108, 175);
		w.add(vai);

		Button applyButton = new Button("Apply");
		applyButton.setSize(100, 20);
		applyButton.setPosition(80, 230);
		applyButton.addCallback(new Runnable() {
			@Override
			public void run() {
				try {
					if (pseudoField.getText().isEmpty()) {
						Configuration.setPeudo("Player");
					} else
						Configuration.setPeudo(pseudoField.getText());

					Resolution r = getSelectedResolution();
					if (r != null) {
						Configuration.setWidth(r.getWidth());
						Configuration.setHeight(r.getHeight());
					}
					Configuration.setFullScreen(checkBox.isActive());
					Configuration.setMusicVolume(((float) musicModel.getValue()) / 100);
					Configuration.setSoundVolume(((float) soundModel.getValue()) / 100);
					Configuration.saveNewConfig();
					game.applyCurrentConfiguration();
					game.initAllTWLComponents();
					game.reloadTWL();
					initTWL = false;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (SlickException e) {
					e.printStackTrace();
				}
			}
		});
		w.add(applyButton);

		// Set root

		root.add(w);

		exitButton = new Button("Back");
		exitButton.setSize(70, 30);
		exitButton.setPosition(x - 350, y + 250);
		exitButton.addCallback(new Runnable() {
			@Override
			public void run() {
				game.enterState(Game.MAIN_MENU_VIEW_ID, new FadeOutTransition(), new FadeInTransition());
			}
		});
		root.add(exitButton);
	}

	public Resolution getSelectedResolution() {
		if (resolutionCombo.getSelected() != -1) {
			return (Resolution) ((SimpleChangableListModel<Resolution>) resolutionCombo.getModel()).getEntry(resolutionCombo.getSelected());
		}
		return null;
	}

	@Override
	public void render(GameContainer container, StateBasedGame sbGame, Graphics g) throws SlickException {
		g.drawImage(background, 0, 0);
		g.drawImage(title, container.getWidth() / 2 - 65, container.getHeight() / 2 - 250);
		super.render(container, game, g);
	}

	@Override
	public int getID() {
		return Game.OPTIONS_VIEW_ID;
	}

}
