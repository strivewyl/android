package com.ly.university.assistant.businesslogic;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.ly.university.assistant.AlwaysFocusedTextView;
import com.ly.university.assistant.C_A_SettingActivity;
import com.ly.university.assistant.R;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
/**
 * ����,֪ͨ�������ʾҳ��.
 * ���Ϊ����ҳ��,����������������Ƶ�Ч��.
 * �����ÿ��һ������һ��0.5��.
 * ��������춯һ��������Ӧ��,����֪ͨ���Զ�����.
 * 
 * @author ������
 *
 */
public class C_A_AlarmActivity extends Activity {
	KeyguardManager km;
	KeyguardLock kl;
	PowerManager pm;
	PowerManager.WakeLock wl;
	 MediaPlayer mediaPlayer;
	 Vibrator vibrator;
	Timer  timer ;
	TimerTask task;
	Thread thread;
	Camera camera;
	Parameters parameters;
	Boolean led_off = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_layout);
		AlwaysFocusedTextView message = (AlwaysFocusedTextView) findViewById(R.id.alarm_message);
		TextView classname = (TextView) findViewById(R.id.classname);
		TextView subject = (TextView) findViewById(R.id.subject);
		TextView address = (TextView) findViewById(R.id.address);
		TextView begin = (TextView) findViewById(R.id.start_time);
		TextView end = (TextView) findViewById(R.id.end_time);
		Button btn = (Button) findViewById(R.id.positiveButton);
		
		final Intent intent = getIntent();// classname subject address begin end
		final CharSequence cname = intent.getStringExtra("classname");
		Calendar now = Calendar.getInstance();
	    String begintext = intent.getStringExtra("begin");
		int minutes = Integer.parseInt(begintext.split(":")[0])*60 + Integer.parseInt(begintext.split(":")[1])
				- now.get(Calendar.HOUR_OF_DAY)*60-now.get(Calendar.MINUTE);
		if(minutes > 0)   message.setText("������"+cname+"�������"+minutes+"����"+", �Ͽ�ȥ�Ͽΰ�!");
		else  message.setText("���Ѿ������"+cname+", �´ο�Ҫע����Ŷ!");
		classname.setText(cname);
		begin.setText(begintext);
		end.setText(intent.getStringExtra("end"));
		subject.setText(intent.getStringExtra("subject"));
		address.setText(intent.getStringExtra("address"));
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				C_A_AlarmActivity.this.finish();
			}
		});
		
		
		if(!intent.getBooleanExtra("notification", false)){//��������,��ʼ������
			km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE); // �õ�����������������
			kl = km.newKeyguardLock("�����Զ�����"); // ������LogCat���õ�Tag
			pm = (PowerManager) getSystemService(Context.POWER_SERVICE);// ��ȡ��Դ����������
			wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
					| PowerManager.SCREEN_DIM_WAKE_LOCK, "������Ļ");
			
			switch (Integer.parseInt(PreferenceManager
					.getDefaultSharedPreferences(this).getString(
							C_A_SettingActivity.CLASS_CLOCK_ALARM_MODE, "0"))) {
			case 0://��:
				vibrator =  (Vibrator) getSystemService(VIBRATOR_SERVICE); 
				break;
			case 1://����
				mediaPlayer = MediaPlayer.create(this
						, Uri.parse(PreferenceManager.getDefaultSharedPreferences(this).getString(C_A_SettingActivity.CLASS_CLOCK_RingTone, "")));
				mediaPlayer.setAudioStreamType(AudioManager.MODE_RINGTONE);
				break;
			default://������
				vibrator =  (Vibrator) getSystemService(VIBRATOR_SERVICE); 
				mediaPlayer = MediaPlayer.create(this
						, Uri.parse(PreferenceManager.getDefaultSharedPreferences(this).getString(C_A_SettingActivity.CLASS_CLOCK_RingTone, "")));
				break;
			}
			final Handler handler= new Handler(){//��һ��ʱ����Ӧ��,ֹͣ����, �Զ��˳�. ������Ӧ��֪ͨ.  
				@Override
				public void handleMessage(Message msg) {
					if(msg.what==0){//�����𶯺���������.
						if(mediaPlayer != null){
							try {
								mediaPlayer.start();
								mediaPlayer.setLooping(true);
							} catch (Exception e) {
								Log.v("mediaPlayer State Exceptiont", "���������쳣");
							}

						}
					}else {//����ʱ����
						Notification n = new Notification(R.drawable.notification_icon, "�Ͽ�������Ӧ��", System.currentTimeMillis());
						intent.putExtra("notification", true);
						n.setLatestEventInfo(C_A_AlarmActivity.this, "��Ӧ��֪ͨ", cname+" �Ͽ�����һ�ֺ���Ӧ��,�ش�֪ͨ"
								, PendingIntent.getActivity(C_A_AlarmActivity.this, 0,intent, Intent.FLAG_ACTIVITY_NEW_TASK));
						n.flags|= Notification.FLAG_AUTO_CANCEL;
						n.defaults=Notification.DEFAULT_ALL;
						((NotificationManager) C_A_AlarmActivity.this.getSystemService(Context.NOTIFICATION_SERVICE)).notify(0,n);
						C_A_AlarmActivity.this.finish();
					}
					
				}
				
			};	
			thread = new Thread(){
				@Override
				public void run() {
					try {
						int timeout_ms = 60000;
						if(PreferenceManager.getDefaultSharedPreferences(C_A_AlarmActivity.this).getString(C_A_SettingActivity.CLASS_CLOCK_ALARM_MODE, "0").equals("3")){
							sleep(10000);
							handler.sendEmptyMessage(0);//����.
							timeout_ms  = 50000;
						}else {
							handler.sendEmptyMessage(0);
						}
						sleep(timeout_ms);
						handler.sendEmptyMessage(1);
					} catch (InterruptedException e) {
						Log.v("�߳���ֹ", "�߳���ֹ");
					}
				}
			};
		}	
	}
	@Override
	public void onStart() {
		super.onStart();
		if(timer !=null){
			Log.v("�����","�����");
			timer.schedule(task, 0, 1000);
		}
		if(vibrator!=null){
			vibrator.vibrate(new long[]{500,1000},0);
		}
		if(kl !=null){
			kl.disableKeyguard(); // ����
			wl.acquire();
			thread.start();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if(kl !=null){
			thread.interrupt();
			kl.reenableKeyguard();
			wl.release();
		}
		if(vibrator != null){
			vibrator.cancel();
			vibrator = null;
		}
		if(mediaPlayer != null){
			if(mediaPlayer.isPlaying()) mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		if(timer !=null){
			timer.cancel();
			camera.release();
			camera = null;
		}


	}
}
