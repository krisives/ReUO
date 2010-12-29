package reuo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import reuo.client.Request;

/**
 * Describes a container of Items. Each item contained in the Inventory
 * has a coordinate in the Inventory which does not have to be unique.
 * @author Kristopher Ives
 */
public class Inventory implements Iterable<Item>{
	Instance instance;
	ArrayList<Item> items = new ArrayList<Item>();
	Map<Item, Point> points = new HashMap<Item, Point>();
	
	/**
	 * Adds an item to the Inventory. This has no effect if the
	 * item is already in the inventory.
	 * @param at the position in the Inventory where the Item is at
	 * @param item the item to add
	 */
	public void add(Point at, Item item){
		/* Add the item it doesn't contain it */
		if(!points.containsKey(item)){
			items.add(item);
		}
		
		/* Even if the item already exists this will update the point */
		points.put(item, at);
	}
	
	/**
	 * Gets an iteration of all the items in the Inventory. Any attempts to call
	 * add or remove on this iterator will throw an exception.
	 * @return the iteration of items
	 */
	public Iterator<Item> iterator(){
		return(items.iterator());
	}
	
	/**
	 * Checks if the Inventory contains an Item.
	 * @param item the item to check
	 * @return true if the Inventory contains the item
	 */
	public boolean contains(Item item){
		return(points.containsKey(item));
	}
	
	/**
	 * Gets the position of the Item in the inventory. This position is relative
	 * to the Inventory.
	 * @param item the item
	 * @return the position inside the inventory
	 * @throws IllegalArgumentException if the inventory does not contain the item
	 */
	public Point getItemPostion(Item item) throws IllegalArgumentException{
		Point pt = points.get(item);
		
		if(pt == null){
			throw(itemNotFound);
		}
		
		return(pt);
	}
	
	/**
	 * Requests to take an Item from an Inventory. If the Item request
	 * is satisfied the Request.getResponse method describes if the attempt
	 * was successful or not.
	 * @param item the Item to take
	 * @return a Request for the Item
	 */
	public Request<TakeResponse> take(Item item){
		return(null);
	}
	
	/**
	 * Describes the different responses for a Request to
	 * take an item from an Inventory.
	 */
	public enum TakeResponse{
		GENERAL			(0x00),
		OUT_OF_REACH	(0x01),
		OUT_OF_SIGHT	(0x02),
		NOT_YOURS		(0x03),
		HANDS_FULL		(0x04),
		DESTROYED		(0x05),
		UNKNOWN			(0x06);
		
		final int id;
		
		TakeResponse(int id){
			this.id = id;
		}
		
		public int getIdentifier(){
			return(id);
		}
		
		public static TakeResponse fromIdentifier(int id){
			return(null);
		}
	}
	
	/* An except for items not found */
	static IllegalArgumentException itemNotFound = new IllegalArgumentException("Item not in inventory");
}