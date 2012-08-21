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
			Log.v("��Ļ��", "��Ļ��");		
			kl.disableKeyguard(); // ����
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
			Log.v("��Ļ��", "��Ļ��");
			if(C_A_LockActivity.instance !=null) C_A_LockActivity.instance.finish();
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v("����", "������������");
		kl = ((KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE))
				.newKeyguardLock("C_A_LockService");
		// ע�������㲥������Ļ���Ͱ�. �˹㲥ֻ�ܶ�̬ע��.���ұ���ע���ע��������Գ���.�������ڷ���������������Ч.
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
		// ����㲥
		Log.v("��������ر���", "��������ر�");
		unregisterReceiver(sreenOff);
		unregisterReceiver(sreenOn);
		kl.reenableKeyguard();
		// ������������
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("on", false))  {
			Log.v("���������Զ�������", "���������Զ�������");
			startService(new Intent(C_A_LockService.this, C_A_LockService.class));
		}
	}

}
