package com.ly.university.assistant;

import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;
/**
 * 
 * @author ������
 * �༭ҳ��
 *Ϊ�˷����û��༭���Զ�������ĻΪ����
 *ͨ��Fragment��̬�༭ʱ�䡢��Ŀ���γ̰��ű�
 */
@ContentView(R.layout.edit_main)
public class C_A_EditActivity extends RoboSherlockFragmentActivity{
	
	public static final String TAG="ʱ���༭ҳ��";
	@InjectResource(R.string.toast_landscape) String toast;
	@Inject FragmentManager fm;
	@Inject ClassHour_editFragment hour;
	@Inject Schedule_editFragment schedule;
	ActionBar ab ;
	
	@Override
	public void onCreate(Bundle savedstateBundle){
		super.onCreate(savedstateBundle);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		Toast.makeText(this, toast , Toast.LENGTH_LONG).show();
		

		//fm.beginTransaction().add(R.id.edit_main_root, hour).commit();
		// ���ý���ActionBar
		ab=getSupportActionBar();
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
						switch (itemPosition) {
						case 0:
							Log.v(TAG, "ʱ���༭");
							// ��Ӵ��룺
							fm.beginTransaction().replace(R.id.edit_main_root, hour ).commit();
							break;
						case 1:
							Log.v(TAG, "�γ̰��ű༭");
							// ��Ӵ��룺
							fm.beginTransaction().replace(R.id.edit_main_root, schedule).commit();
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
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==android.R.id.home){
			 Intent parentActivityIntent = new Intent(this, C_A_MainActivity.class);
	            parentActivityIntent.addFlags(
	                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
	                    Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(parentActivityIntent);
	            finish();
		}
		else {
			Log.v("help","C_A_Edit_help");
		}
		return true;
	}
}
