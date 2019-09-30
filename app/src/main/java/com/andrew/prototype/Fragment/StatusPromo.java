package com.andrew.prototype.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrew.prototype.Adapter.PromoAdapter;
import com.andrew.prototype.Adapter.PromoStatusAdapter;
import com.andrew.prototype.Adapter.StatusPromoAdapter;
import com.andrew.prototype.Model.Product;
import com.andrew.prototype.Model.PromoTransaction;
import com.andrew.prototype.R;
import com.andrew.prototype.Utils.Constant;
import com.andrew.prototype.Utils.PrefConfig;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatusPromo extends Fragment implements StatusPromoAdapter.statusOnClick {
    public static BottomSheetBehavior bottomSheetBehavior;
    public static boolean isBottomSheetVisible;

    private View v;
    private Context mContext;
    private StatusPromoAdapter statusPromoAdapter;
    private PrefConfig prefConfig;

    private HashMap<String, List<Product>> map;
    private List<PromoTransaction> promoTransactions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_status_promo, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        isBottomSheetVisible = false;
        mContext = v.getContext();
        prefConfig = new PrefConfig(mContext);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constant.DB_REFERENCE_TRANSACTION_REQUEST_PROMO + "/" + prefConfig.getMID());

        RecyclerView recyclerView = v.findViewById(R.id.recycler_status_promo);

        NestedScrollView nestedScrollView = v.findViewById(R.id.nestedScrollView_BottomSheet);

        bottomSheetBehavior = BottomSheetBehavior.from(nestedScrollView);

        promoTransactions = new ArrayList<>();
        map = new HashMap<>();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        statusPromoAdapter = new StatusPromoAdapter(mContext, promoTransactions, map, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(statusPromoAdapter);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_HIDDEN || i == BottomSheetBehavior.STATE_COLLAPSED) {
                    isBottomSheetVisible = false;
                } else {
                    isBottomSheetVisible = true;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        databaseReference.addValueEventListener(valueEventListener);
        databaseReference.addChildEventListener(childEventListener);
    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot s, @Nullable String ss) {
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String TID = dataSnapshot.child("tid").getValue().toString();
            for (int i = 0; i < promoTransactions.size(); i++) {
                if (promoTransactions.get(i).getTid().equals(TID)) {
                    statusPromoAdapter.setStatusPromo(promoTransactions.get(i), i);
//                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
//                    NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "channel")
//                            .setSmallIcon(R.drawable.ic_bca)
//                            .setContentTitle("Merchant Service BCA")
//                            .setContentText("Status permohonan anda telah berubah")
//                            .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_bca))
//                            .setPriority(NotificationManager.IMPORTANCE_HIGH);
//                    notificationManager.notify(0, builder.build());
                    break;
                }
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            promoTransactions.clear();
            map.clear();
            statusPromoAdapter.setList(promoTransactions, map);
            statusPromoAdapter.notifyDataSetChanged();
            for (DataSnapshot s : dataSnapshot.getChildren()) {
                // HERE TO GET ALL DATA FROM TRANSACTION
                String TID = s.child("tid").getValue().toString();
                String dateEnd = s.child("dateEnd").getValue().toString();
                String dateStart = s.child("dateStart").getValue().toString();
                String desc = s.child("description").getValue().toString();
                String imageURL = s.child("imageURLForAds").getValue().toString();
                String promoCategory = s.child("promotionCategory").getValue().toString();
                String promoTitle = s.child("promotionTitle").getValue().toString();
                String timeEnd = s.child("timeEnd").getValue().toString();
                String timeStart = s.child("timeStart").getValue().toString();
                String dateRequest = s.child("dateRequest").getValue().toString();
                String emailAddress = s.child("emailAddress").getValue().toString();
                String officePhoneNumber = s.child("officePhoneNumber").getValue().toString();
                String phoneNumber = s.child("phoneNumber").getValue().toString();
                String statusChecking = s.child("statusChecking").getValue().toString();
                String timeRequest = s.child("timeRequest").getValue().toString();
                String address = s.child("address").getValue().toString();

                PromoTransaction promoTransaction = new PromoTransaction(TID, imageURL, promoTitle
                        , timeRequest, dateRequest, dateStart, dateEnd, timeStart
                        , timeEnd, promoCategory, desc, statusChecking, phoneNumber, officePhoneNumber, emailAddress, address);
                promoTransactions.add(promoTransaction);

                List<Product> products = new ArrayList<>();

                for (DataSnapshot ds : s.child("product").getChildren()) {
                    // HERE TO GET PRODUCT DATA FROM TRANSACTION
//                            Log.i("asd",ds.getValue().toString());
//                            map.put("productName", ds.child("productName").getValue().toString());
                    String PID = ds.child("pid").getValue().toString();
                    String productName = ds.child("productName").getValue().toString();
                    String productValue = ds.child("productValuePrice").getValue().toString();
                    String URL = ds.child("urlImageProduct").getValue().toString();
                    products.add(new Product(productName, PID, URL, Integer.valueOf(productValue)));
                }

                map.put(TID, products);
            }
            statusPromoAdapter.setList(promoTransactions, map);
            statusPromoAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public void onClick(int pos) {
        isBottomSheetVisible = true;
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        TextView text_title = v.findViewById(R.id.tvPromotionTitle_RequestStatus);
        TextView text_phone_number = v.findViewById(R.id.tvPhoneNumber_RequestStatus);
        TextView text_office_phone_number = v.findViewById(R.id.tvOfficeNumber_RequestStatus);
        TextView text_email = v.findViewById(R.id.tvEmail_RequestStatus);
        TextView text_start_date = v.findViewById(R.id.tvStartDate_StatusRequest);
        TextView text_end_date = v.findViewById(R.id.tvEndDate_StatusRequest);
        TextView text_start_time = v.findViewById(R.id.tvStartTime_StatusRequest);
        TextView text_end_time = v.findViewById(R.id.tvEndTime_StatusRequest);
        TextView text_promo_category = v.findViewById(R.id.tvPromoCategory_StatusRequest);
        TextView text_address = v.findViewById(R.id.tvAddress_Request_Status);
        TextView text_description = v.findViewById(R.id.tvDescription_Request_Status);
        TextView text_status = v.findViewById(R.id.tvStatus_RequestStatus);
        ImageView image_ads = v.findViewById(R.id.image_ads_request_status);
        RecyclerView recyclerView = v.findViewById(R.id.recycler_request_status);

        PromoTransaction promoTransaction = promoTransactions.get(pos);

        text_title.setText(promoTransaction.getPromotionTitle());

//        if (promoTransaction.getPhoneNumber().isEmpty())
//            text_phone_number.setText(" : -");
//        else
        text_phone_number.setText(" : 0" + promoTransaction.getPhoneNumber());

//        if (promoTransaction.getOfficePhoneNumber().isEmpty())
//            text_office_phone_number.setText(" : -");
//        else
        text_office_phone_number.setText(" : " + promoTransaction.getOfficePhoneNumber());

//        if (promoTransaction.getEmailAddress().isEmpty())
//            text_email.setText(" : -");
//        else
        text_email.setText(" : " + promoTransaction.getEmailAddress());

        text_start_date.setText(" : " + promoTransaction.getDateStart());
        text_end_date.setText(" : " + promoTransaction.getDateEnd());
        text_start_time.setText(" : " + promoTransaction.getTimeStart());
        text_end_time.setText(" : " + promoTransaction.getTimeEnd());

        switch (promoTransaction.getStatusChecking()) {
            case "accepted":
                text_status.setText(" Diterima");
                text_status.setTextColor(mContext.getResources().getColor(R.color.malachite2_palette));
                break;
            case "not-accepted":
                text_status.setText(" Ditolak");
                text_status.setTextColor(mContext.getResources().getColor(R.color.thunderbird_palette));
                break;
            default:
                text_status.setText(" Sedang diproses");
                text_status.setTextColor(mContext.getResources().getColor(R.color.fuel_yellow_palette));
                break;
        }

        switch (promoTransaction.getPromotionCategory()) {
            case PromoAdapter.STATE_CASH_BACK:
                text_promo_category.setText(" : Cashback");
                break;
            case PromoAdapter.STATE_DISCOUNT:
                text_promo_category.setText(" : Diskon");
                break;
            case PromoAdapter.STATE_INSTALLMENT:
                text_promo_category.setText(" : Cicilan");
                break;
            case PromoAdapter.STATE_SPECIAL_PRICE:
                text_promo_category.setText(" : Special Price");
                break;
        }
        text_address.setText(promoTransaction.getAddress());
        text_description.setText(promoTransaction.getDescription());
        Picasso.get().load(promoTransaction.getImageURLForAds()).into(image_ads);

        List<Product> products = new ArrayList<>();
        if (map.get(promoTransaction.getTid()) != null)
            products.addAll(Objects.requireNonNull(map.get(promoTransaction.getTid())));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(new PromoStatusAdapter(mContext, products, promoTransaction.getPromotionCategory()));
    }
}
