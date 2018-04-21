package com.chat.model;

public class OnlineUser {

	private int clientId;
	private String userId;
	private String ip;
	private int port;
	private boolean available;

	private static final String [] HEADER = {"clientId","userId","ip","port","available"};

	public static String[] getHeader() {
		return HEADER;
	}

	public OnlineUser(int clientId, String userId, String ip, int port) {
		this.clientId = clientId;
		this.userId = userId;
		this.ip = ip;
		this.port = port;
		this.available = true;
		
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	@Override
	public String toString() {
		return "OnlineUser [clientId=" + clientId + ", userId=" + userId + ", ip=" + ip + ", port=" + port
				+ ", available=" + available + "]";
	}
	
}
