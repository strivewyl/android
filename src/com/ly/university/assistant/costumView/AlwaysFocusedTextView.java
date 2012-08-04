package com.ly.university.assistant.costumView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("NewApi")
public class AlwaysFocusedTextView extends TextView {

	public AlwaysFocusedTextView(Context context) {
		super(context);
	}
	public AlwaysFocusedTextView(Context context, AttributeSet attrs){
		super(context,attrs);
	}
	public AlwaysFocusedTextView(Context context, AttributeSet attrs,int i){
		super(context,attrs,i);
	}
	
	@Override
	public boolean isFocused(){
		return true;
	}
}
