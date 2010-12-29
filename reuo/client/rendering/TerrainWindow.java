package reuo.client.rendering;

import reuo.resources.StaticLoader;
import reuo.resources.TerrainLoader;

public class TerrainWindow{
	TerrainLoader terrainLoader;
	StaticLoader staticLoader;
	int width, height;
	int cx, cy;
	
	@SuppressWarnings("unchecked")
	Node[][] edges = new Node[4][];
	
	static int
		LEFT = 0,
		RIGHT = 1,
		UP = 2,
		DOWN = 3
	;
	
	private class Node{
		Node[] link = new Node[4];
		
		TerrainLoader.Block terrain;
		StaticLoader.Block statics;
	}
	
	private TerrainWindow(int width, int height, TerrainLoader terrainLoader, StaticLoader staticLoader){
		this.width = width;
		this.height = height;
		this.cx = cx;
		this.cy = cy;
		
		edges[LEFT] = new Node[height];
		edges[RIGHT] = new Node[height];
		edges[UP] = new Node[width];
		edges[DOWN] = new Node[width];
		
		int left = 0;
		int right = width - 1;
		int top = 0;
		int bottom = height - 1;
		
		Node[] row = new Node[width];
	
		
		for(int y=0; y < height; y++){
			// Create linked row
			for(int x=0; x < width; x++){
				row[x] = new Node();
				
				if(x == left){
					edges[LEFT][y] = row[x];
				}
				
				if(x == right){
					edges[RIGHT][y] = row[x];
				}
				
				if(y == top){
					edges[UP][y] = row[y];
				}
				
				if(y == bottom){
					edges[DOWN][y] = row[y];
			}
		}
	}
	
	private void setCenter(int cx, int cy){
		int dx = this.cx - cx;
		int dy = this.cy - cy;
		Node walk = null;
		
		walk = edges[LEFT][0];
		for(int x=0; x < dx; x++){
			
		}
		
		this.cx = cx;
		this.cy = cy;
	}
}