package com.example.mymapapplication;

public class UserLocation {
	
	Long id;
	String created_at;
	
	public UserLocation() {
	}
	
	public UserLocation(Long id, String created_at) {
		this.id = id;
		this.created_at = created_at;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setCreatedAt(String created_at) {
		this.created_at = created_at;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public String getCreatedAt() {
		return this.created_at;
	}

}
