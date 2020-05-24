package com.kiralycraft.dsb.debug;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.chunks.SingleThreadedChunkManager;
import com.kiralycraft.dsb.filesystem.entries.DiscordFile;

public class Debug {

	public static void main(String[] args) throws IOException
	{
	
		FileBasedIO fbio = new FileBasedIO(9);
		AbstractChunkManager acm = new SingleThreadedChunkManager(fbio);
		
		DiscordFile df = new DiscordFile(acm);
		FileInputStream fis = new FileInputStream(new File("star.png"));
		int len;
		byte[] buffer = new byte[1024];
		while((len = fis.read(buffer))>0)
		{
			df.write(buffer,0,len);
		}
		fis.close();
		df.flush();
		
		df.seek(0);
		
		System.out.println(df.length());
		
		FileOutputStream fos = new FileOutputStream(new File("star2.png"));
		while((len = df.read(buffer))>0)
		{
			fos.write(buffer,0,len);
		}
		fos.close();
		
//		FileAllocationTable fat = new FileAllocationTable(acm);
//		tfs.buildEntry(fat);
//		
//		tfs.flushEntry(fat);
//		
//		FileEntry fe = new FileEntry(acm);
//		
//		fe.setGroup("Unknown");
//		fe.setLastModified(System.currentTimeMillis());
//		fe.setName("Unknown");
//		fe.setOwner("Unknown");
//		fe.setPath("/Unknown");
//		fe.setPermissions(777);
//		fe.setSize(20);
//		
//		tfs.buildEntry(fe);
//		tfs.flushEntry(fe);
//		
//		fat.addFile(fe);
//		tfs.flushEntry(fat);
		
//		TextBasedFilesystemFTPIO fs = new TextBasedFilesystemFTPIO();
//		// Creates a noop authenticator, which allows anonymous authentication
//		NoOpAuthenticator auth = new NoOpAuthenticator(fs);
//
//		// Creates the server with the authenticator
//		FTPServer server = new FTPServer(auth);
//
//		// Start listening synchronously
//		server.listenSync(21);
	}

}
