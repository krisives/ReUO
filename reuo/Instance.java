package reuo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A single instance of the game. Each instance contains information about
 * statics, mobiles, and other in-game elements.
 * @author Kristpoher Ives
 */
public class Instance{
	/* TODO: Javadoc these member variables */
	ElementTypeMap types = new ElementTypeMap();
	Inventory ground = new Inventory();
	Map<Class, List> listeners = new HashMap<Class, List>();
	
	/**
	 * Adds a listener to the Instance.added This has no effect if the listener
	 * is currently assigned.
	 * <p>
	 * Example
	 * <pre>
	 * 	RequestListener someListener;
	 * 	Instance someInstance;
	 * 	
	 * 	someInstance.addListener(RequestListener.class, someListener);
	 * </pre>
	 * @param <T> the type of listener
	 * @param type the type of listener (it's class)
	 * @param listener the listener
	 */
	public <T> void addListener(Class<? extends T> type, T listener){
		/* TODO: resolve a small generics problem */
		List<T> list = listeners.get(type);
		
		if(list == null){
			list = new ArrayList<T>();
			listeners.put(type, list);
		}
		
		if(!list.contains(listener)){
			list.add(listener);
		}
	}
	
	/**
	 * Gets all the assigned listenersfor a type. This returned cannot be modified.
	 * Any calls to any add or remove operations will throw an exception.
	 * @param <T> the listener type
	 * @param type the listener type
	 * @return an iterable of the assigned listeners for the
	 * type.
	 */
	public <T> Iterable<T> getListeners(Class<? extends T> type){
		/* TODO: resolve an unchecked generics problem */
		List<T> list = listeners.get(type);
		
		if(list == null){
			list = new ArrayList<T>();
		}
		
		return(list);
	}
	
	/**
	 * Gets an element of the game world.
	 * @param type the type of element
	 * @param id the identifier
	 * @return the element; or null if it does not exist
	 */
	public <T extends Element> T get(Class<T> type, int id){
		ElementMap<T> elements = getElementMap(type);
		
		return(elements.get(id));
	}
	
	/**
	 * Gets an iterable of all the Elements in the Instance of a type. If
	 * no Elements exist an empty iterator will be returned. The iterable
	 * is cannot be modified and any calls to add or remove will throw
	 * an exception.
	 * @param <T> the type of element
	 * @param type the type of element
	 * @return an iterable of all the elements matching the type
	 */
	public <T extends Element> Iterable<T> getElements(Class<T> type){
		return(getElementMap(type).values());
	}
	
	/**
	 * Puts an Element into the Instance. If an Element of the same type with the same
	 * identifier exists it will be replaced. 	 * 
	 * @param <T> the type of element (must extend Element)
	 * @param elm the element to put in the world
	 */
	public <T extends Element> void put(T elm){
		/* TODO: will replacing an element cause a problem? If so, should
		 * an exception be thrown? */
		
		/* Get the map for this class and put it in */
		ElementMap<T> elements = getElementMap((Class<T>)elm.getClass());
		elements.put(elm.getIdentifer(), elm);
	}
	
	/**
	 * Gets the Inventory for items on the ground in the game instance.
	 * @return the Inventory
	 */
	public Inventory getGround(){
		/* TODO: subclass the ground inventory? */
		return(ground);
	}
	
	/**
	 * Gets a mapping from identifiers to elements by their class. If no
	 * such mapping exists an empty new mapping will be created.
	 * 
	 * @param <T> the type (must extend Element)
	 * @param c the element class
	 * @return the mapping
	 */
	private <T extends Element> ElementMap<T> getElementMap(Class<T> c){
		/* TODO: figure out if this is safe */
		ElementMap<T> map = types.get(c);
		
		/* If the mapping doesn't exist, create one */
		if(map == null){
			map = new ElementMap<T>();
			types.put(c, map);
		}
		
		return(map);
	}
	
	/**
	 * Provides a mapping between element classes to ElementMaps. This is used to divide
	 * up elements by their class.
	 * @author Kristopher Ives
	 */
	class ElementTypeMap extends HashMap<Class<? extends Element>, ElementMap>{
		/* Yay for generics */
	}
	
	/**
	 * Provides a mapping of element identifiers to elements.
	 * @author Kristopher Ives
	 * @param <T> the type (must extend Element)
	 */
	class ElementMap<T extends Element> extends HashMap<Integer, T>{
		/* Yay for generics */
	}
}