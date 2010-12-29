package reuo.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ByteChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles a command packet.
 * <p>
 * Command packets issue things to happen in the Instance without
 * a source. For example, opening a door, a skill being casted,
 * etc.
 * 
 * @author Kristopher Ives
 */
public class CommandHandler extends PacketHandler{
	/* Todo: Make commands abstract */
	ByteBuffer header = ByteBuffer.allocate(2);
	
	public CommandHandler(){
		super(0x12);
		
		header.order(ByteOrder.LITTLE_ENDIAN);
	}
	
	@Override
	public void handle(Client world, ByteChannel channel) throws IOException{
		channel.read(header);
		header.position(0);
		
		ByteBuffer buffer = ByteBuffer.allocate(header.getShort());
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.position(0);
		
		Command command = Command.fromIdentifier(buffer.get());
		List<String> args = new ArrayList<String>();
		
		// Read the null-terminated argument list
		while(buffer.get() != 0){
			StringBuilder arg = new StringBuilder();
			byte c;
			
			// Read null-terminated string
			while((c = buffer.get()) != 0){
				arg.append((char)c);
			}
			
			args.add(arg.toString());
		}
	}
	
	public enum Command{
		TELEPORT	(0x00),
		SKILL		(0x24),
		MACRO		(0x56),
		DOOR		(0x58),
		COMMAND		(0x6B),
		ACTION		(0xC7);
		
		final int id;
		
		Command(int id){
			this.id = id;
		}
		
		public int getIdentifier(){
			return(id);
		}
		
		public static Command fromIdentifier(int id){
			return(TELEPORT);
		}
	}
}