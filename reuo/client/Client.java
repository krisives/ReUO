package reuo.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import reuo.Instance;
import reuo.AccountResponse;
import reuo.client.AccountHandler.LoginRequest;
import reuo.client.AccountHandler.ShardRequest;
import reuo.client.Server.Shard;


/**
 * An instance for game for client-side operations. The client will receive
 * and send messages to a server. Operations that require user input will be
 * dispatched using Requests.
 * 
 * @author Kristopher Ives
 */
public class Client extends Instance implements RequestListener{
	/* I nice little main to test */
	public static void main(String[] args){
		//System.out.printf("Connecting to login server at \n", args[0]);
		try{
			Client cl = new Client(2593);
			
			cl.addListener(RequestListener.class, cl);
			
			/* Shard to connect to */
			Server server = new Server(
				new InetSocketAddress(
					InetAddress.getByName(args[0]),
					2593
				)
			);
			
			cl.login(server);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void requestCancelled(Client cl, Request request){
		System.out.printf("cancelled %s\n", request);
	}

	public void requestResolved(Client cl, Request request){
		System.out.printf("Resolved %s: %s\n",
			request.getClass().getName(), request.getResponse());
	}

	public void requestMade(Client cl, Request request){
		System.out.printf("Made: %s\n", request.getClass().getName());
		
		if(request instanceof ShardRequest){
			for(Shard shard : cl.getServer()){
				request.resolve(shard);
				break;
			}
		}
	}
	
	/* These will get javadoc'd a bit later as they are constantly changing
	 * while the design is being changed */
	
	SocketChannel channel = null;
	Server server = null;
	ReceiveThread recvThread;
	SendThread sendThread;
	Map<Integer, PacketHandler> handlers = new HashMap<Integer, PacketHandler>();
	Map<Class, List<RequestListener>> requestListeners = new HashMap<Class, List<RequestListener>>();
	Queue<ByteBuffer> sendQueue = new LinkedList<ByteBuffer>();
	Map<ByteBuffer, Request> cancelledSends = new HashMap<ByteBuffer, Request>();
	Map<Class, List<Request>> requestTypes = new HashMap<Class, List<Request>>();
	int key = 0x13371337;
	
	/**
	 * Adds a Requests as scheduled. The request will remain until it's satisified
	 * or cancelled.
	 * @param type the type of request
	 * @param req the request
	 */
	protected void addRequest(Request request){
		Class type = request.getClass();
		List<Request> requests = requestTypes.get(type);
		
		if(requests == null){
			requests = new ArrayList<Request>();
			requestTypes.put(type, requests);
		}
		
		requests.add(request);
		
		for(RequestListener listener : getListeners(RequestListener.class)){
			listener.requestMade(this, request);
		}
	}
	
	protected void removeRequest(Request request){
		Class type = request.getResponseType();
		List<Request> requests = requestTypes.get(type);
		
		if(requests == null){
			requests = new ArrayList<Request>();
			requestTypes.put(type, requests);
		}
		
		requests.remove(request);
	}
	
	protected Iterable<Request> getRequests(Class type){
		List<Request> requests = requestTypes.get(type);
		
		if(requests == null){			
			requests = new ArrayList<Request>();
			requestTypes.put(type, requests);
		}
		
		return(requests);
	}
	
	/**
	 * Cancels a send and a Request associated with it.
	 * @param request the request
	 * @param buffer the send
	 */
	protected void cancelSend(Request request, ByteBuffer buffer){
		/* Markt he buffer cancelled */
		synchronized(cancelledSends){
			cancelledSends.put(buffer, request);
		}
		
		/* Remove the request */
		List<Request> requests = requestTypes.get(request.getResponseType());
		requests.remove(request);
	}
	

	
	/**
	 * Initializes a game instance as a client on a specific port.
	 * @param localPort the port to use for incoming operations.
	 */
	public Client(int localPort){
		addHandlers(new AccountHandler(this));
	}
	
	static IllegalArgumentException handlerExistsException = 
		new IllegalArgumentException("Handler already exists for packet identifier");
	
	protected void addHandlers(Iterable<PacketHandler> handlerList) throws IllegalArgumentException{
		for(PacketHandler handler : handlerList){
			addHandler(handler);
		}
	}
	
	protected void addHandler(PacketHandler handler) throws IllegalArgumentException{
		if(handlers.containsValue(handler)){
			return;
		}
		
		/* Something is already handling this identifier */
		if(handlers.containsKey(handler.getIdentifier())){
			throw(handlerExistsException);
		}
		
		handlers.put(handler.getIdentifier(), handler);
	}
	
	protected void send(ByteBuffer data){
		synchronized(sendQueue){
			sendQueue.add(data);
			sendQueue.notifyAll();
		}
	}
	
	/**
	 * Gets the Shard the client is connected to; or <code>null</code> if the
	 * client is not connected to a shard.
	 * @return the shard
	 */
	public Server getServer(){
		return(server);
	}
	
	/**
	 * Checks if the client is running.
	 * @return true if the client is running
	 */
	public boolean isRunning(){
		if(recvThread == null){
			return(false);
		}
		
		return(recvThread.isAlive());
	}
	
	protected void connect(InetSocketAddress address) throws IOException{
		synchronized(sendQueue){
			sendQueue.clear();
		}
		
		synchronized(requestTypes){
			requestTypes.clear();
		}
		
		synchronized(cancelledSends){
			cancelledSends.clear();	
		}
		
		channel = SocketChannel.open();
		channel.connect(address);
		
		recvThread = new ReceiveThread();
		sendThread = new SendThread();
		
		recvThread.start();
		sendThread.start();
	}
	
	/**
	 * Connects the client to a server.
	 * @param server the server to perform the login
	 * @throws IOException if there is a problem with the communication
	 * to the server.
	 */
	public Request<AccountResponse> login(Server server) throws IOException{
		if(isRunning()){
			System.out.println("logout first?");
		}
		
		this.server = server;
		connect(server.getAddress());
		
		ByteBuffer keyBuffer = ByteBuffer.allocate(4);
		keyBuffer.order(ByteOrder.LITTLE_ENDIAN);
		keyBuffer.clear();
		
		keyBuffer.putInt(key);
		send(keyBuffer);
		
		/* Send the login request */
		LoginRequest request = new LoginRequest(this, "nullmind", "123321");
		addRequest(request);
		send(request.getData());
		
		return(request);
	}
	
	/**
	 * Registers a RequestListener for a specific type.
	 * 
	 * @param type the type
	 * @param listener the listener
	 */
	public void addRequestListener(Class type, RequestListener listener){
		List<RequestListener> list = requestListeners.get(type);
		
		if(list == null){
			list = new ArrayList<RequestListener>();
			requestListeners.put(type, list);
		}
		
		if(!list.contains(listener)){
			list.add(listener);
		}
	}
	
	/**
	 * Gets an iteration of all the RequestListeners for a specific
	 * type.
	 * 
	 * @param type the of listener
	 * @return an iteration of the listeners
	 */
	public Iterable<RequestListener> getRequestListeners(Class type){
		List<RequestListener> list = requestListeners.get(type);
		
		if(list == null){
			list = new ArrayList<RequestListener>();
			requestListeners.put(type, list);
		}
		
		return(list);
	}
	
	/**
	 * This thread receives data from the server and dispatch
	 * to the PacketHandlers.
	 * @author Kristopher Ives
	 */
	private class ReceiveThread extends Thread{
		public void run(){
			/* Initialize the channel and buffers */
			System.out.println("Thread started");
			
			PacketHandler handler = null;
			ByteBuffer header = ByteBuffer.allocate(1);
			int packetId;
			
			header.order(ByteOrder.LITTLE_ENDIAN);
			header.rewind();
			/* TODO: handle the encryption with an EncryptedChannel ? */
			
			try{
				/* Read until the socket is closed (should always be blocking) */
				while(channel.read(header) > 0){
					header.position(0);
					
					/* Get the handler based on the packet identifier */
					packetId = header.get(0) & 0xFF;
					handler = handlers.get(packetId);
					
					if(handler != null){
						/* Handle this packet */
						System.out.printf("Handling packet %02x\n", packetId);
						handler.handle(Client.this, channel);
					}else{
						/* Packet is unknown! */
						System.err.printf("[%02x] No handler for packet!\n", packetId);
					}
					
					header.rewind();
				}
			}catch(ClosedChannelException e){
				
			}catch(IOException e){
				// The socket was closed (likely by the client)
				e.printStackTrace();
			}
			
			/* The socket is still open we should close it */
			if(!channel.isOpen()){
				try{
					System.out.println("Closing connection");
					channel.close();
				}catch(IOException e){
					/* We can't close the socket... */
					e.printStackTrace();
				}
			}
			
			System.out.println("Recv thread finished");
			
			synchronized(sendQueue){
				/* This will wake the SendThread and cause it to finish */
				sendQueue.notifyAll();
			}
		}
	}
	
	/**
	 * Daemon thread that sends outbound data.
	 * @author Kristopher Ives
	 */
	private class SendThread extends Thread{
		/* The thread procedure */
		public void run(){
			/* This thread only runs while the game is running. */
			while(isRunning()){				
				/* Start sending out data */
				while(!sendQueue.isEmpty()){
					ByteBuffer buffer = sendQueue.remove();
					
					synchronized(cancelledSends){
						Request request = cancelledSends.get(buffer);
						
						if(request != null){
							cancelledSends.remove(buffer);
							
							/* Request is cancelled */
							for(RequestListener listener : getListeners(RequestListener.class)){
								listener.requestCancelled(Client.this, request);
							}
							
							continue;
						}
					}
					
					buffer.rewind();
					System.out.printf("Trying to send (%d bytes)\n", buffer.capacity());
					
					try{
						channel.write(buffer);
					}catch(IOException e){
						e.printStackTrace();
						break;
					}
					
					/*
					StringBuffer dump = new StringBuffer();
					StringBuffer ascii = new StringBuffer();
					
					for(int i=0; i < buffer.limit(); i++){
						if(i != 0) dump.append(" ");
						
						if(i % 12 == 0){
							dump.append(String.format("\n%02x:  ", i));
						}
						
						dump.append(String.format("%02x", buffer.get(i)));
					}
					
					System.out.println(dump.toString());
					*/
				}
				
				try{
					/* The sendQueue will be notified when sending should start again */
					synchronized(sendQueue){
						sendQueue.wait();
					}
				}catch(InterruptedException e){
					continue;
				}
			}
			
			System.out.println("Send thread exited");
		}
	}


}