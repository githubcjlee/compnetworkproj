package com.chat.server;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;


public class ServerUDP extends Thread {
    private final int serverPort;
    private final int tcpport;
    private ArrayList<ServerUDPWorker> workerList = new ArrayList<>();

    public ServerUDP(int serverPort , int _tcpport) {
        this.serverPort = serverPort;
        this.tcpport = _tcpport;
    }

  //  public List<ServerWorker> getWorkerList() {
  //      return workerList;
  //  }

    
    @Override
    public void run() {
    	try {
    		DatagramSocket serverUDPSocket = new DatagramSocket(serverPort);
    		byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];
    		
    		while(true) {
    			System.out.println("[]Server's UDP is listening for new client");
    			
    			// Waiting for incoming clients
    			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverUDPSocket.receive(receivePacket);
         
                String sentence = new String( receivePacket.getData());
                
                // Capture Address and Port of client
                InetAddress ClientIPAddress = receivePacket.getAddress();
                int ClientPort = receivePacket.getPort();
                
                // Start a ServerUDPWorker class
                System.out.println("[]Server's UDP starting udpWorker");
                ServerUDPWorker worker = new ServerUDPWorker ( serverUDPSocket, ClientIPAddress, ClientPort, this.tcpport ,sentence  );
                worker.start();
                worker.join();
              
    		}
    	}catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    
   
    
//    public void removeWorker(ServerWorker serverWorker) {
//        workerList.remove(serverWorker);
//    }
    
}

