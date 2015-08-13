package com.interphone.beta.broadcaster;

import com.interphone.beta.service.MyServicePublish;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBroadcasterPublish extends BroadcastReceiver{

	private String TAG;

	public MyBroadcasterPublish() {
		// TODO Auto-generated constructor stub
		this.TAG = this.getClass().toString();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG,"Broadcaster publish init!");
		Intent it = new Intent(context, MyServicePublish.class);
		it.setAction(intent.getAction());
		context.startService(it);		
		Log.d(TAG,"Broadcaster publish fin !");

	}

}
