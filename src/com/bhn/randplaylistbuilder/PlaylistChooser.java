/* 
 * Had some help on this from Hrishikesh Kumar
 *   http://collegewires.com/android/2012/07/android-spinner-example/
 *   
 *  Must set SongChooser externally using function 
 *  
 *  This class will also have to reset other classes to clear up the song lists. 
 *  
 */
package com.bhn.randplaylistbuilder;

import android.app.Activity;	
import android.view.View;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class PlaylistChooser extends Activity {
	private PlaylistFetcher playlistFetcher;
	private Spinner playlistSpinner;
	private PlaylistChooserAdapter playlistAdapter;
	private Activity context;
	private SongChooser songChooser;
	private SongPlayer songPlayer;
	
	public PlaylistChooser(Activity context) {
		this.context = context;
		init();
		setOnItemSelectedListener();
	}
	
	private void init() {
		playlistFetcher = new PlaylistFetcher(context);
		playlistAdapter = new PlaylistChooserAdapter(context, playlistFetcher);
		playlistSpinner = (Spinner)context.findViewById(R.id.playlist_chooser);
		playlistSpinner.setAdapter(playlistAdapter);
	}
	
	public void setSongChooser(SongChooser songChooser) {
		this.songChooser = songChooser;
	}
	
	public void setSongPlayer(SongPlayer songPlayer) {
		this.songPlayer = songPlayer;
	}
	
	private void setOnItemSelectedListener() {
		playlistSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Util.popToast("item=" + position + " name=" + playlistFetcher.getPlaylists().get(position).getName());
                if (position != 0) {
                	//reset some classes to clean up already loaded songs
                	songChooser.reset();
                	if (songChooser.getSongChooserAdapter() != null) {
                		songChooser.getSongChooserAdapter().reset();
                	}
                	songPlayer.reset();
                	
                	//setup SongChooser
                	songChooser.getSongFetcher().setCursor(playlistFetcher.getPlaylists().get(position).getID());
                	songChooser.getSongFetcher().setSongList();
                	songChooser.getSongFetcher().getSongs();
                	songChooser.setAdapter();
                } else { 
                	//this case is to catch position 0 in the list of playlists
                	//position 0 is instructions
                	songPlayer.setSelectedButtonState(0);
                	songChooser.reset();
                }
            }
            
            //not making use of this
            public void onNothingSelected(AdapterView<?> parent) {
                Util.popToast("Spinner: unselected");
            }
        });
	}
}