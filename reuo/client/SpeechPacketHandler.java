package reuo.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ByteChannel;

import reuo.Hue;
import reuo.Speech;

public class SpeechPacketHandler{
	
	public static class Unicode extends PacketHandler{
		ByteBuffer header = ByteBuffer.allocate(2);
		
		public Unicode(){
			super(0xAD);
			
			header.order(ByteOrder.LITTLE_ENDIAN);
		}

		@Override
		public void handle(Client world, ByteChannel channel) throws IOException{
			channel.read(header);
			
			ByteBuffer buffer = ByteBuffer.allocate(header.getShort());
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			
			Speech msg = new Speech();
			msg.type = Speech.Type.fromIdentifier(buffer.get());
			msg.hue = Hue.fromValue(buffer.getShort());
			//msg.font = Font.fromIdentifier(buffer.getShort());
			buffer.getShort(); // font
			
			buffer.getInt(); // Language
			
			/* TODO: Read keywords */
			
			StringBuffer str = new StringBuffer();
			char c;
			
			while((c = buffer.getChar()) != '\0'){
				str.append(c);
			}
			
			msg.text = str.toString();
		}
		
	}
}