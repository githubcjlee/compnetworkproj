package com.chat.server;

import java.io.*;
import java.net.Socket;
import java.util.List;


public class ServerTCPWorker extends Thread {

	private final Socket clientSocket;
	private final ServerTCP server;
	private OutputStream outputStream;
	private String login = null;
	private boolean available = true;
	private ServerTCPWorker friendWorker = null;

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
			System.out.println("[TCP-W] incoming message from " + login + " =\"" + line + "\"");
			
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
				} 
				else if ("MSG".equalsIgnoreCase(cmd)) {
					if (friendWorker == null) {
						msg = "ERROR(You are currently not chatting with anyone.)\n";
						outputStream.write(msg.getBytes());
					} else {
						sendMessage(tokens[1]);
					}
				} 
				else if ("CHAT".equalsIgnoreCase(cmd)) {
					if (handleJoin(tokens[1]) && friendWorker.handleJoin(login)) {
						msg = "ALERT(Chat opened with " + friendWorker.getLogin() + ")\n";
						outputStream.write(msg.getBytes());
						msg = "ALERT(Chat opened with " + login + ")\n";
						friendWorker.outputStream.write(msg.getBytes());
					} else {
						msg = "ERROR(Could not connect to user " + tokens[1] + ")\n";
						outputStream.write(msg.getBytes());
					}
				} 
				else if ("leave".equalsIgnoreCase(cmd)) {
					handleLeave();
					friendWorker.handleLeave();
				} 
				else if ("logoff".equals(cmd) || "quit".equalsIgnoreCase(cmd)) {
					handleLogoff();
					break;
				} 
				else {
					msg = "UNKNOWN(" + cmd + ")\n";
					outputStream.write(msg.getBytes());
				}
			}
		}

		clientSocket.close();
	}

	private void handleLeave() throws IOException {
		String msg = "LEAVE(" + friendWorker.getLogin() + " has left the room)\n";
		this.friendWorker = null;
		this.available = true;
		outputStream.write(msg.getBytes());
	}

	
	private boolean handleJoin(String loginId) {
		// Token[1] should = ID
		// Connect this.ID to Token[1]

		List<ServerTCPWorker> onlineUsers = server.getWorkerList();
		for (ServerTCPWorker user : onlineUsers) {
			if (user.getLogin().equals(loginId)) {
				if (!user.available) {
					return false;
				}
				this.friendWorker = user;
				user.available = false;
				
				return true;
			}
		}
		
		return false;
		
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	//Sends Message to the currently open chat
	private void sendMessage(String message) throws IOException {
		String msg = "MSG(" + login + ":" + message + ")\n"; 
		friendWorker.outputStream.write(msg.getBytes());
		// *Forward message to appropriate client*
	}

	
	private void handleLogoff() throws IOException {
		
		// *Update list of active users*
		if (friendWorker != null) {
			friendWorker.handleLeave();
		}
		clientSocket.close();
	}

	private String getLogin() {
		return login;
	}
	
	private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
		this.login = tokens[1];
		
		// *Add this login to Active users list*
	}

}
