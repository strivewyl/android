package com.ly.university.assistant;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ly.university.assistant.persistence.DatabaseHelper;

/**
 * 
 * @author ������ /n 2012��7��31��10:22:09 ��ʾ�α�
 */
public  class C_A_ClassTableFragment extends ListFragment {
	SQLiteDatabase db;
	private Cursor c=null;
	private onListItemClickListener mListener;
	public interface onListItemClickListener{
		public void onListItemClick(int position, String title, String subject, String address, String start , String end);
	}
	public static C_A_ClassTableFragment newInstance(int arg) {
		C_A_ClassTableFragment fragment = new C_A_ClassTableFragment();
		Bundle args = new Bundle();
		args.putInt("week_day", arg);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (onListItemClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }
	@Override 
	public void onStart(){
		super.onStart();
		db= new DatabaseHelper(getActivity())
		.getReadableDatabase();
		c = db.rawQuery(
				"SELECT TIME.ID AS _id, TIME.CLASSNAME,SUBJECT,ADDRESS,BEGIN,END "
						+ " FROM SCHEDULE,TIME "
						+ "WHERE SCHEDULE.CLASSNAME=TIME.CLASSNAME AND DAY=? "
						+ "ORDER BY BEGIN", new String[] { "" + getArguments().getInt("week_day", 1) });
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
				getActivity(), R.layout.classtable_item, c,
				new String[] { "CLASSNAME", "SUBJECT", "ADDRESS", "BEGIN",
						"END" }, new int[] { R.id.classname, R.id.subject,
						R.id.address, R.id.begin, R.id.end }, 1);
		setListAdapter(adapter);
		Log.v("db", "��������ɹ�");
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.classtable_oneday, container,
				false);
	}	
	@Override
	public void onListItemClick(ListView parent, View v, int position,
			long id) {
		// ��Ӵ������ ��ʾ��ϸ��Ϣ	
		Boolean b = c.moveToPosition(position);
		Log.v("Tag", "����� " + position + " ��"+", c.moveToPosition(position)= "+b);
		mListener.onListItemClick(position, c.getString(1) ,c.getString(2), c.getString(3), c.getString(4),c.getString(5));
	}
   @Override
   public void onStop(){
	   super.onStop();
	   if(!c.isClosed()) c.close();
	   if(db.isOpen()) db.close();
   }
}