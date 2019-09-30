package com.andrew.prototype.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrew.prototype.Model.TransactionStore;
import com.andrew.prototype.R;
import com.andrew.prototype.Utils.DecodeBitmap;
import com.andrew.prototype.Utils.Utils;

import java.util.List;

public class TransactionStoreAdapter extends RecyclerView.Adapter<TransactionStoreAdapter.Holder> {

    private Context mContext;
    private List<TransactionStore> transactions;

    public TransactionStoreAdapter(Context mContext, List<TransactionStore> transactions) {
        this.mContext = mContext;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_transaction_store, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        TransactionStore transaction = transactions.get(i);
        holder.tid.setText("#" + transaction.getTid());
        holder.date.setText(transaction.getDate());
        holder.earning.setText("IDR " + Utils.priceFormat(transaction.getEarning()));
        DecodeBitmap.setScaledImageView(holder.bank_logo, transaction.getBank_logo(), mContext);
//        Picasso.get().load(transaction.getBank_logo()).into(holder.bank_logo);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView bank_logo;
        TextView earning, date, tid;

        public Holder(@NonNull View v) {
            super(v);
            bank_logo = v.findViewById(R.id.recycler_image_bank_logo);
            earning = v.findViewById(R.id.recycler_text_earning_bank);
            date = v.findViewById(R.id.recycler_text_date_transaction_store);
            tid = v.findViewById(R.id.recycler_tid_transaction_store);
        }
    }
}
