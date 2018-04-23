package com.chat.server;

import java.util.HashMap;
import java.util.Map;

/*
 * @author Aarom Im, Chris Lee, Ted Ahn, Charlie Nguyen
 * 
 */
public class ServerMain {
	private static Map<Integer,Integer> usersKey = new HashMap<Integer,Integer>();
	
    public static void main(String[] args) {
        int TCPport = 8818;
        int UDPport = 9918;
        
        ServerTCP serverTCP = new ServerTCP(usersKey, TCPport);
        ServerUDP serverUDP = new ServerUDP(usersKey, UDPport , TCPport);
        
        serverTCP.start();
        serverUDP.start();
    }
}
