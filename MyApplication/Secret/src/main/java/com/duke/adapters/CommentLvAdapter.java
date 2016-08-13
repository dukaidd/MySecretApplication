package com.duke.adapters;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duke.beans.Comment;
import com.duke.customview.CircleImageView;
import com.duke.secret.ChatActivity;
import com.duke.secret.CommentActivity;
import com.duke.secret.R;
import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.utils.EaseUserUtils;

import java.util.List;

/**
 * Created by dukaidd on 2016/8/11.
 */

public class CommentLvAdapter extends BaseAdapter implements View.OnClickListener {
    private List<Comment> comments;
    private CommentActivity act;

    public CommentLvAdapter(List<Comment> comments, CommentActivity act) {
        this.comments = comments;
        this.act = act;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = act.getLayoutInflater().inflate(R.layout.item_comment_adapter, null);
            vh = new ViewHolder();
            vh.ll= (LinearLayout) convertView.findViewById(R.id.item_comment_ll);
            vh.avatar = (CircleImageView) convertView.findViewById(R.id.item_comment_avatar);
            vh.username = (TextView) convertView.findViewById(R.id.item_comment_username);
            vh.gender = (TextView) convertView.findViewById(R.id.item_comment_gender);
            vh.genderSign = (ImageView) convertView.findViewById(R.id.item_comment_gender_sign);
            vh.time = (TextView) convertView.findViewById(R.id.item_comment_time);
            vh.content = (TextView) convertView.findViewById(R.id.item_comment_content);
            vh.chat = (AppCompatImageButton) convertView.findViewById(R.id.item_comment_chat);
            vh.chat.setOnClickListener(this);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        Comment comment = comments.get(position);
        Log.i("duke","CommentLvAdapter:"+comment.getAuthor().getAvatarUrl());
        EaseUserUtils.setUserAvatar(act,comment.getUsername(),vh.avatar);
        vh.username.setText(comment.getAuthor().getUsername());
        vh.gender.setText(comment.getAuthor().getSex());
        if(comment.getAuthor().getSex().equals("男")){
            vh.genderSign.setImageResource(R.drawable.ic_default_male);
        }else{
            vh.genderSign.setImageResource(R.drawable.ic_default_female);
        }
        vh.time.setText(comment.getCreatedAt().toString());
        vh.content.setText(comment.getContent());

        vh.chat.setTag(comment);

        Typeface typeface = Typeface.createFromAsset(act.getAssets(), "fonts/mi.ttf");
        vh.content.setTypeface(typeface);
        vh.username.setTypeface(typeface);
        vh.gender.setTypeface(typeface);
        vh.time.setTypeface(typeface);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.item_comment_chat:
                Comment comment = (Comment) v.getTag();
                String fromName = comment.getUsername();
                Intent intent = new Intent(act, ChatActivity.class);
                if(fromName!=null){
                    intent.putExtra(EaseConstant.EXTRA_USER_ID, fromName);
                    intent.putExtra("flag", 0);
                    act.startActivity(intent);
                }
                break;
        }
    }

    class ViewHolder {
        private CircleImageView avatar;
        private TextView username, gender, time, content;
        private ImageView genderSign;
        private LinearLayout ll;
        private AppCompatImageButton chat;
    }
}
