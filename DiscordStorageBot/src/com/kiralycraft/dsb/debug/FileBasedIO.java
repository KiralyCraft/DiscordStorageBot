package com.kiralycraft.dsb.debug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import com.kiralycraft.dsb.entities.EntityID;
import com.kiralycraft.dsb.exceptions.ChunkNotFoundException;
import com.kiralycraft.dsb.filesystem.FileIOInterface;
import com.kiralycraft.dsb.filesystem.entries.DiscordFolder;

public class FileBasedIO implements FileIOInterface
{
	private File baseFolder;
	private int currentChunk = 0;
	public FileBasedIO()
	{
		this.baseFolder = new File("data");
		this.baseFolder.mkdir();
		this.currentChunk = baseFolder.list().length;
	}
	@Override
	public int getChunkSize() 
	{
		return 6000;
	}

	@Override
	public String getRawChunkData(EntityID eid) throws IOException 
	{
		File theFile = getFile(eid);
		if (theFile.exists())
		{
			// Open the file
			FileInputStream fstream = new FileInputStream(theFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	
			String strLine;
			String allRead="";
			
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   
			{
				allRead+=strLine+"\n";
			}
			//Close the input stream
			fstream.close();
			
			return allRead;
		}
		else
		{
			throw new ChunkNotFoundException(eid, "Chunk not found!");
		}
	}
	@Override
	public boolean updateRawChunkData(EntityID eid, String newData) throws IOException 
	{
		File theFile = getFile(eid);
		FileOutputStream fos = new FileOutputStream(theFile);
		fos.write(newData.getBytes(Charset.forName("UTF-8")));
		fos.close();
		return true;
	}
	
	private File getFile(EntityID eid)
	{
		return new File(baseFolder,eid.getBaseID()+"-"+eid.getSectionID()+"-"+eid.getEntityID()+".txt");
	}
	@Override
	public EntityID createEmptyChunk() throws IOException
	{
		EntityID eid = new EntityID(currentChunk++);
		updateRawChunkData(eid,"");
		return eid;
	}
}
