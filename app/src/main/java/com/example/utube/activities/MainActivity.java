
package com.example.utube.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.utube.R;
import com.example.utube.models.Users;
import com.example.utube.models.Video;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModelProvider;

import com.example.utube.viewmodels.MainViewModel;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_VIDEO_PICK = 2;
    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private Button btnLogin, btnThemeSwitch, btnRegister, btnAddVideo, btnLogout;
    private EditText searchBox;
    private SharedPreferences sharedPreferences;
    public static final String PREFS_NAME = "theme_prefs";
    private static final String LOGGED_IN_KEY = "logged_in";
    public static final String LOGGED_IN_USER = "logged_in_user";
    private static boolean isNightMode = false; // Static variable for theme mode 
    private int videoIdCounter = 14;
    private Uri selectedVideoUri;
    private String loggedInUser;
    private static boolean isFirstThemeApplication = true;
    private SwipeRefreshLayout swipeRefreshLayout; //try-swip

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(LOGGED_IN_KEY, false);
        loggedInUser = sharedPreferences.getString(LOGGED_IN_USER, null);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        viewModel.getError().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(v -> openOptionsMenu());
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //   videoAdapter = new VideoAdapter(new ArrayList<>(), sharedPreferences);
        videoAdapter = new VideoAdapter(viewModel.getVideos().getValue() != null ? viewModel.getVideos().getValue() : new ArrayList<>(), sharedPreferences);
        recyclerView.setAdapter(videoAdapter);

        viewModel.getVideos().observe(this, videos -> {
            videoAdapter.updateVideos(videos);
            Log.d("MainActivity", "Number of videos in adapter after setting: " + videoAdapter.getItemCount());
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            swipeRefreshLayout.setRefreshing(isLoading);
        }); //try-server

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // viewModel.loadVideos();
            /// viewModel.loadVideosFromDatabase();
            ///  swipeRefreshLayout.setRefreshing(false);
            viewModel.refreshVideos(); //try-server
        });

        // viewModel.loadVideos();

        btnLogin = findViewById(R.id.login_button);
        btnThemeSwitch = findViewById(R.id.theme_button);
        btnRegister = findViewById(R.id.register_button);
        btnAddVideo = findViewById(R.id.add_video_button);
        searchBox = findViewById(R.id.search_box);
        btnLogout = new Button(this);
        btnLogout.setText("Logout");
        btnLogout.setBackgroundResource(R.drawable.button_background);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 0, 10, 0);
        btnLogout.setLayoutParams(params);

        btnThemeSwitch.setText(isNightMode ? "Day Mode" : "Night Mode");
        btnThemeSwitch.setOnClickListener(v -> switchTheme());

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USERNAME")) {
            loggedInUser = intent.getStringExtra("USERNAME");
            sharedPreferences.edit().putBoolean(LOGGED_IN_KEY, true).putString(LOGGED_IN_USER, loggedInUser).apply();
            updateUIForLoggedInUser(loggedInUser);
        } else if (isLoggedIn) {
            if (savedInstanceState == null) {
                sharedPreferences.edit().remove(LOGGED_IN_KEY).remove(LOGGED_IN_USER).apply();
                updateUIForGuest();
            } else {
                updateUIForLoggedInUser(loggedInUser);
            }
        } else {
            updateUIForGuest();
        }

        if (savedInstanceState != null) {
            ArrayList<Video> videoList = savedInstanceState.getParcelableArrayList("video_list");
            if (videoList != null) {
                viewModel.setVideoList(videoList);
            }
            recyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable("recycler_state"));
        } else {
            viewModel.loadVideosFromDatabase();
        }

        restoreUserAddedVideos();

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.filterVideos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnAddVideo.setOnClickListener(v -> {
            if (sharedPreferences.getBoolean(LOGGED_IN_KEY, false)) {
                openVideoPicker();
            } else {
                showLoginPromptDialog();
            }
        });

        if (isFirstThemeApplication) {
            isNightMode = false;
            sharedPreferences.edit().putBoolean("isNightMode", isNightMode).apply();
        }

        isNightMode = sharedPreferences.getBoolean("isNightMode", false);

        applyTheme();

        isFirstThemeApplication = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadVideos(); //try-behave
        //  new Handler().postDelayed(() -> viewModel.loadVideos(), 100); // 100ms delay
        Log.d("MainActivity", "onResume called, reloading videos after delay");
    }


    private void applyTheme() {
        // if (!isFirstThemeApplication) {
        setTheme(isNightMode ? R.style.AppTheme_Dark : R.style.AppTheme_Light);
        updateUIWithTheme(); // Refresh UI elements manually after theme change
        //}
    }

    private void updateUIWithTheme() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        Button menuButton = findViewById(R.id.menu_button);
        LinearLayout mainLayout = findViewById(R.id.main_layout);

        // Determine colors based on the current theme
        int backgroundColor = isNightMode ? getResources().getColor(R.color.my_dark_background) : getResources().getColor(R.color.my_light_background);
        int primaryColor = isNightMode ? getResources().getColor(R.color.my_dark_primary) : getResources().getColor(R.color.my_light_primary);
        int textColor = isNightMode ? getResources().getColor(R.color.my_dark_on_primary) : getResources().getColor(R.color.my_light_on_secondary);
        int buttonTextColor = isNightMode ? getResources().getColor(R.color.my_dark_on_primary) : getResources().getColor(R.color.my_light_on_primary);

        // Apply colors to UI elements
        toolbar.setBackgroundColor(primaryColor);
        menuButton.setTextColor(buttonTextColor);
        mainLayout.setBackgroundColor(backgroundColor);

        // Update colors of all buttons
        btnLogin.setTextColor(buttonTextColor);
        btnRegister.setTextColor(buttonTextColor);
        btnThemeSwitch.setTextColor(buttonTextColor);
        btnAddVideo.setTextColor(buttonTextColor);
        btnLogout.setTextColor(buttonTextColor);

        // Set background for buttons based on the theme
        int buttonBackground = isNightMode ? R.drawable.button_background : R.drawable.button_rounded_light;
        btnLogin.setBackgroundResource(buttonBackground);
        btnRegister.setBackgroundResource(buttonBackground);
        btnThemeSwitch.setBackgroundResource(buttonBackground);
        btnAddVideo.setBackgroundResource(buttonBackground);
        btnLogout.setBackgroundResource(buttonBackground);

        // Refresh RecyclerView to apply theme colors to its items
        if (videoAdapter != null) {
            videoAdapter.notifyDataSetChanged();
        }

        // Redraw the main layout to ensure all changes are visible
        mainLayout.invalidate();
    }

    private void switchTheme() {
        isNightMode = !isNightMode;
        sharedPreferences.edit().putBoolean("isNightMode", isNightMode).apply();
        viewModel.clearFilteredList(); //try-behave
        applyTheme();
        recreate();
    }

    private void saveUserAddedVideos() {
        viewModel.saveUserAddedVideos(sharedPreferences); //try-behave
    }

    private void restoreUserAddedVideos() {
        viewModel.restoreUserAddedVideos(sharedPreferences); //try-behave
        reinitializeAdapter();
    }

    private void reinitializeAdapter() {
        videoAdapter = new VideoAdapter(viewModel.getVideos().getValue(), sharedPreferences); //try-behave
        recyclerView.setAdapter(videoAdapter);
        videoAdapter.notifyDataSetChanged();
    }

    public void refreshVideoList() {
        viewModel.refreshVideos(); //try-server
//        viewModel.loadVideos(); //try-behave
//        if (swipeRefreshLayout != null) {
//            swipeRefreshLayout.setRefreshing(false);
//        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("video_list", new ArrayList<>(viewModel.getVideos().getValue())); //try-behave
        outState.putParcelable("recycler_state", recyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<Video> videoList = savedInstanceState.getParcelableArrayList("video_list");
            if (videoList != null) {
                viewModel.setVideoList(videoList); //try-behave
            }
            recyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable("recycler_state"));
        }
        restoreUserAddedVideos();
    }

    private void showAddVideoDialog(String videoFilePath) {
        AddVideoDialog dialog = new AddVideoDialog();
        dialog.setAddVideoListener((title, category, previewImageUrl) -> {
            String id = "new_" + System.currentTimeMillis();
            String author = loggedInUser != null ? loggedInUser : "guest";
            String uploadTime = "Just now";
            int views = 0;
            int likes = 0;

            String authorProfilePicUrl = Users.getInstance().getUser(loggedInUser).getProfilePic();

            Video video = new Video(id, title, author, views, uploadTime, previewImageUrl, authorProfilePicUrl, videoFilePath, category, likes);
            viewModel.addVideo(video); //try-behave
            sharedPreferences.edit().putString(id + "_videoPath", videoFilePath).apply();
            refreshVideoList(); //try-behave
        });
        dialog.show(getSupportFragmentManager(), "AddVideoDialog");
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

//    private void loadVideoData() {
//        viewModel.loadVideoData(this, sharedPreferences); //try-behave
//    }

    private int getUpdatedViews(String videoId, int defaultViews) {
        return sharedPreferences.getInt(videoId + "_views", defaultViews);
    }

    private int getUpdatedLikes(String videoId, int defaultLikes) {
        return sharedPreferences.getInt(videoId + "_likes", defaultLikes);
    }

    private void filterVideos(String query) {
        viewModel.filterVideos(query); //try-behave
    }

    private void filterVideosByCategory(String category) {
        viewModel.filterVideosByCategory(category); //try-behave
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_PICK && resultCode == RESULT_OK && data != null) {
            selectedVideoUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedVideoUri);
                File videoFile = new File(getFilesDir(), "video_" + System.currentTimeMillis() + ".mp4");
                FileOutputStream outputStream = new FileOutputStream(videoFile);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                inputStream.close();
                outputStream.close();

                String videoFilePath = videoFile.getAbsolutePath();
                showAddVideoDialog(videoFilePath);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to save video", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                String videoId = data.getStringExtra("VIDEO_ID");
                int updatedViews = data.getIntExtra("UPDATED_VIEWS", 0);
                viewModel.updateVideoViews(videoId, updatedViews); //try-behave
            }
        }
    }

    private void showLoginPromptDialog() { //try90
        LoginPromptDialog dialog = new LoginPromptDialog(); //try90
        dialog.show(getSupportFragmentManager(), "LoginPromptDialog"); //try90
    } //try90

    private class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
        private List<Video> videoList;
        private SharedPreferences sharedPreferences; //try90

        public VideoAdapter(List<Video> videoList, SharedPreferences sharedPreferences) {
            this.videoList = videoList != null ? videoList : new ArrayList<>();
            this.sharedPreferences = sharedPreferences;
            Log.d("VideoAdapter", "Number of videos passed to adapter: " + (this.videoList != null ? this.videoList.size() : "null"));
        }

        public void updateVideos(List<Video> newVideos) {
            if (this.videoList == null) {
                this.videoList = new ArrayList<>();
            }
            this.videoList.clear();
            if (newVideos != null) {
                this.videoList.addAll(newVideos);
            }
            notifyDataSetChanged();
            Log.d("VideoAdapter", "Videos updated, new size: " + (newVideos != null ? newVideos.size() : 0));
        }

        //        public void updateVideos(List<Video> newVideos) {
