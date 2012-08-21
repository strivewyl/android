package com.ly.university.assistant.businesslogic;

import java.util.Calendar;
import com.ly.university.assistant.C_A_SettingActivity;
import com.ly.university.assistant.persistence.DatabaseHelper;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;

/**
 * 2012年8月15日15:07
 * 设定时间服务,设定在下一节课(不管有无课)启动.
 * @author 王亚磊
 *
 */

public class C_A_TimingService extends IntentService {


	public C_A_TimingService() {
		super("C_A_TimingService");
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onHandleIntent(Intent arg) {
		Calendar setTime = Calendar.getInstance();
		String[] nextClassMessage = getNextClassMessage();
		String begin = nextClassMessage[1];
		int begin_hour = Integer.parseInt(begin.split(":")[0]);
		int begin_minute = Integer.parseInt(begin.split(":")[1]);
		int pretime =  Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(C_A_SettingActivity.CLASS_CLOCK_PRE_TIME, "10"));
		int a = begin_hour*60+begin_minute-pretime;
		setTime.set(Calendar.HOUR_OF_DAY, a/60);
		setTime.set(Calendar.MINUTE, a%60);
		setTime.set(Calendar.SECOND, 0);
		Intent intent = new Intent(this,C_A_AlarmBroadcastReciver.class);
		intent.putExtra("classname", nextClassMessage[0]);
		intent.putExtra("begin", begin);
		intent.putExtra("end", nextClassMessage[2]);
		
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(C_A_SettingActivity.CLASS_CLOCK_SETTING, true)){//设定上课提醒为  开:
			if(Integer.parseInt(nextClassMessage[3])==0){//今天还有课
				if(setTime.compareTo(Calendar.getInstance()) > 0){//当前时间在下一节课提前提醒时间之前,添加代码,设定启动提醒时间,否则不处理
					int day = setTime.get(Calendar.DAY_OF_WEEK)-1;
					intent.putExtra("week_day", day==0?7:day);
					((AlarmManager) this.getSystemService(Activity.ALARM_SERVICE)).set(AlarmManager.RTC_WAKEUP, setTime.getTimeInMillis(), 
							PendingIntent.getBroadcast( this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT));
					Log.v("设定闹钟时间", "设定的提醒时间为:"+DateFormat.format("MMM dd, yyyy h:mmaa", setTime.getTimeInMillis()));
				}
			}
			else {//今天没有课,明天第一节课提醒时间启动提醒
				setTime.add(Calendar.DATE, 1);
				int day = setTime.get(Calendar.DAY_OF_WEEK)-1;
				intent.putExtra("week_day", day==0?7:day);
				((AlarmManager) this.getSystemService(Activity.ALARM_SERVICE)).set(AlarmManager.RTC_WAKEUP, setTime.getTimeInMillis(), 
						PendingIntent.getBroadcast( this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT));
				Log.v("设定闹钟时间", "设定的提醒时间为:"+DateFormat.format("MMM dd, yyyy h:mmaa", setTime.getTimeInMillis()));
			}
		}
		//设定上课模式
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(C_A_SettingActivity.CLASS_MODE_SETTING, true)
				||PreferenceManager.getDefaultSharedPreferences(this).getBoolean(C_A_SettingActivity.CLASS_LOCK_SETTING, false)){
			setTime = Calendar.getInstance();
		    setTime.add(Calendar.DATE, Integer.parseInt(nextClassMessage[3]));
			setTime.set(Calendar.HOUR_OF_DAY,begin_hour);
			setTime.set(Calendar.MINUTE, begin_minute);
			setTime.set(Calendar.SECOND, 0);
			Intent  classmode =  new Intent(this,C_A_ClassModeBroadcastReceiver.class);
			classmode.putExtra("classname", nextClassMessage[0]);
			classmode.putExtra("end", nextClassMessage[2]);
			int day = setTime.get(Calendar.DAY_OF_WEEK)-1;
			classmode.putExtra("week_day", day==0?7:day);
			((AlarmManager) this.getSystemService(Activity.ALARM_SERVICE)).set(AlarmManager.RTC_WAKEUP, setTime.getTimeInMillis(), 
					PendingIntent.getBroadcast( this, 0,classmode, PendingIntent.FLAG_CANCEL_CURRENT));
			Log.v("设定上课模式启动时间", "设定的上课模式启动时间为:"+DateFormat.format("MMM dd, yyyy h:mmaa", setTime.getTimeInMillis()));
		}
	}
	/**
	 * 
	 * @return 返回当前时间的下一节课的信息.
	 * 索引: 0: 课时名  1:上课时间字符串   2: 下课时间字符串  3: 增加天数(今天0,明天1)
	 */
	String[] getNextClassMessage(){
		SQLiteDatabase db = new DatabaseHelper(this).getReadableDatabase();
		Calendar now = Calendar.getInstance();
		String hour =""+ now.get(Calendar.HOUR_OF_DAY);
		String minute=""+ now.get(Calendar.MINUTE);
		String begin = ( Integer.parseInt(hour)<10 && hour.charAt(0)!='0' ? ""+0+hour : hour ) +":"
				+ ((Integer.parseInt(minute)<10 && minute.charAt(0)!='0')? ""+0+minute : minute) ;
		Cursor c = db.rawQuery("SELECT ID AS _id,CLASSNAME, BEGIN, END FROM TIME WHERE BEGIN>? ORDER BY BEGIN",  new String[]{begin});
		//今天没有课,处理
		if(c.getCount()<1){
			c.close();
			Cursor nc =db.rawQuery("SELECT ID AS _id,CLASSNAME, BEGIN, END FROM TIME  ORDER BY BEGIN", null);
			nc.moveToFirst();
			String[] reslut = new String[]{nc.getString(1),nc.getString(2),nc.getString(3),""+1};
			nc.close();
			db.close();
			return reslut;
		}
		c.moveToFirst();
		String[] reslut = new String[]{c.getString(1),c.getString(2),c.getString(3),""+0};
		c.close();
		db.close();
		return reslut;
	}	
}
