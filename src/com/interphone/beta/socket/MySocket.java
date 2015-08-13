package com.interphone.beta.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.interphone.beta.Main;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;

public class MySocket extends Thread{

	private static ServerSocket socketServer;
	private static Socket socketClient;
	private int port;
	private String TAG;
	private Context context;
	/*private String login="pi";
	private String pwd="Marseille13!";
	private int portSsh = 2222;
	private String server = MyPublish.SERVER;
	*/
	public MySocket(Context context, int port){
		this.context = context;
		this.port = port;
	}
	public void run() {
		// TODO Auto-generated method stub
		this.TAG = this.getClass().toString();
		try {
			socketServer = new ServerSocket(port, 1);
			
			/*SSH : port forwarding
			JSch jsch=new JSch();
			session=jsch.getSession(login, server, portSsh);
			java.util.Properties config = new java.util.Properties(); 
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(pwd); 
			session.connect();
			session.setPortForwardingR(socketServer.getLocalPort(), "localhost", socketServer.getLocalPort());
			System.out.println("ssh");

			//int p = Integer.parseInt(tab[0].split(":")[0]);
			*/
			while(true){
				Log.d(TAG,"Attente de connexion sur le port : "+socketServer.getLocalPort());
				
				/*String [] tab = session.getPortForwardingR();
				for(int i=0; i<tab.length;i++){
					Log.d(TAG, tab[i]);
				}*/
				
				socketClient = socketServer.accept();
				
				//socketClient.setSoTimeout(15000);
				/*Faire des vérification de sécurité : auth etc*/

				Log.d(TAG,"Lancement IHM!");
				/*Lancement de l'IHM avec prise d'appel*/
				Intent it = new Intent(context, Main.class);
				it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				it.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD + WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON + WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
				it.putExtra("appel", true);
				context.startActivity(it);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /*catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	public static ServerSocket getSocketServer() {
		return socketServer;
	}
	public static void setSocketServer(ServerSocket socketServer) {
		MySocket.socketServer = socketServer;
	}
	public static Socket getSocketClient() {
		return socketClient;
	}
	public static void setSocketClient(Socket socketClient) {
		MySocket.socketClient = socketClient;
	}
}
