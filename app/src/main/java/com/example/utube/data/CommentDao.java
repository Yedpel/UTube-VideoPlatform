package com.example.utube.data;

import androidx.room.*;
import com.example.utube.models.CommentEntity;
import java.util.List;

@Dao
public interface CommentDao {
    @Insert
    void insert(CommentEntity comment);

    @Query("SELECT * FROM comments WHERE videoId = :videoId")
    List<CommentEntity> getCommentsForVideo(String videoId);

    @Update
    void updateComment(CommentEntity comment);

    @Delete
    void deleteComment(CommentEntity comment);
}