package com.ly.university.assistant.businesslogic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartupBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("StartupBroadcastReceiver","¿ª»úÆô¶¯");
		context.startService(new Intent(context, C_A_TimingService.class) );
	}

}
