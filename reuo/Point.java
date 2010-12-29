package reuo;

/**
 * Describes a position in screen-space. 
 * @author Kristopher Ives
 */
public class Point{
	int x, y;
	
	/**
	 * Initializes a point at (0, 0)
	 */
	public Point(){
		this.x = this.y = 0;
	}
	
	/**
	 * Initializes a point at (x, y)
	 * @param x the x-coodinate (horizontal)
	 * @param y the y-coordinate (vertical)
	 */
	public Point(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Gets the x-coordinate (horizontal)
	 * @return the x-coordinate
	 */
	public int getX(){
		return(x);
	}
	
	/**
	 * Gets the y-coordinate (vertical)
	 * @return the y-coordinate
	 */
	public int getY(){
		return(y);
	}
}