import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

import reuo.client.rendering.TextureSpace;
import reuo.client.rendering.TextureSpace.Partition;

public class Test extends JFrame{
	public static void main(String[] args){
		Test instance = new Test();
		
		instance.setSize(new Dimension(640, 480));
		instance.setVisible(true);
	}
	
	List<Partition> parts = new ArrayList<Partition>();
	TextureSpace space = new TextureSpace(512, 512);
	
	public Test(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(new Panel());
		
		
		
		//parts.add(space.allocate(500, 255));
		//parts.add(space.allocate(24, 255));
		//parts.add(space.allocate(4,10));
		//parts.add(space.allocate(24, 32));
		

		
		for(int i=0; i < 9; i++){
		//	parts.add(space.allocate(12, 12));
		}
		
		for(int i=0; i < 14; i++){
			Partition part = space.allocate(64+i*2, 64-i*2);
			//System.out.printf("insert = %d\n", n);
			parts.add(part);
		}
		
		parts.add(space.allocate(4, 4));
		parts.add(space.allocate(32, 15));
		
		space.free(parts.get(2));
		parts.remove(2);
		
		for(int i=0; i < 16; i++){
			parts.add(2, space.allocate(16,16));
		}
	}
	
	class Panel extends JComponent{
		public void paintComponent(Graphics g){
			g.setColor(Color.RED);
			g.fillRect(0, 0, 512, 512);
			
			g.setColor(Color.BLUE);
			for(Partition part : parts){
				if(part == null){
					System.out.println(part);
					continue;
				}
				
				g.drawRect(part.getX(), part.getY(), part.getWidth(), part.getHeight());
			}
		}
	}
}