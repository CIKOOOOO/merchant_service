package com.andrew.prototype.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.andrew.prototype.Model.SyncImg;
import com.andrew.prototype.R;
import com.andrew.prototype.Utils.DecodeBitmap;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageSyncAdapter extends RecyclerView.Adapter<ImageSyncAdapter.Holder> {
    private List<SyncImg> syncImgs;
    private Context context;

    public interface onImageSyncClicked {
        void imageIsClicked(SyncImg syncImg);
    }

    ImageSyncAdapter(Context context, onImageSyncClicked onImageSyncClicked) {
        this.context = context;
        this.onImageSyncClicked = onImageSyncClicked;
        syncImgs = new ArrayList<>();
    }

    void addSyncImgs(SyncImg syncImgs) {
        this.syncImgs.add(syncImgs);
        notifyDataSetChanged();
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
        final SyncImg syncImg = syncImgs.get(i);
//        Picasso.get().load(getImageUri(context, DecodeBitmap.compressBitmap(syncImg.getImg()))).into(holder.imageView);
        Glide.with(context).load(DecodeBitmap.compressBitmap(syncImg.getImg()))
                .placeholder(DecodeBitmap.setScaleDrawable(context, R.drawable.placeholder)).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onImageSyncClicked.imageIsClicked(syncImg);
            }
        });
    }

    @Override
    public int getItemCount() {
        return syncImgs.size();
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
