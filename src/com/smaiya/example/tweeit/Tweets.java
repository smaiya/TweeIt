package com.smaiya.example.tweeit;

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
	private String retweetCount;
	private String favCount;

	public static Tweets fromJson(JSONObject jsonObject) {
		Tweets tweet = new Tweets();

		try {
			tweet.uid = jsonObject.getLong("id");
			tweet.body = jsonObject.getString("text");
			tweet.createdAt = jsonObject.getString("created_at");
			tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
		try{
			tweet.retweetCount = jsonObject.getString("retweet_count");
		} catch (JSONException e) {
			tweet.retweetCount = "0";
		}
		
		try{
			tweet.favCount = jsonObject.getString("favourites_count");
		} catch (JSONException e) {
			tweet.favCount = "0";
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
	
	public String getRetweetCount(){
		return retweetCount;
	}
	
	public String getFavCount(){
		return favCount;
	}
}