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
		return "The requested chunk operation regarding EntityID: \""+eid.getLoggableID()+"\" has failed because: "+super.getMessage();
	}

}
