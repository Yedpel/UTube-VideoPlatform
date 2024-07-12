package com.example.utube.activities;
//before likes

import static com.example.utube.activities.MainActivity.LOGGED_IN_USER;
import static com.example.utube.activities.MainActivity.PREFS_NAME;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.view.WindowManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utube.MyApplication;
import com.example.utube.R;
import com.example.utube.data.CommentRepository;
import com.example.utube.models.CommentEntity;
import com.example.utube.models.Users;
import com.example.utube.models.Video;
import com.example.utube.viewmodels.VideoDetailViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VideoDetailActivity extends AppCompatActivity {

    private boolean isFullScreen = false;
    private Button btnFullScreen;
    private VideoView videoView;
    private TextView titleTextView, authorTextView, viewsTextView, uploadTimeTextView, likesTextView, commentsCountTextView;
    private ImageView authorProfilePic;
    private RecyclerView commentsRecyclerView;
    private boolean isLiked = false;
    private String videoId;
    private int likes;
    private int views;
    private List<Video.Comment> comments;
    private CommentsAdapter commentsAdapter;

    // Static HashMaps to keep track of comments and likes state for each video within the session
    private static HashMap<String, List<Video.Comment>> commentsMap = new HashMap<>();
    private static HashMap<String, HashMap<String, Boolean>> likedCommentsStateMap = new HashMap<>();
    private int idCounter = 0;
    private CommentRepository commentRepository;
    private SharedPreferences sharedPreferences;
    public static final String PREFS_NAME = "theme_prefs";
    private VideoDetailViewModel viewModel; //mvvm-change


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load theme from shared preferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isNightMode = sharedPreferences.getBoolean("isNightMode", false);
        setTheme(isNightMode ? R.style.AppTheme_Dark : R.style.AppTheme_Light);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        videoView = findViewById(R.id.video_view);
        titleTextView = findViewById(R.id.video_title);
        authorTextView = findViewById(R.id.video_author);
        viewsTextView = findViewById(R.id.video_views);
        uploadTimeTextView = findViewById(R.id.video_upload_time);
        authorProfilePic = findViewById(R.id.author_profile_pic);
        likesTextView = findViewById(R.id.likes_count);
        commentsCountTextView = findViewById(R.id.comments_count);
        commentsRecyclerView = findViewById(R.id.comments_recycler_view);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnFullScreen = findViewById(R.id.btn_full_screen);

        viewModel = new ViewModelProvider(this).get(VideoDetailViewModel.class); //mvvm-change

        commentsAdapter = new CommentsAdapter(new ArrayList<>());
        commentsRecyclerView.setAdapter(commentsAdapter);

        // Initialize media controller
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
        // likes = getIntent().getIntExtra("LIKES", 0);

        // Increment the views count
        views++;
        // viewsTextView.setText(views + " views");

        // Load likes state from memory
        isLiked = VideoManager.getInstance(getApplication()).getLikedStateMap().getOrDefault(videoId, false);
        // likes = VideoManager.getInstance(getApplication()).getLikesCountMap().getOrDefault(videoId, likes);


        //  likesTextView.setText(likes + " likes");

        // MVVM changes
        viewModel.loadVideo(videoId); //mvvm-change
        viewModel.loadComments(videoId); //mvvm-change
        viewModel.incrementViews(); //mvvm-change

        // Observe only the changing parts of the video data
        viewModel.getVideo().observe(this, video -> { //mvvm-change
            if (video != null) {
                viewsTextView.setText(video.getViews() + " views");
                likesTextView.setText(video.getLikes() + " likes");
                titleTextView.setText(video.getTitle());
                authorTextView.setText(video.getAuthor());
            }
        }); //mvvm-change

        // Log the URL for debugging
        Log.d("VideoDetailActivity", "Video URL: " + videoUrl);

        // Set video details
        // Log and handle video file path or URL
        if (videoUrl != null && !videoUrl.isEmpty()) {
            //  Toast.makeText(this, "the video url isn't null", Toast.LENGTH_SHORT).show(); //try22
            if (videoUrl.startsWith("content://") || videoUrl.startsWith("file://")) {
                try {
                    // Verify the URI is accessible by querying it
                    InputStream inputStream = getContentResolver().openInputStream(Uri.parse(videoUrl)); //try22
                    if (inputStream != null) {
                        inputStream.close(); //try22
                        videoView.setVideoURI(Uri.parse(videoUrl)); //try22
                    } else {
                        throw new Exception("Input stream is null"); //try22
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Can't play this video. Error accessing URI: " + e.getMessage(), Toast.LENGTH_SHORT).show(); //try22
                    Log.e("VideoDetailActivity", "Error accessing URI: " + e.getMessage(), e); //try22
                    return;
                }
            } else if (videoUrl.startsWith("http")) {
                videoView.setVideoURI(Uri.parse(videoUrl)); //try22
            } else if (videoUrl.startsWith("raw/") || videoUrl.startsWith("drawable/")) {
                int videoResId = getResources().getIdentifier(videoUrl, "raw", getPackageName());
                if (videoResId == 0) {
                    videoResId = getResources().getIdentifier(videoUrl, "drawable", getPackageName());
                }
                if (videoResId != 0) {
                    videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + videoResId));
                } else {
                    Toast.makeText(this, "Can't play this video. Resource not found.", Toast.LENGTH_SHORT).show();
                    Log.e("VideoDetailActivity", "Error: Resource not found");
                    return;
                }
            } else {
                File videoFile = new File(videoUrl);
                if (videoFile.exists()) {
                    videoView.setVideoPath(videoFile.getAbsolutePath());
                } else {
                    Toast.makeText(this, "Can't play this video. File not found.", Toast.LENGTH_SHORT).show(); //try22
                    Log.e("VideoDetailActivity", "Error: video file not found");
                    return;
                }
            }
            videoView.setOnPreparedListener(mp -> mp.setOnVideoSizeChangedListener((mp1, width, height) -> { // Ensure video is prepared
                if (width == 0 || height == 0) {
                    Toast.makeText(this, "Can't play this video. Invalid video resolution.", Toast.LENGTH_SHORT).show(); //try22
                }
            }));
            videoView.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(VideoDetailActivity.this, "Can't play this video. Error code: " + what, Toast.LENGTH_SHORT).show(); //try22
                return true;
            });
            videoView.start();
        } else {
            Log.e("VideoDetailActivity", "Error: videoUrl is null or empty");
            Toast.makeText(this, "Can't play this video", Toast.LENGTH_SHORT).show();
        }

        titleTextView.setText(title);
        authorTextView.setText(author);
        uploadTimeTextView.setText(uploadTime);

        // Load author's profile picture with Picasso, set placeholder and error image
        authorProfilePicUrl = getIntent().getStringExtra("AUTHOR_PROFILE_PIC_URL"); // Make sure this is correctly passed in the intent

        // Attempt to load the image from a URL first if it's not null
        if (authorProfilePicUrl != null && !authorProfilePicUrl.startsWith("drawable/")) {
            // URL is not a drawable resource, attempt to load it with Picasso
            Picasso.get()
                    .load(authorProfilePicUrl)
                    .placeholder(R.drawable.policy) // Use a placeholder image while loading
                    .error(R.drawable.policy) // Fallback to error image if loading fails
                    .into(authorProfilePic, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("Picasso", "Image loaded successfully");
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("Picasso", "Error loading image", e);
                            authorProfilePic.setImageResource(R.drawable.policy); // Set error image directly in case of failure
                        }
                    });
        } else {
            // Handle drawable resources or null URLs
            int authorProfilePicResId = getResources().getIdentifier(authorProfilePicUrl, "drawable", getPackageName());
            if (authorProfilePicResId != 0) {
                authorProfilePic.setImageResource(authorProfilePicResId);
            } else {
                // If resource ID is not found or URL is null, set to default error image
                authorProfilePic.setImageResource(R.drawable.policy);
            }
        }


        // Set initial like button state
        updateLikeButton();

        commentRepository = ((MyApplication) getApplicationContext()).getCommentRepository();

        viewModel.getComments().observe(this, commentsList -> { //mvvm-change
            if (commentsList != null) {
                //  comments = commentsList;
                comments = new ArrayList<>(commentsList); //create a new list to avoid modifying the original list
                commentsAdapter.updateComments(comments);
                updateCommentsCount();
            }
        }); //mvvm-change

        // Add comment button click listener
        String finalAuthorProfilePicUrl = authorProfilePicUrl;
        findViewById(R.id.add_comment_button).setOnClickListener(v -> {
            if (getSharedPreferences("theme_prefs", MODE_PRIVATE).getBoolean("logged_in", false)) {
                AddCommentDialog dialog = new AddCommentDialog();
                dialog.setAddCommentListener(text -> {
                    if (!text.trim().isEmpty()) {
                        String currentLoggedInUser = sharedPreferences.getString(LOGGED_IN_USER, "");
                        String profilePicUrl = Users.getInstance().getUser(currentLoggedInUser).getProfilePic();
                        viewModel.addComment(videoId, currentLoggedInUser, text, profilePicUrl); //mvvm-change
                    }
                });
                dialog.show(getSupportFragmentManager(), "AddCommentDialog");
            } else {
                showLoginPromptDialog();
            }
        });
        /*
        findViewById(R.id.add_comment_button).setOnClickListener(v -> { //try6
            if (getSharedPreferences("theme_prefs", MODE_PRIVATE).getBoolean("logged_in", false)) { //try6
                AddCommentDialog dialog = new AddCommentDialog(); //try6
                dialog.setAddCommentListener(text -> { //try6
                    if (!text.trim().isEmpty()) { //try6
                        String currentTime = "Just now"; // Use a proper timestamp in real app //try6
                        int random = (int) (Math.random() * 1000000); //try6
                        //  String authorProfilePicUrl = Users.getInstance().getUser(loggedInUser).getProfilePic();
                        Video.Comment comment = new Video.Comment(random, "user1", text, currentTime, 0, finalAuthorProfilePicUrl); //try6
                        comments.add(comment); //try6
                        commentsAdapter.notifyDataSetChanged(); //try6
                        updateCommentsCount(); //try6
                        commentsMap.put(videoId, comments); //try6
                    } //try6
                }); //try6
                dialog.show(getSupportFragmentManager(), "AddCommentDialog"); //try6
            } else { //try6
                showLoginPromptDialog(); //try6
            } //try6
        }); //try6 */

        // Share button click listener
        findViewById(R.id.share_button).setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, videoUrl);
            startActivity(Intent.createChooser(shareIntent, "Share video via"));
        });

        // Update comments count
        // updateCommentsCount();

        btnFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen) {
                    exitFullScreen();
                } else {
                    enterFullScreen();
                }
            }
        });
    }

    private Video.Comment convertToVideoComment(CommentEntity entity) {
        return new Video.Comment(entity.getId(), entity.getUsername(), entity.getText(),
                entity.getUploadTime(), entity.getLikes(), entity.getProfilePicUrl());
    }

    private void enterFullScreen() {
        isFullScreen = true;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        btnFullScreen.setText("Exit Full Screen");

        // Set VideoView to full screen
        ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        videoView.setLayoutParams(layoutParams);

        // Hide other UI elements
        titleTextView.setVisibility(View.GONE);
        authorTextView.setVisibility(View.GONE);
        viewsTextView.setVisibility(View.GONE);
        uploadTimeTextView.setVisibility(View.GONE);
        authorProfilePic.setVisibility(View.GONE);
        likesTextView.setVisibility(View.GONE);
        commentsCountTextView.setVisibility(View.GONE);
        commentsRecyclerView.setVisibility(View.GONE);
        findViewById(R.id.add_comment_button).setVisibility(View.GONE);
        findViewById(R.id.share_button).setVisibility(View.GONE);
        findViewById(R.id.like_button).setVisibility(View.GONE);
        findViewById(R.id.comments_headline).setVisibility(View.GONE);
        //make toast to exit full screen scroll down to see the button
        Toast.makeText(this, "To exit full screen, scroll down and press the Exit Full Screen button", Toast.LENGTH_LONG).show();
    }

    private void exitFullScreen() {
        isFullScreen = false;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }
        btnFullScreen.setText("Full Screen");

        // Reset VideoView layout parameters
        ViewGroup.LayoutParams params = videoView.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        videoView.setLayoutParams(params);

        // Show other UI elements
        titleTextView.setVisibility(View.VISIBLE);
        authorTextView.setVisibility(View.VISIBLE);
        viewsTextView.setVisibility(View.VISIBLE);
        uploadTimeTextView.setVisibility(View.VISIBLE);
        authorProfilePic.setVisibility(View.VISIBLE);
        likesTextView.setVisibility(View.VISIBLE);
        commentsCountTextView.setVisibility(View.VISIBLE);
        commentsRecyclerView.setVisibility(View.VISIBLE);
        findViewById(R.id.add_comment_button).setVisibility(View.VISIBLE);
        findViewById(R.id.share_button).setVisibility(View.VISIBLE);
        findViewById(R.id.like_button).setVisibility(View.VISIBLE);
        findViewById(R.id.comments_headline).setVisibility(View.VISIBLE);
    }

    private void updateLikeButton() {
        String currentLoggedInUser = sharedPreferences.getString(LOGGED_IN_USER, "");
        isLiked = viewModel.isVideoLiked(videoId, currentLoggedInUser); //mvvm-change

        viewModel.getIsLiked().observe(this, liked -> { //mvvm-change
            isLiked = liked;
            ((TextView) findViewById(R.id.like_button)).setText(isLiked ? "Unlike" : "Like");
        }); //mvvm-change

        findViewById(R.id.like_button).setOnClickListener(v -> {
            if (sharedPreferences.getBoolean("logged_in", false)) {
                boolean newLikeStatus = !isLiked;
                viewModel.updateLikeStatus(videoId, currentLoggedInUser, newLikeStatus); //mvvm-change
            } else {
                showLoginPromptDialog();
            }
        });
        ((TextView) findViewById(R.id.like_button)).setText(isLiked ? "Unlike" : "Like");
    }

