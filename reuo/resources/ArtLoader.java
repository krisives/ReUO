package reuo.resources;

//import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class ArtLoader extends ResourceLoader<ArtLoader.Entry, Sprite> {

	public class Entry extends ResourceLoader.Entry {
		int offset;

		int size;

		int unknown;

		SoftReference<Sprite> image = null;

		@Override
		public boolean isValid() {
			return(offset != 0xFFFFFFFF);
		}

		public String toString() {
			return "Offset: " + offset + "\nSize: " + size + "\nUnknown: "
					+ unknown;

		}
	}

	Map<Integer, Entry> entries = new HashMap<Integer, Entry>();

	FileChannel artChannel;

	public ArtLoader(FileChannel idx, FileChannel art) throws IOException {
		super(idx, 12);

		artChannel = art;
	}

	@Override
	protected long getEntryOffset(int id) throws IOException {
		return(id * getEntrySize(id));
	}

	@Override
	protected int getEntrySize(int id) throws IOException {
		return(12);
	}

	@Override
	public Sprite get(int id) throws IllegalArgumentException, IOException {
		// System.out.println(id);;

		if(id < 0 || (id * 12) >= entrySource.size()) {
			throw (new IllegalArgumentException("Image index out of range"));
		}

		/* Get the entry; if it's already loaded return the reference */
		Entry entry = getEntry(id);
		// System.out.printf("%x\n",entry.offset);

		if(!entry.isValid()) {
			return(null);
		}

		if(entry.image != null) {
			Sprite image = entry.image.get();

			if(image != null) {
				return(image);
			} else {
				entry.image = null;
			}
		}

		/* Seek to where the entry is and read it into memory */
		artChannel.position(entry.offset);
		ByteBuffer data = ByteBuffer.allocate(entry.size);
		data.order(ByteOrder.LITTLE_ENDIAN);

		artChannel.read(data);
		data.clear();

		/* Decode the image and save the reference */
		Sprite image = decode(entry, data, id);
		entry.image = new SoftReference<Sprite>(image);
		return(image);
	}

	private Sprite decode(Entry entry, ByteBuffer data, int id) {
		if(!entry.isValid())
			return null;

		if(id < 0x4000) {
			return(decodeTileArt(entry, data));
		} else if(id == 32767) {
			System.out.println(entry);
			return null;
		} else {
			return(decodeStaticArt(entry, data));
		}
	}

	private Sprite decodeTileArt(Entry entry, ByteBuffer data) {
		ByteBuffer retBuf = ByteBuffer.allocate(44 * 44 * 2);
		retBuf.order(ByteOrder.LITTLE_ENDIAN);
		
		int x, y;
		int nx;
		int pixel, r, g, b;
		int pos;

		int width = 2;

		for(y = 0; y < 22; y++) {
			for(x = 0; x < width; x++) {
				pixel = data.getShort() & 0xFFFF;
				//pixel = pixel | (1<<15);
				
				r = ((pixel >> 10) & 0x1F);
				g = ((pixel >> 5) & 0x1F);
				b = (pixel & 0x1F);
				
				pixel = 1 | (b << 1) | (g << 6) | (r << 11);
				
				nx = x + (44 - width) / 2;
				pos = y * 2 * 44 + nx * 2;
				retBuf.putShort(pos, (short)pixel);
			}

			width += 2;
		}

		width = 44;

		for(y = 22; y < 44; y++) {
			for(x = 0; x < width; x++) {
				pixel = data.getShort() & 0xFFFF;
				//pixel = pixel | (1<<15);
				
				r = ((pixel >> 10) & 0x1F);
				g = ((pixel >> 5) & 0x1F);
				b = (pixel & 0x1F);
				
				pixel = 1 | (b << 1) | (g << 6) | (r << 11);
				
				nx = x + (44 - width) / 2;
				pos = y * 44 + nx;
				retBuf.putShort(pos*2, (short)pixel);
			}

			width -= 2;
		}

		return new Sprite(44, 44, retBuf);
	}

	private Sprite decodeStaticArt(Entry entry, ByteBuffer data) {
		data.position(data.position() + 4);

		int width = data.getShort();
		int height = data.getShort();
		
		ByteBuffer retBuf = ByteBuffer.allocate(width * height * 2);
		retBuf.order(ByteOrder.LITTLE_ENDIAN);
		
		int table[] = new int[height];
		for(int i = 0; i < table.length; i++) {
			table[i] = data.getShort();
		}

		int dataStart = 8 + (height * 2);

		int x = 0;
		int y = 0;
		int pixel, r, g, b;
		int xOffset = 0;
		int xRun = 0;

		data.position(dataStart + (table[y] * 2));

		while(y < (height)) {

			xOffset = data.getShort();
			xRun = data.getShort();

			if(xOffset + xRun >= 2048)
				return null;
			else if((xOffset != 0) || (xRun != 0)) {
				x += xOffset;

				for(int i = 0; i < xRun; i++) {
					pixel = data.getShort() & 0xFFFF;
					//pixel = pixel | (1<<15);
					
					r = ((pixel >> 10) & 0x1F);
					g = ((pixel >> 5) & 0x1F);
					b = (pixel & 0x1F);
					
					pixel = 1 | (b << 1) | (g << 6) | (r << 11);
					
					retBuf.putShort(((y * width) + x) * 2, (short)pixel);
					x++;
				}

			} else {
				x = 0;

				y++;

				if(y < height) {
					data.position(dataStart + (table[y] * 2));
				}
			}
		}
		//Utility.fix16bit(retBuf);
		return new Sprite(width, height, retBuf);
	}

	@Override
	protected Entry getEntryFromBuffer(ByteBuffer buffer) {
		Entry entry = new Entry();

		entry.offset = buffer.getInt();
		entry.size = buffer.getInt();
		entry.unknown = buffer.getInt();

		return(entry);
	}

}
