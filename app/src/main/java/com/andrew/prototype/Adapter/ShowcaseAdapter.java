package com.andrew.prototype.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.andrew.prototype.Model.ShowCase;
import com.andrew.prototype.R;
import com.andrew.prototype.Utils.Constant;
import com.andrew.prototype.Utils.DecodeBitmap;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class ShowcaseAdapter extends RecyclerView.Adapter<ShowcaseAdapter.ViewHolder> {
    private Context context;
    private int position;
    private List<ShowCase> showCases;
    private boolean check;

    public interface onImageClickListener {
        void onImageClick(Context context, int pos);
    }

    public void setShowCases(List<ShowCase> showCases) {
        this.showCases = showCases;
        notifyDataSetChanged();
    }

    public int getAdapterPosition() {
        return position;
    }

    private onImageClickListener onImageClickListener;

    public ShowcaseAdapter(Context context, List<ShowCase> showCases, boolean check, onImageClickListener onItemClickListener) {
        this.context = context;
        this.onImageClickListener = onItemClickListener;
        this.showCases = showCases;
        this.check = check;
        position = -1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_custom_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = viewHolder.getAdapterPosition();
                onImageClickListener.onImageClick(context, pos);
                position = pos;
            }
        });

        viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = viewHolder.getAdapterPosition();
                onImageClickListener.onImageClick(context, pos);
                position = pos;
            }
        });

        if (i == 0) {
            viewHolder.frameLayout.setVisibility(View.VISIBLE);
            viewHolder.imageButton.setVisibility(View.VISIBLE);
            viewHolder.imageView.setVisibility(View.GONE);
        } else {
            if (showCases.get(i).getImage() != 0) {
                DecodeBitmap.setScaledImageView(viewHolder.imageView, showCases.get(i).getImage(), context);
//                Glide.with(context)
//                        .load(showCases.get(i).getImage())
//                        .placeholder(context.getDrawable(R.drawable.placeholder))
//                        .into(viewHolder.imageView);
            } else {
                Glide.with(context)
                        .load(DecodeBitmap.compressBitmap(showCases.get(i).getImgBitmap()))
                        .placeholder(DecodeBitmap.setScaleDrawable(context, R.drawable.placeholder))
                        .into(viewHolder.imageView);
            }
            viewHolder.frameLayout.setVisibility(View.GONE);
            viewHolder.imageButton.setVisibility(View.GONE);
            viewHolder.imageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return showCases.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageView;
        ImageButton imageButton;
        FrameLayout frameLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            frameLayout = itemView.findViewById(R.id.custom_frame);
            imageButton = itemView.findViewById(R.id.addPhoto_custom);
            imageView = itemView.findViewById(R.id.img_recycler);
        }
    }
}
