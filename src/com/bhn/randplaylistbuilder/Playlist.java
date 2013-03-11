/*
 * 
 * Store information about playlists and display the information
 * 
 */
package com.bhn.randplaylistbuilder;

public class Playlist {
	private String name;
	private String ID;
	
	public Playlist(String name, String ID) {
		this.name = name;
		this.ID = ID;
	}
	
	public String getName() {
		return name;
	}
	
	public String getID() {
		return ID;
	}
}