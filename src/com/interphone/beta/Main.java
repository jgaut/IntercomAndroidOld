package com.interphone.beta;

import java.net.Socket;
import org.freedesktop.gstreamer.GStreamer;
import com.interphone.beta.socket.Communicator;
import com.interphone.beta.socket.MySocket;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class Main extends Activity{

	private String TAG = null;
	private boolean isPlaySend;
	private boolean isPlayReceive;
	private Button go;
	private Button close; 
	private Button door;
	private Button up;
	private Button down;
	private Button p50;
	private Button ts;
	private Communicator com;
	private static Socket soc;
	private TextView txtVol;
	private int volume;
	private Ringtone r;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.TAG = this.getClass().toString();
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		

		setContentView(R.layout.main);

		up = (Button) this.findViewById(R.id.ButtonUp);
		down = (Button) this.findViewById(R.id.buttonDown);
		p50 = (Button) this.findViewById(R.id.button50);
		close = (Button) this.findViewById(R.id.buttonclose);
		door = (Button) this.findViewById(R.id.buttonopendoor);
		go = (Button) this.findViewById(R.id.buttongo);
		ts = (Button) this.findViewById(R.id.ButtonTS);
		txtVol = (TextView) this.findViewById(R.id.textVolume);
		
		

		if(this.getIntent()!=null && this.getIntent().getExtras()!=null && this.getIntent().getExtras().getBoolean("appel") && MySocket.getSocketClient()!=null
				 && !MySocket.getSocketClient().isClosed()){
			/*initr Gstreamer*/
			try {
				System.loadLibrary("gstreamer_android");
				GStreamer.init(this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace(); 
			}
			
			/*Init des boutons*/
			close.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Log.d(TAG,"close");
					r.stop();
					com.closeComm();
				}
			});
			
			go.setOnTouchListener(new View.OnTouchListener() {
				
				@SuppressLint("NewApi")
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if(event.getAction()==MotionEvent.ACTION_DOWN){
						Resources res = getResources();
						GradientDrawable sh = (GradientDrawable) res.getDrawable(R.drawable.shapegodown);
						go.setBackground(sh);
					}
					if(event.getAction()==MotionEvent.ACTION_UP){
						Log.d(TAG,"go");
						Resources res = getResources();
						GradientDrawable sh = (GradientDrawable) res.getDrawable(R.drawable.shapegoup);
						go.setBackground(sh);
						
						/*Démarage du communicator*/
						com.start();
						
						/*Gestion IHM*/
						go.setVisibility(View.INVISIBLE);
						door.setVisibility(View.VISIBLE);
						up.setVisibility(View.VISIBLE);
						down.setVisibility(View.VISIBLE);
						p50.setVisibility(View.VISIBLE);
						ts.setVisibility(View.VISIBLE);
						txtVol.setVisibility(View.VISIBLE);
						close.setText(R.string.txt_stop2);
						volume=50;
						txtVol.setText(txtVol.getText()+" "+volume+"%");
						r.stop();
					}
					return false;
				}
			});
			
			door.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Log.d(TAG,"open door");
					door.setEnabled(false);
					com.openDoor();
					door.setEnabled(true);
				}
			});
			
			up.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Log.d(TAG,"up volume");
					up.setEnabled(false);
					com.up();
					up.setEnabled(true);
					volume=Math.min(volume+5, 100);
					txtVol.setText(txtVol.getText().toString().split(" ")[0]+" "+volume+"%");
				}
			});
			
			down.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Log.d(TAG,"down volume");
					down.setEnabled(false);
					com.down();
					down.setEnabled(true);
					volume=Math.max(volume-5, 0);
					txtVol.setText(txtVol.getText().toString().split(" ")[0]+" "+volume+"%");
				}
			});
			
			p50.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Log.d(TAG,"down volume");
					down.setEnabled(false);
					com.p50();
					down.setEnabled(true);
					volume=50;
					txtVol.setText(txtVol.getText().toString().split(" ")[0]+" "+volume+"%");
				}
			});
			
			
			ts.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Log.d(TAG,"Test Sound");
					down.setEnabled(false);
					com.ts();
					down.setEnabled(true);
				}
			});
			
			

			/*Reveil de phone meme en ecran verrouillé*/
			this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
		            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
		            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
		            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
			Log.d(TAG,"Appel !!");
			soc = MySocket.getSocketClient();
			
			/*Sonnerie*/
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			r = RingtoneManager.getRingtone(getApplicationContext(), notification);
			r.play();
			
			Log.d(TAG,"Lancement du communicator");
			com = new Communicator(soc, this);
			/*Faire un thread qui ecoute le thread pour savoir s'il a été coupé avant la réponse du user*/
			//Log.d(TAG, "Lancement du Waiting Thread");
			//this.wt = new WaitingThread(soc, this);
			//this.wt.start();
			
			initAppel();
			
		}else{
			initBasic();
			}

	}

	public void myFinishAppel(){
		Log.d(TAG,"myFinish");
		this.finish();
		this.onDestroy();
	}

	public void initAppel(){
		go.setVisibility(View.VISIBLE);
		door.setVisibility(View.INVISIBLE);
		close.setVisibility(View.VISIBLE);
		up.setVisibility(View.INVISIBLE);
		down.setVisibility(View.INVISIBLE);
		p50.setVisibility(View.INVISIBLE);
		ts.setVisibility(View.INVISIBLE);
		txtVol.setVisibility(View.INVISIBLE);
	}

	public void initBasic(){
		go.setVisibility(View.VISIBLE);
		close.setVisibility(View.VISIBLE);
		door.setVisibility(View.VISIBLE);
		up.setVisibility(View.VISIBLE);
		down.setVisibility(View.VISIBLE);
		p50.setVisibility(View.VISIBLE);
		ts.setVisibility(View.VISIBLE);
		txtVol.setVisibility(View.VISIBLE);
	}

	public boolean isPlayReceive() {
		return isPlayReceive;
	}

	public void setPlayReceive(boolean isPlayReceive) {
		this.isPlayReceive = isPlayReceive;
	}

	public boolean isPlaySend() {
		return isPlaySend;
	}

	public void setPlaySend(boolean isPlaySend) {
		this.isPlaySend = isPlaySend;
	}
	public Communicator getCom() {
		return com;
	}

	public void setCom(Communicator com) {
		this.com = com;
	}

	public Button getGo() {
		return go;
	}

	public void setGo(Button go) {
		this.go = go;
	}

	public Button getClose() {
		return close;
	}

	public void setClose(Button close) {
		this.close = close;
	}

	public Button getDoor() {
		return door;
	}

	public void setDoor(Button door) {
		this.door = door;
	}

	public void onDestroy() {
        super.onDestroy();
        //android.os.Process.killProcess(android.os.Process.myPid());
    }  

}
