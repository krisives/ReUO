

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import reuo.resources.GumpLoader;
import reuo.resources.Sprite;

public class TestGump extends JFrame {
	public static void main(String[] args) {
		try {
			// "E:\\Ultima Online Mondain's Legacy\\GUMPIDX.MUL"
			// "E:\\Ultima Online Mondain's Legacy\\GUMPART.MUL"
			TestGump frame = new TestGump(args[0], args[1]);

			frame.setSize(new Dimension(640, 480));
			frame.setVisible(true);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	//ArtLoader cache;
	GumpLoader cache;
	
	DrawArea drawArea;
	
	// DefaultListModel dlm
	JList lsb;
	
	public TestGump(String idxFile, String artFile) throws IOException {
		cache = new GumpLoader(new RandomAccessFile(idxFile, "r").getChannel(),
				new RandomAccessFile(artFile, "r").getChannel());
		
		int gumpCount = 0;
		ArrayList<String> al = new ArrayList<String>();

		for(int i : cache) {
			al.add("" + i);
			gumpCount++;
		}

		lsb = new JList(al.toArray());
		lsb.setSelectedIndex(0);
		lsb.setAutoscrolls(true);
		lsb.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				drawArea.setIndex(Integer.parseInt((String) lsb
						.getSelectedValue()));
				// System.out.println((String)lsb.getSelectedValue());
			}
		});

		// System.out.println(gumpCount);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// setContentPane(drawArea = new DrawArea());
		setLayout(new BorderLayout());
		getContentPane().add(new JScrollPane(lsb), BorderLayout.WEST);
		getContentPane().add(drawArea = new DrawArea(), BorderLayout.CENTER);
	}

	class DrawArea extends JComponent implements MouseListener {
		Image img = null;

		int index = 0;

		DrawArea() {
			addMouseListener(this);

			reload();
		}

		public void setIndex(int idx) {
			index = idx;
			reload();
			repaint();
		}

		void reload() {
			try {
				Sprite test = cache.get(index);
				img = test.getBufferedImage();
				/*
				 * while(img == null){ img = cache.get(index); if(img == null){
				 * index = index + direction; //System.out.println(index); } }
				 */
				// System.out.println(":");
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

		// private int direction = 1;

		public void mousePressed(MouseEvent event) {
			switch(event.getButton()) {
			case MouseEvent.BUTTON1:
				index++;
				// direction = 1;
				lsb.setSelectedValue("" + index, true);
				break;
			case MouseEvent.BUTTON3:
				index = Math.max(index - 1, 0);
				// direction = -1;
				lsb.setSelectedValue("" + index, true);
				break;
			}

			reload();
			repaint();
		}

		public void mouseReleased(MouseEvent event) {
		}

		public void mouseClicked(MouseEvent event) {
		}

		public void mouseEntered(MouseEvent event) {
		}

		public void mouseExited(MouseEvent event) {
		}

		public void paintComponent(Graphics lg) {
			Graphics2D g = (Graphics2D) lg;
			// System.out.println(img);

			if(img != null) {
				g.drawImage(img, 10, 10, null);
			}
		}
	}
}
