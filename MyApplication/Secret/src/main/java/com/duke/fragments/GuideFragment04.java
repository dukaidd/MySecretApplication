package com.duke.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duke.base.BaseFragment;
import com.duke.secret.MyGuideActivity;
import com.duke.secret.R;

/**
 * Created by dukaidd on 2016/8/10.
 */

public class GuideFragment04 extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tuition_edit, null);
        v.findViewById(R.id.guide_btn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MyGuideActivity activity = (MyGuideActivity) getActivity();
                activity.entryApp();
            }
        });
        return v;
    }
}
