package com.example.utube.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utube.R;
import com.example.utube.models.Video;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Video> videoList = new ArrayList<>();
    private List<Video> filteredVideoList = new ArrayList<>();
    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private Button btnLogin, btnThemeSwitch, btnRegister;
    private EditText searchBox;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "theme_prefs";
    private static final String THEME_KEY = "current_theme";
    private HashMap<String, Boolean> likedStateMap = new HashMap<>();
    private HashMap<String, Integer> likesCountMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(THEME_KEY, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(v -> openOptionsMenu());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        videoAdapter = new VideoAdapter(filteredVideoList);
        recyclerView.setAdapter(videoAdapter);

        btnLogin = findViewById(R.id.login_button);
        btnThemeSwitch = findViewById(R.id.theme_button);
        btnRegister = findViewById(R.id.register_button);
        searchBox = findViewById(R.id.search_box);

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnThemeSwitch.setText(sharedPreferences.getBoolean(THEME_KEY, false) ? "Day Mode" : "Night Mode");
        btnThemeSwitch.setOnClickListener(v -> {
            boolean isNightMode = sharedPreferences.getBoolean(THEME_KEY, false);
            if (isNightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                sharedPreferences.edit().putBoolean(THEME_KEY, false).apply();
                btnThemeSwitch.setText("Night Mode");
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                sharedPreferences.edit().putBoolean(THEME_KEY, true).apply();
                btnThemeSwitch.setText("Day Mode");
            }
        });

        loadVideoData();

        // Filter videos based on search
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterVideos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_sport) {
            filterVideosByCategory("Sport");
            return true;
        } else if (itemId == R.id.action_news) {
            filterVideosByCategory("News");
            return true;
        } else if (itemId == R.id.action_cinema) {
            filterVideosByCategory("Cinema");
            return true;
        } else if (itemId == R.id.action_gaming) {
            filterVideosByCategory("Gaming");
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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
                String id = obj.getString("id"); // Add ID
                String title = obj.getString("title");
                String author = obj.getString("author");
                int views = obj.getInt("views"); // Changed to int
                String uploadTime = obj.getString("uploadTime");
                String thumbnailUrl = obj.getString("thumbnailUrl");
                String authorProfilePicUrl = obj.getString("authorProfilePicUrl");
                String videoUrl = obj.getString("videoUrl");
                String category = obj.getString("category");
                int likes = obj.getInt("likes"); // Load likes

                // Get the updated views count from SharedPreferences
                int updatedViews = getUpdatedViews(id, views);
                int updatedLikes = getUpdatedLikes(id, likes);

                // Store the initial likes count and liked state in memory
                likesCountMap.put(id, updatedLikes);
                likedStateMap.put(id, sharedPreferences.getBoolean(id + "_liked", false));

                Video video = new Video(id, title, author, updatedViews, uploadTime, thumbnailUrl, authorProfilePicUrl, videoUrl, category, updatedLikes);
                videoList.add(video);
            }
            filteredVideoList.addAll(videoList);
            videoAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getUpdatedViews(String videoId, int defaultViews) {
        return sharedPreferences.getInt(videoId + "_views", defaultViews);
    }

    private int getUpdatedLikes(String videoId, int defaultLikes) {
        return sharedPreferences.getInt(videoId + "_likes", defaultLikes);
    }

    private void filterVideos(String query) {
        filteredVideoList.clear();
        for (Video video : videoList) {
            if (video.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    video.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                filteredVideoList.add(video);
            }
        }
        videoAdapter.notifyDataSetChanged();
    }

    private void filterVideosByCategory(String category) {
        filteredVideoList.clear();
        for (Video video : videoList) {
            if (video.getCategory().equalsIgnoreCase(category)) {
                filteredVideoList.add(video);
            }
        }
        videoAdapter.notifyDataSetChanged();
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
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, VideoDetailActivity.class);
                intent.putExtra("VIDEO_ID", video.getId());
                intent.putExtra("VIDEO_URL", video.getVideoUrl());
                intent.putExtra("TITLE", video.getTitle());
                intent.putExtra("AUTHOR", video.getAuthor());
                intent.putExtra("VIEWS", video.getViews());
                intent.putExtra("UPLOAD_TIME", video.getUploadTime());
                intent.putExtra("AUTHOR_PROFILE_PIC_URL", video.getAuthorProfilePicUrl());
                intent.putExtra("LIKES", video.getLikes());
                startActivityForResult(intent, 1);
            });
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
                views.setText(video.getViews() + " views");
                uploadTime.setText(video.getUploadTime());

                int thumbnailResId = getResources().getIdentifier(video.getThumbnailUrl(), null, getPackageName());
                int authorProfilePicResId = getResources().getIdentifier(video.getAuthorProfilePicUrl(), null, getPackageName());

                if (thumbnailResId != 0) {
                    thumbnail.setImageResource(thumbnailResId);
                } else {
                    Picasso.get().load(video.getThumbnailUrl()).into(thumbnail);
                }

                if (authorProfilePicResId != 0) {
                    authorProfilePic.setImageResource(authorProfilePicResId);
                } else {
                    Picasso.get().load(video.getAuthorProfilePicUrl()).into(authorProfilePic);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                String videoId = data.getStringExtra("VIDEO_ID");
                int updatedViews = data.getIntExtra("UPDATED_VIEWS", 0);
                for (Video video : videoList) {
                    if (video.getId().equals(videoId)) {
                        video.setViews(updatedViews);
                        break;
                    }
                }
                videoAdapter.notifyDataSetChanged();
            }
        }
    }
}
