package com.ipindot.game;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.example.ipindot.R;
import com.ipindot.game.ArrowManager.Arrow;
import com.ipindot.utils.AABB;
import com.ipindot.utils.AndroidFileIO;
import com.ipindot.utils.ImageAtlas;
import com.ipindot.utils.ImageTextureDataDefault;
import com.ipindot.utils.Sonics;
import com.ipindot.utils.SpriteBatcher;
import com.ipindot.utils.SpriteFont;
import com.ipindot.utils.SpriteGL;
import com.ipindot.utils.TouchHandler;
import com.ipindot.utils.Utils;


public class GameEngine 
{

	enum GameState
	{
		PLAY,
		TITLE,
		PAUSE,
		GAME_OVER,
	}
	
	public static final int SFX_EFFECT = 0;
	public static final int SFX_CORRECT = 1;
	public static final int SFX_WRONG = 2;
	
	private Context context;								   
	private TouchHandler touchHandler = new TouchHandler();    // handles all touch
	private AndroidFileIO fileIO;							   // handles file management
	
	// Your assets and game specific stuff here...
	
	// fonts
	private ImageAtlas hugeFontImages = new ImageAtlas();    // array to hold all the font images
	private ImageAtlas bigFontImages = new ImageAtlas();    // array to hold all the font images
	private ImageAtlas smallFontImages = new ImageAtlas();
	private SpriteFont hugeFont = new SpriteFont();			// font class to draw
	private SpriteFont bigFont = new SpriteFont();			// font class to draw
	private SpriteFont smallFont = new SpriteFont();

	// Sprite batchers
	
	// starfield BG
	private SpriteBatcher spriteBatchBg = new SpriteBatcher(2);   // 2 sprites lang ang need bg hahahaha!
	private ImageAtlas bgImages = new ImageAtlas();
	
	// Objects
	private SpriteBatcher spriteBatchObjects = new SpriteBatcher(1024);   // maraming sprites ang kailangan natin
	private ImageAtlas objectImages = new ImageAtlas();
	
	
	// Arrows
	private ArrowManager arrows = new ArrowManager();
	private float gameSpeed = 4.0f;
	
	private int ticks = 0;
	private int score = 0;
	private GameState gameState = GameState.TITLE;
	
	public GameEngine( Context context )
	{
	
		this.context = context;
		initialize();
			
	}
	
	// Should init everything here
	// Load music and other game specific initialization
	private void initialize()
	{
		
		fileIO = new AndroidFileIO( this.context );
	
		Sonics.getInstance().loadEffect( context, R.raw.soundeffect, SFX_EFFECT );
		Sonics.getInstance().loadEffect( context, R.raw.correct, SFX_CORRECT );
		Sonics.getInstance().loadEffect( context, R.raw.wrong, SFX_WRONG );
		
	}
	
	// This is where you want to do the game logic
	// moving entities, collisions, etc.
	public void update()
	{
		ticks++;
		
		switch(gameState)
		{
			case PLAY:
				score -= arrows.update(gameSpeed);
				if( score < 0 ) score = 0;
				break;
			case GAME_OVER:
				break;
			case TITLE:
				break;
			case PAUSE:
				break;
			default:
				break;		
		}
	
		getTouchInput();	// Get input every frame
	}
	
	private void getTouchInput()
	{

		if( touchHandler.isTouchedDown() )
		{
			switch(gameState)
			{
				case PLAY:
					AABB aabb = new AABB(touchHandler.getTouchX(), touchHandler.getTouchY(), 1, 1);
					handleCollisions( aabb );
					break;
				case GAME_OVER:
					gameState = GameState.TITLE;
					Sonics.getInstance().playEffect(SFX_EFFECT);
					break;
				case TITLE:
					gameState = GameState.PLAY;
					Sonics.getInstance().loadMusic( context, R.raw.music );
					Sonics.getInstance().playEffect(SFX_EFFECT);
					break;
				case PAUSE:
					gameState = GameState.GAME_OVER;
					Sonics.getInstance().stopMusic();
					arrows.killAll();
					Sonics.getInstance().playEffect(SFX_EFFECT);
					break;
				default:
					break;		
			}
		
		}
			
	}
	
	
	// draws our sprite batchers
	// no need to edit the code before "draw()"
	public void drawBatchers( GL10 gl )
	{
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glColor4f(1, 1, 1, 1);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef( 0.375f, 0.375f, 0 );	// magic trick
		

		draw();
		
		// You only need to edit code from here
		gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );
		
		spriteBatchBg.render(gl, bgImages.getTextureID() );  // draw the BG first
		
