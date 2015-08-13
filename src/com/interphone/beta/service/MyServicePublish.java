package com.interphone.beta.service;

import com.interphone.beta.socket.MyPublish;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyServicePublish extends Service {

	private String TAG;
	private static MyPublish pub;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int onStartCommand(Intent intent, int flags, int startId){
		this.TAG = this.getClass().toString();
		Log.d(TAG,"Service publish start!");
		if(pub!=null && pub.isAlive()){
			Log.d(TAG, "Publish deja existant");
			pub.publish();
		}else{
			Log.d(TAG, "New Publish");
			pub = new MyPublish(this, intent);
			pub.start();
		}
		
		Log.d(TAG,"Service publish stop!");
		return START_STICKY;
		
	}
}
