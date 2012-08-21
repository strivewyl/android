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
 * 2012��8��15��15:07
 * �趨ʱ�����,�趨����һ�ڿ�(�������޿�)����.
 * @author ������
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
		
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(C_A_SettingActivity.CLASS_CLOCK_SETTING, true)){//�趨�Ͽ�����Ϊ  ��:
			if(Integer.parseInt(nextClassMessage[3])==0){//���컹�п�
				if(setTime.compareTo(Calendar.getInstance()) > 0){//��ǰʱ������һ�ڿ���ǰ����ʱ��֮ǰ,��Ӵ���,�趨��������ʱ��,���򲻴���
					int day = setTime.get(Calendar.DAY_OF_WEEK)-1;
					intent.putExtra("week_day", day==0?7:day);
					((AlarmManager) this.getSystemService(Activity.ALARM_SERVICE)).set(AlarmManager.RTC_WAKEUP, setTime.getTimeInMillis(), 
							PendingIntent.getBroadcast( this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT));
					Log.v("�趨����ʱ��", "�趨������ʱ��Ϊ:"+DateFormat.format("MMM dd, yyyy h:mmaa", setTime.getTimeInMillis()));
				}
			}
			else {//����û�п�,�����һ�ڿ�����ʱ����������
				setTime.add(Calendar.DATE, 1);
				int day = setTime.get(Calendar.DAY_OF_WEEK)-1;
				intent.putExtra("week_day", day==0?7:day);
				((AlarmManager) this.getSystemService(Activity.ALARM_SERVICE)).set(AlarmManager.RTC_WAKEUP, setTime.getTimeInMillis(), 
						PendingIntent.getBroadcast( this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT));
				Log.v("�趨����ʱ��", "�趨������ʱ��Ϊ:"+DateFormat.format("MMM dd, yyyy h:mmaa", setTime.getTimeInMillis()));
			}
		}
		//�趨�Ͽ�ģʽ
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
			Log.v("�趨�Ͽ�ģʽ����ʱ��", "�趨���Ͽ�ģʽ����ʱ��Ϊ:"+DateFormat.format("MMM dd, yyyy h:mmaa", setTime.getTimeInMillis()));
		}
	}
	/**
	 * 
	 * @return ���ص�ǰʱ�����һ�ڿε���Ϣ.
	 * ����: 0: ��ʱ��  1:�Ͽ�ʱ���ַ���   2: �¿�ʱ���ַ���  3: ��������(����0,����1)
	 */
	String[] getNextClassMessage(){
		SQLiteDatabase db = new DatabaseHelper(this).getReadableDatabase();
		Calendar now = Calendar.getInstance();
		String hour =""+ now.get(Calendar.HOUR_OF_DAY);
		String minute=""+ now.get(Calendar.MINUTE);
		String begin = ( Integer.parseInt(hour)<10 && hour.charAt(0)!='0' ? ""+0+hour : hour ) +":"
				+ ((Integer.parseInt(minute)<10 && minute.charAt(0)!='0')? ""+0+minute : minute) ;
		Cursor c = db.rawQuery("SELECT ID AS _id,CLASSNAME, BEGIN, END FROM TIME WHERE BEGIN>? ORDER BY BEGIN",  new String[]{begin});
		//����û�п�,����
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
