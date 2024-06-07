package com.example.utube.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.utube.R;

public class AddCommentDialog extends DialogFragment {
    private AddCommentListener addCommentListener;

    public interface AddCommentListener {
        void onAddComment(String text);
    }

    public void setAddCommentListener(AddCommentListener listener) {
        this.addCommentListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_add_comment);

        EditText commentText = dialog.findViewById(R.id.comment_edit_text);
        Button submitCommentButton = dialog.findViewById(R.id.submit_comment_button);

        submitCommentButton.setOnClickListener(v -> {
            String text = commentText.getText().toString().trim();
            if (!text.isEmpty()) {
                addCommentListener.onAddComment(text);
                dismiss();
            } else {
                commentText.setError("Comment cannot be empty");
            }
        });

        return dialog;
    }
}
