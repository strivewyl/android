package com.ly.university.assistant.businesslogic;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class C_A_LockService extends Service {
	Boolean b = true;
	KeyguardLock kl;
	public Boolean isClassON = false;

	private BroadcastReceiver sreenOn = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.v("屏幕亮", "屏幕亮");		
			kl.disableKeyguard(); // 开锁
			Intent mainstart = new Intent(context, C_A_LockActivity.class);
			mainstart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			if(C_A_LockActivity.instance == null){
			     context.startActivity(mainstart);
			}
		}
	};

	private BroadcastReceiver sreenOff = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.v("屏幕灭", "屏幕灭");
			if(C_A_LockActivity.instance !=null) C_A_LockActivity.instance.finish();
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v("琐机", "琐机服务启动");
		kl = ((KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE))
				.newKeyguardLock("C_A_LockService");
		// 注册两个广播接收屏幕亮和暗. 此广播只能动态注册.而且必须注册和注销监听相对出现.必须是在服务生命周期中有效.
		registerReceiver(sreenOn, new IntentFilter(
				"android.intent.action.SCREEN_ON"));
		registerReceiver(sreenOff, new IntentFilter(
				"android.intent.action.SCREEN_OFF"));

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 解除广播
		Log.v("锁屏服务关闭了", "锁屏服务关闭");
		unregisterReceiver(sreenOff);
		unregisterReceiver(sreenOn);
		kl.reenableKeyguard();
		// 重新启动自身
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("on", false))  {
			Log.v("锁屏服务自动开启了", "锁屏服务自动开启了");
			startService(new Intent(C_A_LockService.this, C_A_LockService.class));
		}
	}

}
