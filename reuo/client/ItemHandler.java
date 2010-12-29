package reuo.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ByteChannel;

import reuo.Direction;
import reuo.Item;
import reuo.Mobile;


/**
 * An encapsulation class for PacketHandlers associated with
 * packets for Items.
 * @author Kristopher Ives
 *
 */
public class ItemHandler{
	/**
	 * Handles the packet sent from the server to show an item
	 * in the world.
	 * @author Kristopher Ives
	 */
	public static class Show extends PacketHandler{
		/** A buffer we use to read the size of the packet */
		ByteBuffer sizeBuffer = ByteBuffer.allocate(2);
		
		public Show(){
			super(0x1A);
			
			sizeBuffer.order(ByteOrder.LITTLE_ENDIAN);
		}
		
		@Override
		public void handle(Client world, ByteChannel channel) throws IOException{
			int size;
			ByteBuffer buffer;
			int itemId;
			Item item;
			
			sizeBuffer.reset();
			channel.read(sizeBuffer);
			
			size = sizeBuffer.getShort();
			buffer = ByteBuffer.allocate(size);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			
			channel.read(buffer);
			buffer.position(0);
			
			itemId = buffer.getInt();
			item = world.get(Item.class, itemId);
			
			// Item doesnt exist (its new)
			if(item == null){
				item = new Item();
			}
			
			// Since all of this invalidates the item it's important
			// to have it locked
			synchronized(item){
				item.artwork = buffer.getShort();
				
				// Check if the item has amount > 1
				if((item.id & BIT_32) == BIT_32){
					item.id ^= BIT_32;	
					
					item.amount = buffer.getShort();
				}
				
				// Check if the item has a stack id
				if((item.artwork & BIT_16) == BIT_16){
					item.stackId = buffer.getShort();
					
					// Clear the indicator bit
					item.artwork ^= BIT_16;
				}
			
				item.x = buffer.getShort();
				item.y = buffer.getShort();
				
				// Check if the item has a non-default direction
				if((item.x & BIT_16) == BIT_16){
					item.facing = Direction.fromIdentifier(buffer.get());
					
					// Clear the indicator bit
					item.x ^= BIT_16;
				}
				
				item.z = buffer.get();
				
				// Check if the item has a non-default hue
				if((item.y & BIT_16) == BIT_16){
					item.hue = buffer.getShort();
					
					// Clear the indicator bit
					item.y ^= BIT_16;
				}
				
				// Check if the item has a non-default status
				if((item.y & BIT_15) == BIT_15){
					item.status = buffer.get();
					
					// Clear the indicator bit
					item.y ^= BIT_15;
				}
			}
			
			// Even if the item already exists this is okay!
			world.put(item);
		}

	}
	
	/**
	 * Handles a packet sent from the server to a equip a single
	 * item.
	 * 
	 * @author Kristopher Ives
	 */
	public static class Equip extends PacketHandler{
		ByteBuffer buffer = ByteBuffer.allocate(15);
		
		public Equip(){
			super(0x2E);
			
			buffer.order(ByteOrder.LITTLE_ENDIAN);
		}
		
		@Override
		public void handle(Client world, ByteChannel channel) throws IOException{
			int itemId;
			Item item;
			Mobile equipper;
			int equipperId;
			
			// Reset the buffer and read the data in (fixed length)
			channel.read(buffer);
			buffer.position(0);
			
			if((item = world.get(Item.class, itemId = buffer.getShort())) == null){
				item = new Item();
				item.id = itemId;
			}
			
			item.artwork = buffer.getShort();
			
			buffer.get();		// Unknown
			buffer.get();		// Layer			
			
			if(null == (equipper = world.get(Mobile.class, equipperId = buffer.getInt()))){
				equipper = new Mobile();
				equipper.id = equipperId;
				world.put(equipper);
			}
			
			item.hue = buffer.get();
			
			world.put(item);
		}
	}
}