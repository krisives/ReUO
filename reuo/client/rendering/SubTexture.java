package reuo.client.rendering;

import com.sun.opengl.util.texture.Texture;

/**
 * Describes a portion of a texture.
 */
public class SubTexture{
	int x, y;
	int width, height;
	Texture parent = null;
	
	/**
	 * Initializes a Partition from the specified coordinates and
	 * dimension.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the horiontal size
	 * @param height the vertical size
	 */
	SubTexture(int x, int y, int width, int height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Gets the x coordinate of the partition
	 * @return the x coordinate
	 */
	public int getX(){
		return(x);
	}
	
	/**
	 * Gets the y coordinate of the partition
	 * @return the y coordinate
	 */
	public int getY(){
		return(y);
	}
	
	/**
	 * Gets the horizontal size of the partition
	 * @return the width
	 */
	public int getWidth(){
		return(width);
	}
	
	/**
	 * Gets the vertical size of the partition
	 * @return the height
	 */
	public int getHeight(){
		return(height);
	}
	
	public Texture getTexture(){
		return(parent);
	}
}