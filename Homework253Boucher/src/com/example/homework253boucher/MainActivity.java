package com.example.homework253boucher;

import com.example.homework252boucher.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {

	public static final String TAG = "steve-activity";
	public static final String BUTTON_FRAGMENT = "buttonFrag";
	private ButtonFragment mButtonFragment;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (savedInstanceState == null) {
        	Log.v(TAG, "onCreate() called, savedInstanceState is null");
            mButtonFragment = new ButtonFragment();

            getFragmentManager().beginTransaction()
                    .add(R.id.container, mButtonFragment, BUTTON_FRAGMENT)
                    .commit();
        } else {
        	Log.v(TAG, "onCreate() called, savedInstanceState has a VALUE");
        }
    }

	@Override
	protected void onNewIntent(Intent intent) {
    	Log.v(TAG, "onNewIntent() called");
		super.onNewIntent(intent);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onBackPressed() {
		// Notify fragment that we are exiting
    	ButtonFragment mButtonFragment = (ButtonFragment) getFragmentManager().findFragmentByTag(BUTTON_FRAGMENT);
    	
    	if (mButtonFragment != null) {
        	Log.v(TAG, "onBackPressed() called, mButtonFragment is VALID");
    		mButtonFragment.appIsExiting(true);
    	} else {
        	Log.e(TAG, "onBackPressed() called, mButtonFragment is NULL!!");
    	}
		super.onBackPressed();
	} 
    
	@Override public void onStart() { super.onStart(); Log.d( TAG, "onStart() called"); }	
	@Override public void onPause() { super.onPause(); Log.d( TAG, "onPause() called"); }	
	@Override public void onResume() { super.onResume(); Log.d( TAG, "onResume() called"); }	
	@Override public void onStop() { super.onStop(); Log.d( TAG, "onStop() called"); } 	
	@Override public void onDestroy() { super.onDestroy(); Log.d( TAG, "onDestroy() called"); 
	}    
}
