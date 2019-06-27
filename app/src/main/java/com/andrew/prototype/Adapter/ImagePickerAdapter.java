package com.andrew.prototype.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.prototype.Model.ImagePicker;
import com.andrew.prototype.R;
import com.andrew.prototype.Utils.DecodeBitmap;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class ImagePickerAdapter extends RecyclerView.Adapter<ImagePickerAdapter.Holder> {
    public static final String STATES = "PREVIEW";
    public static final String STATES_IMAGE = "IMAGE";
    public static final String STATES_PDF = "PDF";
    public static final String STATES_NO_BUTTON = "NO_BUTTON";
    public static final String STATES_CLICKED_DELETE = "STATES_CLICKED_DELETE";
    public static final String STATES_CLICKED_PREVIEW_IMAGE = "STATES_CLICKED_PREVIEW_IMAGE";

    private Context context;
    private List<ImagePicker> imageList;
    private String STATE;

    public interface onItemClick {
        void onItemClicked(int pos, String states);
    }

    private onItemClick onItemClick;

    public void setImageList(List<ImagePicker> imageList) {
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    public ImagePickerAdapter(Context context, List<ImagePicker> imageList, onItemClick onItemClick, String STATE) {
        this.context = context;
        this.imageList = imageList;
        this.onItemClick = onItemClick;
        this.STATE = STATE;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        if (i == 1)
            v = LayoutInflater.from(context).inflate(R.layout.recycler_image_thread, viewGroup, false);
        else
            v = LayoutInflater.from(context).inflate(R.layout.recycler_new_thread, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int i) {
        switch (STATE) {
            case STATES:
                DecodeBitmap.setScaledImageView(holder.imageViews, imageList.get(i).getImg_static(), context);
                holder.imageViews.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClick.onItemClicked(holder.getAdapterPosition(), STATES_CLICKED_PREVIEW_IMAGE);
                    }
                });
                break;
            case STATES_NO_BUTTON:
                switch (imageList.get(i).getType()) {
                    case STATES_PDF:
                        DecodeBitmap.setScaledImageView(holder.imageViews, R.drawable.logo_pdf, context);
                        break;
                    case STATES_IMAGE:
                        DecodeBitmap.setScaledImageView(holder.imageViews, imageList.get(i).getImg_static(), context);
                        break;
                    default:
                        Glide.with(context).load(DecodeBitmap.compressBitmap(imageList.get(i).getImage_bitmap())).into(holder.imageViews);
                        break;
                }
                holder.textview_img_thread.setVisibility(View.VISIBLE);
                holder.textview_img_thread.setText(imageList.get(i).getTitle());
                break;
            default:
                switch (imageList.get(i).getType()) {
                    case STATES_PDF:
                        DecodeBitmap.setScaledImageView(holder.imageView, R.drawable.logo_pdf, context);
                        break;
                    case STATES_IMAGE:
                        DecodeBitmap.setScaledImageView(holder.imageView, imageList.get(i).getImg_static(), context);
                        break;
                    default:
                        Glide.with(context).load(DecodeBitmap.compressBitmap(imageList.get(i).getImage_bitmap())).into(holder.imageView);
                        break;
                }

                holder.textView.setText(imageList.get(i).getTitle());

                holder.imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClick.onItemClicked(holder.getAdapterPosition(), STATES_CLICKED_DELETE);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (STATE.equals(STATES) || (STATE.equals(STATES_NO_BUTTON)) ? 1 : 0);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        RoundedImageView imageView;
        ImageView imageViews;
        TextView textView, textview_img_thread;
        ImageButton imageButton;

        Holder(@NonNull View itemView) {
            super(itemView);
            imageViews = itemView.findViewById(R.id.recycler_img_thread);
            imageView = itemView.findViewById(R.id.img_recycler_new_thread);
            textView = itemView.findViewById(R.id.title_recycler_new_thread);
            imageButton = itemView.findViewById(R.id.close_recycler_new_thread);
            textview_img_thread = itemView.findViewById(R.id.title_recycler_img_thread);
        }
    }
}
