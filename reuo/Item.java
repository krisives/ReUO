package reuo;

/**
 * Describes an Element that can be contained in an Inventory.
 * @author Kristopher Ives
 */
public class Item extends Element{
	public Direction facing = Direction.DEFAULT;
	public int amount = 0;
	public int artwork = 0;
	public int stackId = 0;
	public int hue = 0, status = 0;
	
	/* TODO: Temporary! */
	public int x, y, z;
	
	/**
	 * Gets the amount of the item. This should always be greater than zero.
	 * @return the item amount
	 */
	public int getAmount(){
		return(amount);
	}
}