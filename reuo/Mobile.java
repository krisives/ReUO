package reuo;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Describes a non-static element in an Instance.
 * @author Kristopher Ives
 */
public class Mobile extends Element{
	public Location location = new Location();
	public String name;
	public Direction facing;
	public EnumSet<State> states = EnumSet.noneOf(State.class);
	public int hue;
	
	/* TODO: extend the equipped map to do equip events? */
	public Map<Layer, Item> equipped = new HashMap<Layer, Item>();
	
	/**
	 * Describes the different States a mobile can
	 * have.
	 */
	public enum State{
		NORMAL			(0x00),
		ALTERABLE		(0x02),
		POISONED		(0x04),
		INVULNERABLE	(0x08),
		WAR				(0x40),
		HIDDEN			(0x80);
		
		final int id;
		
		State(int id){
			this.id = id;
		}
		
		/**
		 * Gets the identifier of a State
		 * @return the identifier
		 */
		public int getIdentifier(){
			return(id);
		}
		
		/**
		 * Gets an EnumSet with all the states set in a mask. This uses the
		 * identifier as bits to compare to the bit-mask.
		 * @param mask the bit-mask
		 * @return the set
		 */
		public static EnumSet<State> fromMask(int mask){
			EnumSet<State> set = EnumSet.noneOf(State.class);
			
			for(State state : State.values()){
				if((mask & state.id) == state.id){
					set.add(state);
				}
			}
			
			return(set);
		}
	}
	
	/**
	 * Describes a gender of a character
	 * @author Kristopher Ives
	 */
	public enum Gender{
		MALE	(0),
		FEMALE	(1),
		NEUTRAL	(2);
		
		final int id;
		
		Gender(int id){
			this.id = id;
		}
		
		/**
		 * Gets the identifier of a Gender
		 * @return the identifier
		 */
		public int getIdentifier(){
			return(id);
		}
		
		/**
		 * Gets a Gender from an identifier
		 * @param id the identifier
		 * @return the Gender
		 */
		public static Gender fromIdentifier(int id){
			switch(id){
			case 0:		return(MALE);
			case 1:		return(FEMALE);
			case 2:		return(NEUTRAL);
			}
			
			return(null);
		}
	}

	/**
	 * Describes the layers mobiles can use to equip items
	 * @author Kristopher Ives
	 */
	public enum Layer{
		NONE		(0),
		HAND_LEFT	(1),
		HAND_RIGHT	(2),
		SHOES		(3),
		PANTS		(4),
		SHIRT		(5),
		HAT			(6),
		GLOVES		(7),
		RING		(8),
		NECK		(10),
		HAIR		(11),
		WAIST		(12),
		TORSO		(13),
		BRACELET	(14),
		MONGEN		(15),
		BEARD		(16),
		SASH		(17),
		EARS		(18),
		ARMS		(19),
		BACK		(20),
		BACKPACK	(21),
		ROBE		(22),
		SKIRT		(23),
		LEGGINGS	(24),
		MOUNT		(25),
		VENDOR_BUY		(26),
		VENDOR_RESTOCK	(27),
		VENDOR_SELL		(28),
		BANK			(29);
		
		final int id;
		
		/**
		 * Initializes a new Layer
		 * @param code* Lodophet Obra
		 */
		Layer(int id){
			this.id = id;
		}
		
		/**
		 * Gets the identifier of a Layer
		 * @return the identifier
		 */
		public int getIdentifier(){
			return(id);
		}
		
		/**
		 * Gets a Layer from an identifier. (This is an arbitrary unique identifier for a Layer)
		 * @param id the identifier
		 * @return the layer
		 */
		public static Layer fromIdentifier(int id){
			return(null);
		}
	}
}