package com.interphone.beta.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MyPublish extends Thread{

	private String TAG;
	private static String ip;
	private static int port;
	private static String ssid;
	private String ip2;
	private int port2;
	private String ssid2;
	private Context context;
	private Intent intent;
	public static String IP;
	public static String SERVER = "88.166.207.71";

	public MyPublish(Context context, Intent intent) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.TAG = this.getClass().toString();
		this.intent = intent;
	}

	public void run(){
		this.publish();
	}

	public void publish(){
		try 
		{
			//Log.d(TAG, Boolean.toString(NetworkInterface.getNetworkInterfaces().hasMoreElements()));
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) 
			{
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) 
				{
					InetAddress inetAddress = enumIpAddr.nextElement();
					Log.d(TAG,inetAddress.getHostAddress().toString());
					//if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && !inetAddress.isMCGlobal() &&inetAddress.getHostAddress().toString().length()<=12){
					if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().toString().length()<=15){
								ip2 = inetAddress.getHostAddress();
						Log.d(TAG, "Mon ip :" +inetAddress.getHostAddress());
						IP=inetAddress.getHostAddress().toString();
						while(MySocket.getSocketServer()==null){
							try {
								Log.d(TAG, "Attente que le socket serveur soit créé");
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						while(!MySocket.getSocketServer().isBound()){
							try {
								Log.d(TAG, "Attente que le socket serveur soit bound");
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						port2 = MySocket.getSocketServer().getLocalPort();
						WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
						if(wifiManager.getConnectionInfo().getIpAddress()==0){
							ssid2=null;
						}else{
							ssid2 = wifiManager.getConnectionInfo().getSSID().replace('"', ' ').trim();
						}
						
						
						/*Si une des trois composantes a changé*/
						/*Récupération de l'imei*/
						TelephonyManager mngr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE); 
						Log.d(TAG, "IMEI : "+mngr.getDeviceId());
						Log.d(TAG,"IP : "+ip2);
						Log.d(TAG,"Port : "+port2);
						Log.d(TAG, "SSID : "+ssid2);
						Log.d(TAG,"IP : "+MyPublish.getIp());
						Log.d(TAG,"Port : "+MyPublish.getPort());
						Log.d(TAG, "SSID : "+MyPublish.getSsid());
						if(MyPublish.getIp()==null || !MyPublish.getIp().equals(ip2) || MyPublish.getPort()!=port2 || ( MyPublish.getSsid()!=null && !MyPublish.getSsid().equals(ssid2))){
							/*Publication sur le site (annuaire)*/
							Log.d(TAG,"Tentative de publication...");
							URL url;
							HttpURLConnection conn;
							BufferedReader rd;
							String line;
							try {
								String event = intent!=null?intent.getAction():"null";
								String myurl = "http://jintercom13.appspot.com/jintercom13?compte=c1&action=add&imei="+mngr.getDeviceId()+"&server="+ip2+"&port="+port2+"&portSsh="+"2222"+"&event="+event;
								//Log.d(TAG, myurl);
								url = new URL(myurl);
								conn = (HttpURLConnection) url.openConnection();
								conn.setRequestProperty("Content-Type", "text/plain"); 
								conn.setRequestProperty("charset", "utf-8");
								conn.setRequestMethod("POST");
								conn.setReadTimeout(10000);
								Log.d(TAG, conn.toString());
								rd = new BufferedReader(
										new InputStreamReader(conn.getInputStream()));

								while ((line = rd.readLine()) != null) {
									Log.d(TAG,"Publication : "+line);
									if(line.equalsIgnoreCase("OK")){
										MyPublish.setIp(ip2);
										MyPublish.setPort(port2);
										MyPublish.setSsid(ssid2);
									}
								}
								rd.close();
							} catch (IOException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}else{
							Log.d(TAG,"Deja publié");
						}



					}
				}
			}
		} 
		catch (SocketException e) 
		{
			e.printStackTrace();
		}
	}
	public static String getIp() {
		return ip;
	}

	public static void setIp(String ip) {
		MyPublish.ip = ip;
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		MyPublish.port = port;
	}

	public static String getSsid() {
		return ssid;
	}

	public static void setSsid(String ssid) {
		MyPublish.ssid = ssid;
	}

}
