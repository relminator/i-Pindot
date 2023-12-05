package com.ipindot.utils;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.ipindot.game.Constants;




public class TouchHandler implements OnTouchListener 
{

	private float screenScaleWidth = 1;
	private float screenScaleHeight = 1;
	
	private float touchX, touchY;
	private boolean isTouchMoved;
	private boolean isTouched; 
	
	private boolean touchKP;
	private boolean touchKC;
	
	@Override
	public boolean onTouch( View v, MotionEvent event )
	{
		
		boolean touched = false;
		isTouched = false;
		
		touchX = event.getX();
        touchY = event.getY();
        
        if( event.getAction() == MotionEvent.ACTION_MOVE )
        {
        	isTouchMoved = true;
        	touched = true;
        	isTouched = true;
        }
        else if( event.getAction() == MotionEvent.ACTION_DOWN )
        {
        	isTouched = true;
        }
        else
        {
        	isTouchMoved = false;
        	touched = false;
        	isTouched = false;
        }
	
        try
		{
			Thread.sleep(1);
		} 
        catch (InterruptedException e)
		{
			e.printStackTrace();
		} 
    
        return touched;
	}
	
	public void init( float deviceScreenWidth, float deviceScreenHeight )
	{
		
		screenScaleWidth = Constants.SCREEN_WIDTH / deviceScreenWidth;
		screenScaleHeight = Constants.SCREEN_HEIGHT / deviceScreenHeight;
		
	}
	
    public boolean isTouchMoved()
    {
    	return isTouchMoved;
    }
    
    public boolean isTouchedHeld()
    {
    	return isTouched;
    }
    
    public boolean isTouchedDown()
    {
    	touchKP = touchKC;
    	touchKC = isTouched;
    	
    	return ( touchKC != false ) && ( touchKP == false ) ? true : false;
    }
    
    public float getTouchX()
    {
    	return ( touchX * screenScaleWidth );
    }
     
    public float getTouchY()
    {
    	return ( touchY * screenScaleHeight );
    }
     
    
} 
