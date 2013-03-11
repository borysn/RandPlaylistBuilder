/*
 * had some help on this thanks to Lars Vogel, Vogella
 * http://www.vogella.com/articles/AndroidMedia/article.html#tutorial_soundpool
 * and thanks to shadow 
 * 
 * this is application user action control
 */
package com.bhn.randplaylistbuilder;

import java.io.IOException;

//import android.os.Environment;
import android.net.Uri;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.media.AudioManager;

public class SongPlayer extends Activity {
	private Button playButton;
	private Button stopButton;
	private Button pauseButton;
	private Button backButton;
	private Button skipButton;
	private Button shuffleButton;
	private Activity context;
	private SongChooser songChooser;
	private int playingPosition;
	private MediaPlayer mediaPlayer;
	private Song currentSong;
	private boolean paused;
	
	public SongPlayer(Activity context, SongChooser songChooser) {
		this.context = context;
		this.songChooser = songChooser;
		playingPosition = -1;
		mediaPlayer = new MediaPlayer();
		setOnCompletionListener();
		paused = false;
		initButtons();
	}
	
	private void initButtons() {
		playButton = (Button)context.findViewById(R.id.play_button);
		stopButton = (Button)context.findViewById(R.id.stop_button);
		pauseButton = (Button)context.findViewById(R.id.pause_button);
		backButton = (Button)context.findViewById(R.id.back_button);
		skipButton = (Button)context.findViewById(R.id.skip_button);
		shuffleButton = (Button)context.findViewById(R.id.shuffle_button);
		setButtonClickListeners();
		setSelectedButtonState(0); 
	}
	
	public void reset() {
		playingPosition = -1;
	}
	
	public int getPlayingPosition() {
		return playingPosition;
	}
	
