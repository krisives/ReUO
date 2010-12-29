package reuo.client;

/**
 * An abstract respresenting a request to be satisified or cancelled. Requests end once they are cancelled and
 * cannot be satisfied. Likewise, Requests that are satisfied end and cannot be cancelled. The
 * {@link #isOpen()} method will check if the request is still valid.
 * 
 * @param <T> The response type; this is what will satisfy the response (see {@link #resolve(Object)})
 */
public abstract class Request<T>{
	/** The response type (generics don't conver this!) */
	Class type;
	/** The identifier of the request */
	int id;
	
	/**
	 * Initializes a Request for a type.
	 * 
	 * @param responseType the response type
	 */
	public Request(Class responseType){
		this.type = responseType;
	}
	
	/**
	 * Gets Request identifier
	 * @return the identifier
	 */
	public int getIdentifier(){
		return(id);
	}
	
	/**
	 * Gets the type of response requested.
	 * @return the response type
	 */
	public Class getResponseType(){
		return(type);
	}
	
	/**
	 * Checks if a request needs a response.
	 * @return true if the request is open; false if it's been satisfied or cancelled
	 */
	public boolean isOpen(){
		return(!(isCancelled() || isResolved()));
	}
	
	public abstract T getResponse();	
	public abstract boolean isCancelled();
	public abstract boolean isResolved();
	
	/**
	 * Cancels the request. If the request was satisfied this will throw an
	 * IllegalStateException
	 * @throws IllegalStateException if the request is not open
	 */
	public abstract void cancel() throws IllegalStateException;
	
	/**
	 * Responds to a request. If the Request has been cancelled or already
	 * responded to this will throw an IllegalStateException.
	 * @param response the response
	 * @throws IllegalStateException if the request is not open
	 */
	public abstract void resolve(T response) throws IllegalStateException;
	
	protected static IllegalStateException notOpenException =
		new IllegalStateException("Request is not open");
}