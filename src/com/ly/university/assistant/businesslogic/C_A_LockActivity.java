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
		// ����ʱ������
		TimeWidget time = (TimeWidget) findViewById(R.id.time);
		TextView date = (TextView) findViewById(R.id.date);
		time.setDateTextVeiw(date);

		// ����SIM����Ӫ������:
		TextView sim = (TextView) findViewById(R.id.sim);
		TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String operator = telManager.getSimOperator();
		if (operator != null) {
			if (operator.equals("46000") || operator.equals("46002")
					|| operator.equals("46007")) {
				// �й��ƶ�
				sim.setText("�й��ƶ�");
			} else if (operator.equals("46001")) {
				// �й���ͨ
				sim.setText("�й���ͨ");
			} else if (operator.equals("46003")) {
				// �й�����
				sim.setText("�й�����");
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
					click_unlock.setText("��         ");
					line1.setTextColor(Color.GRAY);
					line2.setTextColor(Color.BLUE);
					break;
				case 1:
					click_unlock.setText("   ��      ");
					line1.setTextColor(Color.DKGRAY);
					line2.setTextColor(Color.LTGRAY);
					break;
				case 2:
					click_unlock.setText("      ��   ");
					line1.setTextColor(Color.YELLOW);
					line2.setTextColor(Color.RED);
					break;
				case 3:
					click_unlock.setText("         �� ");
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
					Log.v("��������", "��������");
					Toast.makeText(C_A_LockActivity.this, "������������,����������",
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
				Log.v("��������", "����"+i);
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
				Log.v("Handler", "���ô������� ");
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						C_A_LockActivity.this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
					}
				}, 200);

	 //getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD); //����Home��
	 //getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
	 //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
	 super.onAttachedToWindow();
	 }
	
	// ���ε�Back��
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			Log.v("back�����˰�", "back�����˰�");
			return true;
		} else {
			Log.v("���������˰�", "���������˰�");
			return super.onKeyDown(keyCode, event);
		}
	}
}
