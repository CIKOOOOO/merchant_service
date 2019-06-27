package com.andrew.prototype.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.andrew.prototype.Model.Product;
import com.andrew.prototype.Model.PromoTransaction;
import com.andrew.prototype.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class StatusPromoAdapter extends RecyclerView.Adapter<StatusPromoAdapter.Holder> {

    private Context mContext;
    private List<PromoTransaction> list;
    private HashMap<String, List<Product>> map;

    public void setStatusPromo(PromoTransaction statusPromo, int pos) {
        list.set(pos, statusPromo);
    }

    public void setList(List<PromoTransaction> list, HashMap<String, List<Product>> map) {
        this.list = list;
        this.map = map;
    }

    public interface statusOnClick {
        void onClick(int pos);
    }

    private statusOnClick statusOnClick;

    public StatusPromoAdapter(Context mContext, List<PromoTransaction> list, HashMap<String, List<Product>> map, statusOnClick statusOnClick) {
        this.mContext = mContext;
        this.map = map;
        this.statusOnClick = statusOnClick;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_status_promo, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int i) {
        PromoTransaction promoTransaction = list.get(i);
        Picasso.get().load(promoTransaction.getImageURLForAds()).into(holder.imageView);
        holder.title.setText(promoTransaction.getPromotionTitle());
        holder.date.setText(promoTransaction.getDateRequest());
        if (promoTransaction.getStatusChecking().equals("accepted")) {
            holder.status.setText(" Diterima");
            holder.status.setTextColor(mContext.getResources().getColor(R.color.malachite2_palette));
        } else if (promoTransaction.getStatusChecking().equals("not-accepted")) {
            holder.status.setText(" Ditolak");
            holder.status.setTextColor(mContext.getResources().getColor(R.color.thunderbird_palette));
        } else {
            holder.status.setText(" Sedang diproses");
            holder.status.setTextColor(mContext.getResources().getColor(R.color.fuel_yellow_palette));
        }

        holder.rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                statusOnClick.onClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, date, status;
        RippleView rippleView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recycler_image_status_promotion);
            status = itemView.findViewById(R.id.recycler_condition_status_promotion);
            title = itemView.findViewById(R.id.recycler_title_status_promotion);
            date = itemView.findViewById(R.id.recycler_date_status_promotion);
            rippleView = itemView.findViewById(R.id.ripple_status_promo);
        }
    }
}
