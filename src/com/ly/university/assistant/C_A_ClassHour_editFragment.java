package com.ly.university.assistant;

import com.ly.university.assistant.persistence.DatabaseHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Toast;

public class C_A_ClassHour_editFragment extends Fragment implements
		OnClickListener {

	String updateName=null;
	View editButton=null; //如果是空,则表示没有编辑项.
	ScrollListener sl;
	public interface ScrollListener{
		public void scroll(int y);
	}
	public static final String TAG = "C_A_EditActivity";
	ViewGroup root;
	LayoutInflater lf;
	Cursor c;
	SQLiteDatabase db;

	public C_A_ClassHour_editFragment() {
		super();
	}

	@Override
	public void onCreate(Bundle savedstateBundle) {
		super.onCreate(savedstateBundle);
		// 初始化对象，从数据库中取出数据
		db = new DatabaseHelper(getActivity()).getWritableDatabase();
		c = db.rawQuery(
				"SELECT ID AS _id , CLASSNAME, BEGIN, END FROM TIME ORDER BY BEGIN",
				new String[] {});
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
		// 循环加载数据
		if(c.getCount()>0){
			do {
				c.moveToNext();
				View v = lf.inflate(R.layout.classhour_display_item, root, false);
				((TextView) v.findViewById(R.id.class_hour_name)).setText(c
						.getString(1));
				((TextView) v.findViewById(R.id.start_time))
						.setText(c.getString(2));
				((TextView) v.findViewById(R.id.end_time)).setText(c.getString(3));
				((ImageButton) v.findViewById(R.id.btn_classhour_edit))
						.setOnClickListener(this);
				root.addView(v);
			} while (!c.isLast());
		}
		c.close();
		return view;
	}
	@Override
	public void onAttach(Activity a){
		super.onAttach(a);
		sl = (ScrollListener)a;
	}

	/**
	 * 页面课时增删改查事件处理
	 */
	@Override
	public void onClick(View v) {
		if(v.getId()!=R.id.classhour_edit_item_save&&editButton!=null)
			//添加代码,提示用户还有一项未编辑结束,需要保存后离开.
		{
			alertDialog(v);
			return;
		}
		View item = null;// 添加的view
		int index;// 行索引值（位置）
		ViewGroup vg = null;
		String name, begin, end;
		switch (v.getId()) {
		case R.id.btn_classhour_add:
			Log.v(TAG, "添加课时");
			item = lf.inflate(R.layout.classhour_edit_item, null);
			root.addView(item,1);
			editButton=((ImageButton) item.findViewById(R.id.classhour_edit_item_save));
			editButton.setOnClickListener(this);
			//添加代码,让滚动条滚动到活动位置
			int[] location = new int[2];
			root.getChildAt(root.getChildCount()-1).getLocationInWindow(location);
//			int y = location[1];
//			Log.v("位置", "位置为: "+location[0]+","+y+","+root.getChildCount());
			//sl.scroll(y);
			break;
		case R.id.btn_classhour_delete:
			final View fv = v;
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("您将删除所有该节次的信息,是否继续?")
			       .setPositiveButton("确定", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        		ViewGroup vg = (ViewGroup) fv.getParent();
			    			int index = root.indexOfChild(vg);
			    			root.removeViewAt(index);
			    			// 添加代码，删除数据库
			    			String s =((TextView) vg.getChildAt(0)).getText()
									.toString();
			    			db.delete("TIME", "CLASSNAME=?",new String[] {s });
			    			db.delete("SCHEDULE", "CLASSNAME=?", 	new String[] {s });
			    			
			           }
			       })
			       .setNegativeButton("取消", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       }).setTitle("提示");
			AlertDialog alert = builder.create();
			alert.show();
			
			break;
		case R.id.classhour_edit_item_save:
			// 更改本行为修改后的状态。
			vg = (ViewGroup) v.getParent();
			index = root.indexOfChild(vg);
			name = ((EditText) vg.getChildAt(0)).getText().toString();
			String begin_hour = ((EditText) vg.getChildAt(1)).getText()
					.toString();
			String begin_minute = ((EditText) vg.getChildAt(3)).getText()
					.toString();
			String end_hour = ((EditText) vg.getChildAt(4)).getText()
					.toString();
			String end_minute = ((EditText) vg.getChildAt(6)).getText()
					.toString();
			// 添加代码，验证输入
			String [] times = getTime(begin_hour, begin_minute, end_hour, end_minute);
			if (name != null && !name.equals("") && times!=null)
			{
				begin=times[0];
				end = times[1];
			}
			else 
			{
				Toast.makeText(getActivity(), "请检查输入信息! 请注意:时间格式为24小时  ", Toast.LENGTH_LONG).show();
				editButton=vg.getChildAt(7);
				return;
			}		

			// 添加代码，插入数据库
			if(updateName!=null){
				if(!updateName.equals(name)){
					final String name1 =name;
					final String begin1 =begin;
					final String end1=end;
					final int  index1 = index;
					AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
					builder2.setMessage("由于您修改了课时名,请您选择您所需要的操作: \n" 
					+"删除:   这将导致您所有"+"\""+updateName+"\""+"的课程被删除 \n"
					+"更改:   这将导致您所有"+"\""+updateName+"\""+"的课程都将更名为"+"\""+name1+"\""
					)
					       .setPositiveButton("删除", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
									Log.v("删除节次数",""+db.delete("TIME", "CLASSNAME=?",new String[] {updateName }));
									Log.v("删除课程数",""+db.delete("SCHEDULE", "CLASSNAME=?", new String[] {updateName}));
									updateName=null;
									ContentValues cv = new ContentValues();
									cv.put("CLASSNAME", name1);
									cv.put("BEGIN", begin1);
									cv.put("END",end1);
									db.insert("TIME", null, cv);	
									if(editButton!=null) editButton =null;		
									root.removeViewAt(index1);
									View item = lf.inflate(R.layout.classhour_display_item, null);
									((TextView) item.findViewById(R.id.class_hour_name)).setText(name1);
									 ((TextView) item.findViewById(R.id.start_time)).setText(begin1);
									 ((TextView) item.findViewById(R.id.end_time)).setText(end1);
									((ImageButton) item.findViewById(R.id.btn_classhour_edit))
											.setOnClickListener(C_A_ClassHour_editFragment.this);
									root.addView(item, index1);
					           }
					       })
					       .setNeutralButton("更改", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					        	   ContentValues cv = new ContentValues();
					        	   cv.put("CLASSNAME",name1);
					        	   	Log.v("更新课程数",""+db.update("SCHEDULE", cv, "CLASSNAME=?", new String[]{updateName}));
									Log.v("删除节次数",""+db.delete("TIME", "CLASSNAME=?",new String[] {updateName }));
									updateName=null;
									cv.clear();
									cv.put("CLASSNAME", name1);
									cv.put("BEGIN", begin1);
									cv.put("END",end1);
									db.insert("TIME", null, cv);	
									if(editButton!=null) editButton =null;		
									root.removeViewAt(index1);
									View item = lf.inflate(R.layout.classhour_display_item, null);
									((TextView) item.findViewById(R.id.class_hour_name)).setText(name1);
									 ((TextView) item.findViewById(R.id.start_time)).setText(begin1);
									 ((TextView) item.findViewById(R.id.end_time)).setText(end1);
									((ImageButton) item.findViewById(R.id.btn_classhour_edit))
											.setOnClickListener(C_A_ClassHour_editFragment.this);
									root.addView(item, index1);
					                return;
					           }
					       }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					                dialog.cancel();
					                return;
					           }
					       })
					       .setTitle("请注意");
					AlertDialog alert2 = builder2.create();
					alert2.show();	
				}else {
					db.delete("TIME", "CLASSNAME=?",new String[] { ""	+ updateName });
					updateName=null;
					ContentValues cv = new ContentValues();
					cv.put("CLASSNAME", name);
					cv.put("BEGIN", begin);
					cv.put("END",end);
					db.insert("TIME", null, cv);	
					if(editButton!=null) editButton =null;		
					root.removeViewAt(index);
					item = lf.inflate(R.layout.classhour_display_item, null);
					((TextView) item.findViewById(R.id.class_hour_name)).setText(name);
					 ((TextView) item.findViewById(R.id.start_time)).setText(begin);
					 ((TextView) item.findViewById(R.id.end_time)).setText(end);
					((ImageButton) item.findViewById(R.id.btn_classhour_edit))
							.setOnClickListener(this);
					root.addView(item, index);
				}
			}
			else {
				ContentValues cv = new ContentValues();
				cv.put("CLASSNAME", name);
				cv.put("BEGIN", begin);
				cv.put("END",end);
				db.insert("TIME", null, cv);	
				if(editButton!=null) editButton =null;		
				root.removeViewAt(index);
				item = lf.inflate(R.layout.classhour_display_item, null);
				((TextView) item.findViewById(R.id.class_hour_name)).setText(name);
				 ((TextView) item.findViewById(R.id.start_time)).setText(begin);
				 ((TextView) item.findViewById(R.id.end_time)).setText(end);
				((ImageButton) item.findViewById(R.id.btn_classhour_edit))
						.setOnClickListener(this);
				root.addView(item, index);
			}
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
			updateName=name;
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
			editButton=((ImageButton) item.findViewById(R.id.classhour_edit_item_save));
		    editButton.setOnClickListener(this);
			root.addView(item, index);
			//添加代码,删除数据库
			//db.delete("TIME", "CLASSNAME=?",new String[] { ""	+ name });
			break;
		}
	}
	@Override
	public void onDetach() {
		super.onDetach();
		if (db.isOpen()) db.close();
	}

	private void alertDialog(View v){

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("您有一项编辑未完成,是否保存后退出?")
		       .setPositiveButton("确定", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   View v = editButton;
		        	   editButton=null;
		        	   C_A_ClassHour_editFragment.this.onClick(v);
		           }
		       })
		       .setNegativeButton("取消", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       }).setTitle("提示");
		AlertDialog alert = builder.create();
		alert.show();
	}
	/**
	 * 
	 * @param begin_hour  开始小时字符串
	 * @param begin_minute  开始分钟字符串
	 * @param end_hour  结束小时字符串
	 * @param end_minute 结束分钟字符串
	 * @return 如果符合开始时间小于结束时间,返回两个时间字符串  格式为   05:05   **:**
	 * 若不符合,返回null
	 */
	String[] getTime(String begin_hour,String begin_minute,String end_hour,String end_minute){
		int bh,bm,eh,em;
		try{
			bh = Integer.parseInt(begin_hour);
			bm = Integer.parseInt(begin_minute);
			eh = Integer.parseInt(end_hour);
			em = Integer.parseInt(end_minute);
			if(bh<0 || bh>23 || eh<0 || eh>23 || em<0 ||em>59 || bm<0 || bm>59) return null;
			if(bh*60+bm > eh*60+em) return null;
			return new String[]{( bh<10 && begin_hour.charAt(0)!='0' ? ""+0+begin_hour : begin_hour ) +":"
					+ ((bm<10 && begin_minute.charAt(0)!='0')? ""+0+begin_minute : begin_minute) 
					, ( eh<10 && end_hour.charAt(0)!='0' ? ""+0+end_hour : end_hour )+":"
					+(( em<10 && end_minute.charAt(0)!='0') ? ""+0+end_minute : end_minute) };
		}catch(Exception e){
			Log.v("格式错误","格式错误");
			return null;
		}
	}
}
