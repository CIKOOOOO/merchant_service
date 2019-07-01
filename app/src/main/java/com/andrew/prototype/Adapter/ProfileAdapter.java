package com.andrew.prototype.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrew.prototype.Model.ProfileModel;
import com.andrew.prototype.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.Holder> {
    private Context context;
    private List<ProfileModel> profileModels;

    public ProfileAdapter(Context context, List<ProfileModel> profileModels) {
        this.context = context;
        this.profileModels = profileModels;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.recycler_profile_setting, viewGroup, false);
        return new Holder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        ProfileModel profileModel = profileModels.get(i);
        Glide.with(context).load(profileModel.getIcon()).into(holder.image);
        holder.parents.setText(profileModel.getParent());
        holder.child.setText(profileModel.getChild());
    }

    @Override
    public int getItemCount() {
        return profileModels.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView parents;
        TextView child;

        public Holder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.recycler_icon_profile);
            parents = itemView.findViewById(R.id.recycler_parent_profile);
            child = itemView.findViewById(R.id.recycler_child_profile);
        }
    }
}
