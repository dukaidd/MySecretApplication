package com.duke.adapters;

import java.util.List;

import com.duke.beans.User;
import com.duke.secret.ChatActivity;
import com.duke.secret.R;
import com.duke.utils.BitmapUtil;
import com.duke.utils.StringUtils;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.Direct;
import com.easemob.chat.TextMessageBody;
import com.lidroid.xutils.BitmapUtils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ChatlvAdapter extends BaseAdapter {

    private ChatActivity act;
    private List<EMMessage> messages;

    public ChatlvAdapter(ChatActivity act, List<EMMessage> messages) {
        super();
        this.act = act;
        this.messages = messages;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (messages == null) {
            return 0;
        }
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (getItemViewType(position) == 0) {
            convertView = act.getLayoutInflater().inflate(R.layout.item_chat_left, null);
        } else {
            convertView = act.getLayoutInflater().inflate(R.layout.item_chat_right, null);
        }
        TextView time = (TextView) convertView.findViewById(R.id.item_chat_time);
        final ImageView avatar = (ImageView) convertView.findViewById(R.id.item_chat_avatar);
        TextView message = (TextView) convertView.findViewById(R.id.item_chat_message);
        time.setText(StringUtils.getTime(messages.get(position).getMsgTime()));
        final BitmapUtils bitmapUtil = BitmapUtil.getBitUtil(act);
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("username", messages.get(position).getFrom());
        query.findObjects(new FindListener<User>() {

            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    User user = list.get(0);
                    bitmapUtil.display(avatar, user.getAvatar().getUrl());
                } else {
                    //Toast.makeText(act, "头像获取失败"+e, Toast.LENGTH_SHORT).show();
                    avatar.setImageResource(R.drawable.avator_profile_default);
                }
            }
        });
        TextMessageBody body = (TextMessageBody) messages.get(position).getBody();
        message.setText(body.getMessage());
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        if (messages.get(position).direct == Direct.RECEIVE) {
            return 0;
        } else {
            return 1;
        }
    }
}
