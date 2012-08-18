package com.ly.university.assistant;

import java.util.Calendar;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.ly.university.assistant.C_A_ClassTableFragment.onListItemClickListener;

@SuppressLint("ValidFragment")
@ContentView(R.layout.class_assistant)
public class C_A_MainActivity extends RoboSherlockFragmentActivity implements
		onListItemClickListener {

	private String currentTitle;
	private String currentSubject;
	private String currentAddress;
	private String currentStartTime;
	private String currentEndTime;
	private int currentPagerPosition;

	public final static String TAG = "C_A_MainActivity_message";
	ActionBar ab;
	@InjectView(R.id.pager)
	ViewPager vPager;
	@InjectView(R.id.pager_title_strip)
	PagerTitleStrip pts;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 设置ActionBar
		ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayUseLogoEnabled(true);
		ab.setDisplayShowTitleEnabled(true);
		ab.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.ad_action_bar_gradient_bak));

		// 设置ViewPager.
		vPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
		vPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				Log.v("Vpager","当前是"+arg0);
				currentPagerPosition=arg0;
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				Log.v("onPageScrolled","当前是"+arg0+","+arg1+","+arg2);
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				Log.v("onPageScrollStateChanged","当前是"+arg0);
			}
		});
		int west_day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		vPager.setCurrentItem(west_day - 1 == 0 ? 7 : west_day - 2);

	}

	@Override
	public Dialog onCreateDialog(int dialogId) {
		// CustomDialog.Builder dialogBuider = new
		// CustomDialog.Builder(this).setMessage(subject, address, start_time,
		// end_time);
		final int id = dialogId;
		Log.v("C_A_MainActivity", "点击了" + dialogId);
		C_A_CustomDialog.Builder dialogBuilder = new C_A_CustomDialog.Builder(this);
		dialogBuilder
				.setMessage(currentSubject, currentAddress, currentStartTime,
						currentEndTime).setTitle(currentTitle)
				.setPositiveButtonListener(new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dismissDialog(id);
					}
				});
		return dialogBuilder.create();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.ca_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Log.v(TAG, "UP");
			Intent parentActivityIntent = new Intent(this, MainActivity.class);
			parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(parentActivityIntent);
			finish();
			return true;
		case R.id.ca_main_edit:
			Log.v(TAG, "编辑");
			// 添加代码
			startActivity(new Intent(this, C_A_ClassHourEditActivity.class));
			break;

		case R.id.ca_help:
			Log.v(TAG, "帮助");
			// 添加代码

			break;
		case R.id.ca_setting:
			Log.v(TAG, "设置");
			// 添加代码
			startActivity(new Intent(this, C_A_SettingActivity.class));
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onListItemClick(int position, String title, String subject,
			String address, String start, String end) {
		Log.v("onClicke", "点击了"+position);
		this.currentTitle = title;
		this.currentAddress = address;
		this.currentSubject = subject;
		this.currentStartTime = start;
		this.currentEndTime = end;
		showDialog(currentPagerPosition*50+position);//为了区分每一天不同的Dialog，一般情况下，一天的课程没有超过50节的，为了方便起见，不再查询数据库中一天的节次数
	}

	/**
	 * 时间：2012年7月31日10:22:04 每一个页面显示一个ClassTableFragment
	 * 
	 * @author 王亚磊
	 **/
	static class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg) {

			return C_A_ClassTableFragment.newInstance(arg + 1);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 7;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "星期：" + (position + 1);
		}
	}
}