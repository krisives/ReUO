package reuo.resources;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Streams a Terrain from a data source.
 * @author Kristopher Ives
 */
public class TerrainLoader extends ResourceLoader<TerrainLoader.Block, TerrainLoader.Block>{
	
	/**
	 * Initialies a TerrainLoader from a data source
	 * @param terrainSource the terrain data (typically map0.mul, map1.mul, etc.)
	 */
	public TerrainLoader(FileChannel terrainSource){
		super(terrainSource, 4 + 3 * (8 * 8));
	}
	
	@Override
	public Block get(int id) throws IllegalArgumentException, IOException{
		Block block = getEntry(id);
		
		return(block);
	}
	
	@Override
	protected Block getEntryFromBuffer(ByteBuffer buffer){
		Block block = new Block();
		
		block = new Block();
		block.header = buffer.getInt();
		
		for(int i=0; i < block.cells.length; i++){
			block.cells[i] = block.new Cell();
			
			block.cells[i].tileId = buffer.getShort();
			block.cells[i].terrainLevel = buffer.get();
		}
		
		return(block);
	}
	
	@Override
	protected long getEntryOffset(int id) throws IOException{
		return(id * (4 + 3 * (8 * 8)));
	}

	@Override
	protected int getEntrySize(int id) throws IOException{
		return(4 + 3 * (8 * 8));
	}
	
	public class Block extends ResourceLoader.Entry{
		int header;
		Cell[] cells = new Cell[8 * 8];
		
		@Override
		public boolean isValid(){
			return(true);
		}
		
		/**
		 * Gets a cell at the specified coordinates relative to the
		 * block.
		 * @param x the x coordinate (horizontal) relative to the block
		 * @param y the y coordinate (vertical) relative to the block
		 * @return the cell
		 * @throws IndexOutOfBoundsException if the x and y coordinates
		 * are not inside the Block.
		 */
		public Cell get(int x, int y) throws IndexOutOfBoundsException{
			return(cells[x * 8 + y]);
		}
		
		/**
		 * Describes a Cell of a terrain Block
		 */
		public class Cell{
			int tileId;
			int terrainLevel;
			
			/**
			 * Gets the altitude of the Cell. Zero (0) is at sea-level, with negative
			 * being below sea-level and positive above sea-level. (by typical protocol
			 * limitations this range from -128 to +127)
			 * @return the altitude
			 */
			public int getLevel(){
				return(terrainLevel);
			}
			
			/**
			 * Gets the identifier for the terrain land tile
			 * @return the land tile identifier
			 */
			public int getTileId(){
				return(tileId);
			}
		}
	}
}