package com.ly.university.assistant.businesslogic;

import com.ly.university.assistant.C_A_SettingActivity;
import com.ly.university.assistant.R;
import com.ly.university.assistant.persistence.DatabaseHelper;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class C_A_AlarmBroadcastReciver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//��������п�,����
		String classname = intent.getStringExtra("classname");
		SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
		Cursor c = db.rawQuery("SELECT ID AS _id, SUBJECT, ADDRESS FROM SCHEDULE" +
				"  WHERE CLASSNAME=? AND DAY=?", new String[] {classname,""+intent.getIntExtra("week_day", 1)});
		if(c.getCount()>0){//�п�
			c.moveToFirst();
			Intent alarmIntent = new Intent(context, C_A_AlarmActivity.class);
			alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			alarmIntent.putExtra("classname", classname);
			alarmIntent.putExtra("subject", c.getString(1));
			alarmIntent.putExtra("address", c.getString(2));
			alarmIntent.putExtra("begin", intent.getStringExtra("begin"));
			alarmIntent.putExtra("end", intent.getStringExtra("end"));
			
			if(!PreferenceManager.getDefaultSharedPreferences(context)
					.getString(C_A_SettingActivity.CLASS_CLOCK_ALARM_MODE, "0").equals("4")){//��֪ͨ��ʽ
				context.startActivity(alarmIntent);
				Log.v("��������", "��ʱ��Ϊ: "+ classname +"�п�, ��������");
			}else {//֪ͨ��ʽ
				Log.v("����֪ͨ", "��ʱ��Ϊ: "+ classname +"�п�, ����֪ͨ");
				alarmIntent.putExtra("notification", true);
				PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, alarmIntent, 0);
				//2.0��֧�����µ���
//				Notification n = new Notification.Builder(context)
//				.setContentTitle(classname)
//				.setContentText("����"+classname+"�������"
//				+PreferenceManager.getDefaultSharedPreferences(context)
//				.getString(C_A_SettingActivity.CLASS_CLOCK_PRE_TIME, "10")+"����")
//				.setSmallIcon(R.drawable.notification_icon)
//				.setWhen(System.currentTimeMillis())
//				.setAutoCancel(true)
//				.setContentIntent(pendingIntent)
//				.setDefaults(Notification.DEFAULT_ALL)
//				.getNotification();
//				NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			
				//���ü��ݴ���:
				Notification n = new Notification(R.drawable.notification_icon, "��ѧ����,�Ͽ�����", System.currentTimeMillis());
				n.setLatestEventInfo(context, "�Ͽ�����", "����"+classname+"�������"
						+PreferenceManager.getDefaultSharedPreferences(context)
						.getString(C_A_SettingActivity.CLASS_CLOCK_PRE_TIME, "10")+"����", pendingIntent);
				n.defaults=Notification.DEFAULT_ALL;
				n.flags|= Notification.FLAG_AUTO_CANCEL;
				((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(0,n);
				//������������Ļ
				KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE); // �õ�����������������
				final KeyguardLock kl = km.newKeyguardLock("�����Զ�����"); // ������LogCat���õ�Tag
				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);// ��ȡ��Դ����������
				PowerManager.WakeLock  wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
						| PowerManager.SCREEN_DIM_WAKE_LOCK, "������Ļ");
				kl.disableKeyguard();
			  wl.acquire(10000);
				final Handler handler = new Handler(){
					@Override
					public void handleMessage(Message msg) {
						Log.v("������","������");
						kl.reenableKeyguard();
					}
				};
			  new Thread(){
				  @Override
				  public void run(){
					  try {
						sleep(10000);
						Log.v("����ʱ�䵽", "����ʱ�䵽");
						handler.sendEmptyMessage(0);
					} catch (InterruptedException e) {
						Log.v("�쳣", "�����ȴ��쳣");
						e.printStackTrace();
					}
				  }
			  }.start();
			}
		}else Log.v("����", "��ʱ��Ϊ: "+ classname +"�޿β�����");
		c.close();
		db.close();
	}
}
