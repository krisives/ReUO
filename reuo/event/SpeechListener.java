package reuo.event;

import reuo.Speech;

/**
 * Handles events from speech in an Instance.
 * @author Kristopher Ives
 */
public interface SpeechListener extends Listener{
	/**
	 * Invoked when speech sent to an Instance.
	 * @param msg the speech
	 */
	public void speechReceived(Speech msg);
	
	/**
	 * Invoked when speech is being sent from an instance.
	 * @param msg the speech
	 */
	public void speechSent(Speech msg);
}