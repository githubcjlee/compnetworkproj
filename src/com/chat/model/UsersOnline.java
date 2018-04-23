package com.chat.model;

/*
 * @author Charlie Nguyen, Ted Ahn
 * 
 */
public class UsersOnline {
	private String id;
	private int cookie;

	private static final String [] HEADER = {"id","cookie"};

	public UsersOnline(String id, int cookie) {
		this.id = id;
		this.cookie = cookie;
	}
	
	public static String[] getHeader() {
		return HEADER;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public int getCookie() {
		return cookie;
	}

	public void setCookie(int cookie) {
		this.cookie = cookie;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", cookie=" + cookie + "]";
	}
	
	
}
