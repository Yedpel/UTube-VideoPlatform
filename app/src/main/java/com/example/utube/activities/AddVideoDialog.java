package com.example.utube.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.utube.R;

public class AddVideoDialog extends DialogFragment {
    private AddVideoListener addVideoListener;

    public interface AddVideoListener {
        void onAddVideo(String title, String author, String videoUrl, String thumbnailUrl, String authorProfilePicUrl, String category);
    }

    public void setAddVideoListener(AddVideoListener listener) {
        this.addVideoListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_add_video);

        EditText titleEditText = dialog.findViewById(R.id.video_title_edit_text);
        EditText authorEditText = dialog.findViewById(R.id.video_author_edit_text);
        EditText videoUrlEditText = dialog.findViewById(R.id.video_url_edit_text);
        EditText thumbnailUrlEditText = dialog.findViewById(R.id.thumbnail_url_edit_text);
        EditText authorProfilePicUrlEditText = dialog.findViewById(R.id.author_profile_pic_url_edit_text);
        EditText categoryEditText = dialog.findViewById(R.id.category_edit_text);
        Button addVideoButton = dialog.findViewById(R.id.add_video_button);

        addVideoButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String author = authorEditText.getText().toString().trim();
            String videoUrl = videoUrlEditText.getText().toString().trim();
            String thumbnailUrl = thumbnailUrlEditText.getText().toString().trim();
            String authorProfilePicUrl = authorProfilePicUrlEditText.getText().toString().trim();
            String category = categoryEditText.getText().toString().trim();

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(author) || TextUtils.isEmpty(videoUrl) ||
                    TextUtils.isEmpty(thumbnailUrl) || TextUtils.isEmpty(authorProfilePicUrl) || TextUtils.isEmpty(category)) {
                // Show an error message or do something if fields are empty
            } else {
                addVideoListener.onAddVideo(title, author, videoUrl, thumbnailUrl, authorProfilePicUrl, category);
                dismiss();
            }
        });

        return dialog;
    }
}
