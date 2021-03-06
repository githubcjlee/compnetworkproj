package com.chat.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;
import java.util.Random;

import com.chat.plugin.AESKeyGeneration;

/*
 * @author Aaron Im
 * 
 */
public class ServerUDPWorker extends Thread {

	private final DatagramSocket socket;
	private final ServerUDP server;
	private final InetAddress clientIP;
	private final int clientPort;
	private final int ServerTCPPort;
	private String sentence = "";
	private int clientID;

	public ServerUDPWorker(ServerUDP server, DatagramSocket socket, InetAddress IP, int port, int tcpport, String firstSentence) {
		this.server = server;
		this.socket = socket;
		this.clientIP = IP;
		this.clientPort = port;
		this.sentence = firstSentence;
		this.ServerTCPPort = tcpport;
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

		String sentence = this.sentence;
		String reply = "";
		int route = 1;

		int rand = 0;
		String xres = "";

		while (route > 0) {

			if (route > 1) {
				// Wait for incoming message after initial message
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				this.socket.receive(receivePacket);
				//sentence = new String(receivePacket.getData());
				sentence = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength(), "UTF-8");

			}
			// Split message to read protocol
			String array[] = sentence.split(" ");

			if ("HELLO".equals(array[0])) {
				array[1] = array[1].trim();
				this.clientID = Integer.parseInt(array[1]);
				// set client ID
				// use client ID to look up client's secret key
				
				String password = "" + this.clientID;
				// If Client ID is NOT in list of subscribers, send AUTH_FAIL *** need to add in later
				rand = (int) (new Random().nextInt());
				// Use Client's secret key and rand to create xres
				AESKeyGeneration crypto = new AESKeyGeneration();
				xres = crypto.generateSymmetricKey( rand + password );
				
				// set message to CHALLENGE
				reply = "CHALLENGE " + rand;
				route++;
			}

			else if ("RESPONSE".equals(array[0])) {	
				// index 1 = id and index2 = res
				
				// Test xres against res(from client)
				if ( xres.equals(array[2]) ) {
					rand = new Random().nextInt();
					server.usersKey.put(clientID, rand);
					
					reply = "AUTH_SUCC " + rand + " " + this.ServerTCPPort;
					route = -1;
				} else {
					reply = "AUTH_FAIL";
					route = -1;
				}
			}
			
			// Send out reply
			sendData = reply.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, this.clientIP, this.clientPort);
			this.socket.send(sendPacket);
		}

	}

}
