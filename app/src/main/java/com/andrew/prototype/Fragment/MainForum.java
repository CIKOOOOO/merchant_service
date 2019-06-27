package com.andrew.prototype.Fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import com.andrew.prototype.Model.ForumThread;
import com.andrew.prototype.Model.ShowCase;
import com.andrew.prototype.R;
import com.andrew.prototype.Utils.Constant;
import com.andrew.prototype.Utils.DecodeBitmap;
import com.baoyz.widget.PullRefreshLayout;
import com.bumptech.glide.Glide;
import com.developers.coolprogressviews.ColorfulProgress;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainForum extends Fragment implements View.OnClickListener
        , MainActivity.onBackPressFragment, TextWatcher, TrendingAdapter.itemClickListener
        , ShowcaseAdapter.onImageClickListener, ThreadAdapter.onItemClick, View.OnKeyListener
        , View.OnTouchListener, PullRefreshLayout.OnRefreshListener, NestedScrollView.OnScrollChangeListener
        , ThreadAdapter.hideAccount, ThreadAdapter.onItemDelete {

    public static final String BUNDLE_SEARCH = "BUNDLE_SEARCH";

    public static boolean trendingIsVisible, showcase_condition, isSearch;
    public static FrameLayout frame_showcase;

    private static final String TAG = MainForum.class.getSimpleName();
    private static RecyclerView showcase_recycler_view, trending_recycler_view;
    private static ShowcaseAdapter showcaseAdapter;
    private static TrendingAdapter trendingAdapter;
    private static List<ForumThread> trendingArrayList, tempList;
    private static LinearLayout trending_linear;
    private static List<ForumThread> forumList, searchList;
    private static List<ShowCase> showCaseList;

    private View v;
    private RecyclerView thread_recycler_view;
    private ThreadAdapter threadAdapter;
    private List<ForumThread> tempAll, forRealTemp;
    private EditText etSearch, etInput_ShowCase;
    private TextView tvResult, tvError_AddShowCase;
    private PullRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager threadLayoutManager;
    private ColorfulProgress colorfulProgress;
    private ImageView img_showcase, img_add_showcase;
    private AlertDialog codeAlert;
    private Context mContext;
    private Activity mActivity;
    private ScaleDrawable scaleDrawable;

    private boolean c = false;
    private int page_number = 1;
    private boolean isLoad = true;
    private int pastVisibleItems, visibleItemCounts, totalItemCount, previous_total = 0;

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
        mContext = v.getContext();

        trendingIsVisible = false;
        showcase_condition = false;
        isSearch = false;

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

        trendingArrayList = new ArrayList<>();
        tempList = new ArrayList<>();
        forumList = new ArrayList<>(Constant.getForumList(mContext));
        searchList = new ArrayList<>();
        showCaseList = new ArrayList<>();
        tempAll = new ArrayList<>();
        forRealTemp = new ArrayList<>();

        threadLayoutManager = new LinearLayoutManager(mContext);
        swipeRefreshLayout.setDurations(0, 3);

        tvResult.setVisibility(View.GONE);

        setAdapter(v);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String result = bundle.getString(BUNDLE_SEARCH);
            if (result != null) {
                tvResult.setVisibility(View.VISIBLE);
                tvResult.setText("Hasil dari \'" + result + "\'");
                searchList.clear();

                for (ForumThread thread : forumList) {
                    if (thread.getTitle().toLowerCase().trim().contains(result.toLowerCase().trim())) {
                        searchList.add(thread);
                    }
                }
                threadAdapter.setForumList(searchList);
                removeTrending(mContext);
                isSearch = true;
                bundle.remove(BUNDLE_SEARCH);
            } else {
                tvResult.setVisibility(View.GONE);
                isSearch = false;
            }
        }

        loadData();

        removeTrending(mContext);

        tempList.addAll(trendingArrayList);

        etSearch.addTextChangedListener(this);
        etSearch.setOnClickListener(this);
        etSearch.setOnKeyListener(this);

        file_download.setOnClickListener(this);
        relativeLayout.setOnClickListener(this);
        frame_showcase.setOnClickListener(this);
        close.setOnClickListener(this);
        linearLayout.setOnClickListener(this);
        new_thread.setOnClickListener(this);
        search.setOnClickListener(this);
        img_showcase.setOnClickListener(this);

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
                Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_up);
                Animation animation2 = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_down);
                if (trendingIsVisible) {
                    trendingIsVisible = false;
                    removeTrending(mContext);
                    hideSoftKeyboard(mActivity);
                    trending_linear.setAnimation(animation);
                    showcaseAdapter = new ShowcaseAdapter(view.getContext(), showCaseList, false, this);
                    trendingAdapter = new TrendingAdapter(view.getContext(), tempList, this, false);
                } else {
                    trendingIsVisible = true;
                    makeVisible();
                    trending_linear.setAnimation(animation2);
                    showcaseAdapter = new ShowcaseAdapter(view.getContext(), showCaseList, true, this);
                    trendingAdapter = new TrendingAdapter(view.getContext(), tempList, this, true);
                }
                showcase_recycler_view.setAdapter(showcaseAdapter);
                trending_recycler_view.setAdapter(trendingAdapter);
                break;
            case R.id.parentll_main_forum:
                removeTrending(view.getContext());
                break;
            case R.id.recyclerView_MainForum:
                trendingIsVisible = true;
                trending_linear.setAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_up));
                makeGone();
                break;
            case R.id.imgBtn_AddThread:
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.main_frame, new NewThread());
                fragmentTransaction.commit();
                break;
            case R.id.btn_close_frame:
                frame_showcase.setVisibility(View.GONE);
                break;
            case R.id.rl_frame_main:
                frame_showcase.setVisibility(View.GONE);
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
                            , showCaseList.get(position).getMerchantName(), "");
                }
                break;
            case R.id.btnSubmit_AddShowCase:
                String checkName = etInput_ShowCase.getText().toString();
                tvError_AddShowCase.setVisibility(View.GONE);
                if (checkName.trim().isEmpty()) {
                    etInput_ShowCase.setError(mContext.getResources().getString(R.string.dont_empty_this_edittext));
                } else if (img_add_showcase.getDrawable() == null) {
                    tvError_AddShowCase.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
                    showCaseList.add(1, new ShowCase("", checkName, ((BitmapDrawable) img_add_showcase.getDrawable()).getBitmap()));
                    showcaseAdapter.setShowCases(showCaseList);
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

    private void makeGone() {
        trending_linear.setVisibility(View.GONE);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) trending_linear.getLayoutParams();
        lp.height = 0;
    }

    private void makeVisible() {
        trendingIsVisible = true;
        trending_linear.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) trending_linear.getLayoutParams();
        lp.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
    }

    private void loadData() {
        trendingArrayList.addAll(Constant.getTrending(mContext));
        tempAll.addAll(Constant.getForumList(mContext));
        showCaseList.addAll(Constant.getShowCase());
        if (!isSearch)
            performPagination();
    }

    private void removeTrending(Context context) {
//        trendingIsVisible = true;
        if (trendingIsVisible) {
            trendingIsVisible = false;
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
            trending_linear.setAnimation(animation);
            trendingAdapter = new TrendingAdapter(context, tempList, this, false);
            showcaseAdapter = new ShowcaseAdapter(context, showCaseList, trendingIsVisible, this);
            showcase_recycler_view.setAdapter(showcaseAdapter);
            trending_recycler_view.setAdapter(trendingAdapter);
            makeGone();
            hideSoftKeyboard(mActivity);
        }
    }

    private void setAdapter(View view) {
        trendingAdapter = new TrendingAdapter(mContext, trendingArrayList, this, false);
        trending_recycler_view.setLayoutManager(new LinearLayoutManager(view.getContext()));
        trending_recycler_view.setAdapter(trendingAdapter);

        showcaseAdapter = new ShowcaseAdapter(mContext, showCaseList, true, this);
        showcase_recycler_view.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        showcase_recycler_view.setAdapter(showcaseAdapter);

        threadAdapter = new ThreadAdapter(mContext, forRealTemp, this, this, this);
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
        makeVisible();
    }

    @Override
    public void afterTextChanged(Editable editable) {
        tempList.clear();
        makeVisible();
        if (editable.toString().isEmpty()) {
            tempList.addAll(trendingArrayList);
            trendingAdapter.setTrendingList(trendingArrayList);
        } else {
            for (int i = 0; i < trendingArrayList.size(); i++) {
                if (trendingArrayList.get(i).getTitle().toLowerCase().trim().startsWith(editable.toString().toLowerCase().trim())) {
                    tempList.add(trendingArrayList.get(i));
                }
            }
            trendingAdapter.setTrendingList(tempList);
        }
    }

    @Override
    public void onItemClick(int pos, List<ForumThread> forumThreads) {
        if (forumThreads.size() == 0) {
            Log.e("HEHE", "PENCARIAN");
        } else {
            ForumThread thread = forumThreads.get(pos);
            SelectedThread selectedThread = new SelectedThread();
            Bundle bundle = new Bundle();
            FragmentManager fragmentManager = getFragmentManager();

            bundle.putParcelable(SelectedThread.GET_THREAD_OBJECT, thread);

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
                showcase_condition = true;
                TextView textView = v.findViewById(R.id.merchantName_MainForum);
                textView.setText("Merchant Name : " + showCaseList.get(pos).getMerchantName());
                frame_showcase.setVisibility(View.VISIBLE);
                frame_showcase.getBackground().setAlpha(230);
                if (showCaseList.get(pos).getImage() != 0)
                    DecodeBitmap.setScaledImageView(img_showcase, showCaseList.get(pos).getImage(), context);
                else Glide.with(context)
                        .load(DecodeBitmap.compressBitmap(showCaseList.get(pos).getImgBitmap()))
                        .placeholder(scaleDrawable)
                        .into(img_showcase);
            }

        }
    }

    private void addShowCase() {
        AlertDialog.Builder codeBuilder = new AlertDialog.Builder(mContext);
        View codeView = getLayoutInflater().inflate(R.layout.custom_add_show_case, null);
        img_add_showcase = codeView.findViewById(R.id.imgView_AddShowCase);
        etInput_ShowCase = codeView.findViewById(R.id.etInputName_AddShowCae);
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
    public void onClick(int pos) {
//        removeTrending(v.getContext());
        if (thread_recycler_view.isEnabled()) {
            ForumThread forumThread = forumList.get(pos);
            SelectedThread selectedThread = new SelectedThread();
            Bundle bundle = new Bundle();
            FragmentManager fragmentManager = getFragmentManager();

            bundle.putParcelable(SelectedThread.GET_THREAD_OBJECT, (Parcelable) forumThread);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.main_frame, selectedThread);

            selectedThread.setArguments(bundle);
            fragmentTransaction.commit();
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
        if (!isSearch) {
            if (v.getChildAt(v.getChildCount() - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                        scrollY > oldScrollY) {
                    visibleItemCounts = threadLayoutManager.getChildCount();
                    totalItemCount = threadLayoutManager.getItemCount();
                    pastVisibleItems = threadLayoutManager.findFirstVisibleItemPosition();
                    if (isLoad) {
                        if (totalItemCount > previous_total) {
                            isLoad = false;
                            previous_total = totalItemCount;
                        }
                    }

                    int view_threshold = 2;
                    if (!isLoad && (totalItemCount - visibleItemCounts) <= (pastVisibleItems + view_threshold)) {
                        performPagination();
                        isLoad = true;
                    }
                }
            }
        }
    }

    private void performPagination() {
        colorfulProgress.setVisibility(View.VISIBLE);
        int from = 0, to = 0;
        final List<ForumThread> forumThreads = new ArrayList<>();

        int item_count = 2;
        from = page_number * item_count - (item_count - 1);
        to = page_number * item_count;

        if (page_number == 1) forumThreads.add(tempAll.get(0));

        for (int i = from; i < to + 1; i++) {
            if (i >= tempAll.size()) {
                c = true;
                break;
            }
            c = false;
            forumThreads.add(tempAll.get(i));
        }

        page_number++;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                threadAdapter.addThread(forumThreads);
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
            for (int i = 0; i < forumList.size(); i++) {
                if (forumList.get(i).getTitle().toLowerCase().trim().contains(result.toLowerCase().trim())) {
                    searchList.add(forumList.get(i));
                }
            }
            threadAdapter.setForumList(searchList);
            removeTrending(mContext);
            isSearch = true;
        }
    }

    @Override
    public void hide(String username, List<ForumThread> forumThreads) {
        for (int i = 0; i < forumThreads.size(); i++) {
            if (forumThreads.get(i).getUsername().toLowerCase().trim().equals(username.toLowerCase().trim())) {
                previous_total--;
                forumList.remove(i);
                threadAdapter.deleteList(i);
            }
        }
    }

    @Override
    public void onDelete(int i, List<ForumThread> forumThreads) {
        threadAdapter.deleteList(i);
        forumList.remove(i);
        previous_total--;
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
                            , showCaseList.get(position).getMerchantName(), "");
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
