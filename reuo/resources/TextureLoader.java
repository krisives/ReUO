package reuo.resources;

import static java.lang.Math.sqrt;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/**
 * Loads texture data from TEXMAPS.MUL. The TEXIDX.MUL file should be used as the
 * index. Each loaded texture is a ByteBuffer that contains the 16-bit texture data. Each
 * texture has TextureLoader.Entry instance associated with it that describes any
 * meta-information about the texture.
 */
public class TextureLoader extends ResourceLoader<TextureLoader.Entry, Sprite>{
	FileChannel texSource;
	
	/**
	 * Initializes a TextureLoader for a data source using an index.
	 * @param idxSource the entry index
	 * @param texSource the texture data source
	 */
	public TextureLoader(FileChannel idxSource, FileChannel texSource){
		super(idxSource, 12);
		
		this.texSource = texSource;
	}

	@Override
	public Sprite get(int id) throws IllegalArgumentException, IOException{
		Entry entry = getEntry(id);
		Sprite sprite;
		
		if(entry.sprite != null){
			sprite = entry.sprite.get();
			
			if(sprite != null){
				return(sprite);
			}else{
				sprite = null;
			}
		}
		
		System.out.printf("Reading texture %d\n", id);
		texSource.position(entry.offset);
		
		ByteBuffer buffer = ByteBuffer.allocate(entry.size);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.rewind();
		texSource.read(buffer);
		buffer.clear();
		
		sprite = new Sprite(entry.getWidth(), entry.getHeight(), buffer);
		sprite.setAlpha(true);
		
		entry.sprite = new SoftReference<Sprite>(sprite);
		return(sprite);
	}

	@Override
	protected Entry getEntryFromBuffer(ByteBuffer buffer){
		Entry entry = new Entry();
		
		entry.offset = buffer.getInt();
		entry.size = buffer.getInt();
		buffer.getInt(); // Unknown or useless field
		
		return(entry);
	}

	@Override
	protected long getEntryOffset(int id) throws IndexOutOfBoundsException, IOException{
		if(id < 0 || id >= (texSource.size() / 12)){
			String msg = String.format("Texture %d is invalid", id);
			throw(new IndexOutOfBoundsException(msg));
		}
		
		return(id * 12);
	}

	@Override
	protected int getEntrySize(int id) throws IndexOutOfBoundsException, IOException{
		return(12);
	}
	
	/**
	 * Describes information about a texture.
	 */
	public class Entry extends ResourceLoader.Entry{
		long offset;
		public int size;
		SoftReference<Sprite> sprite;
		
		/**
		 * Gets the width of the texture.
		 * @return the width
		 */
		public int getWidth(){
			return((int)sqrt(size / 2));
		}
		
		/**
		 * Gets the height of the texture.
		 * @return the height
		 */
		public int getHeight(){
			return((int)sqrt(size / 2));
		}
		
		@Override
		public boolean isValid(){
			return(true);
		}
	}
}