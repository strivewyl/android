package com.ly.university.assistant;

import java.util.Calendar;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;

@SuppressLint("ValidFragment")
@ContentView(R.layout.class_assistant)
public class C_A_MainActivity extends RoboSherlockFragmentActivity {
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
		int west_day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		vPager.setCurrentItem(west_day - 1 == 0 ? 7 : west_day - 2);

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
	            parentActivityIntent.addFlags(
	                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
	                    Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(parentActivityIntent);
	            finish();
	            return true;
		case R.id.ca_main_edit:
			Log.v(TAG, "编辑");
			// 添加代码
			startActivity(new Intent(this, C_A_EditActivity.class));
			break;

		case R.id.ca_help:
			Log.v(TAG, "帮助");
			// 添加代码

			break;
		case R.id.ca_setting:
			Log.v(TAG, "设置");
			// 添加代码
			startActivity(new Intent(this,C_A_SettingActivity.class));
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * @author 王亚磊
	 *  时间：2012年7月31日10:22:04 每一个页面显示一个ClassTableFragment
	 **/
	 static class MyPagerAdapter extends FragmentPagerAdapter {


		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg) {
			return  ClassTableFragment.newInstance(arg);
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

	/**
	 * 
	 * @author 王亚磊 /n
	 * 2012年7月31日10:22:09 显示课表
	 */
	public static class ClassTableFragment extends ListFragment {


		 public static ClassTableFragment newInstance(int arg) {
		        ClassTableFragment fragment = new ClassTableFragment();
		        Bundle args = new Bundle();
		        args.putInt("week_day", arg);
		        fragment.setArguments(args);
		        return fragment;
		    }


		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			int week_day = getArguments().getInt("week_day",1);
			MySimpleCursorAdapter adapter = new MySimpleCursorAdapter(getActivity(),
					week_day);
			setListAdapter(adapter);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			return inflater.inflate(R.layout.classtable_oneday, container,
					false);
		}

		@Override
		public void onListItemClick(ListView parent, View v, int position,
				long id) {
			Log.v(TAG, "点击了 " + position + " 行");
			// 添加处理代码
		}
	}

	public static class MySimpleCursorAdapter extends SimpleCursorAdapter {

		
		public MySimpleCursorAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to, int flags) {
			super(context, layout, c, from, to, flags);
		}

		public MySimpleCursorAdapter(Context c, int day) {
			// 实现从数据库取出来数据星期 day的数据
			super(c, R.layout.classtable_item, null, null, null, 0);
		}
	}
}