/*
 * Store information about a specific song, used to diplay info, and get song path in phone
 * 
 */
package com.bhn.randplaylistbuilder;

public class Song {
	private String name;
	private String artist;
	private String audioFilePath;
	//do I only need one of these? TODO
	private String audioFileID;
	private String playlistFileID;
	
	public Song(String name, String artist, String audioFileID, String playlistFileID, String audioFilePath) {
		this.name = name;
		this.artist = artist;
		this.audioFilePath = audioFilePath;
		this.audioFileID = audioFileID;
		this.playlistFileID = playlistFileID; //TODO again with the two IDs, gotta clean this up
	}
	
	public String getName() {
		return name;
	}
	
	public String getArtist() {
		return artist;
	}
	
	public String getAudioFilePath() {
		return audioFilePath;
	}
	
	public String getAudioFileID() {
		return audioFileID;
	}
	
	public String getPlaylistFileID() {
		return playlistFileID;
	}
}