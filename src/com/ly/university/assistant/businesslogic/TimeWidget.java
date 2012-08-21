package com.ly.university.assistant.businesslogic;

import java.util.Calendar;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class TimeWidget extends TextView {

	Calendar mCalendar;
	TextView date ;
	private final static String m12 = "h:mm";
	private final static String m24 = "k:mm";
	private FormatChangeObserver mFormatChangeObserver;

	private Runnable mTicker;
	private Handler mHandler;

	private boolean mTickerStopped = false;
	String mFormat;

	private static String[] weekdays = { "星期日", "星期一", "星期二", "星期三", "星期四",
			"星期五", "星期六" };

	public TimeWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initClock(context);
	}

	public TimeWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		initClock(context);
	}

	public TimeWidget(Context context) {
		super(context);
		initClock(context);
	}

	private void initClock(Context context) {
		if (mCalendar == null) {
			mCalendar = Calendar.getInstance();
		}

		mFormatChangeObserver = new FormatChangeObserver();
		// 注册，监听系统日期设置数据库的改变
		getContext().getContentResolver().registerContentObserver(
				Settings.System.CONTENT_URI, true, mFormatChangeObserver);

		setFormat();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mTickerStopped = true;
	}

	/**
	 * Pulls / mode from system settings
	 */
	private boolean get24HourMode() {
		return android.text.format.DateFormat.is24HourFormat(getContext());
	}

	private void setFormat() {
		if (get24HourMode()) {
			mFormat = m12;
		} else {
			mFormat = m24;
		}
	}

	// format the string of time
	private static String format(int t) {
		String s = "" + t;
		if (s.length() == 1) {
			s = "0" + s;
		}
		return s;
	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		Log.i("时间点击了", "TimeWidget.onTouchEvent");
//		getContext().startActivity(
//				new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
//		return super.onTouchEvent(event);
//	}

	@Override
	protected void onAttachedToWindow() {
		Log.i("", "TimeWidget.onAttachedToWindow");
		mTickerStopped = false;
		super.onAttachedToWindow();
		mHandler = new Handler();

		/**
		 * requests a tick on the next hard-second boundary
		 */
		mTicker = new Runnable() {
			public void run() {
				if (mTickerStopped)
					return;
				mCalendar.setTimeInMillis(System.currentTimeMillis());

				int myear = (mCalendar.get(Calendar.YEAR));
				int mmonth = (mCalendar.get(Calendar.MONTH) + 1);// 月份+是一年中的第几个月
				int mmonthday = (mCalendar.get(Calendar.DAY_OF_MONTH));// 一月中的日期
				final int mweekday = (mCalendar.get(Calendar.DAY_OF_WEEK)) - 1;

				final String mDate = format(myear) + "-" + format(mmonth) + "-"
						+ format(mmonthday);
				setText(DateFormat.format(mFormat, mCalendar) );
				if(date !=null) date.setText(mDate+ " " + weekdays[mweekday]);
				invalidate();
				long now = SystemClock.uptimeMillis();
				long next = now + (60000 - now % 60000);
				mHandler.postAtTime(mTicker, next);
			}
		};
		mTicker.run();
	}

	private class FormatChangeObserver extends ContentObserver {
		public FormatChangeObserver() {
			super(new Handler());
		}
		@Override
		public void onChange(boolean selfChange) {
			setFormat();
		}
	}
	public void setDateTextVeiw(TextView date){
		this.date = date;
	}
}