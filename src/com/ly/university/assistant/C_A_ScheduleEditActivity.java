package com.ly.university.assistant;

import java.util.ArrayList;
import java.util.Random;

import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.ly.university.assistant.businesslogic.C_A_TimingService;
import com.ly.university.assistant.util.DatabaseHelper;
/**
 * 
 *   编辑页面
 *为了方便用户编辑，自动调节屏幕为横屏
 *通过Fragment动态编辑时间、科目、课程安排表
 * @author 王亚磊
 **/
@ContentView(R.layout.edit_main_schedule)
public class C_A_ScheduleEditActivity extends RoboSherlockFragmentActivity{
	public static interface ClearListener{
		void clear();
	}
	ArrayList< ClearListener> clearListeners ;
	public void setOnClearListener(ClearListener c){
		clearListeners.add(c);
	}
	public static final String TAG="课程安排表编辑页面";
	ActionBar ab;
	@InjectResource(R.string.toast_landscape)String toast;
	@Override
	public void onCreate(Bundle savedstateBundle){
		super.onCreate(savedstateBundle);

		clearListeners= new ArrayList<C_A_ScheduleEditActivity.ClearListener>();
//		if (this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_PORTRAIT){
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//			Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
//		}
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
//						FragmentTransaction ft = fm.beginTransaction();
						switch (itemPosition) {
						case 0:
							Log.v(TAG, "时间表编辑");
//							// 添加代码：判断如果存在没有保存的项，则提示用户是否保存后离开
							
//							ft.replace(R.id.edit_main_root, hour );
//							ft.setTransition(FragmentTransaction.TRANSIT_EXIT_MASK);
//							ft.addToBackStack(null);
//							ft.commit();
							Intent intent = new Intent(C_A_ScheduleEditActivity.this,C_A_ClassHourEditActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							randomAnim();
							break;
						}
						return true;
					}
				});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.edit_schedule_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent parentActivityIntent = new Intent(this, C_A_MainActivity.class);
            parentActivityIntent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(parentActivityIntent);
            finish();
			break;
		case R.id.clear:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("清空课程安排表?")
			       .setCancelable(false)
			       .setTitle("严重警告")
			       .setPositiveButton("确定", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   for(ClearListener c : clearListeners){
			        		   c.clear();
			        	   }
		            	SQLiteDatabase db = new DatabaseHelper(C_A_ScheduleEditActivity.this).getWritableDatabase();
		            	db.delete("SCHEDULE", null, null);
			        	db.close(); 
			           }
			       })
			       .setNegativeButton("取消", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.setOwnerActivity(this);
			alert.show();
			break;
		case R.id.edit_main_help:
			startActivity(new Intent(this, C_A_ScheduleHelpActivity.class));
		    randomAnim();
			
			break;
		}
		return true;
	}
	@Override
	public void onStart(){
		super.onStart();
		ab.setSelectedNavigationItem(1);
	}
	@Override
	public void onBackPressed(){
		 Intent parentActivityIntent = new Intent(this, C_A_MainActivity.class);
         parentActivityIntent.addFlags(
                 Intent.FLAG_ACTIVITY_CLEAR_TOP |
                 Intent.FLAG_ACTIVITY_NEW_TASK);
         startActivity(parentActivityIntent);
         finish();
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		startService(new Intent(this, C_A_TimingService.class));
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
