package com.example.mywalkapplication;

import com.google.android.gms.maps.model.LatLng;

public class UserLocationRecord {
	
	Long id;				//子ID
	Long record_id;			//レコードID（親ID)
	Double latitude;		//緯度
	Double longitude;		//経度
	float distance;			//前のデータからの移動距離
	float total_distance;	//トータルの移動距離
	int duration;			//経過時間（ミリ秒）
	String created_at;		//データ作成日時
	
	public UserLocationRecord() {
	}
	
	public UserLocationRecord(Long id
			, Long record_id
			, Double latitude
			, Double longitude
			, float distance
			, float total_distance
			, int duration
			, String created_at) {
		this.id = id;
		this.record_id = record_id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.distance = distance;
		this.total_distance = total_distance;
		this.duration = duration;
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
	
	public void setDistance(float distance) {
		this.distance = distance;
	}
	
	public void setTotalDistance(float total_distance) {
		this.total_distance = total_distance;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
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
	
	public float getDistance() {
		return this.distance;
	}
	
	public float getTotalDistance() {
		return this.total_distance;
	}
	
	public int getDuration() {
		return this.duration;
	}
	
	public String getCreatedAt() {
		return this.created_at;
	}

}
