package com.smaiya.example.tweeit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


/*
 * https://github.com/nostra13/Android-Universal-Image-Loader
 */
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetAdapter extends ArrayAdapter<Tweets> {

	public TweetAdapter(Context context, List<Tweets> tweets) {
		super(context, 0, tweets);
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
		TextView tvTweet = (TextView) v.findViewById(R.id.tvTweet);
		TextView tvTime = (TextView) v.findViewById(R.id.tvTime);
		TextView tvReplyCount = (TextView) v.findViewById(R.id.tvRetweetCount);

		ivDisplayPic.setImageResource(android.R.color.transparent);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));
		imageLoader.displayImage(tweet.getUser().getImageProfileUrl(), ivDisplayPic);

		tvUser.setText(tweet.getUser().getScreenName());
		tvTweet.setText(tweet.getBody());
		tvTime.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
		tvReplyCount.setText("Retweet : "+tweet.getRetweetCount());

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
}