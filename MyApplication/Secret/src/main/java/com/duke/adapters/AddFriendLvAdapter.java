package com.duke.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.duke.beans.User;
import com.duke.customview.CircleImageView;
import com.duke.secret.AddFriendActivity;
import com.duke.secret.HomeActivity;
import com.duke.secret.R;
import com.easemob.easeui.utils.EaseUserUtils;

import java.util.List;

/**
 * Created by dukaidd on 2016/8/19.
 */

public class AddFriendLvAdapter extends BaseAdapter {
    private AddFriendActivity act;
    private List<User> users;

    public AddFriendLvAdapter(AddFriendActivity act, List<User> users) {
        this.act = act;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if(convertView==null){
            vh = new ViewHolder();
            convertView = act.getLayoutInflater().inflate(R.layout.item_add_friend_lv,null);
            vh.avatar = (CircleImageView) convertView.findViewById(R.id.avatar);
            vh.nickname = (TextView) convertView.findViewById(R.id.nickname);
            vh.slogan = (TextView) convertView.findViewById(R.id.slogan);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        User user = users.get(position);
        EaseUserUtils.setUserAvatar(act,user.getUsername(),vh.avatar);
        if(user.getNickname()!=null){
            vh.nickname.setText(user.getNickname());
        }else{
            EaseUserUtils.setUserNick(user.getUsername(),vh.nickname);
        }
        vh.slogan.setText(user.getSlogan());

        vh.nickname.setTypeface(HomeActivity.getInstance().typeface);
        vh.slogan.setTypeface(HomeActivity.getInstance().typeface);

        return convertView;
    }
    class ViewHolder{
        private CircleImageView avatar;
        private TextView nickname,slogan;
    }

}
