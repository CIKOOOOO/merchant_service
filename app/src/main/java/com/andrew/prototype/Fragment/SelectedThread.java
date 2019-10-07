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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.prototype.Activity.MainActivity;
import com.andrew.prototype.Adapter.ImageGridAdapter;
import com.andrew.prototype.Adapter.ImagePickerAdapter;
import com.andrew.prototype.Adapter.PageNumberAdapter;
import com.andrew.prototype.Adapter.ReplyAdapter;
import com.andrew.prototype.Adapter.ReportAdapter;
import com.andrew.prototype.Adapter.TrendingAdapter;
import com.andrew.prototype.Model.Forum;
import com.andrew.prototype.Model.ImagePicker;
import com.andrew.prototype.Model.Merchant;
import com.andrew.prototype.Model.Report;
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
import com.mlsdev.animatedrv.AnimatedRecyclerView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectedThread extends Fragment implements TextWatcher, View.OnClickListener
        , View.OnKeyListener, TrendingAdapter.itemClickListener, MainActivity.onBackPressFragment
        , View.OnTouchListener, ImagePickerAdapter.onItemClick, PopupMenu.OnMenuItemClickListener
        , ReplyAdapter.onReplyClick, ReplyAdapter.onEdit, PageNumberAdapter.pageNumber
        , ReplyAdapter.onReplyDelete, ImageGridAdapter.imageOnClick {

    public static final String GET_THREAD_OBJECT = "thread_object";
    public static final String GET_MERCHANT = "merchant_profile";

    public static int PAGE_NUMBER_STATE = 0;
    public static boolean trendingIsVisible, frameIsVisible;
    public static FrameLayout frameLayout;

    private static final int STATE_LINEAR_VERTICAL = 1;
    private static final int STATE_GRID = 2;
    private static final int STATE_LINEAR_HORIZONTAL = 3;
    private static final int AMOUNT_REPLY = 5;

    @SuppressLint("StaticFieldLeak")
    private static LinearLayout trending_linear;
    @SuppressLint("StaticFieldLeak")
    private static TrendingAdapter trendingAdapter;
    private static List<ImagePicker> imageList, imageReply;

    private View v;
    private LinearLayout reply_linear;
    private RecyclerView recycler_trending, recycler_main_forum, recycler_img_reply, recycler_page_number, recycler_page_number2;
    private AnimatedRecyclerView recycler_reply;
    private TextView amount_like, chosen_image, error_box, merchant_name;
    private ImageButton img_more, frame_close, img_download, img_like;
    private NestedScrollView scrollView;
    private ReplyAdapter replyAdapter;
    private ImagePickerAdapter pickerAdapter;
    private EditText etReply, etName, etSearch;
    private PrefConfig prefConfig;
    private PageNumberAdapter pageNumberAdapter;
    private Context mContext;
    private Activity mActivity;
    private ImageView img_frame_selected, img_profile;
    private Forum forum;
    private Merchant merchant;
    private DatabaseReference dbRef, dbProfile;
    private StorageReference storageReference;
    private ImageGridAdapter imageGridAdapter;
    private RelativeLayout relative_page_number, relative_page_number2;
    private FrameLayout frame_loading;

    private List<Forum> trendingList, tempList, threadList;
    private List<Forum.ForumImage> forumImageList;
    private List<Forum.ForumReply> replyList;
    private Map<String, Merchant> merchantMap;
    private static Map<String, List<Forum.ForumImageReply>> forumImageReplyMap;
    private boolean isLike, isCheck, isReply;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_selected_thread, container, false);
        initVar();
        return v;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initVar() {
        mContext = v.getContext();
        trendingIsVisible = false;
        frameIsVisible = false;
        isCheck = false;
        isReply = false;
        PAGE_NUMBER_STATE = 0;
        prefConfig = new PrefConfig(mContext);
        isLike = false;
        dbRef = FirebaseDatabase.getInstance().getReference(Constant.DB_REFERENCE_FORUM);
        storageReference = FirebaseStorage.getInstance().getReference(Constant.DB_REFERENCE_FORUM_IMAGE_REPLY);
        dbProfile = FirebaseDatabase.getInstance().getReference(Constant.DB_REFERENCE_MERCHANT_PROFILE);

        RelativeLayout relativeLayout = v.findViewById(R.id.rl);
        ImageButton imageButton = v.findViewById(R.id.search_selected_thread);
        ImageButton gallery_opener = v.findViewById(R.id.gallery_opener_reply);
        ImageButton after = v.findViewById(R.id.btn_after_reply);
        ImageButton after2 = v.findViewById(R.id.btn_after_reply2);
        ImageButton before = v.findViewById(R.id.btn_before_reply);
        ImageButton before2 = v.findViewById(R.id.btn_before_reply2);
        Button reply = v.findViewById(R.id.btn_reply_thread);
        Button send = v.findViewById(R.id.btn_send_selected);
        TextView first_page = v.findViewById(R.id.btn_first_page_reply);
        TextView first_page2 = v.findViewById(R.id.btn_first_page_reply2);
        TextView last_page = v.findViewById(R.id.btn_end_reply);
        TextView last_page2 = v.findViewById(R.id.btn_end_reply2);

        img_like = v.findViewById(R.id.img_smile_thread);
        etSearch = v.findViewById(R.id.etsearch_selected_thread);
        scrollView = v.findViewById(R.id.scrollView_SelectedThread);
        etReply = v.findViewById(R.id.etReply_Selected);
        etName = v.findViewById(R.id.etName_Selected);
        error_box = v.findViewById(R.id.show_error_content_selected);
        img_more = v.findViewById(R.id.thread_more_selected);
        chosen_image = v.findViewById(R.id.chosen_image);
        trending_linear = v.findViewById(R.id.linear_trending_selected_thread);
        reply_linear = v.findViewById(R.id.linear_reply);
        recycler_main_forum = v.findViewById(R.id.recycler_img_selected);
        recycler_trending = v.findViewById(R.id.recycler_trending_selected_thread);
        recycler_reply = v.findViewById(R.id.recycler_reply);
        recycler_img_reply = v.findViewById(R.id.recycler_img_reply);
        recycler_page_number = v.findViewById(R.id.recycler_page_number);
        recycler_page_number2 = v.findViewById(R.id.recycler_page_number2);
        frameLayout = v.findViewById(R.id.frame_selected_thread);
        frame_close = v.findViewById(R.id.btn_close_frame_selected);
        img_download = v.findViewById(R.id.download_image_frame_selected);
        img_frame_selected = v.findViewById(R.id.image_frame_selected);
        merchant_name = v.findViewById(R.id.name_frame_selected_thread);
        amount_like = v.findViewById(R.id.smile_amount_thread);
        img_profile = v.findViewById(R.id.image_profile_selected_thread);
        relative_page_number = v.findViewById(R.id.relative_page_number);
        relative_page_number2 = v.findViewById(R.id.relative_page_number2);
        frame_loading = v.findViewById(R.id.frame_loading_selected_thread);

        frameLayout.getBackground().setAlpha(Constant.MAX_ALPHA);
        frame_loading.getBackground().setAlpha(Constant.MAX_ALPHA);

        threadList = new ArrayList<>();
        trendingList = new ArrayList<>();
        tempList = new ArrayList<>();
        imageList = new ArrayList<>();
        imageReply = new ArrayList<>();
        forumImageList = new ArrayList<>();
        replyList = new ArrayList<>();

        merchantMap = new HashMap<>();
        forumImageReplyMap = new HashMap<>();

        setAdapter();

        loadData();

        imageButton.setOnClickListener(this);
        reply.setOnClickListener(this);
        send.setOnClickListener(this);
        gallery_opener.setOnClickListener(this);
        relativeLayout.setOnClickListener(this);

        frameLayout.setOnClickListener(this);
        frame_close.setOnClickListener(this);
        img_download.setOnClickListener(this);
        img_like.setOnClickListener(this);
        img_more.setOnClickListener(this);

        etSearch.addTextChangedListener(this);
        etSearch.setOnClickListener(this);
        etSearch.setOnKeyListener(this);

        scrollView.setSmoothScrollingEnabled(true);
        scrollView.setNestedScrollingEnabled(false);
        scrollView.setOnTouchListener(this);

        after.setOnClickListener(this);
        before.setOnClickListener(this);
        first_page.setOnClickListener(this);
        last_page.setOnClickListener(this);

        first_page2.setOnClickListener(this);
        before2.setOnClickListener(this);
        after2.setOnClickListener(this);
        last_page2.setOnClickListener(this);

        new AsyncTasks().doInBackground();
    }

    private class AsyncTasks extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            TextView merchant_name = v.findViewById(R.id.merchantName_Selected);
            TextView merchant_loc = v.findViewById(R.id.merchantLoc_Selected);
            TextView thread_title = v.findViewById(R.id.title_selected);
            TextView thread_time = v.findViewById(R.id.time_selected);
            TextView reply_to = v.findViewById(R.id.reply_to);
            TextView content = v.findViewById(R.id.content_selected);

            Bundle bundles = getArguments();
            if (bundles != null) {
                if (bundles.getParcelable(GET_THREAD_OBJECT) != null) {
                    /*
                     * Disini kita mendapatkan object yang dibungkus oleh parcelable dari Main Forum
                     * Agar data yang diklik sebelumnya bisa ter-show
                     * */
                    forum = bundles.getParcelable(GET_THREAD_OBJECT);
                    thread_title.setText(forum.getForum_title());
                    content.setText(forum.getForum_content());
                    thread_time.setText(forum.getForum_date() + " WIB");
                    amount_like.setText(forum.getForum_like() + "");

                    dbRef.child(forum.getFid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            replyList.clear();

                            if (dataSnapshot.child("forum_like").getValue() != null)
                                forum.setForum_like(Integer.parseInt(dataSnapshot.child("forum_like").getValue().toString()));

                            amount_like.setText(forum.getForum_like() + "");

                            for (DataSnapshot snapshot : dataSnapshot.child(Constant.DB_REFERENCE_FORUM_REPLY).getChildren()) {
                                /*
                                 * Untuk mendapatkan setiap reply dari forum yang diklik oleh user
                                 * */
                                final Forum.ForumReply forumReply = snapshot.getValue(Forum.ForumReply.class);

                                forumReply.setLike(false);

                                for (DataSnapshot likeSnapshot : snapshot.child("forum_like_by").getChildren()) {
                                    if (likeSnapshot.getKey().equals(prefConfig.getMID())) {
                                        forumReply.setLike(true);
                                        break;
                                    }
                                }

                                replyList.add(forumReply);

                                for (DataSnapshot ss : snapshot.getChildren()) {
                                    // Forum Image Reply
                                    List<Forum.ForumImageReply> list = new ArrayList<>();
                                    for (DataSnapshot snapImageReply : ss.getChildren()) {
                                        Forum.ForumImageReply forumImageReply = snapImageReply.getValue(Forum.ForumImageReply.class);
                                        if (forumImageReply.getImage_url() != null)
                                            list.add(forumImageReply);
                                    }
                                    if (list.size() > 0) {
                                        forumImageReplyMap.put(forumReply.getFrid(), list);
                                        replyAdapter.setReplyImageMap(forumImageReplyMap);
                                        replyAdapter.notifyDataSetChanged();
                                    }
                                }

                                dbProfile.child(forumReply.getMid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot s) {
                                        /*
                                         * Untuk mendapatkan data merchant dari tree merchant_profile
                                         * Menggunakan Map untuk tidak membuang-buang resource jika menggunakan array
                                         * */
                                        if (s.getValue(Merchant.class) == null || forumReply.getMid() == null || merchantMap.containsKey(forumReply.getMid())) {
                                            return;
                                        }
                                        merchantMap.put(forumReply.getMid(), Objects.requireNonNull(s.getValue(Merchant.class)));
                                        replyAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                            replyAdapter.setList(replyList, merchantMap, forumImageReplyMap);
                            replyAdapter.notifyDataSetChanged();

                            if (replyList.size() > 0) {
                                relative_page_number.setVisibility(View.VISIBLE);
                                relative_page_number2.setVisibility(View.VISIBLE);
                                if (isReply) {
                                    isReply = false;
                                } else setList(PAGE_NUMBER_STATE);
                            } else {
                                relative_page_number.setVisibility(View.GONE);
                                relative_page_number2.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    dbRef.child(forum.getFid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            forumImageList.clear();
                            isLike = false;
                            Forum.ForumLikeBy forumLikeBy = dataSnapshot.getValue(Forum.ForumLikeBy.class);

                            for (DataSnapshot a : dataSnapshot.child("forum_like_by").getChildren()) {
                                if (a.getKey().equals(prefConfig.getMID())) {
                                    isLike = true;
                                    break;
                                }
                            }

                            if (isLike) {
                                img_like.setBackground(mContext.getResources().getDrawable(R.drawable.smile_press));
                            } else {
                                img_like.setBackground(mContext.getResources().getDrawable(R.drawable.smile));
                            }

                            for (DataSnapshot s : dataSnapshot.child(Constant.DB_REFERENCE_FORUM_IMAGE).getChildren()) {
                                /*
                                 * untuk mendapatkan image dari Thread Utamanya
                                 * */
                                Forum.ForumImage forumImage = new Forum.ForumImage(s.child("fiid").getValue().toString()
                                        , s.child("image_name").getValue().toString(), s.child("image_url").getValue().toString());
                                forumImageList.add(forumImage);
                            }
                            if (forumImageList.size() > 0) {
                                imageGridAdapter.setForumImageList(forumImageList);
                                imageGridAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    hideSoftKeyboard(mActivity);
                }

                if (bundles.getParcelable(GET_MERCHANT) != null) {
                    merchant = bundles.getParcelable(GET_MERCHANT);
                    Picasso.get().load(merchant.getMerchant_profile_picture()).into(img_profile);
                    merchant_name.setText(merchant.getMerchant_name());
                    reply_to.setText(merchant.getMerchant_name());
                    merchant_loc.setText(merchant.getMerchant_location());
                }
            }
            return null;
        }
    }

    private void loadData() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                trendingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Forum forum = snapshot.getValue(Forum.class);
                    trendingList.add(forum);
                }
                trendingAdapter.setTrendingList(trendingList);
                trendingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int getPageNumber(int reply_amount) {
        int pageAll = reply_amount / AMOUNT_REPLY;
        if (reply_amount % AMOUNT_REPLY > 0) {
            pageAll++;
        }
        return pageAll;
    }

    private void makeVisible() {
        trendingIsVisible = true;
        trending_linear.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) trending_linear.getLayoutParams();
        lp.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
    }

    private void setLayoutManager(RecyclerView recyclerView, RecyclerView.Adapter adapter, int STATE) {
        if (STATE == STATE_LINEAR_VERTICAL)
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        else if (STATE == STATE_LINEAR_HORIZONTAL)
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        else recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerView.setAdapter(adapter);
    }

    private void setAdapter() {
        imageGridAdapter = new ImageGridAdapter(mContext, forumImageList, this);
        setLayoutManager(recycler_main_forum, imageGridAdapter, STATE_GRID);

        trendingAdapter = new TrendingAdapter(mContext, trendingList, this);
        setLayoutManager(recycler_trending, trendingAdapter, STATE_LINEAR_VERTICAL);

        replyAdapter = new ReplyAdapter(replyList, merchantMap, forumImageReplyMap, mContext, this, this, this);
        setLayoutManager(recycler_reply, replyAdapter, STATE_LINEAR_VERTICAL);

        pickerAdapter = new ImagePickerAdapter(mContext, imageReply, this, "");
        setLayoutManager(recycler_img_reply, pickerAdapter, STATE_GRID);

        pageNumberAdapter = new PageNumberAdapter(mContext, getPageNumber(threadList.size()), this);
        setLayoutManager(recycler_page_number, pageNumberAdapter, STATE_LINEAR_HORIZONTAL);
        setLayoutManager(recycler_page_number2, pageNumberAdapter, STATE_LINEAR_HORIZONTAL);

        replyAdapter.setImageFrame(frame_close, img_download, merchant_name, img_frame_selected);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_selected_thread:
                search();
                break;
            case R.id.etsearch_selected_thread:
                Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_up);
                Animation animation2 = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_down);
                if (trendingIsVisible) {
                    trending_linear.setAnimation(animation);
                    removeTrending(view.getContext());
                } else {
                    makeVisible();
                    trending_linear.setAnimation(animation2);
                }
                break;
            case R.id.rl:
                removeTrending(view.getContext());
                break;
            case R.id.img_smile_thread:
                Map<String, Object> map = new HashMap<>();
                if (!isLike) {
                    img_like.setBackground(mContext.getResources().getDrawable(R.drawable.smile_press));
                    amount_like.setText(Integer.parseInt(amount_like.getText().toString()) + 1 + "");
                    isLike = true;
                    map.put("like", true);
                    map.put("forum_like", forum.getForum_like() + 1);
                    dbRef.child(forum.getFid()).updateChildren(map);

                    Map<String, String> maps = new HashMap<>();
                    maps.put("forum_like_time", Utils.getTime("EEEE, dd-MM-yyyy HH:mm"));
                    maps.put("forum_like_mid", prefConfig.getMID());

                    dbRef.child(forum.getFid() + "/forum_like_by/" + prefConfig.getMID()).setValue(maps);
                } else {
                    img_like.setBackground(mContext.getResources().getDrawable(R.drawable.smile));
                    amount_like.setText(Integer.parseInt(amount_like.getText().toString()) - 1 + "");
                    isLike = false;
                    map.put("like", false);
                    map.put("forum_like", forum.getForum_like() - 1);
                    dbRef.child(forum.getFid()).updateChildren(map);
                    dbRef.child(forum.getFid() + "/forum_like_by/" + prefConfig.getMID()).removeValue();
                }
                break;
            case R.id.thread_more_selected:
                Context option_menu = new ContextThemeWrapper(view.getContext(), R.style.PopupMenu);
                PopupMenu popupMenu = new PopupMenu(option_menu, img_more);
                if (merchant.getMid().equals(prefConfig.getMID())) {
                    popupMenu.inflate(R.menu.option_menu_forum_owner);
                } else popupMenu.inflate(R.menu.option_menu_forum_general);
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
                break;
            case R.id.btn_reply_thread:
                scrollView.smoothScrollTo(0, reply_linear.getBottom());
                break;
            case R.id.gallery_opener_reply:
                if (ActivityCompat.checkSelfPermission(mContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                            , Constant.PERMISSION_READ_GALLERY_EXTERNAL);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, Constant.ACTIVITY_CHOOSE_IMAGE);
                }
                break;
            case R.id.btn_send_selected:
                etReply.setBackground(mContext.getDrawable(R.drawable.background_edit_text));
                error_box.setVisibility(View.GONE);
                if (etReply.getText().toString().trim().isEmpty()) {
                    scrollView.smoothScrollTo(scrollView.getScrollX(), reply_linear.getTop());
                    error_box.setVisibility(View.VISIBLE);
                    etReply.setBackground(mContext.getDrawable(R.drawable.background_edit_text_error));
                } else {
                    frame_loading.setVisibility(View.VISIBLE);
                    final String key = dbRef.push().getKey();
                    Forum.ForumReply forumReply = new Forum.ForumReply(key, prefConfig.getMID()
                            , etReply.getText().toString(), Utils.getTime("EEEE, dd/MM/yyyy HH:mm")
                            , 0, false);

                    if (imageReply.size() == 0) {
                        /*
                         * Untuk mengirim reply ke firebase
                         * Dikirim menurut urutan tree nya, berdasarkan Forum -> Forum ID -> Forum Reply -> Forum Reply ID -> Value
                         * */
                        dbRef.child(forum.getFid())
                                .child("forum_reply")
                                .child(key)
                                .setValue(forumReply)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        etReply.setText("");
                                        frame_loading.setVisibility(View.GONE);
                                        setList(getLastPosition());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("Upload Task", "failure");
                                    }
                                });
                    } else {
                        dbRef.child(forum.getFid())
                                .child("forum_reply")
                                .child(key)
                                .setValue(forumReply)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        /*
                                         * Berfungsi untuk mereply sebuah thread
                                         * Jika sudah berhasil maka akan melooping gambar yang ada untuk diupload
                                         * dan diupdate ke dalam tree forum reply
                                         * */
                                        for (int i = 0; i < imageReply.size(); i++) {
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            imageReply.get(i).getImage_bitmap().compress(Bitmap.CompressFormat.JPEG, 30, baos);
                                            byte[] byteData = baos.toByteArray();

                                            final String keys = dbRef.child(forum.getFid()).child("forum_reply").child("forum_image_reply").push().getKey();

                                            Random random = new Random();
                                            final int ran = random.nextInt(10000);
                                            final String imgName = "forum-reply-" + prefConfig.getMID() + "-" + key + "-" + ran;

                                            UploadTask uploadTask = storageReference.child(imgName).putBytes(byteData);
                                            /*
                                             * Untuk upload gambar ke firebase
                                             * */
                                            final int finalI = i;
                                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    storageReference.child(imgName)
                                                            .getDownloadUrl()
                                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {
                                                                    Forum.ForumImageReply forumImageReply = new Forum.ForumImageReply(keys, imgName, uri.toString());
                                                                    dbRef.child(forum.getFid())
                                                                            .child("forum_reply")
                                                                            .child(key)
                                                                            .child("forum_image_reply")
                                                                            .child(keys)
                                                                            .setValue(forumImageReply)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    if (finalI == imageReply.size() - 1) {
                                                                                        frame_loading.setVisibility(View.GONE);
                                                                                        etName.setText("");
                                                                                        etReply.setText("");
                                                                                        chosen_image.setVisibility(View.GONE);
                                                                                        imageReply.clear();
                                                                                        pickerAdapter.setImageList(imageReply);
                                                                                        setList(getLastPosition());
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            });
                                                }
                                            });
                                        }
                                    }
                                });
                    }
                }
                break;
            case R.id.btn_first_page_reply:
            case R.id.btn_first_page_reply2:
                setList(0);
                scrollView.smoothScrollTo(0, recycler_reply.getTop());
                break;
            case R.id.btn_end_reply:
            case R.id.btn_end_reply2:
                setList(pageNumberAdapter.getPageList() - 1);
                scrollView.smoothScrollTo(0, recycler_reply.getTop());
                break;
            case R.id.btn_before_reply:
            case R.id.btn_before_reply2:
                if (pageNumberAdapter.getPage_active() - 1 < 0) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.start_of_thread), Toast.LENGTH_SHORT).show();
                    break;
                }
                setList(pageNumberAdapter.getPage_active() - 1);
                break;
            case R.id.btn_after_reply:
            case R.id.btn_after_reply2:
                if (pageNumberAdapter.getPage_active() + 1 >= pageNumberAdapter.getPageList()) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.end_of_thread), Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    setList(pageNumberAdapter.getPage_active() + 1);
                    scrollView.smoothScrollTo(0, recycler_reply.getTop());
                }
                break;
            case R.id.frame_selected_thread:
            case R.id.btn_close_frame_selected:
                frameLayout.setVisibility(View.GONE);
                frameIsVisible = false;
                MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
                break;
            case R.id.download_image_frame_selected:
                if (ActivityCompat.checkSelfPermission(mContext
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                            , Constant.PERMISSION_WRITE_EXTERNAL);
                } else {
                    Bitmap bitmap = ((BitmapDrawable) img_frame_selected.getDrawable()).getBitmap();
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.download_success), Toast.LENGTH_SHORT).show();
                    MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), bitmap
                            , merchant_name.getText().toString(), "");
                }
                break;
        }
    }

    private void removeTrending(Context context) {
        if (trendingIsVisible) {
            trendingIsVisible = false;
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
            trending_linear.setAnimation(animation);
            hideSoftKeyboard(mActivity);
            trending_linear.setVisibility(View.GONE);
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity == null) return;
        else if (activity.getCurrentFocus() == null) return;
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        makeVisible();
    }

    @Override
    public void afterTextChanged(Editable editable) {
        tempList.clear();
        makeVisible();
        if (editable.toString().isEmpty()) {
            trendingAdapter.setTrendingList(trendingList);
        } else {
            for (int i = 0; i < trendingList.size(); i++) {
                if (trendingList.get(i).getForum_title().toLowerCase().trim().contains(editable.toString().toLowerCase().trim())) {
                    tempList.add(trendingList.get(i));
                }
            }
            trendingAdapter.setTrendingList(tempList);
        }
        trendingAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        switch (view.getId()) {
            case R.id.etSearch:
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (i) {
                        case KeyEvent.KEYCODE_ENTER:
                            search();
                            return true;
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public void onItemClick(int pos, List<Forum> forumThreads) {
        if (forumThreads.size() == 0) {
            Log.e("HEHE", "PENCARIAN");
        } else {
            Forum thread = forumThreads.get(pos);
            SelectedThread selectedThread = new SelectedThread();
            Bundle bundle = new Bundle();
            FragmentManager fragmentManager = getFragmentManager();

            bundle.putParcelable(GET_THREAD_OBJECT, (Parcelable) thread);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.main_frame, selectedThread);

            selectedThread.setArguments(bundle);
            fragmentTransaction.commit();
        }
        hideSoftKeyboard(mActivity);
    }

    @Override
    public void onBackPress(boolean check, Context context) {
        removeTrending(context);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.scrollView_SelectedThread:
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE)
                    if (trendingIsVisible) removeTrending(view.getContext());
                break;
        }
        return false;
    }

    @Override
    public void onItemClicked(int pos, String states) {
        if (states.equals(ImagePickerAdapter.STATES_CLICKED_DELETE)) {
            imageReply.remove(pos);
            pickerAdapter.setImageList(imageReply);
            if (imageReply.size() == 0) chosen_image.setVisibility(View.GONE);
        } else {
            frameLayout.setVisibility(View.VISIBLE);
            frameIsVisible = true;
            merchant_name.setText("Merchant Name : " + merchant.getMerchant_name());
            DecodeBitmap.setScaledImageView(img_frame_selected, imageList.get(pos).getImg_static(), mContext);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
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
            case Constant.PERMISSION_WRITE_EXTERNAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Bitmap bitmap = ((BitmapDrawable) img_frame_selected.getDrawable()).getBitmap();
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.download_success), Toast.LENGTH_SHORT).show();
                    MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), bitmap
                            , merchant_name.getText().toString(), "");
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_failed), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.ACTIVITY_CHOOSE_IMAGE) {
                Bitmap bitmap;
                if (data.getData() != null) {
                    Uri targetUri = data.getData();
                    File f = new File("" + targetUri);
                    try {
                        chosen_image.setVisibility(View.VISIBLE);
                        bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(targetUri));
                        imageReply.add(new ImagePicker(DecodeBitmap.compressBitmap(bitmap), "IMG", f.getName()));
                        pickerAdapter.setImageList(imageReply);
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
                            imageReply.add(new ImagePicker(DecodeBitmap.compressBitmap(bitmap), "IMG", f.getName()));
                            pickerAdapter.setImageList(imageReply);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Apa anda yakin untuk menghapus thread ini?");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dbRef.child(forum.getFid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                MainForum mainForum = new MainForum();

                                FragmentManager manager = getFragmentManager();
                                FragmentTransaction transaction = manager.beginTransaction();

                                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                                transaction.replace(R.id.main_frame, mainForum);

                                transaction.commit();
                                Toast.makeText(mContext, mContext.getResources().getString(R.string.thread_deleted), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();

                break;
            case R.id.menu_edit:
                if (forum != null) {
                    NewThread newThread = new NewThread();

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(NewThread.EDIT_THREAD_SELECTED, forum);

                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    fragmentTransaction.replace(R.id.main_frame, newThread);

                    newThread.setArguments(bundle);
                    fragmentTransaction.commit();
                }
                break;
            case R.id.menu_report:
                final List<Report> reportList = new ArrayList<>();
                reportList.addAll(Constant.getReport());

                AlertDialog.Builder codeBuilder = new AlertDialog.Builder(mContext);
                final View codeView = LayoutInflater.from(mContext).inflate(R.layout.custom_report, null);

                final TextView error = codeView.findViewById(R.id.show_error_content_report);
                final EditText content = codeView.findViewById(R.id.etOther_Report);
                final CheckBox checkBox = codeView.findViewById(R.id.check_other);
                ReportAdapter reportAdapter = new ReportAdapter(reportList, codeView.getContext());

                TextView name = codeView.findViewById(R.id.report_name);
                TextView thread = codeView.findViewById(R.id.report_title);
                TextView threadTitle = codeView.findViewById(R.id.report_tv_title);
                Button send = codeView.findViewById(R.id.btnSubmit_Report);
                Button cancel = codeView.findViewById(R.id.btnCancel_Report);
                RecyclerView recyclerView = codeView.findViewById(R.id.recycler_checkbox_report);

                recyclerView.setLayoutManager(new GridLayoutManager(codeView.getContext(), 2));

                codeBuilder.setView(codeView);
                final AlertDialog codeAlert = codeBuilder.create();

                name.setText(merchant.getMerchant_name());
                thread.setText(forum.getForum_title());

                recyclerView.setAdapter(reportAdapter);

                thread.setVisibility(View.GONE);
                threadTitle.setVisibility(View.GONE);
                content.setEnabled(false);

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isCheck) {
                            content.setBackground(codeView.getContext().getDrawable(R.drawable.background_stroke_white));
                            isCheck = true;
                            content.setEnabled(true);
                        } else {
                            content.setBackground(codeView.getContext().getDrawable(R.drawable.background_grey));
                            isCheck = false;
                            content.setEnabled(false);
                        }
                    }
                });

                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        error.setVisibility(View.GONE);
                        if (isCheck) {
                            if (content.getText().toString().isEmpty())
                                error.setVisibility(View.VISIBLE);
                            else {
                                Toast.makeText(codeView.getContext(), codeView.getContext().getResources().getString(R.string.report_sent)
                                        , Toast.LENGTH_SHORT).show();
                                codeAlert.dismiss();
                            }
                        } else if (isChecked(reportList)) {
                            Toast.makeText(codeView.getContext(), codeView.getContext().getResources().getString(R.string.report_sent)
                                    , Toast.LENGTH_SHORT).show();
                            codeAlert.dismiss();
                        } else {
                            error.setVisibility(View.VISIBLE);
                        }
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        codeAlert.dismiss();
                    }
                });

                codeAlert.show();
                break;
        }
        return false;
    }

    @Override
    public void onReply(int pos) {
        scrollView.smoothScrollTo(0, reply_linear.getBottom());
    }

    @Override
    public void onReplyLike(int pos) {
        int position;
        if (pageNumberAdapter.getPage_active() == 0) {
            position = pos;
        } else {
            position = (pageNumberAdapter.getPage_active() * AMOUNT_REPLY) + pos;
        }
        Forum.ForumReply forumReply = replyList.get(position);
        Map<String, Object> map = new HashMap<>();
        if (!forumReply.isLike()) {
            map.put("like", true);
            map.put("forum_like_amount", forumReply.getForum_like_amount() + 1);
            dbRef.child(forum.getFid() + "/forum_reply/" + forumReply.getFrid()).updateChildren(map);

            Map<String, String> maps = new HashMap<>();
            maps.put("forum_like_time", Utils.getTime("EEEE, dd-MM-yyyy HH:mm"));
            maps.put("forum_like_mid", prefConfig.getMID());

            dbRef.child(forum.getFid() + "/forum_reply/" + forumReply.getFrid() + "/forum_like_by/" + prefConfig.getMID()).setValue(maps);
        } else {
            map.put("like", false);
            map.put("forum_like_amount", forumReply.getForum_like_amount() - 1);
            dbRef.child(forum.getFid() + "/forum_reply/" + forumReply.getFrid()).updateChildren(map);
            dbRef.child(forum.getFid() + "/forum_reply/" + forumReply.getFrid() + "/forum_like_by/" + prefConfig.getMID()).removeValue();
        }
    }

    private void search() {
        String result = etSearch.getText().toString();
        if (!result.trim().isEmpty()) {
            MainForum mainForum = new MainForum();
            Bundle bundle = new Bundle();
            FragmentManager fragmentManager = getFragmentManager();

            bundle.putString(MainForum.BUNDLE_SEARCH, result);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.main_frame, mainForum);

            mainForum.setArguments(bundle);
            fragmentTransaction.commit();
        }
        hideSoftKeyboard(mActivity);
    }

    @Override
    public void onThreadEdit(int pos) {
        Forum.ForumReply thread = replyList.get(pos);
        NewThread newThread = new NewThread();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putParcelable(NewThread.EDIT_THREAD_REPLY, thread);
        bundle.putParcelable(NewThread.EDIT_THREAD_REPLY_BACK, forum);
        bundle.putParcelable(NewThread.EDIT_THREAD_MERCHANT, merchant);

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, newThread);

        newThread.setArguments(bundle);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(int pos) {
        setList(pos);
    }

    private int getLastPosition() {
//        Log.e("asd", replyList.size() + "");
        if (replyList.size() / AMOUNT_REPLY == 0) {
            return 0;
        } else if (replyList.size() % AMOUNT_REPLY == 0) {
            return replyList.size() / AMOUNT_REPLY == 0 ? 0 : (replyList.size() / AMOUNT_REPLY) - 1;
        } else {
            return replyList.size() / AMOUNT_REPLY;
        }
//        return 1;
    }

    private void setList(int pos) {
        List<Forum.ForumReply> threads = new ArrayList<>();
        PAGE_NUMBER_STATE = pos;
        int thread_start = PAGE_NUMBER_STATE * AMOUNT_REPLY;
        int thread_end = thread_start + AMOUNT_REPLY;
        for (int i = thread_start; i < thread_end; i++) {
            if (i >= replyList.size()) break;
            threads.add(replyList.get(i));
        }
        pageNumberAdapter.setPage_active(PAGE_NUMBER_STATE);
        replyAdapter.setList(threads);
        replyAdapter.notifyDataSetChanged();
        if (pageNumberAdapter.getPageList() != getPageNumber(replyList.size())) {
            pageNumberAdapter.setPageList(getPageNumber(replyList.size()));
        }
    }

    private void setDeletedList(int pos) {
        List<Forum.ForumReply> threads = new ArrayList<>();
        PAGE_NUMBER_STATE = pos;
        int thread_start = PAGE_NUMBER_STATE * AMOUNT_REPLY;
        int thread_end = thread_start + AMOUNT_REPLY;
        for (int i = thread_start; i < thread_end; i++) {
            if (i >= replyList.size()) break;
            threads.add(replyList.get(i));
        }
        pageNumberAdapter.setPage_active(PAGE_NUMBER_STATE);
        replyAdapter.setList(replyList);
        if (pageNumberAdapter.getPageList() != getPageNumber(replyList.size())) {
            pageNumberAdapter.setPageList(getPageNumber(replyList.size()));
        }
    }

    @Override
    public void onDelete(int pos) {
        replyList.remove(pos);
        int PAGE_POSITION = pos / AMOUNT_REPLY;
        if (replyList.size() > pos) setDeletedList(PAGE_POSITION);
        else if (PAGE_POSITION - 1 < 0) setDeletedList(PAGE_POSITION);
        else setDeletedList(PAGE_POSITION - 1);
        // kodingin ke android
    }

    @Override
    public void imageOnClick(int pos) {
        frameIsVisible = true;
        frameLayout.setVisibility(View.VISIBLE);
        Picasso.get().load(forumImageList.get(pos).getImage_url()).into(img_frame_selected);
        MainActivity.bottomNavigationView.setVisibility(View.GONE);
    }

    private boolean isChecked(List<Report> list) {
        for (Report reportIsChecked : list) {
            if (reportIsChecked.isReport_checked()) return true;
        }
        return false;
    }
}
