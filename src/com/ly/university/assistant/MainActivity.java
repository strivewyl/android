package com.ly.university.assistant;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

/**
 * 
 * @author ������  /n
 *   ��ʾ�����棬�ṩ������ ��һ�ν���ʱ���Զ����й��ܵ���
 * 2012��8��1��10:12:35
 */
@ContentView(R.layout.activity_main) 
public class MainActivity extends RoboSherlockActivity implements
		OnClickListener {

	public final static String TAG = "MainActivity_message";
	public final static String IS_FIRSTIME_OPEN = "firstTime";
	private ActionBar ab;
	private Timer timer;
	private Handler handler;

	@InjectView(R.id.tv)
	AlwaysFocusedTextView tv;
	@InjectView(R.id.ibtn1)
	ImageButton ibtn1;
	@InjectView(R.id.ibtn2)
	ImageButton ibtn2;
	@InjectView(R.id.ibtn3)
	ImageButton ibtn3;
	@InjectView(R.id.ibtn4)
	ImageButton ibtn4;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//����ʹ��
//		DatabaseHelper dbh = new DatabaseHelper(this); 
//		SQLiteDatabase db=dbh.getWritableDatabase();
//		db.close();
		

		// ���ü�����
		ibtn1.setOnClickListener(this);
		ibtn2.setOnClickListener(this);
		ibtn3.setOnClickListener(this);
		ibtn4.setOnClickListener(this);

		// ��TextView��ý��㣬��ʼ����
		tv.setFocusable(true);
		tv.setFocusableInTouchMode(true);

		// ���ý���ActionBar
		ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(false);
		ab.setDisplayUseLogoEnabled(true);
		ab.setDisplayShowTitleEnabled(false);
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		ab.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.ad_action_bar_gradient_bak));
		// set up list nav
		ab.setListNavigationCallbacks(ArrayAdapter.createFromResource(this,
				R.array.main_list_items,
				R.layout.sherlock_spinner_dropdown_item),
				new OnNavigationListener() {
					public boolean onNavigationItemSelected(int itemPosition,
							long itemId) {
						switch (itemPosition) {
						case 0:
							Log.v(TAG, "������");
							// ��Ӵ��룺

							break;
						case 1:
							Log.v(TAG, "�Ͽ�����");
							// ��Ӵ��룺
							startActivity(new Intent(MainActivity.this,
									C_A_MainActivity.class));
							break;
						case 2:
							Log.v(TAG, "��־");
							// ��Ӵ��룺

							break;
						case 3:
							Log.v(TAG, "��������");
							// ��Ӵ��룺

							break;
						case 4:
							Log.v(TAG, "����Ԥ��");
							// ��Ӵ��룺

							break;
						}
						return true;
					}
				});

		// ������״����г��������򵼡�
		SharedPreferences sp = getPreferences(Activity. MODE_PRIVATE);
		if (sp == null || sp.getBoolean(IS_FIRSTIME_OPEN, false)) {
			Log.v(TAG, "��һ�δ򿪳���");
			// ת���ܽ��ܣ�
			
			//����Ϊ�ǵ�һ�δ򿪳���
			SharedPreferences.Editor editer = sp.edit();
			editer.putBoolean(IS_FIRSTIME_OPEN, true);
			editer.commit();
		}
		// ����ʵ��,�ĸ�button
		final Animation anim = AnimationUtils.loadAnimation(this,
				R.anim.mainactivity);
		 handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					ibtn1.startAnimation(anim);
					break;
				case 2:
					ibtn2.startAnimation(anim);
					break;
				case 3:
					ibtn3.startAnimation(anim);
					break;
				case 4:
					ibtn4.startAnimation(anim);
					break;
				}
				super.handleMessage(msg);
			}
		};
		
	}// onCreate()����

	@Override
	public void onClick(View v) {
		//
		switch (v.getId()) {
		case R.id.ibtn1:
			Log.v(TAG, "ת���Ͽ�����");
			startActivity(new Intent(MainActivity.this, C_A_MainActivity.class));
			break;
		case R.id.ibtn2:
			Log.v(TAG, "ת����־");

			break;
		case R.id.ibtn3:
			Log.v(TAG, "ת�򽡿�����");

			break;
		case R.id.ibtn4:
			Log.v(TAG, "ת����ůԤ֪");

			break;
		}
	}	
	@Override
	public void onResume() {
		super.onResume();
		timer=new Timer();
		timer.schedule(new TimerTask() {		
			@Override
			public void run() {
				Message msg = new Message();
				Random r = new Random();
				msg.what=r.nextInt(4)+1;
				handler.sendMessage(msg);
			}
		}, 100,3000);
	}

	@Override
	public void onPause() {
		super.onPause();
		timer.cancel();
		timer=null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.function_guide:
			Log.v(TAG, "������");
			// ��Ӵ���

			break;

		case R.id.contact_us:
			Log.v(TAG, "��ϵ����");
			// ��Ӵ���

			break;
		}
		return super.onOptionsItemSelected(item);
	}	
	@Override
	public void onStart(){
		super.onStart();
		ab.setSelectedNavigationItem(0);
	}
}
