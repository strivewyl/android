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
	View editButton=null; //����ǿ�,���ʾû�б༭��.
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
		// ��ʼ�����󣬴����ݿ���ȡ������
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
		// �������ݿ�����Ϣ
		// ѭ����������
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
	 * ҳ���ʱ��ɾ�Ĳ��¼�����
	 */
	@Override
	public void onClick(View v) {
		if(v.getId()!=R.id.classhour_edit_item_save&&editButton!=null)
			//��Ӵ���,��ʾ�û�����һ��δ�༭����,��Ҫ������뿪.
		{
			alertDialog(v);
			return;
		}
		View item = null;// ��ӵ�view
		int index;// ������ֵ��λ�ã�
		ViewGroup vg = null;
		String name, begin, end;
		switch (v.getId()) {
		case R.id.btn_classhour_add:
			Log.v(TAG, "��ӿ�ʱ");
			item = lf.inflate(R.layout.classhour_edit_item, null);
			root.addView(item,1);
			editButton=((ImageButton) item.findViewById(R.id.classhour_edit_item_save));
			editButton.setOnClickListener(this);
			//��Ӵ���,�ù������������λ��
			int[] location = new int[2];
			root.getChildAt(root.getChildCount()-1).getLocationInWindow(location);
//			int y = location[1];
//			Log.v("λ��", "λ��Ϊ: "+location[0]+","+y+","+root.getChildCount());
			//sl.scroll(y);
			break;
		case R.id.btn_classhour_delete:
			final View fv = v;
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("����ɾ�����иýڴε���Ϣ,�Ƿ����?")
			       .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        		ViewGroup vg = (ViewGroup) fv.getParent();
			    			int index = root.indexOfChild(vg);
			    			root.removeViewAt(index);
			    			// ��Ӵ��룬ɾ�����ݿ�
			    			String s =((TextView) vg.getChildAt(0)).getText()
									.toString();
			    			db.delete("TIME", "CLASSNAME=?",new String[] {s });
			    			db.delete("SCHEDULE", "CLASSNAME=?", 	new String[] {s });
			    			
			           }
			       })
			       .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       }).setTitle("��ʾ");
			AlertDialog alert = builder.create();
			alert.show();
			
			break;
		case R.id.classhour_edit_item_save:
			// ���ı���Ϊ�޸ĺ��״̬��
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
			// ��Ӵ��룬��֤����
			String [] times = getTime(begin_hour, begin_minute, end_hour, end_minute);
			if (name != null && !name.equals("") && times!=null)
			{
				begin=times[0];
				end = times[1];
			}
			else 
			{
				Toast.makeText(getActivity(), "����������Ϣ! ��ע��:ʱ���ʽΪ24Сʱ  ", Toast.LENGTH_LONG).show();
				editButton=vg.getChildAt(7);
				return;
			}		

			// ��Ӵ��룬�������ݿ�
			if(updateName!=null){
				if(!updateName.equals(name)){
					final String name1 =name;
					final String begin1 =begin;
					final String end1=end;
					final int  index1 = index;
					AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
					builder2.setMessage("�������޸��˿�ʱ��,����ѡ��������Ҫ�Ĳ���: \n" 
					+"ɾ��:   �⽫����������"+"\""+updateName+"\""+"�Ŀγ̱�ɾ�� \n"
					+"����:   �⽫����������"+"\""+updateName+"\""+"�Ŀγ̶�������Ϊ"+"\""+name1+"\""
					)
					       .setPositiveButton("ɾ��", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
									Log.v("ɾ���ڴ���",""+db.delete("TIME", "CLASSNAME=?",new String[] {updateName }));
									Log.v("ɾ���γ���",""+db.delete("SCHEDULE", "CLASSNAME=?", new String[] {updateName}));
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
					       .setNeutralButton("����", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					        	   ContentValues cv = new ContentValues();
					        	   cv.put("CLASSNAME",name1);
					        	   	Log.v("���¿γ���",""+db.update("SCHEDULE", cv, "CLASSNAME=?", new String[]{updateName}));
									Log.v("ɾ���ڴ���",""+db.delete("TIME", "CLASSNAME=?",new String[] {updateName }));
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
					       }).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					                dialog.cancel();
					                return;
					           }
					       })
					       .setTitle("��ע��");
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
			// ���ı���Ϊ�༭״̬
			Log.v("edit", "�༭");
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
			//��Ӵ���,ɾ�����ݿ�
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
		builder.setMessage("����һ��༭δ���,�Ƿ񱣴���˳�?")
		       .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   View v = editButton;
		        	   editButton=null;
		        	   C_A_ClassHour_editFragment.this.onClick(v);
		           }
		       })
		       .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       }).setTitle("��ʾ");
		AlertDialog alert = builder.create();
		alert.show();
	}
	/**
	 * 
	 * @param begin_hour  ��ʼСʱ�ַ���
	 * @param begin_minute  ��ʼ�����ַ���
	 * @param end_hour  ����Сʱ�ַ���
	 * @param end_minute ���������ַ���
	 * @return ������Ͽ�ʼʱ��С�ڽ���ʱ��,��������ʱ���ַ���  ��ʽΪ   05:05   **:**
	 * ��������,����null
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
			Log.v("��ʽ����","��ʽ����");
			return null;
		}
	}
}
