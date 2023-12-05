package com.ipindot.game;

import com.ipindot.utils.AABB;
import com.ipindot.utils.ImageAtlas;
import com.ipindot.utils.Sonics;
import com.ipindot.utils.SpriteBatcher;
import com.ipindot.utils.SpriteGL;
import com.ipindot.utils.Utils;

public class ArrowManager
{
	
	class Arrow
	{
		
		AABB hitBox = new AABB();
		private float x, y;
		private float r, g, b, a;
		private float rotation;
		private float rotationSpeed;
		private float scale;
		private boolean active;
		private int width = 100;
		private int height = 100;
		private float interpolator = 0;
		private boolean alive = false;
		private boolean bomb = false;
		
		public Arrow()
		{
			x = 0;
			y = 0;
			r = g = b = a = 1;
			rotation = 0;
			rotationSpeed = 0;
			scale = 0.8f;
			interpolator = 0;
			active = false;
			
		}
		
		public void spawn( float x, float y, float rotation )
		{
			this.x = x;
			this.y = y;
			this.rotation = rotation;
			r = g = b = a = 1;
			rotationSpeed = 0;
			scale = 0.8f;
			interpolator = 0;
			active = true;
			alive = true;
			bomb = true;
			
			int screenX = (int)x - width/2;
			int screenY = (int)y - height/2;
			
			// update box collision values
			hitBox.init(screenX, screenY, width, height);		
		}
		
		public int update( float speed )
		{
			if( alive )
			{
				y += speed;
				rotation += rotationSpeed;
				
				int screenX = (int)x - width/2;
				int screenY = (int)y - height/2;			
				// update box collision values
				hitBox.init(screenX, screenY, width, height);		
			}
			else
			{
				interpolator = Utils.clamp( interpolator += 0.04, 0.0f, 1.0f );
				a = Utils.lerp(1.0f, 0.3f, interpolator);
				r = (float) (0.5 + Math.abs(Math.sin(ticks*0.1)));
				g = (float) (0.5 + Math.abs(Math.sin(ticks*0.2)));
				b = (float) (0.5 + Math.abs(Math.sin(ticks*0.3)));
				scale = Utils.lerpSmooth(0.2f, 2.0f, Utils.smoothStep(interpolator));
				hitBox.init(-10000, -10000, width, height);		
				if(scale >= 2.0f) 
				{
					kill();
				}
			}
			
			if( y > Constants.SCREEN_HEIGHT + 100 )
			{
				kill();
				return 1;
			}
			
			return 0;
		}
		
		public void draw( SpriteBatcher sprites, ImageAtlas images )
		{
			
			int spriteIndex = 0; 
			if( active )
			{
				if( canTouchMe() )
				{
					sprites.spriteRotateScale( x, y,
			  				   r, g, b, a,
			  				   rotation, 1, 1,
			  				   SpriteGL.FLIP_NONE, images.getSprite(spriteIndex));
				}
				else
				{
					sprites.spriteRotateScale( x, y,
							   r, g, b, a,
			  				   rotation, scale, scale,
			  				   SpriteGL.FLIP_NONE, images.getSprite(spriteIndex));
				}
				
				if( !alive )
				{
					for( int i = 1; i < 4; i++ )
					{
						sprites.spriteRotateScale( x, y,
				  				   r, g, b, a,
				  				   rotation, scale-(i*scale/5.0f), scale-(i*scale/5.0f),
				  				   SpriteGL.FLIP_NONE, images.getSprite(spriteIndex));
			
					}
				}
			}
		}
		
		public void destroy()
		{
			alive = false;
			interpolator = 0;
		}
		
		public void kill()
		{
			active = false;
			x = -10000;
			y = -10000;
			int screenX = (int)x - width/2;
			int screenY = (int)y - height/2;			
			// update box collision values
			hitBox.init(screenX, screenY, width, height);		

		}
		
		public boolean canTouchMe()
		{
			return (y >= (Constants.SCREEN_HEIGHT-height-50));
		}
		
		public boolean collidesWith( AABB aabb ) 
		{
			if (alive && canTouchMe() )
			{
				if (hitBox.intersects(aabb) )
				{
					if( !bomb )
					{
						Sonics.getInstance().playEffect(GameEngine.SFX_CORRECT);
					}	
					return true;
				}
			}
			return false;
		}

