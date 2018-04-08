package com.muc;



/**
 * Created by jim on 4/18/17.
 */
public class ServerMain {
    public static void main(String[] args) {
        int port = 8818;
        int port2 = 9918;
        ServerTCP server = new ServerTCP(port);
        ServerUDP serverUDP = new ServerUDP(port2);
        
        server.start();
        serverUDP.start();
    }
}
