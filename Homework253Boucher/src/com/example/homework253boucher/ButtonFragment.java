package com.example.homework253boucher;

import com.example.homework252boucher.R;

import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class ButtonFragment extends Fragment implements OnClickListener{

	private static final String IS_BEEPING = "isBeeping";
	private static final String TAG = "steve-fragment";
	private static final int NOTIFICATION_ID = 111;
	
	private boolean mIsBeeping = false;
	private boolean mIsExiting = false;
	Button mStartStopButton;
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	private BeepTask beepTask;
	private MediaPlayer mMediaPlayer;

    public ButtonFragment() {
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	Log.v(TAG, "fragment onCreateView()");

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        
        mStartStopButton = (Button)rootView.findViewById(R.id.start_stop_button);
        mStartStopButton.setOnClickListener(this);
          
        return rootView;
    } 
          
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
        	Log.v(TAG, "fragment onActivityCreated(): savedInstanceState has a VALUE");
            boolean isBeeping = savedInstanceState.getBoolean(IS_BEEPING, false);
            // Restart beep, if necessary
    		if (isBeeping) {
    			playBeep(true);		
    		} 
            setBeepingState(isBeeping);
        } else {
        	Log.v(TAG, "fragments onActivityCreated(): savedInstanceState is NULL");
        }
       
        mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
		
		// Intent to send when notification area is clicked
		Intent mainIntent = new Intent(getActivity(), MainActivity.class);
	    mainIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);    
		mainIntent.setAction(Intent.ACTION_MAIN);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
	    	
        PendingIntent mainPendingIntent = PendingIntent.getActivity(getActivity(), 0, mainIntent, 0 );
	    
		// Create the Normal Notification
		mNotification =  new Notification.Builder(getActivity())
			.setContentIntent(mainPendingIntent)
			.setContentTitle(getString(R.string.beeping_notice))
			.setContentText(getString(R.string.select_to_open))
			.setSmallIcon(R.drawable.ic_stat_bell)
		  	.getNotification();
	}  

	@Override public void onStart() { super.onStart(); Log.d( TAG, "onStart() called"); } 
	
	@Override public void onPause() { 
		super.onPause(); 
		Log.d( TAG, "onPause() called, mIsBeeping is " + mIsBeeping + "mIsExiting is " + mIsExiting);
		
		// Checking mIsExiting to prevent the situation where the notification icon flashes
		// momentarily in the status bar just before it app exits -- e.g., due to a user back
		// button press		
		if (mIsBeeping == true && mIsExiting == false) {
        	showNotificationIcon(true);
        }
	}	
	
	@Override public void onResume() { 
		super.onResume(); 
		Log.d( TAG, "onResume() called"); 
		
		// Requirements are that the notification icon not be shown when this app is
		// in the foreground, regardless of beep state, so make sure that it is gone
		showNotificationIcon(false);	
	}
	
	@Override public void onStop() { super.onStop(); Log.d( TAG, "onStop() called"); } 
	
	@Override 
	public void onDestroy() { 
		super.onDestroy(); 
		Log.d( TAG, "onDestroy() called"); 
		if (beepTask != null) {
			beepTask.cancel(true);
			beepTask = null;			
		} else {
			Log.d( TAG, "beepTask is NULL in onDestroy()"); 			
		}	
		showNotificationIcon(false);
    	setBeepingState(false);	
	} 


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.v(TAG, "fragment onSaveInstanceState() called");

		outState.putBoolean(IS_BEEPING, mIsBeeping);
	}

	@Override
	public void onClick(View v) {
		if (mIsBeeping) {  // Currently beeping, so request to turn beep off
			playBeep(false);
		} else {  // Currently NOT beeping, so request to turn beep on
			playBeep(true);		
		}
	} 

    private void playBeep(boolean play) {	
    	if (play) {
    		// Note: setBeepingState(true) and updateNotificationArea() will be called via beepTask
    		if (beepTask == null) {
    			beepTask = new BeepTask();
        		// Start beeping in background task
        		beepTask.execute();
    		}
		} else {
			if (beepTask != null) {
				beepTask.cancel(true);
				beepTask = null;
				setBeepingState(false);	  
			}
		}	
	}

    private void setBeepingState(boolean isBeeping) {
    	mIsBeeping = isBeeping;
    	
		// If beep is ongoing, allow user to stop beep; otherwise, allow user to start it
    	if (mIsBeeping) {
    		mStartStopButton.setText(R.string.stop);
    	}
        else {
        	mStartStopButton.setText(R.string.start);
        }		
	}
   
    // Post-req: setBeepingState() should be called AFTER this function
	private void showNotificationIcon(boolean showIcon) {
		// If isBeeping is true, do nothing
		if (showIcon == true) {
        	Log.v(TAG, "showNotificationIcon(): showing icon");			
        	mNotificationManager.notify(NOTIFICATION_ID, mNotification);
		}
		else {
        	Log.v(TAG, "showNotificationIcon(): removing icon");			
			mNotificationManager.cancel(NOTIFICATION_ID);
		}
	}
	
	protected void appIsExiting(boolean isExiting) {
    	Log.v(TAG, "appIsExiting(): isExiting is " + isExiting);			
		
		mIsExiting = isExiting;
	}
	
    private class BeepTask extends AsyncTask<Void, Boolean, Void> {
        
        @Override
        protected Void doInBackground(Void... params) {
                       
            Thread.currentThread().setName("BeepTask Thread");
            
            mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.ding);
            
            // Play a beep every five seconds            
            while (isCancelled() == false) {
                try {
                	// Play beep
                	if (mMediaPlayer != null) {
                		mMediaPlayer.start(); 
                		publishProgress(true);
                	} else {
                    	Log.i(TAG, "mMediaPlayer is null! Can't play beep!");
                    	publishProgress(false);
                    	break;
                	}                    
                    Thread.sleep(5000);
                }
                catch (InterruptedException e) {
                }                
            }
            return null;
        }

        @Override
        protected void onPreExecute() {           
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            // Stop beeping
        	if (mMediaPlayer != null) {
        		mMediaPlayer.stop();
        		mMediaPlayer.reset();
        		mMediaPlayer = null;
        	}
        	showNotificationIcon(false);
        	setBeepingState(false);

        	super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {  
        	boolean isBeeping = values[0];
        	setBeepingState(isBeeping);
            super.onProgressUpdate(values);
        }

		@Override
		protected void onCancelled(Void result) {
            // Stop beeping
	       	if (mMediaPlayer != null) {
        		mMediaPlayer.stop();
        		mMediaPlayer.reset();
        		mMediaPlayer = null;
        	}
	       	showNotificationIcon(false);
	       	setBeepingState(false);
	       	
			super.onCancelled(result);			
		}
    }
}