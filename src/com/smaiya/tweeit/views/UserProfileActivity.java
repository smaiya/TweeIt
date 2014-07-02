package com.smaiya.tweeit.views;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.smaiya.example.tweeit.R;
import com.smaiya.tweeit.TwitterClient;
import com.smaiya.tweeit.TwitterClientApp;
import com.smaiya.tweeit.User;
import com.smaiya.tweeit.fragment.UserTimelineFragment;
import com.squareup.picasso.Picasso;


public class UserProfileActivity extends FragmentActivity {
	private String userScreenName; 
	TwitterClient client;
	ImageView ivProfilePicture;
	TextView tvProfileName;
	TextView tvProfileScreenName;
	TextView tvProfileTagline;
	TextView tvTweetCount;
	TextView tvFollowingCount;
	TextView tvFollowerCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		client = TwitterClientApp.getRestClient();
		userScreenName = getIntent().getStringExtra("userScreenName");
		userScreenName = userScreenName == null ? "" : userScreenName;
		setupViews();
		getUserInfo();
		showProfileTweets();
	}

	public void setupViews() {
		ivProfilePicture = (ImageView) findViewById(R.id.ivProfilePicture);
		tvProfileName = (TextView) findViewById(R.id.tvProfileName);
		tvProfileScreenName = (TextView) findViewById(R.id.tvProfileScreenName);
		tvProfileTagline = (TextView) findViewById(R.id.tvProfileTagline);
		tvTweetCount = (TextView) findViewById(R.id.tvTweetCount);
		tvFollowingCount = (TextView) findViewById(R.id.tvFollowingCount);
		tvFollowerCount = (TextView) findViewById(R.id.tvFollowerCount);
	}

	public void getUserInfo() {
		client.getUserInfo(userScreenName, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject response) {
				User user = User.fromJson(response);
				tvProfileName.setText(user.getName());
				tvProfileScreenName.setText("@" + user.getScreenName());
				tvProfileTagline.setText(user.getDescription());
				tvTweetCount.setText(user.getTweetCount() + " Tweets");
				tvFollowingCount.setText(user.getFollowingCount() + " Following");
				tvFollowerCount.setText(user.getFollowersCount() + " Followers");
				String userProfileURL = user.getProfileImageUrl();
				
				if (userProfileURL != null) {
					Picasso.with(getBaseContext())
			        .load(userProfileURL)
			        .into(ivProfilePicture);
				} else {
					ivProfilePicture.setImageDrawable(null);
				}
			}

			@Override
			public void onFailure(Throwable e, String s) { 
				Toast.makeText(getApplicationContext(), "Failure!!!", Toast.LENGTH_LONG).show();
				Log.d("debug Timeline", e.toString());
			}
		});
	}

	public void doFragmentTransaction(Fragment fragment, boolean addToBackStack) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.flProfile, fragment);
		if (addToBackStack) {
			ft.addToBackStack(null);
		}
		ft.commit();
	}

	public void showProfileTweets() {
		UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(userScreenName);
		doFragmentTransaction(userTimelineFragment, false);
	}
}
