package com.smaiya.tweeit;

import java.io.Serializable;
import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

public class User implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private long uid;
	private String name;
	private String screenName;
	private String profileImageUrl;
	private long followersCount;
	private long tweetCount;
	private long followingCount;
	private String description;

	public static User fromJson(JSONObject json) {
		User user = new User();
		try {
			user.name = json.getString("name");
			user.uid = json.getLong("id");
			user.screenName = json.getString("screen_name");
			String imageUrl = json.getString("profile_image_url");
			imageUrl = imageUrl.replace("_normal", "_bigger");
			user.profileImageUrl = imageUrl;
			user.followersCount = json.getLong("followers_count");
			user.tweetCount = json.getLong("statuses_count");
			user.followingCount = json.getLong("friends_count");
			user.description = json.getString("description");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return user;
	}

	public String getName() {
		return name;
	}

	public long getUid() {
		return uid;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public String getTweetCount() {
		return getRelativeNum(tweetCount);
	}

	public void setTweetCount(long newCount) {
		 tweetCount = newCount;
	}

	public String getFollowersCount() {
		return getRelativeNum(followersCount);
	}

	public void setFollowersCount(long followersCount) {
		this.followersCount = followersCount;
	}

	public String getFollowingCount() {
		return getRelativeNum(followingCount);
	}

	public void setFollowingCount(long newCount) {
		followingCount = newCount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	private String getRelativeNum (long number) {
		DecimalFormat df = new DecimalFormat("#.#");
		double relNumber = (double) number / 1000000;
		if (relNumber >= 1) {
			return df.format(relNumber) + "M";
		}
		relNumber = number / 1000;
		if (relNumber >= 1 && relNumber < 1000) {
			return df.format(relNumber) + "K";
		} 

		return number + "";

	}

}