package com.andrew.prototype.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.andrew.prototype.R;
import com.andrew.prototype.Utils.DecodeBitmap;

public class TemplateAds extends RecyclerView.Adapter<TemplateAds.Holder> {

    private Context context;
    private int[] icTemplateImage;
    private int lastPosition;

    public interface onClick {
        void iconOnClick(int position);
    }

    public void setPosition(int lastPosition) {
        this.lastPosition = lastPosition;
        notifyDataSetChanged();
    }

    private onClick onClick;

    public TemplateAds(Context context, int[] icTemplateImage, onClick onClick) {
        this.context = context;
        this.onClick = onClick;
        this.icTemplateImage = icTemplateImage;
        lastPosition = 0;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_template_ads, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int i) {

        holder.imageView.setBackgroundResource(icTemplateImage[i]);

        holder.frameLayout.setVisibility(View.GONE);

        if (lastPosition == i) {
            holder.frameLayout.setVisibility(View.VISIBLE);
            holder.frameLayout.getBackground().setAlpha(100);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                onClick.iconOnClick(position);
                setPosition(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return icTemplateImage.length;
    }

    class Holder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private FrameLayout frameLayout;

        Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recycler_image_ads);
            frameLayout = itemView.findViewById(R.id.frame_recycler_template);
        }
    }
}
