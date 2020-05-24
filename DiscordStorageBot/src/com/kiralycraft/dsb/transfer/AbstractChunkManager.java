package com.kiralycraft.dsb.transfer;

import com.kiralycraft.dsb.entities.Chunk;
import com.kiralycraft.dsb.entities.EntityID;
import com.kiralycraft.dsb.filesystem.FileIOInterface;

/**
 * This class handles the abstraction between the underlying filesystem and the {@link Chunk}s. 
 * @author KiralyCraft
 *
 */
public abstract class AbstractChunkManager
{
	private FileIOInterface fioi;

	public AbstractChunkManager(FileIOInterface fioi)
	{
		this.fioi = fioi;
	}
	

	public Chunk getChunk(EntityID id)
	{
		//TODO
		return null;
	}
}
