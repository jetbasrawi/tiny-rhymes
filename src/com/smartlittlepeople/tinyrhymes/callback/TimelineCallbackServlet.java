package com.smartlittlepeople.tinyrhymes.callback;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.mirror.Mirror;
import com.google.api.services.mirror.model.Notification;
import com.google.api.services.mirror.model.UserAction;
import com.smartlittlepeople.tinyrhymes.domain.CategoryService;
import com.smartlittlepeople.tinyrhymes.domain.SongService;
import com.smartlittlepeople.tinyrhymes.mirror.MirrorUtils;
import com.smartlittlepeople.tinyrhymes.model.SongCategories;
import com.smartlittlepeople.tinyrhymes.model.SongCategory;

@SuppressWarnings("serial")
public class TimelineCallbackServlet extends HttpServlet {

	private static final Logger log = Logger
			.getLogger(TimelineCallbackServlet.class.getName());

	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {

	    res.setContentType("text/html");
	    Writer writer = res.getWriter();
	    writer.append("OK");
	    writer.close();
	    
	    ServletContext ctx = getServletContext();
		
		log.severe("doPost");

		JsonFactory jsonFactory = new JacksonFactory();
		Notification notification = jsonFactory.fromInputStream(
				req.getInputStream(), Notification.class);

		String userActionType = null;
		UserAction userAction = null;

		if (notification.getUserActions().isEmpty())
			return;

		userAction = notification.getUserActions().get(0);
		userActionType = userAction.getType();

		log.severe("User Action Type = " + userActionType);

		if ("timeline".equals(notification.getCollection())
				&& "UPDATE".equals(notification.getOperation())) {

			String userId = notification.getUserToken();
			String itemId = notification.getItemId();

			if ("PIN".equals(userActionType)) {
				log.severe("User " + userId + " pinned" + itemId);
			}

			else if ("UNPIN".equals(userActionType)) {
				log.severe("User " + userId + " unpinned" + itemId);
			} else if ("CUSTOM".equals(userActionType)) {

				
				log.severe("Payload " + userAction.getPayload());

				//See if the action is a category change
				SongCategories categories = CategoryService.allCategories();
				SongCategory selectedCategory = categories.categoryWithId(userAction.getPayload());
			
				if(selectedCategory != null){
					log.severe("Load " + selectedCategory.getName());
					MirrorUtils.updateTimelineBundle(ctx, req, userId, itemId, categories, selectedCategory);
					return;
				}
				
				String addFavouritePrefix = "ADD_FAVOURITE_";
				if(userAction.getPayload().startsWith(addFavouritePrefix)){

					String songId = userAction.getPayload().substring(addFavouritePrefix.length());
					SongService.addFavourite(songId, userId);

					return;
				}
				
				String removeFavouritePrefix = "REMOVE_FAVOURITE_";
				if(userAction.getPayload().startsWith(removeFavouritePrefix)){
					
					String songId = userAction.getPayload().substring(removeFavouritePrefix.length());
					SongService.removeFavourite(songId, userId);
					
					return;
				}
								
				log.severe("Unknown Action");
			}
		}
	}
}