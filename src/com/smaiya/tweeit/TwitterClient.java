package com.smaiya.tweeit;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * https://github.com/thecodepath/android-rest-client-template
 */

public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
    public static final String REST_URL = "https://api.twitter.com/1.1";
    
    /*
     * Get these values from dev.twitter.com, by registering a new application.
     */
    public static final String REST_CONSUMER_KEY = "IM1cWZoKfwXAbII59cHJbFDbD";
    public static final String REST_CONSUMER_SECRET = "IOr1UDf0D3dAWYG0b0FmdDYFkuufsuefRKhRuAdSE1Ypb19iOH";
    public static final String REST_CALLBACK_URL = "oauth://cpsimpletweet";
    
    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    public void getTweetHomeTimeline(long max_id, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl("statuses/home_timeline.json");

    	RequestParams params = new RequestParams();

    	if (max_id != -1) {
    		params.put("max_id", max_id+"");
    	} else {
    		params.put("since_id", "1");
    	}
   
    	client.get(apiUrl, params, handler);
    }

    public void getTweetUserTimeline(long max_id, String screen_name, AsyncHttpResponseHandler handler) {
       	String apiUrl = getApiUrl("statuses/user_timeline.json");

    	RequestParams params = new RequestParams();

    	if (max_id != -1) {
    		params.put("max_id", max_id+"");
    	} else {
    		params.put("since_id", "1");
    	}
    	params.put("screen_name", screen_name);
    	client.get(apiUrl, params, handler);
		
	}
    
    public void getTweetMentionsTimeline (long max_id, AsyncHttpResponseHandler handler) {
		String apiURL = getApiUrl("statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
		if (max_id != -1) {
			params.put("max_id", max_id + "");
		} else {
			params.put("since_id", "1");
		}
		client.get(apiURL, params, handler);
	}
    
    public void postTweetUpdate(boolean isNewTweet, String status, String status_id, AsyncHttpResponseHandler handler) {
    	if(isNewTweet){
    		//It's a new Tweet
    	String apiUrl = getApiUrl("statuses/update.json");
    	RequestParams params = new RequestParams();
    	params.put("status", status);
    	client.post(apiUrl, params, handler);
    	}
    	else{
    		//It's a reply to an existing tweet.
    		String apiUrl = getApiUrl("statuses/update.json");
        	RequestParams params = new RequestParams();
        	//Note that status should include @username otherwise reply will be ignored
        	//https://dev.twitter.com/docs/api/1.1/post/statuses/update
        	params.put("status", status);
        	params.put("in_reply_to_status_id", status_id);
        	client.post(apiUrl, params, handler);
    	}
    }
    
    public void getMyInfo (AsyncHttpResponseHandler handler) {
		String apiURL = getApiUrl("account/verify_credentials.json");
		client.get(apiURL, null, handler);
	}

	public void getUserInfo (String userScreenName, AsyncHttpResponseHandler handler) {
		String apiURL = getApiUrl("users/show.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", userScreenName);
		client.get(apiURL, params, handler);
	}

	public void logoutUser(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("logout");
		client.post(apiUrl, null, handler);
	}

	public void onTweetFavChange(boolean favorite, long tweetId, AsyncHttpResponseHandler handler) {
		String apiURL = favorite ? getApiUrl("favorites/create.json") : getApiUrl("favorites/destroy.json");
		RequestParams params = new RequestParams();
		params.put("id", tweetId + "");
		client.post(apiURL, params, handler);
		
	}

	public void retweet(Long tweetId,
			JsonHttpResponseHandler handler) {
		String apiURL = getApiUrl("statuses/retweet/" + tweetId + ".json");
		client.post(apiURL, null, handler);
		
	}

}