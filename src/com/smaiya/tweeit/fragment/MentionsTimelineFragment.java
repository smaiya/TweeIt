package com.smaiya.tweeit.fragment;

import org.json.JSONArray;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.smaiya.tweeit.EndlessScrollListener;
import com.smaiya.tweeit.Tweets;

import android.os.Bundle;
import android.util.Log;


public class MentionsTimelineFragment extends TweetsFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				getOldTweets(page);
			}
		});
		
		getOldTweets(-1);
	}
	
	@Override
	protected void getOldTweets(int page) {
		swipeLayout.setRefreshing(true);
		long max_id = page != -1 ? tweets.getLast().getUid() : -1;
		client.getTweetMentionsTimeline( max_id,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONArray tweets) {
						tweetAdpater.addAll(Tweets.fromJsonArray(tweets));
					}

					@Override
					public void onFailure(Throwable e, String s) {
						Log.d("debug", e.toString());
						Log.d("debug", s);
					}

					@Override
					public void onFinish() {
						swipeLayout.setRefreshing(false);
					}
				});
	}

	@Override
	protected void getNewTweets() {
		swipeLayout.setRefreshing(true);
		long max_id = tweets.isEmpty() ? -1 : tweets.getLast().getUid();
		client.getTweetMentionsTimeline(max_id,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONArray tweets) {
						tweetAdpater.addAll(Tweets.fromJsonArray(tweets));
						tweetAdpater.notifyDataSetChanged();
					}

					@Override
					public void onFailure(Throwable e, String s) {
						Log.d("debug", e.toString());
						Log.d("debug", s);
					}

					@Override
					public void onFinish() {
						swipeLayout.setRefreshing(false);
					}
				});
	}

}