package com.kiralycraft.dsb.exceptions;

import java.io.FileNotFoundException;

import com.kiralycraft.dsb.entities.EntityID;

public class ChunkNotFoundException extends FileNotFoundException 
{
	private static final long serialVersionUID = 1703026975306652240L;
	
	private EntityID eid;
	
	public ChunkNotFoundException(EntityID eid,String message)
	{
		super(message);
		this.eid = eid;
	}
	@Override
	public String getMessage() 
	{
		return "Chunk with EntityID: \""+eid.getLoggableID()+"\" does not exist! Details: "+super.getMessage();
	}

}
