package com.kiralycraft.dsb.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.entities.EntityID;
import com.kiralycraft.dsb.filesystem.entries.DiscordFolder;
import com.kiralycraft.dsb.filesystem.entries.MetadataDiscordFile;

public class TextBasedFilesystem
{
	private DiscordFolder baseFolder;
	private MetadataDiscordFile currentFile;
	private String currentPath;
	private AbstractChunkManager acm;
	
	public TextBasedFilesystem(AbstractChunkManager acm,EntityID baseFolderID)
	{
		if (baseFolderID != null)
		{
			this.baseFolder = new DiscordFolder(acm, baseFolderID);
			System.out.println(this.baseFolder.isFolder());
		}
		else
		{
			this.baseFolder = new DiscordFolder(acm, "/");
		}
		this.currentPath = "/";
		this.currentFile = baseFolder;
		this.acm = acm;
	}
	
	/**
	 * Returns the current path of the file browser in the form of a string.
	 * @return
	 */
	public String getCurrentPath()
	{
		return currentPath;
	}
	
	/**
	 * Returns true if the current file is a folder.
	 * @return
	 */
	public boolean isFolder()
	{
		return currentFile.isFolder();
	}
	
	/**
	 * Adds a file from the local file system to the TFS.
	 * Returns true if something went wrong, or the current file is a folder.
	 * @see TextBasedFilesystem#addFileStream(InputStream, String, long, long)
	 * @param theFile
	 */
	public boolean addFilesystemFile(File theFile)
	{
		try
		{
			FileInputStream fis = new FileInputStream(theFile);
			boolean addResult = addFileStream(fis,theFile.getName(),theFile.length());
			fis.close();
			return addResult;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Adds a file from an {@link InputStream} to the TFS.
	 * Returns false if the current file is not a folder, or something went wrong.
	 * 
	 * @param is
	 * @param name
	 * @param length
	 * @param modificationDate
	 */
	public boolean addFileStream(InputStream is,String name,long length)
	{
		if (currentFile.isFolder())
		{
			MetadataDiscordFile mdf = new MetadataDiscordFile(acm, name, false, length);
//			System.out.println(mdf.getFilename());
			try
			{
				int len;
				byte[] buffer = new byte[4096];
				while((len = is.read(buffer))>0)
				{
					mdf.write(buffer,0,len);
				}
				mdf.flush();
				DiscordFolder.fromMDF(acm, currentFile).addFile(mdf);
				return true;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Adds the given {@link MetadataDiscordFile} to the current folder.
	 * Returns false if the current file is not a folder.
	 * 
	 * @param is
	 * @param name
	 * @param length
	 * @param modificationDate
	 */
	public boolean addFileRaw(MetadataDiscordFile mdf)
	{
		if (currentFile.isFolder())
		{
			DiscordFolder.fromMDF(acm, currentFile).addFile(mdf);
			return true;
		}
		return false;
	}
	
	/**
	 * Lists all files in the current folder.
	 * If the current file is not a folder, it returns null.
	 * @return
	 */
	public ArrayList<MetadataDiscordFile> listFiles()
	{
		if (currentFile.isFolder())
		{
			return DiscordFolder.fromMDF(acm,currentFile).listFiles();
		}
		return null;
	}
	
	/**
	 * Creates a directory in the current path.
	 * If the current file is not a folder, it returns false.
	 * @param name
	 * @return
	 */
	public boolean mkdir(String name)
	{
		if (currentFile.isFolder())
		{
			DiscordFolder newFolder = new DiscordFolder(acm, name);
			DiscordFolder.fromMDF(acm,currentFile).addFile(newFolder);
			return true;
		}
		return false;
	}
	
	/**
	 * Changes the current path to the specified folder.
	 * If the destination is not a folder, it returns false and changes the directory back to the initial one.
	 * This method ONLY supports absolute paths!
	 * @param path
	 * @return
	 */
	public boolean chdir(String path)
	{
		if (path.equals("/"))
		{
			currentFile = baseFolder;
			currentPath = "/";
			return true;
		}
		else
		{
			StringBuilder chdirPath = new StringBuilder();
			chdirPath.append("/");
			
			MetadataDiscordFile currentSearchFolder = baseFolder;
			
			MetadataDiscordFile currentFileBeforeCHDIR = currentFile;
			String[] splitPath = path.split("/");
			boolean moved = false;
			for (int i=0;i<splitPath.length;i++)
			{
				String nextTarget = splitPath[i];
				if (!nextTarget.isEmpty())
				{
					moved = false;
					for (MetadataDiscordFile mdf:DiscordFolder.fromMDF(acm,currentSearchFolder).listFiles())
					{
						if (mdf.getFilename().equals(nextTarget) && mdf.isFolder())
						{
							chdirPath.append(nextTarget);
							if (i<splitPath.length-1)
							{
								chdirPath.append("/");
							}
							
							moved=true;
							currentSearchFolder = mdf;
						}
					}
					
					if (!moved)
					{
						currentSearchFolder = DiscordFolder.fromMDF(acm,currentFileBeforeCHDIR);
						return false;
					}
				}
			}
			
			if (moved) //If we moved on the last iteration, and did not stop previously
			{
				currentPath = chdirPath.toString();
				currentFile = currentSearchFolder;
			}
			return true;
		}
	}
	
	/**
	 * Returns true if the file denoted by the supplied filename exists.
	 * @param filename
	 * @return
	 */
	public boolean exists(String filename)
	{
		for (MetadataDiscordFile mdf:DiscordFolder.fromMDF(acm,currentFile).listFiles())
		{
			if (mdf.getFilename().equals(filename))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns this file. If it doesn't exist, returns null.
	 * @param name
	 * @return
	 */
	public MetadataDiscordFile getFile(String name)
	{
		for (MetadataDiscordFile mdf:DiscordFolder.fromMDF(acm,currentFile).listFiles())
		{
			System.out.println(this.getClass()+" "+mdf);
			if (mdf.getFilename().equals(name))
			{
				return mdf;
			}
		}
		return null;
	}
}
