package com.ly.university.assistant;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

@SuppressLint("NewApi")
public class TimePickerTextView extends TextView {

	Activity activity;
	public TimePickerTextView(Context context) {
		super(context);
		setOnClickListener();
	}
	public TimePickerTextView(Context context, AttributeSet attrs){
		super(context,attrs);
		setOnClickListener();
	}
	public TimePickerTextView(Context context, AttributeSet attrs,int i){
		super(context,attrs,i);
		setOnClickListener();
	}

	public TimePickerTextView setActivity(Activity activity){
		this.activity=activity;
		return this;
	}
	
	void setOnClickListener(){
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.v("", "µã»÷ÁËTimePickerTextView");
				String time = TimePickerTextView.this.getText().toString();
				int hour = 0, minute = 0;
				if(time!=null && !time.equals("")){
					try {
					  hour=Integer.parseInt( time.split(":")[0]);
					  minute=Integer.parseInt( time.split(":")[1]);
					} catch (Exception e) {
						Calendar c = Calendar.getInstance();
						hour = c.get(Calendar.HOUR_OF_DAY);
						minute = c.get(Calendar.MINUTE);
					}
				}
				TimePickerDialog timePickerDialog = new TimePickerDialog(activity, new OnTimeSetListener(){
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						TimePickerTextView.this.setText((hourOfDay<10 ? 0+""+hourOfDay : hourOfDay)+":"+(minute<10 ? 0+""+minute : minute));
					}
					
				},hour, minute, true);
				
				timePickerDialog.setOwnerActivity(activity);
				timePickerDialog.show();
			}
		});
	}
}
