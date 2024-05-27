package com.example.utube.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utube.R;
import com.example.utube.models.Video;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Video> videoList = new ArrayList<>();
    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private Button btnLogin, btnThemeSwitch, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        videoAdapter = new VideoAdapter(videoList);
        recyclerView.setAdapter(videoAdapter);

        btnLogin = findViewById(R.id.login_button);
        btnThemeSwitch = findViewById(R.id.theme_button);
        btnRegister = findViewById(R.id.register_button);

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnThemeSwitch.setOnClickListener(v -> {
            // Implement theme switch logic please
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        loadVideoData();
    }

    private void loadVideoData() {
        try {
            InputStream inputStream = getAssets().open("videos.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String title = obj.getString("title");
                String author = obj.getString("author");
                String views = obj.getString("views");
                String uploadTime = obj.getString("uploadTime");
                String thumbnailUrl = obj.getString("thumbnailUrl");
                String authorProfilePicUrl = obj.getString("authorProfilePicUrl");

                Video video = new Video(title, author, views, uploadTime, thumbnailUrl, authorProfilePicUrl);
                videoList.add(video);
            }
            videoAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
        private List<Video> videoList;

        public VideoAdapter(List<Video> videoList) {
            this.videoList = videoList;
        }

        @Override
        public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
            return new VideoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(VideoViewHolder holder, int position) {
            Video video = videoList.get(position);
            holder.bind(video);
        }

        @Override
        public int getItemCount() {
            return videoList.size();
        }

        class VideoViewHolder extends RecyclerView.ViewHolder {
            TextView title, author, views, uploadTime;
            ImageView thumbnail, authorProfilePic;

            public VideoViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.video_title);
                author = itemView.findViewById(R.id.video_author);
                views = itemView.findViewById(R.id.video_views);
                uploadTime = itemView.findViewById(R.id.video_upload_time);
                thumbnail = itemView.findViewById(R.id.video_thumbnail);
                authorProfilePic = itemView.findViewById(R.id.author_profile_pic);
            }

            public void bind(Video video) {
                title.setText(video.getTitle());
                author.setText(video.getAuthor());
                views.setText(video.getViews());
                uploadTime.setText(video.getUploadTime());
                // Load thumbnail and author profile picture using an image loading library like Glide or Picasso
                // Glide.with(thumbnail.getContext()).load(video.getThumbnailUrl()).into(thumbnail);
                // Glide.with(authorProfilePic.getContext()).load(video.getAuthorProfilePicUrl()).into(authorProfilePic);
                Picasso.get().load(video.getThumbnailUrl()).into(thumbnail);
                Picasso.get().load(video.getAuthorProfilePicUrl()).into(authorProfilePic);
            }

        }
    }
}
