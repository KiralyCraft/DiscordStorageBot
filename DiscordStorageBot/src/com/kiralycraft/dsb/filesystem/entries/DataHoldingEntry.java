package com.kiralycraft.dsb.filesystem.entries;

import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.exceptions.ChunkOffsetException;

public abstract class DataHoldingEntry extends AbstractFilesystemEntry
{

	public DataHoldingEntry(AbstractChunkManager acm)
	{
		super(acm);
	}

	public void putLong(long offset,long theLong) throws ChunkOffsetException
	{
		byte[] longData = new byte[8];
		longData[0] = (byte)theLong;
		longData[1] = (byte)(theLong >> 8);
		longData[2] = (byte)(theLong >> 16);
		longData[3] = (byte)(theLong >> 24);
		longData[4] = (byte)(theLong >> 32);
		longData[5] = (byte)(theLong >> 40);
		longData[6] = (byte)(theLong >> 48);
		longData[7] = (byte)(theLong >> 56);
		
		putByteArray(longData, offset);
	}
	
	public long getLong(long offset) throws ChunkOffsetException
	{
		byte[] longData = fetchByteArray(offset, 8);
		return ((long) longData[7] << 56)
			       | ((long) longData[6] & 0xff) << 48
			       | ((long) longData[5] & 0xff) << 40
			       | ((long) longData[4] & 0xff) << 32
			       | ((long) longData[3] & 0xff) << 24
			       | ((long) longData[2] & 0xff) << 16
			       | ((long) longData[1] & 0xff) << 8
			       | ((long) longData[0] & 0xff);
	}
}
