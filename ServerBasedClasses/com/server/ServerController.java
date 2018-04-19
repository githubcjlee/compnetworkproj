package com.server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.server.model.ChatList;
import com.server.model.ChatLog;
import com.server.model.OnlineUser;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;


public class ServerController {
	private String USERS_LIST_PATH = "resources/users.csv";
	private String CHAT_LIST_PATH = "resources/chatList.csv";
	private String CHAT_HISTORY_PATH = "resources/chatHistory.csv";
	private List<OnlineUser> usersOnline;
	
	public ServerController() {
		usersOnline = new ArrayList<OnlineUser>();
		File chatListFile = new File(CHAT_LIST_PATH);
		
	}
	
	//USERS
	private boolean checkUserStatus(int clientId) {
		boolean status = true;
		//Check if online;
		//Check if available;
		return status;
	}
	
	//USERS ONLINE
	//adds a newly logged in user to array
	private void addOnlineUser(int clientId, String userId, String ip, int port){
		OnlineUser newUser = new OnlineUser();
		newUser.setUserId(userId);
		newUser.setClientId(clientId);
		newUser.setAvailable(true);
		newUser.setIp(ip);
		newUser.setPort(port);
		
		usersOnline.add(newUser);
	}

	//removes user
	private void removeOnlineUser(int clientId){
		for (int i = 0; i < usersOnline.size(); i++) {
			if (clientId == usersOnline.get(i).getClientId()) {
				usersOnline.remove(i);
			}
		}
	}

	private void showOnlineUsers() {
		
	}
	
	//CHAT LIST, LIST OF CHATS WITH LIVE/ONGOING SESSION
	private boolean requestChatSession(int clientA, int clientB){
		if(checkUserStatus(clientA) && checkUserStatus(clientB)){
			//create chat
			return true;
		} else {
			//dont create chat
			return false;
		}
		
	}

	private void createChatSession(int clientA, int clientB){

	}
	
	private void endSession(int clientId){
		//search array chatList
		//look for ClinetId and if match
		//end that session with sessionId
		
		
	}

	
	//CHAT HISTORY
	private List<ChatLog> requestHistory(int clientA, int clientB){
		
		List<ChatLog> chatLogList = new ArrayList<ChatLog>();
		
		ChatLog log = new ChatLog();
		log.setChatId(1);
		log.setSessionId(1);
		log.setChatText("Hello");
		
		chatLogList.add(log);
		
		
		return chatLogList;
	}
	

	private void logChat(int sessionId, String chatText){
		
	}

	
	public static void main(String args[]) {
		ServerController myFunction = new ServerController();
	}
	
}