//            this.videoList = newVideos;
//            notifyDataSetChanged();
//        }
//        public void updateVideos(List<Video> newVideos) {
//            this.videoList.clear();
//            this.videoList.addAll(newVideos);
//            notifyDataSetChanged();
//            Log.d("VideoAdapter", "Videos updated, new size: " + newVideos.size()); //try-behave
//        }

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

                // Apply custom styles to menu items
                for (int i = 0; i < popupMenu.getMenu().size(); i++) {
                    MenuItem menuItem = popupMenu.getMenu().getItem(i);
                    SpannableString spannableTitle = new SpannableString(menuItem.getTitle());
                    spannableTitle.setSpan(new ForegroundColorSpan(Color.parseColor("#D31E1E")), 0, spannableTitle.length(), 0);
                    menuItem.setTitle(spannableTitle);
                }

                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.edit_video) {
                        if (sharedPreferences.getBoolean(LOGGED_IN_KEY, false)) {
                            EditVideoDialog dialog = EditVideoDialog.newInstance(video.getId());
                            dialog.setOnDismissListener(dialogInterface -> notifyDataSetChanged());
                            dialog.show(((AppCompatActivity) holder.itemView.getContext()).getSupportFragmentManager(), "EditVideoDialog");
                        } else {
                            showLoginPromptDialog();
                        }
                        return true;
                    } else if (item.getItemId() == R.id.delete_video) {
                        if (sharedPreferences.getBoolean(LOGGED_IN_KEY, false)) {
                            viewModel.removeVideo(video.getId()); //try-behave
                        } else {
                            showLoginPromptDialog();
                        }
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
            //return videoList.size();
            return videoList != null ? videoList.size() : 0;
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
                authorProfilePic.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, ChannelActivity.class);
                    intent.putExtra("AUTHOR_NAME", video.getAuthor());
                    startActivity(intent);
                });

            }

            //            private void loadImageView(ImageView imageView, String imageUrl) {
