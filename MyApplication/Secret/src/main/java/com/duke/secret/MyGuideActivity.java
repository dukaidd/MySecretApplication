package com.duke.secret;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import com.chechezhi.ui.guide.AbsGuideActivity;
import com.chechezhi.ui.guide.SinglePage;
import com.duke.fragments.GuideFragment01;
import com.duke.fragments.GuideFragment02;
import com.duke.fragments.GuideFragment03;
import com.duke.fragments.GuideFragment04;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dukaidd on 2016/8/10.
 */

public class MyGuideActivity extends AbsGuideActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#323232"));
    }

    @Override
    public List<SinglePage> buildGuideContent() {
        List<SinglePage> guideContent = new ArrayList<SinglePage>();

        SinglePage page01 = new SinglePage();
        page01.mCustomFragment = new GuideFragment01();
        guideContent.add(page01);

        SinglePage page02 = new SinglePage();
        page02.mCustomFragment = new GuideFragment02();
        guideContent.add(page02);

        SinglePage page03 = new SinglePage();
        page03.mCustomFragment = new GuideFragment03();
        guideContent.add(page03);

        SinglePage page04 = new SinglePage();
        page04.mCustomFragment = new GuideFragment04();
        guideContent.add(page04);

        return guideContent;
    }

    @Override
    public boolean drawDot() {
        return false;
    }

    @Override
    public Bitmap dotDefault() {
        return BitmapFactory.decodeResource(getResources(), R.drawable.ic_dot_default);
    }

    @Override
    public Bitmap dotSelected() {
        return BitmapFactory.decodeResource(getResources(), R.drawable.ic_dot_selected);
    }

    /**
     * You need provide an id to the pager. You could define an id in
     * values/ids.xml and use it.
     */
    @Override
    public int getPagerId() {
        return R.id.guide_container;
    }

    public void entryApp() {
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }
}
