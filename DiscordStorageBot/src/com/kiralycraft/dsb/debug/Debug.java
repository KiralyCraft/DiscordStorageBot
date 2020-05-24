package com.kiralycraft.dsb.debug;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.chunks.SingleThreadedChunkManager;
import com.kiralycraft.dsb.entities.EntityID;
import com.kiralycraft.dsb.filesystem.HighLevelTextFS;
import com.kiralycraft.dsb.filesystem.TextFileSystem;

public class Debug {

	public static void main(String[] args) throws IOException
	{
	
		FileBasedIO fbio = new FileBasedIO(9);
		AbstractChunkManager acm = new SingleThreadedChunkManager(fbio);
		
		List<EntityID> fatAddresses = new ArrayList<EntityID>();
		for (int i=0;i<=8;i++)
		{
			fatAddresses.add(new EntityID(i));
		}
		TextFileSystem tfs = new TextFileSystem(acm,fatAddresses);
		HighLevelTextFS hltf = new HighLevelTextFS(tfs, acm);
	
		hltf.addFile(new File("star.png"), "/");
		
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
