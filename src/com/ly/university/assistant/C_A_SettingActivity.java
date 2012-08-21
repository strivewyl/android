package com.ly.university.assistant;

import com.ly.university.assistant.businesslogic.C_A_TimingService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.util.Log;
import android.widget.Toast;

public class C_A_SettingActivity extends PreferenceActivity implements OnPreferenceChangeListener{
	/**
	 * 上课情景模式开关
	 */
	public static final String CLASS_MODE_SETTING="class_mode_setting";
	/**
	 * 上课模式选项代号  0 音量减小  1震动  2 静音 3飞航
	 */
	public static final String CLASS_MODE_CHOOSE="mode_choose";
	/**
	 * 上课琐机开关  
	 */
	public static final String CLASS_LOCK_SETTING="class_lock_setting";
	/**
	 * 开锁密码
	 */
	public static final String CLASS_LOCK_PASSWORD="lock_password";
	/**
	 * 上课提醒开关
	 */
	public static final String CLASS_CLOCK_SETTING="class_clock_setting";
	/**
	 * 提前分钟数	
	 */
	public static final String CLASS_CLOCK_PRE_TIME="pre_time";
	/**
	 * 闹铃选择
	 */
	public static final String CLASS_CLOCK_RingTone="alarm_ringtone";
	/**
	 * 闹铃方式  0:震动  1 闹铃   2 震动+铃声   3 震动后铃声  4 通知
	 */
	public static final String CLASS_CLOCK_ALARM_MODE="alarm_mode";
	
	
	CheckBoxPreference class_mode_setting;
	CheckBoxPreference class_lock_setting;
	CheckBoxPreference class_clock_setting;
	ListPreference alarm_mode;
	ListPreference class_mode;
	EditTextPreference password;
	EditTextPreference pretime;
	RingtonePreference alarm;
	
	public C_A_SettingActivity(){
		super();
	}
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
        addPreferencesFromResource(R.xml.ca_setting); 
        
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        
        class_mode_setting= (CheckBoxPreference) findPreference(CLASS_MODE_SETTING);
        class_mode_setting.setOnPreferenceChangeListener(this);
        class_mode_setting.setChecked(sp.getBoolean(CLASS_MODE_SETTING, true));
        
        class_clock_setting= (CheckBoxPreference) findPreference(CLASS_CLOCK_SETTING);
        class_clock_setting.setOnPreferenceChangeListener(this);
        class_clock_setting.setChecked(sp.getBoolean(CLASS_CLOCK_SETTING, true));
        
        class_lock_setting= (CheckBoxPreference) findPreference(CLASS_LOCK_SETTING);
        class_lock_setting.setChecked(sp.getBoolean(CLASS_LOCK_SETTING, false));
        class_clock_setting.setOnPreferenceChangeListener(this);
        
        class_mode =(ListPreference) findPreference(CLASS_MODE_CHOOSE);
        class_mode.setDefaultValue(sp.getString(CLASS_MODE_CHOOSE, "0"));
        
        password =(EditTextPreference) findPreference(CLASS_LOCK_PASSWORD);
        password.setDefaultValue(sp.getString(CLASS_LOCK_PASSWORD, "password"));
        
        pretime =(EditTextPreference) findPreference(CLASS_CLOCK_PRE_TIME);
        pretime.setOnPreferenceChangeListener(this);
        pretime.setDefaultValue(sp.getString(CLASS_CLOCK_PRE_TIME, "10"));
        
        alarm = (RingtonePreference) findPreference(CLASS_CLOCK_RingTone);
        alarm.setDefaultValue(sp.getString(CLASS_CLOCK_RingTone,""));
        
        alarm_mode = (ListPreference) findPreference(CLASS_CLOCK_ALARM_MODE);
        alarm_mode .setDefaultValue(sp.getString(CLASS_CLOCK_ALARM_MODE, "0"));
        
    }
    @Override  
    public boolean onPreferenceChange(Preference preference, Object newValue) {  
    	Log.v("setting" ,"更改了"+preference.getKey());
    	//添加代码, 发送一个广播,广播设置更改了,需要重新设置PendingIntent.
    	if(preference==pretime){
    		Log.v("pretime","检查格式");
    		try{
    			int pre= Integer.parseInt((String)newValue);
    			if(pre<1||pre>60) throw new Exception();
    		}catch(Exception e){
    			Toast.makeText(this, "输入分钟数有误,请输入一个1--60的数字",Toast.LENGTH_SHORT).show();
    			return false;
    		}
    	}
    	startService(new Intent(this, C_A_TimingService.class));
    	return true;
    }
}
