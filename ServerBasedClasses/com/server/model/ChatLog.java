package com.server.model;

public class ChatLog {
	private int sessionId;
	private String chatText;

	private static final String [] HEADER = {"sessionId","chatText"};

	public static String[] getHeader() {
		return HEADER;
	}


	public ChatLog(int sessionId, String chatText) {
		this.sessionId = sessionId;
		this.chatText = chatText;
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
