package com.kiralycraft.dsb.filesystem.entries;

import com.kiralycraft.dsb.chunks.AbstractChunkManager;
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

		return true;
	}
	
}
