package com.andrew.prototype.Fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.andrew.prototype.Activity.MainActivity;
import com.andrew.prototype.Adapter.PromoAdapter;
import com.andrew.prototype.Adapter.TemplateAds;
import com.andrew.prototype.Model.PromoForm;
import com.andrew.prototype.Model.PromoTransaction;
import com.andrew.prototype.R;
import com.andrew.prototype.Sqlite.PromoSQLite;
import com.andrew.prototype.Sqlite.PromoStuffSQLite;
import com.andrew.prototype.Utils.Constant;
import com.andrew.prototype.Utils.DecodeBitmap;
import com.andrew.prototype.Utils.PrefConfig;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class PromoRequest extends Fragment implements View.OnClickListener, TemplateAds.onClick, View.OnTouchListener, OnMapReadyCallback, MainActivity.onBackPressFragment, PromoAdapter.onDelete {

    public static final String GETTING_DATA = "GETTING_DATA";
    public static final String GETTING_DATA_LIST = "GETTING_DATA_LIST";

    private static final int INSTALLMENT_PERIOD[] = {3, 6, 12, 24};
    private static final int PERMISSION_MAPS = 107;

    private static EditText txt_tgl_start, txt_jam_start, txt_tgl_end, txt_jam_end, etDescription, promotionTitle, etStuffName, etSpecialPrice, etInputAmount, etTemplateAds, etPhone, etEmail, etOfficePhone, etAddress;
    private static FrameLayout frame_template_ads;

    private static PrefConfig prefConfig;
    private static PromoAdapter promoAdapter;
    private static PromoForm promoForm;

    private static List<PromoForm> promoFormList;

    private static int INSTALLMENT_PERIOD_POSITION;
    private static int adsTemplate, adsColorContent;
    private static String STATES;

    private Context mContext;
    private Activity mActivity;
    private View v;
    private Calendar myCalendar;
    private ImageView discountCheckButton, installmentChecklistButton, cashbackChecklistButton, specialPromoCheckButton;
    private TextView tvStatusCategory, tvSign, tvSignError, tvError_Empty_Stuff;
    private LinearLayout linear_special_price, linear_fill_form, linear_input_number;
    private ImageButton img_open_gallery;
    private NestedScrollView nestedScrollView;
    private GoogleMap googleMap;
    private MapView mapView;
    private GoogleMapOptions googleMapOptions;
    private GoogleApiClient googleApiClient;

    private boolean isFormFillAvailable;
    private Bitmap image_bitmap;

    private static PromoSQLite promoSQLite;
    private static PromoStuffSQLite promoStuffSQLite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_promo_request, container, false);
        initVar();
