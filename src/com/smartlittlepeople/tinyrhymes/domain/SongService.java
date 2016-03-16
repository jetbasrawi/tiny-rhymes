package com.smartlittlepeople.tinyrhymes.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.smartlittlepeople.tinyrhymes.callback.TimelineCallbackServlet;
import com.smartlittlepeople.tinyrhymes.model.SongCategory;
import com.smartlittlepeople.tinyrhymes.model.Song;

public class SongService {
	
	private static final Logger log = Logger
			.getLogger(SongService.class.getName());


	public static List<Song> getSongs() {

		List<Song> songsToReturn = new ArrayList<Song>(2);
			
			songsToReturn.add(new Song("BLCK_SHP", "Baa Baa Black Sheep",
					"https://www.youtube.com/watch?v=6RmzV9QgnlQ", 
					"/static/images/black-sheep.png", "NURSERY_RHYMES"));
			songsToReturn.add(new Song("TWINK_STAR", "Twinkle Twinkle Little Star",
					"https://www.youtube.com/watch?v=XyEXmeO_1Rg",
					"/static/images/twinkle_star.jpg", "NURSERY_RHYMES"));				
			songsToReturn.add(new Song("BOB_BUILDER", "Bob the builder",
					"https://www.youtube.com/watch?v=jZ6db7h1C4g", 
					"/static/images/bob_the_builder.jpg", "THEME_SONGS"));
			songsToReturn.add(new Song("POST_PAT", "Postman Pat",
					"https://www.youtube.com/watch?v=Dlao5TWcOnY",
					"/static/images/postman_pat.jpg", "THEME_SONGS"));
			
		return songsToReturn;

	}
	
	
	public static List<Song> getSongsByCategory(SongCategory category) {

		List<Song> songsToReturn = new ArrayList<Song>();
			
		for(Song song : getSongs()){
			
			if(song.getCategory().equals(category.getId())){
				songsToReturn.add(song);
			}
		}
	
		return songsToReturn;

	}


	public static void removeFavourite(String songId, String userId) {
		
		log.severe("Remove from favourites" + songId + " User " + userId);
		
	}

	public static void addFavourite(String songId, String userId) {
		
		log.severe("Add to favourites" + songId + " User " + userId);
		
	}

	
}

