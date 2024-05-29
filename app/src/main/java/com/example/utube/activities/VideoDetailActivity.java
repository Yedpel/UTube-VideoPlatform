package com.example.utube.activities;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
    private TextView titleTextView, authorTextView, viewsTextView, uploadTimeTextView;
    private ImageView authorProfilePic;

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
    }
}
