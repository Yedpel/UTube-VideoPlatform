package com.example.utube.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utube.R;
import com.example.utube.models.Users;
import com.example.utube.models.Video;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_VIDEO_PICK = 2;
    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private Button btnLogin, btnThemeSwitch, btnRegister, btnAddVideo, btnLogout;
    private EditText searchBox;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "theme_prefs";
    private static final String THEME_KEY = "current_theme";
    private static final String LOGGED_IN_KEY = "logged_in";
    private static final String LOGGED_IN_USER = "logged_in_user";
    private int videoIdCounter = 14;
    private Uri selectedVideoUri;
    private String loggedInUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(LOGGED_IN_KEY, false);
        loggedInUser = sharedPreferences.getString(LOGGED_IN_USER, null);
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
        videoAdapter = new VideoAdapter(VideoManager.getInstance().getFilteredVideoList());
        recyclerView.setAdapter(videoAdapter);

        btnLogin = findViewById(R.id.login_button);
        btnThemeSwitch = findViewById(R.id.theme_button);
        btnRegister = findViewById(R.id.register_button);
        btnAddVideo = findViewById(R.id.add_video_button);
        searchBox = findViewById(R.id.search_box);
        btnLogout = new Button(this);
        btnLogout.setText("Logout");

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

        // Initialize intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USERNAME")) {
            loggedInUser = intent.getStringExtra("USERNAME");
            sharedPreferences.edit().putBoolean(LOGGED_IN_KEY, true).putString(LOGGED_IN_USER, loggedInUser).apply();
            updateUIForLoggedInUser(loggedInUser);
        } else if (isLoggedIn) {
            updateUIForLoggedInUser(loggedInUser);
        } else {
            updateUIForGuest();
        }

        if (savedInstanceState != null) {
            // Restore the video list
            ArrayList<Video> videoList = savedInstanceState.getParcelableArrayList("video_list");
            if (videoList != null) {
                VideoManager.getInstance().setVideoList(videoList);
            }

            // Restore the RecyclerView state
            recyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable("recycler_state"));
        } else if (VideoManager.getInstance().getVideoList().isEmpty()) {
            loadVideoData();
        }

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



    private void updateUIForLoggedInUser(String username) {
        btnLogin.setVisibility(View.GONE);
        btnRegister.setVisibility(View.GONE);

        LinearLayout buttonContainer = findViewById(R.id.button_container);
        buttonContainer.addView(btnLogout);

        btnLogout.setOnClickListener(v -> {
            sharedPreferences.edit().putBoolean(LOGGED_IN_KEY, false).remove(LOGGED_IN_USER).apply();
            Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
            reloadMainActivity();
        });
    }

    private void updateUIForGuest() {
        btnLogin.setVisibility(View.VISIBLE);
        btnRegister.setVisibility(View.VISIBLE);

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        LinearLayout buttonContainer = findViewById(R.id.button_container);
        buttonContainer.removeView(btnLogout);
    }

    private void reloadMainActivity() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
                String id = obj.getString("id");
                String title = obj.getString("title");
                String author = obj.getString("author");
                int views = obj.getInt("views");
                String uploadTime = obj.getString("uploadTime");
                String thumbnailUrl = obj.getString("thumbnailUrl");
                String authorProfilePicUrl = obj.getString("authorProfilePicUrl");
                String videoUrl = obj.getString("videoUrl");
                String category = obj.getString("category");
                int likes = obj.getInt("likes");

                int updatedViews = getUpdatedViews(id, views);
                int updatedLikes = getUpdatedLikes(id, likes);

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
        dialog.setAddVideoListener((title, category, previewImageUrl) -> {
            videoIdCounter++;
            String id = "new_" + videoIdCounter;
            String author = loggedInUser != null ? loggedInUser : "guest";
            String uploadTime = "Just now";
            int views = 0;
            int likes = 0;

            Video video = new Video(id, title, author, views, uploadTime, previewImageUrl, "drawable/error_image.webp", selectedVideoUri.toString(), category, likes);
            VideoManager.getInstance().addVideo(video);
            videoAdapter.notifyDataSetChanged();
        });
        dialog.show(getSupportFragmentManager(), "AddVideoDialog");
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the video list
        outState.putParcelableArrayList("video_list", new ArrayList<>(VideoManager.getInstance().getVideoList()));
        // Save the RecyclerView state
        outState.putParcelable("recycler_state", recyclerView.getLayoutManager().onSaveInstanceState());
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

            holder.menuButton.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(holder.menuButton.getContext(), holder.menuButton);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.video_item_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.edit_video) {
                        EditVideoDialog dialog = EditVideoDialog.newInstance(video.getId());
                        dialog.setOnDismissListener(dialogInterface -> videoAdapter.notifyDataSetChanged());
                        dialog.show(((AppCompatActivity) holder.itemView.getContext()).getSupportFragmentManager(), "EditVideoDialog");
                        return true;
                    } else if (item.getItemId() == R.id.delete_video) {
                        VideoManager.getInstance().removeVideo(video.getId());
                        notifyDataSetChanged();
                        return true;
                    }
                    return false;
                });
                popupMenu.show();
            });

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

                try {
                    // Load thumbnail image
                    int thumbnailResId = getResources().getIdentifier(video.getThumbnailUrl(), "drawable", getPackageName());
                    if (thumbnailResId != 0) {
                        thumbnail.setImageResource(thumbnailResId);
                    } else {
                        Picasso.get().load(video.getThumbnailUrl()).into(thumbnail);
                    }

                    // Load author profile picture
                    int authorProfilePicResId = getResources().getIdentifier(video.getAuthorProfilePicUrl(), "drawable", getPackageName());
                    if (authorProfilePicResId != 0) {
                        authorProfilePic.setImageResource(authorProfilePicResId);
                    } else {
                        Picasso.get().load(video.getAuthorProfilePicUrl()).into(authorProfilePic);
                    }
                } catch (Exception e) {
                    Log.e("VideoAdapter", "Error loading images", e);
                    thumbnail.setImageResource(R.drawable.error_image);
                    authorProfilePic.setImageResource(R.drawable.error_image);
                }
            }
        }
    }
}
