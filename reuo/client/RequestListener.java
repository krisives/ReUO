package reuo.client;



/**
 * Handles events related to Requests.
 * @author Kristopher Ives
 */
public interface RequestListener{
	/**
	 * Invoked when a Request is made.
	 * @param request the request
	 */
	public void requestMade(Client with, Request request);
	
	/**
	 * Invoked when a Request has been resolved.
	 * @param request the request
	 */
	public void requestResolved(Client with, Request request);
	
	/**
	 * Invoked when a Request is cancelled.
	 * @param request the request
	 */
	public void requestCancelled(Client with, Request request);
}