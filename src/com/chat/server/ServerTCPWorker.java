package com.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.chat.controller.ServerController;
import com.chat.model.ChatSession;
import com.chat.plugin.AESKeyGeneration;
import com.chat.plugin.StrongAES;

/*
 * @author Aaron Im
 * @contributor Ted Ahn
 */
public class ServerTCPWorker extends Thread {

	private final Socket clientSocket;
	private final ServerTCP server;
	private OutputStream outputStream;
	private int login = -1;
	private ServerTCPWorker friendWorker = null;
	private ServerController controller = new ServerController();
	private ChatSession openSession = null;

	private String CKA = null;
	StrongAES workerCryptor = new StrongAES();
	
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
			
			//String[] tokens = line.split("\\(");
			String cmd = line.substring(0, line.indexOf('('));
			String param = line.substring(line.indexOf('(') + 1, line.lastIndexOf(')'));
			if (cmd != null || param != null) {
				String msg = "";
				
				// take off last parentheses
				
				if ("CONNECT".equalsIgnoreCase(cmd)) {
					handleLogin(outputStream, param);
					msg = "CONNECTED()\n";
					outputStream.write(msg.getBytes());
					
					AESKeyGeneration keyGen = new AESKeyGeneration();
					CKA = keyGen.generateChatKey(""+server.usersKey.get(login));
				} 
				else if ("CHAT".equalsIgnoreCase(cmd)) {
					if (friendWorker == null || openSession == null) {
						msg = "ERROR(You are currently not chatting with anyone.)\n";
						outputStream.write(msg.getBytes());
					} else {
						sendMessage(param);
					}
				} 
				else if ("CHAT_REQUEST".equalsIgnoreCase(cmd)) {
					try {
						int clientIdB = Integer.parseInt(param);
						if (handleJoin(clientIdB) && friendWorker.handleJoin(login) && server.usersKey.containsKey(login)) {
							
							openSession = controller.storeChatSession(login, friendWorker.getLogin(), CKA);
							friendWorker.openSession = this.openSession;
							
							msg = "CHAT_STARTED("+openSession.getSessionId()+","+clientIdB+")\n";
							outputStream.write(msg.getBytes());
							msg = "CHAT_STARTED("+openSession.getSessionId()+","+login+")\n";
							friendWorker.outputStream.write(msg.getBytes());
						} else {
							msg = "UNREACHABLE("+clientIdB+")\n";
							outputStream.write(msg.getBytes());
						}
					} catch (NumberFormatException e) {
						msg = "ERROR(Requested Incorrect Client ID: " + param + ")\n";
						System.out.println("Error by " + login + " " + msg);
						outputStream.write(msg.getBytes());
					}
				} else if ("END_REQUEST".equalsIgnoreCase(cmd)) {
					handleLeave();
					friendWorker.handleLeave();
					friendWorker.friendWorker = null;
					this.friendWorker = null;
				} else if ("ONLINE_REQ".equalsIgnoreCase(cmd)) {
					List<Integer> usersOnline = new ArrayList<Integer>(server.usersKey.keySet());
					if (usersOnline.size() == 1 && usersOnline.get(0) == login) {
						msg = "ONLINE_RESP(No other users online)\n";
						outputStream.write(msg.getBytes());
					} else {
						for (Integer user : usersOnline) {
							if (user != login) {
								msg = "ONLINE_RESP(" + user + ")\n";
								outputStream.write(msg.getBytes());
							}
						}
					}
				} else if ("LOGOFF".equalsIgnoreCase(cmd)) {
					handleLogoff();
					break;
				} else if ("HISTORY_REQ".equalsIgnoreCase(cmd)) {
					try {
						int clientB = Integer.parseInt(param);
						if (openSession == null && friendWorker == null) {
							String[] chatLogs = controller.requestHistory(login, clientB);
							
							if (chatLogs == null) {
								msg = "HISTORY_RESP(No History)\n";
								outputStream.write(msg.getBytes());
							} else {
								for (int i = 0; i < chatLogs.length; i++) {
									msg = "HISTORY_RESP(" + chatLogs[i] + ")\n";
									outputStream.write(msg.getBytes());
								}
								
							}
						}
					} catch (NumberFormatException e) {
						msg = "ERROR(Requested Incorrect Client ID: " + param + ")\n";
						System.out.println("Error by " + login + " " + msg);
						outputStream.write(msg.getBytes());
					}
				} else {
					msg = "UNKNOWN(" + cmd + ")\n";
					outputStream.write(msg.getBytes());
				}
			}
		}

		clientSocket.close();
	}

	private void handleLeave() throws IOException {
		String msg = "END_NOTIF("+openSession.getSessionId()+")\n";
		this.openSession = null;
		outputStream.write(msg.getBytes());
	}

	
	private boolean handleJoin(int loginId) {
		// Token[1] should = ID
		// Connect this.ID to Token[1]

		List<ServerTCPWorker> onlineUsers = server.getWorkerList();
		for (ServerTCPWorker user : onlineUsers) {
			if (user.getLogin() == (loginId)) {
				if (user.openSession != null) {
					return false;
				}
				this.friendWorker = user;
				
				return true;
			}
		}
		
		return false;
		
	}

	//Sends Message to the currently open chat
	private void sendMessage(String encMessage) throws IOException {
		String decMessage = workerCryptor.decrypt(encMessage, CKA);
		controller.logChat(openSession.getSessionId(), login, decMessage);
		
		String fullText = login + ":" + decMessage;
		String newMessage = friendWorker.workerCryptor.encrypt(fullText, friendWorker.CKA);
		
		String msg = "CHAT(" + newMessage + ")\n"; 
		friendWorker.outputStream.write(msg.getBytes());
		// *Forward message to appropriate client*
	}

	
	private void handleLogoff() throws IOException {
		if (friendWorker != null) {
			friendWorker.handleLeave();
		}
		
		String msg = "ALERT(Logged off as " + login + ")\n";
		outputStream.write(msg.getBytes());
		
		clientSocket.close();
	}

	private int getLogin() {
		return login;
	}
	
	private void handleLogin(OutputStream outputStream, String param) throws IOException {
		this.login = Integer.parseInt(param);
		
		// *Add this login to Active users list*
	}

}
