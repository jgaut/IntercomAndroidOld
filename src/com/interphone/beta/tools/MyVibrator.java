package com.interphone.beta.tools;

import android.os.Vibrator;
import android.util.Log;

public class MyVibrator extends Thread {

	private int tvib, tsil;
	private boolean keep;
	private Vibrator vb;
	private String TAG;

	public MyVibrator(Vibrator vb, int silence, int vibration) {
		// TODO Auto-generated constructor stub
		this.vb = vb;
		this.tsil = silence;
		this.tvib = vibration;
		keep=true;
		this.TAG = this.getClass().toString();
	}

	public void run() {
		int cpt=0;
		while(keep && cpt<5){
			this.vb.vibrate(this.tvib);
			try {
				this.sleep(this.tsil);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(TAG, "vibrate !!");
			cpt++;
		}

	}

	public boolean isKeep() {
		return keep;
	}

	public void setKeep(boolean keep) {
		this.keep = keep;
	}

	public Vibrator getVb() {
		return vb;
	}

	public void setVb(Vibrator vb) {
		this.vb = vb;
	}

}
