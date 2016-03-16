package com.smartlittlepeople.tinyrhymes.model;

public class SongCategory {
	
	private String imageURL;
	public String getImageURL() {
		return imageURL;
	}
	private void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	private void setName(String name) {
		this.name = name;
	}
	private String id;
	public String getId() {
		return id;
	}
	private void setId(String id) {
		this.id = id;
	}
	
	private boolean isDefault;
	public boolean isDefault() {
		return isDefault;
	}
	private void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	
	public SongCategory(String name, String id, boolean isDefault, String imageURL) {
		super();
		this.setImageURL(imageURL);
		this.setName(name);
		this.setId(id);
		this.setDefault(isDefault);
		
	}
	
}
