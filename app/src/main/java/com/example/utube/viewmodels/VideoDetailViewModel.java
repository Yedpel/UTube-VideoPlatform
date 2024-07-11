package com.example.utube.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.utube.activities.MainActivity;
import com.example.utube.activities.VideoManager;
import com.example.utube.models.CommentEntity;
import com.example.utube.models.Video;
import com.example.utube.data.CommentRepository;

import java.util.ArrayList;
import java.util.List;

public class VideoDetailViewModel extends AndroidViewModel {
    private VideoManager videoManager;
    private CommentRepository commentRepository;
    private MutableLiveData<Video> video = new MutableLiveData<>();
    private MutableLiveData<List<Video.Comment>> comments = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLiked = new MutableLiveData<>();

    private SharedPreferences sharedPreferences;


    public VideoDetailViewModel(Application application) {
        super(application);
        videoManager = VideoManager.getInstance(application);
        commentRepository = new CommentRepository(application);
        sharedPreferences = application.getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void loadVideo(String videoId) {
        Video loadedVideo = videoManager.getVideoById(videoId);
        if (loadedVideo != null) {
            loadedVideo.setViews(loadedVideo.getViews() + 1);
            videoManager.updateVideo(loadedVideo);
            video.postValue(loadedVideo);
            Log.d("VideoDetailViewModel", "Updated views for video " + videoId + ": " + loadedVideo.getViews());
        }
    }

    public void addComment(String videoId, String username, String text, String profilePicUrl) {
        String currentTime = "Just now"; // You might want to use a proper timestamp
        CommentEntity newComment = new CommentEntity(videoId, username, text, currentTime, 0, profilePicUrl);
        commentRepository.insert(newComment);

        // Update the LiveData directly instead of reloading all comments
        List<Video.Comment> currentComments = comments.getValue();
        if (currentComments == null) {
            currentComments = new ArrayList<>();
        }
        currentComments.add(convertToVideoComment(newComment));
        comments.postValue(currentComments);
    }

    public void updateComment(CommentEntity comment) {
        commentRepository.updateComment(comment);
        loadComments(comment.getVideoId()); // Reload comments after updating
    }

    public void deleteComment(CommentEntity comment) {
        commentRepository.deleteComment(comment);
        loadComments(comment.getVideoId()); // Reload comments after deleting
    }

    public void updateLikesCount(boolean isLiked) {
        Video currentVideo = video.getValue();
        if (currentVideo != null) {
            int currentLikes = currentVideo.getLikes();
            currentVideo.setLikes(isLiked ? currentLikes + 1 : currentLikes - 1);
            videoManager.updateVideo(currentVideo);
            video.postValue(currentVideo);
        }
    }

    private Video.Comment convertToVideoComment(CommentEntity entity) {
        return new Video.Comment(entity.getId(), entity.getUsername(), entity.getText(),
                entity.getUploadTime(), entity.getLikes(), entity.getProfilePicUrl());
    }

    public void loadComments(String videoId) {
        List<CommentEntity> loadedComments = commentRepository.getCommentsForVideo(videoId);
        List<Video.Comment> convertedComments = new ArrayList<>();
        for (CommentEntity entity : loadedComments) {
            convertedComments.add(convertToVideoComment(entity));
        }
        comments.postValue(convertedComments);
    }

    public void updateLikeStatus(String videoId, String currentLoggedInUser, boolean liked) {
        String likeKey = videoId + "_" + currentLoggedInUser + "_liked";
        sharedPreferences.edit().putBoolean(likeKey, liked).apply();
        isLiked.postValue(liked);

        Video video = videoManager.getVideoById(videoId);
        if (video != null) {
            int currentLikes = video.getLikes();
            video.setLikes(liked ? currentLikes + 1 : currentLikes - 1);
            videoManager.updateVideo(video);
            this.video.postValue(video);
        }
    }

    public boolean isVideoLiked(String videoId, String currentLoggedInUser) {
        String likeKey = videoId + "_" + currentLoggedInUser + "_liked";
        return sharedPreferences.getBoolean(likeKey, false);
    }


    public void incrementViews() {
        Video currentVideo = video.getValue();
        if (currentVideo != null) {
            currentVideo.setViews(currentVideo.getViews() + 1);
            videoManager.updateVideo(currentVideo);
            video.postValue(currentVideo);
        }
    }

    //    public void updateCommentLikeStatus(String videoId, Video.Comment comment, boolean isLiked) {
//        CommentEntity commentEntity = convertToCommentEntity(videoId, comment);
//        commentEntity.setLikes(isLiked ? commentEntity.getLikes() + 1 : commentEntity.getLikes() - 1);
//        commentRepository.updateComment(commentEntity);
//        loadComments(videoId);
//    }
    public void updateCommentLikeStatus(String videoId, int commentId, String currentLoggedInUser, boolean liked) {
        String likeKey = videoId + "_" + commentId + "_" + currentLoggedInUser + "_liked";
        sharedPreferences.edit().putBoolean(likeKey, liked).apply();

        List<Video.Comment> currentComments = comments.getValue();
        if (currentComments != null) {
            for (Video.Comment comment : currentComments) {
                if (comment.getId() == commentId) {
                    int currentLikes = comment.getLikes();
                    comment.setLikes(liked ? currentLikes + 1 : currentLikes - 1);
                    break;
                }
            }
            comments.postValue(currentComments);
        }

        // Update the comment in the database
        CommentEntity commentEntity = commentRepository.getCommentById(commentId);
        if (commentEntity != null) {
            commentEntity.setLikes(liked ? commentEntity.getLikes() + 1 : commentEntity.getLikes() - 1);
            commentRepository.updateComment(commentEntity);
        }
    }

    public boolean isCommentLiked(String videoId, int commentId, String currentLoggedInUser) {
        String likeKey = videoId + "_" + commentId + "_" + currentLoggedInUser + "_liked";
        return sharedPreferences.getBoolean(likeKey, false);
    }

    private CommentEntity convertToCommentEntity(String videoId, Video.Comment comment) {
        CommentEntity entity = new CommentEntity(videoId, comment.getUsername(), comment.getText(),
                comment.getUploadTime(), comment.getLikes(), comment.getProfilePicUrl());
        entity.setId(comment.getId());
        return entity;
    }

    // Add getter methods for LiveData objects

    public VideoManager getVideoManager() {
        return videoManager;
    }

    public CommentRepository getCommentRepository() {
        return commentRepository;
    }

    public LiveData<Video> getVideo() {
        return video;
    }

    public LiveData<List<Video.Comment>> getComments() {
        return comments;
    }

    public LiveData<Boolean> getIsLiked() {
        return isLiked;
    }
}