package com.smartlittlepeople.tinyrhymes.model;

import java.util.List;

import com.smartlittlepeople.tinyrhymes.model.SongCategory;

public class SongCategories {

	List<SongCategory> categories;

	public List<SongCategory> getCategories() {
		return categories;
	}
	

	public SongCategories(List<SongCategory> categories) {
		super();
		this.categories = categories;
	}
	
	public SongCategory defaultCategory() {
		
		for(SongCategory category : categories){
			if(category.isDefault())
				return category;
		}
		
		return categories.get(0);
	}

	public SongCategory categoryWithId(String categoryId) {
		
		for(SongCategory category : categories){
			if(categoryId.equals(category.getId()))
				return category;
		}
		
		return null;
	}
	
}
