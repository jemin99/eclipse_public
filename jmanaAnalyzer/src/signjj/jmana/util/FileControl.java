package signjj.jmana.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileControl {
	public boolean download(String urlStr, String fileName) {
		boolean result = false;
		
		try {
			URL url = new URL(urlStr);
			URLConnection uCon = url.openConnection();
			//int fileSize = uCon.getContentLength();
			
			OutputStream outStream = new BufferedOutputStream(new FileOutputStream(fileName));
			InputStream is = uCon.getInputStream();
			
			int ByteRead = 0;
			//int ByteWritten = 0;
			
			byte[] buf = new byte[1024];
			
			while((ByteRead = is.read(buf)) != -1) {
				outStream.write(buf, 0, ByteRead);
				//ByteWritten += ByteRead;
			}
			
			is.close();
			outStream.close();
			
			result = true;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public ByteBuffer read(String fileName) {
		ByteBuffer buffer = null;

		try {
			RandomAccessFile aFile = new RandomAccessFile(fileName, "r");
			FileChannel inChannel = aFile.getChannel();
			long fileSize = inChannel.size();
			buffer = ByteBuffer.allocate((int) fileSize);
			inChannel.read(buffer);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
			
		return buffer;
	}
	
	public void createFolder(String folderName) {
		(new File(folderName)).mkdir();
	}
}
