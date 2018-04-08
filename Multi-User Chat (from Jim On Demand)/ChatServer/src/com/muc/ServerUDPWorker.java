package com.muc;

import java.io.IOException;
import java.net.*;

public class ServerUDPWorker extends Thread {

	private final DatagramSocket server;
	private final InetAddress clientIP;
	private final int clientPort;

	public ServerUDPWorker(DatagramSocket server, InetAddress IP, int port) {
		this.server = server;
		this.clientIP = IP;
		this.clientPort = port;
	}

	@Override
	public void run() {
		try {
			handleClientSocket();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void handleClientSocket() throws IOException, InterruptedException {

		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];

		// Send CHALLENGE message
		String sentence = "CHALLENGE";
		sendData = sentence.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, this.clientIP, this.clientPort);
		this.server.send(sendPacket);

		

		while (true) {

			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			this.server.receive(receivePacket);

			sentence = new String(receivePacket.getData());
			System.out.println("[UDP] RECEIVED: " + sentence);
			
			if ( "x".equals(sentence) ) {
				System.out.println("[UDP] received the RESPONSE");
				sendPacket = new DatagramPacket("AUTH_SUCCESS".getBytes(), "AUTH_SUCCESS".length(), this.clientIP, this.clientPort);
				this.server.send(sendPacket);
				
				break;
			} else if ( "CONNECT".equals(sentence)) {
				sendPacket = new DatagramPacket("CONNECTED".getBytes(), "CONNECTED".length(), this.clientIP, this.clientPort);
				this.server.send(sendPacket);
				// end thread 
				break;
			} 

		}
		
		// ****************** ****************** ****************** ******************
		// Once authentication is complete, send client appropriate message
		// (On Client Side) client will then send TCP message through a TCP socket
		// ****************** ****************** ****************** ******************

	}

}
