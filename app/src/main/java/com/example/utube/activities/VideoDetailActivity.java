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
    private TextView titleTextView, authorTextView, viewsTextView, uploadTimeTextView, likesCountTextView;
    private ImageView authorProfilePic;
    private Button likeButton;
    private int likes;
    private boolean isLiked;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "likes_prefs";
    private String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        videoView = findViewById(R.id.video_view);
        titleTextView = findViewById(R.id.video_title);
        authorTextView = findViewById(R.id.video_author);
        viewsTextView = findViewById(R.id.video_views);
        uploadTimeTextView = findViewById(R.id.video_upload_time);
        authorProfilePic = findViewById(R.id.author_profile_pic);
        likesCountTextView = findViewById(R.id.likes_count);
        likeButton = findViewById(R.id.like_button);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Get video details from intent
        String videoUrl = getIntent().getStringExtra("VIDEO_URL");
        String title = getIntent().getStringExtra("TITLE");
        String author = getIntent().getStringExtra("AUTHOR");
        String views = getIntent().getStringExtra("VIEWS");
        String uploadTime = getIntent().getStringExtra("UPLOAD_TIME");
        String authorProfilePicUrl = getIntent().getStringExtra("AUTHOR_PROFILE_PIC_URL");
        likes = getIntent().getIntExtra("LIKES", 0);
        videoId = getIntent().getStringExtra("VIDEO_ID");

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
        viewsTextView.setText(views);
        uploadTimeTextView.setText(uploadTime);
        likesCountTextView.setText("Likes: " + likes);

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

        updateLikeButton();

        // Like button functionality
        likeButton.setOnClickListener(v -> {
            if (isLiked) {
                likes--;
                isLiked = false;
            } else {
                likes++;
                isLiked = true;
            }
            likesCountTextView.setText("Likes: " + likes);
            updateLikeButton();

            // Save the like status and count
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(videoId + "_liked", isLiked);
            editor.putInt(videoId + "_likes", likes);
            editor.apply();
        });

        // Start video
        videoView.start();
    }

    private void updateLikeButton() {
        if (isLiked) {
            likeButton.setText("Unlike");
        } else {
            likeButton.setText("Like");
        }
    }
}
