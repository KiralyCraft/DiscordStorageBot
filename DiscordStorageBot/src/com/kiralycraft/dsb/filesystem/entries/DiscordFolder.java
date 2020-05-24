package com.kiralycraft.dsb.filesystem.entries;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.logging.Level;

import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.entities.Chunk;
import com.kiralycraft.dsb.entities.EntityID;
import com.kiralycraft.dsb.log.Logger;

public class DiscordFolder extends MetadataDiscordFile
{
	
	private AbstractChunkManager acm;

	public DiscordFolder(AbstractChunkManager acm)
	{
		super(acm);
		setLength(512);
		this.acm = acm;
	}

	public DiscordFolder(AbstractChunkManager acm, EntityID baseID)
	{
		super(acm, baseID);
		this.acm = acm;
	}
	
	public void addFile(MetadataDiscordFile discordFile)
	{
		try
		{
			seek(0);
			long fileCount = readLong();
			seek(fileCount*(8*3)+8);
//			byte[] fileName = new byte[MAX_STRING_LENGTH];
//			byte[] actualFilename = discordFile.getFilename().getBytes(Charset.forName("UTF-8"));
//			
//			int byteCount = Math.min(actualFilename.length,MAX_STRING_LENGTH-1);
//			System.arraycopy(actualFilename, 0, fileName, 0, byteCount);
//			fileName[byteCount]=0;
//			write(fileName);
//			
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
//				byte[] fileName = new byte[MAX_STRING_LENGTH];
//				read(fileName,0,MAX_STRING_LENGTH);
//				
//				int targetSize = 0;
//				for (int j=0;j<MAX_STRING_LENGTH-1;j++)
//				{
//					if (fileName[j]!=0)
//					{
//						targetSize = i;
//					}
//					else
//					{
//						break;
//					}
//				}
				long baseID = readLong();
				long sectionID = readLong();
				long entityID = readLong();
				MetadataDiscordFile mdf = new MetadataDiscordFile(acm, new EntityID(baseID,sectionID,entityID));
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
}
