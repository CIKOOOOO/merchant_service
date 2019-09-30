package com.andrew.prototype.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrew.prototype.Model.Forum;
import com.andrew.prototype.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageSyncAdapter extends RecyclerView.Adapter<ImageSyncAdapter.Holder> {
    private List<Forum.ForumImageReply> reply;
    private Context context;

    public interface onImageSyncClicked {
        void imageIsClicked(Forum.ForumImageReply syncImg);
    }

    public ImageSyncAdapter(List<Forum.ForumImageReply> reply, Context context, ImageSyncAdapter.onImageSyncClicked onImageSyncClicked) {
        this.reply = reply;
        this.context = context;
        this.onImageSyncClicked = onImageSyncClicked;
    }

    private onImageSyncClicked onImageSyncClicked;

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_image_thread, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        final Forum.ForumImageReply reply = this.reply.get(i);
        Picasso.get().load(reply.getImage_url()).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onImageSyncClicked.imageIsClicked(reply);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reply.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        RoundedImageView imageView;

        Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recycler_img_thread);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
