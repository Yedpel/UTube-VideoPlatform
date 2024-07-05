//package com.example.utube.adapters;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.utube.R;
//import com.example.utube.models.Video;
//import com.squareup.picasso.Picasso;
//
//import java.util.List;
//
//public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
//    private List<Video> videoList;
//
//    public VideoAdapter(List<Video> videoList) {
//        this.videoList = videoList;
//    }
//
//    @NonNull
//    @Override
//    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
//        return new VideoViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
//        Video video = videoList.get(position);
//        holder.bind(video);
//    }
//
//    @Override
//    public int getItemCount() {
//        return videoList.size();
//    }
//
//    static class VideoViewHolder extends RecyclerView.ViewHolder {
//        TextView title;
//        TextView author;
//        TextView views;
//        TextView uploadTime;
//        ImageView thumbnail;
//        ImageView authorProfilePic;
//
//        public VideoViewHolder(@NonNull View itemView) {
//            super(itemView);
//            title = itemView.findViewById(R.id.video_title);
//            author = itemView.findViewById(R.id.video_author);
//            views = itemView.findViewById(R.id.video_views);
//            uploadTime = itemView.findViewById(R.id.video_upload_time);
//            thumbnail = itemView.findViewById(R.id.video_thumbnail);
//            authorProfilePic = itemView.findViewById(R.id.author_profile_pic);
//        }
//
//        public void bind(Video video) {
//            title.setText(video.getTitle());
//            author.setText(video.getAuthor());
//            views.setText(video.getViews());
//            uploadTime.setText(video.getUploadTime());
//            // Assuming you have a method to load images, e.g., using Glide or Picasso
//            // Glide.with(thumbnail.getContext()).load(video.getThumbnailUrl()).into(thumbnail);
//            // Glide.with(authorProfilePic.getContext()).load(video.getAuthorProfilePicUrl()).into(authorProfilePic);
//            // Use Picasso or Glide to load the images
//            Picasso.get().load(video.getThumbnailUrl()).into(thumbnail);
//            Picasso.get().load(video.getAuthorProfilePicUrl()).into(authorProfilePic);
//        }
//    }
//}
