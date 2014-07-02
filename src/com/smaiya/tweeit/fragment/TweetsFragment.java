package com.smaiya.tweeit.fragment;

import java.util.LinkedList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.smaiya.example.tweeit.R;
import com.smaiya.tweeit.TweetAdapter;
import com.smaiya.tweeit.TweetAdapter.TweetAdapterListener;
import com.smaiya.tweeit.TweetDetailedViewActivity;
import com.smaiya.tweeit.Tweets;
import com.smaiya.tweeit.TwitterClient;
import com.smaiya.tweeit.TwitterClientApp;
import com.smaiya.tweeit.User;
import com.smaiya.tweeit.views.LoginActivity;
import com.smaiya.tweeit.views.TweetComposeActivity;
import com.smaiya.tweeit.views.UserProfileActivity;

public abstract class TweetsFragment extends Fragment implements
		TweetAdapterListener {
	protected LinkedList<Tweets> tweets;
	protected ArrayAdapter<Tweets> tweetAdpater;
	protected ListView lvTweets;
	protected SwipeRefreshLayout swipeLayout;
	protected TwitterClient client;
	private static SharedPreferences pref;
	private static User myUserDetails;

	private static final int COMPOSE_ACTIVITY_REQUEST_CODE = 100;
	private static final int REPLY_ACTIVITY_REQUEST_CODE = 200;
	private static final int DETAILED_VIEW_ACTIVITY_REQUEST_CODE = 300;
	private static final String PREFERENCE_KEY = "smaiya";

	// Abstract methods that needs to implemented in the inherited class
	protected abstract void getOldTweets(int page);

	protected abstract void getNewTweets();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		client = TwitterClientApp.getRestClient();
		tweets = new LinkedList<Tweets>();
		tweetAdpater = new TweetAdapter(getActivity(), tweets,
				TweetsFragment.this);
		
		//Get the logged in user info
		pref = getActivity().getSharedPreferences(PREFERENCE_KEY,
				Context.MODE_PRIVATE);
		getMyUserInfo();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tweet_list, container, false);

		swipeLayout = (SwipeRefreshLayout) view
				.findViewById(R.id.swipe_container);
		lvTweets = (ListView) view.findViewById(R.id.lvTweets);

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

		lvTweets.setAdapter(tweetAdpater);

		// Detailed view
		lvTweets.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent(getActivity(),
						TweetDetailedViewActivity.class);
				Tweets tweet = tweets.get(position);
				i.putExtra("tweet", tweet);
				startActivityForResult(i, DETAILED_VIEW_ACTIVITY_REQUEST_CODE);
				return true;
			}
		});

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.timeline, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.miCompose:
			onCompose();
			return true;
		case R.id.miLogout:
			onLogout();
			return true;
		case R.id.miProfile:
			onViewProfile();
			return true;
		case android.R.id.home:
			getActivity().finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * Logout by clearing the access token. Handle the on click of logout item.
	 */
	public void onLogout() {
		client.clearAccessToken();
		Intent i = new Intent(getActivity(), LoginActivity.class);
		startActivity(i);
	}

	public void onViewProfile() {
		Intent i = new Intent(getActivity(), UserProfileActivity.class);
		i.putExtra("userScreenName", myUserDetails.getScreenName());
		startActivity(i);
	}

	public void onCompose() {
		Intent i = new Intent(getActivity(), TweetComposeActivity.class);
		startActivityForResult(i, COMPOSE_ACTIVITY_REQUEST_CODE);
	}

	public void getMyUserInfo() {
		if (pref.contains("name") && !pref.getString("name", "").isEmpty()) {
			myUserDetails = new User();
			myUserDetails.setName(pref.getString("name", ""));
			myUserDetails.setUid(pref.getLong("uid", 0));
			myUserDetails.setProfileImageUrl(pref.getString("profileImageUrl",
					""));
			myUserDetails.setScreenName(pref.getString("screenName", ""));
		} else {
			client.getMyInfo(new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject response) {
					myUserDetails = User.fromJson(response);
				}

				@Override
				public void onFailure(Throwable e, String s) {
					Log.d("debug Timeline", e.toString());
				}
			});
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == COMPOSE_ACTIVITY_REQUEST_CODE
				&& resultCode == Activity.RESULT_OK) {
			// Handle new Tweet
			String status = data.getStringExtra("status").trim();
			if (status.length() > 0) {
				client.postTweetUpdate(true, status, null,
						new JsonHttpResponseHandler() {
							@Override
							public void onSuccess(JSONObject jsonObject) {
								getNewTweets();
							}

							@Override
							public void onFailure(Throwable e, String s) {
								Toast.makeText(getActivity(),
										"An error occurred!",
										Toast.LENGTH_SHORT).show();
							}
						});
			}

		}

		if (requestCode == DETAILED_VIEW_ACTIVITY_REQUEST_CODE
				&& resultCode == Activity.RESULT_OK) {
			// Handle Reply to an existing tweet
			String reply = data.getStringExtra("status_reply").trim();
			if (reply.length() > 0) {
				String reply_id = data.getStringExtra("id").trim();
				client.postTweetUpdate(false, reply, reply_id,
						new JsonHttpResponseHandler() {
							@Override
							public void onSuccess(JSONObject jsonObject) {
								getNewTweets();
							}

							@Override
							public void onFailure(Throwable e, String s) {
								Toast.makeText(getActivity(),
										"An error occurred!",
										Toast.LENGTH_SHORT).show();
							}
						});
			}
		}

	}

	@Override
	public void onTweetReplyClick(Tweets tweet) {
		Intent i = new Intent(getActivity(), TweetComposeActivity.class);
		i.putExtra("tweetForReply", tweet);
		startActivityForResult(i, REPLY_ACTIVITY_REQUEST_CODE);
	}

	@Override
	public void onUserProfileClicked(Tweets tweet) {
		String userScreenName = tweet.getUser().getScreenName();
		Intent i = new Intent(getActivity(), UserProfileActivity.class);
		i.putExtra("userScreenName", userScreenName);
		startActivity(i);
	}

	@Override
	public void onRetweetClick(Tweets tweet, final int position) {
		Long uid = tweet.getUid();
		boolean isRetweeted = tweet.isRetweeted();
		if (!isRetweeted == true) {
			client.retweet(uid, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject response) {
					Tweets currTweet = tweets.get(position);
					currTweet.setRetweeted(true);
					currTweet.setRetweetCount(currTweet.getRetweetCount() + 1);
					tweets.set(position, currTweet);
					tweetAdpater.notifyDataSetChanged();
				}

				@Override
				public void onFailure(Throwable e, String s) {
					Log.d("debug TweetArrayAdapter", e.toString());
				}
			});
		}
	}

	@Override
	public void onFavoriteClick(Tweets tweet, final int position) {
		Long uid = tweet.getUid();
		final boolean willGetFavorited = tweet.isFav() ? false : true;
		client.onTweetFavChange(willGetFavorited, uid,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject response) {
						Tweets currTweet = tweets.get(position);
						if (willGetFavorited) {
							currTweet.setFav(true);
							currTweet.setFavCount(currTweet.getFavCount() + 1);
						} else {
							currTweet.setFav(false);
							currTweet.setFavCount(currTweet.getFavCount() - 1);
						}
						tweets.set(position, currTweet);
						tweetAdpater.notifyDataSetChanged();
					}

					@Override
					public void onFailure(Throwable e, String s) {
						Log.d("debug TweetArrayAdapter", e.toString());
					}
				});
	}
}
