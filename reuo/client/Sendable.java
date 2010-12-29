package reuo.client;

import java.nio.ByteBuffer;

public interface Sendable{
	/**
	 * Gets the data that should be sent
	 * @return the data
	 */
	public ByteBuffer getData();
}