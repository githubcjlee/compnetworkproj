package com.chat.server;

import java.io.IOException;
import java.net.*;
import java.util.Random;

public class ServerUDPWorker extends Thread {

	private final DatagramSocket server;
	private final InetAddress clientIP;
	private final int clientPort;
	private final int ServerTCPPort;
	private String sentence = "";
	private int ClientID;

	public ServerUDPWorker(DatagramSocket server, InetAddress IP, int port, int tcpport,String firstSentence) {
		this.server = server;
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
		
		int rand =0;
		int xres=0;
		System.out.println("entered handle client socket");
		while ( route > 0  ) {
			
			if ( route > 1) {
				// Wait for incoming message after initial message
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				this.server.receive(receivePacket);
				sentence = new String(receivePacket.getData());
			}
			
			// Split message to read protocol 
			String array[] = sentence.split("\\(");
							
			// take off last parentheses
			array[1] = array[1].substring(0 , array[1].indexOf(')'));
			
			
			if ( "LOGIN".equals(array[0])) {
				System.out.println("Server received LOGIN");
				array[2] = array[2].substring(0 , array[2].indexOf(')'));
				System.out.println("login: " + array[1] + "   pass: " + array[2]);
				Thread.sleep(2000);
				// If ID and Password are valid
				reply = "OK";
				route++;
			}
			
			
			else if ( "HELLO".equals(array[0])) {
				// set client ID
				// use client ID to look up client's secret key
				this.ClientID = Integer.parseInt(array[1]);
				
				System.out.println("HELLO received");
				Thread.sleep(2000);
				// If Client ID is NOT in list of subscribers, send AUTH_FAIL *** need to add in later
				
				// Else:
				// Create random number
				rand = (int)(new Random().nextInt());
				
				// Use Client's secret key and rand to create xres (currently arbitrarily set to ID)
				xres = this.ClientID;
				
				// set message to CHALLENGE
				reply = "CHALLENGE(" + rand + ")";
				route ++;
			}
			
			else if ( "RESPONSE".equals(array[0])) {
				System.out.println("RESPONSE received");
				Thread.sleep(2000);
				
				// split contents into ID and res
				String array2[] = array[1].split(",");
				
				// For now testing xres against ID but should be array2[1]
				if ( this.ClientID == Integer.parseInt(array2[0]) && xres==Integer.parseInt(array2[1])) {
					// set message to Auth_succ and send with rand_cookie, using rand from challenge for now
					reply = "AUTH_SUCC(" + rand + "," + this.ServerTCPPort +")";
					
					System.out.println("Client " + this.ClientID + " is authenticated!");
					Thread.sleep(2000);
					route = -1;
				}
				else {
					reply = "AUTH_FAIL";
					route = -1;
				}
				
			} 
			/*else if ( "CONNECT".equals(array[0])) {
				System.out.println("CONNECT received");
				Thread.sleep(2000);
				
				if ( Integer.parseInt(array[1]) == rand) {
					System.out.println("Client " + this.ClientID + " is authenticated!");
					Thread.sleep(2000);
					reply = "CONNECTED";
					route = -1;
				}
			}*/
			
			// Send out reply
			sendData = reply.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, this.clientIP, this.clientPort);
			this.server.send(sendPacket);
		}

		// ****************** ****************** ****************** ******************
		// Once authentication is complete, send client appropriate message
		// (On Client Side) client will then send TCP message through a TCP socket
		// ****************** ****************** ****************** ******************

	}

}
