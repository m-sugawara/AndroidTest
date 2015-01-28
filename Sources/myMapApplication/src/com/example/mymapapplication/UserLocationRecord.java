package com.example.mymapapplication;

import com.google.android.gms.maps.model.LatLng;

public class UserLocationRecord {
	
	Long id;
	Long record_id;
	Double latitude;
	Double longitude;
	String created_at;
	
	public UserLocationRecord() {
	}
	
	public UserLocationRecord(Long id, Long record_id, Double latitude, Double longitude, String created_at) {
		this.id = id;
		this.record_id = record_id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.created_at = created_at;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setRecordId(Long record_id) {
		this.record_id = record_id;
	}
	
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	public void setLatlng (LatLng latlng) {
		this.latitude = latlng.latitude;
		this.longitude = latlng.longitude;
	}
	
	public void setCreatedAt(String created_at) {
		this.created_at = created_at;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public Long getRecordId() {
		return this.record_id;
	}
	
	public Double getLatitude() {
		return this.latitude;
	}
	
	public Double getLongitude() {
		return this.longitude;
	}
	
	public LatLng getLatLng() {
		return new LatLng(this.latitude, this.longitude);
	}
	
	public String getCreatedAt() {
		return this.created_at;
	}

}
