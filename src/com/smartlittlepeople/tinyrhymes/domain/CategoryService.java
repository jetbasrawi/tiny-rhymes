package com.smartlittlepeople.tinyrhymes.domain;

import com.smartlittlepeople.tinyrhymes.model.SongCategories;
import com.smartlittlepeople.tinyrhymes.model.SongCategory;

import java.util.ArrayList;
import java.util.List;

public class CategoryService {

	public static SongCategories allCategories(){
		
		List<SongCategory> categories = new ArrayList<SongCategory>(2);
		
		categories.add(new SongCategory("Nursery Rhymes", "NURSERY_RHYMES", true, "/static/images/nursery_rhymes_cover.jpg"));
		categories.add(new SongCategory("Theme Songs", "THEME_SONGS", false, "/static/images/theme_songs_cover.jpg"));
		
		return new SongCategories(categories);
		
	}	
}

