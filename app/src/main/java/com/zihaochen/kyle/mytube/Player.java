package com.zihaochen.kyle.mytube;

import android.os.Bundle;
import android.util.Log;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import static com.zihaochen.kyle.mytube.YouTubeConfig.getApiKey;

/**
 * Created by Kyle on 5/11/2018.
 */

public class Player extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    YouTubePlayerView youtubePlayer;
    private String resource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        Bundle b = getIntent().getExtras();
        if(b != null)
            resource = b.getString("key");
        Log.d("resource",resource);
        youtubePlayer= findViewById(R.id.videoView);
        youtubePlayer.initialize(getApiKey(), this);
    }
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.cueVideo(resource);
    }
    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
}