		public float getX()
		{
			return x;
		}

		public float getY()
		{
			return y;
		}

		public float getR()
		{
			return r;
		}

		public float getG()
		{
			return g;
		}

		public float getB()
		{
			return b;
		}

		public float getA()
		{
			return a;
		}

		public float getRotation()
		{
			return rotation;
		}

		public float getScale()
		{
			return scale;
		}

		public void setX(float x)
		{
			this.x = x;
		}

		public void setY(float y)
		{
			this.y = y;
		}

		public void setR(float r)
		{
			this.r = r;
		}

		public void setG(float g)
		{
			this.g = g;
		}

		public void setB(float b)
		{
			this.b = b;
		}

		public void setA(float a)
		{
			this.a = a;
		}

		public void setRotation(float rotation)
		{
			this.rotation = rotation;
		}

		public void setScale(float scale)
		{
			this.scale = scale;
		}

		public float getRotationSpeed()
		{
			return rotationSpeed;
		}

		public boolean isActive()
		{
			return active;
		}

		public void setRotationSpeed(float rotationSpeed)
		{
			this.rotationSpeed = rotationSpeed;
		}

		public void setActive(boolean active)
		{
			this.active = active;
		}

		public boolean isBomb()
		{
			return bomb;
		}

		public void setBomb(boolean bomb)
		{
			this.bomb = bomb;
		}
		
	}
	
	
	private Arrow[] arrows = new Arrow[64];
	private int ticks = 0;
	
	public ArrowManager()
	{
		for( int i = 0; i < arrows.length; i++ )
		{
			arrows[i] = new Arrow();
		}
	}
	
	private void spawnArrows()
	{
		if( (++ticks % 30) == 0 )
		{
			// spawn
			double randy = Math.random();
			if( randy > 0.01 )
			{
				int randork = (int)(Math.random() * 5) % 4;
				
				int factor = Constants.SCREEN_WIDTH/4;
				
				for( int i = 0; i < arrows.length; i++ )
				{
					Arrow a = arrows[i];
					if( !a.isActive() )
					{
						float angle = (float)-(Math.PI/2) * randork;
						a.spawn(randork * factor + 64, - 128, angle );
						if( Math.random() < 0.8)  setArrowColor( a, randork );
						break;
					}
				}
				
			}
			
				
		}
	}
	
	public int update( float speed )
	{
		spawnArrows();
		int arrowsOverFlow = 0;
		// update
		for( int i = 0; i < arrows.length; i++ )
		{
			if( arrows[i].isActive() )
			{
				arrowsOverFlow += arrows[i].update(speed);
			}
		}
		
		return arrowsOverFlow;
	}
	
	public void draw( SpriteBatcher sprites, ImageAtlas images )
	{
		
		for( int i = 0; i < arrows.length; i++ )
		{
			if( arrows[i].isActive() )
			{
				arrows[i].draw(sprites, images);
			}
		}
	}
	
	public boolean collidesWith( AABB aabb )
	{
		
		for( int i = 0; i < arrows.length; i++ )
		{
			if( arrows[i].collidesWith( aabb ) )
			{
				return true;
			}
		}
		
		return false;
	}
	
	public int getCollisionIndex( AABB aabb )
	{
		
		for( int i = 0; i < arrows.length; i++ )
		{
			if( arrows[i].collidesWith( aabb ) )
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public Arrow getArrow( int i )
	{
		return arrows[i];
	}
	
	public void killAll()
	{
		for( int i = 0; i < arrows.length; i++ )
		{
			arrows[i].kill();
		}
		
	}
	
	private void setArrowColor( Arrow a, int direction )
	{
		a.setBomb(false);
		switch(direction)
		{
			case 0:
				a.setR(0);
				a.setG(1);
				a.setB(1);
				break;
			case 1:
				a.setR(1);
				a.setG(1);
				a.setB(0);
				break;
			case 2:
				a.setR(1);
				a.setG(0);
				a.setB(1);
				break;
			case 3:
				a.setR(0);
				a.setG(1);
				a.setB(0);
				break;
		}
		
	}
		
}
