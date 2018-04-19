package com.server.model;

public class ChatLog {
	private int chatId;
	private int sessionId;
	private String chatText;

	private static final String [] HEADER = {"sessionId","clientA","clientB","encryptionKey"};

	public static String[] getHeader() {
		return HEADER;
	}


	public ChatLog(int chatId,int sessionId, String chatText) {
		this.chatId = chatId;
		this.sessionId = sessionId;
		this.chatText = chatText;
	}


	public int getChatId() {
		return chatId;
	}


	public void setChatId(int chatId) {
		this.chatId = chatId;
	}


	public int getSessionId() {
		return sessionId;
	}


	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}


	public String getChatText() {
		return chatText;
	}


	public void setChatText(String chatText) {
		this.chatText = chatText;
	}
	
	
	
}
