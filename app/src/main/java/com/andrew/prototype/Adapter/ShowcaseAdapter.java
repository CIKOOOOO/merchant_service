package com.andrew.prototype.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.andrew.prototype.Model.Merchant;
import com.andrew.prototype.Model.MerchantStory;
import com.andrew.prototype.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class ShowcaseAdapter extends RecyclerView.Adapter<ShowcaseAdapter.ViewHolder> {
    private Context context;
    private int position;
    private List<MerchantStory> showCases;
    private Map<String, Merchant> merchantMap;
    private boolean check;

    public interface onImageClickListener {
        void onImageClick(Context context, int pos);
    }

    public void setShowCases(List<MerchantStory> showCases) {
        this.showCases = showCases;
    }

    public void setMerchantMap(Map<String, Merchant> merchantMap) {
        this.merchantMap = merchantMap;
    }

    public int getAdapterPosition() {
        return position;
    }

    private onImageClickListener onImageClickListener;

    public ShowcaseAdapter(Context context, List<MerchantStory> showCases, boolean check, Map<String, Merchant> merchantMap, onImageClickListener onItemClickListener) {
        this.context = context;
        this.merchantMap = merchantMap;
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
//                onImageClickListener.onImageClick(context, pos);
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

        viewHolder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = viewHolder.getAdapterPosition();
                onImageClickListener.onImageClick(context, pos);
                position = pos;
            }
        });

        if (i == 0) {
            viewHolder.frameLayout.setVisibility(View.VISIBLE);
            viewHolder.imageView.setVisibility(View.GONE);
            viewHolder.imageButton.setEnabled(true);
        } else {
            Picasso.get().load(showCases.get(i - 1).getStory_picture()).into(viewHolder.imageView);
//            if (showCases.get(i).getImage() != 0) {
//                DecodeBitmap.setScaledImageView(viewHolder.imageView, showCases.get(i).getImage(), context);
////                Glide.with(context)
////                        .load(showCases.get(i).getImage())
////                        .placeholder(context.getDrawable(R.drawable.placeholder))
////                        .into(viewHolder.imageView);
//            } else {
//                Glide.with(context)
//                        .load(DecodeBitmap.compressBitmap(showCases.get(i).getImgBitmap()))
//                        .placeholder(DecodeBitmap.setScaleDrawable(context, R.drawable.placeholder))
//                        .into(viewHolder.imageView);
//            }
            viewHolder.imageButton.setEnabled(false);
            viewHolder.frameLayout.setVisibility(View.GONE);
            viewHolder.imageView.setVisibility(View.VISIBLE);
            if (merchantMap.get(showCases.get(i - 1).getMid()) != null) {
                Picasso.get()
                        .load(merchantMap.get(showCases.get(i - 1).getMid()).getMerchant_profile_picture())
                        .into(viewHolder.image_profile);
            }
        }
    }

    @Override
    public int getItemCount() {
        return showCases.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageView, image_profile;
        ImageButton imageButton;
        FrameLayout frameLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            frameLayout = itemView.findViewById(R.id.custom_frame);
            imageButton = itemView.findViewById(R.id.addPhoto_custom);
            imageView = itemView.findViewById(R.id.img_recycler);
            image_profile = itemView.findViewById(R.id.img_profile_recycler);
        }
    }
}
