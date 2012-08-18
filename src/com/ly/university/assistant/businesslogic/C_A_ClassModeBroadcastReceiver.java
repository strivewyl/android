package com.ly.university.assistant.businesslogic;

import com.ly.university.assistant.persistence.DatabaseHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class C_A_ClassModeBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//��ѯ�Ƿ��п�,����п�,�������������Ͽ�״̬,���޿�,��������ʱ����.
		SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
		Cursor c = db.rawQuery("SELECT ID AS _id, SUBJECT, ADDRESS FROM SCHEDULE" +
				"  WHERE CLASSNAME=? AND DAY=?", new String[] {intent.getStringExtra("classname"),""+intent.getIntExtra("week_day", 1)});
		if(c.getCount()>0){//�п�		
			
		}
	}
}
