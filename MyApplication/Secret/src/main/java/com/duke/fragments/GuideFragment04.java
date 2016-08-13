package com.duke.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView tv = (TextView) findViewById(R.id.tuition_edit_tv);
        TextView tv2 = (TextView) findViewById(R.id.tuition_edit_title);
        tv.setText("分享的是知识\n共享的是快乐");
        tv2.setText("分享");
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/mi.ttf");
        tv.setTypeface(typeface);
        tv2.setTypeface(typeface);
    }
}
