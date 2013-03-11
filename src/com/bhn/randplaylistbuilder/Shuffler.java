/*
 * had help on this thanks to tamas
 * http://www.helloandroid.com/tutorials/how-display-custom-dialog-your-android-application
 * 
 * this class provides a means to shuffle a certain quantity of songs out of MediaStore 
 * 
 */
package com.bhn.randplaylistbuilder;

import java.util.List;
import java.util.LinkedList;
import java.util.Random;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.content.Context;
import android.view.View;
import android.app.Activity;
import android.app.Dialog;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioButton;

public class Shuffler extends Activity {
	private List<Song> shuffledList;
	private int numSongs;
	private Context context;
	private SongChooser songChooser;
	
	public Shuffler(Context context, SongChooser songChooser){
		this.context = context;
		this.songChooser = songChooser;
		shuffledList = new LinkedList<Song>();
		promptForNumSongs();
	}
	
	public List<Song> getShuffledList() {
		return shuffledList;
	}
	
	public void shuffle() {
		//songFetcher.reset();
		//setup SongChooser
		songChooser.reset();
    	if (songChooser.getSongChooserAdapter() != null) {
    		songChooser.getSongChooserAdapter().reset();
    	}
    	songChooser.getSongFetcher().setCursor(getCursor());
    	songChooser.getSongFetcher().setSongList();

    	//have some room here before setAdapter() to return songs from fetcher and shuffle them.
    	//TODO a way to shuffle songs before processing them through songFetcher?
    	//	   that way I wouldn't have to send every single song through songFetcher
    	List<Song> wholeList = songChooser.getSongFetcher().getSongs();
    	//random position generator
    	Random generator = new Random();
    	for (int i = 0; i < numSongs; i++) {
    		//get random position in songList for entire MediaStore 
    		int pos = generator.nextInt(wholeList.size());
    		shuffledList.add(wholeList.get(pos));
    	}
    	
    	songChooser.getSongFetcher().setSongList(shuffledList);
    	songChooser.setAdapter();
	}
	
	private void promptForNumSongs() { 
		//create a dialog with some buttons and a radio group
		//use to limit how many songs will be shuffled
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.num_songs_picker_dialog);
		dialog.setTitle("How many songs should be shuffled?");
		dialog.setCancelable(true);
		
		//dialog members
		Button okButton = (Button)dialog.findViewById(R.id.ok_button);
		Button cancelButton = (Button)dialog.findViewById(R.id.cancel_button);
		final RadioGroup options = (RadioGroup)dialog.findViewById(R.id.numSongsPickerGroup);
		
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.popToast("OK button pressed");
				//if something is selected
				if (options.getCheckedRadioButtonId() != -1) {
					//get selected ID
					int selectionID = options.getCheckedRadioButtonId();
					RadioButton selection = (RadioButton)options.findViewById(selectionID);
					//parse text out of radio button, this is the number of songs to be shuffled
					numSongs = Integer.parseInt(selection.getText().toString());
					Util.popToast("numSongs = " + numSongs);
					dialog.dismiss();
					shuffle();
				} else {
					//must select option or cancel
					Util.popToast("No selection made");
				}
			}
		});
		
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.popToast("Cancel button pressed");
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}
	
	private Cursor getCursor() {
		Cursor cursor = getCompleteSongList();
		
		Util.popToast("Shuffler num songs = " + cursor.getCount());
		
		return cursor;
	}
	
	private Cursor getCompleteSongList() {
		Cursor cursor = null;
		ContentResolver resolver = context.getContentResolver();
        String[] songlistProjection = {
    			MediaStore.Audio.Media.ARTIST_ID, // TODO, two IDs?
    			MediaStore.Audio.Media.ARTIST,
    			MediaStore.Audio.Media.TITLE,   
    			MediaStore.Audio.Media._ID,
    			MediaStore.Audio.Media.DATA	
        };
        String songlistSelection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        
        cursor = resolver.query(
        		MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        		songlistProjection,
        		songlistSelection,	 
        		null,
        		null); 
        
        return cursor;
	}
}
