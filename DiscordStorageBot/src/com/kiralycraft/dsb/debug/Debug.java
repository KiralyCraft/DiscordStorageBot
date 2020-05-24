package com.kiralycraft.dsb.debug;

import java.io.IOException;

import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.chunks.SingleThreadedChunkManager;
import com.kiralycraft.dsb.filesystem.TextFileSystem;
import com.kiralycraft.dsb.filesystem.entries.FileAllocationTable;

public class Debug {

	public static void main(String[] args) throws IOException
	{
//		// Uses the current working directory as the root
//		File root = new File(".");
//
//		// Creates a native file system
//		NativeFileSystem fs = new NativeFileSystem(root);
//
//		// Creates a noop authenticator, which allows anonymous authentication
//		NoOpAuthenticator auth = new NoOpAuthenticator(fs);
//
//		// Creates the server with the authenticator
//		FTPServer server = new FTPServer(auth);
//
//		// Start listening synchronously
//		server.listenSync(21);
		
		FileBasedIO fbio = new FileBasedIO();
		AbstractChunkManager acm = new SingleThreadedChunkManager(fbio);
		TextFileSystem tfs = new TextFileSystem(acm);
		
		FileAllocationTable fat = new FileAllocationTable(acm);
		tfs.buildEntry(fat);
		
		tfs.flushEntry(fat);
	}

}
