package com.kiralycraft.dsb.filesystem.entries;

import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.exceptions.ChunkOffsetException;

public class FileEntry extends DataHoldingEntry
{
	private long size;
	private String path; //exactly 255
	private int permissions;
	private long lastModified;
	private long dataSize;
	
	
	public FileEntry(AbstractChunkManager acm,long dataSize)
	{
		super(acm);
		this.dataSize = dataSize;
	}

	@Override
	public boolean buildNew()
	{
		try
		{
			this.putLong(0, size); 
			this.putString(8, path);
			this.putInt16(265, permissions); 
			this.putLong(273, lastModified);
			
			return true;
		} 
		catch (ChunkOffsetException e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public int getEntryByteSize()
	{
		return (int) dataSize;
	}
	
	public boolean writeBytes(byte[] theArray,int sourceOffset, int length)
	{
		try
		{
			this.putByteArray(281+sourceOffset, theArray,length);
			return true;
		} 
		catch (ChunkOffsetException e)
		{
			return false;
		}
	}
	
	public long getSize()
	{
		return size;
	}

	public void setSize(long size)
	{
		this.size = size;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public int getPermissions()
	{
		return permissions;
	}

	public void setPermissions(int permissions)
	{
		this.permissions = permissions;
	}

	public long getLastModified()
	{
		return lastModified;
	}

	public void setLastModified(long lastModified)
	{
		this.lastModified = lastModified;
	}
}
