package com.andrew.prototype.Fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.prototype.Activity.MainActivity;
import com.andrew.prototype.Adapter.ShowcaseAdapter;
import com.andrew.prototype.Adapter.ThreadAdapter;
import com.andrew.prototype.Adapter.TrendingAdapter;
import com.andrew.prototype.Model.Forum;
import com.andrew.prototype.Model.ForumThread;
import com.andrew.prototype.Model.Merchant;
import com.andrew.prototype.Model.MerchantStory;
import com.andrew.prototype.R;
import com.andrew.prototype.Utils.Constant;
import com.andrew.prototype.Utils.DecodeBitmap;
import com.andrew.prototype.Utils.PrefConfig;
import com.andrew.prototype.Utils.Utils;
import com.baoyz.widget.PullRefreshLayout;
import com.bumptech.glide.Glide;
import com.developers.coolprogressviews.ColorfulProgress;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainForum extends Fragment implements View.OnClickListener
        , MainActivity.onBackPressFragment, TextWatcher, TrendingAdapter.itemClickListener
        , ShowcaseAdapter.onImageClickListener, ThreadAdapter.onItemClick, View.OnKeyListener
        , View.OnTouchListener, PullRefreshLayout.OnRefreshListener, NestedScrollView.OnScrollChangeListener
        , ThreadAdapter.onItemDelete {

    public static final String BUNDLE_SEARCH = "BUNDLE_SEARCH";

    public static FrameLayout frame_showcase;
    public static boolean trendingIsVisible, showcase_condition, isSearch;

    private static final String TAG = MainForum.class.getSimpleName();

    private static RecyclerView showcase_recycler_view, trending_recycler_view;
    private static ShowcaseAdapter showcaseAdapter;
    private static TrendingAdapter trendingAdapter;
    private static List<Forum> trendingArrayList, tempList, searchList, forumLists;
    private static LinearLayout trending_linear;
    private static List<MerchantStory> showCaseList;

    private View v;
    private RecyclerView thread_recycler_view;
    private ThreadAdapter threadAdapter;
    private EditText etSearch;
    private TextView tvResult, tvError_AddShowCase;
    private PullRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager threadLayoutManager;
    private ColorfulProgress colorfulProgress;
    private ImageView img_showcase, img_add_showcase;
    private AlertDialog codeAlert;
    private Context mContext;
    private Activity mActivity;
    private ScaleDrawable scaleDrawable;
    private DatabaseReference dbStory, dbForum, dbProfile;
    private StorageReference storageStory;
    private PrefConfig prefConfig;
    private FrameLayout frame_loading;

    private Map<String, Merchant> merchantMap;
    private boolean c;
    private int page_number;
    private boolean isLoad;
    private int pastVisibleItems, visibleItemCounts, totalItemCount, previous_total;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_main_forum, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        mContext = v.getContext();

        c = false;
        page_number = 1;
        isLoad = true;
        previous_total = 0;
        trendingIsVisible = false;
        showcase_condition = false;
        isSearch = false;

        prefConfig = new PrefConfig(mContext);
        dbStory = FirebaseDatabase.getInstance().getReference(Constant.DB_REFERENCE_MERCHANT_STORY);
        dbForum = FirebaseDatabase.getInstance().getReference(Constant.DB_REFERENCE_FORUM);
        dbProfile = FirebaseDatabase.getInstance().getReference(Constant.DB_REFERENCE_MERCHANT_PROFILE);
        storageStory = storage.getReference(Constant.DB_REFERENCE_MERCHANT_STORY);
        scaleDrawable = DecodeBitmap.setScaleDrawable(mContext, R.drawable.placeholder);

        LinearLayout linearLayout = v.findViewById(R.id.parentll_main_forum);
        ImageButton search = v.findViewById(R.id.imgBtn_Search);
        ImageButton new_thread = v.findViewById(R.id.imgBtn_AddThread);
        ImageButton close = v.findViewById(R.id.btn_close_frame);
        ImageButton file_download = v.findViewById(R.id.download_image_frame);
        RelativeLayout relativeLayout = v.findViewById(R.id.rl_frame_main);
        NestedScrollView scrollView = v.findViewById(R.id.nestedScrollView_MainForum);

        showcase_recycler_view = v.findViewById(R.id.recyclerView_MainForum);
        trending_recycler_view = v.findViewById(R.id.recyclerTrending_MainForum);
        trending_linear = v.findViewById(R.id.linear_trending);
        frame_showcase = v.findViewById(R.id.picture_frame);

        etSearch = v.findViewById(R.id.etSearch);
        swipeRefreshLayout = v.findViewById(R.id.swipe);
        tvResult = v.findViewById(R.id.tv_result);
        colorfulProgress = v.findViewById(R.id.colorfulProgress);
        img_showcase = v.findViewById(R.id.image_frame);
        thread_recycler_view = v.findViewById(R.id.recyclerThread_MainForum);
        frame_loading = v.findViewById(R.id.frame_loading_main_forum);

        trendingArrayList = new ArrayList<>();
        tempList = new ArrayList<>();
        searchList = new ArrayList<>();
        showCaseList = new ArrayList<>();

        forumLists = new ArrayList<>();

        merchantMap = new HashMap<>();

        threadLayoutManager = new LinearLayoutManager(mContext);
        swipeRefreshLayout.setDurations(0, 3);

        tvResult.setVisibility(View.GONE);

        setAdapter(v);

        final Bundle bundle = getArguments();
        if (bundle != null) {
            /*
             * Untuk mendapatkan hasil search dari Selected Forum
             * */
            if (bundle.getString(BUNDLE_SEARCH) != null) {
                final String result = bundle.getString(BUNDLE_SEARCH);
                tvResult.setVisibility(View.VISIBLE);
                tvResult.setText("Hasil dari \'" + result + "\'");
                forumLists.clear();

                dbForum.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        trendingArrayList.clear();
                        forumLists.clear();
                        merchantMap.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final Forum forum = snapshot.getValue(Forum.class);
                            if (forum.getForum_title().trim().toLowerCase().contains(result.trim().toLowerCase())) {
                                dbProfile.child(forum.getMid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshots) {
                                        if (dataSnapshots.getValue(Merchant.class) == null || forum.getMid() == null || merchantMap.containsKey(forum.getMid())) {
                                            return;
                                        }
                                        merchantMap.put(forum.getMid(), Objects.requireNonNull(dataSnapshots.getValue(Merchant.class)));
                                        threadAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                forumLists.add(0, forum);
                            }
                        }
                        threadAdapter.setForumList(forumLists, merchantMap);

                        threadAdapter.notifyDataSetChanged();
                        isSearch = true;
                        bundle.remove(BUNDLE_SEARCH);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                tvResult.setVisibility(View.GONE);
                isSearch = false;
            }
        }

        loadData();

        removeTrending(mContext);

        etSearch.addTextChangedListener(this);
        etSearch.setOnClickListener(this);
        etSearch.setOnKeyListener(this);

        file_download.setOnClickListener(this);
        relativeLayout.setOnClickListener(this);
        close.setOnClickListener(this);
        linearLayout.setOnClickListener(this);
        new_thread.setOnClickListener(this);
        search.setOnClickListener(this);
        img_showcase.setOnClickListener(this);
        frame_showcase.setOnClickListener(this);

        scrollView.setOnTouchListener(this);
        scrollView.setOnScrollChangeListener(this);

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBtn_Search:
                search();
                break;
            case R.id.etSearch:
                /*
                 * Jika kolom search di klik maka akan masuk kedalam kondisi dibawah
                 * */
                if (trendingIsVisible) {
                    removeTrending(mContext);
                } else {
                    makeTrendingVisible();
                }
                break;
            case R.id.parentll_main_forum:
                removeTrending(view.getContext());
                break;
            case R.id.recyclerView_MainForum:
                removeTrending(view.getContext());
                break;
            case R.id.imgBtn_AddThread:
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.main_frame, new NewThread());
                fragmentTransaction.commit();
                break;
            case R.id.btn_close_frame:
                frame_showcase.setVisibility(View.GONE);
                MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_frame_main:
                frame_showcase.setVisibility(View.GONE);
                MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
                break;
            case R.id.download_image_frame:
                Bitmap bitmap = ((BitmapDrawable) img_showcase.getDrawable()).getBitmap();
                int position = showcaseAdapter.getAdapterPosition();
                if (ActivityCompat.checkSelfPermission(mActivity
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSION_WRITE_EXTERNAL);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.download_success), Toast.LENGTH_SHORT).show();
                    MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), bitmap
                            , showCaseList.get(position).getSid(), "");
                }
                break;
            case R.id.btnSubmit_AddShowCase:
                tvError_AddShowCase.setVisibility(View.GONE);
                if (img_add_showcase.getDrawable() == null) {
                    tvError_AddShowCase.setVisibility(View.VISIBLE);
                } else {
                    Random random = new Random();
                    final int ran = random.nextInt(10000);
                    frame_loading.setVisibility(View.VISIBLE);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ((BitmapDrawable) img_add_showcase.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG, 30, baos);
                    byte[] byteData = baos.toByteArray();

                    final UploadTask uploadTask = storageStory.child("story-" + prefConfig.getMID() + "-" + ran).putBytes(byteData);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageStory.child("story-" + prefConfig.getMID() + "-" + ran).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(final Uri uri) {
                                    /*
                                     * Apabila data tersebut telah dihapus, maka sekarang mencari URL dr foto yang diupload
                                     * Data merchant akan diupdate sesuai dengan URL terbaru
                                     * */
                                    String key = dbStory.child(prefConfig.getMID() + "").push().getKey();

                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("sid", key);
                                    map.put("story_picture", uri.toString());
                                    map.put("story_date", Utils.getTime("yyyy-MM-dd HH:mm"));

                                    dbStory.child(prefConfig.getMID() + "").child(key).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            /*
                                             * Kondisi dibawah ini akan berjalan jika value sudah berhasil diUpdate
                                             * */
                                            Toast.makeText(mContext, mContext.getResources().getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
                                            frame_loading.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                    codeAlert.dismiss();
                }
                break;
            case R.id.btnCancel_AddShowCase:
                codeAlert.dismiss();
                break;
            case R.id.imgView_AddShowCase:
                if (ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constant.PERMISSION_READ_GALLERY_EXTERNAL);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, Constant.ACTIVITY_CHOOSE_IMAGE);
                }
                break;
        }
    }

    private void makeTrendingVisible() {
        /*
         * Merupakan function yang berfungsi untuk memunculkan trending list pada search box
         * */
        Animation animation2 = AnimationUtils.loadAnimation(mContext, R.anim.slide_down);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) trending_linear.getLayoutParams();

        lp.height = RelativeLayout.LayoutParams.WRAP_CONTENT;

        trendingIsVisible = true;
        trending_linear.setVisibility(View.VISIBLE);
        trending_recycler_view.setVisibility(View.VISIBLE);
        trending_linear.setAnimation(animation2);
    }

    private void loadData() {
        dbStory
//                .orderByChild("story_date")
//                .startAt(Utils.getTime("yyyy-MM-") + "01")
//                .endAt(Utils.getTime("yyyy-MM-") + Utils.gettingAmountDaysOfMonth())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            showCaseList.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String URL = "";
                                String MID = "";
                                String date = "";
                                String SID = "";
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    SID = snapshot1.child("sid").getValue(String.class);
                                    date = snapshot1.child("story_date").getValue(String.class);
                                    URL = snapshot1.child("story_picture").getValue(String.class);
                                    MID = snapshot.getRef().getKey();
                                }
                                showCaseList.add(0, new MerchantStory(URL, SID, MID, date));
                            }

                            Collections.sort(showCaseList, new Comparator<MerchantStory>() {
                                @Override
                                public int compare(MerchantStory t2, MerchantStory t1) {
                                    return t1.getStory_date().compareTo(t2.getStory_date());
                                }
                            });

                            showcaseAdapter.setShowCases(showCaseList);
                            showcaseAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        dbForum.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!isSearch) {
                    trendingArrayList.clear();
                    forumLists.clear();
                    merchantMap.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final Forum forum = snapshot.getValue(Forum.class);
                        dbProfile.child(forum.getMid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshots) {
                                if (dataSnapshots.getValue(Merchant.class) == null || forum.getMid() == null || merchantMap.containsKey(forum.getMid())) {
                                    return;
                                }
                                merchantMap.put(forum.getMid(), Objects.requireNonNull(dataSnapshots.getValue(Merchant.class)));
                                threadAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        trendingArrayList.add(forum);
                        forumLists.add(0, forum);
                    }
                    threadAdapter.setForumList(forumLists, merchantMap);
                    trendingAdapter.setTrendingList(trendingArrayList);

                    trendingAdapter.notifyDataSetChanged();
                    threadAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        if (!isSearch)
//            performPagination();
    }

    private void removeTrending(Context context) {
        trendingIsVisible = false;
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        trending_linear.setAnimation(animation);
        hideSoftKeyboard(mActivity);
        trending_linear.setVisibility(View.GONE);
        trending_recycler_view.setVisibility(View.GONE);
    }

    private void setAdapter(View view) {
        trendingAdapter = new TrendingAdapter(mContext, trendingArrayList, this);
        trending_recycler_view.setLayoutManager(new LinearLayoutManager(view.getContext()));
        trending_recycler_view.setAdapter(trendingAdapter);

        showcaseAdapter = new ShowcaseAdapter(mContext, showCaseList, true, this);
        showcase_recycler_view.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        showcase_recycler_view.setAdapter(showcaseAdapter);

        threadAdapter = new ThreadAdapter(mContext, forumLists, merchantMap, this, this);
        thread_recycler_view.setLayoutManager(threadLayoutManager);
        thread_recycler_view.setAdapter(threadAdapter);
    }

    @Override
    public void onBackPress(boolean checks, Context context) {
        removeTrending(context);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!trendingIsVisible) {
            makeTrendingVisible();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        tempList.clear();
        if (editable.toString().isEmpty()) {
            tempList.addAll(trendingArrayList);
            trendingAdapter.setTrendingList(trendingArrayList);
        } else {
            for (int i = 0; i < trendingArrayList.size(); i++) {
                if (trendingArrayList.get(i).getForum_title().toLowerCase().trim().contains(editable.toString().toLowerCase().trim())) {
                    tempList.add(trendingArrayList.get(i));
                }
            }
            trendingAdapter.setTrendingList(tempList);
        }
        trendingAdapter.notifyDataSetChanged();
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

            bundle.putParcelable(SelectedThread.GET_THREAD_OBJECT, thread);
            bundle.putParcelable(SelectedThread.GET_MERCHANT, merchantMap.get(thread.getMid()));

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.main_frame, selectedThread);

            selectedThread.setArguments(bundle);
            fragmentTransaction.commit();
            hideSoftKeyboard(mActivity);
        }
    }

    @Override
    public void onImageClick(Context context, int pos) {
        if (trendingIsVisible) {
            removeTrending(context);
        } else {
            if (pos == 0) {
                addShowCase();
            } else {
                TextView textView = v.findViewById(R.id.merchantName_MainForum);
                if (showCaseList.get(pos - 1).getMid().equals(prefConfig.getMID())) {
                    textView.setText("Merchant Name : " + prefConfig.getName());
                } else {
                    textView.setText("Merchant Name : " + showCaseList.get(pos - 1).getMid());
                }
                showcase_condition = true;
                frame_showcase.setVisibility(View.VISIBLE);
                frame_showcase.getBackground().setAlpha(230);
                Picasso.get().load(showCaseList.get(pos - 1).getStory_picture()).into(img_showcase);
                MainActivity.bottomNavigationView.setVisibility(View.GONE);
            }
        }
    }

    private void addShowCase() {
        AlertDialog.Builder codeBuilder = new AlertDialog.Builder(mContext);
        View codeView = getLayoutInflater().inflate(R.layout.custom_add_show_case, null);
        img_add_showcase = codeView.findViewById(R.id.imgView_AddShowCase);
        tvError_AddShowCase = codeView.findViewById(R.id.show_error_content_add_show_case);
        Button submit = codeView.findViewById(R.id.btnSubmit_AddShowCase);
        Button cancel = codeView.findViewById(R.id.btnCancel_AddShowCase);
        img_add_showcase.setOnClickListener(this);
        submit.setOnClickListener(this);
        cancel.setOnClickListener(this);
        codeBuilder.setView(codeView);
        codeAlert = codeBuilder.create();
        codeAlert.show();
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity == null) return;
        else if (activity.getCurrentFocus() == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onClick(int pos, final Merchant merchant) {
        if (thread_recycler_view.isEnabled()) {
            final Forum forumThread = forumLists.get(pos);
            Map<String, Object> map = new HashMap<>();
            map.put(forumThread.getFid() + "/view_count", forumThread.getView_count() + 1);

            dbForum.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    SelectedThread selectedThread = new SelectedThread();
                    Bundle bundle = new Bundle();
                    FragmentManager fragmentManager = getFragmentManager();

                    bundle.putParcelable(SelectedThread.GET_THREAD_OBJECT, (Parcelable) forumThread);
                    bundle.putParcelable(SelectedThread.GET_MERCHANT, (Parcelable) merchant);

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    fragmentTransaction.replace(R.id.main_frame, selectedThread);

                    selectedThread.setArguments(bundle);
                    fragmentTransaction.commit();
                }
            });
        }
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.nestedScrollView_MainForum:
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE)
                    if (trendingIsVisible) removeTrending(mContext);
                break;
        }
        return false;
    }

    @Override
    public void onRefresh() {
        pastVisibleItems = 0;
        page_number = 1;
        totalItemCount = 1;
        previous_total = 0;
        visibleItemCounts = 0;
        etSearch.setText("");
        removeTrending(mContext);
        initVar();

        // use when data is loaded
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//        if (!isSearch) {
//            if (v.getChildAt(v.getChildCount() - 1) != null) {
//                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
//                        scrollY > oldScrollY) {
//                    visibleItemCounts = threadLayoutManager.getChildCount();
//                    totalItemCount = threadLayoutManager.getItemCount();
//                    pastVisibleItems = threadLayoutManager.findFirstVisibleItemPosition();
//                    if (isLoad) {
//                        if (totalItemCount > previous_total) {
//                            isLoad = false;
//                            previous_total = totalItemCount;
//                        }
//                    }
//
//                    int view_threshold = 2;
//                    if (!isLoad && (totalItemCount - visibleItemCounts) <= (pastVisibleItems + view_threshold)) {
//                        performPagination();
//                        isLoad = true;
//                    }
//                }
//            }
//        }
    }

    private void performPagination() {
        colorfulProgress.setVisibility(View.VISIBLE);
        int from = 0, to = 0;
        final List<ForumThread> forumThreads = new ArrayList<>();

        int item_count = 2;
        from = page_number * item_count - (item_count - 1);
        to = page_number * item_count;

//        if (page_number == 1) forumThreads.add(tempAll.get(0));

//        for (int i = from; i < to + 1; i++) {
//            if (i >= tempAll.size()) {
//                c = true;
//                break;
//            }
//            c = false;
//            forumThreads.add(tempAll.get(i));
//        }

        page_number++;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                threadAdapter.addThread(forumThreads);
                colorfulProgress.setVisibility(View.GONE);
                if (c) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.end_of_thread), Toast.LENGTH_SHORT).show();
                }
            }
        }, Constant.DELAY_THREAD);
    }

    private void search() {
        String result = etSearch.getText().toString();
        if (!result.trim().isEmpty()) {
            tvResult.setVisibility(View.VISIBLE);
            tvResult.setText("Hasil dari \'" + result + "\'");
            searchList.clear();
            for (int i = 0; i < forumLists.size(); i++) {
                if (forumLists.get(i).getForum_title().toLowerCase().trim().contains(result.toLowerCase().trim())) {
                    searchList.add(forumLists.get(i));
                }
            }
            threadAdapter.setForumList(searchList, merchantMap);
            removeTrending(mContext);
            isSearch = true;
            threadAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDelete(final int i, final Forum forum) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setMessage("Apa Anda yakin untuk menghapus thread berjudul " + forum.getForum_title() + " ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int a) {
                        dbForum.child(forum.getFid()).removeValue();
                        forumLists.remove(i);
                        threadAdapter.deleteList(i);
                        previous_total--;
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.thread_deleted), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.PERMISSION_READ_GALLERY_EXTERNAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, Constant.ACTIVITY_CHOOSE_IMAGE);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_failed), Toast.LENGTH_SHORT).show();
                }
                break;
            case Constant.PERMISSION_WRITE_EXTERNAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Bitmap bitmap = ((BitmapDrawable) img_showcase.getDrawable()).getBitmap();
                    int position = showcaseAdapter.getAdapterPosition();
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.download_success), Toast.LENGTH_SHORT).show();
                    MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), bitmap
                            , showCaseList.get(position).getSid(), "");
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
            switch (requestCode) {
                case Constant.ACTIVITY_CHOOSE_IMAGE:
                    if (data.getData() != null) {
                        Uri targetUri = data.getData();
                        File f = new File("" + targetUri);
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(targetUri));
                            Glide.with(mContext)
                                    .load(DecodeBitmap.compressBitmap(bitmap))
                                    .placeholder(scaleDrawable)
                                    .into(img_add_showcase);
                            tvError_AddShowCase.setVisibility(View.GONE);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }
}
