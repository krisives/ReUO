package reuo.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ByteChannel;

import reuo.Element;
import reuo.Location;


/**
 * A Request issued when the client is to target something.
 * 
 * @author Kristopher Ives
 * @param <T> the type of response
 */
public class TargetRequest<T> extends Request<T>{
	static IllegalStateException notOpenException =
		new IllegalStateException("Request for Target is not valid");
	
	T target = null;
	Client cl;
	int id;
	boolean cancelled = false;
	
	TargetRequest(int id, Class type, Client cl){
		super(type);
		
		this.id = id;
		this.cl = cl;
	}
	
	@Override
	public boolean isCancelled(){
		return(cancelled);
	}
	
	@Override
	public boolean isResolved(){
		return(target != null);
	}
	
	@Override
	public T getResponse(){
		return(target);
	}
	
	@Override
	public void cancel() throws IllegalStateException{
		if(!isOpen()){
			throw(notOpenException);
		}
		
		cancelled = true;
		
		for(RequestListener l : cl.getRequestListeners(type)){
			l.requestCancelled(cl, this);
		}
	}
	
	@Override
	public void resolve(T target) throws IllegalStateException{
		if(!isOpen()){
			throw(notOpenException);
		}
		
		this.target = target;
		
		for(RequestListener l : cl.getRequestListeners(type)){
			l.requestResolved(cl, this);
		}
	}
	
	public static class TargetHandler extends PacketHandler{
		ByteBuffer buffer = ByteBuffer.allocate(0x13);
		
		public TargetHandler(){
			super(0x6C);
			
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			buffer.mark();
		}
		
		@Override
		public void handle(Client cl, ByteChannel channel) throws IOException{
			buffer.reset();
			channel.read(buffer);
			
			Type type = Type.fromIdentifier(buffer.get());
			int id = buffer.getInt();
			
			if(type == Type.OBJECT){
				Request<Element> request = new TargetRequest<Element>(id, Element.class, cl);
				
				/* Broadcast the target request */
				for(RequestListener l : cl.getRequestListeners(Element.class)){
					l.requestMade(cl, request);
				}
			}else if(type == Type.LOCATION){
				Request<Location> request = new TargetRequest<Location>(id, Location.class, cl);
				
				/* Broadcast the target request */
				for(RequestListener l : cl.getRequestListeners(Element.class)){
					l.requestMade(cl, request);
				}
			}
		}
	}
	
	public enum Type{
		OBJECT		(0x00),
		LOCATION	(0x01);
		
		final int id;
		
		Type(int id){
			this.id = id;
		}
		
		public int getIdentifier(){
			return(id);
		}
		
		public static Type fromIdentifier(int id){
			switch(id){
			case 0: return(OBJECT);
			case 1: return(LOCATION);
			}
			
			throw(new IllegalArgumentException("Invalid picking mode"));
		}
	}
}