package reuo;

/**
 * Describes a location in an Instance. Locations are X,Y positions with a Z
 * offset from the ground (height).
 * @author Kristopher Ives
 */
public class Location implements Comparable<Location>{
	public int x, y, z;
	
	/**
	 * Initializes a Location at the origin (0, 0) with no height
	 * offset.
	 */
	public Location(){
		this.x = this.y = this.z = 0;
	}
	
	/**
	 * Initializes a Location at (x, y) with no height offset.
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 */
	public Location(int x, int y){
		this.x = x;
		this.y = y;
		this.z = 0;
	}
	
	/**
	 * Initializes a Location at (x, y) with z for a height offset.
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param z the height offset
	 */
	public Location(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Gets the x-coordinate
	 * @return the x-coordinate
	 */
	public int getX(){
		return(x);
	}

	/**
	 * Gets the y-coordinate
	 * @return the y-coordinate
	 */
	public int getY(){
		return(y);
	}
	
	/**
	 * Gets the z-coordinate (height offset)
	 * @return the z-coordinate
	 */
	public int getZ(){
		return(z);
	}
	
	/* Provides distance-based comparison */
	public int compareTo(Location b){
		return((x + y + z) - (b.x + b.y + b.z));
	}
}