package rts.core.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import rts.core.network.menu_tcp_containers.ServerState;
import rts.utils.Configuration;

public class NetworkServerDiscover {

	private final static int BUFFER_SIZE = 300;

	private NetworkManager manager;

	// Listenings
	private INetworkDiscoverListener listener;
	private DatagramSocket clientSocket;
	private DatagramSocket serverSocket;

	public NetworkServerDiscover(NetworkManager manager) {
		this.manager = manager;
	}

	// Client

	public void discover() throws IOException {
		byte leMessage[] = ("Client:infos connection").getBytes();
		DatagramPacket packet = new DatagramPacket(leMessage, leMessage.length);
		packet.setAddress(InetAddress.getByName("255.255.255.255"));
		packet.setPort(Configuration.getUdpListeningServerPort());
		clientSocket.send(packet);
	}

	public void launchClientListening(INetworkDiscoverListener l) {
		this.listener = l;
		Thread listeningThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					clientSocket = new DatagramSocket(Configuration.getUdpListeningClientPort());
					boolean listen = true;
					byte buffer[] = new byte[BUFFER_SIZE];
					DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE);
					while (listen) {
						try {
							clientSocket.receive(packet);
							if (packet != null) {
								String[] datas = new String(packet.getData(), packet.getOffset(), packet.getLength()).split(":");
								if (listener != null && datas.length == 5) {
									listener.receiveServerInfos(datas[0], datas[1], datas[2], datas[3], datas[4], packet.getAddress().getHostAddress());
								}
							}
						} catch (IOException e) {
							// e.printStackTrace();
							break;
						}

					}
					clientSocket.close();
				} catch (SocketException e) {
					e.printStackTrace();
				} finally {
					if (clientSocket != null && !clientSocket.isClosed())
						clientSocket.close();
				}
			}
		});
		listeningThread.start();
	}

	public void stopClientListening() {
		clientSocket.close();
	}

	// Server

	public void launchServerListening() {
		Thread listeningThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					serverSocket = new DatagramSocket(Configuration.getUdpListeningServerPort());
					boolean listen = true;
					byte buffer[] = new byte[BUFFER_SIZE];
					DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE);
					while (listen) {
						try {
							serverSocket.receive(packet);
							if (packet != null) {
								ServerState state = manager.getServerState();
								state.update();
								byte leMessage[] = (state.state + ":" + state.gameType + ":" + state.nbPlayer + ":" + state.nbMaxPlayer + ":" + state.mapName)
										.getBytes();
								serverSocket.send(new DatagramPacket(leMessage, leMessage.length, packet.getAddress(), Configuration
										.getUdpListeningClientPort()));
							}
						} catch (IOException e) {
							// e.printStackTrace();
							break;
						}

					}
					serverSocket.close();
				} catch (SocketException e) {
					e.printStackTrace();
				} finally {
					if (serverSocket != null && !serverSocket.isClosed())
						serverSocket.close();
				}
			}
		});
		listeningThread.start();
	}

	public void stopServerListening() {
		serverSocket.close();
	}

}
