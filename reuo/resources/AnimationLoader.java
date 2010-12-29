package reuo.resources;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;


public class AnimationLoader extends
		ResourceLoader<AnimationLoader.Entry, Animation> {
			
	public class Entry extends reuo.resources.ResourceLoader.Entry {
		int offset;

		int size;

		int unknown;

		@Override
		public boolean isValid() {
			return(offset != 0xFFFFFFFF);
			//return true;
		}

	}

	Map<Integer, Entry> entries = new HashMap<Integer, Entry>();

	FileChannel artChannel;

	public AnimationLoader(FileChannel idx, FileChannel art) {
		super(idx, 12);

		artChannel = art;
	}

	@Override
	public Animation get(int id) throws IllegalArgumentException,
			IOException {
		if(id < 0 || (id * 12) >= entrySource.size()) {
			throw (new IllegalArgumentException("Image index out of range"));
		}

		Entry entry = getEntry(id);

		if(!entry.isValid()) {
			return(null);
		}

		artChannel.position(entry.offset);
		ByteBuffer data = ByteBuffer.allocate(entry.size);
		data.order(ByteOrder.LITTLE_ENDIAN);

		artChannel.read(data);
		data.clear();

		return new Animation(data);
	}

	@Override
	protected Entry getEntryFromBuffer(ByteBuffer buffer) {
		Entry entry = new Entry();

		entry.offset = buffer.getInt();
		entry.size = buffer.getInt();
		entry.unknown = buffer.getInt();

		return(entry);
	}

	@Override
	protected long getEntryOffset(int id) throws IOException {
		return(id * getEntrySize(id));
	}

	@Override
	protected int getEntrySize(int id) throws IOException {
		return 12;
	}
}
