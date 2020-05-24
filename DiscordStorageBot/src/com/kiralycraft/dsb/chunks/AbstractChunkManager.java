package com.kiralycraft.dsb.chunks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
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
	private class PendingChunkChange
	{
		int chunkInternalOffset;
		byte[] bytesToChange;
	}
	
	private class CachedChunk
	{
		boolean dirty;
		Chunk theChunk;
	}
	
	private FileIOInterface fioi;
	private int maxChunkSizeBytes;
	private HashMap<EntityID,CachedChunk> cachedChunks;
	private HashMap<EntityID,List<PendingChunkChange>> pendingChunkChanges;

	public AbstractChunkManager(FileIOInterface fioi)
	{
		this.fioi = fioi;
		maxChunkSizeBytes = (3 * (fioi.getChunkSize() / 4)) - 2; //2 is the max number of padding = put the at end of any base64 string. There can be at most 2
		cachedChunks = new HashMap<EntityID,CachedChunk>();
		pendingChunkChanges = new HashMap<EntityID,List<PendingChunkChange>>();
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
			CachedChunk foundChunk = getChunkFromCache(id);			
			applyPendingChanges(foundChunk); //Changes the cached chunk too
			return foundChunk.theChunk;
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
	 * This method fetches a {@link Chunk} either from the cache, or directly from the underlying filesystem.
	 * @param id
	 * @return
	 * @throws IOException
	 */
	private CachedChunk getChunkFromCache(EntityID id) throws IOException
	{
		CachedChunk foundChunk;
		if ((foundChunk = cachedChunks.get(id))==null)
		{
			String chunkData = fioi.getRawChunkData(id).trim();
			byte[] chunkByteData = Base64.getDecoder().decode(chunkData);
			
			if (chunkByteData.length == 0)
			{
				chunkByteData = new byte[getMaxChunkByteSize()];
			}
			
			Chunk theChunk = new Chunk(id, chunkByteData);
			CachedChunk toReturn = new CachedChunk();
			toReturn.dirty = false;
			toReturn.theChunk = theChunk;
			cachedChunks.put(id, toReturn);
			
			return toReturn;
		}
		else
		{
			return foundChunk;
		}
	}

	/**
	 * Inserts a {@link Chunk} with the given ID. If insertion failed, it should return null.
	 * @param chunk
	 * @return
	 */
	public boolean flushPendingChanges(EntityID id)
	{
		try
		{
			CachedChunk cachedChunk = getChunkFromCache(id); //Get the chunk data live, or from the cache.
			
			
			EntityID chunkID = cachedChunk.theChunk.getID();
			byte[] chunkByteData = cachedChunk.theChunk.getChunkData();
			
			applyPendingChanges(cachedChunk); //Apply the pending chunk changes, and delete them.
			
			if (cachedChunk.dirty)
			{ 
				//Only actually do it if there are pending changes
				cachedChunks.remove(chunkID);
				
				String chunkData = Base64.getEncoder().encodeToString(chunkByteData);
				return fioi.updateRawChunkData(cachedChunk.theChunk.getID(),chunkData);
			}
		}
		catch(IOException e)
		{
			Logger.log(this.getClass().getSimpleName()+" encountered an issue when trying to insert Chunk ID: \""+id.getLoggableID()+"\"!",Level.SEVERE);
			Logger.log(e,Level.SEVERE);
		}

		return false;
	}
	
	/**
	 * This method applies the pending changes to the given byte array, representing a {@link Chunk}'s data.
	 * If there are no changes, nothing happens.
	 * 
	 * Returns the number of changes applied.
	 * @param chunkByteData
	 * @param id
	 * @param removeFromCache 
	 */
	private int applyPendingChanges(CachedChunk cachedChunk)
	{
		EntityID id = cachedChunk.theChunk.getID();
		List<PendingChunkChange> pendingChanges;
		if ((pendingChanges = pendingChunkChanges.get(id))!=null)
		{
			int pendingChangesCount = pendingChanges.size();
			for (PendingChunkChange pcc:pendingChanges)
			{
				byte[] changedData = pcc.bytesToChange;
				int changedDataLength = changedData.length;
				for (int i=0;i<changedDataLength;i++)
				{
					cachedChunk.theChunk.setByte(i+pcc.chunkInternalOffset,changedData[i]);
				}
			}
			if (pendingChangesCount > 0)
			{
				cachedChunk.dirty = true;
			}
			return pendingChangesCount;
		}
		pendingChunkChanges.remove(id);
		return 0; //No changes
	}

	/**
	 * This method allocates a new, empty {@link Chunk}, and returns it's ID, just like insertion.
	 * Returns null if anything went wrong.
	 * @return 
	 */
	public EntityID allocateChunk()
	{
		try
		{
			return fioi.createEmptyChunk();
		}
		catch(IOException e)
		{
			Logger.log(this.getClass().getSimpleName()+" encountered an issue when trying to create an empty Chunk!",Level.SEVERE);
			Logger.log(e,Level.SEVERE);
			return null;
		}
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
	 * Adds a pending change for the supplied {@link EntityID}
	 * @param chunkID
	 * @param theArray
	 * @param offset
	 */
	public void addPendingChange(EntityID chunkID, byte[] theArray, int offset)
	{
		synchronized(pendingChunkChanges)
		{
			List<PendingChunkChange> pendingChanges;
			if ((pendingChanges = pendingChunkChanges.get(chunkID))==null)
			{
				pendingChanges = new ArrayList<PendingChunkChange>();
				pendingChunkChanges.put(chunkID, pendingChanges);
			}
			PendingChunkChange pccToAdd = new PendingChunkChange();
			pccToAdd.bytesToChange = theArray;
			pccToAdd.chunkInternalOffset = offset;
			pendingChanges.add(pccToAdd);
		}
	}
}
