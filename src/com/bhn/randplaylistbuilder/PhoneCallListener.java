/*
       Thanks to Almond Joseph Mendoza
       http://www.tutorialforandroid.com/2009/01/get-phone-state-when-someone-is-calling_22.html
*/
package com.bhn.randplaylistbuilder;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneCallListener extends PhoneStateListener {
    private SongPlayer songPlayer;
    private boolean isPaused;
    
    //need to pass in some values for the full functionality
    public PhoneCallListener(SongPlayer songPlayer) {
    	this.songPlayer = songPlayer;
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