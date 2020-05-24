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

	public AbstractChunkManager(FileIOInterface fioi)
	{
		this.fioi = fioi;
	}
	
	/**
	 * Retrieves a {@link Chunk} from the filesystem.
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
			String chunkData = fioi.getRawChunkData(id);
			byte[] chunkDecodedBytes = Base64.getDecoder().decode(chunkData);
			return new Chunk(id, chunkDecodedBytes);
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
	public EntityID insertChunk(Chunk chunk)
	{
		try
		{
			String chunkData = Base64.getEncoder().encodeToString(chunk.getChunkData());
			return fioi.updateRawChunkData(chunk.getID(),chunkData);
		}
		catch(IOException e)
		{
			Logger.log(this.getClass().getSimpleName()+" encountered an issue when trying to insert Chunk ID: \""+chunk.getID().getLoggableID()+"\"!",Level.SEVERE);
			Logger.log(e,Level.SEVERE);
			return null;
		}
	}
}
