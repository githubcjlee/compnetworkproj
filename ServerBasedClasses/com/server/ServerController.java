package com.server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.server.model.ChatList;
import com.server.model.ChatLog;
import com.server.model.OnlineUser;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;


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

	private Map<Integer, ChatList> getChatList() {
		return new HashMap<Integer, ChatList>();
	}
	
	//CHAT HISTORY
	private List<ChatLog> requestHistory(int clientA, int clientB){
		
		List<ChatList> chatLogList = new ArrayList<ChatList>(getChatList().values());
		
		//first loop
		//Loops ChatList looking for last session between clientA and clientB
		//get the session ID		
		int sessionId;
		
		for(int i = chatLogList.size()- 1; i >= 0; i--) {
			
			int client1 = chatLogList.get(i).getClientA();
			int client2 = chatLogList.get(i).getClientB();
			
			if(clientA == client2 && clientB == client2) {
				sessionId = chatLogList.get(i).getSessionId();
			}
		}
		
		
		//second loop, looping ChatLog
		//get everything under that sessionID
		
		FileReader fileReader = null;
		CSVParser csvFileParser = null;
		String[] logHead = ChatLog.getHeader();
		//Create the CSVFormat object with the header mapping
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(logHead);
     
        //Create a new list of student to be filled by CSV file data 
        List<ChatLog> chatLog = new ArrayList<ChatLog>();
        
        try {
        	
            
            //initialize FileReader object
            fileReader = new FileReader(CHAT_HISTORY_PATH);
            
            //initialize CSVParser object
            csvFileParser = new CSVParser(fileReader, csvFileFormat);
            
            //Get a list of CSV file records
            List<CSVRecord> csvRecords = csvFileParser.getRecords(); 
            
            //Read the CSV file records starting from the second record to skip the header
            for (int i = 1; i < csvRecords.size(); i++) {
            	CSVRecord record = csvRecords.get(i);
            	//Create a new student object and fill his data
            	ChatLog chat = new ChatLog(
	            			Integer.parseInt(record.get(logHead[0])), 
	            			Integer.parseInt(record.get(logHead[1])), 
	            			record.get(logHead[2])
            			);
            	chatLog.add(chat);	
			}
            
        } 
        catch (Exception e) {
        	System.out.println("Error in CsvFileReader !!!");
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
                csvFileParser.close();
            } catch (IOException e) {
            	System.out.println("Error while closing fileReader/csvFileParser !!!");
                e.printStackTrace();
            }
        }
		
		
		return chatLog;
	}
	

	private void logChat(int sessionId, String chatText){
		
	}

	
	public static void main(String args[]) {
		ServerController myFunction = new ServerController();
	}
	
}
