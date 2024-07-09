package com.example.utube.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.utube.R;
import com.example.utube.models.Video;

public class EditVideoDialog extends DialogFragment {
    private static final int REQUEST_VIDEO_PICK = 3;
    private static final String ARG_VIDEO_ID = "video_id";

    private EditText titleEditText;
    private Spinner categorySpinner;
    private Button changeVideoButton, saveChangesButton;
    private Uri newVideoUri;
    private String videoId;
    private OnDismissListener onDismissListener;

    public static EditVideoDialog newInstance(String videoId) {
        EditVideoDialog dialog = new EditVideoDialog();
        Bundle args = new Bundle();
        args.putString(ARG_VIDEO_ID, videoId);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Edit Video");
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_video, container, false);

        titleEditText = view.findViewById(R.id.edit_title);
        categorySpinner = view.findViewById(R.id.edit_category_spinner);
        changeVideoButton = view.findViewById(R.id.change_video_button);
        saveChangesButton = view.findViewById(R.id.save_changes_button);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.video_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        videoId = getArguments().getString(ARG_VIDEO_ID);
        Video video = VideoManager.getInstance(requireActivity().getApplication()).getVideoMap().get(videoId);
        if (video != null) {
            titleEditText.setText(video.getTitle());
            int categoryPosition = adapter.getPosition(video.getCategory());
            categorySpinner.setSelection(categoryPosition);
        }

        changeVideoButton.setOnClickListener(v -> openVideoPicker());
        saveChangesButton.setOnClickListener(v -> saveChanges());

        return view;
    }

    private void openVideoPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_VIDEO_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_PICK && resultCode == getActivity().RESULT_OK && data != null) {
            newVideoUri = data.getData();
        }
    }

    private void saveChanges() {
        String newTitle = titleEditText.getText().toString();
        String newCategory = categorySpinner.getSelectedItem().toString();
        Video video = VideoManager.getInstance(requireActivity().getApplication()).getVideoMap().get(videoId);
        if (video != null) {
            video.setTitle(newTitle);
            video.setCategory(newCategory);
            if (newVideoUri != null) {
                video.setVideoUrl(newVideoUri.toString());
            }
            VideoManager.getInstance(requireActivity().getApplication()).updateVideo(video);
            dismiss();
        }
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.onDismissListener = listener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    public interface OnDismissListener {
        void onDismiss(DialogInterface dialog);
    }
}
