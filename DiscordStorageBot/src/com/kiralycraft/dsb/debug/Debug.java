package com.kiralycraft.dsb.debug;

import java.io.IOException;

import com.guichaguri.minimalftp.FTPServer;
import com.guichaguri.minimalftp.impl.NoOpAuthenticator;
import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.chunks.SingleThreadedChunkManager;
import com.kiralycraft.dsb.entities.EntityID;
import com.kiralycraft.dsb.filesystem.TextBasedFilesystem;
import com.kiralycraft.dsb.ftp.TextBasedFilesystemFTPIO;

public class Debug {

	public static void main(String[] args) throws IOException
	{
	
		FileBasedIO fbio = new FileBasedIO();
		AbstractChunkManager acm = new SingleThreadedChunkManager(fbio);
		
		TextBasedFilesystem tbf = new TextBasedFilesystem(acm, new EntityID(0));

//		System.out.println(tbf.chdir("/pisat"));
//		System.out.println(tbf.getCurrentPath());
//		System.out.println(tbf.listFiles());
//		tbf.mkdir("pisat");
//		tbf.addFilesystemFile(new File("star.png"));
//		tbf.add
		
		
//		DiscordFolder df = new DiscordFolder(acm, "penis");
		
//		File star = new File("star.png");
//		
//		MetadataDiscordFile mdf = new MetadataDiscordFile(acm, star.getName(), false, star.length());
//		
//		
//		FileInputStream fis = new FileInputStream(star);
//		int len;
//		byte[] buffer = new byte[1024];
//		while((len = fis.read(buffer))>0)
//		{
//			mdf.write(buffer,0,len);
//		}
//		fis.close();
//		mdf.flush();
//		
//		df.addFile(mdf);
//		
//		
//		System.out.println(df.listFiles().get(0).length());
//		
//		
//		MetadataDiscordFile source = df.listFiles().get(0);
//		FileOutputStream fos = new FileOutputStream(new File("star2.png"));
//		while((len = source.read(buffer))>0)
//		{
//			fos.write(buffer,0,len);
//		}
//		fos.close();
//		
		
//		MetadataDiscordFile df = new MetadataDiscordFile(acm);
//		df.setFilename("pisat.png");
//		df.writeBoolean(true);
//		df.flush();
//		
//		DiscordFolder discoFolder = new DiscordFolder(acm);
//		discoFolder.addFile(df);
////		
//		for (MetadataDiscordFile mdf:discoFolder.listFiles())
//		{
//			System.out.println(mdf.getFilename());
//		}
//		df.writeBoolean(true);
//		System.out.println(df.readBoolean());
//		FileInputStream fis = new FileInputStream(new File("star.png"));
//		int len;
//		byte[] buffer = new byte[1024];
//		while((len = fis.read(buffer))>0)
//		{
//			df.write(buffer,0,len);
//		}
//		fis.close();
//		df.flush();
//		
//		df.seek(0);
//		
//		System.out.println(df.length());
//		
//		FileOutputStream fos = new FileOutputStream(new File("star2.png"));
//		while((len = df.read(buffer))>0)
//		{
//			fos.write(buffer,0,len);
//		}
//		fos.close();
		
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
		
		TextBasedFilesystemFTPIO fs = new TextBasedFilesystemFTPIO(acm, tbf);
		// Creates a noop authenticator, which allows anonymous authentication
		NoOpAuthenticator auth = new NoOpAuthenticator(fs);

		// Creates the server with the authenticator
		FTPServer server = new FTPServer(auth);
		server.setBufferSize(1);

		// Start listening synchronously
		server.listenSync(21);
	}

}
