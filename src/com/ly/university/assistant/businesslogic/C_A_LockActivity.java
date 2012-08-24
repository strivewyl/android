package com.ly.university.assistant.businesslogic;

import java.util.Timer;
import java.util.TimerTask;
import com.ly.university.assistant.C_A_SettingActivity;
import com.ly.university.assistant.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class C_A_LockActivity extends Activity {

	public static C_A_LockActivity instance;
	Timer timer;
	Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.keyguard_layout);
		final EditText password = (EditText) findViewById(R.id.password);
		// 设置时间日期
		TimeWidget time = (TimeWidget) findViewById(R.id.time);
		TextView date = (TextView) findViewById(R.id.date);
		time.setDateTextVeiw(date);

		// 设置SIM卡运营商名字:
		TextView sim = (TextView) findViewById(R.id.sim);
		TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String operator = telManager.getSimOperator();
		if (operator != null) {
			if (operator.equals("46000") || operator.equals("46002")
					|| operator.equals("46007")) {
				// 中国移动
				sim.setText("中国移动");
			} else if (operator.equals("46001")) {
				// 中国联通
				sim.setText("中国联通");
			} else if (operator.equals("46003")) {
				// 中国电信
				sim.setText("中国电信");
			}
		}


		final TextView click_unlock = (TextView) findViewById(R.id.click_unlock);
		final TextView line1 = (TextView) findViewById(R.id.line1);
		final TextView line2 = (TextView) findViewById(R.id.line2);
	    handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					click_unlock.setText("点         ");
					line1.setTextColor(Color.GRAY);
					line2.setTextColor(Color.BLUE);
					break;
				case 1:
					click_unlock.setText("   击      ");
					line1.setTextColor(Color.DKGRAY);
					line2.setTextColor(Color.LTGRAY);
					break;
				case 2:
					click_unlock.setText("      开   ");
					line1.setTextColor(Color.YELLOW);
					line2.setTextColor(Color.RED);
					break;
				case 3:
					click_unlock.setText("         锁 ");
					line1.setTextColor(Color.WHITE);
					line2.setTextColor(Color.CYAN);
					break;

				}
			}
		};

		ImageButton btn = (ImageButton) findViewById(R.id.btn);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (password
						.getText()
						.toString()
						.equals(PreferenceManager.getDefaultSharedPreferences(
								C_A_LockActivity.this).getString(
								C_A_SettingActivity.CLASS_LOCK_PASSWORD,
								"password")))
					C_A_LockActivity.this.finish();
				else {
					Log.v("密码有误", "密码有误");
					Toast.makeText(C_A_LockActivity.this, "输入密码有误,请重新输入",
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		instance=this;
		if(timer ==null)  timer = new Timer();
		timer.schedule(new TimerTask() {
			int i = 0;

			@Override
			public void run() {
				handler.sendEmptyMessage(i);
				Log.v("锁屏动画", "锁屏"+i);
				i++;
				i %= 4;
			}
		}
   , 0, 1000);
	}

	@Override
	public void onPause() {
		super.onPause();
		timer.cancel();
		timer = null;
	}

	 @Override
	 public void onAttachedToWindow()
	 {
				Log.v("Handler", "设置窗口属性 ");
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						C_A_LockActivity.this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
					}
				}, 200);

	 //getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD); //屏蔽Home建
	 //getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
	 //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
	 super.onAttachedToWindow();
	 }
	
	// 屏蔽掉Back键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			Log.v("back建按了啊", "back建按了啊");
			return true;
		} else {
			Log.v("其它建按了啊", "其它建按了啊");
			return super.onKeyDown(keyCode, event);
		}
	}
}
