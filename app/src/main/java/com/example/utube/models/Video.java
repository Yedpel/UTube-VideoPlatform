package com.example.utube.models;

public class Video {
    public String title;
    public String author;
    public String views;
    public String uploadTime;
    String thumbnailUrl;
    String authorProfilePicUrl;
    String videoUrl;

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getViews() {
        return views;
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

    public Video(String title, String author, String views, String uploadTime, String thumbnailUrl, String authorProfilePicUrl, String videoUrl) {
        this.title = title;
        this.author = author;
        this.views = views;
        this.uploadTime = uploadTime;
        this.thumbnailUrl = thumbnailUrl;
        this.authorProfilePicUrl = authorProfilePicUrl;
        this.videoUrl = videoUrl;
    }
}
