package com.example.mywalkapplication;

public class UserLocation {
	
	Long id;
	String movie_file_name;
	String created_at;
	
	public UserLocation() {
	}
	
	public UserLocation(Long id, String movie_file_name, String created_at) {
		this.id = id;
		this.movie_file_name = movie_file_name;
		this.created_at = created_at;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setMovieFileName(String movie_file_name) {
		this.movie_file_name = movie_file_name;
	}
	
	public void setCreatedAt(String created_at) {
		this.created_at = created_at;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public String getMovieFileName() {
		return this.movie_file_name;
	}
	
	public String getCreatedAt() {
		return this.created_at;
	}

}
