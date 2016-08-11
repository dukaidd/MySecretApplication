package com.duke.base;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.duke.secret.R;
import com.easemob.easeui.widget.EaseTitleBar;

public class BaseFragment extends Fragment {
	protected InputMethodManager inputMethodManager;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	}

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
	protected void hideSoftKeyboard() {
		if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getActivity().getCurrentFocus() != null)
				inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}
