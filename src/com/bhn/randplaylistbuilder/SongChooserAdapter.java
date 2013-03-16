/*
 * 
 *  had help on this thanks to 
 * 	http://www.learn-android.com/2011/11/22/lots-of-lists-custom-adapter/
 *  & Siten
 *  http://stackoverflow.com/questions/4990963/android-problem-with-checkbox-with-baseadapter
 * 
 */
package com.bhn.randplaylistbuilder;

import java.util.List;
import java.util.LinkedList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.CheckBox;
import android.app.Activity;

public class SongChooserAdapter extends BaseAdapter {
	private List<Song> songs;
	private List<Integer> doNotPlayPositionList;
	//private List<View> songViews;
	private Activity context;
	private int selectedPosition;
	private int playingPosition;
	//private SongPlayer songPlayer;
	
	public SongChooserAdapter(Activity context, SongFetcher songFetcher, SongPlayer songPlayer) {
		this.context = context;
		songs = new LinkedList<Song>();
		songs.addAll(songFetcher.getSongs());
		selectedPosition = -1;
		playingPosition = -1;
		//this.songPlayer = songPlayer;
		doNotPlayPositionList = new LinkedList<Integer>();
		//this.songViews = new LinkedList<View>();
	}
	
	/*
	public List<View> getSongViews() {
		return songViews;
	}*/
	
	public List<Integer> getDoNotPlayPositionList() {
		return doNotPlayPositionList;
	}
	
	public void reset() {
		songs.clear();
		doNotPlayPositionList.clear();
		selectedPosition = -1;
		playingPosition = -1;
		notifyDataSetChanged();
	}
	
	public void setSelectedPosition(int selectedPosition) {
		this.selectedPosition = selectedPosition;
	}
	
	public void setPlayingPosition(int playingPosition) {
		this.playingPosition = playingPosition;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = getWorkingView(convertView);
		ViewHolder viewHolder = getViewHolder(view);
		
		/*
		//not really sure how to solve this one yet, going to use this for now.
		//also may be unnecessary, could probably find another way
		if (!songViews.contains(view)) {
			songViews.add(view);
		}*/
		
		/* 
		 * having issues with check boxes and scrolling
		 * seems there is still a tie to a recycled view, which causes the display to incorrectly display marks 
		 * http://stackoverflow.com/questions/5444355/android-listview-with-checkbox-problem
		 * solved, changed to onclicklistener, and added some code below
		 */
	    viewHolder.checkBox.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            	CheckBox checkBox = (CheckBox) view.findViewById(R.id.songPlayStatus);
            	Integer positionChecked = Integer.valueOf(position);
                if (checkBox.isChecked()) { 
                	Util.popToast("UnCheck..." + positionChecked);
                	//remove from blacklist
                	doNotPlayPositionList.remove(positionChecked);
                } 
                else if (!checkBox.isChecked()) { 
                	Util.popToast("UnCheck..." + positionChecked);
                	//blacklist
                	doNotPlayPositionList.add(positionChecked);
                } 
            } 
        });
		viewHolder.checkBox.setTag(position); //keep track of relevant view
		
		//this is the solution to the list box scrolling issue
		//if the song has been blacklisted, it should be unchecked, otherwise checked. 
		if (doNotPlayPositionList.contains(position)) {
			viewHolder.checkBox.setChecked(false);
		} else {
			viewHolder.checkBox.setChecked(true);
		}
		
		//song name
		viewHolder.songName.setText(songs.get(position).getName());
		//artist name
		viewHolder.artistName.setText(songs.get(position).getArtist());
	
		//color song selected seperate from song playing
		if ((position == selectedPosition) && (position != playingPosition)) {
			//view.setPressed(true);
			view.setBackgroundColor(context.getResources().getColor(R.color.song_selector_selected));
			//songPlayer.setSelectedButtonState(1);
		} else if (position == playingPosition) {
			//...
			//view.setPressed(false);
			view.setBackgroundColor(context.getResources().getColor(R.color.player_button_color));
			//songPlayer.setPlayingButtonState(0);
		}	else { //not selected and not playing
			//view.setPressed(false);
			//view.setSelected(false);
			view.setBackgroundColor(context.getResources().getColor(R.color.song_selector_normal));
		}
		
		view.setTag(viewHolder);
		
		return view;
	}
	
	@Override
	public int getCount() {
		return songs.size();
	}
	
	@Override
	public Object getItem(int position) {
		//return songViews.get(position);
		return null;
	}
	
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public void add(Song song) {
		songs.add(song);
		notifyDataSetChanged();
	}
	
	private View getWorkingView(View convertView) {
		//working view is convertview, re-used, or inflated
		View workingView = null;
		
		if (convertView == null) { 
			Context context = this.context; 
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			workingView = inflater.inflate(R.layout.song_item, null); 
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
		
		if (tag == null || !(tag instanceof ViewHolder)) {
			viewHolder = new ViewHolder();
			
			viewHolder.artistName = (TextView)workingView.findViewById(R.id.artistName);
			viewHolder.songName = (TextView)workingView.findViewById(R.id.songName);
			viewHolder.checkBox = (CheckBox) workingView.findViewById(R.id.songPlayStatus);
		} else { 
			viewHolder = (ViewHolder) tag;
		}
		
		return viewHolder;
	}
	
	private static class ViewHolder {
		public TextView artistName;
		public TextView songName;
		public CheckBox checkBox;
	}
}