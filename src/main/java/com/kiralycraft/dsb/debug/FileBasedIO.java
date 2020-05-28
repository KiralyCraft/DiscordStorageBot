package com.kiralycraft.dsb.debug;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import com.kiralycraft.dsb.entities.EntityID;
import com.kiralycraft.dsb.exceptions.ChunkNotFoundException;
import com.kiralycraft.dsb.filesystem.FileIOInterface;

public class FileBasedIO implements FileIOInterface
{
	
	private File baseFolder;
	private int currentChunk = 0;
	public FileBasedIO()
	{
		this.baseFolder = new File("data");
		this.baseFolder.mkdir();
		
		for (File f:this.baseFolder.listFiles())
		{
			f.delete();
		}
		this.currentChunk = 0;
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
			byte[] theFileByte = new byte[(int) theFile.length()];
			fstream.read(theFileByte);
			fstream.close();
			
//			debug aici, vezi daca citeste fisieru bine, al doilea chunk pare sa fie gol complet, nici macar previous nu e pus 
			return new String(theFileByte);
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
	public EntityID createEmptyChunk(String emptyChunkData) throws IOException
	{
		EntityID theID = new EntityID(currentChunk);
		File theFile = getFile(theID);
		FileOutputStream fos = new FileOutputStream(theFile);
		fos.write(emptyChunkData.getBytes(Charset.forName("UTF-8")));
		fos.close();
		currentChunk++;
		return theID;
	}
}
