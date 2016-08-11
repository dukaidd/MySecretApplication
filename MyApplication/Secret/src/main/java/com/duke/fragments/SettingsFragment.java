package com.duke.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duke.app.Constant;
import com.duke.app.MyApplication;
import com.duke.secret.HomeActivity;
import com.duke.secret.LoginActivity;
import com.duke.secret.R;
import com.duke.utils.HXPreferenceUtils;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;

import cn.bmob.v3.BmobUser;

public class SettingsFragment extends Fragment implements OnClickListener {

    /**
     * 设置新消息通知布局
     */
    private RelativeLayout rl_switch_notification;
    /**
     * 设置声音布局
     */
    private RelativeLayout rl_switch_sound;
    /**
     * 设置震动布局
     */
    private RelativeLayout rl_switch_vibrate;
    /**
     * 设置扬声器布局
     */
    private RelativeLayout rl_switch_speaker;

    /**
     * 打开新消息通知imageView
     */
    private ImageView iv_switch_open_notification;
    /**
     * 关闭新消息通知imageview
     */
    private ImageView iv_switch_close_notification;
    /**
     * 打开声音提示imageview
     */
    private ImageView iv_switch_open_sound;
    /**
     * 关闭声音提示imageview
     */
    private ImageView iv_switch_close_sound;
    /**
     * 打开消息震动提示
     */
    private ImageView iv_switch_open_vibrate;
    /**
     * 关闭消息震动提示
     */
    private ImageView iv_switch_close_vibrate;
    /**
     * 打开扬声器播放语音
     */
    private ImageView iv_switch_open_speaker;
    /**
     * 关闭扬声器播放语音
     */
    private ImageView iv_switch_close_speaker;

    /**
     * 声音和震动中间的那条线
     */
    private TextView textview1, textview2;


    /**
     * 退出按钮
     */
    private Button logoutBtn;

