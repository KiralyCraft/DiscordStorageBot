package com.kiralycraft.dsb.debug;

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
		for (int i=1;i<=1000;i++)
		{
			df.writeUTF("pisat");
		}
//		df.seek(0);
		System.out.println(df.readUTF());
		df.flush();
		
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
