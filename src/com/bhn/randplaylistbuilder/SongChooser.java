/*
 * Had some help on this and song_list_selector/play_list_selector thanks to Labeeb P
 * http://stackoverflow.com/questions/4860085/force-a-listview-item-to-stay-pressed-after-being-clicked
 *  
 * playlist must set fetcher and adapter invoking appropriate methods externally
 * 
 * Store listview for songs, populate, display, allow for selection of views
 * 
 */
package com.bhn.randplaylistbuilder;

import android.app.Activity;	
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class SongChooser extends Activity {
	private SongFetcher songFetcher;
	private ListView songList;
	private SongChooserAdapter songlistAdapter; //funny naming? TODO
	private Activity context;
	private SongPlayer songPlayer;
	//private View selectedSong;
	private int selectedPosition;
	//private int playingPosition;
	
	public SongChooser(Activity context) {
		this.context = context;
		init();
		setOnItemClickListener();
	}
	
	private void init() {
		songFetcher = new SongFetcher(context);
		songlistAdapter = null;
		songList = (ListView)context.findViewById(R.id.song_chooser);
		//selectedSong = null;
		selectedPosition = -1;
		//playingPosition = -1;
	}
	
	public void reset() {
		//clean up for reloading songs
		songFetcher.reset();
		if (songlistAdapter != null) {
			songlistAdapter.reset();
		}
	} 
	
	public SongFetcher getSongFetcher() {
		return songFetcher;
	}
	
	public SongChooserAdapter getSongChooserAdapter() {
		return songlistAdapter;
	}
	
	public int getSelectedPosition() {
		return selectedPosition; 
	}
	
	public void setAdapter() {
		songlistAdapter = new SongChooserAdapter(context, songFetcher, songPlayer);
		songList.setAdapter(songlistAdapter);
	} 
	
	public void setSongPlayer(SongPlayer songPlayer) {
		this.songPlayer = songPlayer;
	}
	
	private void setOnItemClickListener() {
		songList.setOnItemClickListener((new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	int playingPosition = songPlayer.getPlayingPosition();
            	//this is used to help setup different button states
            	//	1 state for a selected song that is playing
            	//  1 state for a selected song that is not playing
            	//what defines 1st state?
            	//selectedPosition == -1 -- so no songs have been selected let it through
            	//position != playingPosition -- this is next state
            	//selectedPosition != position -- song reselcted, do nothing
            	//(selectedPosition != position) || ((selectedPosition != songPlayer.getPlayingPosition()) || selectedPosition == -1)
            	if ((selectedPosition != position || selectedPosition == -1) && playingPosition != position)  {
            		//button state 
            		songPlayer.setSelectedButtonState(1); 
	                Util.popToast("item=" + position + " | songname=" + songFetcher.getSongs().get(position).getName());
	                selectedPosition = position; 
	                songlistAdapter.setSelectedPosition(position);
            	} else if (position == songPlayer.getPlayingPosition()){ //this case is only true if playing song is reselected
            		//this gets called after i re-select the song item twice. 
            		//needs to be called after the initial selection     TODO
            		//not sure whats going on yet
            		selectedPosition = songPlayer.getPlayingPosition();
            		songlistAdapter.setSelectedPosition(selectedPosition);
            		songPlayer.setPlayingButtonState(0);
            		//songlistAdapter.notifyDataSetChanged();
            	}
            }
        }));
	}
}