package com.ly.university.assistant.businesslogic;

import java.util.Calendar;

import com.ly.university.assistant.C_A_SettingActivity;
import com.ly.university.assistant.util.DatabaseHelper;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.text.format.DateFormat;
import android.util.Log;

/**
 * 2012��8��18��15:51:08 �����Ͽ�״̬
 * 
 * @author ������
 * 
 */
public class C_A_ClassModeBroadcastReceiver extends BroadcastReceiver {
	

	@Override
	public void onReceive(Context context, Intent intent) {
		AudioManager audio = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		ContentResolver cr = context.getContentResolver();

		if (!intent.getBooleanExtra("classend", false)) {// �Ͽ�,
															// ���浱ǰ״̬,�����Ͽ�״̬,�����¿λָ���ʱ.
			// ��ѯ�Ƿ��п�,����п�,�������������Ͽ�״̬,���޿�,��������ʱ����.
			SQLiteDatabase db = new DatabaseHelper(context)
					.getReadableDatabase();
			Cursor c = db.rawQuery(
					"SELECT ID AS _id, SUBJECT, ADDRESS FROM SCHEDULE"
							+ "  WHERE CLASSNAME=? AND DAY=?",
					new String[] { intent.getStringExtra("classname"),
							"" + intent.getIntExtra("week_day", 1) });

			if (c.getCount() > 0) {// �п� ���浱ǰ״̬,�����Ͽ�״̬,�����¿λָ���ʱ.
				Log.v("�Ͽ���", "�Ͽ���");
				Intent classendIntent = new Intent(context,
						C_A_ClassModeBroadcastReceiver.class);
				classendIntent.putExtra("classend", true);

				if (PreferenceManager.getDefaultSharedPreferences(context)
						.getBoolean(C_A_SettingActivity.CLASS_MODE_SETTING,
								true)) {
					// �Ͽ��龰ģʽ��
					classendIntent.putExtra(
							C_A_SettingActivity.CLASS_MODE_SETTING, true);

					// ��Ӵ������
					switch (Integer
							.parseInt(PreferenceManager
									.getDefaultSharedPreferences(context)
									.getString(
											C_A_SettingActivity.CLASS_MODE_CHOOSE,
											"0"))) {
					case 0:
						Log.v("�Ͽν�������", "�Ͽν�������");
						classendIntent.putExtra("mode", 0);
						// ���ݹ�ȥ ���� ֪ͨ ���� ϵͳ ���� �ָ�ʱʹ��
						classendIntent
								.putExtra(
										"AudioManager.STREAM_ALARM",
										audio.getStreamVolume(AudioManager.STREAM_ALARM));
						classendIntent
								.putExtra(
										"AudioManager.STREAM_NOTIFICATION",
										audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
						classendIntent
								.putExtra(
										"AudioManager.STREAM_RING",
										audio.getStreamVolume(AudioManager.STREAM_RING));
						classendIntent
								.putExtra(
										"AudioManager.STREAM_SYSTEM",
										audio.getStreamVolume(AudioManager.STREAM_SYSTEM));
						// ��������Ϊ1
						audio.setStreamVolume(AudioManager.STREAM_ALARM, 1, 0);
						audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION,
								1, 0);
						audio.setStreamVolume(AudioManager.STREAM_RING, 1, 0);
						audio.setStreamVolume(AudioManager.STREAM_SYSTEM, 1, 0);

						break;
					case 1:
						classendIntent.putExtra("mode", 1);
						classendIntent.putExtra("RingMode",
								audio.getRingerMode());
						audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

						break;
					case 2:
						classendIntent.putExtra("mode", 2);
						classendIntent.putExtra("RingMode",
								audio.getRingerMode());
						audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
						break;
					case 3: // �ɺ�
						classendIntent.putExtra("mode", 3);
						int AIRPLANE_MODE = -1;
						try {
							AIRPLANE_MODE = System.getInt(cr,
									System.AIRPLANE_MODE_ON);
							if (AIRPLANE_MODE != 1) {
								System.putInt(cr, System.AIRPLANE_MODE_ON, 1);
								context.sendBroadcast(new Intent(
										Intent.ACTION_AIRPLANE_MODE_CHANGED));
							}
						} catch (SettingNotFoundException e) {
							e.printStackTrace();
							Log.e("��ȡϵͳ�ɺ�ģʽ�쳣", "��ȡϵͳ�ɺ�ģʽ�쳣");
						}
						classendIntent.putExtra("AIRPLANE_MODE", AIRPLANE_MODE);
						break;
					}
				}

				if (PreferenceManager.getDefaultSharedPreferences(context)
						.getBoolean(C_A_SettingActivity.CLASS_LOCK_SETTING,
								false)) {
					// �Ͽ�������
					classendIntent.putExtra(C_A_SettingActivity.CLASS_LOCK_SETTING,
							true);
					// ��Ӵ������,��������.
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(context);
					Editor editor = sp.edit();
					editor.putBoolean("on", true);
					editor.commit();
					context.startService(new Intent(context,
							C_A_LockService.class));
				}

				Calendar classend = Calendar.getInstance();
				String end = intent.getStringExtra("end");
				classend.set(Calendar.HOUR_OF_DAY,
						Integer.parseInt(end.split(":")[0]));
				classend.set(Calendar.MINUTE,
						Integer.parseInt(end.split(":")[1]));
				classend.set(Calendar.SECOND, 0);
				((AlarmManager) context
						.getSystemService(Activity.ALARM_SERVICE)).set(
						AlarmManager.RTC_WAKEUP, classend.getTimeInMillis(),
						PendingIntent.getBroadcast(context, 0, classendIntent,
								PendingIntent.FLAG_CANCEL_CURRENT));
				Log.v("�趨�¿λָ�",
						"ʱ��Ϊ:"
								+ DateFormat.format("MMM dd, yyyy h:mmaa",
										classend.getTimeInMillis()));
			} else {// �޿�,�����½ڿζ�ʱ:
				context.startService(new Intent(context,
						C_A_TimingService.class));
				Log.v("�޿�,�½ڿζ�ʱ", "�޿�,��ʱ����");
			}
			c.close();
			db.close();
		} else {
			 Log.v("�¿���", "�¿���");
			// �¿�,�ָ�״̬. �ж��Ͽ�ǰ�Ƿ������������Ͽ��龰ģʽ��������(��Intent�л�ȡ��Ϣ). ,�����½ڿζ�ʱ
			// ��Ӵ���,�ָ�״̬:

			if (intent.getBooleanExtra(C_A_SettingActivity.CLASS_MODE_SETTING,
					false)) {
				Log.v("�ָ�", "�ָ��龰ģʽ");
				// �ָ��Ͽ�ǰ�龰ģʽ
				switch (intent.getIntExtra("mode", 0)) {
				case 0:
					audio.setStreamVolume(AudioManager.STREAM_ALARM,
							intent.getIntExtra("AudioManager.STREAM_ALARM", 5),
							0);
					audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION,
							intent.getIntExtra(
									"AudioManager.STREAM_NOTIFICATION", 5), 0);
					audio.setStreamVolume(AudioManager.STREAM_RING,
							intent.getIntExtra("AudioManager.STREAM_RING", 5),
							0);
					audio.setStreamVolume(
							AudioManager.STREAM_SYSTEM,
							intent.getIntExtra("AudioManager.STREAM_SYSTEM", 5),
							0);
					break;
				case 1:
					audio.setRingerMode(intent.getIntExtra("RingMode", 2));
					break;
				case 2:
					audio.setRingerMode(intent.getIntExtra("RingMode", 2));
					break;
				case 3:
					try {
						if (intent.getIntExtra("AIRPLANE_MODE", -1) == 0
								&& System.getInt(cr, System.AIRPLANE_MODE_ON) == 1) {
							System.putInt(cr, System.AIRPLANE_MODE_ON, 0);
							context.sendBroadcast(new Intent(
									Intent.ACTION_AIRPLANE_MODE_CHANGED));
						}
					} catch (Exception e) {
						e.printStackTrace();
						Log.v("�ɺ��Ļָ��쳣", "�ɺ��Ļָ��쳣");
					}
				}

			}
			if (intent.getBooleanExtra(C_A_SettingActivity.CLASS_LOCK_SETTING,
					false)) {
				// ����
				Log.v("����", "����");
				SharedPreferences sp = PreferenceManager
						.getDefaultSharedPreferences(context);
				Editor editor = sp.edit();
				editor.putBoolean("on", false);
				editor.commit();
				if(C_A_LockActivity.instance !=null){
					C_A_LockActivity.instance.finish();
				}
				Log.v("�Ƿ�ر�����������",""+context.stopService(new Intent(context, C_A_LockService.class)));
			}
			context.startService(new Intent(context, C_A_TimingService.class));
			Log.v("�¿�", "�¿ζ�ʱ����");
		}

	}
	
	
}
