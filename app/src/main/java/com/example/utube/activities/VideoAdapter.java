package com.example.utube.activities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utube.R;
import com.example.utube.models.Video;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private List<Video> videoList;
    private Context context;

    public VideoAdapter(List<Video> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videoList.get(position);
        holder.bind(video);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VideoDetailActivity.class);
            intent.putExtra("VIDEO_ID", video.getId());
            intent.putExtra("VIDEO_URL", video.getVideoUrl());
            intent.putExtra("TITLE", video.getTitle());
            intent.putExtra("AUTHOR", video.getAuthor());
            intent.putExtra("VIEWS", video.getViews());
            intent.putExtra("UPLOAD_TIME", video.getUploadTime());
            intent.putExtra("AUTHOR_PROFILE_PIC_URL", video.getAuthorProfilePicUrl());
            intent.putExtra("LIKES", video.getLikes());
            ((AppCompatActivity) context).startActivityForResult(intent, 1);
        });

        holder.menuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(holder.menuButton.getContext(), holder.menuButton);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.video_item_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.edit_video) {
                    EditVideoDialog dialog = EditVideoDialog.newInstance(video.getId());
                    dialog.show(((AppCompatActivity) holder.itemView.getContext()).getSupportFragmentManager(), "EditVideoDialog");
                    return true;
                } else if (itemId == R.id.delete_video) {
                    VideoManager.getInstance().removeVideo(video.getId());
                    notifyDataSetChanged();
                    return true;
                } else {
                    return false;
                }
            });
            popupMenu.show();
        });

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, views, uploadTime;
        ImageView thumbnail, authorProfilePic;
        Button menuButton;

        public VideoViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.video_title);
            author = itemView.findViewById(R.id.video_author);
            views = itemView.findViewById(R.id.video_views);
            uploadTime = itemView.findViewById(R.id.video_upload_time);
            thumbnail = itemView.findViewById(R.id.video_thumbnail);
            authorProfilePic = itemView.findViewById(R.id.author_profile_pic);
            menuButton = itemView.findViewById(R.id.menu_button);
        }




        public void bind(Video video) {
            title.setText(video.getTitle());
            author.setText(video.getAuthor());
            views.setText(video.getViews() + " views");
            uploadTime.setText(video.getUploadTime());

            loadImageView(thumbnail, video.getThumbnailUrl());
            loadImageView(authorProfilePic, video.getAuthorProfilePicUrl());
        }

        private void loadImageView(ImageView imageView, String imageUrl) {
            if (imageUrl != null && imageUrl.startsWith("drawable/")) {
                // Handle drawable resources
                int imageResId = itemView.getContext().getResources().getIdentifier(imageUrl, null, itemView.getContext().getPackageName());
                if (imageResId != 0) {
                    imageView.setImageResource(imageResId);
                } else {
                    imageView.setImageResource(R.drawable.policy); // Fallback to error image if resource not found
                }
            } else if (imageUrl != null) {
                // Handle remote images or local file URIs
                Picasso.get().load(imageUrl).error(R.drawable.policy).into(imageView);
            } else {
                // Fallback for unexpected image URL formats or null imageUrl
                imageView.setImageResource(R.drawable.policy);
            }
        }

    }
}
