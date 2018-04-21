package com.chat.server;

import java.io.IOException;

import java.net.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;


public class ServerTCP extends Thread {
    
	private final int serverPort;
    private ArrayList<ServerTCPWorker> workerList = new ArrayList<>();

    public ServerTCP(int serverPort) {
        this.serverPort = serverPort;
    }

    public List<ServerTCPWorker> getWorkerList() {
        return workerList;
    }

   
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            while(true) {
                System.out.println("[]Server's TCP is listening for new client..");
                Socket clientSocket = serverSocket.accept();
                System.out.println("[]TCP connection made " + clientSocket);
                ServerTCPWorker worker = new ServerTCPWorker(this, clientSocket);
                workerList.add(worker);
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeWorker(ServerTCPWorker serverWorker) {
        workerList.remove(serverWorker);
    }
}

