package com.smartlittlepeople.tinyrhymes.model;

public class Song {

	private String id;
	private String name;
	private String videoURL;
	private String imageURL;
	private String category;

	public Song(String id, String name, String videoURL, String imageURL, String category) {
		super();
		
		//TODO: Validate parameters
		
		this.setId(id);
		this.setName(name);
		this.setVideoUrl(videoURL);
		this.setImageURL(imageURL);
		this.setCategory(category);
	}
	
	public String getCategory(){
		return this.category;
	}
	
	private void setCategory(String category) {
		this.category = category;	
	}

	public String getId() {
		return id;
	}

	private void setId(String id) {
		this.id = id;
	}

	
	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	public String getVideoUrl() {
		return videoURL;
	}

	private void setVideoUrl(String url) {
		this.videoURL = url;
	}

	public String getImageURL() {
		return imageURL;
	}

	private void setImageURL(String imgURL) {
		this.imageURL = imgURL;

	}

}
