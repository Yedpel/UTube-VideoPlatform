package com.example.utube.activities;

import android.content.SharedPreferences;
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

public class VideoDetailActivity extends AppCompatActivity {

    private VideoView videoView;
    private TextView titleTextView, authorTextView, viewsTextView, uploadTimeTextView, likesTextView;
    private ImageView authorProfilePic;
    private Button likeButton;
    private SharedPreferences sharedPreferences;
    private boolean isLiked = false;
    private String videoId;
    private int likes;
    private static final String PREFS_NAME = "video_prefs";

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

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Get video details from intent
        videoId = getIntent().getStringExtra("VIDEO_ID");
        String videoUrl = getIntent().getStringExtra("VIDEO_URL");
        String title = getIntent().getStringExtra("TITLE");
        String author = getIntent().getStringExtra("AUTHOR");
        int views = getIntent().getIntExtra("VIEWS", 0); // Changed to int
        String uploadTime = getIntent().getStringExtra("UPLOAD_TIME");
        String authorProfilePicUrl = getIntent().getStringExtra("AUTHOR_PROFILE_PIC_URL");
        likes = getIntent().getIntExtra("LIKES", 0); // Get likes from intent

        // Increment the views count
        int updatedViews = views + 1;
        viewsTextView.setText(updatedViews + " views");

        // Save the updated views count
        saveUpdatedViews(videoId, updatedViews);

        // Load likes from SharedPreferences
        likes = sharedPreferences.getInt(videoId + "_likes", likes);
        isLiked = sharedPreferences.getBoolean(videoId + "_liked", false);

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
        likesTextView.setText(likes + " likes");

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
        isLiked = sharedPreferences.getBoolean(videoId + "_liked", false);
        updateLikeButton();

        likeButton.setOnClickListener(v -> {
            isLiked = !isLiked;
            if (isLiked) {
                likes++;
            } else {
                likes--;
            }
            likesTextView.setText(likes + " likes");
            saveUpdatedLikes(videoId, likes);
            sharedPreferences.edit().putBoolean(videoId + "_liked", isLiked).apply();
            updateLikeButton();
        });
    }

    private void saveUpdatedViews(String videoId, int updatedViews) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(videoId + "_views", updatedViews);
        editor.apply();
    }

    private void saveUpdatedLikes(String videoId, int updatedLikes) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(videoId + "_likes", updatedLikes);
        editor.apply();
    }

    private void updateLikeButton() {
        if (isLiked) {
            likeButton.setText("Unlike");
        } else {
            likeButton.setText("Like");
        }
    }
}