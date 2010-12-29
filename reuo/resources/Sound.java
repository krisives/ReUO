package reuo.resources;

import java.nio.ByteBuffer;

/**
 * Describes the name and waveform data of a Sound. The sample frequency of the sound
 * waveform is always 22Khz (22,050), the sample resolution is always 16-bit, and is always
 * Mono (1 channel).
 * 
 * @author Kristopher Ives
 */
public class Sound{
	String name;
	byte[] header;
	ByteBuffer waveData;
	
	@Override
	public String toString(){
		return(getName());
	}
	
	/**
	 * Gets the name of the sound. (typically this is a filename
	 * such as foosound.wav, although the getData method does not
	 * return a WAVE file)
	 * @return the name of the sound
	 */
	public String getName(){
		return(name);
	}
	
	/**
	 * Gets the wave data of the sound. This does not include the WAVE
	 * header, but only the waveform data with the format described in the
	 * class description.c
	 * @return the wave data
	 */
	public ByteBuffer getData(){
		return(waveData);
	}
}