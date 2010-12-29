package reuo.event;

import reuo.Element;
import reuo.Instance;

/**
 * Handles events for Elements in an Instance.
 * @author Kristopher Ives
 */
public interface ElementListener<I extends Instance> extends Listener{
	/**
	 * Invoked when an Element is create in an Instance 
	 * @param elem the created Element
	 */
	public void elementCreated(Element elem);
	
	/**
	 * Invoked when an Element is removed from an Instance.
	 * @param elem the removed Element
	 */
	public void elementRemoved(Element elem);
	
	/**
	 * Invoked when an Element is Targeted in an Instance
	 * @param target the Element targeted
	 * @param aimer the Element doing the targeting
	 */
	public void elementTargeted(Element target, Element aimer);
}