package com.chat.server;

import java.io.IOException;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ServerTCP extends Thread {

	private final int serverPort;
    private ArrayList<ServerTCPWorker> workerList = new ArrayList<ServerTCPWorker>();

	public ServerTCP(int serverPort) {
		this.serverPort = serverPort;
	}
	
    public List<ServerTCPWorker> getWorkerList() {
        return workerList;
    }


	@SuppressWarnings("resource")
	@Override
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(serverPort);
			while (true) {
				Socket clientSocket = serverSocket.accept();
				ServerTCPWorker worker = new ServerTCPWorker(this, clientSocket);
				workerList.add(worker);
				worker.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
