package com.server;

import java.util.ArrayList;
import java.util.List;

import com.server.model.*;

public class ServerFunctions {

	public ServerFunctions() {}
	
	private boolean checkUserStatus(int clientId) {
		boolean status = true;
		//Check if online;
		//Check if available
		return status;
	}
	
	private void addOnlineUser(int clientId){
	}

	private void removeOnlineUser(int clientId){
	}

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
		ServerFunctions myFunction = new ServerFunctions();
		
		List<ChatLog> myChats = myFunction.requestHistory(1,2);
		
		for(int i = 0; i < myChats.size(); i++) {
			System.out.println(myChats.get(i).getChatText());
		}
		
	}
	
}
