/*
 * 
 * Got a lot of help on this thanks to Shane Burgess
 * http://stackoverflow.com/questions/2694909/given-an-android-music-playlist-name-how-can-one-find-the-songs-in-the-playlist
 * & Theo
 * http://stackoverflow.com/questions/13154662/how-to-delete-a-playlist-from-mediastore-audio-playlists-in-android
 * 
 * this class provides access to mediastore on android, and retrieves all playlists
 * which are to be populated 
 */
package com.bhn.randplaylistbuilder;

import java.util.List;
import java.util.LinkedList; 

import android.database.Cursor;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.ContentResolver;

public class PlaylistFetcher {
	private Activity context;
	private Cursor cursor;
	private List<Playlist> playlists;
	private List<String> playlistIDs;
	private List<String> playlistNames;
	
	public PlaylistFetcher(Activity context) {
		this.context = context;
		playlists = new LinkedList<Playlist>();
		playlistIDs = new LinkedList<String>();
		playlistNames = new LinkedList<String>();
		cursor = getPlaylistCursor();
		setPlaylistList();
	}
	
	public List<Playlist> getPlaylists() {
		return playlists;
	}
	
	private void setPlaylistList() {
		playlistIDs.addAll(getPlaylistIDs());
		playlistNames.addAll(getPlaylistNames());
		
		//edit First Element to display "Choose a playlist" text
		playlistIDs.set(0, "0");
		playlistNames.set(0, context.getString(R.string.playlist_chooser_prompt_text));
		
		for (int i = 0; i < playlistIDs.size(); i++) {
			playlists.add(new Playlist(playlistNames.get(i).toString(),playlistIDs.get(i)));
		}
	}
	
	private List<String> getPlaylistIDs() {
		List<String> IDs = new LinkedList<String>();
        if (cursor.getCount() > 0) {
        	cursor.moveToFirst();
        	do {
        		IDs.add(cursor.getString(0));
        	} while (cursor.moveToNext());
        }
        return IDs;
	}
	
	private List<String> getPlaylistNames() {
		List<String> names = new LinkedList<String>();
        if (cursor.getCount() > 0) {
        	cursor.moveToFirst();
        	do {
        		names.add(cursor.getString(1));
        	} while (cursor.moveToNext());
        }
        return names;
	}
	
	private Cursor getPlaylistCursor() {
		Cursor cursor = null;
		ContentResolver resolver = context.getContentResolver();
		
        String[] projection = {
        		MediaStore.Audio.Playlists._ID,
        		MediaStore.Audio.Playlists.NAME};

        cursor = resolver.query(
        		MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
        		projection,
        		null,	 //+ " = "+playlistId+"",
        		null,
        		null); 
        
        return cursor;
	}
}