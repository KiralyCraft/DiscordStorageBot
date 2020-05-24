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

	public void setChunkData(byte[] chunkData) 
	{
		this.chunkData = chunkData;
	}

	public void setByte(int i, byte b)
	{
		chunkData[i] = b;
	}
	
}
