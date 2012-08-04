package com.ly.university.assistant;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
/**
 * 
 * @author 王亚磊
 * 时间 2012年8月4日9:06:02
 * 用于编辑课表
 *
 */
public class Schedule_editFragment extends Fragment{

	public static final String TAG="课程安排编辑页面";
	@Override
	public View onCreateView(LayoutInflater lf , ViewGroup vg,  Bundle savedstate){
		View root = lf.inflate(R.layout.schedule_edit, vg,false);
		ViewPager vp = (ViewPager) root.findViewById(R.id.pager);
		vp.setAdapter(new Schedule_editFragment_Adapter(getActivity().getSupportFragmentManager()));
		return root;
	}

	static class Schedule_editFragment_Adapter extends FragmentPagerAdapter{

		public Schedule_editFragment_Adapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int week_day) {
			return new Schedule_item_Fragment(week_day);	
		}	
		@Override
		public int getCount() {

			return 7;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return "星期：" + (position + 1);
		}

	}
	
	static class Schedule_item_Fragment extends Fragment implements OnClickListener{
		
		int week_day;
		
		public Schedule_item_Fragment(int week_day) {
			super();
			this.week_day=week_day;
		}
		@Override
		public View onCreateView(LayoutInflater lf , ViewGroup vg,  Bundle savedstate){
			View root = lf.inflate(R.layout.schedule_edit_main, vg,false);
			//添加按钮，从数据库中取数据填充，注意注册按钮事件。
			
				
			return root;
		}

		//参考时间表输入操作。处理事件。
		@Override
		public void onClick(View v) {
			
		}
	}
	
}
