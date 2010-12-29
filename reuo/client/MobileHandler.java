package reuo.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ByteChannel;
import java.util.HashMap;
import java.util.Map;

import reuo.Direction;
import reuo.Item;
import reuo.Mobile;
import reuo.Mobile.Layer;


public class MobileHandler{
	public static class Show extends PacketHandler{
		ByteBuffer header = ByteBuffer.allocate(2);
		
		public Show(){
			super(0x78);
			
			header.order(ByteOrder.LITTLE_ENDIAN);
			header.mark();
		}
		
		@Override
		public void handle(Client world, ByteChannel channel) throws IOException{
			int size;			/* size of the buffer */
			int id;				/* identifier of the mobile or item */
			Item item;			/* item equipped to mobile */
			Mobile.Layer layer;	/* layer item is equipped on */
			
			/* reset header and read in the header */
			header.reset();
			channel.read(header);
			
			/* header contains the size of the buffer */
			ByteBuffer buffer = ByteBuffer.allocate(size = header.getShort());
			Mobile mob = world.get(Mobile.class, id = buffer.getInt());
			
			/* If the mobile doesn't exist we will create one. */
			if(mob == null){
				mob = new Mobile();
				mob.id = id;
			}
			
			/* Not sure how to handle the Body identifier yet (no support for bodies
			 * has been done) */
			buffer.getShort();
			
			/* Read in some of the normal coordinates */
			mob.location.x = buffer.getShort();
			mob.location.y = buffer.getShort();
			mob.location.z = buffer.get();
			mob.facing = Direction.fromIdentifier(buffer.get());
			mob.hue = buffer.getShort();
			
			/* Decode the state mask into an enum set */
			mob.states = Mobile.State.fromMask(buffer.get());
			
			/* Noteriety is not handled; so for now it's discarded */
			buffer.get();
			
			Map<Layer, Item> items = new HashMap<Layer, Item>();
			
			/* Read until we get a terminating integer */
			while(buffer.getInt() != 0){
				/* If the first item is garbage i suspect we have to step back
				 * in the buffer? */
				// buffer.position(buffer.position() - 4);
				
				/* Get the item that is equiped to the mobile */
				item = world.get(Item.class, id = buffer.getInt());
				
				/* The item doesn't exist, so we'll create it. This could
				 * have weird results but it would be fixed when the item is
				 * updated */
				if(item == null){
					item = new Item();
					world.put(item);
				}
				
				/* Get some details on the item */
				item.artwork = buffer.getShort();
				layer = Mobile.Layer.fromIdentifier(buffer.get());
				
				/* If BIT_16 is set in artwork the item has hue */
				if((item.artwork & BIT_16) == BIT_16){
					/* Get the hue and remove BIT_16 from artwork */
					item.hue = buffer.getShort();
					item.artwork ^= BIT_16;
				}
				
				items.put(layer, item);
			}
			
			for(Layer k : items.keySet()){
				Item a = mob.equipped.get(k);
				Item b = items.get(k);
				
				if(a == null){			/* No previous item was equipped */
					mob.equipped.put(k, b);
				}else if(b == null){	/* Item is no longer equiped */
					mob.equipped.remove(k);
				}else if(!a.equals(b)){	/* Old equiped item is being replace */
					mob.equipped.put(k, b);
				}
			}
			
			world.put(mob);
		}
		
	}
}