package reuo.client.rendering;

import java.nio.ByteBuffer;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;

public class TextureManager{
	GL gl;
	SortedMap<TextureSpace, Texture> spaces = new TreeMap<TextureSpace, Texture>();
	int[] generated = new int[1];
	
	public TextureManager(GL gl){
		this.gl = gl;
	}
	
	private int getTextureDimension(int x){
		int base = (int)(Math.log(x) / Math.log(2));
		return((int)Math.pow(2, base));
	}
	
	public SubTexture allocate(TextureData data){
		/* TODO: Support multiple texture pixel formats? */
		SubTexture subTexture = null;
		
		int width = data.getWidth();
		int height = data.getHeight();
		
		for(TextureSpace space : spaces.keySet()){
			subTexture = space.allocate(width, height);
			
			if(subTexture != null){
				subTexture.parent = spaces.get(space);
				return(subTexture);
			}
		}
		
		int spaceWidth = Math.min(512, getTextureDimension(width));
		int spaceHeight = Math.min(512, getTextureDimension(height));
		
		/* Nothing fit this texture so allocate a new larger space */
		TextureSpace space = new TextureSpace(spaceWidth, spaceHeight);
		ByteBuffer pixelData = ByteBuffer.allocate(2 * space.getWidth() * space.getHeight());
		
		TextureData spaceData = new TextureData(
			data.getInternalFormat(),
			space.getWidth(), space.getHeight(),
			0,
			data.getPixelFormat(),
			data.getPixelType(),
			false,
			false,
			false,
			pixelData,
			null
		);
		
		gl.glGenTextures(1, generated, 0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, generated[0]);
		Texture texture = TextureIO.newTexture(spaceData);
		spaces.put(space, texture);
		
		return(subTexture);
	}
}