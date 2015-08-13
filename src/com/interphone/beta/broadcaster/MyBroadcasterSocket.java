package com.interphone.beta.broadcaster;

import com.interphone.beta.service.MyServiceSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBroadcasterSocket extends BroadcastReceiver{

	private String TAG;

	public MyBroadcasterSocket() {
		// TODO Auto-generated constructor stub
		this.TAG = this.getClass().toString();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG,"Broadcaster socket init!");
		Intent it = new Intent(context, MyServiceSocket.class);
		context.startService(it);
		Log.d(TAG,"Broadcaster socket fin !");
		
	}

}
