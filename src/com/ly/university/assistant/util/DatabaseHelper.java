package com.ly.university.assistant.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 
 * @author 王亚�?
 * 时间 2012�?�?�?3:24:02 
 * 数据库中两张�?时间�? 课程安排�?
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	static final String DATABASE_NAME = "us.db";
	static final int DATABASE_VERSION = 2;
	public static final String TABLE_NAME_SCHEDULE = "SCHEDULE";// 课表安排名字
	public static final String TABLE_NAME_TIME = "TIME";// 时间表名�?
	public static final String COLUMN_NAME_SUBJECT = "SUBJECT";// 科目
	public static final String COLUMN_NAME_ADDRESS = "ADDRESS";// 地址
	public static final String COLUMN_NAME_CLASSNAME = "CLASSNAME";// 课时�?
	public static final String COLUMN_NAME_BEGIN = "BEGIN";// �?��时间
	public static final String COLUMN_NAME_EDN = "END";// �?��时间
	public static final String COLUMN_NAME_WEEK_DAY = "DAY";
	public static final String ID ="ID";


	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// 数据库不存在，执行onCreate
	@Override
	public void onCreate(SQLiteDatabase db) {
		// 建立两张表�?
		
		String sqlstr1 = "CREATE TABLE "
				+ TABLE_NAME_SCHEDULE
				+ " ( ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, SUBJECT TEXT, ADDRESS TEXT, CLASSNAME TEXT NOT NULL ,DAY INTEGER NOT NULL)";
		String sqlstr2 = "CREATE TABLE "
				+ TABLE_NAME_TIME
				+ " (ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, CLASSNAME TEXT NOT NULL, BEGIN TEXT,END TEXT)";	
		db.execSQL(sqlstr1);
		db.execSQL(sqlstr2);

		Log.v("SQLite", "成功建立数据�?);
	
		ContentValues values = new ContentValues();
		//插入时间表，早读，上午第�?��，上午第二节，下午第�?��，下午第二节，晚自习�?
		values.put(COLUMN_NAME_CLASSNAME, "早读");
		values.put(COLUMN_NAME_BEGIN, "06:00");
		values.put(COLUMN_NAME_EDN, "07:00");
		db.insert(TABLE_NAME_TIME, null, values);
		values.clear();
		values.put(COLUMN_NAME_CLASSNAME, "上午第一�?);
		values.put(COLUMN_NAME_BEGIN, "08:00");
		values.put(COLUMN_NAME_EDN, "09:50");
		db.insert(TABLE_NAME_TIME, null, values);
		values.clear();
		values.put(COLUMN_NAME_CLASSNAME, "上午第二�?);
		values.put(COLUMN_NAME_BEGIN, "10:10");
		values.put(COLUMN_NAME_EDN, "12:00");
		db.insert(TABLE_NAME_TIME, null, values);
		values.clear();
		values.put(COLUMN_NAME_CLASSNAME, "下午第一�?);
		values.put(COLUMN_NAME_BEGIN, "14:00");
		values.put(COLUMN_NAME_EDN, "15:50");
		db.insert(TABLE_NAME_TIME, null, values);
		values.clear();
		values.put(COLUMN_NAME_CLASSNAME, "下午第二�?);
		values.put(COLUMN_NAME_BEGIN, "16:10");
		values.put(COLUMN_NAME_EDN, "18:00");
		db.insert(TABLE_NAME_TIME, null, values);
		values.clear();
		values.put(COLUMN_NAME_CLASSNAME, "晚自�?);
		values.put(COLUMN_NAME_BEGIN, "19:30");
		values.put(COLUMN_NAME_EDN, "21:00");
		db.insert(TABLE_NAME_TIME, null, values);
		values.clear();
		//插入�?��数据课表数据�?
		for(int i =1;i<=7;i++){
			values.put(COLUMN_NAME_SUBJECT, "英语");
			values.put(COLUMN_NAME_ADDRESS, "B201");
			values.put(COLUMN_NAME_CLASSNAME,"早读");
			values.put(COLUMN_NAME_WEEK_DAY,i);
			db.insert(TABLE_NAME_SCHEDULE, null, values);
			values.clear();
		}
		
		for(int i =1;i<=7;i++){
			values.put(COLUMN_NAME_SUBJECT, "C语言");
			values.put(COLUMN_NAME_ADDRESS, "B201");
			values.put(COLUMN_NAME_CLASSNAME,"晚自�?);
			values.put(COLUMN_NAME_WEEK_DAY,i);
			db.insert(TABLE_NAME_SCHEDULE, null, values);
			values.clear();
		}
		
		for(int i =1;i<=7;i++){
			values.put(COLUMN_NAME_SUBJECT, "语文");
			values.put(COLUMN_NAME_ADDRESS, "B201");
			values.put(COLUMN_NAME_CLASSNAME,"下午第一�?);
			values.put(COLUMN_NAME_WEEK_DAY,i);
			db.insert(TABLE_NAME_SCHEDULE, null, values);
			values.clear();
		}
		
		Log.v("Sqlite", "成功插入数据");

		// 插入�?��示例数据
	}

	// 数据库版本发生改变，执行
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.v("SQLite", "版本�? + oldVersion + "到了" + newVersion + "版�?");
	}
}