/*    private void updateLikeButton() { //try5
        findViewById(R.id.like_button).setOnClickListener(v -> { //try5
            if (getSharedPreferences("theme_prefs", MODE_PRIVATE).getBoolean("logged_in", false)) { //try5
                isLiked = !isLiked; //try5
                if (isLiked) { //try5
                    likes++; //try5
                } else { //try5
                    likes--; //try5
                } //try5
                likesTextView.setText(likes + " likes"); //try5
                VideoManager.getInstance(getApplication()).getLikedStateMap().put(videoId, isLiked); //try5
                VideoManager.getInstance(getApplication()).getLikesCountMap().put(videoId, likes); //try5
                ((TextView) findViewById(R.id.like_button)).setText(isLiked ? "Unlike" : "Like"); //try5
            } else { //try5
                showLoginPromptDialog(); //try5
            } //try5
        }); //try5
        ((TextView) findViewById(R.id.like_button)).setText(isLiked ? "Unlike" : "Like"); //try5
    } //try5 */


    private void updateCommentsCount() {
        commentsCountTextView.setText("(" + comments.size() + ")");
    }

    //    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent();
//        intent.putExtra("VIDEO_ID", videoId);
//        intent.putExtra("UPDATED_VIEWS", views);
//        setResult(RESULT_OK, intent);
//        super.onBackPressed();
//    }
    @Override
    public void onBackPressed() {
        Video video = VideoManager.getInstance(getApplication()).getVideoById(videoId); //try-behave
        if (video != null) { //try-behave
            video.setViews(views); //try-behave
            VideoManager.getInstance(getApplication()).updateVideo(video); //try-behave
        } //try-behave
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    private void showLoginPromptDialog() { //try5
        LoginPromptDialog dialog = new LoginPromptDialog(); //try5
        dialog.show(getSupportFragmentManager(), "LoginPromptDialog"); //try5
    } //try5


    private class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {
        private List<Video.Comment> commentList;

        public CommentsAdapter(List<Video.Comment> commentList) {
            this.commentList = commentList;
        }

        public void updateComments(List<Video.Comment> newComments) {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CommentDiffCallback(this.commentList, newComments));
            this.commentList = new ArrayList<>(newComments);
            diffResult.dispatchUpdatesTo(this);
            Log.d("VideoDetailActivity", "Updated comments list. Size: " + commentList.size());
            for (Video.Comment comment : commentList) {
                Log.d("VideoDetailActivity", "Comment in list: ID=" + comment.getId() + ", Text=" + comment.getText());
            }
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, int position) {
            Video.Comment comment = commentList.get(position);
            holder.bind(comment);
        }
        public int findCommentPosition(int commentId) {
            for (int i = 0; i < commentList.size(); i++) {
                if (commentList.get(i).getId() == commentId) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }

        class CommentViewHolder extends RecyclerView.ViewHolder {
            TextView usernameTextView, commentTextView, uploadTimeTextView, commentLikesTextView;
            ImageView profilePicImageView;
            Button likeCommentButton, editCommentButton, deleteCommentButton;
            private boolean isCommentLiked = false;

            public CommentViewHolder(View itemView) {
                super(itemView);
                usernameTextView = itemView.findViewById(R.id.comment_username);
                commentTextView = itemView.findViewById(R.id.comment_text);
                uploadTimeTextView = itemView.findViewById(R.id.comment_upload_time);
                commentLikesTextView = itemView.findViewById(R.id.comment_likes_count);
                profilePicImageView = itemView.findViewById(R.id.comment_profile_pic);
                likeCommentButton = itemView.findViewById(R.id.like_comment_button);
                editCommentButton = itemView.findViewById(R.id.edit_comment_button);
                deleteCommentButton = itemView.findViewById(R.id.delete_comment_button);
            }

            public void bind(Video.Comment comment) {
                usernameTextView.setText(comment.getUsername());
                commentTextView.setText(comment.getText());
                uploadTimeTextView.setText(comment.getUploadTime());
                commentLikesTextView.setText(comment.getLikes() + " likes");

                loadImageView(profilePicImageView, comment.getProfilePicUrl());

                String currentLoggedInUser = sharedPreferences.getString(LOGGED_IN_USER, "");
                // String commentLikeKey = videoId + "_" + comment.getId() + "_" + currentLoggedInUser + "_liked";
                //isCommentLiked = sharedPreferences.getBoolean(commentLikeKey, false);
                isCommentLiked = viewModel.isCommentLiked(videoId, comment.getId(), currentLoggedInUser);


                updateLikeCommentButton();

//                likeCommentButton.setOnClickListener(v -> {
//                    if (sharedPreferences.getBoolean("logged_in", false)) {
//                        boolean newLikeStatus = !isCommentLiked;
//                        viewModel.updateCommentLikeStatus(videoId, comment, newLikeStatus);
//                    } else {
//                        showLoginPromptDialog();
//                    }
//                });
                likeCommentButton.setOnClickListener(v -> {
                    if (sharedPreferences.getBoolean("logged_in", false)) {
                        isCommentLiked = !isCommentLiked;
                        viewModel.updateCommentLikeStatus(videoId, comment.getId(), currentLoggedInUser, isCommentLiked);
                        updateLikeCommentButton();
                    } else {
                        showLoginPromptDialog();
                    }
                });

                editCommentButton.setOnClickListener(v -> {
                    if (getSharedPreferences("theme_prefs", MODE_PRIVATE).getBoolean("logged_in", false)) {
                        AddCommentDialog dialog = AddCommentDialog.newInstance(comment.getText());
                        dialog.setAddCommentListener(text -> {
                            if (!text.trim().isEmpty()) {
                                Log.d("VideoDetailActivity", "Editing comment: " + comment.getId() + ", New text: " + text);
                                CommentEntity updatedComment = new CommentEntity(videoId, comment.getUsername(), text, comment.getUploadTime(), comment.getLikes(), comment.getProfilePicUrl());
                                updatedComment.setId(comment.getId());
                                viewModel.updateComment(updatedComment);
                            }
                        });
                        dialog.show(getSupportFragmentManager(), "EditCommentDialog");
                    } else {
                        showLoginPromptDialog();
                    }
                });
//                editCommentButton.setOnClickListener(v -> {
//                    if (getSharedPreferences("theme_prefs", MODE_PRIVATE).getBoolean("logged_in", false)) {
//                        AddCommentDialog dialog = AddCommentDialog.newInstance(comment.getText());
//                        dialog.setAddCommentListener(text -> {
//                            if (!text.trim().isEmpty()) {
//                                comment.setText(text);
//                                CommentEntity commentEntity = convertToCommentEntity(comment);
//                                viewModel.updateComment(commentEntity);
//                                notifyDataSetChanged(); // Add this line to refresh the RecyclerView
//                            }
//                        });
//                        dialog.show(getSupportFragmentManager(), "EditCommentDialog");
//                    } else {
//                        showLoginPromptDialog();
//                    }
//                });
//                editCommentButton.setOnClickListener(v -> {
//                    if (getSharedPreferences("theme_prefs", MODE_PRIVATE).getBoolean("logged_in", false)) {
//                        AddCommentDialog dialog = new AddCommentDialog();
//                        dialog.setAddCommentListener(text -> {
//                            if (!text.trim().isEmpty()) {
//                                comment.setText(text);
//                                CommentEntity commentEntity = convertToCommentEntity(comment);
//                                viewModel.updateComment(commentEntity); //mvvm-change
//                            }
//                        });
//                        dialog.show(getSupportFragmentManager(), "EditCommentDialog");
//                    } else {
//                        showLoginPromptDialog();
//                    }
//                });

                deleteCommentButton.setOnClickListener(v -> {
                    if (getSharedPreferences("theme_prefs", MODE_PRIVATE).getBoolean("logged_in", false)) {
                        CommentEntity commentEntity = convertToCommentEntity(comment);
                        viewModel.deleteComment(commentEntity); //mvvm-change
                    } else {
                        showLoginPromptDialog();
                    }
                });
               /* editCommentButton.setOnClickListener(v -> { //try7
                    if (getSharedPreferences("theme_prefs", MODE_PRIVATE).getBoolean("logged_in", false)) { //try7
                        AddCommentDialog dialog = new AddCommentDialog(); //try7
                        dialog.setAddCommentListener(text -> { //try7
                            if (!text.trim().isEmpty()) { //try7
                                comment.setText(text); //try7
                                commentTextView.setText(text); //try7
                            } //try7
                        }); //try7
                        dialog.show(getSupportFragmentManager(), "EditCommentDialog"); //try7
                    } else { //try7
                        showLoginPromptDialog(); //try7
                    } //try7
                }); //try7

                deleteCommentButton.setOnClickListener(v -> { //try7
                    if (getSharedPreferences("theme_prefs", MODE_PRIVATE).getBoolean("logged_in", false)) { //try7
                        int position = getAdapterPosition(); //try7
                        if (position != RecyclerView.NO_POSITION) { //try7
                            comments.remove(position); //try7
                            notifyItemRemoved(position); //try7
                            updateCommentsCount(); //try7
                        } //try7
                    } else { //try7
                        showLoginPromptDialog(); //try7
                    } //try7
                }); //try7 */
            }

            private CommentEntity convertToCommentEntity(Video.Comment comment) {
                CommentEntity entity = new CommentEntity(videoId, comment.getUsername(), comment.getText(),
                        comment.getUploadTime(), comment.getLikes(),
                        comment.getProfilePicUrl());
                entity.setId(comment.getId());
                return entity;
            }


            private void loadImageView(ImageView imageView, String imageUrl) {
                if (imageUrl != null && imageUrl.startsWith("drawable/")) {
                    // Handle drawable resources
                    int imageResId = getResources().getIdentifier(imageUrl, null, getPackageName());
                    if (imageResId != 0) {
                        imageView.setImageResource(imageResId);
                    } else {
                        imageView.setImageResource(R.drawable.policy); // Fallback to policy image if resource not found
                    }
                } else if (imageUrl != null) {
                    // Handle remote images or local file URIs
                    Picasso.get().load(imageUrl)
                            .error(R.drawable.policy) // Use a policy image as the error fallback
                            .into(imageView);
                } else {
                    // Fallback for null or unexpected image URL formats
                    imageView.setImageResource(R.drawable.policy);
                }
            }

            //            private void updateLikeCommentButton() {
//                if (isCommentLiked) {
//                    likeCommentButton.setText("Unlike");
//                } else {
//                    likeCommentButton.setText("Like");
//                }
//            }
            private void updateLikeCommentButton() {
                likeCommentButton.setText(isCommentLiked ? "Unlike" : "Like");
            }
        }

        private class CommentDiffCallback extends DiffUtil.Callback { //try-edit-fix
            private final List<Video.Comment> oldList;
            private final List<Video.Comment> newList;

            public CommentDiffCallback(List<Video.Comment> oldList, List<Video.Comment> newList) {
                this.oldList = oldList;
                this.newList = newList;
            }

            @Override
            public int getOldListSize() { return oldList.size(); }

            @Override
            public int getNewListSize() { return newList.size(); }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Video.Comment oldComment = oldList.get(oldItemPosition);
                Video.Comment newComment = newList.get(newItemPosition);
                return oldComment.getText().equals(newComment.getText());
            }
        }

    }
}
