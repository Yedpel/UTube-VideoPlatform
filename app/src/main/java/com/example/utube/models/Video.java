package com.example.utube.models;

public class Video {
    private String id;
    private String title;
    private String author;
    private int views; // Changed to int
    private String uploadTime;
    private String thumbnailUrl;
    private String authorProfilePicUrl;
    private String videoUrl;
    private String category;
    private int likes;

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

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getAuthorProfilePicUrl() {
        return authorProfilePicUrl;
    }

    public void setAuthorProfilePicUrl(String authorProfilePicUrl) {
        this.authorProfilePicUrl = authorProfilePicUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
