package com.smartlittlepeople.tinyrhymes.domain;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.api.services.mirror.model.TimelineItem;
import com.smartlittlepeople.tinyrhymes.mirror.MirrorUtils;
import com.smartlittlepeople.tinyrhymes.model.SongCategories;
import com.smartlittlepeople.tinyrhymes.model.Song;

public class TinyRhymesService {

	private static final Logger log = Logger.getLogger(MirrorUtils.class.getName());
	
	public static void insertSongBundle(ServletContext ctx, HttpServletRequest req, String userId)
			throws IOException, ServletException {
		
		log.info("Inserting song bundle for user: " + userId);

		SongCategories categories = CategoryService.allCategories();
		
		//Get the songs for the user
		List<Song> songs = SongService.getSongsByCategory(categories.defaultCategory());
	
		//Build timeline bundle
		List<TimelineItem> timelineItems = MirrorUtils.buildTimelineBundle(ctx, req, userId, songs, categories);

		//Insert into timeline
		MirrorUtils.insertItemsIntoTimeline(timelineItems, userId);
		
	}

}
