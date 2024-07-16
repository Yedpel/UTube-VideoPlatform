package com.example.utube.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.utube.models.UserEntity;
import com.example.utube.models.VideoEntity;
import com.example.utube.models.CommentEntity;

@Database(entities = {UserEntity.class, VideoEntity.class, CommentEntity.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract UserDao userDao();
    public abstract VideoDao videoDao();
    public abstract CommentDao commentDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "utube_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}