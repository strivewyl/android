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
		//如果本节有课,启动
		String classname = intent.getStringExtra("classname");
		SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
		Cursor c = db.rawQuery("SELECT ID AS _id, SUBJECT, ADDRESS FROM SCHEDULE" +
				"  WHERE CLASSNAME=? AND DAY=?", new String[] {classname,""+intent.getIntExtra("week_day", 1)});
		if(c.getCount()>0){//有课
			c.moveToFirst();
			Intent alarmIntent = new Intent(context, C_A_AlarmActivity.class);
			alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			alarmIntent.putExtra("classname", classname);
			alarmIntent.putExtra("subject", c.getString(1));
			alarmIntent.putExtra("address", c.getString(2));
			alarmIntent.putExtra("begin", intent.getStringExtra("begin"));
			alarmIntent.putExtra("end", intent.getStringExtra("end"));
			
			if(!PreferenceManager.getDefaultSharedPreferences(context)
					.getString(C_A_SettingActivity.CLASS_CLOCK_ALARM_MODE, "0").equals("4")){//非通知方式
				context.startActivity(alarmIntent);
				Log.v("启动闹钟", "课时名为: "+ classname +"有课, 启动闹钟");
			}else {//通知方式
				Log.v("启动通知", "课时名为: "+ classname +"有课, 启动通知");
				alarmIntent.putExtra("notification", true);
				PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, alarmIntent, 0);
				//2.0不支持以下的类
//				Notification n = new Notification.Builder(context)
//				.setContentTitle(classname)
//				.setContentText("距离"+classname+"还有最后"
//				+PreferenceManager.getDefaultSharedPreferences(context)
//				.getString(C_A_SettingActivity.CLASS_CLOCK_PRE_TIME, "10")+"分钟")
//				.setSmallIcon(R.drawable.notification_icon)
//				.setWhen(System.currentTimeMillis())
//				.setAutoCancel(true)
//				.setContentIntent(pendingIntent)
//				.setDefaults(Notification.DEFAULT_ALL)
//				.getNotification();
//				NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			
				//采用兼容代码:
				Notification n = new Notification(R.drawable.notification_icon, "大学助手,上课提醒", System.currentTimeMillis());
				n.setLatestEventInfo(context, "上课提醒", "距离"+classname+"还有最后"
						+PreferenceManager.getDefaultSharedPreferences(context)
						.getString(C_A_SettingActivity.CLASS_CLOCK_PRE_TIME, "10")+"分钟", pendingIntent);
				n.defaults=Notification.DEFAULT_ALL;
				n.flags|= Notification.FLAG_AUTO_CANCEL;
				((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(0,n);
				//开锁并点亮屏幕
				KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE); // 得到键盘锁管理器对象
				final KeyguardLock kl = km.newKeyguardLock("闹钟自动解锁"); // 参数是LogCat里用的Tag
				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);// 获取电源管理器对象
				PowerManager.WakeLock  wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
						| PowerManager.SCREEN_DIM_WAKE_LOCK, "点亮屏幕");
				kl.disableKeyguard();
			  wl.acquire(10000);
				final Handler handler = new Handler(){
					@Override
					public void handleMessage(Message msg) {
						Log.v("反解锁","反解锁");
						kl.reenableKeyguard();
					}
				};
			  new Thread(){
				  @Override
				  public void run(){
					  try {
						sleep(10000);
						Log.v("休眠时间到", "休眠时间到");
						handler.sendEmptyMessage(0);
					} catch (InterruptedException e) {
						Log.v("异常", "琐机等待异常");
						e.printStackTrace();
					}
				  }
			  }.start();
			}
		}else Log.v("闹钟", "课时名为: "+ classname +"无课不启动");
		c.close();
		db.close();
	}
}
