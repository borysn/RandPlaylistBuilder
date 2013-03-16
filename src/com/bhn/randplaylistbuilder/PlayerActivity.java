/*
 * 	 (C) 2013 Borys Niewiadomski
 * 		Android MediaStore application
 *      - provide quick access to playlists
 *      - provide a quick shuffle of all songs in MediaStore, limited by number of songs choice
 *      - provide a way to not play certain songs in a playlist
 *      - provide basic mp3 player functionality 
 *   
 *   Also, thanks http://colorschemedesigner.com/#4L51ThxaWdJvy! for my app color scheme
 *   
 *   TODO: add song durations, add song progress slide bar, add pause for phone call
 *   TODO: add query manager for a more dynamic approach to android MediaStore
 *   TODO: implement SongQueue
 *   TODO: functionality to re-order playlists, drag and drop to position
 *   
 *   had a lot of issues with @drawable items. Just couldn't get the list selectors working properly. TODO 
 *   going to leave them there for later. I resolved a lot of the issues pragmatically in the appropriate
 *   source locations.
 */
package com.bhn.randplaylistbuilder;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PlayerActivity extends Activity {
	private PlaylistChooser playlistChooser;
	private SongChooser songChooser;
	private SongPlayer songPlayer;
	private TelephonyManager teleMan;
	private PhoneCallListener phoneCallListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		
		//Custom utility class, only used for Toast right now. 
		Util util = new Util(this);
		//song list
		songChooser = new SongChooser(this);
		
		//play list names
		playlistChooser = new PlaylistChooser(this); 
		playlistChooser.setSongChooser(songChooser);
		
		//songplayer
		songPlayer = new SongPlayer(this, songChooser);
		songChooser.setSongPlayer(songPlayer);
		playlistChooser.setSongPlayer(songPlayer);
		
		//now setup what to do when a phone call is received
		phoneCallListener = new PhoneCallListener(songPlayer);
		teleMan = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		teleMan.listen(phoneCallListener, PhoneStateListener.LISTEN_CALL_STATE);		
	}
}
