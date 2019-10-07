package com.andrew.prototype.Fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.prototype.Activity.MainActivity;
import com.andrew.prototype.Adapter.ImagePickerAdapter;
import com.andrew.prototype.Adapter.PromoAdapter;
import com.andrew.prototype.Model.Forum;
import com.andrew.prototype.Model.ForumThread;
import com.andrew.prototype.Model.ImagePicker;
import com.andrew.prototype.Model.Merchant;
import com.andrew.prototype.Model.SyncImg;
import com.andrew.prototype.R;
import com.andrew.prototype.Utils.Constant;
import com.andrew.prototype.Utils.DecodeBitmap;
import com.andrew.prototype.Utils.PrefConfig;
import com.andrew.prototype.Utils.Utils;
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
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewThread extends Fragment implements View.OnClickListener
        , ImagePickerAdapter.onItemClick, View.OnTouchListener, MainActivity.onBackPressFragment {

    public static final String EDIT_THREAD = "EDIT_THREAD";
    public static final String EDIT_THREAD_SELECTED = "EDIT_THREAD_SELECTED";
    public static final String EDIT_THREAD_REPLY = "EDIT_THREAD_REPLY";
    public static final String EDIT_THREAD_REPLY_BACK = "EDIT_THREAD_REPLY_BACK";
    public static final String EDIT_THREAD_MERCHANT = "EDIT_THREAD_MERCHANT";
    public static final String EDIT_THREAD_REPLY_BACK_LIST = "EDIT_THREAD_REPLY_BACK_LIST";

    private static final String TAG = NewThread.class.getSimpleName();

    public static String THREAD_CONDITION;

    private static PrefConfig prefConfig;
    private static Forum forum;
    private static Forum.ForumReply forumReply;

    private View v;
    private RecyclerView recyclerView;
    private EditText title, content;
    private ImagePickerAdapter imagePickerAdapter;
    private TextView error_content, error_title;
    private AlertDialog codeAlert;
    private Context mContext;
    private Activity mActivity;
    private DatabaseReference dbRef;
    private StorageReference storageReference, strRef;
    private FrameLayout frame_loading;
    private Merchant merchant;

    private List<ImagePicker> imageList;
    private List<Forum.ForumImage> forumImageList;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_new_thread, container, false);
        initVar();
        return v;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initVar() {
        THREAD_CONDITION = "";
        mContext = v.getContext();
        prefConfig = new PrefConfig(mContext);
        dbRef = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference(Constant.DB_REFERENCE_FORUM_IMAGE);
        strRef = FirebaseStorage.getInstance().getReference(Constant.DB_REFERENCE_FORUM_IMAGE_REPLY);

        TextView tvTitle = v.findViewById(R.id.title_new_thread);
        TextView title_only = v.findViewById(R.id.tvTitle_NewThread);
        Button submit = v.findViewById(R.id.btnSubmit_NewThread);
        ImageButton photo = v.findViewById(R.id.photo_taker_new_thread);
        ImageButton file = v.findViewById(R.id.file_taker_new_thread);
        ImageButton camera = v.findViewById(R.id.camera_taker_new_thread);

        recyclerView = v.findViewById(R.id.recyclerView_NewThread);
        title = v.findViewById(R.id.etTitle_NewThread);
        content = v.findViewById(R.id.etContent_NewThread);
        error_content = v.findViewById(R.id.show_error_content_new_thread);
        error_title = v.findViewById(R.id.show_error_title_new_thread);
        frame_loading = v.findViewById(R.id.frame_loading_new_thread);

        imageList = new ArrayList<>();
        forumImageList = new ArrayList<>();

        setRecyclerView();
        frame_loading.getBackground().setAlpha(Constant.MAX_ALPHA);

        submit.setOnClickListener(this);
        photo.setOnClickListener(this);
//        it will appear when we know how to upload pdf file to firebase storage
//        file.setOnClickListener(this);
        camera.setOnClickListener(this);
        frame_loading.setOnClickListener(this);
        content.setOnTouchListener(this);

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getParcelable(EDIT_THREAD) != null || bundle.getParcelable(EDIT_THREAD_SELECTED) != null) {
                if (bundle.getParcelable(EDIT_THREAD) != null) {
                    THREAD_CONDITION = EDIT_THREAD;
                } else if (bundle.getParcelable(EDIT_THREAD_SELECTED) != null) {
                    THREAD_CONDITION = EDIT_THREAD_SELECTED;
                }
                frame_loading.setVisibility(View.VISIBLE);
                forum = bundle.getParcelable(THREAD_CONDITION);
                if (forum != null) {
                    title.setText(forum.getForum_title());
                    content.setText(forum.getForum_content());

                    dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + forum.getFid() + "/" + Constant.DB_REFERENCE_FORUM_IMAGE)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    /*
                                     * Harus menggunakan single value event listener, agar saat diupload data tidak refresh
                                     * */
                                    forumImageList.clear();
                                    imageList.clear();
                                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        forumImageList.add(snapshot.getValue(Forum.ForumImage.class));
                                        Picasso.get().load(snapshot.child("image_url").getValue().toString()).into(new Target() {
                                            @Override
                                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                imageList.add(new ImagePicker(bitmap, ImagePickerAdapter.STATES
                                                        , snapshot.child("image_name").getValue().toString()));
                                            }

                                            @Override
                                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                            }

                                            @Override
                                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                                            }
                                        });
                                    }
                                    if (imageList.size() > 0) {
                                        imagePickerAdapter.setImageList(imageList);
                                        imagePickerAdapter.notifyDataSetChanged();
                                    }
                                    frame_loading.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            } else if (bundle.getParcelable(EDIT_THREAD_REPLY) != null) {
                THREAD_CONDITION = EDIT_THREAD_REPLY;
                title.setVisibility(View.GONE);
                title_only.setVisibility(View.GONE);
                forumReply = bundle.getParcelable(EDIT_THREAD_REPLY);
                forum = bundle.getParcelable(EDIT_THREAD_REPLY_BACK);
                merchant = bundle.getParcelable(EDIT_THREAD_MERCHANT);
                content.setText(forumReply.getForum_content());
                dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + forum.getFid() + "/"
                        + Constant.DB_REFERENCE_FORUM_REPLY + "/" + forumReply.getFrid() + "/"
                        + Constant.DB_REFERENCE_FORUM_IMAGE_REPLY)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                forumImageList.clear();
                                imageList.clear();
                                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    forumImageList.add(snapshot.getValue(Forum.ForumImage.class));
                                    Picasso.get().load(snapshot.child("image_url").getValue().toString()).into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            imageList.add(new ImagePicker(bitmap, ImagePickerAdapter.STATES
                                                    , snapshot.child("image_name").getValue().toString()));
                                        }

                                        @Override
                                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                        }
                                    });
                                }
                                if (imageList.size() > 0) {
                                    imagePickerAdapter.setImageList(imageList);
                                    imagePickerAdapter.notifyDataSetChanged();
                                }
                                frame_loading.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
            tvTitle.setText(mContext.getResources().getString(R.string.edit_thread));
        }
    }

    private void setRecyclerView() {
        imagePickerAdapter = new ImagePickerAdapter(mContext, imageList, this, "");
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(imagePickerAdapter);
    }

    @SuppressLint("SetTextI18n")
    private void setPreview() {
        AlertDialog.Builder codeBuilder = new AlertDialog.Builder(mContext);
        @SuppressLint("InflateParams") View codeView = getLayoutInflater().inflate(R.layout.custom_preview_thread, null);

        TextView title = codeView.findViewById(R.id.title_custom_preview);
        TextView content = codeView.findViewById(R.id.content_preview);
        TextView time = codeView.findViewById(R.id.time_preview);
        ScrollView scrollView = codeView.findViewById(R.id.sc_preview);
        RoundedImageView imageView = codeView.findViewById(R.id.merchantPic_Preview);
        TextView user = codeView.findViewById(R.id.merchantName);
        TextView loc = codeView.findViewById(R.id.merchantLoc_Preview);
        RecyclerView recyclerView_preview = codeView.findViewById(R.id.recycler_preview);
        ImageButton imageButton = codeView.findViewById(R.id.close_preview_thread);
        Button cancel = codeView.findViewById(R.id.btnCancel_Preview);
        Button save = codeView.findViewById(R.id.btnSubmit_Preview);

        scrollView.smoothScrollTo(0, 0);

//        if (!THREAD_CONDITION.equals(EDIT_THREAD) && !THREAD_CONDITION.isEmpty()) {
//            if (THREAD_CONDITION.equals(EDIT_THREAD_SELECTED)) {
//                forumThread.setTitle(this.title.getText().toString());
//                forumThread.setContent(this.content.getText().toString());
//                user.setText(forumThread.getUsername());
//                loc.setText(forumThread.getLocation());
//            } else {
//                title.setVisibility(View.GONE);
//                time.setVisibility(View.GONE);
////                DecodeBitmap.setScaledImageView(imageView, thread.getProfile_picture(), mContext);
////                Glide.with(mContext)
////                        .load(thread.getProfile_picture())
////                        .placeholder(mContext.getDrawable(R.drawable.placeholder))
////                        .into(imageView);
//                user.setText(thread.getUsername());
//                loc.setText(thread.getLocation());
//            }
//        } else if (THREAD_CONDITION.equals(EDIT_THREAD)) {
//            forumThread.setContent(this.content.getText().toString());
//            user.setText(forumThread.getUsername());
//            loc.setText(forumThread.getLocation());
//        }

        user.setText(prefConfig.getName());
        loc.setText(prefConfig.getLocation());

        Picasso.get().load(prefConfig.getProfilePicture()).into(imageView);

        title.setText(this.title.getText().toString());
        content.setText(this.content.getText().toString());
        time.setText(getDate() + " WIB");

        ImagePickerAdapter imagePickerAdapter = new ImagePickerAdapter(mContext, imageList, this, ImagePickerAdapter.STATES_NO_BUTTON);
        recyclerView_preview.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerView_preview.setAdapter(imagePickerAdapter);

        imageButton.setOnClickListener(this);
        cancel.setOnClickListener(this);
        save.setOnClickListener(this);
        codeBuilder.setView(codeView);
        codeAlert = codeBuilder.create();
        codeAlert.show();
    }

    private String getDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy HH:mm");
        return sdf.format(new Date());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit_NewThread:
                final String titles = title.getText().toString();
                final String contents = content.getText().toString();

                title.setBackground(getResources().getDrawable(R.drawable.background_edit_text));
                content.setBackground(getResources().getDrawable(R.drawable.background_edit_text));
                error_content.setVisibility(View.GONE);
                error_title.setVisibility(View.GONE);

                if (titles.isEmpty() && !THREAD_CONDITION.equals(EDIT_THREAD_REPLY)) {
                    error_title.setVisibility(View.VISIBLE);
                    title.setBackground(getResources().getDrawable(R.drawable.background_edit_text_error));
                } else if (contents.isEmpty()) {
                    error_content.setVisibility(View.VISIBLE);
                    content.setBackground(getResources().getDrawable(R.drawable.background_edit_text_error));
                } else {
                    if (THREAD_CONDITION.equals(EDIT_THREAD_REPLY)) {
                        frame_loading.setVisibility(View.VISIBLE);
                        Map<String, Object> map = new HashMap<>();
                        if (forumImageList.size() > 0) {
                            for (int i = 0; i < forumImageList.size(); i++) {
                                storageReference.child(forumImageList.get(i).getImage_name()).delete();
                            }
                            dbRef.child(Constant.DB_REFERENCE_FORUM)
                                    .child(forum.getFid() + "/" + Constant.DB_REFERENCE_FORUM_REPLY + "/"
                                            + forumReply.getFrid() + "/" + Constant.DB_REFERENCE_FORUM_IMAGE_REPLY)
                                    .removeValue();
                        }
                        if (imageList.size() == 0) {
                            map.put(Constant.DB_REFERENCE_FORUM + "/" + forum.getFid()
                                    + "/forum_reply/" + forumReply.getFrid() + "/forum_content", content.getText().toString());
                            dbRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    SelectedThread selectedThread = new SelectedThread();

                                    AppCompatActivity activity = (AppCompatActivity) mContext;

                                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable(SelectedThread.GET_THREAD_OBJECT, forum);
                                    bundle.putParcelable(SelectedThread.GET_MERCHANT, merchant);

                                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                                    fragmentTransaction.replace(R.id.main_frame, selectedThread);

                                    selectedThread.setArguments(bundle);
                                    fragmentTransaction.commit();
                                    Toast.makeText(mContext, getResources().getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            final String key = dbRef.push().getKey();
                            map.put(Constant.DB_REFERENCE_FORUM + "/" + forum.getFid() + "/forum_reply/"
                                    + forumReply.getFrid() + "/forum_content", content.getText().toString());
                            for (int i = 0; i < imageList.size(); i++) {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                imageList.get(i).getImage_bitmap().compress(Bitmap.CompressFormat.JPEG, 30, baos);
                                byte[] byteData = baos.toByteArray();

                                Random random = new Random();
                                final int ran = random.nextInt(10000);
                                final String imgName = "forum-reply-" + prefConfig.getMID() + "-" + key + "-" + ran;

                                UploadTask uploadTask = strRef.child(imgName).putBytes(byteData);

                                final int finalI = i;
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        strRef.child(imgName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String frid = dbRef.child(Constant.DB_REFERENCE_FORUM_REPLY).push().getKey();

                                                HashMap<String, String> imgMap = new HashMap<>();
                                                imgMap.put("image_name", imgName);
                                                imgMap.put("image_url", uri.toString());
                                                imgMap.put("frid", frid);

                                                dbRef.child(Constant.DB_REFERENCE_FORUM + "/"
                                                        + forum.getFid() + "/" + Constant.DB_REFERENCE_FORUM_REPLY
                                                        + "/" + forumReply.getFrid() + "/" + Constant.DB_REFERENCE_FORUM_IMAGE_REPLY + "/" + key).setValue(imgMap)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                if (finalI == imageList.size() - 1) {
                                                                    SelectedThread selectedThread = new SelectedThread();

                                                                    AppCompatActivity activity = (AppCompatActivity) mContext;

                                                                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                                                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                                                    Bundle bundle = new Bundle();
                                                                    bundle.putParcelable(SelectedThread.GET_THREAD_OBJECT, forum);
                                                                    bundle.putParcelable(SelectedThread.GET_MERCHANT, merchant);

                                                                    /*
                                                                     * kita harus tambah posisi si reply thread yang di edit sebelumnya
                                                                     * jadi kita bisa direct ke replynya user
                                                                     * */

                                                                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                                                                    fragmentTransaction.replace(R.id.main_frame, selectedThread);

                                                                    selectedThread.setArguments(bundle);
                                                                    fragmentTransaction.commit();
                                                                    frame_loading.setVisibility(View.GONE);
                                                                    Toast.makeText(mContext, getResources().getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    } else {
                        setPreview();
                    }
                }

                break;
            case R.id.photo_taker_new_thread:
                if (ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constant.PERMISSION_READ_GALLERY_EXTERNAL);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, Constant.ACTIVITY_CHOOSE_IMAGE);
                }
                break;

            case R.id.file_taker_new_thread:
                if (ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constant.PERMISSION_READ_FILE_EXTERNAL);
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(intent, Constant.ACTIVITY_CHOOSE_FILE);
                }
                break;

            case R.id.camera_taker_new_thread:
                if (ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                        || ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSION_CAMERA_TAKER);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, Constant.ACTIVITY_TAKE_IMAGE);
                }
                break;
            case R.id.close_preview_thread:
                codeAlert.dismiss();
                break;
            case R.id.btnCancel_Preview:
                codeAlert.dismiss();
                break;
            case R.id.btnSubmit_Preview:
                codeAlert.dismiss();
                frame_loading.setVisibility(View.VISIBLE);

                if (THREAD_CONDITION.isEmpty()) {
                    final String key = dbRef.push().getKey();

                    dbRef.child("forum").child(key);

//                HashMap<String, String> map = new HashMap<>();
//                map.put("FID", key);
//                map.put("MID", prefConfig.getMid() + "");
//                map.put("forum_content", content.getText().toString());
//                map.put("forum_title", title.getText().toString());
//                map.put("forum_date", getStory_date());
//                map.put("forum_is_like", "false");
//                map.put("forum_amount_like", "0");
//                map.put("view_count", "1");

                    Forum forum = new Forum(key, prefConfig.getMID(), content.getText().toString(), getDate(), title.getText().toString(), 0, 1, false);

                    if (imageList.size() == 0) {
                        dbRef.child("forum")
                                .child(key)
                                .setValue(forum)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        frame_loading.setVisibility(View.GONE);
                                        Toast.makeText(mContext, getResources().getString(R.string.submit_success), Toast.LENGTH_SHORT).show();

                                        Forum forum = new Forum(key, String.valueOf(prefConfig.getMID())
                                                , content.getText().toString(), getDate()
                                                , title.getText().toString(), 0, 1, false);
                                        SelectedThread selectedThread = new SelectedThread();

                                        AppCompatActivity activity = (AppCompatActivity) mContext;

                                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                        Bundle bundle = new Bundle();
                                        bundle.putParcelable(SelectedThread.GET_THREAD_OBJECT, forum);
                                        bundle.putParcelable(SelectedThread.GET_MERCHANT, prefConfig.getMerchantData());

                                        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                                        fragmentTransaction.replace(R.id.main_frame, selectedThread);

                                        selectedThread.setArguments(bundle);
                                        fragmentTransaction.commit();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "This is failure");
                                    }
                                });
                    } else {
                        dbRef.child("forum").child(key).setValue(forum)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        for (int i = 0; i < imageList.size(); i++) {
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            imageList.get(i).getImage_bitmap().compress(Bitmap.CompressFormat.JPEG, 30, baos);
                                            byte[] byteData = baos.toByteArray();

                                            Random random = new Random();
                                            final int ran = random.nextInt(10000);
                                            final String imgName = "forum-first-" + prefConfig.getMID() + "-" + key + "-" + ran;

                                            UploadTask uploadTask = storageReference.child(imgName).putBytes(byteData);
                                            // it will make image name become unique
                                            final int finalI = i;
                                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    storageReference
                                                            .child(imgName)
                                                            .getDownloadUrl()
                                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {
                                                                    HashMap<String, String> imgMap = new HashMap<>();
                                                                    imgMap.put("image_name", imgName);
                                                                    imgMap.put("image_url", uri.toString());

                                                                    dbRef.child("forum_image");

                                                                    String key1 = dbRef.push().getKey();

                                                                    imgMap.put("fiid", key1);

                                                                    dbRef.child(Constant.DB_REFERENCE_FORUM).child(key + "/forum_image/" + key1)
                                                                            .setValue(imgMap)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    if (finalI == imageList.size() - 1) {
                                                                                        Forum forum = new Forum(key, String.valueOf(prefConfig.getMID())
                                                                                                , content.getText().toString(), getDate()
                                                                                                , title.getText().toString(), 0, 1, false);
                                                                                        SelectedThread selectedThread = new SelectedThread();

                                                                                        AppCompatActivity activity = (AppCompatActivity) mContext;

                                                                                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
                                                                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                                                                        Bundle bundle = new Bundle();
                                                                                        bundle.putParcelable(SelectedThread.GET_THREAD_OBJECT, forum);
                                                                                        bundle.putParcelable(SelectedThread.GET_MERCHANT, merchant);

                                                                                        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                                                                                        fragmentTransaction.replace(R.id.main_frame, selectedThread);

                                                                                        selectedThread.setArguments(bundle);
                                                                                        fragmentTransaction.commit();
                                                                                        frame_loading.setVisibility(View.GONE);
                                                                                        Toast.makeText(mContext, getResources().getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {

                                                                                }
                                                                            });
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                }
                                                            });

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "This is failure");
                                    }
                                });
                    }
                } else if (THREAD_CONDITION.equals(EDIT_THREAD) || THREAD_CONDITION.equals(EDIT_THREAD_SELECTED)) {
                    Map<String, Object> map = new HashMap<>();

                    if (imageList.size() == 0) {
                        if (forumImageList.size() > 0) {
                            for (int i = 0; i < forumImageList.size(); i++) {
                                storageReference.child(forumImageList.get(i).getImage_name()).delete();
                            }
                            dbRef.child("forum")
                                    .child(forum.getFid() + "/forum_image")
                                    .removeValue();
                        }
                        map.put(Constant.DB_REFERENCE_FORUM + "/" + forum.getFid() + "/forum_content", content.getText().toString());
                        map.put(Constant.DB_REFERENCE_FORUM + "/" + forum.getFid() + "/forum_title/", title.getText().toString());
                        dbRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                                frame_loading.setVisibility(View.GONE);
                                Forum forums = new Forum(forum.getFid(), String.valueOf(prefConfig.getMID())
                                        , content.getText().toString(), forum.getForum_date()
                                        , title.getText().toString(), forum.getForum_like()
                                        , forum.getView_count(), forum.isLike());
                                SelectedThread selectedThread = new SelectedThread();

                                AppCompatActivity activity = (AppCompatActivity) mContext;

                                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                Bundle bundle = new Bundle();
                                bundle.putParcelable(SelectedThread.GET_THREAD_OBJECT, forums);
                                bundle.putParcelable(SelectedThread.GET_MERCHANT, prefConfig.getMerchantData());

                                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                                fragmentTransaction.replace(R.id.main_frame, selectedThread);

                                selectedThread.setArguments(bundle);
                                fragmentTransaction.commit();
                                Toast.makeText(mContext, getResources().getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        map.put(Constant.DB_REFERENCE_FORUM + "/" + forum.getFid() + "/forum_content", content.getText().toString());
                        map.put(Constant.DB_REFERENCE_FORUM + "/" + forum.getFid() + "/forum_title/", title.getText().toString());

                        dbRef.updateChildren(map);

                        if (forumImageList.size() > 0) {
                            for (int i = 0; i < forumImageList.size(); i++) {
                                storageReference.child(forumImageList.get(i).getImage_name()).delete();
                                dbRef.child("forum")
                                        .child(forum.getFid() + "/forum_image/" + forumImageList.get(i).getFiid())
                                        .removeValue();
                            }
                        }

                        for (int i = 0; i < imageList.size(); i++) {
                            final String key = dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + forum.getFid() + "/" + Constant.DB_REFERENCE_FORUM_IMAGE).push().getKey();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            imageList.get(i).getImage_bitmap().compress(Bitmap.CompressFormat.JPEG, 30, baos);
                            byte[] byteData = baos.toByteArray();

                            Random random = new Random();
                            final int ran = random.nextInt(10000);
                            final String imgName = "forum-first-" + prefConfig.getMID() + "-" + key + "-" + ran;

                            UploadTask uploadTask = storageReference.child(imgName).putBytes(byteData);
                            // it will make image name become unique
                            final int finalI = i;
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    storageReference
                                            .child(imgName)
                                            .getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    HashMap<String, String> imgMap = new HashMap<>();
                                                    imgMap.put("image_name", imgName);
                                                    imgMap.put("image_url", uri.toString());

                                                    dbRef.child("forum_image");

                                                    String key1 = dbRef.push().getKey();

                                                    imgMap.put("fiid", key1);

                                                    dbRef.child("forum").child(forum.getFid() + "/forum_image/" + key1)
                                                            .setValue(imgMap)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    if (finalI == imageList.size() - 1) {
                                                                        Forum forums = new Forum(forum.getFid(), String.valueOf(prefConfig.getMID())
                                                                                , content.getText().toString(), forum.getForum_date()
                                                                                , title.getText().toString(), forum.getForum_like()
                                                                                , forum.getView_count(), forum.isLike());
                                                                        SelectedThread selectedThread = new SelectedThread();

                                                                        AppCompatActivity activity = (AppCompatActivity) mContext;

                                                                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
                                                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                                                        Bundle bundle = new Bundle();
                                                                        bundle.putParcelable(SelectedThread.GET_THREAD_OBJECT, forums);
                                                                        bundle.putParcelable(SelectedThread.GET_MERCHANT, merchant);

                                                                        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                                                                        fragmentTransaction.replace(R.id.main_frame, selectedThread);

                                                                        selectedThread.setArguments(bundle);
                                                                        fragmentTransaction.commit();
                                                                        Toast.makeText(mContext, getResources().getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                        }
                    }
                } else


//                FragmentManager fragmentManager = getFragmentManager();
//                assert fragmentManager != null;
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                if (THREAD_CONDITION.isEmpty())
//                    // direct it to specific thread
//                    fragmentTransaction.replace(R.id.main_frame, new MainForum());
//                else
//                    onBackPress(false, mContext);
//                fragmentTransaction.commit();
                    break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.PERMISSION_CAMERA_TAKER:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, Constant.ACTIVITY_TAKE_IMAGE);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_failed), Toast.LENGTH_SHORT).show();
                }
                break;
            case Constant.PERMISSION_READ_FILE_EXTERNAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(intent, Constant.ACTIVITY_CHOOSE_FILE);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_failed), Toast.LENGTH_SHORT).show();
                }
                break;
            case Constant.PERMISSION_READ_GALLERY_EXTERNAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, Constant.ACTIVITY_CHOOSE_IMAGE);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_failed), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constant.ACTIVITY_CHOOSE_IMAGE:
                    Bitmap bitmap;
                    if (data.getData() != null) {
                        Uri targetUri = data.getData();
                        File f = new File("" + targetUri);
                        try {
                            bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(targetUri));
                            imageList.add(new ImagePicker(DecodeBitmap.compressBitmap(bitmap), "IMG", f.getName()));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else if (data.getClipData() != null) {
                        ClipData clipData = data.getClipData();
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            Uri uri = item.getUri();
                            File f = new File("" + uri);
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
                                imageList.add(new ImagePicker(DecodeBitmap.compressBitmap(bitmap), "IMG", f.getName()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    imagePickerAdapter.setImageList(imageList);
                    break;
                case Constant.ACTIVITY_CHOOSE_FILE:
                    if (data.getData() != null) {
                        Uri targetUri = data.getData();
                        String filePath = getFileName(targetUri);
                        imageList.add(new ImagePicker(null, ImagePickerAdapter.STATES_PDF, filePath));
                    } else if (data.getClipData() != null) {
                        ClipData clipData = data.getClipData();
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            Uri targetUri = item.getUri();
                            String filePath = getFileName(targetUri);
                            imageList.add(new ImagePicker(null, ImagePickerAdapter.STATES_PDF, filePath));
                        }
                    }
                    imagePickerAdapter.setImageList(imageList);
                    break;
                case Constant.ACTIVITY_TAKE_IMAGE:
                    Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                    Uri tempUri = getImageUri(mContext, photo);
                    File finalFile = new File(getRealPathFromURI(tempUri));
                    imageList.add(new ImagePicker(photo, "IMG", finalFile.getName()));
                    imagePickerAdapter.setImageList(imageList);
                    break;
            }
            recyclerView.smoothScrollToPosition(imageList.size());
        }
    }

    private String getFileName(Uri uri) throws IllegalArgumentException {
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        assert cursor != null;
        if (cursor.getCount() <= 0) {
            cursor.close();
            throw new IllegalArgumentException("Can't obtain file name, cursor is empty");
        }
        cursor.moveToFirst();
        String fileName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
        cursor.close();
        return fileName;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000, true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (mContext.getContentResolver() != null) {
            Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    @Override
    public void onItemClicked(final int pos, String states) {
//        if (THREAD_CONDITION.equals(EDIT_THREAD)) {
//            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
//            builder.setMessage("Apakah Anda yakin? Gambar yang telah dihapus tidak dapat dikembalikan.")
//                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            frame_loading.setVisibility(View.VISIBLE);
//                            dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + forum.getFid() + "/" + Constant.DB_REFERENCE_FORUM_IMAGE + "/" + forumImageList.get(pos).getFiid())
//                                    .removeValue(new DatabaseReference.CompletionListener() {
//                                        @Override
//                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//
//                                            storageReference.child(imageList.get(pos).getTitle())
//                                                    .delete()
//                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                        @Override
//                                                        public void onSuccess(Void aVoid) {
//                                                            imageList.remove(pos);
//                                                            imagePickerAdapter.setImageList(imageList);
//                                                            frame_loading.setVisibility(View.GONE);
//                                                        }
//                                                    });
//                                        }
//                                    });
////                        MainForum mainForum = new MainForum();
////
////                        AppCompatActivity activity = (AppCompatActivity) context;
////
////                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
////                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
////
////                        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
////                        fragmentTransaction.replace(R.id.main_frame, mainForum);
////
////                        fragmentTransaction.commit();
//                        }
//                    })
//                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                        }
//                    }).show();
//        } else {
        imageList.remove(pos);
        imagePickerAdapter.setImageList(imageList);
//        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId() == R.id.etContent_NewThread) {
            view.getParent().requestDisallowInterceptTouchEvent(true);
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
        }
        return false;
    }

    @Override
    public void onBackPress(boolean check, final Context context) {
        if (THREAD_CONDITION.equals(EDIT_THREAD) || THREAD_CONDITION.isEmpty()) {
            MainForum mainForum = new MainForum();

            AppCompatActivity activity = (AppCompatActivity) context;

            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.main_frame, mainForum);

            fragmentTransaction.commit();
        } else {
            SelectedThread selectedThread = new SelectedThread();

            AppCompatActivity activity = (AppCompatActivity) context;

            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Bundle bundle = new Bundle();
            bundle.putParcelable(SelectedThread.GET_THREAD_OBJECT, forum);
            bundle.putParcelable(SelectedThread.GET_MERCHANT, prefConfig.getMerchantData());

            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.main_frame, selectedThread);

            selectedThread.setArguments(bundle);
            fragmentTransaction.commit();
        }
    }
}
