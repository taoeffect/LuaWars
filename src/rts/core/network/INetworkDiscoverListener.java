package rts.core.network;

public interface INetworkDiscoverListener {

	public void receiveServerInfos(String state, String gameType, String nbPlayer, String maxPlayer, String mapName, String ip);
}
