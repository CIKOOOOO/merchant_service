package com.andrew.prototype.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrew.prototype.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TermCondition extends Fragment {


    private View v;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_term_condition, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        mContext = v.getContext();



    }
}
