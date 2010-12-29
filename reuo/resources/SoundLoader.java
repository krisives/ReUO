package reuo.resources;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/**
 * Loads playable sounds used to play audio in a client instance.
 * @author Kristopher Ives
 */
public class SoundLoader extends ResourceLoader<SoundLoader.Entry, Sound>{
	ByteBuffer headerBuffer = ByteBuffer.allocate(20);
	FileChannel sndSource = null;
	
	/**
	 * Initializes a SoundLoader from an index and data source. The index describes
	 * the resources contained in the data source.
	 * @param idxSource the index (typically soundidx.mul)
	 * @param sndSource the resource (typically sound.mul
	 */
	public SoundLoader(FileChannel idxSource, FileChannel sndSource){
		super(idxSource, 12);
		this.sndSource = sndSource;
		
		headerBuffer.order(ByteOrder.LITTLE_ENDIAN);
	}

	@Override
	public Sound get(int id) throws IllegalArgumentException, IOException{
		Entry entry = getEntry(id);
		
		if(entry.sound != null){
			Sound sound = entry.sound.get();
			
			if(sound != null){
				return(sound);
			}else{
				entry.sound = null;
			}
		}
		
		sndSource.position(entry.offset);
		Sound sound = new Sound();
		
		headerBuffer.rewind();
		sndSource.read(headerBuffer);
		headerBuffer.clear();
		byte[] name = new byte[20];
		headerBuffer.get(name);
		sound.name = new String(name, "ASCII");
		
		headerBuffer.rewind();
		sndSource.read(headerBuffer);
		headerBuffer.clear();
		
		sound.waveData = ByteBuffer.allocate(entry.length - 40);
		sound.waveData.order(ByteOrder.LITTLE_ENDIAN);
		sound.waveData.rewind();
		sndSource.read(sound.waveData);
		sound.waveData.clear();
	
		entry.sound = new SoftReference<Sound>(sound);
		return(sound);
	}

	@Override
	protected Entry getEntryFromBuffer(ByteBuffer buffer){
		Entry entry = new Entry();
		
		entry.offset = buffer.getInt();
		entry.length = buffer.getInt();
		entry.id = buffer.getShort();
		//buffer.position(buffer.position() + 5);
		
		return(entry);
	}

	@Override
	protected long getEntryOffset(int id) throws IndexOutOfBoundsException, IOException{
		if(id < 0){
			throw(new IndexOutOfBoundsException());
		}
		
		return(id * 12);
	}

	@Override
	protected int getEntrySize(int id) throws IndexOutOfBoundsException, IOException{
		if(id < 0){
			throw(new IndexOutOfBoundsException());
		}
		
		return(12);
	}
	
	/**
	 * Describes an index for sound data
	 */
	public class Entry extends ResourceLoader.Entry{
		int offset, length;
		int id;
		SoftReference<Sound> sound = null;
		
		@Override
		public boolean isValid(){
			return(offset != 0xFFFFFFFF && length != 0);
		}
	}
}