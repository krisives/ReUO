package reuo;

import reuo.resources.Font;

public class Speech{
	public Element speaker;
	public Hue hue;
	public String text;
	public Font font;
	public Type type;
	
	public static enum Type{
		REGULAR		(0),
		BROADCAST	(1),
		EMOTE		(2),
		LABEL		(6),
		EMPHASIS	(7),
		WHISPER		(8),
		YELL		(9),
		SPELL		(10);
		
		final int id;
		
		Type(int id){
			this.id = id;
		}
		
		public static Type fromIdentifier(int id){
			Type[] types = Type.values();
			
			if(id < 0 || id >= types.length){
				return(null);
			}
			
			return(types[id]);
		}
	}
}