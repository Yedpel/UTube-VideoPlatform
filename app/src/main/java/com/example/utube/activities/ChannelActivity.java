package com.example.utube.activities;

import com.example.utube.models.Video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.utube.R;
import com.example.utube.activities.VideoManager;
import com.squareup.picasso.Picasso;
import static com.example.utube.activities.MainActivity.PREFS_NAME;


import java.util.List;

public class ChannelActivity extends AppCompatActivity {

    private TextView channelTitle;
    private Button editUserButton;
    private Button deleteUserButton;
    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private SwipeRefreshLayout swipeRefreshLayout; //try-swip
    private String authorName; //try-swip
    private static final int REQUEST_VIDEO_DETAIL = 1; //try-chanUpd

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load theme from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isNightMode = sharedPreferences.getBoolean("isNightMode", false);
        setTheme(isNightMode ? R.style.AppTheme_Dark : R.style.AppTheme_Light);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        channelTitle = findViewById(R.id.channel_title);
        editUserButton = findViewById(R.id.edit_user_button);
        deleteUserButton = findViewById(R.id.delete_user_button);
        recyclerView = findViewById(R.id.channel_recycler_view);

        authorName = getIntent().getStringExtra("AUTHOR_NAME");
        channelTitle.setText(authorName + "'s Channel");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        videoAdapter = new VideoAdapter(VideoManager.getInstance(getApplication()).getVideosForAuthor(authorName), this);
        recyclerView.setAdapter(videoAdapter);

        authorName = getIntent().getStringExtra("AUTHOR_NAME"); //try-swip
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); //try-swip
        swipeRefreshLayout.setOnRefreshListener(this::refreshVideoList); //try-swip

        editUserButton.setOnClickListener(v -> {
            // TODO: Implement edit user functionality
            Toast.makeText(this, "Edit User functionality not implemented yet", Toast.LENGTH_SHORT).show(); //try-chanUpd
        });

        deleteUserButton.setOnClickListener(v -> {
            // TODO: Implement delete user functionality
            Toast.makeText(this, "Delete User functionality not implemented yet", Toast.LENGTH_SHORT).show(); //try-chanUpd
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_DETAIL && resultCode == RESULT_OK) {
            refreshVideoList(); //try-chanUpd
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshVideoList(); //try-chanUpd
    }

    private void refreshVideoList() { //try-swip
        List<Video> updatedVideos = VideoManager.getInstance(getApplication()).getVideosForAuthor(authorName);
        if (updatedVideos != null && !updatedVideos.isEmpty()) {
            videoAdapter.updateVideos(updatedVideos);
        } else {
            // If the list is empty, we might want to show a message to the user
            Toast.makeText(this, "No videos found for this author", Toast.LENGTH_SHORT).show();
        }
        swipeRefreshLayout.setRefreshing(false);
        Log.d("ChannelActivity", "Refreshed videos count: " + (updatedVideos != null ? updatedVideos.size() : 0)); //try-swip
    } //try-swip

    private class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
        private List<Video> videoList;
        private Context context;

        public VideoAdapter(List<Video> videoList, Context context) {
            this.videoList = videoList;
            this.context = context;
        }

        @Override
        public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
            return new VideoViewHolder(view);
        }

        public void updateVideos(List<Video> newVideos) { //try-swip
            this.videoList.clear(); //try-swip
            this.videoList.addAll(newVideos); //try-swip
            notifyDataSetChanged(); //try-swip
        } //try-swip

        @Override
        public void onBindViewHolder(VideoViewHolder holder, int position) {
            Video video = videoList.get(position);
            holder.bind(video);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, VideoDetailActivity.class);
                intent.putExtra("VIDEO_ID", video.getId());
                intent.putExtra("VIDEO_URL", video.getVideoUrl());
                intent.putExtra("TITLE", video.getTitle());
                intent.putExtra("AUTHOR", video.getAuthor());
                intent.putExtra("VIEWS", video.getViews());
                intent.putExtra("UPLOAD_TIME", video.getUploadTime());
                intent.putExtra("AUTHOR_PROFILE_PIC_URL", video.getAuthorProfilePicUrl());
                intent.putExtra("LIKES", video.getLikes());
                ((Activity) context).startActivityForResult(intent, REQUEST_VIDEO_DETAIL); //try-chanUpd
                // context.startActivity(intent);
            });

            // You can keep the menu button functionality if needed, or remove it for the channel page
        }

        @Override
        public int getItemCount() {
            return videoList.size();
        }

        class VideoViewHolder extends RecyclerView.ViewHolder {
            TextView title, author, views, uploadTime;
            ImageView thumbnail, authorProfilePic;
            Button menuButton;

            public VideoViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.video_title);
                author = itemView.findViewById(R.id.video_author);
                views = itemView.findViewById(R.id.video_views);
                uploadTime = itemView.findViewById(R.id.video_upload_time);
                thumbnail = itemView.findViewById(R.id.video_thumbnail);
                authorProfilePic = itemView.findViewById(R.id.author_profile_pic);
                menuButton = itemView.findViewById(R.id.menu_button);
            }

            public void bind(Video video) {
                title.setText(video.getTitle());
                author.setText(video.getAuthor());
                views.setText(video.getViews() + " views");
                uploadTime.setText(video.getUploadTime());

                loadImageView(thumbnail, video.getThumbnailUrl());
                loadImageView(authorProfilePic, video.getAuthorProfilePicUrl());
            }

            private void loadImageView(ImageView imageView, String imageUrl) {
                if (imageUrl.startsWith("drawable/")) {
                    int imageResId = context.getResources().getIdentifier(imageUrl, null, context.getPackageName());
                    if (imageResId != 0) {
                        imageView.setImageResource(imageResId);
                    } else {
                        imageView.setImageResource(R.drawable.policy);
                    }
                } else if (imageUrl != null) {
                    Picasso.get().load(imageUrl).error(R.drawable.policy).into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.policy);
                }
            }
        }
    }
}