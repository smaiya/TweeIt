package com.smaiya.tweeit.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.codepath.oauth.OAuthLoginActivity;
import com.smaiya.example.tweeit.R;
import com.smaiya.tweeit.TwitterClient;

public class LoginActivity extends OAuthLoginActivity<TwitterClient> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}
	
    @Override
    public void onLoginSuccess() {
    	Intent i = new Intent(this, TimelineActivity.class);
    	startActivity(i);
    }

    @Override
    public void onLoginFailure(Exception e) {
        e.printStackTrace();
    }

    public void loginToTweet(View view) {
        getClient().connect();
    }

}

