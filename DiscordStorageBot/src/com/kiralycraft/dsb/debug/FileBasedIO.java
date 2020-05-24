package com.kiralycraft.dsb.debug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.SecureRandom;

import com.kiralycraft.dsb.entities.EntityID;
import com.kiralycraft.dsb.exceptions.ChunkNotFoundException;
import com.kiralycraft.dsb.filesystem.FileIOInterface;

public class FileBasedIO implements FileIOInterface
{
	private File baseFolder;
	private int currentChunk = 0;
	public FileBasedIO(int i)
	{
		this.currentChunk = i;
		this.baseFolder = new File("data");
		this.baseFolder.mkdir();
	}
	@Override
	public int getChunkSize() 
	{
		return 2000;
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

	@Override
	public boolean checkChunkExists(EntityID eid) throws IOException 
	{
		try
		{
			getRawChunkData(eid);
			return true;
		}
		catch(ChunkNotFoundException e)
		{
			return false;
		}
		catch(IOException ie)
		{
			throw ie;
		}
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
