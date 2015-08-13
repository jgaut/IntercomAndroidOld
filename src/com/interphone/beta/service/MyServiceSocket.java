package com.interphone.beta.service;

import com.interphone.beta.socket.MySocket;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public final class MyServiceSocket extends Service {

	private static MySocket soc;
	private String TAG;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int onStartCommand(Intent intent, int flags, int startId){
		this.TAG = this.getClass().toString();
		Log.d(TAG,"Service socket start!");
		if(soc!=null && soc.isAlive()){
			Log.d(TAG,"Socket serveur deja ouvert sur le port "+MySocket.getSocketServer().getLocalPort());
		}else{
			Log.d(TAG,"Ouverture du socket serveur");
			soc = new MySocket(this.getApplicationContext(), 25000);
			soc.start();
		}

		Log.d(TAG,"Service socket stop!");
		return START_STICKY;
		
	}
}
