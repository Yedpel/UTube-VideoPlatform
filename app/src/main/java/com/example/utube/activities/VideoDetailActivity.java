package com.example.utube.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.utube.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class VideoDetailActivity extends AppCompatActivity {

    private VideoView videoView;
    private TextView titleTextView, authorTextView, viewsTextView, uploadTimeTextView, likesTextView;
    private ImageView authorProfilePic;
    private Button likeButton;
    private boolean isLiked = false;
    private String videoId;
    private int likes;
    private int views;
    private static HashMap<String, Boolean> likedStateMap = new HashMap<>();
    private static HashMap<String, Integer> likesCountMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        videoView = findViewById(R.id.video_view);
        titleTextView = findViewById(R.id.video_title);
        authorTextView = findViewById(R.id.video_author);
        viewsTextView = findViewById(R.id.video_views);
        uploadTimeTextView = findViewById(R.id.video_upload_time);
        authorProfilePic = findViewById(R.id.author_profile_pic);
        likesTextView = findViewById(R.id.likes_count);
        likeButton = findViewById(R.id.like_button);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Get video details from intent
        videoId = getIntent().getStringExtra("VIDEO_ID");
        String videoUrl = getIntent().getStringExtra("VIDEO_URL");
        String title = getIntent().getStringExtra("TITLE");
        String author = getIntent().getStringExtra("AUTHOR");
        views = getIntent().getIntExtra("VIEWS", 0);
        String uploadTime = getIntent().getStringExtra("UPLOAD_TIME");
        String authorProfilePicUrl = getIntent().getStringExtra("AUTHOR_PROFILE_PIC_URL");
        likes = getIntent().getIntExtra("LIKES", 0);

        // Increment the views count
        views++;
        viewsTextView.setText(views + " views");

        // Load likes state from memory
        isLiked = likedStateMap.getOrDefault(videoId, false);
        likes = likesCountMap.getOrDefault(videoId, likes);
        likesTextView.setText(likes + " likes");

        // Log the URL for debugging
        Log.d("VideoDetailActivity", "Author Profile Pic URL: " + authorProfilePicUrl);

        // Set video details
        if (videoUrl.startsWith("http")) {
            videoView.setVideoURI(Uri.parse(videoUrl));
        } else {
            int videoResId = getResources().getIdentifier(videoUrl, "raw", getPackageName());
            videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + videoResId));
        }
        titleTextView.setText(title);
        authorTextView.setText(author);
        uploadTimeTextView.setText(uploadTime);

        // Load author's profile picture with Picasso, set placeholder and error image
        int authorProfilePicResId = getResources().getIdentifier(authorProfilePicUrl, "drawable", getPackageName());
        Picasso.get()
                .load(authorProfilePicResId)
                .placeholder(R.drawable.placeholder_image)  // Replace with your placeholder image
                .error(R.drawable.error_image)  // Replace with your error image
                .into(authorProfilePic, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Picasso", "Image loaded successfully");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("Picasso", "Error loading image", e);
                    }
                });

        // Start video
        videoView.start();

        // Set initial like button state
        updateLikeButton();

        likeButton.setOnClickListener(v -> {
            isLiked = !isLiked;
            if (isLiked) {
                likes++;
            } else {
                likes--;
            }
            likesTextView.setText(likes + " likes");
            likedStateMap.put(videoId, isLiked);
            likesCountMap.put(videoId, likes);
            updateLikeButton();
        });
    }

    private void updateLikeButton() {
        if (isLiked) {
            likeButton.setText("Unlike");
        } else {
            likeButton.setText("Like");
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("VIDEO_ID", videoId);
        intent.putExtra("UPDATED_VIEWS", views);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
