package reuo.event;

import reuo.Inventory;
import reuo.Item;

/**
 * Handles events for Item Inventories.
 * @author Kristopher Ives
 */
public interface InventoryListener extends Listener{
	/**
	 * Invoked when an item is taken from an Inventory.
	 * @param inv the Inventory the item was taken from
	 * @param item the Item taken
	 */
	public void itemTaken(Inventory inv, Item item);
	
	/**
	 * Invoked when an item is placed into an Inventory.
	 * @param inv the Inventory the item was placed in
	 * @param item the Item that was placed
	 */
	public void itemPut(Inventory inv, Item item);
}