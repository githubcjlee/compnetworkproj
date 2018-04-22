package com.chat.server;

import java.io.IOException;

import java.net.*;

public class ServerTCP extends Thread {

	private final int serverPort;

	public ServerTCP(int serverPort) {
		this.serverPort = serverPort;
	}

	@SuppressWarnings("resource")
	@Override
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(serverPort);
			while (true) {
				Socket clientSocket = serverSocket.accept();
				ServerTCPWorker worker = new ServerTCPWorker(this, clientSocket);
				worker.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
