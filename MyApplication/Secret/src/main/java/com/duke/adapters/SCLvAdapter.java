package com.duke.adapters;

import java.util.List;

import com.duke.beans.User;
import com.duke.secret.HomeActivity;
import com.duke.secret.R;
import com.duke.utils.BitmapUtil;
import com.duke.utils.StringUtils;
import com.easemob.chat.EMConversation;
import com.easemob.chat.TextMessageBody;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SCLvAdapter extends BaseAdapter {
	private HomeActivity act;
	private List<EMConversation> conversations;

	public SCLvAdapter(HomeActivity act, List<EMConversation> conversations) {
		super();
		this.act = act;
		this.conversations = conversations;
	}

	@Override
	public int getCount() {
		return conversations.size();
	}

	@Override
	public Object getItem(int position) {
		return conversations.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = act.getLayoutInflater().inflate(R.layout.item_chatlist_adapter, null);
		TextView name = (TextView) convertView.findViewById(R.id.item_chatlist_name);
		TextView time = (TextView) convertView.findViewById(R.id.item_chatlist_time);
		TextView content = (TextView) convertView.findViewById(R.id.item_chatlist_content);
		final ImageView avatar = (ImageView) convertView.findViewById(R.id.item_chatlist_avatar);
		TextView from = (TextView) convertView.findViewById(R.id.item_chatlist_from);

		name.setText(conversations.get(position).getUserName());
		time.setText(StringUtils.getTime(conversations.get(position).getLastMessage().getMsgTime()));
		from.setText(conversations.get(position).getLastMessage().getFrom() + ": ");
		content.setText(((TextMessageBody) conversations.get(position).getLastMessage().getBody()).getMessage());

		BmobQuery<User> query = new BmobQuery<User>();
		query.addWhereEqualTo("username", conversations.get(position).getUserName());
		query.findObjects(new FindListener<User>() {

			@Override
			public void done(List<User> list, BmobException e) {
				if(e==null){
					User user = list.get(0);
					BitmapUtil.getBitUtil(act).display(avatar, user.getAvatar().getUrl());
				}else{
					avatar.setImageResource(R.drawable.avator_profile_default);
				}
			}
		});
		return convertView;
	}
}
