package rts.core.network.menu_tcp_containers;

public class MessageState {

	public String name;
	public String message;
	public String color;

	public String getCmpMessage() {
		return name + ":" + message;
	}
}
