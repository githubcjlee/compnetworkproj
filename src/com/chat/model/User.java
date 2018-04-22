package com.chat.model;

public class User {
	private String id;
	private String password;

	private static final String [] HEADER = {"id","password"};

	public User(String id, String password) {
		this.id = id;
		this.password = password;
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", password=" + password + "]";
	}
	
	
}
