import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import reuo.resources.Sound;
import reuo.resources.SoundLoader;

public class SoundTest{
	public static void main(String[] args){
		try{
			SoundTest instance = new SoundTest(args[0]);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	AudioFormat audioFormat = new AudioFormat(22050, 16, 1, true, false);
	SoundLoader loader;
	
	public SoundTest(String dir) throws IOException, LineUnavailableException, InterruptedException{		
		FileInputStream idxSource = new FileInputStream(dir+"soundidx.mul");
		FileInputStream sndSource = new FileInputStream(dir+"sound.mul");
		
		loader = new SoundLoader(idxSource.getChannel(), sndSource.getChannel());
		
		DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
		
		SourceDataLine line = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
		Sound sound = loader.get(42);
		ByteBuffer buffer = sound.getData();
		byte[] bytes = buffer.array();
		int amount = (bytes.length / 2) * 2;
		
		line.open(audioFormat);
		line.start();
		
		while(amount > 0){
			int written = line.write(bytes, amount, 0);
			System.out.printf("%d of %d bytes written\n", written, amount);
			Thread.sleep(50);
			
			amount -= written;
		}
		
		
		
	}
}