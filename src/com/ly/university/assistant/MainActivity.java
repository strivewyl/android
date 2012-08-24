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
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

/**
 * 
 * @author 王亚磊  /n
 *   显示主界面，提供导航。 第一次进入时候，自动运行功能导航
 * 2012年8月1日10:12:35
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
	ImageButton  ibtn2;
	@InjectView(R.id.ibtn3)
	ImageButton  ibtn3;
	@InjectView(R.id.ibtn4)
	ImageButton  ibtn4;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//测试使用
//		DatabaseHelper dbh = new DatabaseHelper(this); 
//		SQLiteDatabase db=dbh.getWritableDatabase();
//		db.close();
		

		// 设置监听器
		ibtn1.setOnClickListener(this);
		ibtn2.setOnClickListener(this);
		ibtn3.setOnClickListener(this);
		ibtn4.setOnClickListener(this);
		
		// 让TextView获得焦点，开始滚动
		tv.setFocusable(true);
		tv.setFocusableInTouchMode(true);

		// 设置界面ActionBar
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
							Log.v(TAG, "主界面");
							// 添加代码：

							break;
						case 1:
							Log.v(TAG, "上课助手");
							// 添加代码：
							startActivity(new Intent(MainActivity.this,
									C_A_MainActivity.class));
							randomAnim();
							break;
						case 2:
							Log.v(TAG, "励志");
							// 添加代码：

							break;
						case 3:
							Log.v(TAG, "健康助手");
							// 添加代码：

							break;
						case 4:
							Log.v(TAG, "天气预报");
							// 添加代码：

							break;
						}
						return true;
					}
				});


		// 动画实现,四个button
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
		
	}// onCreate()结束

	@Override
	public void onClick(View view) {
		final View v = view;
		ScaleAnimation anim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		anim.setDuration(1000);
		((ViewGroup)v.getParent()).startAnimation(anim);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.ibtn1:
					Log.v(TAG, "转向上课助手");
					startActivity(new Intent(MainActivity.this, C_A_MainActivity.class));
					randomAnim();
					break;
				case R.id.ibtn2:
					Log.v(TAG, "转向励志");
					
					break;
				case R.id.ibtn3:
					Log.v(TAG, "转向健康助手");
				
					break;
				case R.id.ibtn4:
					Log.v(TAG, "转向冷暖预知");
					break;
				}
			}
		}, 750);
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
		SharedPreferences sp = getPreferences(Activity. MODE_PRIVATE);
		if (! sp.getBoolean(IS_FIRSTIME_OPEN, false)) {
			Log.v(TAG, "第一次打开程序。");
			// 添加代码,  转向功能介绍：
			startActivity(new Intent(this, FunctionGuideActivity.class));
			randomAnim();
			//设置为非第一次打开程序
			SharedPreferences.Editor editer = sp.edit();
			editer.putBoolean(IS_FIRSTIME_OPEN, true);
			editer.commit();
		}
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
			Log.v(TAG, "功能向导");
			// 添加代码
			startActivity(new Intent(this, FunctionGuideActivity.class));
			randomAnim();
			break;

		case R.id.contact_us:
			Log.v(TAG, "联系我们");
			// 添加代码
			startActivity(new Intent(MainActivity.this, ContactActivity.class));
			randomAnim();
			break;
		}
		return super.onOptionsItemSelected(item);
	}	
	@Override
	public void onStart(){
		super.onStart();
		ab.setSelectedNavigationItem(0);
		// 如果是首次运行程序，打开向导。
	}
	
	@Override
	public void finish(){
		super.finish();
		randomAnim();
	}
	
	void randomAnim(){
		Random random = new Random();
		int i = random.nextInt(12);
		Log.v("动画师", ""+i);
		switch (i) {
		case 0:
			overridePendingTransition(R.anim.fade, R.anim.hold);
			break;
		case 1:
			overridePendingTransition(R.anim.my_scale_action,
					R.anim.my_alpha_action);
			break;
		case 2:
			overridePendingTransition(R.anim.scale_rotate,
					R.anim.my_alpha_action);
			break;
		case 3:
			overridePendingTransition(R.anim.scale_translate_rotate,
					R.anim.my_alpha_action);
			break;
		case 4:
			overridePendingTransition(R.anim.scale_translate,
					R.anim.my_alpha_action);
			break;
		case 5:
			overridePendingTransition(R.anim.hyperspace_in,
					R.anim.hyperspace_out);
			break;
		case 6:
			overridePendingTransition(R.anim.push_left_in,
					R.anim.push_left_out);
			break;
		case 7:
			overridePendingTransition(R.anim.push_up_in,
					R.anim.push_up_out);
			break;
		case 8:
			overridePendingTransition(R.anim.slide_left,
					R.anim.slide_right);
			break;
		case 9:
			overridePendingTransition(R.anim.wave_scale,
					R.anim.my_alpha_action);
			break;
		case 10:
			overridePendingTransition(R.anim.zoom_enter,
					R.anim.zoom_exit);
			break;
		case 11:
			overridePendingTransition(R.anim.slide_up_in,
					R.anim.slide_down_out);
			break;
		}
	}
}
