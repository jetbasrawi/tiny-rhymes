package com.smartlittlepeople.tinyrhymes.mirror;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.smartlittlepeople.tinyrhymes.domain.SongService;
import com.smartlittlepeople.tinyrhymes.model.Song;
import com.smartlittlepeople.tinyrhymes.model.SongCategories;
import com.smartlittlepeople.tinyrhymes.model.SongCategory;
import com.smartlittlepeople.tinyrhymes.auth.AuthUtils;
import com.smartlittlepeople.tinyrhymes.auth.SessionUtils;
import com.smartlittlepeople.common.URLUtils;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.Maps;
import com.google.api.services.mirror.Mirror;
import com.google.api.services.mirror.Mirror.Timeline;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.MenuValue;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.Subscription;
import com.google.api.services.mirror.model.SubscriptionsListResponse;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.api.services.mirror.model.TimelineListResponse;


public final class MirrorUtils {

	private static final Logger log = Logger.getLogger(MirrorUtils.class.getName());
	
	public static Mirror getMirror(HttpServletRequest req) throws IOException {
		String userId = SessionUtils.getUserId(req);
		Credential credential = AuthUtils.getCredential(userId);
		return getMirror(credential);
	}

	public static Mirror getMirror(String userId) throws IOException {
		Credential credential = AuthUtils.getCredential(userId);
		return getMirror(credential);
	}

	public static Mirror getMirror(Credential credential) throws IOException {
		return new Mirror.Builder(new UrlFetchTransport(),
				new JacksonFactory(), credential)
		.setApplicationName("Tiny Rhymes").build();
	}

	public static void insertItemsIntoTimeline(List<TimelineItem> timelineItems, String userId) throws IOException {
		
		String bundleId = timelineItems.get(0).getBundleId();
		
		Mirror mirror = MirrorUtils.getMirror(userId);
		Timeline timeline = mirror.timeline();
		for (TimelineItem item : timelineItems) {
			timeline.insert(item).execute();
		}
		
		log.severe("Inserted " + retrieveAllTimelineItems(mirror, bundleId).size() + " timeline items for user " + userId);
		
	}
	
	public static void subscribeToTimelineUpdates(HttpServletRequest req,
			String userId) throws IOException {
		Mirror mirror = MirrorUtils.getMirror(req);

		final String callbackUrl = URLUtils.fullUrl(req, "/timelinecallback");

		Subscription tliSubscription = new Subscription()
				.setCallbackUrl(callbackUrl)
				.setVerifyToken("a_secret_to_everybody").setUserToken(userId)
				.setCollection("timeline")
				.setOperation(Collections.singletonList("UPDATE"));

		mirror.subscriptions().insert(tliSubscription).execute();

		// TODO: check if this user has subscribed, skip if already has
		SubscriptionsListResponse subscriptions = mirror.subscriptions().list()
				.execute();
		for (Subscription sub : subscriptions.getItems()) {
			System.out.println(sub);
		}
	}
	

	public static List<TimelineItem> buildTimelineBundle(ServletContext ctx,
			HttpServletRequest req, String userId, List<Song> songs, SongCategories categories) throws IOException,
			ServletException {

		String bundleId = "tinyrhymes" + UUID.randomUUID();

		List<TimelineItem> timelineItems = new ArrayList<TimelineItem>(songs.size() + 2);

		timelineItems.add(buildBundleCoverItem(ctx, req, bundleId));
		timelineItems.addAll(buildSongItems(ctx, req, songs, bundleId));	
		timelineItems.add(buildCategorySelectorItem(ctx, req, bundleId, categories, categories.defaultCategory(), songs));
		
		return timelineItems;

	}

	private static TimelineItem buildCategorySelectorItem(ServletContext ctx, HttpServletRequest req, String bundleId, SongCategories categories, SongCategory selectedCategory, List<Song> songs) 
			throws IOException, ServletException {
		
		Map<String, String> replacementValues;
		String htmlString;
		
		replacementValues = Maps.newHashMap();
		replacementValues.put("heading", selectedCategory.getName());
		replacementValues.put("numsongs", Integer.toString(songs.size()));
		replacementValues.put("imageURL", URLUtils.fullUrl(req, selectedCategory.getImageURL()));
		
		htmlString = TemplateRenderer.renderSongBundleCategorySelector(ctx, replacementValues);
		
		TimelineItem categoryItem = new TimelineItem()
			.setTitle("Select category")
			.setHtml(htmlString)
			.setBundleId(bundleId)
			.setMenuItems(new LinkedList<MenuItem>());
		
		for(SongCategory category : categories.getCategories()){
			
			//Add category Nursery Rhymes
			List<MenuValue> menuValues = new ArrayList<MenuValue>(2);
			menuValues.add(
					new MenuValue()
						.setState("DEFAULT")
						.setDisplayName(category.getName()));
			menuValues.add(
					new MenuValue()
						.setState("PENDING")
						.setDisplayName(category.getName() + " loading..."));
			
			categoryItem.getMenuItems().add(
					new MenuItem()
						.setAction("CUSTOM")
						.setId(category.getId())
						.setValues(menuValues));
			
		}
		
		//Add pin menu item
		categoryItem.getMenuItems().add(
				new MenuItem()
				.setAction("TOGGLE_PINNED"));

		
		return categoryItem;
	}

