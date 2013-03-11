package rts.views;

import java.io.IOException;
import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.Table;
import de.matthiasmann.twl.ThemeInfo;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.TableBase.CellWidgetCreator;
import de.matthiasmann.twl.model.AbstractTableModel;

import rts.core.Game;
import rts.core.engine.GameGoal;
import rts.core.network.INetworkDiscoverListener;
import rts.core.network.NetworkManager;
import rts.core.network.menu_tcp_containers.ServerState;
import rts.utils.ResourceManager;
import rts.utils.Timer;

/**
 * Menu associated to the network.
 * 
 * @author Vince
 * 
 */
public class NetworkView extends View {

	private NetworkManager manager;
	private Timer unreachableServerTimer;
	private Image background;
	private Image title;

	// Gui
	private Button createButton;
	private Button refreshButton;
	private TableServerModel tableModel;
	private Button joinButton;
	private EditField ipTextField;
	private Button exitButton;

	@Override
	public void initResources() {
		background = ResourceManager.getImage("network_view_background");
		title = ResourceManager.getSpriteSheet("menutitles").getSprite(0, 0);
		manager = game.getNetworkManager();
		unreachableServerTimer = new Timer(4000);
		unreachableServerTimer.setTimeComplete();
	}

	@Override
	public void initTwlComponent() {
		int x = container.getWidth() / 2;
		int y = container.getHeight() / 2;

		createButton = new Button("Create Game");
		createButton.setSize(80, 30);
		createButton.setPosition(x - 360, y - 240);
		createButton.addCallback(new Runnable() {
			@Override
			public void run() {
				create();
			}
		});
		root.add(createButton);

		refreshButton = new Button("Refresh");
		refreshButton.setSize(70, 30);
		refreshButton.setPosition(x + 250, y - 240);
		refreshButton.addCallback(new Runnable() {
			@Override
			public void run() {
				try {
					tableModel.clear();
					manager.discover();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		root.add(refreshButton);

		Table table = new Table();
		table.setSize(692, 367);
		table.setDefaultSelectionManager();
		tableModel = new TableServerModel();
		table.setModel(tableModel);
		table.registerCellRenderer(String.class, new EditFieldCellWidgetCreator());
		table.registerCellRenderer(ServerInfo.class, new ButtonCellWidgetCreator());
		table.setColumnWidth(0, 75);
		table.setColumnWidth(1, 75);
		table.setColumnWidth(2, 50);
		table.setColumnWidth(3, 200);
		table.setColumnWidth(4, 150);
		table.setColumnWidth(5, 100);

		ScrollPane scrollPane = new ScrollPane(table);
		scrollPane.setFixed(ScrollPane.Fixed.HORIZONTAL);
		scrollPane.setPosition(9, 6);
		scrollPane.setSize(702, 367);

		Widget widget = new Widget();
		widget.setSize(720, 380);
		widget.setPosition(x - 360, y - 200);
		widget.add(scrollPane);
		root.add(widget);

		widget = new Widget();
		widget.setSize(720, 50);
		widget.setPosition(x - 360, y + 200);

		Label label = new Label("Connection using ip:");
		label.setPosition(20, 25);
		widget.add(label);

		ipTextField = new EditField();
		ipTextField.setText("localhost");
		ipTextField.setMaxTextLength(15);
		ipTextField.setSize(120, 15);
		ipTextField.setPosition(170, 15);
		widget.add(ipTextField);

		joinButton = new Button();
		joinButton.setText("Join");
		joinButton.setSize(70, 20);
		joinButton.setPosition(310, 14);
		joinButton.addCallback(new Runnable() {
			@Override
			public void run() {
				join(ipTextField.getText());
			}
		});
		widget.add(joinButton);

		exitButton = new Button("Exit");
		exitButton.setSize(70, 30);
		exitButton.setPosition(x - 360, y + 255);
		exitButton.addCallback(new Runnable() {
			@Override
			public void run() {
				NetworkView.this.game.enterState(Game.MAIN_MENU_VIEW_ID, new FadeOutTransition(), new FadeInTransition());
			}
		});
		root.add(exitButton);

		root.add(widget);

	}

	private void create() {
		try {
			createButton.setEnabled(false);
			joinButton.setEnabled(false);
			game.getNetworkManager().createServer();
			game.getNetworkManager().joinServer("localhost");
			game.enterState(Game.CREATE_VIEW_ID, new FadeOutTransition(), new FadeInTransition());
		} catch (IOException e) {
			createButton.setEnabled(true);
			joinButton.setEnabled(true);
			e.printStackTrace();
		}
	}

	private void join(String ip) {
		try {
			createButton.setEnabled(false);
			joinButton.setEnabled(false);
			game.getNetworkManager().joinServer(ip);
			game.enterState(Game.CREATE_VIEW_ID, new FadeOutTransition(), new FadeInTransition());
		} catch (IOException e) {
			createButton.setEnabled(true);
			joinButton.setEnabled(true);
			unreachableServerTimer.resetTime();
		}
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		super.enter(container, game);
		createButton.setEnabled(true);
		joinButton.setEnabled(true);
		manager.launchClientListening(new INetworkDiscoverListener() {
			@Override
			public void receiveServerInfos(String state, String gameType, String nbPlayer, String maxPlayer, String mapName, String ip) {
				ServerInfo si = new ServerInfo();
				si.state = state;
				si.gameType = gameType;
				si.nbPlayer = nbPlayer;
				si.maxPlayer = maxPlayer;
				si.mapName = mapName.split("]")[1].trim();
				si.ip = ip;
				tableModel.addServerInfo(si);
			}
		});
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		super.leave(container, game);
		manager.stopClientListening();
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.drawImage(background, 0, 0);
		super.render(container, game, g);
		int x = container.getWidth() / 2 - 370;
		int y = container.getHeight() / 2 - 250;
		g.drawImage(title, x + 290, y - 40);

		if (!unreachableServerTimer.isTimeComplete()) {
			g.setColor(Color.red);
			g.drawString("Server is unreachable !", x + 468, y + 466);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		super.update(container, game, delta);
		unreachableServerTimer.update(delta);
	}

	@Override
	public int getID() {
		return Game.NETWORK_VIEW_ID;
	}

	private class TableServerModel extends AbstractTableModel {

		private ArrayList<ServerInfo> datas;

		public TableServerModel() {
			datas = new ArrayList<ServerInfo>();
		}

		public void clear() {
			datas.clear();
			fireAllChanged();
		}

		public void addServerInfo(ServerInfo si) {
			datas.add(si);
			fireRowsInserted(datas.size() - 1, datas.size());
		}

		@Override
		public Object getCell(int row, int column) {
			if (row < datas.size()) {
				if (column == 5)
					return datas.get(row);
				else
					return datas.get(row).getInfo(column);
			}
			return "";
		}

		@Override
		public int getNumRows() {
			return datas.size();
		}

		@Override
		public String getColumnHeaderText(int colomn) {
			switch (colomn) {
			case 0:
				return "State";
			case 1:
				return "Game Type";
			case 2:
				return "Players";
			case 3:
				return "Map Name";
			case 4:
				return "Ip";
			case 5:
				return "Join";
			default:
				return null;
			}
		}

		@Override
		public int getNumColumns() {
			return 6;
		}

	}

	private class EditFieldCellWidgetCreator implements CellWidgetCreator {

		private String data;

		public void applyTheme(ThemeInfo themeInfo) {
		}

		public String getTheme() {
			return "EditFieldCellRenderer";
		}

		public Widget updateWidget(Widget existingWidget) {
			EditField field = (EditField) existingWidget;
			if (field == null) {
				field = new EditField();
				field.setReadOnly(true);
				field.setText(data);
			}
			return field;
		}

		public void positionWidget(Widget widget, int x, int y, int w, int h) {
			// this method will size and position the ComboBox
			// If the widget should be centered (like a check box) then this
			// would be done here
			widget.setPosition(x, y);
			widget.setSize(w, h);
		}

		public void setCellData(int row, int column, Object data) {
			// we have to remember the cell data for the next call of
			// updateWidget
			this.data = (String) data;
		}

		public Widget getCellRenderWidget(int x, int y, int width, int height, boolean isSelected) {
			// this cell does not render anything itself
			return null;
		}

		public int getColumnSpan() {
			// no column spanning
			return 1;
		}

		public int getPreferredHeight() {
			// we have to inform the table about the required cell height
			// before
			// we can create the widget - so we need to get the required
			// height
			// from the theme - see applyTheme/getTheme
			return 20;
		}

	}

	private class ButtonCellWidgetCreator implements CellWidgetCreator {

		private ServerInfo info;

		public void applyTheme(ThemeInfo themeInfo) {
		}

		public String getTheme() {
			return "button";
		}

		public Widget updateWidget(Widget existingWidget) {
			MyButton button = (MyButton) existingWidget;
			if (info.canJoin()) {
				if (button == null) {
					button = new MyButton("Join");
					button.setIp(info.ip);
					button.setSize(50, 20);
				}
			}
			return button;
		}

		public void positionWidget(Widget widget, int x, int y, int w, int h) {
			// this method will size and position the ComboBox
			// If the widget should be centered (like a check box) then this
			// would be done here
			widget.setPosition(x, y);
			widget.setSize(w, h);
		}

		public void setCellData(int row, int column, Object data) {
			// we have to remember the cell data for the next call of
			// updateWidget
			this.info = (ServerInfo) data;
		}

		public Widget getCellRenderWidget(int x, int y, int width, int height, boolean isSelected) {
			// this cell does not render anything itself
			return null;
		}

		public int getColumnSpan() {
			// no column spanning
			return 1;
		}

		public int getPreferredHeight() {
			// we have to inform the table about the required cell height
			// before
			// we can create the widget - so we need to get the required
			// height
			// from the theme - see applyTheme/getTheme
			return 20;
		}
	}

	private class MyButton extends Button {

		private String ip;

		public MyButton(String title) {
			super(title);
			setTheme("button");
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
			this.addCallback(new Runnable() {
				@Override
				public void run() {
					join(MyButton.this.ip);
				}
			});
		}

	}

	private class ServerInfo {
		private String state;
		private String gameType;
		private String nbPlayer;
		private String maxPlayer;
		private String mapName;
		private String ip;

		public Object getInfo(int column) {
			switch (column) {
			case 0:
				switch (Integer.parseInt(state)) {
				case ServerState.FULL:
					return "Full";
				case ServerState.IN_GAME:
					return "In Game";
				case ServerState.OK:
					return "OK";
				default:
					break;
				}
			case 1:
				return GameGoal.getType(gameType);
			case 2:
				return nbPlayer + "/" + maxPlayer;
			case 3:
				return mapName;
			case 4:
				return ip;
			default:
				return null;
			}
		}

		public boolean canJoin() {
			return Integer.parseInt(state) == ServerState.OK;
		}
	}

}
