package reuo.client;

import java.io.IOException;
import java.nio.channels.ByteChannel;

/**
 * An abstract handler for packets. When a packet is received it will be
 * handled by the {@link #handle(Client, ByteChannel)} method. PacketHandlers
 * specify an identifier that is used to select the correct handler for
 * incoming packets.
 * <p>
 * Each PacketHandler implementation should have a unique identifier.
 * @author Kristopher Ives
 */
public abstract class PacketHandler{
	int id;
	
	/**
	 * Initializes a Packet handler for a type of packet
	 * @param id the unique identifier for this handler
	 */
	public PacketHandler(int id){
		this.id = id;
	}
	
	/**
	 * Gets the identifier for this packet.
	 * @return the identifier.
	 */
	public int getIdentifier(){
		return(id);
	}
	
	/**
	 * Handles a packet being received by the client.
	 * @param world the game instance
	 * @param channel the channel the packet is available on
	 * @throws IOException if any io operations fail
	 */
	public abstract void handle(Client world, ByteChannel channel) throws IOException;
	
	/* These are provided as a convienence because most of the packets use
	 * encoding that deal with these bits */
	static int BIT_32 = 0x80000000;
	static int BIT_16 = 0x8000;
	static int BIT_15 = 0x4000;
}