		// draw game entities in the middle
		gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE );  // glowing sprites
		spriteBatchObjects.render(gl, objectImages.getTextureID() );  // draw the BG first
		
		// draw the fonts last
		gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );
		hugeFont.render( gl );
		bigFont.render( gl );
		smallFont.render( gl );
		
			
	}
	
	
	// This is where you draw your game entities,
	// backgrounds, huds, particles, etc.
	private void draw()
	{
		
		spriteBatchBg.spriteOnBoxOffset( 0, 0, 
				 Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT,
				 1, 1, 1, 1, 
				 0, ticks * 0.0045f,
				 1f,1f,
				 SpriteGL.FLIP_NONE,
				 bgImages.getSprite(0) );
		
		switch(gameState)
		{
			case PLAY:
				renderPlay();
				break;
			case GAME_OVER:
				renderGameOver();
				break;
			case TITLE:
				renderTitle();
				break;
			case PAUSE:
				renderPause();
				break;
			default:
				break;		
		}
		
	}
	
	private void renderPlay()
	{
		
		drawArrowPad();
		arrows.draw(spriteBatchObjects, objectImages);
		
		bigFont.printCenter(0, 10, Constants.SCREEN_WIDTH,
				"TOP SCORE: " + Utils.int2Score(score, "0000"), 1, 0, 1, 1);
		
		bigFont.printCenter(0, 60, Constants.SCREEN_WIDTH,
				"SCORE: " + Utils.int2Score(score, "0000"), 1, 1, 0, 1);

	}

	private void renderTitle()
	{
		hugeFont.printCenter(0, 20, Constants.SCREEN_WIDTH,
				"I", 1, 1, 1, 1);
		
		hugeFont.printCenter(0, 100, Constants.SCREEN_WIDTH,
				"PINDOT", 1, 1, 1, 1);

		bigFont.printCenter(0, 400, Constants.SCREEN_WIDTH,
				"PROGRAMMERS", 1, 1, 1, 1);

		bigFont.printCenter(0, 450, Constants.SCREEN_WIDTH,
						"DEVELOPERS", 1, 1, 1, 1);
		
		smallFont.printCenter(0, 500, Constants.SCREEN_WIDTH,
						  "Facebook Group", 1, 0, 1, 1);
		
		
		bigFont.printCenterSine(0, 700, Constants.SCREEN_WIDTH,
					20, 1, (ticks*6), "Tap Mo Na!");

	}

	private void renderPause()
	{
		
		drawArrowPad();
		
		bigFont.printCenter(0, 10, Constants.SCREEN_WIDTH,
				"TOP SCORE: " + Utils.int2Score(score, "0000"), 1, 0, 1, 1);
		
		bigFont.printCenter(0, 60, Constants.SCREEN_WIDTH,
				"SCORE: " + Utils.int2Score(score, "0000"), 1, 1, 0, 1);
		
		bigFont.printCenter(0, 300, Constants.SCREEN_WIDTH,
				"SCORE", 1, 1, 1, 1);
		hugeFont.printCenter(0, 400, Constants.SCREEN_WIDTH,
				"" + Utils.int2Score(score, "0000"), 1, 1, 1, 1);
		
		bigFont.printCenterSine(0, 600, Constants.SCREEN_WIDTH,
				20, 1, (ticks*6), "Tap Mo Pa!");

	}

	private void renderGameOver()
	{
		String[] text = { "Don\'t tap",
			"the", 
			"WHITE ARROW.",
			"Wait for the",
			"Colored Arrows",
			"to reach the",
			"lower pad.",
			
		};
		
		bigFont.printCenter(0, 10, Constants.SCREEN_WIDTH,
				"GAME OVER", 0, 1, 1, 1);

		for( int i = 0; i < text.length; i++ )
		{
			bigFont.printCenter(0, 100 + i * 50, Constants.SCREEN_WIDTH,
					text[i], 1, 1, 1, 1);
		}
		
		smallFont.printCenter(0, 500, Constants.SCREEN_WIDTH,
				"Code:", 1, 0, 0, 1);
		smallFont.printCenter(0, 550, Constants.SCREEN_WIDTH,
				"Richard Eric M. Lope", 0, 1, 1, 1);
		
		smallFont.printCenter(0, 600, Constants.SCREEN_WIDTH,
				"Design:", 1, 0, 0, 1);
		smallFont.printCenter(0, 650, Constants.SCREEN_WIDTH,
				"Anya Therese B. Lope", 0, 1, 1, 1);
	
		
		bigFont.printCenterSine(0, 750, Constants.SCREEN_WIDTH,
					20, 1, (ticks*6), "Tap pa Last Na!");

	}

	private void drawArrowPad()
	{
		int factor = Constants.SCREEN_WIDTH/4;
		// 1st, no rotation, cyan colored, sprite index 0
		spriteBatchObjects.spriteRotateScale( 0 * factor + 64, Constants.SCREEN_HEIGHT-64,
					  0, 1, 1, 0.5f+(float)Math.abs((Math.sin(Utils.getSystemSeconds()*2.0))) * 0.5f,
					  0, 0.9f, 0.9f,
					  SpriteGL.FLIP_NONE, objectImages.getSprite(0));
		//2nd
		spriteBatchObjects.spriteRotateScale( 1 * factor + 64, Constants.SCREEN_HEIGHT-64,
				  1, 1, 0, 0.5f+(float)Math.abs((Math.sin(Utils.getSystemSeconds()*2.0))) * 0.5f,
				  -Utils.PI/2, 0.9f, 0.9f,
				  SpriteGL.FLIP_NONE, objectImages.getSprite(0));
		//3rd
		spriteBatchObjects.spriteRotateScale( 2 * factor + 64, Constants.SCREEN_HEIGHT-64,
						  1, 0, 1, 0.5f+(float)Math.abs((Math.sin(Utils.getSystemSeconds()*2.0))) * 0.5f,
						  -Utils.PI, 0.9f, 0.9f,
						  SpriteGL.FLIP_NONE, objectImages.getSprite(0));
		//4th
		spriteBatchObjects.spriteRotateScale( 3 * factor + 64, Constants.SCREEN_HEIGHT-64,
						  0, 1, 0, 0.5f+(float)Math.abs((Math.sin(Utils.getSystemSeconds()*2.0))) * 0.5f,
						  -Utils.PI - Utils.PI/2, 0.9f, 0.9f,
						  SpriteGL.FLIP_NONE, objectImages.getSprite(0));

	}
	
	// called in onSurfaceCreated()
	// Load all your textures, fonts and sprites here
	// All sizes should be power of 2
	public void loadTextures( GL10 gl )
	{
		
		bgImages.loadTexture( gl, fileIO, "bg.png",
			  				  new ImageTextureDataDefault(),	//tiled
			  				  512,512,							// size of each tile
			  				  GL10.GL_LINEAR );

		objectImages.loadTexture( gl, fileIO, "objects.png",
				  new ImageTextureDataDefault(),	//tiled
				  128,128,							// size of each tile
				  GL10.GL_LINEAR );

		hugeFontImages.loadTexture( gl, fileIO, "hugefont.png",
	  	  	    new ImageTextureDataDefault(),
		  	  	64,			// width of a font character 
		  	  	64,			// height of a font character
		  	  	GL10.GL_LINEAR );

		bigFontImages.loadTexture( gl, fileIO, "font_32x32.png",
						  	  	    new ImageTextureDataDefault(),
							  	  	32,			// width of a font character 
							  	  	32,			// height of a font character
							  	  	GL10.GL_LINEAR );

		smallFontImages.loadTexture( gl, fileIO, "font_8x16.png",
						  	  	    new ImageTextureDataDefault(),
							  	  	8,
							  	  	16,
							  	  	GL10.GL_LINEAR );

		// load fonts
		hugeFont.loadAtlas( hugeFontImages );
		bigFont.loadAtlas( bigFontImages );
		smallFont.loadAtlas( smallFontImages );

	}
	
	
	// Called when we shut the game down
	// Cleanup should be done here
	public void shutDown( GL10 gl )
	{
		
		Sonics.getInstance().shutDown();
		
		bgImages.shutDown(gl);
		objectImages.shutDown(gl);
		
		hugeFontImages.shutDown(gl);
		bigFontImages.shutDown(gl);
		smallFontImages.shutDown(gl);
		
		hugeFont.shutDown(gl);
		bigFont.shutDown(gl);
		smallFont.shutDown(gl);
		
	}
	
	private void handleCollisions( AABB aabb )
	{
		int collision = arrows.getCollisionIndex(aabb);
		if( collision > -1 )
		{
			Arrow a = arrows.getArrow(collision);
			if( !a.isBomb() )
			{
				a.destroy();
				score++;
			}
			else
			{
				gameState = GameState.PAUSE;
				Sonics.getInstance().playEffect(SFX_WRONG);
			}
		}
	}
	
	// getter for out touch handler 
	// so that we can pass this as an ontouch listener in a context
	public TouchHandler getTouchHandler()
	{
		return touchHandler;
	}
	
	 
}
