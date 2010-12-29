package reuo.resources;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class Animation {
	int palette[] = new int[256];

	int frameCount;

	int frameOffset[];

	public Frame frames[];

	Animation(ByteBuffer buffer) {
		for(int i = 0; i < palette.length; i++) {
			palette[i] = buffer.getShort() & 0xFFFF;
			/* System.out.println(palette[i]); */
		}

		int offsetPoint = buffer.position();

		frameCount = buffer.getInt() & 0xFFFFFFFF;
		// System.out.println("" + frameCount);
		frameOffset = new int[frameCount];
		frames = new Frame[frameCount];

		for(int i = 0; i < frameOffset.length; i++) {
			frameOffset[i] = buffer.getInt() & 0xFFFFFFFF;
		}

		for(int i = 0; i < frameCount; i++) {
			buffer.position(offsetPoint + frameOffset[i]); /* Looking for this */
			frames[i] = new Frame(buffer);
		}
	}

	public class Frame extends Sprite {
		int centerX;

		int centerY;

		public final int DOUBLE_XOR = (0x200 << 22) | (0x200 << 12);

		public int getCenterX() {
			return centerX;
		}

		public int getCenterY() {
			return centerY;
		}
		/*
		public void setCenterX(int newCenterX) {
			centerX = newCenterX;
		}

		public void setCenterY(int newCenterY) {
			centerY = newCenterY;
		}
		*/

		Frame() {

		}

		Frame(ByteBuffer buffer) {
			super();
			centerX = buffer.getShort();
			centerY = buffer.getShort();
			width = buffer.getShort() & 0xFFFF;
			height = buffer.getShort() & 0xFFFF;
			data = ByteBuffer.allocate(width * height);
			
			
			
			centerX = width / 2;
			centerY = height / 2;

			int header = 0;

			//int xBase = centerX;// - 0x200;
			//int yBase = (centerY + height);// - 0x200;
			//int line = 0;

			//line = xBase;
			//line += (yBase * (width >> 1));
			
			int cur = 0;
			int end = 0;
			int xOff;
			int yOff;
			int x, y;
			int run;
			byte pixel;
			
			System.out.printf("width: %d\nheight: %d\n",width,height);
			System.out.printf("cx: %d\ncy: %d\n", centerX, centerY);
			
			while((header = buffer.getInt()) != 0x7FFF7FFF) {
				header ^= DOUBLE_XOR;
				
				xOff = (header >> 12) & 0x3ff;
				yOff = (header >> 22) & 0x3ff;
				
				xOff = xOff - 512;
				yOff = yOff - 512;
				
				//xOff = (xOff ^ 512) - 512;
				//yOff = (yOff ^ 512) - 512;
				System.out.printf("xOff: %d\nyOff: %d\n",xOff,yOff);
				
				x = (centerX + xOff);
				y = (centerY + yOff);
				
				System.out.printf("x = %d, y = %d\n", x, y);
				
				run = (header & 0xfff);
				cur = ((y * width) + x);
				end = cur + run;
				// System.out.printf("run: %d\n",end-cur);
				// System.out.printf("cur: %x\n",cur);
				
				while(cur < end) {
					data.put(cur, buffer.get());
					//data.put(buffer.get());
					cur++;
				}
			}
		}

		@Override
		public BufferedImage getBufferedImage() {
			ByteBuffer img = ByteBuffer.allocate(data.capacity() * 2);
			data.rewind();

			while(data.hasRemaining()) {
				int index = (data.get() & 0xFF);
				int tmp = palette[index];
				// System.out.println("Tmp: "+tmp);
				img.putShort((short) tmp);
			}

			Sprite retSprite = new Sprite(width, height, img);
			retSprite.fix16bit();
			return retSprite.getBufferedImage();
		}
	}
}
