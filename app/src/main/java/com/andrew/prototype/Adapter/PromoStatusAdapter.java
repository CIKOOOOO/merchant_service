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

import com.andrew.prototype.Model.Product;
import com.andrew.prototype.Model.PromoForm;
import com.andrew.prototype.R;
import com.andrew.prototype.Utils.DecodeBitmap;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class PromoStatusAdapter extends RecyclerView.Adapter<PromoStatusAdapter.Holder> {
    private Context context;
    private List<Product> promoList;
    private String states;

    public PromoStatusAdapter(Context context, List<Product> promoList, String states) {
        this.states = states;
        this.context = context;
        this.promoList = promoList;
    }

    @NonNull
    @Override
    public PromoStatusAdapter.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_promo, viewGroup, false);
        return new PromoStatusAdapter.Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        Product product = promoList.get(i);
        holder.text_name.setText(product.getProductName());
        if (!promoList.get(i).getURLImage().equals("null"))
            Picasso.get().load(product.getURLImage()).into(holder.imageView);
        else holder.imageView.setVisibility(View.GONE);
        switch (states) {
            case PromoAdapter.STATE_CASH_BACK:
                holder.text_amount.setText("Cashback : " + product.getProductValue() + " %");
                break;
            case PromoAdapter.STATE_DISCOUNT:
                holder.text_amount.setText("Diskon : " + product.getProductValue() + " %");
                break;
            case PromoAdapter.STATE_INSTALLMENT:
                holder.text_amount.setText("Periode cicilan : " + product.getProductValue() + " " + context.getResources().getString(R.string.month));
                break;
            case PromoAdapter.STATE_SPECIAL_PRICE:
                holder.text_amount.setText("Special Price : IDR " + priceFormat(product.getProductValue()) + ",-");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return promoList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView text_name;
        TextView text_amount;
        ImageView imageView;

        Holder(@NonNull View itemView) {
            super(itemView);
            text_amount = itemView.findViewById(R.id.installment);
            text_name = itemView.findViewById(R.id.edittext_itemname);
            imageView = itemView.findViewById(R.id.recycler_image_promo);
        }
    }

    private String priceFormat(long totalPrice) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        return formatter.format(totalPrice);
    }

}
