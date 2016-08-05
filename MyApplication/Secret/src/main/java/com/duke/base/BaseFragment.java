package com.duke.base;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

public class BaseFragment extends Fragment {
	public View findViewById(int id) {
		return getView().findViewById(id);
	}

	public void dialog(String title, String message, int icon, String btn1, String btn2, OnClickListener l,
			OnClickListener l1) {
		Builder dialog = new Builder(getActivity());
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setIcon(icon);
		dialog.setPositiveButton(btn1, l);
		dialog.setNegativeButton(btn2, l1);
		dialog.create().show();
	}

	public void toast(String text) {
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
	}

}
