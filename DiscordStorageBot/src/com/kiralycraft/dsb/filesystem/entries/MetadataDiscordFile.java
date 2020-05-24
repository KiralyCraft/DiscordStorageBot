package com.kiralycraft.dsb.filesystem.entries;

import java.nio.charset.Charset;

import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.entities.Chunk;
import com.kiralycraft.dsb.entities.EntityID;

public class MetadataDiscordFile extends DataDiscordFile
{
	protected final int MAX_STRING_LENGTH=64;
	
	public MetadataDiscordFile(AbstractChunkManager acm)
	{
		super(acm);
	}
	
	public MetadataDiscordFile(AbstractChunkManager acm, EntityID baseID) 
	{
		super(acm,baseID);
	}
	
	public String getFilename()
	{
		return getString(Chunk.getDataOffset()+8); //First 8 bytes of the data is the length 
	}
	
	public void setFilename(String filename)
	{
		putString(filename,Chunk.getDataOffset()+8);
		flushBaseChunk();
	}
	
	public boolean isFolder()
	{
		return getBaseChunk().getChunkData()[Chunk.getDataOffset()+8+MAX_STRING_LENGTH]==1;
	}
	
	public void setFolder()
	{
		getBaseChunk().getChunkData()[Chunk.getDataOffset()+8+MAX_STRING_LENGTH]=1;
		flushBaseChunk();
	}
	
	public long getLastModified()
	{
		return getBaseChunk().getLong(Chunk.getDataOffset()+8+MAX_STRING_LENGTH+1);
	}
	
	public void setLastModified(long lastModified)
	{
		getBaseChunk().setLong(Chunk.getDataOffset()+8+MAX_STRING_LENGTH+1, lastModified);
		flushBaseChunk();
	}
	
	
	private String getString(int offset)
	{
		byte[] chunkData = getBaseChunk().getChunkData();
		int targetSize = 0;
		
		for (int i=0;i<MAX_STRING_LENGTH-1;i++)
		{
			if (chunkData[i+offset]!=0)
			{
				targetSize = i;
			}
			else
			{
				break;
			}
		}
		return new String(chunkData, offset, targetSize+1, Charset.forName("UTF-8"));
	}
	private void putString(String string,int offset)
	{
		byte[] chunkData = getBaseChunk().getChunkData();
		byte[] stringData = string.getBytes(Charset.forName("UTF-8"));
		int lastByteIndex = 0;
		for (int i=0;i<MAX_STRING_LENGTH-1 && i < stringData.length;i++)
		{
			lastByteIndex = offset+i;
			chunkData[lastByteIndex]=stringData[i];
		}
		chunkData[lastByteIndex+1] = 0;
	}
}
