package com.kiralycraft.dsb.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.filesystem.entries.FileEntry;

public class HighLevelTextFS 
{
	private TextFileSystem tfs;
	private AbstractChunkManager acm;

	public HighLevelTextFS(TextFileSystem tfs,AbstractChunkManager acm)
	{
		this.tfs = tfs;
		this.acm = acm;
	}
	
	public boolean addFile(File theFile,String path)
	{
		FileEntry fe = new FileEntry(acm, (long) (theFile.length()*1.5));
		fe.setLastModified(theFile.lastModified());
		fe.setPath(path+"/"+theFile.getName());
		fe.setPermissions(777);
		fe.setSize(theFile.length());
		
		tfs.buildEntry(fe);
		
		try
		{
			FileInputStream fis = new FileInputStream(theFile);
			int len;
			byte[] buffer = new byte[1024];
			
			long currentOffset = 0;
			while((len = fis.read(buffer))>0)
			{
				fe.writeBytes(buffer, (int) currentOffset, len);
			}
			fis.close();
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		tfs.flushEntry(fe);
		
		tfs.getFAT().addFile(fe);
		tfs.flushEntry(tfs.getFAT());
		
		return true;
	}
}
