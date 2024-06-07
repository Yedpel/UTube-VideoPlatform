package com.example.utube.models;

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
