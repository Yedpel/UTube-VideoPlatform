package com.example.utube.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.utube.R;

public class AddMovieActivity extends AppCompatActivity {

    private static final int PICK_VIDEO_REQUEST = 1;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText categoryEditText;
    private Button uploadButton;
    private Button pickVideoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);

        titleEditText = findViewById(R.id.title_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        categoryEditText = findViewById(R.id.category_edit_text);
        uploadButton = findViewById(R.id.upload_button);
        pickVideoButton = findViewById(R.id.pick_video_button);

        pickVideoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("video/*");
            startActivityForResult(intent, PICK_VIDEO_REQUEST);
        });

        uploadButton.setOnClickListener(v -> {
            // Handle video upload logic here
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            // Handle the selected video URI
        }
    }
}