	private static TimelineItem buildBundleCoverItem(ServletContext ctx, HttpServletRequest req, String bundleId)
			throws IOException, ServletException {
		
		String appName = "Tiny Rhymes"; 
		Map<String, String> replacementValues;
		String htmlString;
		// Build Cover Item
		replacementValues = Maps.newHashMap();
		replacementValues.put("imageURL", URLUtils.fullUrl(req, "/static/images/tr_cover.jpg"));
		replacementValues.put("covertitle", appName);
		
		htmlString = TemplateRenderer.renderSongBundleCover(ctx,
				replacementValues);

		TimelineItem coverItem = new TimelineItem().setTitle(appName)
				.setHtml(htmlString).setBundleId(bundleId)
				.setIsBundleCover(true)
				.setNotification(
						new NotificationConfig()
							.setLevel("DEFAULT")) //Will chime when inserted
				.setMenuItems(new LinkedList<MenuItem>());
		

		
		return coverItem;
	}

	private static List<TimelineItem> buildSongItems(ServletContext ctx, HttpServletRequest req, List<Song> songs, String bundleId) 
					throws IOException, ServletException {
		
		List<TimelineItem> timelineItems = new ArrayList<TimelineItem>(songs.size());
		
		Map<String, String> replacementValues;
		String htmlString;
		// Build Song Items
		for (Song song : songs) {

			int index = songs.indexOf(song) + 1;

			replacementValues = Maps.newHashMap();
			replacementValues.put("song", song.getName());
			replacementValues.put("imageURL",  URLUtils.fullUrl(req, song.getImageURL()));
			
			htmlString = TemplateRenderer.renderSongBundleItem(ctx, replacementValues);

			TimelineItem songItem = new TimelineItem().setTitle(song.getName())
					.setHtml(htmlString)
					.setSpeakableText("You should sing " + song.getName())
					.setBundleId(bundleId)
					.setMenuItems(new LinkedList<MenuItem>());

			//Add play menu item
			songItem.getMenuItems().add(
					new MenuItem()
						.setAction("PLAY_VIDEO")
						.setId("PLAY_VIDEO")
						.setPayload(song.getVideoUrl()));

			//Add custom menu item
			List<MenuValue> menuValues = new ArrayList<MenuValue>(1);
			menuValues.add(
					new MenuValue()
						.setState("DEFAULT")
						.setDisplayName("Favourite")
						.setIconUrl(URLUtils.fullUrl(req, "/static/icons/ic_star_50.png")));
			
			songItem.getMenuItems().add(
					new MenuItem()
						.setAction("CUSTOM")
						.setId("ADD_FAVOURITE_" + song.getId())
						.setValues(menuValues));


			timelineItems.add(songItem);

		}
		
		return timelineItems;
	}

	public static void updateTimelineBundle(ServletContext ctx, HttpServletRequest req,
			String userId, String itemId, SongCategories categories, SongCategory selectedCategory) throws IOException, ServletException {
		
		List<Song> songs = SongService.getSongsByCategory(selectedCategory);
		
		Mirror mirror = getMirror(userId);
		Timeline timeline = mirror.timeline();
		TimelineItem item = timeline.get(itemId).execute();
		String bundleId = item.getBundleId();
		
		List<TimelineItem> items = retrieveAllTimelineItems(mirror, bundleId);
		
		log.severe("Number of items retreived " + items.size() + "for bundleId " + bundleId);
		
		int count = 0;
		for(TimelineItem itm : items) {
			
			log.severe("Item " + count++);
			log.severe("Item Id " + itm.getId());
			log.severe("Is Bundle Cover " + itm.getIsBundleCover());
			
			if(itm.getId().equals(itemId) || itm.getIsBundleCover() != null ){
				continue;
			}
			
			timeline.delete(itm.getId()).execute();
			
		}
			
		List<TimelineItem> songItems = buildSongItems(ctx, req, songs, bundleId);
		insertItemsIntoTimeline(songItems, userId);

		TimelineItem categorySelector = buildCategorySelectorItem(ctx, req, bundleId, categories, selectedCategory, songs);
		timeline.patch(itemId, categorySelector).execute();	
		
	}
	
	public static List<TimelineItem> retrieveAllTimelineItems(Mirror service, String bundleId) {
	    List<TimelineItem> result = new ArrayList<TimelineItem>();
	    try {
	      
	    	Timeline.List request = bundleId != null ? 
	    			service.timeline().list().set("bundleId", bundleId) 
	    			: service.timeline().list();
	    	
	    	do {
	    		TimelineListResponse timelineItems = request.execute();
	    		if (timelineItems.getItems() != null && timelineItems.getItems().size() > 0) {
	    			result.addAll(timelineItems.getItems());
	    			request.setPageToken(timelineItems.getNextPageToken());
	        } else {
	          break;
	        }
	      } while (request.getPageToken() != null && request.getPageToken().length() > 0);
	    } catch (IOException e) {
	      System.err.println("An error occurred: " + e);
	      return null;
	    }
	    return result;
	  }


}
