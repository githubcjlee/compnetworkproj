package com.server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.server.model.ChatSession;
import com.server.model.ChatLog;
import com.server.model.OnlineUser;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

/*
 * @author Charlie Nguyen, Ted Ahn
 * 
 */
public class ServerController {
	private String USERS_LIST_PATH;
	private static String CHAT_LIST_PATH;
	private String CHAT_HISTORY_PATH;
	private Map<Integer, OnlineUser> usersOnline;
	private Map<Integer, ChatSession> liveChats;
	
	public ServerController(){
		usersOnline = new HashMap<Integer, OnlineUser>();
		liveChats = new HashMap<Integer, ChatSession>();

		USERS_LIST_PATH = ServerController.class.getResource("/users.csv").getPath();
		CHAT_LIST_PATH = ServerController.class.getResource("/chatList.csv").getPath();
		CHAT_HISTORY_PATH = ServerController.class.getResource("/chatHistory.csv").getPath();
		
	}
	
	//USERS
	private boolean checkUserStatus(int clientId) {
		boolean status = false;

		for (int i = 0; i < usersOnline.size(); i++) {
			if (clientId == usersOnline.get(i).getClientId()) {
				status = usersOnline.get(i).isAvailable();
			}
		}
		
		return status;
	}
	
	//USERS ONLINE
	//adds a newly logged in user to array
	private boolean addOnlineUser(int clientId, String userId, String ip, int port){
		OnlineUser newUser = new OnlineUser(clientId,userId,ip,port);
		
		if (usersOnline.containsKey(clientId)) {
			if (usersOnline.get(clientId).getUserId().equals(userId)) {
				System.out.println("ERROR: User is already Online!");
			} else {
				System.out.println("ERROR: Client ID conflict!");
			}
			return false;
		} else {
			usersOnline.put(clientId, newUser);
			return true;
		}
	}

	//removes user by Client ID
	private void removeOnlineUser(int clientId){
		usersOnline.remove(clientId);
	}

	//Lists all online users on server to server console
	private void showOnlineUsers() {
		for (int i = 0; i < usersOnline.size(); i++) {
			System.out.println(usersOnline.get(i));
		}
	}
	
	//CHAT LIST, LIST OF CHATS WITH LIVE/ONGOING SESSION
	private boolean requestChatSession(int clientA, int clientB){
		//both users are online AND available 
		if(checkUserStatus(clientA) && checkUserStatus(clientB)){
			//create chat
			
			//Create encryption
			String CKA = "insert encryption key here";
			
			createChatSession(clientA, clientB,CKA);
			
			return true;
		} else {
			//dont create chat
			return false;
		}
		
	}

	private void createChatSession(int clientA, int clientB, String CKA){
		//Save to ChatList
		int sessionId = 0;
		
		//add to liveChatList
		ChatSession chat = new ChatSession(sessionId, clientA, clientB, CKA);
		liveChats.put(chat.getSessionId(), chat);
		
		
		//Make both clients unavailable
		
	}
	
	private void endSession(int clientId){
		//search array chatList
		//look for ClinetId and if match
		//end that session with sessionId
		
		
	}

	/*
	 * Map< Integer , ChatSession >
	 * Key = SessionId
	 * Value = ChatSesssion
	 * 
	 */
	private Map<Integer, ChatSession> getChatList(){
		FileReader fileReader = null;
		CSVParser csvFileParser = null;
		String[] chatHeader = ChatSession.getHeader();
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(chatHeader);
		
		//Map of ChatList with sessionId as Key
		Map<Integer, ChatSession> chatMap = new HashMap<Integer, ChatSession>();
		
		try {

            //initialize FileReader object
            fileReader = new FileReader(CHAT_LIST_PATH);
            
            //initialize CSVParser object
            csvFileParser = new CSVParser(fileReader, csvFileFormat);
            
            //Get a list of CSV file records
            List<CSVRecord> csvRecords = csvFileParser.getRecords(); 
            
            //Read the CSV file records starting from the second record to skip the header
            for (int i = 1; i < csvRecords.size(); i++) {
	            	CSVRecord record = csvRecords.get(i);
	            	//Create a new student object and fill his data
	            	ChatSession chat = new ChatSession(
	            				Integer.parseInt(record.get(chatHeader[0])), 
	            				Integer.parseInt(record.get(chatHeader[1])), 
	            				Integer.parseInt(record.get(chatHeader[2])), 
	            				record.get(chatHeader[3])
	            			);
	            	chatMap.put(chat.getSessionId(), chat);	
			}
    		
		} catch (Exception e){
			System.out.println("ERROR: Cannot read CSV File chatList.csv!");
			e.printStackTrace();
		} finally {
			try {
				fileReader.close();
				csvFileParser.close();
			} catch (IOException e){
				System.out.println("WARNING: Cannot close fileReader/csvFileParser!");
				e.printStackTrace();
			}
		}
		return chatMap;
		
	}
	
	
	//CHAT HISTORY
	private List<ChatLog> requestHistory(int clientA, int clientB){
		
		List<ChatLog> chatLogList = new ArrayList<ChatLog>();
		
		
		
		return chatLogList;
	}
	

	private void logChat(int sessionId, String chatText){
		
	}

	
	public static void main(String args[]) {
		ServerController myFunction = new ServerController();
		List<ChatSession> myChats = new ArrayList<ChatSession>(myFunction.getChatList().values());
		for (int i = 0; i < myChats.size(); i++) {
			System.out.println(myChats.get(i).toString());
		}
	}
	
}
