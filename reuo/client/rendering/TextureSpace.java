package reuo.client.rendering;

import java.util.HashMap;
import java.util.Map;

/**
 * A collection of texture areas that map to a single pixel map. Each allocated texture
 * is placed somewhere in the pixel map.
 * @author Kristopher Ives
 */
public class TextureSpace implements Comparable<TextureSpace>{
	private Node root = null;
	private Map<SubTexture, Node> allocated = new HashMap<SubTexture, Node>();
	private int available;
	
	/**
	 * Initializes a texture space with the specified dimensions
	 * @param width the horizontal size
	 * @param height the vertical size
	 */
	public TextureSpace(int width, int height){
		root = new Node(0, 0, width, height);
		available = width * height;
	}
	
	/**
	 * Allocates a SubTexture inside the texture space with the specified
	 * dimensions. If the requested allocation cannot fit inside the texture
	 * space this method will return null and no allocation will occur.
	 * @param width the horizontal size of the SubTexture being requested
	 * @param height the vertical size of the parition being requested
	 * @return the allocated SubTexture; or null if no space was available
	 */
	public SubTexture allocate(int width, int height){
		Node inserted = root.insert(width, height);
		
		if(inserted == null){
			return(null);
		}
		
		inserted.area = new SubTexture(inserted.getX(), inserted.getY(), width, height);
		allocated.put(inserted.area, inserted);
		
		available -= width * height;
		return(inserted.area);
	}
	
	/**
	 * Frees a SubTexture of the texture space
	 * @param area the SubTexture to free
	 * @throws IllegalArgumentException if the SubTexture is not allocated
	 */
	public void free(SubTexture area) throws IllegalArgumentException{
		Node n = allocated.get(area);
		
		if(n == null){
			throw(new IllegalArgumentException("SubTexture is not allocated"));
		}
		
		available += area.getWidth() * area.getHeight();
		n.area = null;
		allocated.remove(area);
	}
	
	public int getWidth(){
		return(root.getWidth());
	}
	
	public int getHeight(){
		return(root.getHeight());
	}
	
	/**
	 * Describes a split in the SubTexture graph.
	 */
	private class Node{
		private Node before = null;
		private Node after = null;
		private SubTexture area = null;
		private int left, top, right, bottom;
		
		/**
		 * Initializes a Node with the specified coordinates.
		 * @param left the horizontal start
		 * @param top the vertical start
		 * @param right the horizontal end
		 * @param bottom the vertical end
		 */
		private Node(int left, int top, int right, int bottom){
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
		}
		
		/**
		 * Gets the x coordinate.
		 * @return the x coordinate
		 */
		private int getX(){
			return(left);
		}
		
		/**
		 * Gets the y coordinate.
		 * @return the y coordinate
		 */
		private int getY(){
			return(top);
		}
		
		/**
		 * Gets the width of the node in the SubTexture graph.
		 * @return the width
		 */
		private int getWidth(){
			return(right - left);
		}
		
		/**
		 * Gets the height of the node in the SubTexture graph.
		 * @return the height
		 */
		private int getHeight(){
			return(bottom - top);
		}
		
		/**
		 * Checks if something of the specified dimensions could fit inside
		 * the node.
		 * @param width the horizontal size
		 * @param height the vertical size
		 * @return true if the specified dimensions can fit
		 */
		private boolean isInside(int width, int height){
			return(width <= getWidth() && height <= getHeight());
		}
		
		/**
		 * Checks if the node already has an allocated SubTexture.
		 * @return true if node is free; false if it's allocated
		 */
		private boolean isFree(){
			return(area == null);
		}
		
		/**
		 * Checks if the node is a leaf (has no children) in the SubTexture graph.
		 * @return true if the node is a leaf
		 */
		private boolean isLeaf(){
			return(before == null && after == null);
		}
		
		/**
		 * Inserts a node of the specified dimension as a child of this node. If there
		 * is no space (either because of other nodes or because the specified dimensions
		 * are too big) the return value is <code>null</code>.
		 * @param width the horizontal size
		 * @param height the vertical size
		 * @return the allocated Node; or <code>null</code> if the node cannot contain
		 * something of the specified dimensions
		 */
		private Node insert(int width, int height){
			int dw, dh;
			Node inserted = null;
			
			if(!isLeaf()){
				// try inserting into first child
				inserted = before.insert(width, height);
				
				if(inserted == null){
					inserted = after.insert(width, height);
				}
			}else{
				// there's already a texture here
				if(!isFree()){
					return(null);
				}
				
				// this is too small
				if(!isInside(width, height)){
					return(null);
				}
				
				// area fits perfectly into this node
				if(width == getWidth() && height == getHeight()){
					//area = new SubTexture(left, top, width, height);
					
					return(this);
				}
				
				// decide which way to split
				dw = getWidth() - width;
				dh = getHeight() - height;
				
				if(dw > dh){					
					before = new Node(
						left, top,
						left + width, bottom
					);
					
					after = new Node(
						left + width+1, top,
						right, bottom
					);
				}else{
					before = new Node(
						left, top,
						right, top + height
					);
					
					after = new Node(
						left, top + height+1,
						right, bottom
					);
				}
				
				inserted = before.insert(width, height);
			}
			
			return(inserted);
		}
	}
	
	public int compareTo(TextureSpace anotherSpace){
		return(this.available - anotherSpace.available);
	}
}