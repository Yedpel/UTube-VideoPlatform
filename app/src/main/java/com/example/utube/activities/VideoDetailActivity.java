package com.example.utube.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utube.R;
import com.example.utube.models.Video;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class VideoDetailActivity extends AppCompatActivity {

    private VideoView videoView;
    private TextView titleTextView, authorTextView, viewsTextView, uploadTimeTextView, likesTextView, commentsCountTextView;
    private ImageView authorProfilePic;
    private Button likeButton, addCommentButton;
    private RecyclerView commentsRecyclerView;
    private boolean isLiked = false;
    private String videoId;
    private int likes;
    private int views;
    private List<Video.Comment> comments;
    private CommentsAdapter commentsAdapter;

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
        likeButton = findViewById(R.id.like_button);
        addCommentButton = findViewById(R.id.add_comment_button);
        commentsCountTextView = findViewById(R.id.comments_count);
        commentsRecyclerView = findViewById(R.id.comments_recycler_view);

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
        isLiked = MainActivity.likedStateMap.getOrDefault(videoId, false);
        likes = MainActivity.likesCountMap.getOrDefault(videoId, likes);
        likesTextView.setText(likes + " likes");

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

        // Set initial like button state
        updateLikeButton();

        likeButton.setOnClickListener(v -> {
            isLiked = !isLiked;
            if (isLiked) {
                likes++;
            } else {
                likes--;
            }
            likesTextView.setText(likes + " likes");
            MainActivity.likedStateMap.put(videoId, isLiked);
            MainActivity.likesCountMap.put(videoId, likes);
            updateLikeButton();
        });

        // Initialize comments section
        comments = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(comments);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentsAdapter);

        // Add comment button click listener
        addCommentButton.setOnClickListener(v -> {
            AddCommentDialog dialog = new AddCommentDialog();
            dialog.setAddCommentListener(text -> {
                String currentTime = "Just now"; // Use a proper timestamp in real app
                Video.Comment comment = new Video.Comment("user1", text, currentTime, 0, "drawable/error_image.webp");
                comments.add(comment);
                commentsAdapter.notifyDataSetChanged();
                updateCommentsCount();
            });
            dialog.show(getSupportFragmentManager(), "AddCommentDialog");
        });
    }

    private void updateLikeButton() {
        if (isLiked) {
            likeButton.setText("Unlike");
        } else {
            likeButton.setText("Like");
        }
    }

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

                likeCommentButton.setOnClickListener(v -> {
                    comment.setLikes(comment.getLikes() + 1);
                    commentLikesTextView.setText(comment.getLikes() + " likes");
                });

                editCommentButton.setOnClickListener(v -> {
                    AddCommentDialog editDialog = new AddCommentDialog();
                    editDialog.setAddCommentListener(newText -> {
                        comment.setText(newText);
                        commentTextView.setText(newText);
                    });
                    editDialog.show(getSupportFragmentManager(), "EditCommentDialog");
                });

                deleteCommentButton.setOnClickListener(v -> {
                    comments.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    updateCommentsCount();
                });
            }
        }
    }
}