//        mapView = v.findViewById(R.id.map_view_promo_request);
//        mapView.onCreate(savedInstanceState);
//        mapView.onResume();
//        try {
//            MapsInitializer.initialize(mContext);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        mapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap map) {
//                googleMap = map;
////                if (ActivityCompat
////                        .checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
////                        && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_MAPS);
////                    return;
////                }
////        googleMap.setMyLocationEnabled(true);
//                LatLng sydney = new LatLng(-34, 151);
//                googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));
//
//                // For zooming automatically to the location of the marker
//                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
//                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//            }
//        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
    }

    private void initVar() {
        INSTALLMENT_PERIOD_POSITION = 0;
        isFormFillAvailable = false;
        STATES = PromoAdapter.STATE_DISCOUNT;
        mContext = v.getContext();
        promoForm = new PromoForm();
        promoSQLite = new PromoSQLite(mContext);
        promoStuffSQLite = new PromoStuffSQLite(mContext);
        prefConfig = new PrefConfig(mContext);
        hideSoftKeyboardFromRoot(v);

        Button btnDiscount = v.findViewById(R.id.discount_promo_request);
        Button btnInstallment = v.findViewById(R.id.installment_promo_request);
        Button btnSpecialPrice = v.findViewById(R.id.special_price_promo_request);
        Button btnCash_back = v.findViewById(R.id.cashback_promo_request);
        Button btnCancel = v.findViewById(R.id.btnCancel_Promo);
        Button btnSubmit = v.findViewById(R.id.btnSubmit_Promo);
        Button increment = v.findViewById(R.id.btn_increment_promo);
        Button decrement = v.findViewById(R.id.btn_decrement_promo);
        Button tester = v.findViewById(R.id.tester_button_promo);
        Button promo_submit = v.findViewById(R.id.btn_promo_submit);
        ImageButton imageButton = v.findViewById(R.id.addItem_PromoRequest);
        RecyclerView recyclerView = v.findViewById(R.id.recycler_promo_request);
        RippleView btnColorPicker = v.findViewById(R.id.ripple_color_picker);
        RecyclerView recycler_template_ads = v.findViewById(R.id.recycler_template_ads);

//        mapView = v.findViewById(R.id.map_view_promo_request);
        img_open_gallery = v.findViewById(R.id.img_btn_open_gallery);
        promotionTitle = v.findViewById(R.id.etPromotionTitle_PromoRequest);
        etDescription = v.findViewById(R.id.editDesc);
        linear_fill_form = v.findViewById(R.id.ll_parent_form_promo);
        linear_special_price = v.findViewById(R.id.ll_special_price_promo);
        linear_input_number = v.findViewById(R.id.ll_number_input);
        nestedScrollView = v.findViewById(R.id.nestedScrollView_PromoRequest);
        etStuffName = v.findViewById(R.id.etStuffName);
        etSpecialPrice = v.findViewById(R.id.etSpecialPrice_Promo);
        etInputAmount = v.findViewById(R.id.etInputNumber_Promo);
        etTemplateAds = v.findViewById(R.id.etTemplateAds_Promo);
        etPhone = v.findViewById(R.id.etPhoneNumber_PromoRequest);
        etEmail = v.findViewById(R.id.etEmail_PromoRequest);
        etOfficePhone = v.findViewById(R.id.etOfficePhone_PromoRequest);
        etAddress = v.findViewById(R.id.editAddress);

        txt_tgl_start = v.findViewById(R.id.txt_start_date);
        txt_jam_start = v.findViewById(R.id.txt_start_time);
        txt_tgl_end = v.findViewById(R.id.txt_end_date);
        txt_jam_end = v.findViewById(R.id.txt_end_time);
        tvStatusCategory = v.findViewById(R.id.tvStatusCategory);
        tvSignError = v.findViewById(R.id.show_error_title_promo);
        frame_template_ads = v.findViewById(R.id.frame_layout_template_ads);
        tvSign = v.findViewById(R.id.tvSign);
        tvError_Empty_Stuff = v.findViewById(R.id.show_error_number_of_stuff);
        discountCheckButton = v.findViewById(R.id.check1);
        installmentChecklistButton = v.findViewById(R.id.check2);
        cashbackChecklistButton = v.findViewById(R.id.check3);
        specialPromoCheckButton = v.findViewById(R.id.check4);

        myCalendar = Calendar.getInstance();
        promoFormList = new ArrayList<>();

        promoAdapter = new PromoAdapter(mContext, promoFormList, this);
        TemplateAds templateAds = new TemplateAds(mContext, Constant.iconTemplateAds, this);

        recycler_template_ads.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recycler_template_ads.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);

        recycler_template_ads.setAdapter(templateAds);
        recyclerView.setAdapter(promoAdapter);

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getParcelable(GETTING_DATA) != null) {
                PromoTransaction promoTransaction = bundle.getParcelable(GETTING_DATA);
                if (promoTransaction != null) {
                    frame_template_ads.setBackgroundColor(promoTransaction.getAdsTemplate());
                    templateAds.setPosition(Constant.imgTemplateAds.length);
                    for (int i = 0; i < Constant.imgTemplateAds.length; i++) {
                        if (promoTransaction.getAdsTemplate() == Constant.imgTemplateAds[i]) {
                            frame_template_ads.setBackgroundResource(Constant.imgTemplateAds[i]);
                            templateAds.setPosition(i);
                            break;
                        }
                    }
                    etTemplateAds.setText(promoTransaction.getAdsContent());
                    adsColorContent = promoTransaction.getAdsColorContent();
                    promotionTitle.setText(promoTransaction.getPromotionTitle());
                    txt_tgl_start.setText(promoTransaction.getDateStart());
                    txt_tgl_end.setText(promoTransaction.getDateEnd());
                    txt_jam_start.setText(promoTransaction.getTimeStart());
                    txt_jam_end.setText(promoTransaction.getTimeEnd());
                    etDescription.setText(promoTransaction.getDescription());
                    etAddress.setText(promoTransaction.getAddress());
                    etEmail.setText(promoTransaction.getEmailAddress());
                    etPhone.setText(promoTransaction.getPhoneNumber());
                    etOfficePhone.setText(promoTransaction.getOfficePhoneNumber());
                    setCheckState(promoTransaction.getPromotionCategory());
                    if (bundle.getParcelableArrayList(GETTING_DATA_LIST) != null) {
                        promoFormList.addAll(Objects.requireNonNull(bundle.<PromoForm>getParcelableArrayList(GETTING_DATA_LIST)));
                        promoAdapter.setPromoList(promoTransaction.getPromotionCategory(), promoFormList);
                    }
                }
            }
        } else {
            adsTemplate = Constant.imgTemplateAds[0];
            adsColorContent = mContext.getResources().getColor(R.color.blackPalette);
        }
        etTemplateAds.setTextColor(adsColorContent);

        imageButton.setOnClickListener(this);
        promo_submit.setOnClickListener(this);
        btnDiscount.setOnClickListener(this);
        btnInstallment.setOnClickListener(this);
        btnSpecialPrice.setOnClickListener(this);
        btnCash_back.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        increment.setOnClickListener(this);
        decrement.setOnClickListener(this);
        btnColorPicker.setOnClickListener(this);
        etAddress.setOnTouchListener(this);
        etDescription.setOnTouchListener(this);
        img_open_gallery.setOnClickListener(this);
        txt_tgl_start.setOnClickListener(this);
        txt_tgl_end.setOnClickListener(this);
        txt_jam_start.setOnClickListener(this);
        txt_jam_end.setOnClickListener(this);
        tester.setOnClickListener(this);

        Cursor cursor = promoSQLite.getData(prefConfig.getID());
        Cursor cursor1 = promoStuffSQLite.getData(prefConfig.getID());
        while (cursor.moveToNext()) {
            if (cursor.getInt(0) == prefConfig.getID()) {
                frame_template_ads.setBackgroundColor(cursor.getInt(1));
                etTemplateAds.setText(cursor.getString(2));
                promotionTitle.setText(cursor.getString(3));
                etEmail.setText(cursor.getString(4));
                etPhone.setText(cursor.getString(5));
                etOfficePhone.setText(cursor.getString(6));
                txt_tgl_start.setText(cursor.getString(7));
                txt_tgl_end.setText(cursor.getString(8));
                txt_jam_start.setText(cursor.getString(9));
                txt_jam_end.setText(cursor.getString(10));
                setCheckState(cursor.getString(11));
                etAddress.setText(cursor.getString(12));
                etDescription.setText(cursor.getString(13));

                templateAds.setPosition(Constant.imgTemplateAds.length);
                for (int i = 0; i < Constant.imgTemplateAds.length; i++) {
                    if (cursor.getInt(1) == Constant.imgTemplateAds[i]) {
                        frame_template_ads.setBackgroundResource(Constant.imgTemplateAds[i]);
                        templateAds.setPosition(i);
                        break;
                    }
                }
            }
        }

        while (cursor1.moveToNext()) {
            if (cursor1.getInt(1) == prefConfig.getID()) {
                String name = cursor1.getString(2);
                byte[] image = cursor1.getBlob(3);
                String value = cursor1.getString(4);
                promoForm = new PromoForm(name);
                if (image != null) {
                    promoForm.setBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
                }
                switch (STATES) {
                    case PromoAdapter.STATE_DISCOUNT:
                        promoForm.setDiscount(Integer.parseInt(value));
                        break;
                    case PromoAdapter.STATE_INSTALLMENT:
                        promoForm.setInstallment(Integer.parseInt(value));
                        break;
                    case PromoAdapter.STATE_CASH_BACK:
                        promoForm.setCashback(Integer.parseInt(value));
                        break;
                    case PromoAdapter.STATE_SPECIAL_PRICE:
                        promoForm.setSpecial_price(Long.parseLong(value));
                        break;
                }
                promoFormList.add(promoForm);
                promoAdapter.setPromoList(STATES, promoFormList);
            }
        }
    }

    private void updateDateLabel(EditText editText) {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tester_button_promo:
                etTemplateAds.setText("Promo 1 rupiah");
                promotionTitle.setText("Promo 1 rupiah");
                txt_tgl_start.setText("24/06/2029");
                txt_tgl_end.setText("30/06/2029");
                txt_jam_start.setText("12:00");
                txt_jam_end.setText("12:00");
                etDescription.setText("Deskripsi");
                etAddress.setText("Alamat");
                etEmail.setText("clarissa.aristania001@gmail.com");
                etPhone.setText("08971056622");
                break;
            case R.id.addItem_PromoRequest:
                isFormFillAvailable = true;
                setVisibility(STATES);
                nestedScrollView.postOnAnimationDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nestedScrollView.smoothScrollTo(0, linear_fill_form.getTop());
                    }
                }, 200);
                break;
            case R.id.btn_promo_submit:
                String phoneRegex = "^[0][8][1-9]{2}[0-9]{6,9}$";
                TextView tvError = v.findViewById(R.id.show_error_title);
                TextView tvError_desc = v.findViewById(R.id.show_error_description_promo_request);
                TextView tvError_address = v.findViewById(R.id.show_error_address_promo_request);
                String description = etDescription.getText().toString();
                String promoTitle = promotionTitle.getText().toString();
                String templateAds = etTemplateAds.getText().toString();
                String phoneNumber = etPhone.getText().toString().trim();
                String officePhone = etOfficePhone.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String address = etAddress.getText().toString().trim();

                tvError.setVisibility(View.GONE);
                tvError_desc.setVisibility(View.GONE);
                tvError_address.setVisibility(View.GONE);
                tvError_Empty_Stuff.setVisibility(View.GONE);

                if (templateAds.trim().isEmpty()) {
                    etTemplateAds.requestFocus();
                    tvError.setText(mContext.getResources().getString(R.string.not_empty_this_field));
                    tvError.setVisibility(View.VISIBLE);
                } else if (promoTitle.trim().isEmpty()) {
                    promotionTitle.requestFocus();
                    promotionTitle.setError(mContext.getResources().getString(R.string.not_empty_this_field));
                } else if (email.isEmpty()) {
                    etEmail.requestFocus();
                    etEmail.setError(mContext.getResources().getString(R.string.not_empty_this_field));
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etEmail.requestFocus();
                    etEmail.setError(mContext.getResources().getString(R.string.need_valid_email_address));
                } else if (phoneNumber.isEmpty()) {
                    etPhone.requestFocus();
                    etPhone.setError(mContext.getResources().getString(R.string.not_empty_this_field));
                } else if (!phoneNumber.matches(phoneRegex)) {
                    etPhone.requestFocus();
                    etPhone.setError(mContext.getResources().getString(R.string.need_valid_phone_number));
                } else if (!isTimeValidate()) {
                    return;
                } else if (promoAdapter.getItemCount() < 1) {
                    tvError_Empty_Stuff.setVisibility(View.VISIBLE);
                } else if (address.isEmpty()) {
                    etAddress.requestFocus();
                    tvError_address.setVisibility(View.VISIBLE);
                } else if (description.isEmpty()) {
                    etDescription.requestFocus();
                    tvError_desc.setVisibility(View.VISIBLE);
                } else {
//                etTemplateAds.clearComposingText();
//                etTemplateAds.setCursorVisible(false);
                    promoSQLite.deleteAll(prefConfig.getID());
                    promoStuffSQLite.deleteAll(prefConfig.getID());
                    prefConfig.insertId(-1);
                    PromoTransaction promoTransaction =
                            new PromoTransaction(promoTitle, txt_tgl_start.getText().toString()
                                    , txt_tgl_end.getText().toString(), txt_jam_start.getText().toString(), txt_jam_end.getText().toString()
                                    , STATES, description, templateAds, phoneNumber, officePhone, email, adsTemplate, adsColorContent, address);

                    PreviewProquest previewProquest = new PreviewProquest();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(PreviewProquest.PROQUEST_GET_DATA, promoTransaction);
                    bundle.putParcelableArrayList(PreviewProquest.PROQUEST_GET_LIST, (ArrayList<? extends Parcelable>) promoFormList);

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    fragmentTransaction.replace(R.id.main_frame, previewProquest);

                    previewProquest.setArguments(bundle);
                    fragmentTransaction.commit();
                }
                break;
            case R.id.discount_promo_request:
                if (!STATES.equals(PromoAdapter.STATE_DISCOUNT)) {
                    if (promoFormList.size() > 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage(mContext.getResources().getString(R.string.change_category))
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        setCheckState(PromoAdapter.STATE_DISCOUNT);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).show();
                    } else {
                        setCheckState(PromoAdapter.STATE_DISCOUNT);
                    }
                }
                break;
            case R.id.special_price_promo_request:
                if (!STATES.equals(PromoAdapter.STATE_SPECIAL_PRICE)) {
                    if (promoFormList.size() > 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage(mContext.getResources().getString(R.string.change_category))
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        setCheckState(PromoAdapter.STATE_SPECIAL_PRICE);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).show();
                    } else {
                        setCheckState(PromoAdapter.STATE_SPECIAL_PRICE);
                    }
                }
                break;
            case R.id.cashback_promo_request:
                if (!STATES.equals(PromoAdapter.STATE_CASH_BACK)) {
                    if (promoFormList.size() > 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage(mContext.getResources().getString(R.string.change_category))
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        setCheckState(PromoAdapter.STATE_CASH_BACK);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).show();
                    } else {
                        setCheckState(PromoAdapter.STATE_CASH_BACK);
                    }
                }
                break;
            case R.id.installment_promo_request:
                if (!STATES.equals(PromoAdapter.STATE_INSTALLMENT)) {
                    if (promoFormList.size() > 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage(mContext.getResources().getString(R.string.change_category))
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        setCheckState(PromoAdapter.STATE_INSTALLMENT);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).show();
                    } else {
                        setCheckState(PromoAdapter.STATE_INSTALLMENT);
                    }
                }
                break;
            case R.id.txt_start_date:
                getDate(txt_tgl_start);
                break;
            case R.id.txt_end_date:
                getDate(txt_tgl_end);
                break;
            case R.id.txt_start_time:
                setTime(txt_jam_start);
                break;
            case R.id.txt_end_time:
                setTime(txt_jam_end);
                break;
            case R.id.btnSubmit_Promo:
                String stuffName = etStuffName.getText().toString().trim();
                String amountRegex = "^[1-9][0-9]*$";
                tvSignError.setVisibility(View.GONE);
                if (stuffName.isEmpty()) {
                    etStuffName.setError(mContext.getResources().getString(R.string.not_empty_stuff_name));
                    return;
                }
                promoForm = new PromoForm(stuffName);
                switch (STATES) {
                    case PromoAdapter.STATE_CASH_BACK:
                        String amount_cash_back = etInputAmount.getText().toString().trim();
                        if (!amount_cash_back.matches(amountRegex) || amount_cash_back.isEmpty()) {
                            tvSignError.setVisibility(View.VISIBLE);
                        } else if (Integer.valueOf(amount_cash_back) > 100) {
                            tvSignError.setVisibility(View.VISIBLE);
                        } else {
                            promoForm.setCashback(Integer.valueOf(amount_cash_back));
                            addPromoFormList(promoForm, PromoAdapter.STATE_CASH_BACK);
                        }
                        break;
                    case PromoAdapter.STATE_DISCOUNT:
                        String amount_discount = etInputAmount.getText().toString().trim();
                        if (!amount_discount.matches(amountRegex) || amount_discount.isEmpty()) {
                            tvSignError.setVisibility(View.VISIBLE);
                        } else if (Integer.valueOf(amount_discount) > 100) {
                            tvSignError.setVisibility(View.VISIBLE);
                        } else {
                            promoForm.setDiscount(Integer.valueOf(amount_discount));
                            addPromoFormList(promoForm, PromoAdapter.STATE_DISCOUNT);
                        }
                        break;
                    case PromoAdapter.STATE_INSTALLMENT:
                        String amount_installment = etInputAmount.getText().toString().trim();
                        if (!amount_installment.matches(amountRegex) || amount_installment.isEmpty()) {
                            tvSignError.setVisibility(View.VISIBLE);
                        } else {
                            promoForm.setInstallment(Integer.valueOf(amount_installment));
                            addPromoFormList(promoForm, PromoAdapter.STATE_INSTALLMENT);
                        }
                        break;
                    case PromoAdapter.STATE_SPECIAL_PRICE:
                        String specialPrice = etSpecialPrice.getText().toString().trim();
                        if (specialPrice.isEmpty()) {
                            etSpecialPrice.setError(mContext.getResources().getString(R.string.not_empty_this_field));
                        } else if (!specialPrice.matches(amountRegex)) {
                            etSpecialPrice.setError(mContext.getResources().getString(R.string.need_valid_price));
                        } else if (Long.valueOf(specialPrice) < 1 || Long.valueOf(specialPrice) > Long.MAX_VALUE) {
                            etSpecialPrice.setError(mContext.getResources().getString(R.string.need_valid_price));
                        } else {
                            promoForm.setSpecial_price(Long.valueOf(specialPrice));
                            addPromoFormList(promoForm, PromoAdapter.STATE_SPECIAL_PRICE);
                        }
                        break;
                }
                break;
            case R.id.btnCancel_Promo:
                linear_fill_form.setVisibility(View.GONE);
                break;
            case R.id.btn_increment_promo:
                switch (STATES) {
                    case PromoAdapter.STATE_CASH_BACK:
                        if (Integer.valueOf(etInputAmount.getText().toString()) < 100) {
                            etInputAmount.setText(Integer.valueOf(etInputAmount.getText().toString()) + 5 + "");
                        }
                        break;
                    case PromoAdapter.STATE_DISCOUNT:
                        if (Integer.valueOf(etInputAmount.getText().toString()) < 100) {
                            etInputAmount.setText(Integer.valueOf(etInputAmount.getText().toString()) + 5 + "");
                        }
                        break;
                    case PromoAdapter.STATE_INSTALLMENT:
                        if (INSTALLMENT_PERIOD_POSITION != 3) {
                            etInputAmount.setText(INSTALLMENT_PERIOD[++INSTALLMENT_PERIOD_POSITION] + "");
                        }
                        break;
                }
                break;
            case R.id.btn_decrement_promo:
                switch (STATES) {
                    case PromoAdapter.STATE_CASH_BACK:
                        if (Integer.valueOf(etInputAmount.getText().toString()) > 0) {
                            etInputAmount.setText(Integer.valueOf(etInputAmount.getText().toString()) - 5 + "");
                        }
                        break;
                    case PromoAdapter.STATE_DISCOUNT:
                        if (Integer.valueOf(etInputAmount.getText().toString()) > 0) {
                            etInputAmount.setText(Integer.valueOf(etInputAmount.getText().toString()) - 5 + "");
                        }
                        break;
                    case PromoAdapter.STATE_INSTALLMENT:
                        if (INSTALLMENT_PERIOD_POSITION != 0) {
                            etInputAmount.setText(INSTALLMENT_PERIOD[--INSTALLMENT_PERIOD_POSITION] + "");
                        }
                        break;
                }
                break;
            case R.id.ripple_color_picker:
                ColorPickerDialogBuilder
                        .with(mContext)
                        .setTitle("Pilih warna teks")
                        .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                        .density(12)
                        .setPositiveButton("Pilih", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                etTemplateAds.setTextColor(selectedColor);
                                adsColorContent = selectedColor;
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .build()
                        .show();
                break;
            case R.id.img_btn_open_gallery:
                if (ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constant.ACTIVITY_CHOOSE_IMAGE);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, Constant.ACTIVITY_CHOOSE_IMAGE);
                }
                break;
        }
    }

    private void setCheckState(String STATES) {
        promoFormList.clear();
        promoAdapter.notifyDataSetChanged();
        switch (STATES) {
            case PromoAdapter.STATE_DISCOUNT:
                discountCheckButton.setVisibility(View.VISIBLE);
                installmentChecklistButton.setVisibility(View.INVISIBLE);
                cashbackChecklistButton.setVisibility(View.INVISIBLE);
                specialPromoCheckButton.setVisibility(View.INVISIBLE);

                tvSign.setText("%");
                etInputAmount.setEnabled(true);
                etInputAmount.setText("0");
                tvStatusCategory.setText(mContext.getResources().getString(R.string.discount));

                tvSignError.setVisibility(View.GONE);
                PromoRequest.STATES = PromoAdapter.STATE_DISCOUNT;
                if (isFormFillAvailable) setVisibility(STATES);
                break;
            case PromoAdapter.STATE_INSTALLMENT:
                promoFormList.clear();
                promoAdapter.notifyDataSetChanged();
                discountCheckButton.setVisibility(View.INVISIBLE);
                installmentChecklistButton.setVisibility(View.VISIBLE);
                cashbackChecklistButton.setVisibility(View.INVISIBLE);
                specialPromoCheckButton.setVisibility(View.INVISIBLE);

                tvSignError.setVisibility(View.GONE);
                PromoRequest.STATES = PromoAdapter.STATE_INSTALLMENT;
                if (isFormFillAvailable) setVisibility(STATES);

                etInputAmount.setText("3");
                etInputAmount.setEnabled(false);
                tvSign.setText(getResources().getString(R.string.month));
                tvStatusCategory.setText(mContext.getResources().getString(R.string.installment));
                break;
            case PromoAdapter.STATE_CASH_BACK:
                discountCheckButton.setVisibility(View.INVISIBLE);
                installmentChecklistButton.setVisibility(View.INVISIBLE);
                cashbackChecklistButton.setVisibility(View.VISIBLE);
                specialPromoCheckButton.setVisibility(View.INVISIBLE);

                tvSignError.setVisibility(View.GONE);
                PromoRequest.STATES = PromoAdapter.STATE_CASH_BACK;
                if (isFormFillAvailable) setVisibility(STATES);

                tvSign.setText("%");
                etInputAmount.setEnabled(true);
                etInputAmount.setText("0");
                tvStatusCategory.setText(mContext.getResources().getString(R.string.cashback));
                break;
            case PromoAdapter.STATE_SPECIAL_PRICE:
                discountCheckButton.setVisibility(View.INVISIBLE);
                installmentChecklistButton.setVisibility(View.INVISIBLE);
                cashbackChecklistButton.setVisibility(View.INVISIBLE);
                specialPromoCheckButton.setVisibility(View.VISIBLE);

                tvSignError.setVisibility(View.GONE);
                PromoRequest.STATES = PromoAdapter.STATE_SPECIAL_PRICE;
                if (isFormFillAvailable) setVisibility(STATES);

                tvStatusCategory.setText(mContext.getResources().getString(R.string.special_price));
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.ACTIVITY_CHOOSE_IMAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, Constant.ACTIVITY_CHOOSE_IMAGE);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_failed), Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_MAPS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapView.getMapAsync(this);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_failed), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == mActivity.RESULT_OK) {
            switch (requestCode) {
                case Constant.ACTIVITY_CHOOSE_IMAGE:
                    if (data.getData() != null) {
                        Uri targetUri = data.getData();
                        image_bitmap = DecodeBitmap.decodeSampleBitmapFromUri(targetUri, 100, 100, mContext);
                        img_open_gallery.setImageBitmap(DecodeBitmap.decodeSampleBitmapFromUri(targetUri, 200, 200, mContext));
                    }
                    break;
            }
        }
    }

    private void setVisibility(String c) {
        linear_fill_form.setVisibility(View.VISIBLE);
        if (c.equals(PromoAdapter.STATE_SPECIAL_PRICE)) {
            linear_special_price.setVisibility(View.VISIBLE);
            tvStatusCategory.setVisibility(View.GONE);
            linear_input_number.setVisibility(View.GONE);
        } else {
            linear_special_price.setVisibility(View.GONE);
            tvStatusCategory.setVisibility(View.VISIBLE);
            linear_input_number.setVisibility(View.VISIBLE);
        }
    }

    private void setFillFormDismiss() {
        linear_fill_form.setVisibility(View.GONE);
        etStuffName.setText("");
        img_open_gallery.setImageResource(0);
        etSpecialPrice.setText("");
    }

    private boolean isTimeValidate() {
        TextView tv_error_date_from = v.findViewById(R.id.show_error_date_from);
        TextView tv_error_date_to = v.findViewById(R.id.show_error_date_to);
        tv_error_date_from.setVisibility(View.GONE);
        tv_error_date_to.setVisibility(View.GONE);

        String dateStart = txt_tgl_start.getText().toString();
        String dateEnd = txt_tgl_end.getText().toString();
        String timeStart = txt_jam_start.getText().toString();
        String timeEnd = txt_jam_end.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.US);

        if (dateStart.isEmpty() || timeStart.isEmpty()) {
            tv_error_date_from.setVisibility(View.VISIBLE);
            return false;
        } else if (dateEnd.isEmpty() || timeEnd.isEmpty()) {
            tv_error_date_to.setVisibility(View.VISIBLE);
            return false;
        } else {
            try {
                Date tglStart = sdf.parse(dateStart);
                Date tglEnd = sdf.parse(dateEnd);
                Date rightNow = sdf.parse(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

                Date jamStart = sdfTime.parse(timeStart);
                Date jamEnd = sdfTime.parse(timeEnd);

                if (tglStart.after(tglEnd)) {
                    tv_error_date_to.setVisibility(View.VISIBLE);
                } else if (tglStart.compareTo(rightNow) < 0) {
                    tv_error_date_to.setVisibility(View.VISIBLE);
                } else if (tglStart.compareTo(tglEnd) == 0 && jamStart.after(jamEnd)) {
                    tv_error_date_to.setVisibility(View.VISIBLE);
                } else if (tglStart.compareTo(tglEnd) == 0 && jamStart.compareTo(jamEnd) == 0) {
                    tv_error_date_to.setVisibility(View.VISIBLE);
                } else {
                    return true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void getDate(final EditText editText) {
        Calendar mcurrentDate2 = Calendar.getInstance();
        int mYear2 = mcurrentDate2.get(Calendar.YEAR);
        int mMonth2 = mcurrentDate2.get(Calendar.MONTH);
        int mDay2 = mcurrentDate2.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dateEnd = new DatePickerDialog(mContext, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                myCalendar.set(Calendar.YEAR, selectedyear);
                myCalendar.set(Calendar.MONTH, selectedmonth);
                myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                updateDateLabel(editText);
            }
        }, mYear2, mMonth2, mDay2);
        dateEnd.show();
    }

    private void setTime(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int hour2 = calendar.get(Calendar.HOUR_OF_DAY);
        int minute2 = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog;

        timePickerDialog = new TimePickerDialog(v.getContext(), R.style.DialogTheme,
                new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String hourEndFinal;
                        String minuteEndFinal;
                        if (hourOfDay < 10 && minute < 10) {
                            hourEndFinal = "0" + hourOfDay;
                            minuteEndFinal = "0" + minute;
                            editText.setText(hourEndFinal + ":" + minuteEndFinal);
                        } else if (hourOfDay >= 10 && minute >= 10) {
                            editText.setText(hourOfDay + ":" + minute);
                        } else if (hourOfDay < 10 || minute >= 10) {
                            hourEndFinal = "0" + hourOfDay;
                            editText.setText(hourEndFinal + ":" + minute);
                        } else if (hourOfDay >= 10 || minute < 10) {
                            minuteEndFinal = "0" + minute;
                            editText.setText(hourOfDay + ":" + minuteEndFinal);
                        } else {
                            editText.setText(hourOfDay + ":" + minute);
                        }
                    }
                }, hour2, minute2, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    private void addPromoFormList(PromoForm promoForm, String STATES) {
        promoForm.setBitmap(image_bitmap);
        promoFormList.add(promoForm);
        promoAdapter.setPromoList(STATES, promoFormList);
        isFormFillAvailable = false;
        tvError_Empty_Stuff.setVisibility(View.GONE);
        setFillFormDismiss();
        image_bitmap = null;
    }

    @Override
    public void iconOnClick(int position) {
        if (position != Constant.imgTemplateAds.length - 1) {
            frame_template_ads.setBackgroundResource(Constant.imgTemplateAds[position]);
            adsTemplate = Constant.imgTemplateAds[position];
        } else {
            ColorPickerDialogBuilder
                    .with(mContext)
                    .setTitle("Pilih warna latar")
                    .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                    .density(12)
                    .setPositiveButton("Pilih", new ColorPickerClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                            frame_template_ads.setBackgroundColor(selectedColor);
                            adsTemplate = selectedColor;
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .build()
                    .show();
        }
    }

    public void hideSoftKeyboardFromRoot(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    hideSoftKeyboard(mActivity);
                    return false;
                }
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                hideSoftKeyboardFromRoot(innerView);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity == null) return;
        else if (activity.getCurrentFocus() == null) return;
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.editAddress:
                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                break;
            case R.id.editDesc:
                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                break;
//            case R.id.map_view_promo_request:
//                nestedScrollView.requestDisallowInterceptTouchEvent(true);
//                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
//                    case MotionEvent.ACTION_DOWN:
//                        // Disallow ScrollView to intercept touch events.
//                        nestedScrollView.requestDisallowInterceptTouchEvent(true);
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                        // Allow ScrollView to intercept touch events.
//                        nestedScrollView.requestDisallowInterceptTouchEvent(false);
//                        break;
//                }
//                break;
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap map) {
    }

    @Override
    public void onBackPress(boolean check, final Context context) {
        if (!txt_tgl_start.getText().toString().isEmpty() || !txt_jam_start.getText().toString().isEmpty()
                || !txt_tgl_end.getText().toString().isEmpty() || !txt_jam_end.getText().toString().isEmpty()
                || !etDescription.getText().toString().isEmpty() || !promotionTitle.getText().toString().isEmpty()
                || !etTemplateAds.getText().toString().isEmpty() || !etPhone.getText().toString().isEmpty()
                || !etEmail.getText().toString().isEmpty() || !etOfficePhone.getText().toString().isEmpty()
                || !etAddress.getText().toString().isEmpty() || promoFormList.size() > 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getResources().getString(R.string.save_changes));
            builder.setMessage(context.getResources().getString(R.string.discard_changes_content));
            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Random random = new Random();
                    int ran = random.nextInt(10);
                    promoSQLite.deleteAll(prefConfig.getID());
                    promoStuffSQLite.deleteAll(prefConfig.getID());
                    prefConfig.insertId(ran);
                    promoSQLite.insertData(ran, etTemplateAds.getText().toString(), adsTemplate
                            , promotionTitle.getText().toString(), etEmail.getText().toString()
                            , etPhone.getText().toString(), etOfficePhone.getText().toString()
                            , txt_tgl_start.getText().toString(), txt_tgl_end.getText().toString()
                            , txt_jam_start.getText().toString(), txt_jam_end.getText().toString()
                            , STATES, etAddress.getText().toString(), etDescription.getText().toString());
                    if (promoFormList.size() > 0) {
                        for (PromoForm promoForm : promoFormList) {
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            byte[] imgArr = null;
                            if (promoForm.getBitmap() != null) {
                                promoForm.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                                imgArr = byteArrayOutputStream.toByteArray();
                            }

                            String value = "";
                            switch (STATES) {
                                case PromoAdapter.STATE_DISCOUNT:
                                    value = String.valueOf(promoForm.getDiscount());
                                    break;
                                case PromoAdapter.STATE_INSTALLMENT:
                                    value = String.valueOf(promoForm.getInstallment());
                                    break;
                                case PromoAdapter.STATE_CASH_BACK:
                                    value = String.valueOf(promoForm.getCashback());
                                    break;
                                case PromoAdapter.STATE_SPECIAL_PRICE:
                                    value = String.valueOf(promoForm.getSpecial_price());
                                    break;
                            }
                            promoStuffSQLite.insertData(ran, promoForm.getStuffName(), imgArr, value);
                        }
                    }
                    changeFragment(context, new Profile());
                }
            });
            builder.setNegativeButton("Tidak, buang perubahan", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    changeFragment(context, new Profile());
                    if (prefConfig.getID() != -1) {
                        promoSQLite.deleteAll(prefConfig.getID());
                        promoStuffSQLite.deleteAll(prefConfig.getID());
                        prefConfig.insertId(-1);
                    }
                }
            });
            builder.setCancelable(true);
            builder.show();
        } else {
            promoSQLite.deleteAll(prefConfig.getID());
            promoStuffSQLite.deleteAll(prefConfig.getID());
            prefConfig.insertId(-1);
            changeFragment(context, new Profile());
        }
    }

    private void changeFragment(Context context, Fragment fragment) {
        AppCompatActivity activity = (AppCompatActivity) context;
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onDeletes(int position) {
        promoFormList.remove(position);
        promoAdapter.setPromoList(STATES, promoFormList);
        promoAdapter.notifyDataSetChanged();
    }
}
