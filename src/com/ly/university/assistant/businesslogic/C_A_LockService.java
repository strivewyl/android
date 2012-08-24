package com.ly.university.assistant.businesslogic;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class C_A_LockService extends Service {
	Boolean b = true;
	KeyguardLock kl;
	public Boolean isClassON = false;
	TelephonyManager tpm;
	private BroadcastReceiver sreenOn = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.v("��Ļ��", "��Ļ��");		
			kl.disableKeyguard(); // ����
			Intent mainstart = new Intent(context, C_A_LockActivity.class);
			mainstart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			if(C_A_LockActivity.instance == null && C_A_LockService.this.tpm.getCallState() !=TelephonyManager.CALL_STATE_RINGING){
			     context.startActivity(mainstart);
			}
		}
	};

	private BroadcastReceiver sreenOff = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.v("��Ļ��", "��Ļ��");
			if(C_A_LockActivity.instance !=null) {
				C_A_LockActivity.instance.finish();
				C_A_LockActivity.instance=null;
			}
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
		//ע���������:�����Զ���������:
		tpm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		tpm.listen( pl, PhoneStateListener.LISTEN_CALL_STATE);
		Log.v("�Ͽ�����", "�Ͽ�����");
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
		tpm.listen(pl, PhoneStateListener.LISTEN_NONE);
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("on", false))  {
			Log.v("���������Զ�������", "���������Զ�������");
			startService(new Intent(C_A_LockService.this, C_A_LockService.class));
		}
	}
	
	 PhoneStateListener pl = new PhoneStateListener(){
		   @Override  
	        public void onCallStateChanged(int state, String incomingNumber) {  
	            switch(state){  
	            case TelephonyManager.CALL_STATE_RINGING:  
	                Log.v("�绰", "[Listener]�ȴ��ӵ绰:"+incomingNumber);  
	                if(C_A_LockActivity.instance !=null){
	                	C_A_LockActivity.instance.finish();
	                }
	                break;  
	            case TelephonyManager.CALL_STATE_IDLE:  
	                Log.v("�绰", "[Listener]�绰�Ҷ�:"+incomingNumber);  
	                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);// ��ȡ��Դ����������
	                if(pm.isScreenOn()){
	                	Log.v("��Ļ��ǰ��:", "��");
	                	Intent intent = new Intent(C_A_LockService.this, C_A_LockActivity.class);
	  	                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	  	                C_A_LockService.this.startActivity(intent);
	                }
  	                break; 	
	            case TelephonyManager.CALL_STATE_OFFHOOK:  
	                Log.v("�绰", "[Listener]ͨ����:"+incomingNumber);  
	                break;  
	            }  
	            super.onCallStateChanged(state, incomingNumber);  
	        }  
	    };
}