//                if (imageUrl.startsWith("drawable/")) {
//                    // Handle drawable resources
//                    int imageResId = getResources().getIdentifier(imageUrl, null, getPackageName());
//                    if (imageResId != 0) {
//                        imageView.setImageResource(imageResId);
//                    } else {
//                        imageView.setImageResource(R.drawable.policy); // Fallback to error image if resource not found
//                    }
//                } else if (imageUrl != null) {
//                    // Handle remote images or local file URIs
//                    Picasso.get().load(imageUrl).error(R.drawable.policy).into(imageView);
//                } else {
//                    // Fallback for unexpected image URL formats
//                    imageView.setImageResource(R.drawable.policy);
//                }
//            }
            private void loadImageView(ImageView imageView, String imageUrl) {
                if (imageUrl == null || imageUrl.isEmpty()) {
                    imageView.setImageResource(R.drawable.policy);
                } else if (imageUrl.startsWith("drawable/")) {
                    // Handle drawable resources
                    int imageResId = getResources().getIdentifier(imageUrl, null, getPackageName());
                    if (imageResId != 0) {
                        imageView.setImageResource(imageResId);
                    } else {
                        imageView.setImageResource(R.drawable.policy);
                    }
                } else {
                    // Handle remote images
                    String fullUrl = "http://10.0.2.2:12345" + imageUrl; // Adjust the base URL as needed
                    Picasso.get().load(fullUrl).error(R.drawable.policy).into(imageView);
                }
            }

        }
    }
}
