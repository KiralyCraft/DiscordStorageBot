package com.kiralycraft.dsb.ftp;

import java.io.IOException;
import java.io.OutputStream;

import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.filesystem.TextBasedFilesystem;
import com.kiralycraft.dsb.filesystem.entries.MetadataDiscordFile;

public class NewFileStream extends OutputStream
{
	private MetadataDiscordFile newFile;
	private TextBasedFilesystem tbf;

	public NewFileStream(AbstractChunkManager acm,String filename,TextBasedFilesystem tbf)
	{
		System.out.println("Writing the file "+filename+" to folder "+tbf.getCurrentPath());
		this.newFile = new MetadataDiscordFile(acm, filename, false, 0); //Initial size is 0 bytes
		this.tbf = tbf;
	}
	
	@Override
	public void write(int b) throws IOException
	{
		this.newFile.write(b);
	}
	
	@Override
	public void flush() throws IOException
	{
		this.newFile.flush(true);
	}
	@Override
	public void close() throws IOException
	{
		flush(); //Flushes the data in memory to the disk
		
		System.out.println("Written the file "+newFile.getFilename()+" to folder "+tbf.getCurrentPath());
		if (!tbf.addFileRaw(newFile))
		{
			throw new IOException("Could not add the file to the filesystem!");
		}
	}
	
}
