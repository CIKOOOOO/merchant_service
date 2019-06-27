package com.andrew.prototype.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.prototype.R;

import java.util.List;

public class PageNumberAdapter extends RecyclerView.Adapter<PageNumberAdapter.Holder> {
    private static final int NUMBER_BEFORE_AFTER = 2;
    private static final int MAX_THREAD = 5;

    private Context context;
    private int pageList;
    private int page_active;

    public int getPageList() {
        return pageList;
    }

    public int getPage_active() {
        return page_active;
    }

    public void setPageList(int pageList) {
        this.pageList = pageList;
        notifyDataSetChanged();
    }

    public PageNumberAdapter(Context context, int pageList, pageNumber pageNumber) {
        this.context = context;
        this.pageNumber = pageNumber;
        this.pageList = pageList;
        page_active = 0;
    }

    public interface pageNumber {
        void onClick(int pos);
    }

    private pageNumber pageNumber;

    public void setPage_active(int page_active) {
        this.page_active = page_active;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_page_number, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int i) {
        if (page_active >= pageList - NUMBER_BEFORE_AFTER && i >= pageList - MAX_THREAD) {
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText(String.valueOf(i + 1));
        } else if (page_active > NUMBER_BEFORE_AFTER && i >= page_active - NUMBER_BEFORE_AFTER
                && i <= page_active + NUMBER_BEFORE_AFTER) {
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText(String.valueOf(i + 1));
        } else if (page_active >= 0 && page_active < MAX_THREAD && i < MAX_THREAD
                && i >= page_active - NUMBER_BEFORE_AFTER) {
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText(String.valueOf(i + 1));
        } else {
            holder.textView.setVisibility(View.GONE);
            holder.textView.setText(String.valueOf(i + 1));
        }

        if (i == page_active) {
            holder.textView.setBackground(context.getResources().getDrawable(R.drawable.background_circle));
            holder.textView.setTextColor(context.getResources().getColor(R.color.toolbar_base));
        } else {
            holder.textView.setBackground(context.getResources().getDrawable(android.R.color.transparent));
            holder.textView.setTextColor(context.getResources().getColor(R.color.white_color));
        }

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageNumber.onClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return pageList;
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView textView;

        Holder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.recycler_page);
        }
    }
}
