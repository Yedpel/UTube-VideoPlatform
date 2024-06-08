package com.example.utube.models;

import java.util.List;

public class Video {
    private String id;
    private String title;
    private String author;
    private int views;
    private String uploadTime;
    private String thumbnailUrl;
    private String authorProfilePicUrl;
    private String videoUrl;
    private String category;
    private int likes;

    public static class Comment {
        private int id;
        private String username;
        private String text;
        private String uploadTime;
        private int likes;
        private String profilePicUrl;

        public Comment(int id, String username, String text, String uploadTime, int likes, String profilePicUrl) {
            this.id = id;
            this.username = username;
            this.text = text;
            this.uploadTime = uploadTime;
            this.likes = likes;
            this.profilePicUrl = profilePicUrl;
        }

        public int getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getUploadTime() {
            return uploadTime;
        }

        public int getLikes() {
            return likes;
        }

        public void setLikes(int likes) {
            this.likes = likes;
        }

        public String getProfilePicUrl() {
            return profilePicUrl;
        }
    }

    public Video(String id, String title, String author, int views, String uploadTime, String thumbnailUrl, String authorProfilePicUrl, String videoUrl, String category, int likes) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.views = views;
        this.uploadTime = uploadTime;
        this.thumbnailUrl = thumbnailUrl;
        this.authorProfilePicUrl = authorProfilePicUrl;
        this.videoUrl = videoUrl;
        this.category = category;
        this.likes = likes;
    }

    // Getters and setters for all fields
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getAuthorProfilePicUrl() {
        return authorProfilePicUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getCategory() {
        return category;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}