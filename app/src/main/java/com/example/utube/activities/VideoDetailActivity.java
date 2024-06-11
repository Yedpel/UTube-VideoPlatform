package com.example.utube.activities;

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
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utube.R;
import com.example.utube.models.Video;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VideoDetailActivity extends AppCompatActivity {

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
        commentsCountTextView = findViewById(R.id.comments_count);
        commentsRecyclerView = findViewById(R.id.comments_recycler_view);

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
        likes = getIntent().getIntExtra("LIKES", 0);

        // Increment the views count
        views++;
        viewsTextView.setText(views + " views");

        // Load likes state from memory
        isLiked = VideoManager.getInstance().getLikedStateMap().getOrDefault(videoId, false);
        likes = VideoManager.getInstance().getLikesCountMap().getOrDefault(videoId, likes);
        likesTextView.setText(likes + " likes");

        // Log the URL for debugging
        Log.d("VideoDetailActivity", "Video URL: " + videoUrl);

        // Set video details
        if (videoUrl != null && !videoUrl.isEmpty()) { //try1
            if (videoUrl.startsWith("content://") || videoUrl.startsWith("file://")) { //try1
                videoView.setVideoURI(Uri.parse(videoUrl)); //try1
            } else if (videoUrl.startsWith("http")) { //try1
                videoView.setVideoURI(Uri.parse(videoUrl)); //try1
            } else { //try1
                int videoResId = getResources().getIdentifier(videoUrl, "raw", getPackageName()); //try1
                videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + videoResId)); //try1
            } //try1
            videoView.start(); //try1
        } else { //try1
            Log.e("VideoDetailActivity", "Error: videoUrl is null or empty"); //try1
            // Handle the case where the video URL is invalid //try1
        } //try1


        titleTextView.setText(title);
        authorTextView.setText(author);
        uploadTimeTextView.setText(uploadTime);

        // Load author's profile picture with Picasso, set placeholder and error image
        int authorProfilePicResId = getResources().getIdentifier(authorProfilePicUrl, "drawable", getPackageName());
        if (authorProfilePicResId != 0) {
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
        } else {
            Picasso.get()
                    .load(authorProfilePicUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
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
        }

        // Set initial like button state
        updateLikeButton();

        // Initialize comments section
        comments = commentsMap.getOrDefault(videoId, new ArrayList<>());
        commentsAdapter = new CommentsAdapter(comments);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentsAdapter);

        // Add comment button click listener
        findViewById(R.id.add_comment_button).setOnClickListener(v -> {
            AddCommentDialog dialog = new AddCommentDialog();
            dialog.setAddCommentListener(text -> {
                if (!text.trim().isEmpty()) {
                    String currentTime = "Just now"; // Use a proper timestamp in real app
                    int random = (int)(Math.random()*1000000);
                    Video.Comment comment = new Video.Comment(random, "user1", text, currentTime, 0, "drawable/error_image.webp");
                    comments.add(comment);
                    commentsAdapter.notifyDataSetChanged();
                    updateCommentsCount();
                    commentsMap.put(videoId, comments);
                }
            });
            dialog.show(getSupportFragmentManager(), "AddCommentDialog");
        });

        // Share button click listener
        findViewById(R.id.share_button).setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, videoUrl);
            startActivity(Intent.createChooser(shareIntent, "Share video via"));
        });

        // Update comments count
        updateCommentsCount();
    }

    private void updateLikeButton() { //try5
        findViewById(R.id.like_button).setOnClickListener(v -> { //try5
            if (getSharedPreferences("theme_prefs", MODE_PRIVATE).getBoolean("logged_in", false)) { //try5
                isLiked = !isLiked; //try5
                if (isLiked) { //try5
                    likes++; //try5
                } else { //try5
                    likes--; //try5
                } //try5
                likesTextView.setText(likes + " likes"); //try5
                VideoManager.getInstance().getLikedStateMap().put(videoId, isLiked); //try5
                VideoManager.getInstance().getLikesCountMap().put(videoId, likes); //try5
                ((TextView) findViewById(R.id.like_button)).setText(isLiked ? "Unlike" : "Like"); //try5
            } else { //try5
                showLoginPromptDialog(); //try5
            } //try5
        }); //try5
        ((TextView) findViewById(R.id.like_button)).setText(isLiked ? "Unlike" : "Like"); //try5
    } //try5


    private void updateCommentsCount() {
        commentsCountTextView.setText("(" + comments.size() + ")");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("VIDEO_ID", videoId);
        intent.putExtra("UPDATED_VIEWS", views);
        setResult(RESULT_OK, intent);
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

                int profilePicResId = getResources().getIdentifier(comment.getProfilePicUrl(), "drawable", getPackageName());
                if (profilePicResId != 0) {
                    profilePicImageView.setImageResource(profilePicResId);
                } else {
                    Picasso.get().load(comment.getProfilePicUrl()).into(profilePicImageView);
                }

                // Load comment like state
                isCommentLiked = likedCommentsStateMap
                        .getOrDefault(videoId, new HashMap<>())
                        .getOrDefault(comment.getText(), false);

                updateLikeCommentButton();

                likeCommentButton.setOnClickListener(v -> {
                    isCommentLiked = !isCommentLiked;
                    if (isCommentLiked) {
                        comment.setLikes(comment.getLikes() + 1);
                    } else {
                        comment.setLikes(comment.getLikes() - 1);
                    }
                    commentLikesTextView.setText(comment.getLikes() + " likes");
                    likedCommentsStateMap
                            .computeIfAbsent(videoId, k -> new HashMap<>())
                            .put(comment.getText(), isCommentLiked);
                    updateLikeCommentButton();
                });

                editCommentButton.setOnClickListener(v -> {
                    AddCommentDialog dialog = new AddCommentDialog();
                    dialog.setAddCommentListener(text -> {
                        if (!text.trim().isEmpty()) {
                            comment.setText(text);
                            commentTextView.setText(text);
                        }
                    });
                    dialog.show(getSupportFragmentManager(), "EditCommentDialog");
                });

                deleteCommentButton.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        comments.remove(position);
                        notifyItemRemoved(position);
                        updateCommentsCount();
                    }
                });
            }

            private void updateLikeCommentButton() {
                if (isCommentLiked) {
                    likeCommentButton.setText("Unlike");
                } else {
                    likeCommentButton.setText("Like");
                }
            }
        }
    }
}