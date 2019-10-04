package com.andrew.prototype.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.andrew.prototype.Model.Forum;
import com.andrew.prototype.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.Holder> {

    private Context mContext;
    private List<Forum.ForumImage> forumImageList;

    public ImageGridAdapter(Context mContext, List<Forum.ForumImage> forumImageList, ImageGridAdapter.imageOnClick imageOnClick) {
        this.mContext = mContext;
        this.forumImageList = forumImageList;
        this.imageOnClick = imageOnClick;
    }

    public void setForumImageList(List<Forum.ForumImage> forumImageList) {
        this.forumImageList = forumImageList;
    }

    public interface imageOnClick {
        void imageOnClick(int pos);
    }

    private imageOnClick imageOnClick;

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_image_thread, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int i) {
        Picasso.get().load(forumImageList.get(i).getImage_url()).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageOnClick.imageOnClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return forumImageList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recycler_img_thread);
        }
    }
}
