package com.andrew.prototype.Fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.andrew.prototype.Adapter.TabAdapter;
import com.andrew.prototype.R;


public class TabPromoRequest extends Fragment {
    public static int PAGE;

    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_tab_promo_request, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        PAGE = 0;
        PromoRequest promoRequest = new PromoRequest();
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getParcelable(PromoRequest.GETTING_DATA) != null) {
                Bundle bundle1 = new Bundle();
                bundle1.putParcelable(PromoRequest.GETTING_DATA, bundle.getParcelable(PromoRequest.GETTING_DATA));
                if (bundle.getParcelableArrayList(PromoRequest.GETTING_DATA_LIST) != null) {
                    bundle1.putParcelableArrayList(PromoRequest.GETTING_DATA_LIST, bundle.getParcelableArrayList(PromoRequest.GETTING_DATA_LIST));
                }
                promoRequest.setArguments(bundle1);
            }
        }

        ViewPager viewPager = v.findViewById(R.id.viewPager_TabPromo);
        TabLayout tabLayout = v.findViewById(R.id.tabLayout_PromoRequest);
        TabAdapter tabAdapter = new TabAdapter(getFragmentManager());

        tabAdapter.addTab(promoRequest, "Tambah Promosi");
        tabAdapter.addTab(new StatusPromo(), "Status Promosi");

        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);

//        viewPager.setCurrentItem(1);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float vs, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                PAGE = i;
                if (i == 0)
                    StatusPromo.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }
}
