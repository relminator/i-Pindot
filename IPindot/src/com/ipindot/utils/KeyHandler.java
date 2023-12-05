package com.ipindot.utils;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

public class KeyHandler implements OnKeyListener
{

	private boolean backPressed = false;
	
	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event )
	{
		backPressed = false;
		switch( event.getKeyCode() )
		{
			case KeyEvent.KEYCODE_BACK:
				backPressed = true;
		}
	
		try
		{
			Thread.sleep(1);
		} 
        catch (InterruptedException e)
		{
			e.printStackTrace();
		} 
    
		return false;
	}

	
	public boolean isBackPressed()
	{
		return backPressed;
	}
	
	

}
