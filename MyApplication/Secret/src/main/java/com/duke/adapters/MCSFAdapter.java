package com.duke.adapters;

import java.util.List;

import com.duke.beans.Secret;
import com.duke.secret.HomeActivity;
import com.duke.secret.R;

import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MCSFAdapter extends BaseAdapter {
	List<Secret> secrets;
	HomeActivity act;

	public MCSFAdapter(List<Secret> secrets, HomeActivity act) {
		super();
		this.secrets = secrets;
		this.act = act;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (secrets == null || secrets.size() == 0) {
			return 0;
		}
		return secrets.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return secrets.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder vh = null;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = act.getLayoutInflater().inflate(R.layout.item_mcsf_adapter, null);
			vh.username = (TextView) convertView.findViewById(R.id.item_mcsf_adapter_username);
			vh.text = (TextView) convertView.findViewById(R.id.item_mcsf_adapter_text);
			vh.cv = (CardView) convertView.findViewById(R.id.item_mcsf_adapter_cv);
			vh.ll = (LinearLayout)convertView.findViewById(R.id.item_mcsf_adapter_ll);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.username.setText(secrets.get(position).getUsername() + ":");
		vh.username.setTextColor(secrets.get(position).getTextColor());
		vh.text.setText(secrets.get(position).getText());
		vh.text.setTextColor(secrets.get(position).getTextColor());
		vh.cv.setBackgroundColor(secrets.get(position).getBgColor());
		vh.ll.setBackgroundColor(secrets.get(position).getBgColor());

		return convertView;
	}

	static class ViewHolder {
		private TextView username, text;
		private CardView cv;
		private LinearLayout ll;

	}

}
