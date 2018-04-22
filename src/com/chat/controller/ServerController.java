package com.chat.controller;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import com.chat.model.ChatLog;
import com.chat.model.ChatSession;
import com.chat.model.OnlineUser;
import com.chat.model.User;

/*
 * @author Charlie Nguyen, Ted Ahn
 * 
 */
public class ServerController {
	private String USERS_LIST_PATH;
	private String CHAT_LIST_PATH;
	private String CHAT_HISTORY_PATH;
	private Map<Integer, OnlineUser> usersOnline;
	private Map<Integer, ChatSession> liveChats;
	
	public ServerController(){
		usersOnline = new HashMap<Integer, OnlineUser>();
		liveChats = new HashMap<Integer, ChatSession>();

		USERS_LIST_PATH = this.getClass().getClassLoader().getResource("users.csv").getFile();
		CHAT_LIST_PATH = this.getClass().getClassLoader().getResource("chatList.csv").getFile();
		CHAT_HISTORY_PATH = this.getClass().getClassLoader().getResource("chatHistory.csv").getFile();
		
	}
	
	//USERS
	public boolean checkUserStatus(int clientId) {
		boolean status = false;

		for (int i = 0; i < usersOnline.size(); i++) {
			if (clientId == usersOnline.get(i).getClientId()) {
				status = usersOnline.get(i).isAvailable();
			}
		}
		
		return status;
	}
	
	public boolean isValidLogin(String id, String password) {
		boolean valid = false;
		
		FileReader fileReader = null;
		CSVParser csvFileParser = null;
		String[] chatHeader = ChatSession.getHeader();
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(chatHeader);
		
		try {

            //initialize FileReader object
            fileReader = new FileReader(USERS_LIST_PATH);
            
            //initialize CSVParser object
            csvFileParser = new CSVParser(fileReader, csvFileFormat);
            
            //Get a list of CSV file records
            List<CSVRecord> csvRecords = csvFileParser.getRecords(); 
            
            //Read the CSV file records starting from the second record to skip the header
            for (int i = 1; i < csvRecords.size(); i++) {
	            	CSVRecord record = csvRecords.get(i);
	            	//Create a new student object and fill his data
	            	User user = new User(
	            		record.get(chatHeader[0]), 
	            		record.get(chatHeader[1])
	            	);
	            	
	            	
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
		
		return valid;
	}
	
	//USERS ONLINE
	//adds a newly logged in user to array
	public boolean addOnlineUser(int clientId, String userId, String ip, int port){
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
	public void removeOnlineUser(int clientId){
		usersOnline.remove(clientId);
	}

	//Lists all online users on server to server console
	public void showOnlineUsers() {
		for (int i = 0; i < usersOnline.size(); i++) {
			System.out.println(usersOnline.get(i));
		}
	}
	
	//CHAT LIST, LIST OF CHATS WITH LIVE/ONGOING SESSION
	public boolean requestChatSession(int clientA, int clientB){
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
		//Save to ChatSession
		int sessionId = Collections.max(this.getChatList().keySet()) + 1;
		
		//add to liveChatSession
		ChatSession chat = new ChatSession(sessionId, clientA, clientB, CKA);
		liveChats.put(chat.getSessionId(), chat);
		
		
		//Make both clients unavailable
		usersOnline.get(clientA).setAvailable(false);
		usersOnline.get(clientB).setAvailable(false);
		
	}
	
	public void endSession(int clientId){
		//search array chatList
		for (ChatSession chat : liveChats.values()) {
			//look for ClinetId and if match
			//end that session with sessionId
			if (chat.getClientA() == clientId || chat.getClientB() == clientId) {
				//close chat
				liveChats.remove(chat.getSessionId());
				
				//mark both clients available again
				usersOnline.get(chat.getClientA()).setAvailable(true);
				usersOnline.get(chat.getClientB()).setAvailable(true);
			}
		} 
		
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
		
		//Map of ChatSession with sessionId as Key
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
	private String[] requestHistory(int clientA, int clientB){
		
		List<ChatSession> chatLogList = new ArrayList<ChatSession>(getChatList().values());
		//first loop
		//Loops ChatSession looking for last session between clientA and clientB
		//get the session ID		
		int sessionId = -1;
		
		for(int i = chatLogList.size()- 1; i >= 0; i--) {
			
			int client1 = chatLogList.get(i).getClientA();
			int client2 = chatLogList.get(i).getClientB();
			
			if(clientA == client1 && clientB == client2) {
				sessionId = chatLogList.get(i).getSessionId();
			}
		}
		
		if (sessionId == -1) {
			return null;
		}
		
		//second loop, looping ChatLog
		//get everything under that sessionID
		
		FileReader fileReader = null;
		CSVParser csvFileParser = null;
		String[] logHead = ChatLog.getHeader();
		//Create the CSVFormat object with the header mapping
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(logHead);
     
        //Create a new list of student to be filled by CSV file data 
       // List<ChatLog> chatLog = new ArrayList<ChatLog>();
        String[] chatLog = null;
        
        try {
        	
            
            //initialize FileReader object
            fileReader = new FileReader(CHAT_HISTORY_PATH);
            
            //initialize CSVParser object
            csvFileParser = new CSVParser(fileReader, csvFileFormat);
            
            //Get a list of CSV file records
            List<CSVRecord> csvRecords = csvFileParser.getRecords(); 
            chatLog = new String[csvRecords.size()];
            		
            //Read the CSV file records starting from the second record to skip the header
            for (int i = 1; i < csvRecords.size(); i++) {
            	CSVRecord record = csvRecords.get(i);

            	chatLog[i] = record.get(logHead[1]);	
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
	
	//Delimiter used in CSV file
	private static final String NEW_LINE_SEPARATOR = "\n";

	private void logChat(int sessionId, String chatText){
		
	
		FileWriter fileWriter = null;
		CSVPrinter csvFilePrinter = null;
		
		//Create the CSVFormat object with "\n" as a record delimiter
		
				
		try {
			
			//initialize FileWriter object
			fileWriter = new FileWriter(CHAT_HISTORY_PATH, true);
			//initialize CSVPrinter object 
	        csvFilePrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);
	        //Create CSV file header
	        
			//Write a new student object list to the CSV file
	        List studentDataRecord = new ArrayList();
            studentDataRecord.add(String.valueOf(sessionId));
            studentDataRecord.add(chatText);
            
	        csvFilePrinter.printRecord(studentDataRecord);

			//System.out.println("CSV file was created successfully !!!");
			
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
				csvFilePrinter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter/csvPrinter !!!");
                e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String args[]) {
		ServerController function = new ServerController();
		List<ChatSession> list = new ArrayList<ChatSession>(function.getChatList().values());
	}

	
}
