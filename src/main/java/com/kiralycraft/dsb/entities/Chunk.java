package com.kiralycraft.dsb.entities;

public class Chunk
{
	private EntityID id;
	private byte[] chunkData;
	
	public Chunk(EntityID id, byte[] chunkData) 
	{
		this.id = id;
		this.chunkData = chunkData;		
	}
	
	public EntityID getID() 
	{
		return id;
	}

	public void setID(EntityID id) 
	{
		this.id = id;
	}

	public byte[] getChunkData() 
	{
		return chunkData;
	}
	
	/**
	 * Returns the next Chunk's ID, or null if it does not exist
	 * @return
	 */
	public EntityID getNext()
	{
		return getEntityID(0);
	}

	/**
	 * Returns the previous Chunk's ID, or null if it does not exist.
	 * @return
	 */
	public EntityID getPrevious()
	{
		return getEntityID(3);
	}
	
	private EntityID getEntityID(int i)
	{
		long baseID = getLong(i*8);
		if (baseID != 0)
		{
			long sectionID = getLong((i+1)*8);
			long entityID = getLong((i+2)*8);
			
			return new EntityID(baseID,sectionID,entityID);
		}
		else
		{
			return null;
		}
	}
	
	public void setNext(EntityID id)
	{
		setEntityID(0,id);
	}
	public void setPrevious(EntityID id)
	{
		setEntityID(3,id);
	}
	private void setEntityID(int i, EntityID id)
	{
		if (id == null)
		{
			setLong(i*8,0);
			setLong((i+1)*8,0);
			setLong((i+2)*8,0);
		}
		else
		{
			setLong(i*8,id.getBaseID());
			setLong((i+1)*8,id.getSectionID());
			setLong((i+2)*8,id.getEntityID());
		}
	}

	/**
	 * The offset in bytes where data can begin
	 * @return
	 */
	public static int getDataOffset()
	{
		return 3*8*2;
	}

	public long getLong(int offset)
	{
		return ((long) chunkData[offset+7] << 56)
	       | ((long) chunkData[offset+6] & 0xff) << 48
	       | ((long) chunkData[offset+5] & 0xff) << 40
	       | ((long) chunkData[offset+4] & 0xff) << 32
	       | ((long) chunkData[offset+3] & 0xff) << 24
	       | ((long) chunkData[offset+2] & 0xff) << 16
	       | ((long) chunkData[offset+1] & 0xff) << 8
	       | ((long) chunkData[offset+0] & 0xff);
	}
	
	public void setLong(int offset,long theLong)
	{
		chunkData[offset+0] = (byte)theLong;
		chunkData[offset+1] = (byte)(theLong >> 8);
		chunkData[offset+2] = (byte)(theLong >> 16);
		chunkData[offset+3] = (byte)(theLong >> 24);
		chunkData[offset+4] = (byte)(theLong >> 32);
		chunkData[offset+5] = (byte)(theLong >> 40);
		chunkData[offset+6] = (byte)(theLong >> 48);
		chunkData[offset+7] = (byte)(theLong >> 56);
	}
}
