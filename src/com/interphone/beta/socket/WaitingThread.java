package com.interphone.beta.socket;

import java.io.IOException;
import java.net.Socket;

import android.util.Log;

import com.interphone.beta.Main;

public class WaitingThread extends Thread{

	private Socket soc;
	private Main main;
	private boolean keep;
	private String TAG;
	
	public WaitingThread(Socket soc, Main main) {
		// TODO Auto-generated constructor stub
		this.TAG = this.getClass().toString();
		this.soc = soc;
		this.main = main;
		this.keep = true;
	}

	public void run(){
		while(keep){
			
			try {
				Log.d(TAG,"Socket close ?");
				int c;
				if(soc.getInputStream().read()==0){
					Log.d(TAG,"Socket close !");
					keep=false;
					main.myFinishAppel();
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.d(TAG," WaitingThread fini !");
	}

	public void setKeep(boolean keep) {
		this.keep = keep;
	}
}
