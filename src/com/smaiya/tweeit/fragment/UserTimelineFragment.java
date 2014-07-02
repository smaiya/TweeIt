package com.smaiya.tweeit.fragment;

import org.json.JSONArray;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.smaiya.tweeit.EndlessScrollListener;
import com.smaiya.tweeit.Tweets;

import android.os.Bundle;
import android.util.Log;

public class UserTimelineFragment extends TweetsFragment {
	protected String screenName;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		screenName = getArguments().getString("userScreenName", "");
	}
	
	public static UserTimelineFragment newInstance(String userScreenName) {
		UserTimelineFragment fragmentDemo = new UserTimelineFragment();
		Bundle args = new Bundle();
		args.putString("userScreenName", userScreenName);
		fragmentDemo.setArguments(args);
		return fragmentDemo;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getOldTweets(-1);
		
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				getOldTweets(page);
			}
		});
	}
	
	@Override
	protected void getOldTweets(int page) {
		swipeLayout.setRefreshing(true);
		long max_id = page != -1 ? tweets.getLast().getUid() : -1;
		client.getTweetUserTimeline(max_id, screenName,
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
		client.getTweetUserTimeline(max_id, screenName,
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