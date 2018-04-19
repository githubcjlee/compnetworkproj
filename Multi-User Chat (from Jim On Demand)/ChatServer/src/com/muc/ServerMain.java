package com.muc;

import java.util.ArrayList;
import java.util.List;

import com.server.ServerController;
import com.server.model.ChatSession;

/**
 * Created by jim on 4/18/17.
 */
public class ServerMain {
    public static void main(String[] args) {
        //int port = 8818;
        int port2 = 9918;
        //ServerTCP server = new ServerTCP(port);
        ServerUDP serverUDP = new ServerUDP(port2);
        
        //server.start();
        serverUDP.start();

		ServerController myFunction = new ServerController();
		List<ChatSession> myChats = new ArrayList<ChatSession>(myFunction.getChatList().values());
		for (int i = 0; i < myChats.size(); i++) {
			System.out.println(myChats.get(0).toString());
		}
    }
}
