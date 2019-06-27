package com.andrew.prototype.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrew.prototype.Model.Store;
import com.andrew.prototype.Model.TransactionStore;
import com.andrew.prototype.R;
import com.andrew.prototype.Utils.Constant;
import com.andrew.prototype.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.Holder> {

    private Context mContext;
    private List<Store> storeList;

    public HomeAdapter(Context mContext, List<Store> storeList) {
        this.mContext = mContext;
        this.storeList = storeList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_store, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        List<TransactionStore> transactions = new ArrayList<>();
        Store store = storeList.get(i);
        holder.transaction.setText(store.getAmount_transaction() + " Transaksi");
        Picasso.get().load(store.getStore_image()).into(holder.imageView);
        holder.earning.setText("IDR " + Utils.priceFormat(store.getEarning()));
        holder.store.setText(store.getName());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        transactions.add(new TransactionStore("12345678", "21 Feb 2019(13.45)", R.drawable.mastercard_logo, 4650000));
        transactions.add(new TransactionStore("12345678", "21 Feb 2019(13.45)", R.drawable.visa_logo, 4650000));
        transactions.add(new TransactionStore("12345678", "21 Feb 2019(13.45)", R.drawable.ic_bca, 4650000));
        holder.recyclerView.setAdapter(new TransactionStoreAdapter(mContext, transactions));
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        ImageView imageView;
        TextView store, earning, transaction;

        public Holder(@NonNull View v) {
            super(v);
            recyclerView = v.findViewById(R.id.recycler_bank_store);
            imageView = v.findViewById(R.id.recycler_img_store);
            store = v.findViewById(R.id.recycler_text_name_store);
            earning = v.findViewById(R.id.recycler_text_earning_store);
            transaction = v.findViewById(R.id.recycler_text_transaction_store);
        }
    }
}
