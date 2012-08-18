package com.ly.university.assistant;

import java.util.ArrayList;
import com.ly.university.assistant.persistence.DatabaseHelper;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author 王亚磊
 * 时间 2012年8月4日9:06:02
 * 用于编辑课表
 *
 */

public class C_A_Schedule_editFragment extends Fragment{


	public C_A_Schedule_editFragment() {
		// TODO Auto-generated constructor stub
		super();
	}
	public static final String TAG="课程安排编辑页面";
	
	@Override
	public View onCreateView(LayoutInflater lf , ViewGroup vg,  Bundle savedstate){
		View root = lf.inflate(R.layout.schedule_edit, vg,false);
		ViewPager vp = (ViewPager) root.findViewById(R.id.pager);
		vp.setAdapter(new Schedule_editFragment_Adapter(getActivity().getSupportFragmentManager()));
		return root;
	}

	static class Schedule_editFragment_Adapter extends FragmentPagerAdapter{

		public Schedule_editFragment_Adapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int week_day) {
			return new Schedule_item_Fragment(week_day);	
		}	
		@Override
		public int getCount() {

			return 7;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return "星期：" + (position + 1);
		}

	}
	
	static class Schedule_item_Fragment extends Fragment implements OnClickListener{
		
		String updateClassname=null;
		View editButton=null;
		int week_day;
		SQLiteDatabase db ;
		ArrayList<String> chlist ;
		LayoutInflater lf;
		ViewGroup root ;
		
		public Schedule_item_Fragment(int week_day) {
			super();
			this.week_day=week_day+1;
		}
		
		@Override 
		public void onCreate(Bundle savedStateBundle){
			super.onCreate(savedStateBundle);
			db = new DatabaseHelper(getActivity()).getWritableDatabase();
			Cursor ch = db.rawQuery( "SELECT ID AS _id, CLASSNAME FROM TIME ORDER BY BEGIN",null);
			chlist= new ArrayList<String>();
			if(ch.getCount()>0){
				do{
					ch.moveToNext();
					chlist.add(ch.getString(1));
				}while(!ch.isLast());
			}
			ch.close();
			ch=null;
		}
		
		@Override
		public View onCreateView(LayoutInflater lf , ViewGroup vg,  Bundle savedstate){
			this.lf=lf;
			View rootView = lf.inflate(R.layout.schedule_edit_main, vg,false);
			root=(ViewGroup) rootView.findViewById(R.id.root);
			//添加按钮，从数据库中取数据填充，注意注册按钮事件。
			for(int i=0;i<chlist.size();i++){
					Cursor c = db.rawQuery("SELECT ID AS _id, SUBJECT, ADDRESS " +
		                "FROM SCHEDULE"+
						"  WHERE CLASSNAME=? AND DAY=?"
						,new String[]{""+chlist.get(i), ""+week_day });
				View view = lf.inflate(R.layout.schedule_display_item, vg,false);
				((TextView)view.findViewById(R.id.classname)).setText(chlist.get(i));
				if(c.getCount()>0){
					c.moveToFirst();
					((TextView)view.findViewById(R.id.subject)).setText(c.getString(1));
					((TextView)view.findViewById(R.id.address)).setText(c.getString(2));
				}else {
					((TextView)view.findViewById(R.id.subject)).setText("无课");
					((TextView)view.findViewById(R.id.address)).setText("无信息");
				}
				((Button)view.findViewById(R.id.btn_edit)).setOnClickListener(this);
				root.addView(view);
				c.close();
			}
			return rootView;
		}
		//参考时间表输入操作。处理事件。
		@Override
		public void onClick(View v) {
			if(v.getId()!=R.id.save&&editButton!=null){
				alertDialog(v);
				return;
			}
			 int index=-1;
			 ViewGroup vg;
			 String classname;
			 String subject;
			 String address;
			 View view ;
			switch (v.getId()) {
			case R.id.btn_edit:
				vg=(ViewGroup) v.getParent();
				index = root.indexOfChild(vg);
				root.removeViewAt(index);
				classname =((TextView) vg.getChildAt(0)).getText().toString();
				subject =((TextView) vg.getChildAt(1)).getText().toString();
				address =((TextView) vg.getChildAt(2)).getText().toString();
				if(subject.equals("无课")){
					Log.v("空", "空数据");
					//转向添加
					view = lf.inflate(R.layout.schedule_edit_item, null);
					((TextView)view.findViewById(R.id.classname)).setText(classname);
					editButton= ((Button)view.findViewById(R.id.save));
					editButton.setOnClickListener(this);
					root.addView(view, index);
					return;
				}
				view = lf.inflate(R.layout.schedule_display_edit_item, null);
				((TextView)view.findViewById(R.id.classname)).setText(classname);
				((TextView)view.findViewById(R.id.subject)).setText(subject);
				((TextView)view.findViewById(R.id.address)).setText(address);
				((Button)view.findViewById(R.id.btn_update)).setOnClickListener(this);
				((Button)view.findViewById(R.id.btn_clear)).setOnClickListener(this);
				root.addView(view, index);
				break;

			case R.id.btn_clear:
				vg=(ViewGroup) v.getParent();
				index = root.indexOfChild(vg);
				classname =((TextView) vg.getChildAt(0)).getText().toString();
				root.removeViewAt(index);
				view = lf.inflate(R.layout.schedule_display_item, null);
				((TextView)view.findViewById(R.id.classname)).setText(classname);
				((TextView)view.findViewById(R.id.subject)).setText("无课");
				((TextView)view.findViewById(R.id.address)).setText("无");
				((Button)view.findViewById(R.id.btn_edit)).setOnClickListener(this);
				root.addView(view, index);
				break;
			case R.id.btn_update:
				vg=(ViewGroup) v.getParent();
				index = root.indexOfChild(vg);
				root.removeViewAt(index);
				classname =((TextView) vg.getChildAt(0)).getText().toString();
				subject =((TextView) vg.getChildAt(1)).getText().toString();
				address =((TextView) vg.getChildAt(2)).getText().toString();
				updateClassname=classname;				
				view = lf.inflate(R.layout.schedule_edit_item, null);
				((TextView)view.findViewById(R.id.classname)).setText(classname);
				((EditText)view.findViewById(R.id.subject)).setText(subject);
				((EditText)view.findViewById(R.id.address)).setText(address);
				editButton = ((Button)view.findViewById(R.id.save));
				editButton.setOnClickListener(this);
				root.addView(view, index);
				break;
			case R.id.save:
				vg=(ViewGroup) v.getParent();
				index = root.indexOfChild(vg);
				classname =((TextView) vg.getChildAt(0)).getText().toString();
				subject =((TextView) vg.getChildAt(1)).getText().toString();
				address =((TextView) vg.getChildAt(2)).getText().toString();
				if(subject!=null&&!subject.equals("")&&address!=null&&!address.equals("")){
					root.removeViewAt(index);
					//保存数据库
					if(updateClassname!=null){
					     db.delete("SCHEDULE", "CLASSNAME=? AND DAY=?", new String[]{""+updateClassname, ""+week_day});
					     updateClassname=null;
					}
					ContentValues values = new ContentValues();
					values.put("SUBJECT", subject);
					values.put("ADDRESS", address);
					values.put("DAY", week_day);
					values.put("CLASSNAME", classname);
					db.insert("SCHEDULE", null, values);
					
					view = lf.inflate(R.layout.schedule_display_item, null);
					((TextView)view.findViewById(R.id.classname)).setText(classname);
					((TextView)view.findViewById(R.id.subject)).setText(subject);
					((TextView)view.findViewById(R.id.address)).setText(address);
					((Button)view.findViewById(R.id.btn_edit)).setOnClickListener(this);
					root.addView(view, index);
					if(editButton!=null) editButton =null;
				}else {
					Toast.makeText(getActivity(), "请输入有效信息", Toast.LENGTH_SHORT).show();
					editButton=vg.getChildAt(3);
				}
				break;
			}
		}
		@Override
		public void onDetach(){
			super.onDetach();
			if(db.isOpen()) db.close();
		}
		
		private void alertDialog(View v){

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("您有一项编辑未完成,是否保存后退出?")
			       .setPositiveButton("确定", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   View v=editButton;
			        	   editButton = null;
			        	  Schedule_item_Fragment.this.onClick(v);
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
	}
	
}
