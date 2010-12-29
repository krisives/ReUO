package reuo.client;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Describes a set of Servers.
 * @author Kristopher Ives
 */
public class Server implements Iterable<Server.Shard>{
	InetSocketAddress address;
	List<Shard> shards = new ArrayList<Shard>();
	
	/**
	 * Initializes a Server from the address.
	 * @param address the address of the Server
	 */
	public Server(InetSocketAddress address){
		this.address = address;
	}
	
	/**
	 * Gets the address for the Server.
	 * @return the address
	 */
	public InetSocketAddress getAddress(){
		return(address);
	}
	
	/**
	 * Gets an iteration of all the Shards on the Server
	 * @return the iterationgame server on a Shard.
	 */
	public Iterator<Shard> iterator(){
		return(shards.iterator());
	}
	
	@Override
	public String toString(){
		return(getAddress().toString());
	}
	
	/**
	 * Describes a Shard reported by the Server
	 * @author Kristopher Ives
	 */
	public class Shard{
		int id;
		String name;
		int load;
		InetAddress address;
		
		/**
		 * Gets the identifier of the Shard (typically an Index)
		 * @return the identifier
		 */
		public int getIdentifier(){
			return(id);
		}
		
		/**
		 * Describes amount of load of the Shard. This ranges from 0.0 to 1.0 as the
		 * Shard gets more users.
		 * @return the load
		 */
		public double getLoad(){
			return((double)load / 255.0);
		}
		
		/**
		 * Gets the address for this Shard
		 * @return the address
		 */
		public InetAddress getAddress(){
			return(address);
		}
		
		/**
		 * Gets the name of the Shard
		 * @return the name
		 */
		public String getName(){
			return(name);
		}
		
		@Override
		public String toString(){
			String str = String.format("%s (%d, %s)",
				name, id, address.toString());
			
			return(str);
		}
	}
}