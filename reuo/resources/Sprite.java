package reuo.resources;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

/**
 * Describes an image of 16-bit pixels. The red, green, and blue
 * componenents are each represented by 5 bits, and the remaining
 * bit is used as an alpha mask. Each pixel has the following
 * bit mask:
 * <code>
 * 	15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00
 * 	A  R  R  R  R  R  G  G  G  G  G  B  B  B  B  B
 * </code>
 * @author Luke Green
 */
public class Sprite {
	int width;
	int height;
	ByteBuffer data;
	
	/**
	 * Initializes an unusable Sprite
	 */
	Sprite(){
		width = height = 0;
		data = null;
	}
	
	/**
	 * Initializes a Sprite from the specified dimensions and pixe
	 * data. The Sprite is backed by the pixel data specified; and
	 * any changes to the passed buffer will modify the Sprite.
	 * @param width the horizontal size
	 * @param height the vertical size
	 * @param data the pixel data
	 */
	public Sprite(int width, int height, ByteBuffer data) {
		this.width = width;
		this.height = height;
		this.data = data;
	}
	
	/**
	 * Gets the Sprite pixel data
	 * @return
	 */
	public ByteBuffer getPixels(){
		return(data);
	}
	
	public int getWidth(){
		return(width);
	}
	
	public int getHeight(){
		return(height);
	}
	
	public BufferedImage getBufferedImage() {

		data.clear();

		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		
		int pixel, a, r, g, b;
		
		for(int i = 0; i < (data.capacity()/2); i++) {
			pixel = data.getShort() & 0xFFFF;
			/*
			a = (pixel >> 15) * 255 ;
			r = ((pixel & 0x7C00) >> 10) * 8;
			g = ((pixel & 0x3E0) >> 5) * 8;
			b = (pixel & 0x1F) * 8;
			*/
			
			a = (pixel & 1) * 255;
			r = ((pixel >> 11) & 31) * 8;
			g = ((pixel >> 6) & 31) * 8;
			b = ((pixel >> 1) & 31) * 8;
			
			pixel = (a << 24) | (r << 16) | (g << 8) | b;

			image.setRGB(i % width, i / width , pixel);
		}
		
		return image;
	}
	
	private void wipeOSIsASS(int maskRed, int maskGreen, int maskBlue) {
		int pixel, r, g, b, a;
		
		for(int i = 0; i < (data.capacity()); i+=2) {
			pixel = data.getShort(i);

			r = ((pixel & 0x7C00) >> 10);
			g = ((pixel & 0x3E0) >> 5);
			b = (pixel & 0x1F);

			if((r == maskRed) && (g == maskGreen) && (b == maskBlue)) {
				a = 0;
			}else{
				a = 1;
			}

			pixel = (a << 15) | (b << 10) | (g << 5) | r;
			data.putShort(i,(short) pixel);
		}

		return;
	}
	
	public void fix16bit(){
		wipeOSIsASS(0, 0, 0);
	}
	
	public void setMask(int maskRed, int maskGreen, int maskBlue){
		wipeOSIsASS(maskRed, maskGreen, maskBlue);
	}
	
	public void setAlpha(boolean value){
		int alpha = value ? 1 : 0;
		int pixel, r, g, b;
		
		for(int i = 0; i < (data.capacity()); i+=2) {
			pixel = data.getShort(i);

			r = ((pixel >> 10) & 0x1F);
			g = ((pixel >> 5) & 0x1F);
			b = (pixel & 0x1F);

			//pixel = (alpha << 15) | (b << 10) | (g << 5) | r;
			pixel = alpha | (b << 1) | (g << 6) | (r << 11);
			data.putShort(i, (short)pixel);
		}
	}
}
