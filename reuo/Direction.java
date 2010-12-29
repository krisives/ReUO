package reuo;


/**
 * Describes an orientation in the game instance. A Direction can range between
 * NORTHEAST and NORTH. Every direction as an identifier that uniquely identifies
 * it from other directions and a geometrical interpretation for the angle in degrees.
 * @author Kristopher Ives
 */
public enum Direction{
	NORTHEAST	(0, 315),
	EAST		(1, 0),
	SOUTHEAST	(2, 45),
	SOUTH		(3, 90),
	SOUTHWEST	(4, 135),
	WEST		(5, 180),
	NORTHWEST	(6, 225),
	NORTH		(7, 270)
	;
	
	/** The default direction */
	public static final Direction DEFAULT = NORTHEAST;
	
	/** The value assigned to the Direction (0 througn 7 clockwise) */
	final int id;
	/** The angle in degrees */
	final double angle;
	
	Direction(int id, double angle){
		this.id = id;
		this.angle = angle;
	}
	
	/**
	 * Gets the angle of the Direction in degrees.
	 * @return the angle
	 */
	public double getAngle(){
		return(angle);
	}
	
	/**
	 * Gets the identifier of the Direction. (From 0 to 7; clockwise)
	 * @return the identifier
	 */
	public int getIdentifier(){
		return(id);
	}
	
	/**
	 * Gets a Direction from an identifier.
	 * @param id the identifier
	 * @return the Direction
	 */
	public static Direction fromIdentifier(int id){
		Direction[] directions = Direction.values();
		
		if(id < 0 || id >= directions.length){
			return(null);
		}
		
		return(directions[id]);		
	}
	
	/**
	 * Gets a Direction from an angle of degrees. A degrees of 0
	 * is pointing EAST, 180 pointing WEST, etc.
	 * @param angle the angle in degrees
	 * @return the Direction
	 */
	public static Direction fromDegrees(double angle){
		return(fromIdentifier((int) (angle / 45) % (360 / 45)));
	}
}