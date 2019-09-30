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
import com.andrew.prototype.Model.Forum;
import com.andrew.prototype.Model.ForumThread;
import com.andrew.prototype.R;

import java.util.List;

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.ViewHolder> {
    private List<Forum> trendingList;
    private Context context;

    public interface itemClickListener {
        void onItemClick(int pos, List<Forum> forumThreads);
    }

    public void setTrendingList(List<Forum> trendingList) {
        this.trendingList = trendingList;
    }

    private itemClickListener itemClickListener;

    public TrendingAdapter(Context context, List<Forum> trendingList, itemClickListener itemClickListener) {
        this.trendingList = trendingList;
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_trending, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder v, int i) {
        if (trendingList.size() == 0) {
            v.textView.setText(context.getResources().getString(R.string.find_search));
        } else {
            v.textView.setText(trendingList.get(i).getForum_title());
//            if (check) {
            v.rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    int pos = v.getAdapterPosition();
                    itemClickListener.onItemClick(pos, trendingList);
                }
            });
//            }
        }
    }

    @Override
    public int getItemCount() {
        if (trendingList.size() == 0) return 1;
        else if (trendingList.size() > 5) return 5;
        else return trendingList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        RippleView rippleView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.trending_fire);
            textView = itemView.findViewById(R.id.trending_text);
            rippleView = itemView.findViewById(R.id.ripple_trending);
        }
    }
//    private List<Trending> trending;
//
//    public TrendingAdapter(Context context, List<Trending> trending) {
//        super(context, 0, trending);
//        this.trending = new ArrayList<>(trending);
//        Log.e("HEHE", trending.size() + "");
//    }
//
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.recycler_trending, parent, false);
//        }
////        if (trending.size() > 0) {
//        Trending trendings = getItem(position);
//        TextView productLabel = convertView.findViewById(R.id.trending_text);
////            ImageView imageView = v.findViewById(R.id.trending_fire);
////            if(trending){
////
////            }
//        productLabel.setText(trendings.getTrending_title());
////        }
//
//        return convertView;
//    }
//
//    @NonNull
//    @Override
//    public Filter getFilter() {
//        return nameFilter;
//    }
//
//    private Filter nameFilter = new Filter() {
//        public String convertResultToString(Object resultValue) {
//            return ((Trending) resultValue).getTrending_title();
//        }
//
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            return null;
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint,
//                                      FilterResults results) {
//            clear();
//            List<Trending> suggestions = new ArrayList<>();
//            if (constraint == null) {
//                suggestions.addAll(trending);
//            } else {
//                String filterPattern = constraint.toString().toLowerCase().trim();
//                for (int i = 0; i < trending.size(); i++) {
//                    if (trending.get(i).getTrending_title().trim().toLowerCase().startsWith(filterPattern)) {
//                        suggestions.add(trending.get(i));
//                    }
//                }
//            }
//            addAll(suggestions);
//            notifyDataSetChanged();
//        }
//    };
}
