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
 * @author ������
 * ʱ�� 2012��8��4��9:06:02
 * ���ڱ༭�α�
 *
 */
public class Schedule_editFragment extends Fragment{

	public static final String TAG="�γ̰��ű༭ҳ��";
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
			return "���ڣ�" + (position + 1);
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
			//��Ӱ�ť�������ݿ���ȡ������䣬ע��ע�ᰴť�¼���
			
				
			return root;
		}

		//�ο�ʱ�����������������¼���
		@Override
		public void onClick(View v) {
			
		}
	}
	
}
