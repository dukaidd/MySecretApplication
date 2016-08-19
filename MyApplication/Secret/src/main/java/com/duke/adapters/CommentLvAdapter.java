package com.duke.adapters;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.duke.base.BitmapCache;
import com.duke.beans.Comment;
import com.duke.customview.CircleImageView;
import com.duke.secret.ChatActivity;
import com.duke.secret.CommentActivity;
import com.duke.secret.FriendMsgActivity;
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
    private RequestQueue queue;
    private ImageLoader imageLoader;
    public CommentLvAdapter(List<Comment> comments, CommentActivity act) {
        this.comments = comments;
        this.act = act;
        queue = Volley.newRequestQueue(act);
        imageLoader = new ImageLoader(queue, new BitmapCache());
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
            vh.avatar.setOnClickListener(this);
            vh.nickname = (TextView) convertView.findViewById(R.id.item_comment_nickname);
            vh.nickname.setOnClickListener(this);
            vh.gender = (TextView) convertView.findViewById(R.id.item_comment_gender);
            vh.gender.setOnClickListener(this);
            vh.genderSign = (ImageView) convertView.findViewById(R.id.item_comment_gender_sign);
            vh.genderSign.setOnClickListener(this);
            vh.time = (TextView) convertView.findViewById(R.id.item_comment_time);
            vh.time.setOnClickListener(this);
            vh.content = (TextView) convertView.findViewById(R.id.item_comment_content);
            vh.chat = (AppCompatImageButton) convertView.findViewById(R.id.item_comment_chat);
            vh.chat.setOnClickListener(this);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        Comment comment = comments.get(position);

//        vh.avatar.setDefaultImageResId(R.drawable.ic_default_male);
//        vh.avatar.setErrorImageResId(R.drawable.ic_default_male);
//        BmobQuery<Avatar> query = new BmobQuery<>();
//        query.order("-createdAt");
//        query.addWhereEqualTo("user", comment.getAuthor());
//        final ViewHolder finalVh = vh;
//        query.findObjects(new FindListener<Avatar>() {
//            @Override
//            public void done(List<Avatar> list, BmobException e) {
//                if (e == null) {
//                    finalVh.avatar.setImageUrl(list.get(0).getAvatar().getUrl(), imageLoader);
//                } else {
//                    Log.e("duke", e.toString());
//                }
//            }
//        });
        EaseUserUtils.setUserAvatar(act,comment.getUsername(),vh.avatar);
        EaseUserUtils.setUserNick(comment.getUsername(),vh.nickname);
        vh.gender.setText(comment.getAuthor().getSex());
        if(comment.getAuthor().getSex().equals("男")){
            vh.genderSign.setImageResource(R.drawable.ic_sex_male);
        }else{
            vh.genderSign.setImageResource(R.drawable.ic_sex_female);
        }
        vh.time.setText(comment.getCreatedAt().toString());
        vh.content.setText(comment.getContent());
        vh.chat.setTag(comment);
        vh.avatar.setTag(comment);
        vh.time.setTag(comment);
        vh.nickname.setTag(comment);
        vh.gender.setTag(comment);
        vh.genderSign.setTag(comment);

        Typeface typeface = Typeface.createFromAsset(act.getAssets(), "fonts/mi.ttf");
        vh.content.setTypeface(typeface);
        vh.nickname.setTypeface(typeface);
        vh.gender.setTypeface(typeface);
        vh.time.setTypeface(typeface);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        Comment comment = (Comment) v.getTag();
        switch (v.getId()){
            case R.id.item_comment_nickname:
            case R.id.item_comment_gender:
            case R.id.item_comment_gender_sign:
            case R.id.item_comment_time:
            case R.id.item_comment_avatar:
                String username = comment.getUsername();
                if(username!=null){
                    Intent intent3 = new Intent(act, FriendMsgActivity.class);
                    intent3.putExtra("author",username);
                    act.startActivity(intent3);
                }
                break;
            case R.id.item_comment_chat:
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
        private TextView nickname, gender, time, content;
        private ImageView genderSign;
        private LinearLayout ll;
        private AppCompatImageButton chat;
    }
}
