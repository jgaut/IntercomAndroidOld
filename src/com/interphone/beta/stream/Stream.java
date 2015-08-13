package com.interphone.beta.stream;

import android.util.Log;

public class Stream  {
	private native void nativeInitSendStream(String ip, String port);     // Initialize native code, build pipeline, etc about send stream
	private native void nativeInitReceiveStream();     // Initialize native code, build pipeline, etc about receive stream
    private native void nativeFinalize(); // Destroy pipeline and shutdown native code
    private native void nativePlay();     // Set pipeline to PLAYING
    private native void nativePause();    // Set pipeline to PAUSED
    private static native boolean nativeClassInit(); // Initialize native class: cache Method IDs for callbacks
    private long native_custom_data;      // Native code will use this to keep private data
    private String TAG;
    private String mess;
	private int port=0;
	
    //private native void nativeThreadC();  // Thread to communicate with Interphone
    
    private boolean is_playing_desired=true;   // Whether the user asked to go to PLAYING


    // Called when the activity is first created.
    
    public Stream(){
    	this.TAG = this.getClass().toString();
    }
    
    public void initSendStream(String ip, String port){
    	nativeInitSendStream(ip, port);
    }
    
    public void initReceiveStream(){
    	nativeInitReceiveStream();
    }
    

    public void onDestroy() {
        nativeFinalize();
    }

    // Called from native code. This sets the content of the TextView from the UI thread.
    private void setMessage(final String message) {
        this.mess = message;
        Log.d(TAG, mess);
    }
    
    private void setPort(final String port) {
        this.port = Integer.parseInt(port);
        Log.d(TAG, port);
    }

    // Called from native code. Native code calls this once it has created its pipeline and
    // the main loop is running, so it is ready to accept commands.
    private void onGStreamerInitialized () {
		Log.d(TAG, "Gst initialized. Restoring state, playing:" + is_playing_desired);
        // Restore previous playing state
        if (is_playing_desired) {
            nativePlay();
        } else {
            nativePause();
        }
    }
    
    public void play(){
    	this.nativePlay();
    }
    
    public void pause(){
    	this.nativePause();
    }

	static {
        System.loadLibrary("gstreamer_android");
        System.loadLibrary("stream");
        nativeClassInit();
    }

	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

}
