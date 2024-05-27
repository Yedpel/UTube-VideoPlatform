package com.example.utube.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.utube.R;

public class MovieActivity extends AppCompatActivity {

    private VideoView videoView;
    private Button playButton;
    private Button pauseButton;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        videoView = findViewById(R.id.video_view);
        playButton = findViewById(R.id.play_button);
        pauseButton = findViewById(R.id.pause_button);
        stopButton = findViewById(R.id.stop_button);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Sample video URL
        String videoUrl = "http://techslides.com/demos/sample-videos/small.mp4";
        videoView.setVideoPath(videoUrl);

        playButton.setOnClickListener(v -> videoView.start());

        pauseButton.setOnClickListener(v -> videoView.pause());

        stopButton.setOnClickListener(v -> {
            videoView.stopPlayback();
            videoView.resume();
        });
    }
}
