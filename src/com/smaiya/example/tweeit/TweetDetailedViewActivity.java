package com.smaiya.example.tweeit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetDetailedViewActivity extends Activity {

	private static final int STATUS_MAX_LENGTH = 140;
	private ImageView ivDisplayPic;
	private TextView tvUser;
	private TextView tvTweet;
	private TextView tvTime;
	private EditText etReply;
	private TextView tvCharCount;

	private long reply_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tweet_detailed_view);
		Tweets tweet = (Tweets) getIntent().getSerializableExtra("tweet");

		ivDisplayPic = (ImageView) findViewById(R.id.ivProfilePic);
		tvUser = (TextView) findViewById(R.id.tvUser);
		tvTweet = (TextView) findViewById(R.id.tvTweet);
		tvTime = (TextView) findViewById(R.id.tvTime);
		tvCharCount = (TextView) findViewById(R.id.tvCharCnt);
		etReply = (EditText) findViewById(R.id.etReplyTweet);
		reply_id = tweet.getUid();
		

		ivDisplayPic.setImageResource(android.R.color.transparent);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration
				.createDefault(getBaseContext()));
		imageLoader.displayImage(tweet.getUser().getImageProfileUrl(),
				ivDisplayPic);

		tvUser.setText(tweet.getUser().getScreenName());
		tvTweet.setText(tweet.getBody());
		tvTime.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
		
		etReply.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void afterTextChanged(Editable s) {
				int count = STATUS_MAX_LENGTH - s.length();
				tvCharCount.setText(String.valueOf(count));
				if (count > 10) {
					tvCharCount.setTextColor(Color.GRAY);
				} else if (count > 5){
					tvCharCount.setTextColor(Color.rgb(240, 190, 40)); // orange
				} else {
					tvCharCount.setTextColor(Color.RED);
				}
			}
		});
		
		etReply.setText("@"+tweet.getUser().getScreenName());
	}

	// getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
	public String getRelativeTimeAgo(String rawJsonDate) {
		String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(twitterFormat,
				Locale.ENGLISH);
		sf.setLenient(true);

		String relativeDate = "";
		try {
			long dateMillis = sf.parse(rawJsonDate).getTime();
			relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
					System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS)
					.toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return relativeDate;
	}
	
	public void onReply(View v) {
		Intent i = new Intent();
		i.putExtra("id", String.valueOf(reply_id));
		i.putExtra("status_reply", etReply.getText().toString());
		setResult(RESULT_OK, i);
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.detailed, menu);
		return true;
	}

	public void onCancel(MenuItem mi) {
		Intent i = new Intent();
		setResult(RESULT_CANCELED, i);
		finish();
	}
}
