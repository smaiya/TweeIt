package com.smaiya.example.tweeit;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class User implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private long uid;
	private String name;
	private String screenName;
	private String imageProfileUrl;

	public long getUid() {
		return uid;
	}

	public String getName() {
		return name;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getImageProfileUrl() {
		return imageProfileUrl;
	}

	public static User fromJson(JSONObject jsonObject) {
		User user = new User();

		try {
			user.uid = jsonObject.getLong("id");
			user.name = jsonObject.getString("name");
			user.screenName = jsonObject.getString("screen_name");
			user.imageProfileUrl = jsonObject.getString("profile_image_url");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		return user;
	}
}