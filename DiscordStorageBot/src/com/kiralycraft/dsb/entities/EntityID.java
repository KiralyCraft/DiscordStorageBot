package com.kiralycraft.dsb.entities;

import java.util.Arrays;

/**
 * A keystroke-friendly ID system.
 * @author KiralyCraft
 *
 * @param <T>
 */
public class EntityID
{
	private long[] id;
	
	public EntityID(long baseID,long sectionID,long entityID)
	{
		this.id = new long[3];
		this.id[0] = baseID;
		this.id[1] = sectionID;
		this.id[2] = entityID;
	}
	
	/**
	 * Used for testing
	 * @see Chunk#Chunk(long, long, long)
	 * @param id
	 */
	public EntityID(long id)
	{
		this(id,id,id);
	}

	/**
	 * If for some reason this ID needs to be printed, use this method to get a log-friendly representation.
	 * @return
	 */
	public String getLoggableID() 
	{
		return "EntityID{bid="+id[0]+",sectionID="+id[1]+",entityID="+id[2]+"}";
	}
	
	public long getBaseID()
	{
		return id[0];
	}
	
	public long getSectionID()
	{
		return id[1];
	}
	
	public long getEntityID()
	{
		return id[2];
	}
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(id);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntityID other = (EntityID) obj;
		if (!Arrays.equals(id, other.id))
			return false;
		return true;
	}
}