    private EMChatOptions chatOptions;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView(savedInstanceState);

    }

    private void initView(Bundle savedInstanceState) {
        LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.fragment_setting_ll);
        View header = new View(getActivity());
        TypedValue tv = new TypedValue();
        int hight = 0;
        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            hight = TypedValue.complexToDimensionPixelSize(tv.data, getActivity().getResources().getDisplayMetrics());
        }
        header.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, hight));
        header.setBackgroundResource(R.drawable.flag);
        linearLayout.addView(header,0);

        logoutBtn = (Button) getView().findViewById(R.id.btn_logout);
        logoutBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                BmobUser.logOut();
                EMChatManager.getInstance().logout();
                MyApplication.getInstance().setContactList(null);
                getActivity().finish();
            }
        });


        if(savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        rl_switch_notification = (RelativeLayout) getView().findViewById(R.id.rl_switch_notification);
        rl_switch_sound = (RelativeLayout) getView().findViewById(R.id.rl_switch_sound);
        rl_switch_vibrate = (RelativeLayout) getView().findViewById(R.id.rl_switch_vibrate);
        rl_switch_speaker = (RelativeLayout) getView().findViewById(R.id.rl_switch_speaker);

        iv_switch_open_notification = (ImageView) getView().findViewById(R.id.iv_switch_open_notification);
        iv_switch_close_notification = (ImageView) getView().findViewById(R.id.iv_switch_close_notification);
        iv_switch_open_sound = (ImageView) getView().findViewById(R.id.iv_switch_open_sound);
        iv_switch_close_sound = (ImageView) getView().findViewById(R.id.iv_switch_close_sound);
        iv_switch_open_vibrate = (ImageView) getView().findViewById(R.id.iv_switch_open_vibrate);
        iv_switch_close_vibrate = (ImageView) getView().findViewById(R.id.iv_switch_close_vibrate);
        iv_switch_open_speaker = (ImageView) getView().findViewById(R.id.iv_switch_open_speaker);
        iv_switch_close_speaker = (ImageView) getView().findViewById(R.id.iv_switch_close_speaker);



        if(!TextUtils.isEmpty(EMChatManager.getInstance().getCurrentUser())){
            logoutBtn.setText(getString(R.string.button_logout) + "(" + EMChatManager.getInstance().getCurrentUser() + ")");
        }

        textview1 = (TextView) getView().findViewById(R.id.textview1);
        textview2 = (TextView) getView().findViewById(R.id.textview2);


        rl_switch_notification.setOnClickListener(this);
        rl_switch_sound.setOnClickListener(this);
        rl_switch_vibrate.setOnClickListener(this);
        rl_switch_speaker.setOnClickListener(this);

        chatOptions = EMChatManager.getInstance().getChatOptions();


        // 震动和声音总开关，来消息时，是否允许此开关打开
        // the vibrate and sound notification are allowed or not?
        if (HXPreferenceUtils.getInstance().getSettingMsgNotification()) {
            iv_switch_open_notification.setVisibility(View.VISIBLE);
            iv_switch_close_notification.setVisibility(View.INVISIBLE);
        } else {
            iv_switch_open_notification.setVisibility(View.INVISIBLE);
            iv_switch_close_notification.setVisibility(View.VISIBLE);
        }

        // 是否打开声音
        // sound notification is switched on or not?
        if (HXPreferenceUtils.getInstance().getSettingMsgSound()) {
            iv_switch_open_sound.setVisibility(View.VISIBLE);
            iv_switch_close_sound.setVisibility(View.INVISIBLE);
        } else {
            iv_switch_open_sound.setVisibility(View.INVISIBLE);
            iv_switch_close_sound.setVisibility(View.VISIBLE);
        }

        // 是否打开震动
        // vibrate notification is switched on or not?
        if (HXPreferenceUtils.getInstance().getSettingMsgVibrate()) {
            iv_switch_open_vibrate.setVisibility(View.VISIBLE);
            iv_switch_close_vibrate.setVisibility(View.INVISIBLE);
        } else {
            iv_switch_open_vibrate.setVisibility(View.INVISIBLE);
            iv_switch_close_vibrate.setVisibility(View.VISIBLE);
        }

        // 是否打开扬声器
        // the speaker is switched on or not?
        if (HXPreferenceUtils.getInstance().getSettingMsgSpeaker()) {
            iv_switch_open_speaker.setVisibility(View.VISIBLE);
            iv_switch_close_speaker.setVisibility(View.INVISIBLE);
        } else {
            iv_switch_open_speaker.setVisibility(View.INVISIBLE);
            iv_switch_close_speaker.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_switch_notification:
                if (iv_switch_open_notification.getVisibility() == View.VISIBLE) {
                    iv_switch_open_notification.setVisibility(View.INVISIBLE);
                    iv_switch_close_notification.setVisibility(View.VISIBLE);
                    rl_switch_sound.setVisibility(View.GONE);
                    rl_switch_vibrate.setVisibility(View.GONE);
                    textview1.setVisibility(View.GONE);
                    textview2.setVisibility(View.GONE);
                    chatOptions.setNotificationEnable(false);
                    EMChatManager.getInstance().setChatOptions(chatOptions);

                    HXPreferenceUtils.getInstance().setSettingMsgNotification(false);
                } else {
                    iv_switch_open_notification.setVisibility(View.VISIBLE);
                    iv_switch_close_notification.setVisibility(View.INVISIBLE);
                    rl_switch_sound.setVisibility(View.VISIBLE);
                    rl_switch_vibrate.setVisibility(View.VISIBLE);
                    textview1.setVisibility(View.VISIBLE);
                    textview2.setVisibility(View.VISIBLE);
                    chatOptions.setNotificationEnable(true);
                    EMChatManager.getInstance().setChatOptions(chatOptions);
                    HXPreferenceUtils.getInstance().setSettingMsgNotification(true);
                }
                break;
            case R.id.rl_switch_sound:
                if (iv_switch_open_sound.getVisibility() == View.VISIBLE) {
                    iv_switch_open_sound.setVisibility(View.INVISIBLE);
                    iv_switch_close_sound.setVisibility(View.VISIBLE);
                    chatOptions.setNoticeBySound(false);
                    EMChatManager.getInstance().setChatOptions(chatOptions);
                    HXPreferenceUtils.getInstance().setSettingMsgSound(false);
                } else {
                    iv_switch_open_sound.setVisibility(View.VISIBLE);
                    iv_switch_close_sound.setVisibility(View.INVISIBLE);
                    chatOptions.setNoticeBySound(true);
                    EMChatManager.getInstance().setChatOptions(chatOptions);
                    HXPreferenceUtils.getInstance().setSettingMsgSound(true);
                }
                break;
            case R.id.rl_switch_vibrate:
                if (iv_switch_open_vibrate.getVisibility() == View.VISIBLE) {
                    iv_switch_open_vibrate.setVisibility(View.INVISIBLE);
                    iv_switch_close_vibrate.setVisibility(View.VISIBLE);
                    chatOptions.setNoticedByVibrate(false);
                    EMChatManager.getInstance().setChatOptions(chatOptions);
                    HXPreferenceUtils.getInstance().setSettingMsgVibrate(false);
                } else {
                    iv_switch_open_vibrate.setVisibility(View.VISIBLE);
                    iv_switch_close_vibrate.setVisibility(View.INVISIBLE);
                    chatOptions.setNoticedByVibrate(true);
                    EMChatManager.getInstance().setChatOptions(chatOptions);
                    HXPreferenceUtils.getInstance().setSettingMsgVibrate(true);
                }
                break;
            case R.id.rl_switch_speaker:
                if (iv_switch_open_speaker.getVisibility() == View.VISIBLE) {
                    iv_switch_open_speaker.setVisibility(View.INVISIBLE);
                    iv_switch_close_speaker.setVisibility(View.VISIBLE);
                    chatOptions.setUseSpeaker(false);
                    EMChatManager.getInstance().setChatOptions(chatOptions);
                    HXPreferenceUtils.getInstance().setSettingMsgSpeaker(false);
                } else {
                    iv_switch_open_speaker.setVisibility(View.VISIBLE);
                    iv_switch_close_speaker.setVisibility(View.INVISIBLE);
                    chatOptions.setUseSpeaker(true);
                    EMChatManager.getInstance().setChatOptions(chatOptions);
                    HXPreferenceUtils.getInstance().setSettingMsgSpeaker(true);
                }
                break;
            default:
                break;
        }

    }
}
