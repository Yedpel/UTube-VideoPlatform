package com.example.utube.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utube.R;
import com.example.utube.models.Video;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_VIDEO_PICK = 2;
    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private Button btnLogin, btnThemeSwitch, btnRegister, btnAddVideo;
    private EditText searchBox;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "theme_prefs";
    private static final String THEME_KEY = "current_theme";
    private int videoIdCounter = 14;
    private Uri selectedVideoUri;

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
        videoAdapter = new VideoAdapter(VideoManager.getInstance().getFilteredVideoList(), this);
        recyclerView.setAdapter(videoAdapter);

        btnLogin = findViewById(R.id.login_button);
        btnThemeSwitch = findViewById(R.id.theme_button);
        btnRegister = findViewById(R.id.register_button);
        btnAddVideo = findViewById(R.id.add_video_button);
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

        btnAddVideo.setOnClickListener(v -> openVideoPicker());
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

    private void openVideoPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_VIDEO_PICK);
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
                VideoManager.getInstance().getLikesCountMap().put(id, updatedLikes);
                VideoManager.getInstance().getLikedStateMap().put(id, sharedPreferences.getBoolean(id + "_liked", false));

                Video video = new Video(id, title, author, updatedViews, uploadTime, thumbnailUrl, authorProfilePicUrl, videoUrl, category, updatedLikes);
                VideoManager.getInstance().addVideo(video);
            }
            videoAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("MainActivity", "Error loading video data", e);
        }
    }

    private int getUpdatedViews(String videoId, int defaultViews) {
        return sharedPreferences.getInt(videoId + "_views", defaultViews);
    }

    private int getUpdatedLikes(String videoId, int defaultLikes) {
        return sharedPreferences.getInt(videoId + "_likes", defaultLikes);
    }

    private void filterVideos(String query) {
        List<Video> filteredVideoList = VideoManager.getInstance().getFilteredVideoList();
        filteredVideoList.clear();
        for (Video video : VideoManager.getInstance().getVideoList()) {
            if (video.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    video.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                filteredVideoList.add(video);
            }
        }
        videoAdapter.notifyDataSetChanged();
    }

    private void filterVideosByCategory(String category) {
        List<Video> filteredVideoList = VideoManager.getInstance().getFilteredVideoList();
        filteredVideoList.clear();
        for (Video video : VideoManager.getInstance().getVideoList()) {
            if (video.getCategory().equalsIgnoreCase(category)) {
                filteredVideoList.add(video);
            }
        }
        videoAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_PICK && resultCode == RESULT_OK && data != null) {
            selectedVideoUri = data.getData();
            showAddVideoDialog();
        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                String videoId = data.getStringExtra("VIDEO_ID");
                int updatedViews = data.getIntExtra("UPDATED_VIEWS", 0);
                Video video = VideoManager.getInstance().getVideoMap().get(videoId);
                if (video != null) {
                    video.setViews(updatedViews);
                    videoAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void showAddVideoDialog() {
        AddVideoDialog dialog = new AddVideoDialog();
        dialog.setAddVideoListener((title, category) -> {
            videoIdCounter++;
            String id = "new_" + videoIdCounter;
            String author = "defUser";
            String uploadTime = "Just now";
            String thumbnailUrl = "drawable/error_image.webp";
            String authorProfilePicUrl = "drawable/error_image.webp";
            int views = 0;
            int likes = 0;

            Video video = new Video(id, title, author, views, uploadTime, thumbnailUrl, authorProfilePicUrl, selectedVideoUri.toString(), category, likes);
            VideoManager.getInstance().addVideo(video);
            videoAdapter.notifyDataSetChanged();
        });
        dialog.show(getSupportFragmentManager(), "AddVideoDialog");
    }
}
