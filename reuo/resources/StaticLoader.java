package reuo.resources;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticLoader extends ResourceLoader<StaticLoader.Block, Map<Integer, List<Static>>>{
	public class Block extends ResourceLoader.Entry{
		int offset, length;
		SoftReference<Map<Integer, List<Static>>> ref;
		
		@Override
		public boolean isValid(){
			return(offset != 0xFFFFFFFF);
		}
		
		public int getCount(){
			return(length / 7);
		}
	}
	
	FileChannel dataSource;
	ByteBuffer resourceBuffer = ByteBuffer.allocate(7);
	
	public StaticLoader(FileChannel idxSource, FileChannel dataSource){
		super(idxSource, 12);
		
		this.dataSource = dataSource;
		resourceBuffer.order(ByteOrder.LITTLE_ENDIAN);
	}
	
	public List<Static> get(int x, int y) throws IllegalArgumentException, IOException{
		int blockX = x / 8;
		int blockY = y / 8;
		int blockId = blockX * 512 + blockY;
		
		int cellX = x - (blockX * 8);
		int cellY = y - (blockY * 8);
		int cellId = cellX + cellY * 8;
		
		Map<Integer, List<Static>> cells = get(blockId);
		
		if(cells.get(cellId) == null){
			return(emptyList);
		}
		
		return(cells.get(cellId));		
	}
	
	static List<Static> emptyList = new ArrayList<Static>();
	static Map<Integer, List<Static>> empty = new HashMap<Integer, List<Static>>();
	
	@Override
	public Map<Integer, List<Static>> get(int id) throws IllegalArgumentException, IOException{
		Block block = getEntry(id);
		Map<Integer, List<Static>> statics = null;
		
		if(block.ref != null){
			if((statics = block.ref.get()) != null){
				return(statics);
			}
		}
		
		if(!block.isValid()){
			return(empty);
		}
		
		int count = block.getCount();
		
		if(count <= 0){
			return(empty);
		}
		
		dataSource.position(block.offset);
		statics = new HashMap<Integer, List<Static>>(count);
		List<Static> cell = null;
		
		for(int i=0; i < count; i++){
			resourceBuffer.rewind();
			dataSource.read(resourceBuffer);
			resourceBuffer.clear();
			
			Static resource = new Static();
			resource.artId = resourceBuffer.getShort();
			resource.x = resourceBuffer.get();
			resource.y = resourceBuffer.get();
			resource.z = resourceBuffer.get();
			resource.flags = resourceBuffer.getShort();
			
			int cellId = resource.x + resource.y * id;
			
			cell = statics.get(cellId);
			
			if(cell == null){
				cell = new ArrayList<Static>();
				statics.put(cellId, cell);
			}
			
			cell.add(resource);
		}
		
		block.ref = new SoftReference<Map<Integer, List<Static>>>(statics);
		return(statics);
	}

	@Override
	protected Block getEntryFromBuffer(ByteBuffer buffer){
		Block block = new Block();
		
		block.offset = buffer.getInt();
		block.length = buffer.getInt();
		buffer.getInt();
		
		return(block);
	}

	@Override
	protected long getEntryOffset(int id) throws IndexOutOfBoundsException, IOException{
		return(id * 12);
	}

	@Override
	protected int getEntrySize(int id) throws IndexOutOfBoundsException, IOException{
		return(12);
	}
}