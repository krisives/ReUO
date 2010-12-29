package reuo.resources;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Loads information about tiles on the Terrain. Each tile on the terrain
 * may have a Texture and a set of flags that describe the limitations of
 * the tile.
 * 
 * @author Kristopher Ives
 */
public class TerrainTileLoader extends ResourceLoader<TerrainTileLoader.Tile, TerrainTileLoader.Tile>{
	byte[] nameBytes = new byte[20];
	
	/**
	 * Initializes a TerrainLoader from a source to read the tile data. (typically
	 * tiledata.mul)
	 * @param tileDataSource the data source for the terrain tile data
	 */
	public TerrainTileLoader(FileChannel tileDataSource){
		super(tileDataSource, 4 + 2 + 20);
	}
	
	@Override
	public Tile get(int id) throws IllegalArgumentException, IOException{
		/* This file has no index */
		return(getEntry(id));
	}
	
	@Override
	protected Tile getEntryFromBuffer(ByteBuffer buffer){
		Tile tile = new Tile();
		
		tile.flags = buffer.getInt();
		tile.textureId = buffer.getShort();
		buffer.get(nameBytes);
		
		try{
			tile.name = new String(nameBytes, "ASCII");
		}catch(UnsupportedEncodingException e){
			tile.name = "";
			System.err.printf("WARNING: SCII encoding not supported?\n");
		}
		
		return(tile);
	}
	
	@Override
	protected long getEntryOffset(int id) throws IndexOutOfBoundsException, IOException{
		//if(id < 0 || id >= 512){
		//	String msg = String.format("Terrain tile %d is invalid", id);
		//	throw(new IndexOutOfBoundsException(msg));
		//}
		
		int headers = 1 + id / 32;
		
		return((headers * 4) + id * (4 + 2 + 20));
	}
	
	@Override
	protected int getEntrySize(int id) throws IndexOutOfBoundsException, IOException{
		//if(id < 0 || id >= 512){
		//	String msg = String.format("Terrain tile %d is invalid", id);
		//	throw(new IndexOutOfBoundsException(msg));
		//}
		
		return(4 + 2 + 20);
	}
	
	/**
	 * Describes a tile on a terrain.
	 */
	public class Tile extends ResourceLoader.Entry{
		int flags;
		int textureId;
		String name;
		
		@Override
		public boolean isValid(){
			return(true);
		}
		
		public String getName(){
			return(name);
		}
		
		/**
		 * Gets the identifier for the terrain tile.
		 * @return the identifier
		 */
		public int getTextureId(){
			return(textureId);
		}
		
		/**
		 * Checks if the terrain tile is textured.
		 * @return true if the getTextureId holds a valid texture id
		 */
		public boolean isTextured(){
			return(textureId != 0);
		}
	}
}