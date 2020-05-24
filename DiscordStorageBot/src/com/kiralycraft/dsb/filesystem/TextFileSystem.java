package com.kiralycraft.dsb.filesystem;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.entities.EntityID;
import com.kiralycraft.dsb.filesystem.entries.AbstractFilesystemEntry;
import com.kiralycraft.dsb.filesystem.entries.FileAllocationTable;
import com.kiralycraft.dsb.log.Logger;

public class TextFileSystem
{
	private FileAllocationTable fat;
	private AbstractChunkManager acm;

	/**
	 * Supply a null {@link FileAllocationTable} address to create a new one.
	 * @param acm
	 * @param fatAddress
	 */
	public TextFileSystem(AbstractChunkManager acm,List<EntityID> fatAddresses)
	{
		this.acm = acm;
		fat = new FileAllocationTable(acm);
		
		if (fatAddresses == null)
		{
			Logger.log("Creating new FAT");
			buildEntry(fat);
			flushEntry(fat);
		}
		else
		{
			Logger.log("Using existing FAT");
			fat.setAllocatedChunks(fatAddresses);
		}
	}
	
		
	
	private boolean allocateEntry(AbstractFilesystemEntry afe)
	{
		int entryByteSize = afe.getEntryByteSize();
		int chunkCount = (int) Math.ceil((double)entryByteSize/(double)acm.getMaxChunkByteSize());
		
		ArrayList<EntityID> allocatedChunks = new ArrayList<EntityID>();
		for (int i=0;i<chunkCount;i++)
		{
			EntityID eid = acm.allocateChunk();
			if (eid!=null)
			{
				allocatedChunks.add(eid);
			}
			else
			{
				Logger.log("Failed to allocate Chunk #"+i+"/"+chunkCount+" for AFE of type: "+afe.getClass().getSimpleName(),Level.SEVERE);
				return false;
			}
		}
		afe.setAllocatedChunks(allocatedChunks);
		return true;
	}
	
	public boolean buildEntry(AbstractFilesystemEntry afe)
	{
		boolean allocationSuccess = allocateEntry(afe);
		if (allocationSuccess)
		{
			if (afe.buildNew())
			{
				if (flushEntry(afe))
				{
					return true;
				}
			}
		}
		return false;
	}

	public boolean flushEntry(AbstractFilesystemEntry afe)
	{
		for (EntityID eid:afe.getContainedChunks())
		{
			if (!acm.flushPendingChanges(eid))
			{
				return false;
			}
		}
		return true;
	}

	public FileAllocationTable getFAT()
	{
		return fat;		
	}
}
