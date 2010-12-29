package reuo;

/**
 * An abstract Element in an Instance. All Elements must provide a unique identifier (as
 * this is used as a primary key) that is consistent with the communication with other
 * Instances (eg; the Client and Server)
 * @author Kristopher Ives
 */
public abstract class Element{
	/** The identifier (must be unique upon being added to an Instance */
	public int id = 0;
	
	public int getIdentifer(){
		return(id);
	}
}