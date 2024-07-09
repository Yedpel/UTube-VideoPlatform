package com.example.utube.data;

import android.app.Application;
import com.example.utube.models.CommentEntity;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommentRepository {
    private CommentDao commentDao;
    private ExecutorService executorService;

    public CommentRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        commentDao = db.commentDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(CommentEntity comment) {
        executorService.execute(() -> commentDao.insert(comment));
    }

    public List<CommentEntity> getCommentsForVideo(String videoId) {
        try {
            return executorService.submit(() -> commentDao.getCommentsForVideo(videoId)).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateComment(CommentEntity comment) {
        executorService.execute(() -> commentDao.updateComment(comment));
    }

    public void deleteComment(CommentEntity comment) {
        executorService.execute(() -> commentDao.deleteComment(comment));
    }
}