	private void setOnCompletionListener() {
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mediaPlayer) {
				skipButton.performClick(); //basically the same thing
			}
		});
	}
	
	/*
	 * these next two methods will be used to differentiate button states between 
	 * selected song button states, and playing song button states.
	 * 
	 * still not sure if this will be necessary, could probably simplify it.
	 * 
	 * what I want to do though is have different actions for a playing song
	 * and different actions for a song that is only selected, or to be played
	 * 
	 * should also enumerate the states so im not just passing in arbitrary numbers TODO
	 * 		i.e. DISABLE_ALL = 0, SONG_SELECTED_NO_PLAYING = 1, ...
	 */
	//selected song button states
	public void setSelectedButtonState(int state) {
		switch(state) {
			//disable all
			default:
			case 0: 
				playButton.setEnabled(false);
				stopButton.setEnabled(false);
				pauseButton.setEnabled(false);
				backButton.setEnabled(false);
				skipButton.setEnabled(false);
				break;
			//song selected, no playing yet
			case 1:
				playButton.setEnabled(true);
				stopButton.setEnabled(false);
				pauseButton.setEnabled(false);
				backButton.setEnabled(false);
				skipButton.setEnabled(false);
				break;
			//song selected, different song is playing
			case 2:
				playButton.setEnabled(true);
				stopButton.setEnabled(true);
				pauseButton.setEnabled(true);
				backButton.setEnabled(true);
				skipButton.setEnabled(true);
				break;
		}
	}
	//playing song selected button states
	public void setPlayingButtonState(int state) {
		//play button has been pressed on a song
		switch(state) {
		//songPlying
		default:
		case 0:
			playButton.setEnabled(false);
			stopButton.setEnabled(true);
			pauseButton.setEnabled(true);
			backButton.setEnabled(true);
			skipButton.setEnabled(true);
			break;
		//stop button pressed
		case 1:
			playButton.setEnabled(true);
			stopButton.setEnabled(false);
			pauseButton.setEnabled(false);
			backButton.setEnabled(false);
			skipButton.setEnabled(false);
			break;
		//pause button pressed
		case 3:
			playButton.setEnabled(true);
			stopButton.setEnabled(true);
			pauseButton.setEnabled(false);
			backButton.setEnabled(true);
			skipButton.setEnabled(true);
			break;
		}
	}
	
	private void setButtonClickListeners() {
		//play
		playButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Util.popToast("play button pressed");
				
				if (!paused || (playingPosition != songChooser.getSelectedPosition())) {
					//set playing position
					playingPosition = songChooser.getSelectedPosition();
					songChooser.getSongChooserAdapter().setPlayingPosition(playingPosition);
					songChooser.getSongChooserAdapter().notifyDataSetChanged();
					
					//button state
					setPlayingButtonState(0);
					
					//get sound ID, an begin playing song
					//soundID = soundPool.load(this, R.raw.sound1, 1);
					Song newSong = songChooser.getSongFetcher().getSongs().get(playingPosition);
					if (newSong != currentSong) {
						currentSong = newSong; 
					}
					
					//whats the path showing?
					Uri path = Uri.parse(newSong.getAudioFilePath());
					Util.popToast(path.toString());
					
					playSong(path);
				} else {
					mediaPlayer.start(); //resume play after pause
					paused = false;
				}
			}
		});
		//stop
		stopButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Util.popToast("stop button pressed");
				
				//set button state to stop 
				setSelectedButtonState(1);
				
				mediaPlayer.stop();
			}
		}); 
		//pause
		pauseButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Util.popToast("pause button pressed");
				
				//button state
				setPlayingButtonState(3);
				
				mediaPlayer.pause();
				
				paused = true;
			}		
		}); 
		//back
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Util.popToast("back button pressed");
				
				//get sound ID, an begin playing song
				//soundID = soundPool.load(this, R.raw.sound1, 1);
				int temp = playingPosition; //save for reset
				if (playingPosition > -1 && playingPosition != 0) {
					//first set old playing position to selected
					songChooser.getSongChooserAdapter().setSelectedPosition(playingPosition);
					
					do {
						//keep skipping songs as long as they are black listed
						//while statement is pretty long TODO
						playingPosition -= 1; 
					} while (isBlackListed(Integer.valueOf(playingPosition)) && (playingPosition > -1)); 
					
					if (!isBlackListed(playingPosition) && (playingPosition > -1)) {
						Song newSong = songChooser.getSongFetcher().getSongs().get(playingPosition);
						if (newSong != currentSong) {
							currentSong = newSong; 
						}
	
						//now set new playing position
						songChooser.getSongChooserAdapter().setPlayingPosition(playingPosition);
						songChooser.getSongChooserAdapter().notifyDataSetChanged();
						
						//whats the path showing?
						Uri path = Uri.parse(newSong.getAudioFilePath());
						Util.popToast(path.toString());
						
					
						playSong(path);
					} else {
						playingPosition = temp;
						Util.popToast("Cannot go back anymore.");
					}
				} else { //tried going back on first song. Loop to last song instead? TODO
					Util.popToast("Cannot go back anymore.");
				}
			}
		});
		//skip
		skipButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Util.popToast("skip button pressed");
				//get sound ID, an begin playing song
				//soundID = soundPool.load(this, R.raw.sound1, 1);
				int numSongs = songChooser.getSongFetcher().getSongs().size();
				//play next song as long as it can keep going forward 
				//TODO loop
				int temp = playingPosition; //this is getting lost, save for resetting
				if (playingPosition != -1 && playingPosition != (numSongs-1)) {
					//first set old playing position to selected
					songChooser.getSongChooserAdapter().setSelectedPosition(playingPosition);
					
					do {
						//keep skipping songs as long as they are black listed
						playingPosition += 1; 
					} while (isBlackListed(Integer.valueOf(playingPosition)) && (playingPosition < numSongs)); 
					
					if (!isBlackListed(playingPosition) && (playingPosition < (numSongs-1))) {
						Song newSong = songChooser.getSongFetcher().getSongs().get(playingPosition);
						if (newSong != currentSong) {
							currentSong = newSong; 
						}
						//now set new playing position
						songChooser.getSongChooserAdapter().setPlayingPosition(playingPosition);
						songChooser.getSongChooserAdapter().notifyDataSetChanged();
						
						//whats the path showing?
						Uri path = Uri.parse(newSong.getAudioFilePath());
						Util.popToast(path.toString());
						
						playSong(path);
					} else {
						playingPosition = temp;
						Util.popToast("Cannot go forward anymore.");
					}
				} else { //tried skipping on last song. Loop to first song instead? TODO
					Util.popToast("Cannot go forward anymore.");
				}
			}
		});
		//create shuffle button
		shuffleButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Util.popToast("create shuffle pressed");
				Shuffler shuffler = new Shuffler(context, songChooser);
			}
		});
	}
	
	private boolean isBlackListed(Integer position) {
		//statement is pretty long, its probably fine though TODO
		if (songChooser.getSongChooserAdapter().getDoNotPlayPositionList().contains(Integer.valueOf(position))) {
			return true;
		} else {
			return false;
		}
	}
	
	private void playSong(Uri path) {
		//volume, should I keep track of this instead of setting it every time? TODO
		AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
		int volume = (int) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
		
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(context, path);
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (IOException e) { 
			Util.popToast("Cannot find audio file...");
		}
	}
}