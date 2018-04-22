package com.chat.server;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;


public class ServerTCPWorker extends Thread {

	private final Socket clientSocket;
	private final ServerTCP server;
	private String login = null;
	private OutputStream outputStream;
	private HashSet<String> topicSet = new HashSet<>();

	public ServerTCPWorker(ServerTCP server, Socket clientSocket) {
		this.server = server;
		this.clientSocket = clientSocket;
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
		InputStream inputStream = clientSocket.getInputStream();
		this.outputStream = clientSocket.getOutputStream();

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line;

		while ((line = reader.readLine()) != null) {
			System.out.println("[ServerTCPWorker] incoming message =\"" + line + "\"");
			
			String[] tokens = line.split("\\(");
			
			if (tokens != null && tokens.length > 0) {
				String msg = "";
				String cmd = tokens[0];
				
				// take off last parentheses
				tokens[1] = tokens[1].substring(0, tokens[1].indexOf(')'));

				if ("CONNECT".equalsIgnoreCase(cmd)) {
					handleLogin(outputStream, tokens);
					msg = "CONNECTED()\n";
					outputStream.write(msg.getBytes());
				} else if ("MSG".equalsIgnoreCase(cmd)) {
					String[] tokensMsg = StringUtils.split(line, null, 3);
					handleMessage(tokensMsg);
				} else if ("CHAT".equalsIgnoreCase(cmd)) {
					handleJoin(tokens);
				} else if ("leave".equalsIgnoreCase(cmd)) {
					handleLeave(tokens);
				} else if ("logoff".equals(cmd) || "quit".equalsIgnoreCase(cmd)) {
					handleLogoff();
					break;
				} else {
					msg = "unknown " + cmd + "\n";
					outputStream.write(msg.getBytes());
				}
			}
		}

		clientSocket.close();
	}

	private void handleLeave(String[] tokens) {
		if (tokens.length > 1) {
			String topic = tokens[1];
			topicSet.remove(topic);
		}
	}

	
	private void handleJoin(String[] tokens) {
		// Token[1] should = ID
		// Connect this.ID to Token[1]
	}

	private void handleMessage(String[] tokens) throws IOException {
		String sendTo = tokens[1];
		String body = tokens[2];


		// *Forward message to appropriate client*
	}

	private void handleLogoff() throws IOException {
		
		// *Update list of active users*
		
		clientSocket.close();
	}

	
	private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
		this.login = tokens[1];
		
		// *Add this login to Active users list*
	}

}
