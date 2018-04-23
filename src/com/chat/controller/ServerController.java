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
import com.chat.model.UsersOnline;

/*
 * @author Charlie Nguyen, Ted Ahn
 * 
 */
public class ServerController {
	//*IMPORTANT* Enter local csv file for each
	private String USERS_LIST_PATH = "/Users/ted/Desktop/users.csv";
	private String CHAT_LIST_PATH = "/Users/ted/Desktop/chatList.csv";
	private String CHAT_HISTORY_PATH = "/Users/ted/Desktop/chatHistory.csv";
	
	public ServerController(){
		USERS_LIST_PATH = this.getClass().getClassLoader().getResource("users.csv").getFile();
		//CHAT_LIST_PATH = this.getClass().getClassLoader().getResource("chatList.csv").getFile();
		//CHAT_HISTORY_PATH = this.getClass().getClassLoader().getResource("chatHistory.csv").getFile();
	}
	
	public int getUserCookie(String id) {
		
		FileReader fileReader = null;
		CSVParser csvFileParser = null;
		String[] userHeader = UsersOnline.getHeader();
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(userHeader);
		
		try {

            //initialize FileReader object / CSVParser object
            fileReader = new FileReader(USERS_LIST_PATH);
            csvFileParser = new CSVParser(fileReader, csvFileFormat);
            
            //Get a list of CSV file records
            List<CSVRecord> csvRecords = csvFileParser.getRecords(); 
            
            //Read the CSV file records starting from the second record to skip the header
            for (int i = 1; i < csvRecords.size(); i++) {
	            	CSVRecord record = csvRecords.get(i);
	            	//Create a new student object and fill his data
	            	if ( record.get(userHeader[0]).equals(id) ) {
		            	return Integer.parseInt(record.get(userHeader[1]));
	            	}
	            	
	            	
			}
    		
		} catch (Exception e){
			System.out.println("ERROR: Cannot read CSV File user.csv!");
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
		
		return -1;
	}
	

	public int storeUserWithCookie(String id, int cookie) {
		FileReader fileReader = null;
		CSVParser csvFileParser = null;
		String[] userHeader = UsersOnline.getHeader();
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(userHeader);
		
		try {

            //initialize FileReader object / CSVParser object
            fileReader = new FileReader(USERS_LIST_PATH);
            csvFileParser = new CSVParser(fileReader, csvFileFormat);
            
            //Get a list of CSV file records
            List<CSVRecord> csvRecords = csvFileParser.getRecords(); 
            
            //Read the CSV file records starting from the second record to skip the header
            for (int i = 1; i < csvRecords.size(); i++) {
	            	CSVRecord record = csvRecords.get(i);
	            	//Create a new student object and fill his data
	            	if ( record.get(userHeader[0]).equals(id) ) {
		            	return Integer.parseInt(record.get(userHeader[1]));
	            	}
	            	
	            	
			}
    		
		} catch (Exception e){
			System.out.println("ERROR: Cannot read CSV File user.csv!");
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
		
		return -1;
	}

	public ChatSession storeChatSession(int clientA, int clientB, String CKA){
		//Save to ChatSession
		Set<Integer> list = this.getChatList().keySet();
		int sessionId = 1;
		if (!list.isEmpty()) { 
			sessionId = Collections.max(list) + 1;
		}
		
		//add to liveChatSession
		ChatSession chat = new ChatSession(sessionId, clientA, clientB, CKA);

		FileWriter fileWriter = null;
		CSVPrinter csvFilePrinter = null;
		
		try {
			
			//initialize FileWriter object / CSVPrinter object 
			fileWriter = new FileWriter(CHAT_LIST_PATH, true);
	        csvFilePrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);
	        
			//Write a new student object list to the CSV file
	        List chatSession = new ArrayList();
	        chatSession.add(String.valueOf(sessionId));
	        chatSession.add(String.valueOf(clientA));
	        chatSession.add(String.valueOf(clientB));
	        chatSession.add(CKA);
            
	        csvFilePrinter.printRecord(chatSession);

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
		
		return chat;
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
	public String[] requestHistory(int clientA, int clientB){
		
		List<ChatSession> chatLogList = new ArrayList<ChatSession>(getChatList().values());
		//first loop
		//Loops ChatSession looking for last session between clientA and clientB
		//get the session ID		
		int sessionId = -1;
		
		for(int i = chatLogList.size()- 1; i >= 0; i--) {
			
			int client1 = chatLogList.get(i).getClientA();
			int client2 = chatLogList.get(i).getClientB();
			
			if((clientA == client1 && clientB == client2) || (clientB == client1 && clientA == client2)) {
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
     
       // List<ChatLog> chatLog = new ArrayList<ChatLog>();
        String[] chatLog = null;
        
        try {
        	
            
            //initialize FileReader object
            fileReader = new FileReader(CHAT_HISTORY_PATH);
            
            //initialize CSVParser object
            csvFileParser = new CSVParser(fileReader, csvFileFormat);
            
            //Get a list of CSV file records
            List<CSVRecord> csvRecords = csvFileParser.getRecords(); 
            chatLog = new String[csvRecords.size()-1];
            		
            //Read the CSV file records starting from the second record to skip the header
            for (int i = 1; i < csvRecords.size(); i++) {
	            	CSVRecord record = csvRecords.get(i);
	
	            	chatLog[i-1] = record.get(logHead[1]) + ":" + record.get(logHead[2]);	
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
	//private static final String NEW_LINE_SEPARATOR = "\n";

	public void logChat(int sessionId,int byClientId, String chatText){
		
	
		FileWriter fileWriter = null;
		CSVPrinter csvFilePrinter = null;
		
		//Create the CSVFormat object with "\n" as a record delimiter
		
				
		try {
			
			//initialize FileWriter object / CSVPrinter object 
			fileWriter = new FileWriter(CHAT_HISTORY_PATH, true);
	        csvFilePrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);
	        
			//Write a new student object list to the CSV file
	        List chatLog = new ArrayList();
	        chatLog.add(String.valueOf(sessionId));
	        chatLog.add(String.valueOf(byClientId));
	        chatLog.add(chatText);
            
	        csvFilePrinter.printRecord(chatLog);

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
	

	
}
