package com.kiralycraft.dsb.filesystem.entries;

import java.util.ArrayList;

import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.entities.Chunk;
import com.kiralycraft.dsb.entities.EntityID;
import com.kiralycraft.dsb.exceptions.ChunkOffsetException;

public abstract class AbstractFilesystemEntry
{
	private ArrayList<EntityID> containedChunks;
	private AbstractChunkManager acm;
	
	public AbstractFilesystemEntry(AbstractChunkManager acm)
	{
		this.acm = acm;
	}
		
	/**
	 * Subclasses should define their own entry byte size.
	 * By default, the size of any entry is the maximum size allowed by a {@link Chunk}
	 * @return
	 */
	public int getEntryByteSize()
	{
		return acm.getMaxChunkByteSize();
	}
	
	/**
	 * Returns the chunk number that contains this offset.
	 * 
	 * If the specified offset is out of range, it throws an exception.
	 * @param offset
	 * @return
	 * @throws ChunkOffsetException 
	 */
	private int getChunkNumberContainingOffset(long offset) throws ChunkOffsetException
	{
		if (offset >= containedChunks.size()*getEntryByteSize() || offset < 0)
		{
			throw new ChunkOffsetException("Index "+offset+" is out of bounds for chunk with size "+acm.getMaxChunkByteSize(), offset);
		}
		else
		{
			return (int) (offset/acm.getMaxChunkByteSize());
		}
	}
	
	protected void putByteArray(byte[] theArray,long offset) throws ChunkOffsetException
	{
		int bytesLeftToWrite = theArray.length;
		
		int offsetInsideChunk = (int) (offset%acm.getMaxChunkByteSize());
		if (bytesLeftToWrite + offsetInsideChunk < acm.getMaxChunkByteSize()) //If we can fit the data in this remaining chunk
		{
			EntityID chunkID = containedChunks.get(getChunkNumberContainingOffset(offset));
			acm.addPendingChange(chunkID,theArray,offsetInsideChunk);
		}
		else
		{
			long currentOffset = offset;
			int writtenBytes = 0;
			//If we cannot
			while(bytesLeftToWrite!=0)
			{
				int offsetInsideCurrentChunk = (int) (currentOffset%acm.getMaxChunkByteSize());
				EntityID currentChunkID = containedChunks.get(getChunkNumberContainingOffset(currentOffset));
				int writtenBytesThisRound = 0;
				
				int writableNow = acm.getMaxChunkByteSize()-offsetInsideCurrentChunk;
				if (writableNow >= bytesLeftToWrite)
				{
					writtenBytesThisRound = bytesLeftToWrite;
					byte[] toWrite = new byte[writtenBytesThisRound];
					System.arraycopy(theArray, writtenBytes, toWrite, 0, writtenBytesThisRound);
					
					acm.addPendingChange(currentChunkID, toWrite, offsetInsideCurrentChunk);
					
					bytesLeftToWrite = 0;
					writtenBytes += writtenBytesThisRound;
				}
				else
				{
					writtenBytesThisRound = writableNow;
					byte[] toWrite = new byte[writtenBytesThisRound];
					System.arraycopy(theArray, writtenBytes, toWrite, 0, writtenBytesThisRound);
					
					acm.addPendingChange(currentChunkID, toWrite, offsetInsideCurrentChunk);
					
					bytesLeftToWrite -= writtenBytesThisRound;
					writtenBytes += writtenBytesThisRound;
				}
				currentOffset+=writtenBytesThisRound;
			}
		}
	}
	
	protected byte[] fetchByteArray(long offset,int size) throws ChunkOffsetException
	{
		byte[] toReturn = new byte[size];
		
		int bytesLeftToRead = size;
		
		int offsetInsideChunk = (int) (offset%acm.getMaxChunkByteSize());
		if (bytesLeftToRead + offsetInsideChunk < acm.getMaxChunkByteSize()) //If we can fit the data in this remaining chunk
		{
			EntityID chunkID = containedChunks.get(getChunkNumberContainingOffset(offset));
			byte[] chunkData = acm.getChunk(chunkID).getChunkData();
			
			System.arraycopy(chunkData, 0, toReturn, 0, bytesLeftToRead);
		}
		else
		{
			long currentOffset = offset;
			int writtenBytes = 0;
			//If we cannot
			while(bytesLeftToRead!=0)
			{
				int offsetInsideCurrentChunk = (int) (currentOffset%acm.getMaxChunkByteSize());
				EntityID currentChunkID = containedChunks.get(getChunkNumberContainingOffset(currentOffset));
				int writtenBytesThisRound = 0;
				
				int writableNow = acm.getMaxChunkByteSize()-offsetInsideCurrentChunk;
				if (writableNow >= bytesLeftToRead)
				{
					writtenBytesThisRound = bytesLeftToRead;
					
					byte[] chunkData = acm.getChunk(currentChunkID).getChunkData();
					System.arraycopy(chunkData, 0, toReturn, writtenBytes, writtenBytesThisRound);
					
					bytesLeftToRead = 0;
					writtenBytes += writtenBytesThisRound;
				}
				else
				{
					writtenBytesThisRound = writableNow;
					
					byte[] chunkData = acm.getChunk(currentChunkID).getChunkData();
					System.arraycopy(chunkData, 0, toReturn, writtenBytes, writtenBytesThisRound);
					
					bytesLeftToRead -= writtenBytesThisRound;
					writtenBytes += writtenBytesThisRound;
				}
				currentOffset+=writtenBytesThisRound;
			}
		}
		
		return toReturn;
	}

	public void setAllocatedChunks(ArrayList<EntityID> allocatedChunks)
	{
		this.containedChunks = allocatedChunks;		
	}

	/**
	 * All entries should have a basic creation method, that initializes their data.
	 * Returns false if anything went wrong.
	 */
	public abstract boolean buildNew();

	/**
	 * Returns the contained {@link EntityID}s corresponding to the chunks.
	 * @return
	 */
	public ArrayList<EntityID> getContainedChunks()
	{
		return containedChunks;
	}
}
