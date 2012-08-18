package com.ly.university.assistant;

import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.ly.university.assistant.C_A_ClassHour_editFragment.ScrollListener;
import com.ly.university.assistant.businesslogic.C_A_TimingService;

/**
 * 
 * �༭ҳ�� Ϊ�˷����û��༭���Զ�������ĻΪ���� ͨ��Fragment��̬�༭ʱ�䡢��Ŀ���γ̰��ű�
 * 
 * @author ������
 **/
@ContentView(R.layout.edit_main_classhour)
public class C_A_ClassHourEditActivity extends RoboSherlockFragmentActivity
		implements ScrollListener {

	public static final String TAG = "ʱ���༭ҳ��";
	@InjectResource(R.string.toast_landscape)
	String toast;
	@InjectView(R.id.scroll)
	ScrollView sv;

	// @InjectView(R.id.classhour) FragmentManager fm;

	@Override
	public void onCreate(Bundle savedstateBundle) {
		super.onCreate(savedstateBundle);
		// if (this.getResources().getConfiguration().orientation
		// ==Configuration.ORIENTATION_PORTRAIT){
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
		// }
		// fm.beginTransaction().add(R.id.edit_main_root, hour).commit();
		// ���ý���ActionBar
		ActionBar ab = getSupportActionBar();
		ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayShowTitleEnabled(false);
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		ab.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.ad_action_bar_gradient_bak));
		// set up list nav
		ab.setListNavigationCallbacks(ArrayAdapter.createFromResource(this,
				R.array.ca_edit_list_items,
				R.layout.sherlock_spinner_dropdown_item),
				new OnNavigationListener() {
					public boolean onNavigationItemSelected(int itemPosition,
							long itemId) {
						// FragmentTransaction ft = fm.beginTransaction();
						switch (itemPosition) {
						case 0:
							Log.v(TAG, "ʱ���༭");
							// // ��Ӵ��룺�ж��������û�б���������ʾ�û��Ƿ񱣴���뿪

							// ft.replace(R.id.edit_main_root, hour );
							// ft.setTransition(FragmentTransaction.TRANSIT_EXIT_MASK);
							// ft.addToBackStack(null);
							// ft.commit();

							break;
						case 1:
							Log.v(TAG, "�γ̰��ű༭");
							// ��Ӵ��룺�ж��������û�б���������ʾ�û��Ƿ񱣴���뿪

							// ft.replace(R.id.edit_main_root, schedule );
							// ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
							// ft.commit();
							Intent intent = new Intent(
									C_A_ClassHourEditActivity.this,
									C_A_ScheduleEditActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							break;
						}
						return true;
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.edit_main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		startService(new Intent(this, C_A_TimingService.class));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			Intent parentActivityIntent = new Intent(this,
					C_A_MainActivity.class);
			parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(parentActivityIntent);
			finish();
		} else {
			Log.v("help", "C_A_Edit_help");
		}
		return true;
	}

	@Override
	public void scroll(int y) {
		// TODO Auto-generated method stub
		sv.scrollTo(0, y);
	}
}
