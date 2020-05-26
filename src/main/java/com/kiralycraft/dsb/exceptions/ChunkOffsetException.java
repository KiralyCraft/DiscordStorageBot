package com.kiralycraft.dsb.exceptions;

import java.io.IOException;

import com.kiralycraft.dsb.entities.EntityID;

public class ChunkOffsetException extends ChunkIOException
{
	private static final long serialVersionUID = 1703026975306652240L;
	
	private long offset;
	
	public ChunkOffsetException(String message,long offset)
	{
		super(message);
		this.offset = offset;
	}
	@Override
	public String getMessage() 
	{
		return "Offset "+offset+" inside a chunk is out of bounds! "+super.getMessage();
	}
}
