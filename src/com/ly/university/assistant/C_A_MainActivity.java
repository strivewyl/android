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

		// ����ActionBar
		ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayUseLogoEnabled(true);
		ab.setDisplayShowTitleEnabled(true);
		ab.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.ad_action_bar_gradient_bak));

		// ����ViewPager.
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
			Log.v(TAG, "�༭");
			// ��Ӵ���
			startActivity(new Intent(this, C_A_EditActivity.class));
			break;

		case R.id.ca_help:
			Log.v(TAG, "����");
			// ��Ӵ���

			break;
		case R.id.ca_setting:
			Log.v(TAG, "����");
			// ��Ӵ���
			startActivity(new Intent(this,C_A_SettingActivity.class));
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * @author ������
	 *  ʱ�䣺2012��7��31��10:22:04 ÿһ��ҳ����ʾһ��ClassTableFragment
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
			return "���ڣ�" + (position + 1);
		}

	}

	/**
	 * 
	 * @author ������ /n
	 * 2012��7��31��10:22:09 ��ʾ�α�
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
			Log.v(TAG, "����� " + position + " ��");
			// ��Ӵ������
		}
	}

	public static class MySimpleCursorAdapter extends SimpleCursorAdapter {

		
		public MySimpleCursorAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to, int flags) {
			super(context, layout, c, from, to, flags);
		}

		public MySimpleCursorAdapter(Context c, int day) {
			// ʵ�ִ����ݿ�ȡ������������ day������
			super(c, R.layout.classtable_item, null, null, null, 0);
		}
	}
}