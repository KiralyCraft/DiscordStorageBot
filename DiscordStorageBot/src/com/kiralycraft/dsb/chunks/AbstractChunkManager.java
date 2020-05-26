package com.kiralycraft.dsb.chunks;

import java.io.IOException;
import java.util.Base64;
import java.util.logging.Level;

import com.kiralycraft.dsb.entities.Chunk;
import com.kiralycraft.dsb.entities.EntityID;
import com.kiralycraft.dsb.filesystem.FileIOInterface;
import com.kiralycraft.dsb.log.Logger;

/**
 * This class handles the abstraction between the underlying filesystem and the {@link Chunk}s. 
 * @author KiralyCraft
 *
 */
public abstract class AbstractChunkManager
{
	private FileIOInterface fioi;
	private int maxChunkSizeBytes;

	public AbstractChunkManager(FileIOInterface fioi)
	{
		this.fioi = fioi;
		maxChunkSizeBytes = (3 * (fioi.getChunkSize() / 4)) - 2; //2 is the max number of padding = put the at end of any base64 string. There can be at most 2
	}
	
	/**
	 * Retrieves a {@link Chunk} from the filesystem, or from the cache. All pending changes are applied.
	 * 
	 * Returns null if anything went wrong.
	 * 
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public Chunk getChunk(EntityID id)
	{
		try
		{
			String chunkData = fioi.getRawChunkData(id).trim();
			byte[] chunkByteData = Base64.getDecoder().decode(chunkData);				
			return new Chunk(id, chunkByteData);
		}
		catch(IllegalArgumentException iae)
		{
			Logger.log("The "+fioi.getClass().getSimpleName()+" did not provide a valid Base64 encoded string!",Level.SEVERE);
			Logger.log(iae,Level.SEVERE);
		}
		catch(IOException ie)
		{
			Logger.log(fioi.getClass().getSimpleName()+" encountered an error when fetching a Chunk!",Level.SEVERE);
			Logger.log(ie,Level.SEVERE);
		}
		return null;
	}

	/**
	 * Inserts a {@link Chunk} with the given ID. If insertion failed, it should return null.
	 * @param chunk
	 * @return
	 */
	public boolean flushChunk(Chunk chunk)
	{
		try
		{			
			byte[] chunkByteData = chunk.getChunkData();
			return fioi.updateRawChunkData(chunk.getID(),encodeChunkData(chunkByteData));
		}
		catch(IOException e)
		{
			Logger.log(this.getClass().getSimpleName()+" encountered an issue when trying to insert Chunk ID: \""+chunk.getID().getLoggableID()+"\"!",Level.SEVERE);
			Logger.log(e,Level.SEVERE);
		}

		return false;
	}

	/**
	 * This method allocates a new, empty {@link Chunk}, and returns it's ID, just like insertion.
	 * Returns null if anything went wrong.
	 * @return 
	 */
	public Chunk allocateChunk()
	{
		try
		{
			byte[] chunkByteData = new byte[getMaxChunkByteSize()];
			EntityID chunkID = fioi.createEmptyChunk(encodeChunkData(chunkByteData));
			Chunk toReturn = new Chunk(chunkID, chunkByteData);
			if (chunkID!=null)
			{
				return toReturn;
			}
			else
			{
				throw new IOException("Could not flush the newly created Chunk!");
			}
		}
		catch(IOException e)
		{
			Logger.log(this.getClass().getSimpleName()+" encountered an issue when trying to create an empty Chunk!",Level.SEVERE);
			Logger.log(e,Level.SEVERE);
		}
		return null;
	}
	
	/**
	 * Returns the size in bytes of a chunk. 
	 * 
	 * This class should automatically calculate the number of bytes available in a chunk, based on the value returned by the FileIOInterface
	 * @return
	 */
	public int getMaxChunkByteSize()
	{
		return maxChunkSizeBytes;		
	}
	
	/**
	 * This is a helper method for encoding the supplied data in a format supported by the Chunk filesystem
	 * @return
	 */
	private String encodeChunkData(byte[] chunkByteData)
	{
		return Base64.getEncoder().encodeToString(chunkByteData);
	}
}
