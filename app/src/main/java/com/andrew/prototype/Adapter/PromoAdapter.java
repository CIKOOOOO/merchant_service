package com.andrew.prototype.Adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andrew.prototype.Model.PromoForm;
import com.andrew.prototype.R;
import com.andrew.prototype.Utils.DecodeBitmap;
import com.andrew.prototype.Utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class PromoAdapter extends RecyclerView.Adapter<PromoAdapter.Holder> {
    public static final String STATE_DISCOUNT = "STATE_DISCOUNT";
    public static final String STATE_INSTALLMENT = "STATE_INSTALLMENT";
    public static final String STATE_CASH_BACK = "STATE_CASH_BACK";
    public static final String STATE_SPECIAL_PRICE = "STATE_SPECIAL_PRICE";

    private String STATE;
    private boolean isTrashVisible = true;
    private Context context;

    private List<PromoForm> promoList;

    public interface onDelete {
        void onDeletes(int position);
    }

    private onDelete onDelete;

    public PromoAdapter(String STATE, Context context, List<PromoForm> promoList, boolean isTrashVisible) {
        this.STATE = STATE;
        this.isTrashVisible = isTrashVisible;
        this.context = context;
        this.promoList = promoList;
    }

    public PromoAdapter(Context context, List<PromoForm> promoList, onDelete onDelete) {
        this.context = context;
        this.onDelete = onDelete;
        this.promoList = promoList;
        STATE = STATE_DISCOUNT;
    }

    public void setPromoList(String STATE, List<PromoForm> promoList) {
        this.STATE = STATE;
        this.promoList = promoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_promo, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        Utils utils = new Utils();
        holder.text_name.setText(promoList.get(i).getStuffName());
        final int position = holder.getAdapterPosition();
        if (isTrashVisible) holder.remove.setVisibility(View.VISIBLE);
        /*
        * Image Will appear only with this way. I do not find another way for this,
        * I don't know if it related with static reference or not, but i try to remove static
        * reference from PromoRequest fragment and it changes nothing
        * So, i have to check if there is bitmap or not, then set size for image
        * */
        if (promoList.get(i).getBitmap() != null) {
            holder.imageView.setVisibility(View.VISIBLE);
//            holder.imageView.getLayoutParams().height = utils.dpToPx(context, 100);
//            holder.imageView.getLayoutParams().width = utils.dpToPx(context, 100);
//            holder.imageView.requestLayout();
            Glide.with(context)
                    .load(DecodeBitmap.compressBitmap(promoList.get(i).getBitmap()))
                    .into(holder.imageView);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
        switch (STATE) {
            case STATE_DISCOUNT:
                holder.text_amount.setText("Diskon : " + promoList.get(i).getDiscount() + " %");
                break;
            case STATE_INSTALLMENT:
                holder.text_amount.setText("Periode cicilan : " + promoList.get(i).getInstallment() + " " + context.getResources().getString(R.string.month));
                break;
            case STATE_CASH_BACK:
                holder.text_amount.setText("Cashback : " + promoList.get(i).getCashback() + " %");
                break;
            case STATE_SPECIAL_PRICE:
                holder.text_amount.setText("Special Price : IDR " + priceFormat(promoList.get(i).getSpecial_price()) + ",-");
                break;
        }

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDelete.onDeletes(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return promoList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView text_name;
        TextView text_amount;
        ImageButton remove;
        ImageView imageView;

        Holder(@NonNull View itemView) {
            super(itemView);
            text_amount = itemView.findViewById(R.id.installment);
            text_name = itemView.findViewById(R.id.edittext_itemname);
            remove = itemView.findViewById(R.id.deleteItem);
            imageView = itemView.findViewById(R.id.recycler_image_promo);
        }
    }

    private String priceFormat(long totalPrice) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        return formatter.format(totalPrice);
    }
}
