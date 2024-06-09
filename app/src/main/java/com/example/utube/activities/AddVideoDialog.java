package com.example.utube.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.utube.R;

public class AddVideoDialog extends DialogFragment {
    private AddVideoListener addVideoListener;

    public interface AddVideoListener {
        void onAddVideo(String title, String category);
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
        Spinner categorySpinner = dialog.findViewById(R.id.category_spinner);
        Button addVideoButton = dialog.findViewById(R.id.add_video_button);

        // Set up the spinner with categories
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.video_categories,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        addVideoButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String category = categorySpinner.getSelectedItem().toString();
            if (!title.isEmpty() && !category.isEmpty()) {
                addVideoListener.onAddVideo(title, category);
                dismiss();
            } else {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        return dialog;
    }
}
