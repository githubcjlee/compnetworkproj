package com.muc;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;


public class ServerUDP extends Thread {
    private final int serverPort;
    private ArrayList<ServerUDPWorker> workerList = new ArrayList<>();

    public ServerUDP(int serverPort) {
        this.serverPort = serverPort;
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
    			System.out.println("Waiting for client UDP connection...");
    			
    			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverUDPSocket.receive(receivePacket);
         
                String sentence = new String( receivePacket.getData());
                System.out.println("[UDP] RECEIVED: " + sentence);
                
                
                
                
                // Capture Address and Port of client
                InetAddress ClientIPAddress = receivePacket.getAddress();
                int ClientPort = receivePacket.getPort();
                ServerUDPWorker worker = new ServerUDPWorker ( serverUDPSocket, ClientIPAddress, ClientPort  );
                worker.start();
               
              
    		}
    	}catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
   
    
//    public void removeWorker(ServerWorker serverWorker) {
//        workerList.remove(serverWorker);
//    }
    
}

