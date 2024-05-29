package com.example.utube.activities;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.utube.R;

public class VideoPlayerActivity extends AppCompatActivity {
    private VideoView videoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoView = findViewById(R.id.video_view);

        // Get the video URL from the intent
        String videoUrl = getIntent().getStringExtra("videoUrl");
        if (videoUrl != null) {
            Uri videoUri = Uri.parse(videoUrl);
            videoView.setVideoURI(videoUri);
            videoView.setMediaController(new MediaController(this));
            videoView.setOnPreparedListener(mp -> videoView.start());
        }
        // Add this in VideoPlayerActivity.java
        Log.d("VideoPlayerActivity", "Video URL: " + videoUrl);

    }
}
