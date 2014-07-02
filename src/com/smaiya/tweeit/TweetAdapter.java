package com.smaiya.tweeit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import com.smaiya.example.tweeit.R;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetAdapter extends ArrayAdapter<Tweets> {
	
	public interface TweetAdapterListener {
		public void onTweetReplyClick(Tweets tweet);
		public void onRetweetClick(Tweets tweet, int position);
		public void onFavoriteClick(Tweets tweet, int position);
		public void onUserProfileClicked(Tweets tweet);
	}
	
	private TweetAdapterListener listener;

	public TweetAdapter(Context context, List<Tweets> tweets, Fragment fragment) {
		super(context, 0, tweets);
		listener = (TweetAdapterListener) fragment;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Tweets tweet = getItem(position);

		View v;
		if (convertView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			v = inflator.inflate(R.layout.tweet_details, parent, false);
		} else {
			v = convertView;
		}

		ImageView ivDisplayPic = (ImageView) v.findViewById(R.id.ivProfilePic);
		TextView tvUser = (TextView) v.findViewById(R.id.tvUser);
		TextView tvUserScreenName = (TextView) v.findViewById(R.id.tvUserScreenName);
		TextView tvTweet = (TextView) v.findViewById(R.id.tvTweet);
		TextView tvTime = (TextView) v.findViewById(R.id.tvTime);
		TextView tvRetweet = (TextView) v.findViewById(R.id.tvRetweet);
		TextView tvFavorite = (TextView) v.findViewById(R.id.tvFavorite);
		ImageView ivPics = (ImageView) v.findViewById(R.id.ivPics);
		ImageView ivReply = (ImageView) v.findViewById(R.id.ivReply);
		ImageView ivFavorite = (ImageView) v.findViewById(R.id.ivFavorite);
		ImageView ivRetweet = (ImageView) v.findViewById(R.id.ivRetweet);
		

		Picasso.with(getContext())
        .load(tweet.getUser().getProfileImageUrl())
        .resize(100,100)
        .error(R.drawable.ic_launcher)
        .into(ivDisplayPic); 
		tvTweet.setText(tweet.getBody());
		tvUserScreenName.setText("@" + tweet.getUser().getScreenName());
		tvUser.setText(tweet.getUser().getName());
		tvTime.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
		tvRetweet.setText(tweet.getRetweetCount()  + "");
		tvFavorite.setText(tweet.getFavCount() + ""); 
		
		if (tweet.isFav()) {
			Picasso.with(getContext())
	        .load(R.drawable.ic_fav_blue)
	        .resize(20,20)
	        .into(ivFavorite); 
		} else {
			Picasso.with(getContext())
	        .load(R.drawable.ic_fav_grey)
	        .resize(20,20)
	        .into(ivFavorite);
		}

		if (tweet.isRetweeted()) {
			Picasso.with(getContext())
	        .load(R.drawable.ic_retweet_blue)
	        .resize(20,20)
	        .into(ivRetweet);
		} else {
			Picasso.with(getContext())
	        .load(R.drawable.ic_retweet_grey)
	        .resize(20,20)
	        .into(ivRetweet); 
		}

		String pics = tweet.getPicsUrl();
		if (pics != null) {
			Picasso.with(getContext())
	        .load(pics)
	        .into(ivPics);
		} else {
			ivPics.setImageDrawable(null);
		}

		setupEventListeners(position, ivReply, ivFavorite, ivRetweet, tvUser, ivDisplayPic);
		
		return v;
	}

	// getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
	public String getRelativeTimeAgo(String rawJsonDate) {
		String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
		sf.setLenient(true);

		String relativeDate = "";
		try {
			long dateMillis = sf.parse(rawJsonDate).getTime();
			relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
					System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return relativeDate;
	}
	
	public void setupEventListeners(final int position, ImageView ivReply, ImageView ivFavorite, ImageView ivRetweet, TextView tvUserName, ImageView ivDisplayPic) {

		ivReply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Tweets tweet = getItem(position);
				listener.onTweetReplyClick(tweet);
			} 
		});

		ivFavorite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Tweets tweet = getItem(position);
				listener.onFavoriteClick(tweet, position);
			}
		});

		ivRetweet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Tweets tweet = getItem(position);
				listener.onRetweetClick(tweet, position);
			}
		});

		tvUserName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Tweets tweet = (Tweets) getItem(position);
				listener.onUserProfileClicked(tweet);
			} 
		});
		
		ivDisplayPic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Tweets tweet = (Tweets) getItem(position);
				listener.onUserProfileClicked(tweet);
			} 
		});
	}
}