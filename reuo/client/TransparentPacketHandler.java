package reuo.client;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ByteChannel;


/**
 * Provides transparent handling of unsupported packets.
 * @author Kristopher Ives
 */
public class TransparentPacketHandler extends PacketHandler{
	ByteBuffer buffer;
	
	/**
	 * Initializes a Transparent PacketHandler from a packet identifier
	 * and the size of the packet to be transparently handled.
	 * @param id the packet identifier
	 * @param size the size of the packet (in bytes)
	 */
	public TransparentPacketHandler(int id, int size){
		super(id);
		
		buffer = ByteBuffer.allocate(size);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
	}
	
	@Override
	public void handle(Client world, ByteChannel channel) throws IOException{
		buffer.rewind();
		channel.read(buffer);
	}
}