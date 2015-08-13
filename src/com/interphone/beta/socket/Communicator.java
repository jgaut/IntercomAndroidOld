package com.interphone.beta.socket
;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;

import com.interphone.beta.Main;
import com.interphone.beta.Constante;
import com.interphone.beta.stream.Stream;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class Communicator extends Thread{
	//Algo AA
	/*Debut
		Ouverture de la connexion
		Recuperation de l'IP et du port de la connexion
		Publication de l'IP et du port de la communication sur l'annuaire (site web)
		Attente de connexion (Mise en place IHM)
		Si connexion etablie alors
			Attente acceptation de la part de l'utilisateur (Mise en place IHM)
			Si l'utilisateur accepte
				Ouverture du flux audio de reception (thread)
				Envoi du port du flux audio de reception

				Attente de reception du port du flux audio d'emission
				Reception du port du flux audio d'emission
				Ouverture du flux audio d'emission (thread)

				Attente d'ordre de la part de l'utilisateur
				Tant que l'ordre recu n'est pas fin de communication
					Si ordre recu est "ouverture de porte" alors
						Procedure d'ouverture de porte
					Fin si
				Fin tant que
			Fin si
		Fin si
		Fermeture de la communication
	Fin*/
	/*private String ip;
	private int port;*/
	private Socket soc;
	private BufferedReader in;
	private PrintWriter out;
	private Main main;
	private boolean loop;
	private Stream mySendStream;
	private Stream myreceiveStream;
	private String TAG;

	public Communicator(Socket soc, final Main main) {
		this.TAG = this.getClass().toString();
		this.soc=soc;
		//this.ip = soc.getInetAddress().toString();
		//this.port = soc.getPort();
		this.main=main;
		this.main.setCom(this);
		
		Handler refresh = new Handler(Looper.getMainLooper());
		refresh.post(new Runnable() {
			public void run()
			{
				main.initAppel();	
			}
		});
		try {
			in= new BufferedReader(new InputStreamReader(soc.getInputStream()));
			out = new PrintWriter(soc.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public void run(){
		try {
			Log.d(TAG,"Envoie de GO : "+Constante.GO);
			out.println(Constante.GO);
			out.flush();
			//Ouverture du flux audio de reception (thread)
			Log.d(TAG,"Lancement du stream entrant");
			this.myreceiveStream = new Stream();
			this.myreceiveStream.initReceiveStream();
			this.myreceiveStream.play();
			//Envoi du port du flux audio de reception
			while(this.myreceiveStream.getPort()==0){
				
			}
			int portR =this.myreceiveStream.getPort();
			Log.d(TAG, "Port de reception : "+portR);
			
			String ssid = "XX";
			Log.d(TAG,"infos:"+MyPublish.IP+":"+portR+":"+ssid);
			out.println(MyPublish.IP+":"+portR+":"+ssid);
			out.flush();

			//Attente de reception du port du flux audio d'emission
			//Reception du port du flux audio d'emission
			//Ouverture du flux audio d'emission (thread)
			String mess=in.readLine();
			Log.d(TAG,mess+"");
			String[] rep = mess.split(":");
			if(rep!=null && rep.length==3){
				String myport = rep[1];
				String myip = rep[0];
				myip = myip.split("/")[1];
				Log.d(TAG,"Port recu : "+myport);
				Log.d(TAG,"Lancement du stream sortant : "+myip+":"+myport);
				this.mySendStream = new Stream();
				this.mySendStream.initSendStream(new String (myip.getBytes(), Charset.forName("UTF-8")), new String(myport.getBytes(), Charset.forName("UTF-8")));
				this.mySendStream.play();
				loop=true;
			}else{
				Log.d(TAG,"Erreur de protocol : "+mess);
				loop=false;
			}

			while(loop){
				int ordre=0;
				String m=null;
				try{
					Log.d(TAG,"Attente d'ordre...");
					m=in.readLine();
					Log.d(TAG,"mess = "+ m);
					if(m==null){
						ordre=Constante.CLOSE;
					}else{
						ordre = Integer.parseInt(m);
					}

				}catch(NumberFormatException e){	
					ordre=Constante.ERR;
				}


				Log.d(TAG,"Ordre = "+ ordre);
				
				switch(ordre){
				case Constante.CLOSE:
					Log.d(TAG,"Reception Close : "+Constante.CLOSE);
					loop=false;
					break;
				case Constante.HB:
					Log.d(TAG,"Reception hb : "+Constante.HB);
					break;
				default :
					Log.d(TAG,"Ordre non compréhensible");
					break;
				}
			}

			deco();
			Log.d(TAG,"out of the switch");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d(TAG,"Deco brutale1");
			deco();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG,"Deco brutale2");
			e.printStackTrace();
			deco();

		}

	}

	public void deco(){
		Log.d(TAG,"Cloture du communicator");
			try {
				if(in!=null){
					in.close();
					Log.d(TAG,"in.close()");
				}
				if(out!=null){
					out.close();
					Log.d(TAG,"out.close()");
				}
				if(soc!=null){
					soc.close();
					Log.d(TAG,"soc.close()");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		if(this.myreceiveStream!=null){
			this.myreceiveStream.pause();
			//this.myreceiveStream.onDestroy();
			Log.d(TAG,"this.myreceiveStream.onDestroy();");
		}
		
		if(this.mySendStream!=null){
			this.mySendStream.pause();
			//this.mySendStream.onDestroy();
			Log.d(TAG,"this.mySendStream.onDestroy();");
		}
		
	
		Handler refresh = new Handler(Looper.getMainLooper());
		refresh.post(new Runnable() {
			public void run()
			{
				Log.d(TAG,"Deco brutale : appel de myFinish");
				main.myFinishAppel();
			}
		});
		Log.d(TAG,"Fin du thread");
	}
	public void closeComm() {
		// TODO Auto-generated method stub
		setLoop(false);
		out.println(Constante.CLOSE);
		out.flush();
		
		deco();
	}

	public boolean isLoop() {
		return loop;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	public void openDoor() {
		// TODO Auto-generated method stub
		if(out!=null){
			out.println(Constante.OPENDOOR);
			out.flush();
		}
	}
	
	public void up() {
		// TODO Auto-generated method stub
		if(out!=null){
			out.println(Constante.VOLUP);
			out.flush();
		}
	}
	
	public void down() {
		// TODO Auto-generated method stub
		if(out!=null){
			out.println(Constante.VOLDOWN);
			out.flush();
		}
	}
	
	public void p50() {
		// TODO Auto-generated method stub
		if(out!=null){
			out.println(Constante.P50);
			out.flush();
		}
	}
	
	public void ts() {
		// TODO Auto-generated method stub
		if(out!=null){
			out.println(Constante.TS);
			out.flush();
		}
	}
    
	public Stream getMySendStream() {
		return mySendStream;
	}

	public void setMySendStream(Stream mySendStream) {
		this.mySendStream = mySendStream;
	}

	public Stream getMyreceiveStream() {
		return myreceiveStream;
	}

	public void setMyreceiveStream(Stream myreceiveStream) {
		this.myreceiveStream = myreceiveStream;
	}

	public Socket getSoc() {
		return soc;
	}
}
