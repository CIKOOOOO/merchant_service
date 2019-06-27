package com.andrew.prototype.Fragment;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.prototype.Activity.MainActivity;
import com.andrew.prototype.Adapter.PromoAdapter;
import com.andrew.prototype.Model.PromoForm;
import com.andrew.prototype.Model.PromoTransaction;
import com.andrew.prototype.R;
import com.andrew.prototype.Utils.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreviewProquest extends Fragment implements View.OnClickListener, MainActivity.onBackPressFragment {

    public static final String PROQUEST_GET_DATA = "PROQUEST_GET_DATA";
    public static final String PROQUEST_GET_LIST = "PROQUEST_GET_LIST";

    private static PromoTransaction promoTransaction;
    private static List<PromoForm> promoTransactionList;

    private View v;
    private Context mContext;
    private FrameLayout frame_ads;
    private DatabaseReference dbRef;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;
    private ProgressDialog progressDialog;

    private Bitmap img_bitmap;
    private String downloadUrl, promoTitle, address, promoDesc, promoDateStart, promoDateEnd, promoTimeStart, promoTimeEnd, promoCategory, phoneNumber, officePhoneNumber, email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_preview_proquest, container, false);
        initVar();
        return v;
    }

    @SuppressLint("SetTextI18n")
    private void initVar() {
        mContext = v.getContext();
        storageRef = storage.getReference();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));

        RecyclerView recyclerView = v.findViewById(R.id.recycler_preview_promo);
        TextView tvDesc = v.findViewById(R.id.tvDesc_Preview_Proquest);
        TextView tvPromoDate = v.findViewById(R.id.tvPromoDate_Preview_Request);
        TextView tvTitle = v.findViewById(R.id.tvTitle_Preview_Request);
        TextView tvDate = v.findViewById(R.id.tvDate_Preview_Request);
        Button btnSend = v.findViewById(R.id.btn_preview_proquest_send);
        Button btnCancel = v.findViewById(R.id.btn_preview_proquest_cancel);
        TextView tvTemplateAds = v.findViewById(R.id.tvTemplateAds_Preview_Proquest);
        TextView tvPhone = v.findViewById(R.id.tvPhoneNumber_Prequest);
        TextView tvEmail = v.findViewById(R.id.tvEmail_Prequest);
        TextView tvOffice = v.findViewById(R.id.tvOfficePhoneNumber_Prequest);
        TextView tvAddress = v.findViewById(R.id.tvAddress_Prequest);

        frame_ads = v.findViewById(R.id.frame_preview_proquest);

        promoTransactionList = new ArrayList<>();

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getParcelable(PROQUEST_GET_DATA) != null) {
                promoTransaction = bundle.getParcelable(PROQUEST_GET_DATA);
                if (promoTransaction != null) {
                    promoCategory = promoTransaction.getPromotionCategory();
                    promoTitle = promoTransaction.getPromotionTitle();
                    promoDesc = promoTransaction.getDescription();
                    promoDateStart = promoTransaction.getDateStart();
                    promoDateEnd = promoTransaction.getDateEnd();
                    promoTimeEnd = promoTransaction.getTimeEnd();
                    promoTimeStart = promoTransaction.getTimeStart();
                    phoneNumber = promoTransaction.getPhoneNumber();
                    officePhoneNumber = promoTransaction.getOfficePhoneNumber();
                    email = promoTransaction.getEmailAddress();
                    address = promoTransaction.getAddress();

                    String pattern = "MMM dd, yyyy  HH:mm";
                    frame_ads.setBackgroundColor(promoTransaction.getAdsTemplate());
                    for (int i = 0; i < Constant.imgTemplateAds.length; i++) {
                        if (promoTransaction.getAdsTemplate() == Constant.imgTemplateAds[i]) {
                            frame_ads.setBackgroundResource(Constant.imgTemplateAds[i]);
                            break;
                        }
                    }
                    tvTemplateAds.setText(promoTransaction.getAdsContent());
                    tvTemplateAds.setTextColor(promoTransaction.getAdsColorContent());
                    tvDesc.setText(promoDesc);
                    tvTitle.setText(promoTitle);
                    tvDate.setText(getTime(pattern) + " WIB");
                    tvAddress.setText(address);

                    if (!promoTransaction.getPhoneNumber().isEmpty()) {
                        v.findViewById(R.id.tv_dummy1).setVisibility(View.VISIBLE);
                        v.findViewById(R.id.tv_dummy0).setVisibility(View.VISIBLE);
                        tvPhone.setVisibility(View.VISIBLE);
                        tvPhone.setText(": " + promoTransaction.getPhoneNumber());
                    }
                    if (!promoTransaction.getOfficePhoneNumber().isEmpty()) {
                        v.findViewById(R.id.tv_dummy2).setVisibility(View.VISIBLE);
                        v.findViewById(R.id.tv_dummy0).setVisibility(View.VISIBLE);
                        tvOffice.setVisibility(View.VISIBLE);
                        tvOffice.setText(": " + promoTransaction.getOfficePhoneNumber());
                    }
                    if (!promoTransaction.getEmailAddress().isEmpty()) {
                        v.findViewById(R.id.tv_dummy0).setVisibility(View.VISIBLE);
                        v.findViewById(R.id.tv_dummy3).setVisibility(View.VISIBLE);
                        tvEmail.setVisibility(View.VISIBLE);
                        tvEmail.setText(": " + promoTransaction.getEmailAddress());
                    }

                    tvPromoDate.setText("Promo hanya berlaku mulai dari " + promoDateStart
                            + " sampai dengan " + promoDateEnd + " (Jam "
                            + promoTimeStart + " - " + promoTimeEnd + ").");
                    if (bundle.getParcelableArrayList(PROQUEST_GET_LIST) != null) {
                        promoTransactionList.addAll(Objects.requireNonNull(bundle.<PromoForm>getParcelableArrayList(PROQUEST_GET_LIST)));
                        PromoAdapter promoAdapter = new PromoAdapter(promoTransaction.getPromotionCategory(), mContext, promoTransactionList, false);
                        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                        recyclerView.setAdapter(promoAdapter);
                    }
                }
            }
        }

        btnCancel.setOnClickListener(this);
        btnSend.setOnClickListener(this);
    }

    private String getTime(String pattern) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(new Date());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_preview_proquest_send:
                img_bitmap = setViewToBitmapImage(frame_ads);
                progressDialog.show();
                writeToDatabase();
                break;
            case R.id.btn_preview_proquest_cancel:
                TabPromoRequest promoRequest = new TabPromoRequest();
                Bundle bundle = new Bundle();

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);

                bundle.putParcelable(PromoRequest.GETTING_DATA, promoTransaction);
                bundle.putParcelableArrayList(PromoRequest.GETTING_DATA_LIST, (ArrayList<? extends Parcelable>) promoTransactionList);
                promoRequest.setArguments(bundle);

                fragmentTransaction.replace(R.id.main_frame, promoRequest);

                fragmentTransaction.commit();
                break;
        }
    }

    private void writeToDatabase() {
        dbRef = FirebaseDatabase.getInstance().getReference(Constant.DB_REFERENCE_TRANSACTION_REQUEST_PROMO);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        img_bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] data = baos.toByteArray();

        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String imgName = "Image-" + n + ".jpeg";
        final int MID = 299;
        final StorageReference childRef = storageRef.child(imgName);

        UploadTask uploadTask = childRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(mContext, "Gagal Submit", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                childRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        downloadUrl = uri.toString();
                        final String TID = dbRef.push().getKey();

                        HashMap<String, String> map = new HashMap<>();
                        map.put("MID", String.valueOf(MID));
                        map.put("TID", TID);
                        map.put("imageURLForAds", downloadUrl);
                        map.put("promotionTitle", promoTitle);
                        map.put("dateStart", promoDateStart);
                        map.put("dateEnd", promoDateEnd);
                        map.put("timeStart", promoTimeStart);
                        map.put("timeEnd", promoTimeEnd);
                        map.put("promotionCategory", promoCategory);
                        map.put("statusChecking", "Not Checking");
                        map.put("isClick", "false");
                        map.put("description", promoDesc);
                        map.put("phoneNumber", phoneNumber);
                        map.put("officePhoneNumber", officePhoneNumber);
                        map.put("emailAddress", email);
                        map.put("dateRequest", getTime("dd/MM/yyyy"));
                        map.put("timeRequest", getTime("HH:mm"));
                        map.put("address", address);

                        dbRef.child(String.valueOf(MID)).child(TID).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                for (final PromoForm promoTransaction1 : promoTransactionList) {
                                    final String PID = dbRef.child(TID).child("product").push().getKey();
                                    final HashMap<String, String> map = new HashMap<>();
                                    map.put("pid", PID);
                                    map.put("productName", promoTransaction1.getStuffName());
                                    if (promoTransaction1.getCashback() != 0)
                                        map.put("productValuePrice", String.valueOf(promoTransaction1.getCashback()));
                                    else if (promoTransaction1.getDiscount() != 0) {
                                        map.put("productValuePrice", String.valueOf(promoTransaction1.getDiscount()));
                                    } else if (promoTransaction1.getInstallment() != 0) {
                                        map.put("productValuePrice", String.valueOf(promoTransaction1.getInstallment()));
                                    } else {
                                        map.put("productValuePrice", String.valueOf(promoTransaction1.getSpecial_price()));
                                    }

                                    if (promoTransaction1.getBitmap() != null) {
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        promoTransaction1.getBitmap().compress(Bitmap.CompressFormat.JPEG, 30, baos);
                                        byte[] data = baos.toByteArray();
                                        final StorageReference childRef = storageRef.child(promoTransaction1.getStuffName());

                                        UploadTask uploadTask = childRef.putBytes(data);

                                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                childRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String downloadURL = uri.toString();
                                                        map.put("urlImageProduct", downloadURL);
                                                        dbRef.child(String.valueOf(MID)).child(TID).child("product").child(PID).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(mContext, "Sukses", Toast.LENGTH_SHORT).show();
                                                                getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.main_frame, new TabPromoRequest()).commit();
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        map.put("urlImageProduct", "null");
                                        dbRef.child(String.valueOf(MID)).child(TID).child("product").child(PID).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                Toast.makeText(mContext, "Sukses", Toast.LENGTH_SHORT).show();
                                                getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.main_frame, new TabPromoRequest()).commit();
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private static Bitmap setViewToBitmapImage(View view) {
        //Define a image_bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the image_bitmap
        return returnedBitmap;
    }

    @Override
    public void onBackPress(boolean check, Context context) {
        TabPromoRequest promoRequest = new TabPromoRequest();
        Bundle bundle = new Bundle();

        AppCompatActivity activity = (AppCompatActivity) context;

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);

        bundle.putParcelable(PromoRequest.GETTING_DATA, promoTransaction);
        bundle.putParcelableArrayList(PromoRequest.GETTING_DATA_LIST, (ArrayList<? extends Parcelable>) promoTransactionList);
        promoRequest.setArguments(bundle);

        fragmentTransaction.replace(R.id.main_frame, promoRequest);

        fragmentTransaction.commit();
    }
}
