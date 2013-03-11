/*
 * 
 * Got help on this thanks to Shane Burgess
 * http://stackoverflow.com/questions/2694909/given-an-android-music-playlist-name-how-can-one-find-the-songs-in-the-playlist
 * & Theo
 * http://stackoverflow.com/questions/13154662/how-to-delete-a-playlist-from-mediastore-audio-playlists-in-android
 * 
 *  Must set Cursor
 *  
 *  gotta clean this up a little bit, maybe something more like this TODO
 *  http://www.androidsnippets.com/list-all-music-files
 *  
 *  provides access to media store to retrieve songs from a playlist
 *  provides access to media store for a complete list of songs that shuffler uses
 */
package com.bhn.randplaylistbuilder;

import java.util.List;
import java.util.LinkedList;

import android.database.Cursor;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.ContentResolver;

public class SongFetcher {
	private Activity context;
	private Cursor cursor;
	private List<Song> songs;
	private List<String> songAudioFileIDs;
	private List<String> songPlaylistFileIDs;
	private List<String> songArtists;
	private List<String> songNames;
	private List<String> songFilePaths;
	
	public SongFetcher(Activity context) {
		this.context = context;
		songs = new LinkedList<Song>();
		songAudioFileIDs = new LinkedList<String>();
		songPlaylistFileIDs = new LinkedList<String>();
		songArtists = new LinkedList<String>();
		songNames = new LinkedList<String>();
		songFilePaths = new LinkedList<String>();
		cursor = null;
	}
	
	public void reset() {
		songs.clear();
		songAudioFileIDs.clear();
		songPlaylistFileIDs.clear();
		songArtists.clear();
		songNames.clear();
		songFilePaths.clear();
		cursor = null;
	}
	
	public List<Song> getSongs() {
		return songs;
	}
	
	//would probably want to call reset before setCursor
	public void setCursor(String playlistID) {
		cursor = getSongListCursor(playlistID);
	}
	
	public void setCursor(Cursor cursor) {
		this.cursor = cursor;
	}
	
	//set song list from a playlist
	public void setSongList() {
		songAudioFileIDs.addAll(getSongAudioFileIDs());
		songPlaylistFileIDs.addAll(getSongPlaylistFileIDs());
		songArtists.addAll(getSongArtists());
		songNames.addAll(getSongNames());
		songFilePaths.addAll(getSongFilePaths());
		for (int i = 0; i < songAudioFileIDs.size(); i++) {
			songs.add(new Song(songNames.get(i), 
							   songArtists.get(i), 
							   songAudioFileIDs.get(i),
							   songPlaylistFileIDs.get(i),
							   songFilePaths.get(i)));
		}
	}
	
	//this is used for Shuffler to pass a shuffled list of songs back in
	public void setSongList(List<Song> songList) {
		this.songs.clear();
		this.songs.addAll(songList);
	}
	
	// **these next 5 methods populate song information to the appropriate storage list
	// all i'm doing is traversing through the cursor
	
	private List<String> getSongArtists() {
		List<String> artists = new LinkedList<String>();
        if (cursor.getCount() > 0) {
        	cursor.moveToFirst();
        	do {
        		artists.add(cursor.getString(1));
        	} while (cursor.moveToNext());
        }
        return artists;
	}
	
	private List<String> getSongFilePaths() {
		List<String> paths = new LinkedList<String>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				paths.add(cursor.getString(4));
			} while (cursor.moveToNext());
		}
		return paths;
	}
	
	private List<String> getSongAudioFileIDs() {
		List<String> IDs = new LinkedList<String>();
        if (cursor.getCount() > 0) {
        	cursor.moveToFirst();
        	do {
        		IDs.add(cursor.getString(0));
        	} while (cursor.moveToNext());
        }
        return IDs;
	}
	
	private List<String> getSongPlaylistFileIDs() {
		List<String> IDs = new LinkedList<String>();
        if (cursor.getCount() > 0) {
        	cursor.moveToFirst();
        	do {
        		IDs.add(cursor.getString(3));
        	} while (cursor.moveToNext());
        }
        return IDs;
	}
	
	private List<String> getSongNames() {
		List<String> names = new LinkedList<String>();
        if (cursor.getCount() > 0) {
        	cursor.moveToFirst();
        	do {
        		names.add(cursor.getString(2));
        	} while (cursor.moveToNext());
        }
        return names;
	}
	
	//setup the cursor for a song list from a specific playist
	private Cursor getSongListCursor(String playlistID) {
		Cursor cursor = null;
		ContentResolver resolver = context.getContentResolver();
		
		//what to look for in the db
        String[] playlistProjection = {
        		MediaStore.Audio.Playlists._ID};

        //setup a query to only get a specific playlist
        cursor = resolver.query(
        		MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
        		playlistProjection,
        		MediaStore.Audio.Playlists._ID + " = " +playlistID + "",	 
        		null,
        		null); 
        
        //setup another query to get all song information from that playlist
        if (cursor.getCount()> 0) {
        	cursor.moveToFirst();
        	Long playlistID_Long = cursor.getLong(0);
        	if (playlistID_Long > 0) {
        		String [] songListProjection = {
        			MediaStore.Audio.Playlists.Members.AUDIO_ID, // TODO, two IDs?
        			MediaStore.Audio.Playlists.Members.ARTIST,
        			MediaStore.Audio.Playlists.Members.TITLE,   
        			MediaStore.Audio.Playlists.Members._ID,
        			MediaStore.Audio.Playlists.Members.DATA
        		};
        		cursor = null;
        		cursor = resolver.query(
        				MediaStore.Audio.Playlists.Members.getContentUri("external", playlistID_Long),
        				songListProjection,
        				MediaStore.Audio.Media.IS_MUSIC + " != 0 ",
        				null,
        				null);
        	}
        }
        
        return cursor;
	}
}