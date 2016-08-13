package com.duke.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duke.base.BaseFragment;
import com.duke.secret.R;

/**
 * Created by dukaidd on 2016/8/10.
 */

public class GuideFragment02 extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.tuition_like, null);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView tv = (TextView) findViewById(R.id.tuition_like_tv);
        TextView tv2 = (TextView) findViewById(R.id.tuition_like_title);
        tv.setText("一个真心的点赞\n守护点滴精彩");
        tv2.setText("点赞");
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/mi.ttf");
        tv.setTypeface(typeface);
        tv2.setTypeface(typeface);
    }
}
