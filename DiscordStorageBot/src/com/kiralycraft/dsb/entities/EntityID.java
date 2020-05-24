package com.kiralycraft.dsb.entities;

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
}
