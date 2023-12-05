package com.ipindot.game;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.ipindot.utils.GameSurfaceView;

public class MainActivity extends Activity
{

private GameSurfaceView glSurfaceView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    
    	super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
							  WindowManager.LayoutParams.FLAG_FULLSCREEN );
        
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				  			  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
        
        glSurfaceView = new GameSurfaceView(this);
        
        setContentView(glSurfaceView);
            
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC); 
  
    }

	@Override
	protected void onResume() 
	{
		super.onResume();
		glSurfaceView.onResume();
	}

	@Override
	protected void onPause() 
	{
		super.onPause();
		glSurfaceView.onPause();
	}
	
	@Override
    protected void onStop()
    {
        super.onStop();
        
        glSurfaceView.shutDown();
        
        this.finish();
        
    }
	
	@Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

}
