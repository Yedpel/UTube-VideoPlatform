package com.example.utube.models;

public class Video {
    public String title;
    public String author;
    public String views;
    public String uploadTime;
    String thumbnailUrl;
    String authorProfilePicUrl;
    String videoUrl;
    String category;
    int likes;

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
    public String getCategory() { return category; }

    public int getLikes() {return likes;}

    public void setLikes(int likes) {this.likes = likes;}

    public Video(String title, String author, String views, String uploadTime, String thumbnailUrl, String authorProfilePicUrl, String videoUrl, String category, int likes) {
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
}
