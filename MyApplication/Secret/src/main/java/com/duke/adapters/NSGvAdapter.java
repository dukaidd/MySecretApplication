package com.duke.adapters;

import java.util.List;

import com.duke.secret.NewSecretActivity;
import com.duke.secret.R;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class NSGvAdapter extends BaseAdapter {
	private List<Integer> colors;
	private NewSecretActivity act;

	public NSGvAdapter(List<Integer> colors, NewSecretActivity act) {
		super();
		this.colors = colors;
		this.act = act;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return colors.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return colors.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = act.getLayoutInflater().inflate(R.layout.item_nsgv_adapter, null);
		}
		View view = convertView.findViewById(R.id.item_nsgv_v);
		view.setBackgroundColor(colors.get(position));
		return convertView;
	}
}
