package reuo.resources;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

// TODO: Make this return a 16-bit (with alpha bit corrected) buffer */

/**
 * Loads graphics dedicated to the Graphical User Menu Popups (Gump) for a
 * client instance.
 * 
 * @author Kristopher Ives
 */
public class GumpLoader extends ResourceLoader<GumpLoader.Entry, Sprite> {
	public static void main(String[] args) {
		try {
			RandomAccessFile idx;
			RandomAccessFile art;

			idx = new RandomAccessFile(
					"E:\\Ultima Online Mondain's Legacy\\GUMPIDX.MUL", "r");
			art = new RandomAccessFile(
					"E:\\Ultima Online Mondain's Legacy\\GUMPART.MUL", "r");
			GumpLoader cache = new GumpLoader(idx.getChannel(), art
					.getChannel());
			cache.get(1);
		} catch(IOException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}

		System.out.println("Done");
	}

	FileChannel artSource;

	/**
	 * Initializes a GumpLoader with a source for an index and the Gump resource
	 * data.
	 * 
	 * @param idxSource
	 *            the source for the index (typically gumpidx.mul)
	 * @param artSource
	 *            the source for the gump data (typically gumpart.mul)
	 * @throws IOException
	 *             if any IO operations fail
	 */
	public GumpLoader(FileChannel idxSource, FileChannel artSource)
			throws IOException {
		super(idxSource, 12);

		this.artSource = artSource;
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
	protected Entry getEntryFromBuffer(ByteBuffer buffer) {
		Entry entry = new Entry();

		entry.offset = buffer.getInt();
		entry.size = buffer.getInt();
		entry.height = buffer.getShort();
		entry.width = buffer.getShort();

		return(entry);
	}

	@Override
	public Sprite get(int id) throws IllegalArgumentException, IOException {

		/* Check if the index exists */
		if(id < 0 || (id * 12) >= entrySource.size()) {
			throw (new IllegalArgumentException("Image index out of range"));
		}

		/* Get the entry; if it's already loaded return the reference */
		Entry entry = getEntry(id);

		if((entry.offset == 0xFFFFFFFF) || (entry.size == 0)) {
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
		artSource.position(entry.offset);
		ByteBuffer data = ByteBuffer.allocate(entry.size);
		data.order(ByteOrder.LITTLE_ENDIAN);

		artSource.read(data);
		data.clear();

		/* Decode the image and save the reference */
		Sprite image = decode(entry, data);
		entry.image = new SoftReference<Sprite>(image);
		return(image);
	}

	/**
	 * Decodes a 16-bit RLE image into a BufferedImage (RGBA)
	 * 
	 * @param entry
	 *            the entry (used for width and height)
	 * @param data
	 *            the RLE data (table(!?), 16-bit pixels and run lengths
	 *            repeating)
	 */
	private Sprite decode(Entry entry, ByteBuffer data) throws IOException {
		// BufferedImage image = new BufferedImage(entry.width, entry.height,
		// BufferedImage.TYPE_INT_ARGB);
		// Graphics2D ig = image.createGraphics();
		/* int r, g, b; */

		/* The position in the image (this is the sum of the x-coordinates) */
		// int pos = 0;
		/* The color to fill for the masked portions of the image */
		/* Color clear = new Color(0, 0, 0, 0); */

		/* I have no idea what this is for... we decode without using it */
		data.position(data.position() + (entry.height * 4));
		ByteBuffer retBuf = ByteBuffer.allocate(entry.height * entry.width * 2);

		while(data.remaining() >= 4) {
			// Get the pixel color and run count
			int rgb = data.getShort();
			int run = data.getShort();

			/*
			 * It's important to note that the 1-bit isn't used as an alpha, and
			 * instead an implied mask of (0,0,0) is used... brilliant.
			 */
			/*
			 * r = (rgb & 0x7C00) >> 10; g = (rgb & 0x3E0) >> 5; b = (rgb &
			 * 0x1F);
			 */

			/* Check if this pixel is masked */
			/*
			 * if(r == 0 && g == 0 && b == 0){ ig.setColor(clear); }else{ Color
			 * color = new Color(r * 8, g * 8, b *8); ig.setColor(color); }
			 */

			/* Process the run for this color */
			while(run > 0) {
				/*
				 * int restOfLine = entry.width - (pos % entry.width); int width =
				 * run < restOfLine ? run : restOfLine;
				 */
				/** Fill the run */
				/*
				 * ig.fillRect( pos % entry.width, pos / entry.width, width, 1 );
				 * 
				 * pos += width;
				 */
				retBuf.putShort((short) rgb);
				run--;
			}
		}
		Sprite retSprite = new Sprite(entry.width,entry.height,retBuf);
		retSprite.fix16bit();
		return(retSprite);
	}

	/**
	 * Describes a Gump graphic
	 */
	public class Entry extends ResourceLoader.Entry {
		long offset;

		int size;

		int width, height;

		SoftReference<Sprite> image = null;

		@Override
		public String toString() {
			return(String.format("%d[%d]: %d, %d", offset, size, width, height));
		}

		@Override
		public boolean isValid() {
			return(offset != 0xFFFFFFFF && size != 0);
		}

		/**
		 * Gets the width of the Gump image
		 * 
		 * @return the width (in pixels)
		 */
		public int getWidth() {
			return(width);
		}

		/**
		 * Gets the height of the Gump image
		 * 
		 * @return the height (in pixels)
		 */
		public int getHeight() {
			return(height);
		}
	}
}
