/*
       Thanks to Almond Joseph Mendoza
       http://www.tutorialforandroid.com/2009/01/get-phone-state-when-someone-is-calling_22.html
*/
package com.bhn.randplaylistbuilder;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.content.Context;	
import android.content.ContextWrapper;

public class PhoneCallListener extends PhoneStateListener {
    private Context context;
    private ContextWrapper wrapper;
    private SongPlayer songPlayer;
    private boolean isPaused;
    
    //need to pass in some values for the full functionality
    public PhoneCallListener(Context context, SongPlayer songPlayer) {
    	this.context = context;
    	this.songPlayer = songPlayer;
    	wrapper = new ContextWrapper(context);
    	isPaused = false;
    }
    
    /*
     * 
     * What we have is a function called onCallStateChanged which would be fired when the 
     * LISTEN_CALL_STATE dispatches it. The states are either, 
     * ringing(CALL_STATE_RINGING),
     * answers (CALL_STATE_OFFHOOK), 
     * or hang up/end call (CALL_STATE_IDLE). 
     */
    public void onCallStateChanged(int state, String incomingNumber) {
    	switch(state){
        case TelephonyManager.CALL_STATE_IDLE:
          Log.d("DEBUG", "IDLE");
          if (isPaused == true) {
        	  songPlayer.playSong();
        	  isPaused = false;
          }
        break;
        case TelephonyManager.CALL_STATE_OFFHOOK:
          Log.d("DEBUG", "OFFHOOK");
        break;
        case TelephonyManager.CALL_STATE_RINGING:
          Log.d("DEBUG", "RINGING");
          songPlayer.pauseSong();
          isPaused = true;
        break;
        }
    }
}