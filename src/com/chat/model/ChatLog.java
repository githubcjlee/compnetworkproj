package com.chat.model;

/*
 * @author Charlie Nguyen, Ted Ahn
 * 
 */
public class ChatLog {
	
	private int sessionId;
	private int byClientId;
	private String chatText;

	private static final String [] HEADER = {"sessionId","byClientId","chatText"};

	public static String[] getHeader() {
		return HEADER;
	}


	public ChatLog(int sessionId, int byClientId, String chatText) {
		this.sessionId = sessionId;
		this.byClientId = byClientId;
		this.chatText = chatText;
	}



	public int getSessionId() {
		return sessionId;
	}


	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}


	public int getByClientId() {
		return byClientId;
	}


	public void setByClientId(int byClientId) {
		this.byClientId = byClientId;
	}


	public String getChatText() {
		return chatText;
	}


	public void setChatText(String chatText) {
		this.chatText = chatText;
	}


	@Override
	public String toString() {
		return "ChatLog [sessionId=" + sessionId + ", byClientId=" + byClientId + ", chatText=" + chatText + "]";
	}
	
}
