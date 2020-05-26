package com.kiralycraft.dsb.filesystem.entries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.entities.EntityID;
import com.kiralycraft.dsb.log.Logger;

public class DiscordFolder extends MetadataDiscordFile
{
	public DiscordFolder(AbstractChunkManager acm,String filename)
	{
		super(acm, filename, true, 6000);
	}
	
	public DiscordFolder(AbstractChunkManager acm, EntityID entityID)
	{
		super(acm,entityID);
	}

	public void addFile(MetadataDiscordFile discordFile)
	{
		try
		{
			seek(0);
			long fileCount = readLong();
			seek(fileCount*(8*3)+8);			
			EntityID baseID = discordFile.getBaseChunk().getID();
			writeLong(baseID.getBaseID());
			writeLong(baseID.getSectionID());
			writeLong(baseID.getEntityID());
			
			seek(0);
			writeLong(fileCount+1);
			flush();
		} 
		catch (IOException e)
		{
			Logger.log(e,Level.SEVERE);
		} 
	}
	
	public ArrayList<MetadataDiscordFile> listFiles()
	{
		try
		{
			ArrayList<MetadataDiscordFile> fileList = new ArrayList<MetadataDiscordFile>();
			seek(0);
			long fileCount = readLong();
			for (int i=0;i<fileCount;i++)
			{
				seek(i*(8*3)+8);
				long baseID = readLong();
				long sectionID = readLong();
				long entityID = readLong();
				MetadataDiscordFile mdf = new MetadataDiscordFile(getACM(), new EntityID(baseID,sectionID,entityID)); //Load the file 
				fileList.add(mdf);
			}
			return fileList;
		} 
		catch (IOException e)
		{
			Logger.log(e,Level.SEVERE);
			return null;
		} 
	}

	/**
	 * Converts a MDF to a DiscordFolder. This does, sadly, re-read the file.
	 * @param acm
	 * @param mdf
	 * @return
	 */
	public static DiscordFolder fromMDF(AbstractChunkManager acm,MetadataDiscordFile mdf)
	{
		return new DiscordFolder(acm,mdf.getBaseChunk().getID());
	}
}
