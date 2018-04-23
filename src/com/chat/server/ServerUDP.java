package com.chat.server;

import java.io.IOException;
import java.net.*;
import java.util.Map;

/*
 * @author Aaron Im
 * 
 */
public class ServerUDP extends Thread {
	private final int serverPort;
	private final int tcpport;
    public Map<Integer,Integer> usersKey;

	public ServerUDP(Map<Integer,Integer> usersKey, int serverPort, int _tcpport) {
		this.setUsersKey(usersKey);
		this.serverPort = serverPort;
		this.tcpport = _tcpport;
	}

	@Override
	public void run() {
		try {
			DatagramSocket serverUDPSocket = new DatagramSocket(serverPort);
			byte[] receiveData = new byte[1024];
			
			while (true) {

				// Waiting for incoming clients
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverUDPSocket.receive(receivePacket);

				String sentence = new String(receivePacket.getData());

				// Capture Address and Port of client
				InetAddress ClientIPAddress = receivePacket.getAddress();
				int ClientPort = receivePacket.getPort();

				// Start a ServerUDPWorker class
				ServerUDPWorker worker = new ServerUDPWorker(this, serverUDPSocket, ClientIPAddress, ClientPort, this.tcpport,
						sentence);
				worker.start();
				worker.join();

			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Map<Integer,Integer> getUsersKey() {
		return usersKey;
	}

	public void setUsersKey(Map<Integer,Integer> usersKey) {
		this.usersKey = usersKey;
	}
}
