package com.ly.university.assistant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ClassHour_editFragment extends Fragment implements OnClickListener {

	public static final String TAG = "C_A_EditActivity";
	ViewGroup root;
	LayoutInflater lf;

	@Override
	public void onCreate(Bundle savedstateBundle) {
		super.onCreate(savedstateBundle);
		// 初始化对象，从数据库中取出数据
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		lf = inflater;
		View view = lf.inflate(R.layout.ca_classhour_edit, container, false);
		root = (ViewGroup) view.findViewById(R.id.classhour_edite_root);
		ImageButton add = (ImageButton) view
				.findViewById(R.id.btn_classhour_add);
		add.setOnClickListener(this);
		// 加载数据库中信息
		//循环加载数据
		return view;
	}

	/**
	 * 页面课时增删改查事件处理
	 */
	@Override
	public void onClick(View v) {

		View item = null;// 添加的view
		int index;// 行索引值（位置）
		ViewGroup vg = null;
		String name, begin, end;
		switch (v.getId()) {
		case R.id.btn_classhour_add:
			Log.v(TAG, "添加课时");
			item = lf.inflate(R.layout.classhour_edit_item, null);
			root.addView(item);
			((ImageButton) item.findViewById(R.id.classhour_edit_item_save))
					.setOnClickListener(this);
			break;
		case R.id.btn_classhour_delete:
			root.removeViewAt(root.indexOfChild((ViewGroup) v.getParent()));
			// 添加代码，删除数据库

			break;
		case R.id.classhour_edit_item_save:
			// 更改本行为修改后的状态。
			index = root.indexOfChild((ViewGroup) v.getParent());
			vg = (ViewGroup) v.getParent();
			name = ((EditText) vg.getChildAt(0)).getText().toString();
			begin = ((EditText) vg.getChildAt(1)).getText().toString() + ":"
					+ ((EditText) vg.getChildAt(3)).getText().toString();
			end = ((EditText) vg.getChildAt(4)).getText().toString() + ":"
					+ ((EditText) vg.getChildAt(6)).getText().toString();
			// 添加代码，验证输入

			root.removeViewAt(index);
			item = lf.inflate(R.layout.classhour_display_item, null);
			((TextView) item.findViewById(R.id.class_hour_name)).setText(name);
			((TextView) item.findViewById(R.id.start_time)).setText(begin);
			((TextView) item.findViewById(R.id.end_time)).setText(end);
			((ImageButton) item.findViewById(R.id.btn_classhour_edit))
					.setOnClickListener(this);
			root.addView(item, index);
			// 添加代码，插入数据库

			break;
		case R.id.btn_classhour_edit:
			// 更改本行为编辑状态
			Log.v("edit", "编辑");
			index = root.indexOfChild((ViewGroup) v.getParent());
			vg = (ViewGroup) v.getParent();
			name = ((TextView) vg.getChildAt(0)).getText().toString();
			begin = ((TextView) vg.getChildAt(1)).getText().toString();
			end = ((TextView) vg.getChildAt(2)).getText().toString();
			root.removeViewAt(index);
			
			item = lf.inflate(R.layout.classhour_display_edit_item, null);
			((TextView) item.findViewById(R.id.class_hour_name)).setText(name);
			((TextView) item.findViewById(R.id.start_time)).setText(begin);
			((TextView) item.findViewById(R.id.end_time)).setText(end);
			((ImageButton) item.findViewById(R.id.btn_classhour_update))
					.setOnClickListener(this);
			((ImageButton) item.findViewById(R.id.btn_classhour_delete))
					.setOnClickListener(this);
			root.addView(item, index);
			break;
		case R.id.btn_classhour_update:
			index = root.indexOfChild((ViewGroup) v.getParent());
			vg = (ViewGroup) v.getParent();
			name = ((TextView) vg.getChildAt(0)).getText().toString();
			begin = ((TextView) vg.getChildAt(1)).getText().toString();
			end = ((TextView) vg.getChildAt(2)).getText().toString();
			item = lf.inflate(R.layout.classhour_edit_item, null);
			root.removeViewAt(index);
			
			((EditText) item.findViewById(R.id.classhour_edit_item_name))
					.setText(name);
			((EditText) item.findViewById(R.id.classhour_edit_item_beginhour))
					.setText(begin.split(":")[0]);
			((EditText) item.findViewById(R.id.classhour_edit_item_beginminute))
					.setText(begin.split(":")[1]);
			((EditText) item.findViewById(R.id.classhour_edit_item_endhour))
					.setText(end.split(":")[0]);
			((EditText) item.findViewById(R.id.classhour_edit_item_endminute))
					.setText(end.split(":")[1]);
			((ImageButton) item.findViewById(R.id.classhour_edit_item_save))
					.setOnClickListener(this);
			root.addView(item, index);
			break;
		}
	}

}
