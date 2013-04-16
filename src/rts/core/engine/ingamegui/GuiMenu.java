package rts.core.engine.ingamegui;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SpriteSheet;

import rts.core.engine.Engine;
import rts.core.engine.layers.entities.EData;
import rts.utils.ResourceManager;

public class GuiMenu {

	private Engine engine;
	private ArrayList<Integer> buildingList;
    private ArrayList<GuiPanel> panels;
	private GuiButton[] menusButton;
	private GuiButton repairButton;
	private GuiButton sellButton;
	private SpriteSheet sheet;

	private int selected;
	private boolean sellMod;
	private boolean repairMod;

	public GuiMenu(Engine engine) {
		this.engine = engine;
		this.buildingList = new ArrayList<Integer>();
	}

	public void init() {
		sheet = ResourceManager.getSpriteSheet("buttons");
		menusButton = new GuiButton[5];

		for (int i = 0; i < menusButton.length; i++) {
			menusButton[i] = new GuiButton(engine, sheet.getSprite(i, 0), (engine.getContainer().getWidth() - 190) + (i * 30), 240);
			menusButton[i].setTabButton(true);
		}

		// Needed combinations

		// Repair
		repairButton = new GuiButton(engine, sheet.getSprite(5, 0), engine.getContainer().getWidth() - 190, 200);
		repairButton.setTabButton(true);
		repairButton.setName("Repair");
		repairButton.setAlwaysEnable(true);

		sellButton = new GuiButton(engine, sheet.getSprite(6, 0), engine.getContainer().getWidth() - 160, 200);
		sellButton.setTabButton(true);
		sellButton.setName("Sell");
		sellButton.setAlwaysEnable(true);

		// Building
		menusButton[0].addEnableCombination(new int[] { EData.BUILDING_BUILDER });
		menusButton[0].setName("Building");

		// Defense
		menusButton[1].addEnableCombination(new int[] { EData.BUILDING_BUILDER });
		menusButton[1].setName("Defense");

		// Earth
		menusButton[2].addEnableCombination(new int[] { EData.BUILDING_BARRACK });
		menusButton[2].addEnableCombination(new int[] { EData.BUILDING_CONSTRUCTOR });
		menusButton[2].addEnableCombination(new int[] { EData.BUILDING_BIG_CONSTRUCTOR });
		menusButton[2].setName("Vehicle");

		// Air
		menusButton[3].addEnableCombination(new int[] { EData.BUILDING_STARPORT });
		menusButton[3].addEnableCombination(new int[] { EData.BUILDING_STARPORT_2 });
		menusButton[3].setName("Aircraft");

		// Marine
		menusButton[4].addEnableCombination(new int[] { EData.BUILDING_PORT });
		menusButton[4].setName("Marine");

		selected = -1;

		// Panels
		panels = GuiPanelFactory.getAllPanels(engine, this);
	}

	public void resizeMenu() {
		for (int i = 0; i < menusButton.length; i++) {
			menusButton[i].setLocation((engine.getContainer().getWidth() - 190) + (i * 30), 240);
			menusButton[i].setTabButton(true);
		}
		repairButton.setLocation(engine.getContainer().getWidth() - 190, 200);
		repairButton.setTabButton(true);
		sellButton.setLocation(engine.getContainer().getWidth() - 160, 200);
		sellButton.setTabButton(true);
		for (int i = 0; i < panels.size(); i++) {
			panels.get(i).setLocation(engine.getContainer().getWidth() - 190, 280);
		}
	}

	public void mousePressed(int button, int x, int y) {
		if (button == Input.MOUSE_LEFT_BUTTON) {
			for (int i = 0; i < menusButton.length; i++) {
				if (menusButton[i].isMouseOver(x, y) && menusButton[i].isEnable()) {
					for (int j = 0; j < menusButton.length; j++) {
						menusButton[j].setImage(sheet.getSprite(j, 0));
					}
					menusButton[i].setImage(sheet.getSprite(i, 1));
					selected = i;
					return;
				}
			}

			sellMod = false;
			repairMod = false;
			repairButton.setImage(sheet.getSprite(5, 0));
			sellButton.setImage(sheet.getSprite(6, 0));

			if (sellButton.isMouseOver(x, y)) {
				sellButton.setImage(sheet.getSprite(6, 1));
				sellMod = true;
				return;
			} else {
				if (repairButton.isMouseOver(x, y)) {
					repairButton.setImage(sheet.getSprite(5, 1));
					repairMod = true;
					return;
				}
			}

			for (int i = 0; i < panels.size(); i++) {
				panels.get(i).mousePressed(x, y);
			}
		}
	}

	public void blinkButton(int id, int delta) {
		menusButton[id].blink(delta);
	}

	public void stopBlinkButton(int id) {
		menusButton[id].resetBlink();
	}

	public void increaseBuildLimit(int panelId, int increase) {
		panels.get(panelId).increaseBuildLimit(increase);
	}

	public void decreaseBuildLimit(int panelId, int decrease) {
		panels.get(panelId).decreaseBuildLimit(decrease);
	}

	public void render(GameContainer container, Graphics g) {
		for (int i = 0; i < menusButton.length; i++) {
			menusButton[i].render(container, g);
		}

		repairButton.render(container, g);
		sellButton.render(container, g);

		for (int i = 0; i < menusButton.length; i++) {
			menusButton[i].renderInfo(container, g);
		}

		repairButton.renderInfo(container, g);
		sellButton.renderInfo(container, g);

		if (selected != -1) {
			panels.get(selected).render(container, g);
		}
	}

	public void update(GameContainer container, int delta) {
		for (int i = 0; i < menusButton.length; i++) {
			menusButton[i].checkEnable(buildingList, delta, true);
		}
		for (int i = 0; i < panels.size(); i++) {
			panels.get(i).update(buildingList, delta, (selected == i));
		}
		repairButton.checkEnable(buildingList, delta, true);
		sellButton.checkEnable(buildingList, delta, true);
	}

	public void addEntityToBuildingList(int type) {
		buildingList.add(new Integer(type));
	}

	public void removeEntityFromBuildingList(int type) {
		buildingList.remove(new Integer(type));
	}

	public boolean containRadarOrDevCenter() {
		return buildingList.contains(new Integer(EData.BUILDING_RADAR)) || buildingList.contains(new Integer(EData.BUILDING_DEV_CENTER));
	}

	public void clear() {
		buildingList.clear();
		selected = -1;
		sellMod = false;
		repairMod = false;
		for (int i = 0; i < menusButton.length; i++) {
			menusButton[i].clear();
		}

		for (int i = 0; i < panels.size(); i++) {
			panels.get(i).clear();
		}
		repairButton.clear();
		sellButton.clear();
	}

	public boolean isRepairMod() {
		return repairMod;
	}

	public boolean isSellMod() {
		return sellMod;
	}

    public ArrayList<GuiPanel> getPanels() {
        return panels;
    }

}
