package com.ly.university.assistant;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;

public class C_A_SettingActivity extends PreferenceActivity implements OnPreferenceChangeListener,   
OnPreferenceClickListener{
    public static final String SHAREDPREFERENCE_FILE_NAME="us_preferences_file";
	public static final String CLASS_MODE_SETTING="class_mode_setting";//�Ͽ��龰ģʽ����
	public static final String CLASS_MODE_CHOOSE="mode_choose";//�Ͽ�ģʽѡ�����  0 ������С  1��  2 ���� 3�ɺ�
	public static final String CLASS_LOCK_SETTING="class_lock_setting";//�Ͽ���������  
	public static final String CLASS_LOCK_PASSWORD="lock_password";//��������
	public static final String CLASS_CLOCK_SETTING="class_clock_setting";//�Ͽ����ѿ���
	public static final String CLASS_CLOCK_PRE_TIME="pre_time";//��ǰ������	
	CheckBoxPreference class_mode_setting;
	CheckBoxPreference class_lock_setting;
	CheckBoxPreference class_clock_setting;
	ListPreference class_mode;
	EditTextPreference password;
	EditTextPreference pretime;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
        addPreferencesFromResource(R.xml.ca_setting); 
        
        @SuppressWarnings("static-access")
		SharedPreferences sp = getPreferenceManager().getDefaultSharedPreferences(this);
        
        class_mode_setting= (CheckBoxPreference) findPreference(CLASS_MODE_SETTING);
        class_mode_setting.setOnPreferenceChangeListener(this);
        class_mode_setting.setOnPreferenceClickListener(this);
        class_mode_setting.setChecked(sp.getBoolean(CLASS_MODE_SETTING, true));
        
        class_clock_setting= (CheckBoxPreference) findPreference(CLASS_CLOCK_SETTING);
        class_clock_setting.setOnPreferenceChangeListener(this);
        class_clock_setting.setOnPreferenceClickListener(this);
        class_clock_setting.setChecked(sp.getBoolean(CLASS_CLOCK_SETTING, true));
        
        class_lock_setting= (CheckBoxPreference) findPreference(CLASS_LOCK_SETTING);
        class_lock_setting.setOnPreferenceChangeListener(this);
        class_lock_setting.setOnPreferenceClickListener(this);
        class_lock_setting.setChecked(sp.getBoolean(CLASS_LOCK_SETTING, false));
        
        class_mode =(ListPreference) findPreference(CLASS_MODE_CHOOSE);
        class_mode.setOnPreferenceChangeListener(this);
        class_mode.setOnPreferenceClickListener(this);
        class_mode.setDefaultValue(sp.getString(CLASS_MODE_CHOOSE, "0"));
        
        password =(EditTextPreference) findPreference(CLASS_LOCK_PASSWORD);
        password.setOnPreferenceChangeListener(this);
        password.setOnPreferenceClickListener(this);
        password.setDefaultValue(sp.getString(CLASS_LOCK_PASSWORD, "password"));
        
        pretime =(EditTextPreference) findPreference(CLASS_CLOCK_PRE_TIME);
        pretime.setOnPreferenceChangeListener(this);
        pretime.setOnPreferenceClickListener(this);
        pretime.setDefaultValue(sp.getString(CLASS_CLOCK_PRE_TIME, "10"));
        
    }
    
    @Override  
    public boolean onPreferenceChange(Preference preference, Object newValue) {  
        // TODO Auto-generated method stub  
        //�ж����ĸ�Preference�ı���
    
    	Log.v("setting" ,"������"+preference.getKey());
        if(preference.getKey().equals(""))  
        {  
        	
        }  
        else  
        {  
            //�������false��ʾ�������ı�  
            return true;  
        }  
        //����true��ʾ����ı�  
        return true;  
    }  
    @Override  
    public boolean onPreferenceClick(Preference preference) {  
    	Log.v("setting" ,"�����"+preference.getKey());
      return true;
    }
}
