package com.server.model;



public class ChatList {
	private int sessionId;
	private int clientA;
	private int clientB;
	private String encryptionKey;
	
	private static final String [] HEADER = {"sessionId","clientA","clientB","encryptionKey"};

	
	public static String[] getHeader() {
		return HEADER;
	}

	public ChatList(){
		
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public int getClientA() {
		return clientA;
	}

	public void setClientA(int clientA) {
		this.clientA = clientA;
	}

	public int getClientB() {
		return clientB;
	}

	public void setClientB(int clientB) {
		this.clientB = clientB;
	}

	public String getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	

}
