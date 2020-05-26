package com.kiralycraft.dsb.chunks;

import com.kiralycraft.dsb.entities.Chunk;
import com.kiralycraft.dsb.entities.EntityID;
import com.kiralycraft.dsb.filesystem.FileIOInterface;
import com.kiralycraft.dsb.log.Logger;

import java.io.IOException;
import java.util.Base64;
import java.util.logging.Level;

/**
 * This class handles the abstraction between the underlying filesystem and the {@link Chunk}s.
 *
 * @author KiralyCraft
 */
public abstract class AbstractChunkManager {
    private FileIOInterface fioi;
    private int maxChunkSizeBytes;

    public AbstractChunkManager(FileIOInterface fioi) {
        this.fioi = fioi;
        maxChunkSizeBytes = (3 * (fioi.getChunkSize() / 4)) - 2; //2 is the max number of padding = put the at end of any base64 string. There can be at most 2
    }

    /**
     * Retrieves a {@link Chunk} from the filesystem, or from the cache. All pending changes are applied.
     * <p>
     * Returns null if anything went wrong.
     *
     * @param id
     * @return
     * @throws IOException
     */
    public Chunk getChunk(EntityID id) {
        try {
//			Chunk foundChunk = getChunkFromCache(id);
//			if (foundChunk==null) //If no chunk was found in cache, it means it cannot be dirty
//			{
            String chunkData = fioi.getRawChunkData(id).trim();
            byte[] chunkByteData = Base64.getDecoder().decode(chunkData);
            return new Chunk(id, chunkByteData);
//			}
//			else
//			{
////				applyPendingChanges(foundChunk); //Changes the cached chunk too
//				return foundChunk;
//			}
        } catch (IllegalArgumentException iae) {
            Logger.log("The " + fioi.getClass().getSimpleName() + " did not provide a valid Base64 encoded string!", Level.SEVERE);
            Logger.log(iae, Level.SEVERE);
        } catch (IOException ie) {
            Logger.log(fioi.getClass().getSimpleName() + " encountered an error when fetching a Chunk!", Level.SEVERE);
            Logger.log(ie, Level.SEVERE);
        }
        return null;
    }

    /**
     * Inserts a {@link Chunk} with the given ID. If insertion failed, it should return null.
     *
     * @param chunk
     * @return
     */
    public boolean flushChunk(Chunk chunk) {
        try {
            byte[] chunkByteData = chunk.getChunkData();

            String chunkData = Base64.getEncoder().encodeToString(chunkByteData);
            return fioi.updateRawChunkData(chunk.getID(), chunkData);
        } catch (IOException e) {
            Logger.log(this.getClass().getSimpleName() + " encountered an issue when trying to insert Chunk ID: \"" + chunk.getID().getLoggableID() + "\"!", Level.SEVERE);
            Logger.log(e, Level.SEVERE);
        }

        return false;
    }

    /**
     * This method allocates a new, empty {@link Chunk}, and returns it's ID, just like insertion.
     * Returns null if anything went wrong.
     *
     * @return
     */
    public Chunk allocateChunk() {
        try {
            byte[] chunkByteData = new byte[getMaxChunkByteSize()];
            EntityID chunkID = fioi.createEmptyChunk(Base64.getEncoder().encodeToString(chunkByteData));
            Chunk toReturn = new Chunk(chunkID, chunkByteData);
            if (chunkID != null) {
                return toReturn;
            } else {
                throw new IOException("Could not flush the newly created Chunk!");
            }
        } catch (IOException e) {
            Logger.log(this.getClass().getSimpleName() + " encountered an issue when trying to create an empty Chunk!", Level.SEVERE);
            Logger.log(e, Level.SEVERE);
        }
        return null;
    }

//	/**
//	 * This method applies the pending changes to the given byte array, representing a {@link Chunk}'s data.
//	 * If there are no changes, nothing happens.
//	 *
//	 * Returns the number of changes applied.
//	 * @param chunkByteData
//	 * @param id
//	 * @param removeFromCache
//	 */
//	private int applyPendingChanges(Chunk chunk)
//	{
//		EntityID id = chunk.getID();
//		List<PendingChunkChange> pendingChanges;
//		if ((pendingChanges = pendingChunkChanges.get(id))!=null)
//		{
//			int pendingChangesCount = pendingChanges.size();
//			for (PendingChunkChange pcc:pendingChanges)
//			{
//				byte[] changedData = pcc.bytesToChange;
//				int changedDataLength = changedData.length;
//				for (int i=0;i<changedDataLength;i++)
//				{
//					chunk.getChunkData()[i+pcc.chunkInternalOffset] = changedData[i];
//				}
//			}
//			if (pendingChangesCount > 0)
//			{
//				pendingChunkChanges.remove(id);
//			}
//			return pendingChangesCount;
//		}
//		return 0; //No changes
//	}

    /**
     * Returns the size in bytes of a chunk.
     * <p>
     * This class should automatically calculate the number of bytes available in a chunk, based on the value returned by the FileIOInterface
     *
     * @return
     */
    public int getMaxChunkByteSize() {
        return maxChunkSizeBytes;
    }

    private class PendingChunkChange {
        int chunkInternalOffset;
        byte[] bytesToChange;
    }

//	/**
//	 * Adds a pending change for the supplied {@link EntityID}
//	 * @param chunkID
//	 * @param theArray
//	 * @param offset
//	 */
//	public void addPendingChange(EntityID chunkID, byte[] theArray, int offset)
//	{
//		synchronized(pendingChunkChanges)
//		{
//			cacheChunk(chunkID); //Caches it only if it does not exist
//
//			List<PendingChunkChange> pendingChanges;
//			if ((pendingChanges = pendingChunkChanges.get(chunkID))==null)
//			{
//				pendingChanges = new ArrayList<PendingChunkChange>();
//				pendingChunkChanges.put(chunkID, pendingChanges);
//			}
//			PendingChunkChange pccToAdd = new PendingChunkChange();
//			pccToAdd.bytesToChange = theArray;
//			pccToAdd.chunkInternalOffset = offset;
//			pendingChanges.add(pccToAdd);
//
//
//		}
//	}

//	/**
//	 * This method caches the given Chunk only if it is not already cached
//	 * @param chunkID
//	 */
//	private void cacheChunk(EntityID chunkID)
//	{
//		if (!cachedChunks.containsKey(chunkID))
//		{
//			cachedChunks.put(chunkID, getChunk(chunkID));
//		}
//	}
}
