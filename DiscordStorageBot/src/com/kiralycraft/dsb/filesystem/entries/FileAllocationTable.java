package com.kiralycraft.dsb.filesystem.entries;

import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.entities.EntityID;
import com.kiralycraft.dsb.exceptions.ChunkOffsetException;

public class FileAllocationTable extends DataHoldingEntry
{
	public FileAllocationTable(AbstractChunkManager acm)
	{
		super(acm);
	}

	@Override
	public int getEntryByteSize()
	{
		int chunkIDLength = 8*3; //24 bytes for the ID, three longs
		return chunkIDLength*512; //512 files per FAT
	}

	@Override
	public boolean buildNew()
	{
		try
		{
			putLong(0, 0);//How many entries
		} catch (ChunkOffsetException e)
		{
			e.printStackTrace();
			return false;
		} 
		return true;
	}
	
	public boolean addFile(FileEntry fe)
	{
		try
		{
			long currentEntries = getLong(0);
			
			System.out.println(currentEntries);
			
			long currentOffset = 8+currentEntries*(8*3+4); //the counter + id for each file and hashcode
//			sysout
			
			EntityID baseChunkAddress = fe.getFirstChunkAddress();
			putLong(currentOffset,baseChunkAddress.getBaseID());
			putLong(currentOffset+8,baseChunkAddress.getSectionID());
			putLong(currentOffset+16,baseChunkAddress.getEntityID());
			putInt32(currentOffset+24,fe.getPath().hashCode());
			
			putLong(0,currentEntries+1);
			return true;
		} 
		catch (ChunkOffsetException e)
		{
			e.printStackTrace();
		} 
		return false;
	}
}
