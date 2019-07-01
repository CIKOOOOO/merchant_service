package com.andrew.prototype.Activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.andrew.prototype.Fragment.HomeFragment;
import com.andrew.prototype.Fragment.MainForum;
import com.andrew.prototype.Fragment.NewThread;
import com.andrew.prototype.Fragment.PreviewProquest;
import com.andrew.prototype.Fragment.Profile;
import com.andrew.prototype.Fragment.PromoRequest;
import com.andrew.prototype.Fragment.SelectedThread;
import com.andrew.prototype.Fragment.StatusPromo;
import com.andrew.prototype.Fragment.TabPromoRequest;
import com.andrew.prototype.Fragment.TermCondition;
import com.andrew.prototype.R;
import com.andrew.prototype.Utils.Constant;
import com.andrew.prototype.Utils.PrefConfig;
import com.google.firebase.FirebaseApp;

import java.util.Objects;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
        , View.OnClickListener, DrawerLayout.DrawerListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private boolean exit = false;

    public interface onBackPressFragment {
        void onBackPress(boolean check, Context context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVar();
    }

    private void initVar() {
        getApplicationContext().deleteDatabase("recent_promo");
        getApplicationContext().deleteDatabase("stuff_promo");
        PrefConfig prefConfig = new PrefConfig(this);
//        prefConfig.insertId(-1);
        FirebaseApp.initializeApp(this);
        Toolbar toolbar = findViewById(R.id.toolbar_app_bar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        NavigationView navigationView = findViewById(R.id.nav_view);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_main);
        ImageButton burgerMenu = findViewById(R.id.burgerMenu);
        ImageButton icon = findViewById(R.id.main_icon_toolbar);

        drawer = findViewById(R.id.drawer_layout);

        changeFragment(new HomeFragment());

        drawer.addDrawerListener(this);
        burgerMenu.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        icon.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.blue_palette));
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_frame);
        MainActivity.onBackPressFragment onBackPressFragment;
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else if (fragment instanceof MainForum && MainForum.trendingIsVisible) {
            onBackPressFragment = new MainForum();
            onBackPressFragment.onBackPress(false, fragment.getContext());
        } else if (fragment instanceof NewThread) {
            onBackPressFragment = new NewThread();
            switch (NewThread.THREAD_CONDITION) {
                case NewThread.EDIT_THREAD_SELECTED:
                    onBackPressFragment.onBackPress(false, fragment.getContext());
                    break;
                case NewThread.EDIT_THREAD_REPLY:
                    onBackPressFragment.onBackPress(false, fragment.getContext());
                    break;
                default:
                    changeFragment(new MainForum());
                    break;
            }
        } else if (fragment instanceof SelectedThread) {
            if (SelectedThread.trendingIsVisible) {
                onBackPressFragment = new SelectedThread();
                onBackPressFragment.onBackPress(false, fragment.getContext());
            } else if (SelectedThread.frameIsVisible) {
                SelectedThread.frameIsVisible = false;
                SelectedThread.frameLayout.setVisibility(View.GONE);
            } else
                changeFragment(new MainForum());
        } else if (fragment instanceof Profile) {
            if (Profile.showcase_condition) {
                Profile.frame_add_showcase.setVisibility(View.GONE);
                Profile.showcase_condition = false;
            } else
                changeFragment(new HomeFragment());
        } else if (fragment instanceof TabPromoRequest) {
            onBackPressFragment = new PromoRequest();
            // Page = 1 adalah kondisi dimana viewpager menunjukkan Fragment Status Promosi
            if (TabPromoRequest.PAGE == 1 && StatusPromo.isBottomSheetVisible) {
                StatusPromo.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                StatusPromo.isBottomSheetVisible = false;
            } else
                onBackPressFragment.onBackPress(false, fragment.getContext());
        } else if (fragment instanceof PreviewProquest) {
            onBackPressFragment = new PreviewProquest();
            onBackPressFragment.onBackPress(false, fragment.getContext());
        } else if (fragment instanceof MainForum) {
            if (MainForum.showcase_condition) {
                MainForum.showcase_condition = false;
                MainForum.frame_showcase.setVisibility(View.GONE);
            } else changeFragment(new HomeFragment());
        } else {
            if (exit) {
                finishAffinity();
            } else {
                Toast.makeText(this, getResources().getText(R.string.exit),
                        Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, Constant.DELAY);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                changeFragment(new Profile());
                break;
            case R.id.promorequest:
                changeFragment(new TabPromoRequest());
                break;
            case R.id.merchantforum:
                changeFragment(new MainForum());
                break;

            case R.id.bot_home:
                changeFragment(new HomeFragment());
                break;
            case R.id.bot_account:
                changeFragment(new Profile());
                break;
            case R.id.bot_edc:
                break;
            case R.id.bot_store:
                break;
            case R.id.bot_transaction:
                break;
        }

//        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.burgerMenu:
//                if (drawer.isDrawerOpen(GravityCompat.END)) {
//                    drawer.closeDrawer(GravityCompat.END);
//                } else {
//                    drawer.openDrawer(GravityCompat.END);
//                    hideSoftKeyboard(this);
//                }
//                break;
            case R.id.main_icon_toolbar:
                changeFragment(new MainForum());
                break;
        }
    }

    private void changeFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity == null) return;
        else if (activity.getCurrentFocus() == null) return;
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onDrawerSlide(@NonNull View view, float v) {
    }

    @Override
    public void onDrawerOpened(@NonNull View view) {
        hideSoftKeyboard(MainActivity.this);
    }

    @Override
    public void onDrawerClosed(@NonNull View view) {
    }

    @Override
    public void onDrawerStateChanged(int i) {
    }
}
