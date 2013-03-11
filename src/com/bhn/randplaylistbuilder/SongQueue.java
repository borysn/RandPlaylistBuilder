/*
 * 
 * this is yet to be implemented. 
 * will be used to help songPlayer get organizd
 * 
 */
package com.bhn.randplaylistbuilder;

import java.util.List;
import java.util.LinkedList;
import java.util.Queue;

public class SongQueue {
	private List<Song> songList;
	private Queue<List> songQueue;
	
	public SongQueue(List<Song> songList) {
		this.songList = new LinkedList<Song>();
		this.songList.addAll(songList);
	}
}