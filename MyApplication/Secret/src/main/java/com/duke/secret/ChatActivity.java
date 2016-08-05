package com.duke.secret;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.duke.adapters.ChatlvAdapter;
import com.duke.app.MyApplication;
import com.duke.base.BaseActivity;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;

import java.util.List;

public class ChatActivity extends BaseActivity {
    private Toolbar toolbar;
    private List<EMMessage> messages;
    private ListView lv;
    private EditText text;
    private String fromName;
    private EMMessage msg;
    private ChatlvAdapter adapter;
    private int flag = 2;
    private Handler hander = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                adapter.notifyDataSetChanged();
                lv.setSelection(messages.size() - 1);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);
        toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity.this.finish();
            }
        });
        MyApplication.app.setOpen(true);
        flag = getIntent().getIntExtra("flag", 0);
        if (flag == 0) {
            fromName = getIntent().getStringExtra("name");
        } else {
            msg = getIntent().getParcelableExtra("msg");
            fromName = msg.getFrom();
        }
        initDatas();
        initViews();
        initViewsOper();
        ChatActivity.NewMessageBroadcastReceiver msgReceiver = new ChatActivity.NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        registerReceiver(msgReceiver, intentFilter);
        EMChat.getInstance().setAppInited();
    }
    private void initDatas() {
        EMConversation conversation = EMChatManager.getInstance().getConversation(fromName);
        List<EMMessage> datas = conversation.getAllMessages();
        messages = datas;
    }

    private void initViewsOper() {
        toolbar.setTitle(fromName);
        if (flag == 1) {
            EMConversation conversation = EMChatManager.getInstance().getConversation(fromName);
            conversation.addMessage(msg);
            adapter.notifyDataSetChanged();
            lv.setSelection(messages.size() - 1);
        }
    }

    private void initViews() {
        lv = (ListView) findViewById(R.id.act_chat_lv);
        text = (EditText) findViewById(R.id.act_chat_text);
        text.setOnKeyListener(onKey);
        adapter = new ChatlvAdapter(this, messages);
        lv.setAdapter(adapter);
        lv.setSelection(messages.size() - 1);
    }


    public void click_send(View view) {
        sendMessage();
    }

    private void sendMessage() {
        String text_content = text.getText().toString().trim();
        if (text_content == null || text_content.equals("")) {
            toast("请不要发送空消息");
            text.setText("");
            return;
        }
        text.setText("");
        // 获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
        EMConversation conversation = EMChatManager.getInstance().getConversation(fromName);
        // 创建一条文本消息
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        // 如果是群聊，设置chattype,默认是单聊
        // message.setChatType(ChatType.GroupChat);
        // 设置消息body
        TextMessageBody txtBody = new TextMessageBody(text_content);
        message.addBody(txtBody);
        // 设置接收人
        message.setReceipt(fromName);
        // 把消息加入到此会话对象中
        conversation.addMessage(message);
        // 发送消息
        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

            @Override
            public void onError(int arg0, String arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgress(int arg0, String arg1) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                hander.sendEmptyMessage(1);
            }
        });

    }

    View.OnKeyListener onKey = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            int action = event.getAction();
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                if (action == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                    return true;
                }

            }
            return false;
        }
    };

     class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 消息id
            String msgId = intent.getStringExtra("msgid");
            // 发消息的人的username(userid)
            String msgFrom = intent.getStringExtra("from");
            // 消息类型，文本，图片，语音消息等,这里返回的值为msg.type.ordinal()。
            // 所以消息type实际为是enum类型
            int msgType = intent.getIntExtra("type", 0);
            Log.d("main", "new message id:" + msgId + " from:" + msgFrom + " type:" + msgType);
            // 更方便的方法是通过msgId直接获取整个message
            EMMessage message = EMChatManager.getInstance().getMessage(msgId);
            EMConversation conversation = EMChatManager.getInstance().getConversation(msgFrom);
            conversation.addMessage(message);
            adapter.notifyDataSetChanged();
            lv.setSelection(messages.size() - 1);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        MyApplication.app.setOpen(false);
        super.onDestroy();
    }
}
