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
			Log.v("屏幕亮", "屏幕亮");		
			kl.disableKeyguard(); // 开锁
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
			Log.v("屏幕灭", "屏幕灭");
			if(C_A_LockActivity.instance !=null) {
				C_A_LockActivity.instance.finish();
				C_A_LockActivity.instance=null;
			}
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
		//注册监听来电:来电自动开启锁屏:
		tpm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		tpm.listen( pl, PhoneStateListener.LISTEN_CALL_STATE);
		Log.v("上课琐机", "上课琐机");
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
		tpm.listen(pl, PhoneStateListener.LISTEN_NONE);
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("on", false))  {
			Log.v("锁屏服务自动开启了", "锁屏服务自动开启了");
			startService(new Intent(C_A_LockService.this, C_A_LockService.class));
		}
	}
	
	 PhoneStateListener pl = new PhoneStateListener(){
		   @Override  
	        public void onCallStateChanged(int state, String incomingNumber) {  
	            switch(state){  
	            case TelephonyManager.CALL_STATE_RINGING:  
	                Log.v("电话", "[Listener]等待接电话:"+incomingNumber);  
	                if(C_A_LockActivity.instance !=null){
	                	C_A_LockActivity.instance.finish();
	                }
	                break;  
	            case TelephonyManager.CALL_STATE_IDLE:  
	                Log.v("电话", "[Listener]电话挂断:"+incomingNumber);  
	                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);// 获取电源管理器对象
	                if(pm.isScreenOn()){
	                	Log.v("屏幕当前是:", "亮");
	                	Intent intent = new Intent(C_A_LockService.this, C_A_LockActivity.class);
	  	                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	  	                C_A_LockService.this.startActivity(intent);
	                }
  	                break; 	
	            case TelephonyManager.CALL_STATE_OFFHOOK:  
	                Log.v("电话", "[Listener]通话中:"+incomingNumber);  
	                break;  
	            }  
	            super.onCallStateChanged(state, incomingNumber);  
	        }  
	    };
}
