package com.kiralycraft.dsb.filesystem;

import java.io.IOException;

import com.kiralycraft.dsb.entities.EntityID;

/**
 * This interface provides basic level functions for storing and retrieving raw text-based filesystem data.
 * @author KiralyCraft
 *
 */
public interface FileIOInterface 
{
	/**
	 * Implementing classes should define a maximum size of one chunk, in bytes.
	 * 
	 * For a plain ASCII encoded {@link String}, this is the number of characters in the string.
	 * Under no circumstances should this limit be exceeded by the application.
	 * @return
	 */
	public int getChunkSize();
	
	/**
	 * This method should retrieve the given String associated with the provided {@link EntityID}.
	 * 
	 * It is expected for this method to block.
	 * WARNING: If anything goes wrong, this method should throw an {@link IOException} with details.
	 * @param eid
	 * @return
	 */
	public String getRawChunkData(EntityID eid) throws IOException;
	
	/**
	 * This method should update the String associated with the provided {@link EntityID}, with the new data.
	 * 
	 * It is expected for this method to block.
	 * 
	 * Returns false if anything goes wrong.
	 * 
	 * WARNING: If anything goes wrong, this method should throw an {@link IOException} with details.
	 * @param eid
	 * @return
	 */
	public boolean updateRawChunkData(EntityID eid,String newData) throws IOException;
	
	/**
	 * This method should create a new chunk, and return the associated {@link EntityID}
	 * 
	 * It is expected for this method to block.
	 * 
	 * WARNING: If anything goes wrong, this method should throw an {@link IOException} with details.
	 * @param emptyChunkData 
	 * @param eid
	 * @return
	 */
	public EntityID createEmptyChunk(String emptyChunkData) throws IOException;
}
