package com.smaiya.example.tweeit;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.loopj.android.http.JsonHttpResponseHandler;

public class TimelineActivity extends Activity {
	private TwitterClient client;
	private ArrayList<Tweets> tweets;
	private ArrayAdapter<Tweets> aTweets;

	private SwipeRefreshLayout swipeLayout;
	private ListView lvTweets;

	private static final int COMPOSE_ACTIVITY_REQUEST_CODE = 100;
	private static final int LOGIN_ACTIVITY_REQUEST_CODE = 200;
	private static final int DETAILED_VIEW_ACTIVITY_REQUEST_CODE = 300;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);

		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		lvTweets = (ListView) findViewById(R.id.lvTweets);

		swipeLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				getNewTweets();
			}
		});

		swipeLayout
				.setColorScheme(android.R.color.holo_blue_dark,
						android.R.color.holo_green_dark,
						android.R.color.holo_orange_dark,
						android.R.color.holo_red_dark);

		client = TwitterClientApp.getRestClient();

		tweets = new ArrayList<Tweets>();
		aTweets = new TweetAdapter(this, tweets);
		lvTweets.setAdapter(aTweets);

		lvTweets.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				String max_id = String.valueOf(tweets.get(tweets.size() - 1)
						.getUid());
				getOldTweets(max_id);
			}
		});

		// Display image in full view
		lvTweets.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent(getApplicationContext(),
						TweetDetailedViewActivity.class);
				Tweets tweet = tweets.get(position);
				i.putExtra("tweet", tweet);
				startActivityForResult(i, DETAILED_VIEW_ACTIVITY_REQUEST_CODE);
			}
		});

		getOldTweets(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.timeline, menu);
		return true;
	}

	void getOldTweets(String max_id) {
		swipeLayout.setRefreshing(true);
		client.getTweetHomeTimeline(null, max_id,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONArray tweets) {
						aTweets.addAll(Tweets.fromJsonArray(tweets));
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

	void getNewTweets() {
		swipeLayout.setRefreshing(true);
		String since_id = String.valueOf(tweets.get(0).getUid());
		client.getTweetHomeTimeline(since_id, null,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONArray tweets) {
						TimelineActivity.this.tweets.addAll(0,
								Tweets.fromJsonArray(tweets));
						TimelineActivity.this.aTweets.notifyDataSetChanged();
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

	/*
	 * Compose a new Tweet
	 */
	public void onCompose(MenuItem mi) {
		Intent i = new Intent(this, TweetComposeActivity.class);
		startActivityForResult(i, COMPOSE_ACTIVITY_REQUEST_CODE);
	}

	/*
	 * Logout by clearing the access token. Handle the on click of logout item.
	 */
	public void onLogout(MenuItem mi) {
		client.clearAccessToken();
		Intent i = new Intent(this, LoginActivity.class);
		startActivityForResult(i, LOGIN_ACTIVITY_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == COMPOSE_ACTIVITY_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			// Handle new Tweet
			String status = data.getStringExtra("status").trim();
			if (status.length() > 0) {
				client.postTweetUpdate(true, status, null,
						new JsonHttpResponseHandler() {
							@Override
							public void onSuccess(JSONObject jsonObject) {
								TimelineActivity.this.getNewTweets();
							}

							@Override
							public void onFailure(Throwable e, String s) {
								Toast.makeText(TimelineActivity.this,
										"An error occurred!",
										Toast.LENGTH_SHORT).show();
							}
						});
			}

		}

		if (requestCode == DETAILED_VIEW_ACTIVITY_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			// Handle Reply to an existing tweet
			String reply = data.getStringExtra("status_reply").trim();
			if (reply.length() > 0) {
				String reply_id = data.getStringExtra("id").trim();
				client.postTweetUpdate(false, reply, reply_id,
						new JsonHttpResponseHandler() {
							@Override
							public void onSuccess(JSONObject jsonObject) {
								TimelineActivity.this.getNewTweets();
							}

							@Override
							public void onFailure(Throwable e, String s) {
								Toast.makeText(TimelineActivity.this,
										"An error occurred!",
										Toast.LENGTH_SHORT).show();
							}
						});
			}
		}
	}
}
