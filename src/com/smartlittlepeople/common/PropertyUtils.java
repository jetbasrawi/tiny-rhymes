package com.smartlittlepeople.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertyUtils {

	public static Properties getOauthProperties(){
		
		return getProperties("/oauth.properties");
				
	}
	
	
	public static Properties getProperties(String url){
		
		Properties prop = new Properties();
		InputStream input = null;
	 
		try {
	 
			InputStream is = PropertyUtils.class.getResourceAsStream(url);
	 		prop.load(is);
	  
		} 
		catch (IOException ex) {
			ex.printStackTrace();
		} 
		finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	}
}

