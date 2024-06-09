package com.example.utube;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.utube.activities.MainActivity;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Set the default uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            // Show a user-friendly message
            new Handler(Looper.getMainLooper()).post(() -> {
                Toast.makeText(getApplicationContext(), "Sorry, unable to complete your last action, please do not downgrade us", Toast.LENGTH_LONG).show();
            });

            // Log the exception (optional)
            throwable.printStackTrace();

            // Restart the app or take other actions if needed
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // Exit the app
            System.exit(1);
        });
    }
}