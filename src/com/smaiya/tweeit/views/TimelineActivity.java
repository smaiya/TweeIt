package com.smaiya.tweeit.views;


import com.smaiya.example.tweeit.R;
import com.smaiya.tweeit.FragmentTabListener;
import com.smaiya.tweeit.fragment.TweetsFragment;
import com.smaiya.tweeit.fragment.UserTimelineFragment;
import com.smaiya.tweeit.fragment.HomeTimelineFragment;
import com.smaiya.tweeit.fragment.MentionsTimelineFragment;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class TimelineActivity extends FragmentActivity{
	TweetsFragment tweetsListFragment;
	UserTimelineFragment userTimelineFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		setupTabs();
	}

	private void setupTabs() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true); 

		Tab tab1 = actionBar
			.newTab()
			.setText("Home")
			.setTag("HomeTimelineFragment")
			.setTabListener(
				new FragmentTabListener<HomeTimelineFragment>(R.id.flTimeline, this, "Home",
								HomeTimelineFragment.class));

		actionBar.addTab(tab1);

		Tab tab2 = actionBar
			.newTab()
			.setText("Mentions")
			.setTag("MentionsTimelineFragment")
			.setTabListener(
			    new FragmentTabListener<MentionsTimelineFragment>(R.id.flTimeline, this, "Mentions",
			    		MentionsTimelineFragment.class));

		actionBar.addTab(tab2);
		actionBar.selectTab(tab1);
	}
}