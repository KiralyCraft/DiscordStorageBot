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
		
		putByteArray(offset, longData);
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
	
	public void putInt16(long offset,int theLong) throws ChunkOffsetException
	{
		byte[] longData = new byte[2];
		longData[0] = (byte)theLong;
		longData[1] = (byte)(theLong >> 8);
		
		putByteArray(offset, longData);
	}
	
	public int getInt16(long offset) throws ChunkOffsetException
	{
		byte[] longData = fetchByteArray(offset, 2);
		return ((short) longData[1] & 0xff) << 8
			       | ((short) longData[0] & 0xff);
	}
	
	public void putInt32(long offset,int theLong) throws ChunkOffsetException
	{
		byte[] longData = new byte[4];
		longData[0] = (byte)theLong;
		longData[1] = (byte)(theLong >> 8);
		longData[2] = (byte)(theLong >> 16);
		longData[3] = (byte)(theLong >> 24);
		
		putByteArray(offset, longData);
	}
	
	public int getInt32(long offset) throws ChunkOffsetException
	{
		byte[] longData = fetchByteArray(offset, 4);
		return  ((int) longData[3] & 0xff) << 24
			       | ((int) longData[2] & 0xff) << 16
			       | ((int) longData[1] & 0xff) << 8
			       | ((int) longData[0] & 0xff);
	}
	
	public void putString(long offset,String theString) throws ChunkOffsetException
	{
		byte[] stringBytes = theString.getBytes();
		putInt16(offset,stringBytes.length);
		putByteArray(offset+2,stringBytes);
	}
	
	public String getString(long offset) throws ChunkOffsetException
	{
		int stringLength = getInt16(offset);
		return new String(this.fetchByteArray(offset+2, stringLength));		
	}
}
