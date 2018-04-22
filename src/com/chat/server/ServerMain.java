package com.chat.server;

public class ServerMain {
    public static void main(String[] args) {
        int TCPport = 8818;
        int UDPport = 9918;
        ServerTCP server = new ServerTCP(TCPport);
        ServerUDP serverUDP = new ServerUDP(UDPport , TCPport);
        
        server.start();
        serverUDP.start();
    }
}
