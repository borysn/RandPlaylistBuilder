/*
 *  
 *  this adapter is for the playlist spinner drop down menu
 *  populate the menu and allow for selection
 * 
 */
package com.bhn.randplaylistbuilder;

import java.util.List;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PlaylistChooserAdapter extends BaseAdapter {
	private List<Playlist> playlists;
	public Activity context;
	
	public PlaylistChooserAdapter(Activity context, PlaylistFetcher fetcher) {
		this.playlists = new LinkedList<Playlist>();
		this.playlists.addAll(fetcher.getPlaylists());
		this.context = context; 
	} 
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = getWorkingView(convertView);
		ViewHolder viewHolder = getViewHolder(view);
		//Toast.makeText(context, playlistNames.get(position), Toast.LENGTH_SHORT).show();
		//song name
		viewHolder.playlistName.setText(playlists.get(position).getName());
		
		return view;
	}
	
	@Override
	public int getCount() {
		return playlists.size();
	}
	
	@Override
	public Object getItem(int position) {
		return null;
	}
	
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public void add(String playlistName, String playlistID) {
		playlists.add(new Playlist(playlistName, playlistID));
		notifyDataSetChanged();
	}
	
	private View getWorkingView(View convertView) {
		//working view is convertview, re-used, or inflated
		View workingView = null;
		
		if (null == convertView) {
			Context context = this.context;
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			workingView = inflater.inflate(R.layout.playlist_item, null);
		} else {
			workingView = convertView;
		}
		
		return workingView;	
	}
	
	//ViewHolder
	private ViewHolder getViewHolder(View workingView) {
		//avoid re-looking up references
		ViewHolder viewHolder = null;
		Object tag = workingView.getTag();
		
		if (null == tag || !(tag instanceof ViewHolder)) {
			viewHolder = new ViewHolder();
			
			viewHolder.playlistName = (TextView)workingView.findViewById(R.id.playlist_name);
		} else {
			viewHolder = (ViewHolder) tag;
		}
		
		return viewHolder;
	}
	
	private static class ViewHolder {
		public TextView playlistName;
	}
}