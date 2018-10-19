package com.mobile.instagram.bean;

public class User {
	private String id;
	private String name;
	private String headUrl;
	private String location;
	private String date;
	private String content;
	public User(String id, String name, String headUrl, String location, String date, String content){
		this.id = id;
		this.name = name;
		this.headUrl = headUrl;
		this.location=location;
		this.date=date;
		this.content=content;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHeadUrl() {
		return headUrl;
	}
	public String getLocation() {
		return location;
	}
	public String getDate() {
		return date;
	}
	public String getContent() {
		return content;
	}
	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}
	
}
