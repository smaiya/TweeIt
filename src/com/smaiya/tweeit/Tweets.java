package com.smaiya.tweeit;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Tweets implements Serializable{

	private static final long serialVersionUID = 1L;
	private long uid;
	private String body;
	private String createdAt;
	private User user;
	private long retweetCount;
	private long favCount;
	private boolean favorite;
	private boolean retweeted;
	private String pics;

	public static Tweets fromJson(JSONObject json) {
		Tweets tweet = new Tweets();
		try {
			tweet.body = json.getString("text");
			tweet.uid = json.getLong("id");
			tweet.createdAt = json.getString("created_at");
			tweet.retweetCount = json.getLong("retweet_count");
			tweet.favCount = json.getLong("favorite_count");
			tweet.favorite = json.getBoolean("favorited");
			tweet.retweeted = json.getBoolean("retweeted");
			if (json.has("retweeted_status")) {
				JSONObject retweetObject = json
						.getJSONObject("retweeted_status");
				tweet.user = User.fromJson(retweetObject.getJSONObject("user"));
			} else {
				tweet.user = User.fromJson(json.getJSONObject("user"));
			}
			if (json.has("entities")) {
				JSONObject entitiesObj = json.getJSONObject("entities");
				if (entitiesObj.has("media")) {
					JSONArray mediaObjArr = entitiesObj.getJSONArray("media");
					// For now just caring about first item
					JSONObject mediaObj = mediaObjArr.getJSONObject(0);
					if (mediaObj.getString("type").equals("photo")) {
						tweet.pics = mediaObj.getString("media_url");
					}
				} 
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return tweet;
	}

	public static ArrayList<Tweets> fromJsonArray(JSONArray jsonArray) {
		ArrayList<Tweets> tweets = new ArrayList<Tweets>();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject;

			try {
				jsonObject = jsonArray.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
				continue;
			}

			Tweets tweet = Tweets.fromJson(jsonObject);
			if (tweet != null) {
				tweets.add(tweet);
			}
		}

		return tweets;
	}

	public long getUid() {
		return uid;
	}

	public String getBody() {
		return body;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public User getUser() {
		return user;
	}
	
	public long getRetweetCount(){
		return retweetCount;
	}
	
	public long getFavCount(){
		return favCount;
	}

	public boolean isFav() {
		return favorite;
	}

	public boolean isRetweeted() {
		return retweeted;
	}

	public String getPicsUrl() {
		return pics;
	}

	public void setRetweeted(boolean isRetweeted) {
		retweeted = isRetweeted;
	}

	public void setRetweetCount(long newCount) {
		retweetCount = newCount;
	}
	
	public void setFav(boolean isFav) {
		favorite = isFav;
	}

	public void setFavCount(long newCount) {
		favCount = newCount;
	}
}