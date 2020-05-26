package com.kiralycraft.dsb.debug;

import java.io.IOException;

public class Debug {

	public static void main(String[] args) throws IOException, InterruptedException
	{
		long id = 714890500661837864L;
		
//		System.out.println((id >> 22)+1420070400000L);
		
		System.out.println((id & 0x3E0000) >> 17);
		System.out.println((id & 0x1F000) >> 12);
		System.out.println((id & 0xFFF));
		
//		while(true)
//		{
//			System.out.println((System.currentTimeMillis()/5000)%6);
//			Thread.sleep(1000);
//		}
//		FileBasedIO fbio = new FileBasedIO();
//		AbstractChunkManager acm = new SingleThreadedChunkManager(fbio);
//		
//		TextBasedFilesystem tbf = new TextBasedFilesystem(acm, new EntityID(0));
//		
//		TextBasedFilesystemFTPIO fs = new TextBasedFilesystemFTPIO(acm, tbf);
//		// Creates a noop authenticator, which allows anonymous authentication
//		NoOpAuthenticator auth = new NoOpAuthenticator(fs);
//
//		// Creates the server with the authenticator
//		FTPServer server = new FTPServer(auth);
//		server.setBufferSize(1);
//
//		// Start listening synchronously
//		server.listenSync(21);
	}
	
	public long getMillisFromID(long snowflake)
	{
		return (snowflake >> 22)+1420070400000L;
	}

}
