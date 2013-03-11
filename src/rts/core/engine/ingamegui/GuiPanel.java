package rts.core.engine.ingamegui;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class GuiPanel {

	private ArrayList<GuiButton> buttons;
	private ArrayList<GuiButton> waitingList;
	private GuiMenu menu;
	private int id;
	private int buildLimit;
	private int x;
	private int y;

	public GuiPanel(GuiMenu menu, int id) {
		this.menu = menu;
		this.id = id;
		buttons = new ArrayList<GuiButton>();
		waitingList = new ArrayList<GuiButton>();
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).setLocation(x + ((i % 3) * 60), y + ((i / 3) * 60));
		}
	}

	public void mousePressed(int x, int y) {
		for (int i = 0; i < buttons.size(); i++) {
			if (buttons.get(i).isMouseOver(x, y) && buttons.get(i).isEnable()) {
				buttons.get(i).launchCreateEntityProcess();
			}
		}
	}

	public void addButton(GuiButton button) {
		button.setPanel(this);
		button.setLocation(x + ((buttons.size() % 3) * 60), y + ((buttons.size() / 3) * 60));
		buttons.add(button);
	}

	public void addButtonToWait(GuiButton button) {
		waitingList.add(button);
	}

	public void render(GameContainer container, Graphics g) {
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).render(container, g);
		}
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).renderInfo(container, g);
		}
	}

	public void update(ArrayList<Integer> buildingList, int delta, boolean visible) {
		boolean oneProcessReady = false;
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).checkEnable(buildingList, delta, visible);
			if (visible)
				buttons.get(i).checkCancelProcess();
			if (!oneProcessReady && buttons.get(i).hasProcessReady()) {
				oneProcessReady = true;
			}
		}

		// update only the waiting process
		for (int i = 0; i < buildLimit; i++) {
			if (i < waitingList.size()) {
				if (waitingList.get(i).update(delta)) {
					waitingList.remove(i);
				}
			} else {
				break;
			}
		}

		if (oneProcessReady) {
			menu.blinkButton(id, delta);
		} else {
			menu.stopBlinkButton(id);
		}
	}

	public void increaseBuildLimit(int increase) {
		buildLimit += increase;
	}

	public void decreaseBuildLimit(int decrease) {
		buildLimit -= decrease;
	}

	public void clear() {
		buildLimit = 0;
		waitingList.clear();
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).clear();
		}
	}

}
