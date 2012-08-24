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
 * 2012年8月18日15:51:08 设置上课状态
 * 
 * @author 王亚磊
 * 
 */
public class C_A_ClassModeBroadcastReceiver extends BroadcastReceiver {
	

	@Override
	public void onReceive(Context context, Intent intent) {
		AudioManager audio = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		ContentResolver cr = context.getContentResolver();

		if (!intent.getBooleanExtra("classend", false)) {// 上课,
															// 保存当前状态,设置上课状态,启动下课恢复定时.
			// 查询是否有课,如果有课,根据设置设置上课状态,若无课,则启动定时服务.
			SQLiteDatabase db = new DatabaseHelper(context)
					.getReadableDatabase();
			Cursor c = db.rawQuery(
					"SELECT ID AS _id, SUBJECT, ADDRESS FROM SCHEDULE"
							+ "  WHERE CLASSNAME=? AND DAY=?",
					new String[] { intent.getStringExtra("classname"),
							"" + intent.getIntExtra("week_day", 1) });

			if (c.getCount() > 0) {// 有课 保存当前状态,设置上课状态,启动下课恢复定时.
				Log.v("上课啦", "上课啦");
				Intent classendIntent = new Intent(context,
						C_A_ClassModeBroadcastReceiver.class);
				classendIntent.putExtra("classend", true);

				if (PreferenceManager.getDefaultSharedPreferences(context)
						.getBoolean(C_A_SettingActivity.CLASS_MODE_SETTING,
								true)) {
					// 上课情景模式开
					classendIntent.putExtra(
							C_A_SettingActivity.CLASS_MODE_SETTING, true);

					// 添加处理代码
					switch (Integer
							.parseInt(PreferenceManager
									.getDefaultSharedPreferences(context)
									.getString(
											C_A_SettingActivity.CLASS_MODE_CHOOSE,
											"0"))) {
					case 0:
						Log.v("上课降低音量", "上课降低音量");
						classendIntent.putExtra("mode", 0);
						// 传递过去 闹铃 通知 响铃 系统 音量 恢复时使用
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
						// 设置音量为1
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
					case 3: // 飞航
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
							Log.e("获取系统飞航模式异常", "获取系统飞航模式异常");
						}
						classendIntent.putExtra("AIRPLANE_MODE", AIRPLANE_MODE);
						break;
					}
				}

				if (PreferenceManager.getDefaultSharedPreferences(context)
						.getBoolean(C_A_SettingActivity.CLASS_LOCK_SETTING,
								false)) {
					// 上课琐机开
					classendIntent.putExtra(C_A_SettingActivity.CLASS_LOCK_SETTING,
							true);
					// 添加处理代码,开启服务.
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
				Log.v("设定下课恢复",
						"时间为:"
								+ DateFormat.format("MMM dd, yyyy h:mmaa",
										classend.getTimeInMillis()));
			} else {// 无课,启动下节课定时:
				context.startService(new Intent(context,
						C_A_TimingService.class));
				Log.v("无课,下节课定时", "无课,定时启动");
			}
			c.close();
			db.close();
		} else {
			 Log.v("下课了", "下课了");
			// 下课,恢复状态. 判断上课前是否启动了设置上课情景模式或者琐机(从Intent中获取信息). ,启动下节课定时
			// 添加代码,恢复状态:

			if (intent.getBooleanExtra(C_A_SettingActivity.CLASS_MODE_SETTING,
					false)) {
				Log.v("恢复", "恢复情景模式");
				// 恢复上课前情景模式
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
						Log.v("飞航的恢复异常", "飞航的恢复异常");
					}
				}

			}
			if (intent.getBooleanExtra(C_A_SettingActivity.CLASS_LOCK_SETTING,
					false)) {
				// 开锁
				Log.v("开锁", "开锁");
				SharedPreferences sp = PreferenceManager
						.getDefaultSharedPreferences(context);
				Editor editor = sp.edit();
				editor.putBoolean("on", false);
				editor.commit();
				if(C_A_LockActivity.instance !=null){
					C_A_LockActivity.instance.finish();
				}
				Log.v("是否关闭了锁屏服务",""+context.stopService(new Intent(context, C_A_LockService.class)));
			}
			context.startService(new Intent(context, C_A_TimingService.class));
			Log.v("下课", "下课定时启动");
		}

	}
	
	
}
