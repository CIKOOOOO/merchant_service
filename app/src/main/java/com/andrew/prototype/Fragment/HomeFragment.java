package com.andrew.prototype.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.andrew.prototype.Adapter.HomeAdapter;
import com.andrew.prototype.Model.Store;
import com.andrew.prototype.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private View v;
    private Context mContext;

    public HomeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        mContext = v.getContext();

        List<Store> storeList = new ArrayList<>();

        storeList.add(new Store("Rukan Permata Senayan", 45985000, 52, R.drawable.mcd_store));
        storeList.add(new Store("Rukan Permata Senayan", 45985000, 52, R.drawable.mcd_store));

        HomeAdapter homeAdapter = new HomeAdapter(mContext, storeList);

        RecyclerView recycler_home = v.findViewById(R.id.recycler_home);
        ImageButton image_profile = v.findViewById(R.id.image_profile_home);

        setRecyclerAdapter(recycler_home, homeAdapter);
        image_profile.setOnClickListener(this);
    }

    private void setRecyclerAdapter(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_profile_home:
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.main_frame, new Profile());
                fragmentTransaction.commit();
                break;
        }
    }
}
