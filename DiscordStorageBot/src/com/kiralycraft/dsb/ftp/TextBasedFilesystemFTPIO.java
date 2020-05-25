package com.kiralycraft.dsb.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;

import com.guichaguri.minimalftp.api.IFileSystem;
import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.entities.EntityID;
import com.kiralycraft.dsb.filesystem.entries.DiscordFolder;
import com.kiralycraft.dsb.filesystem.entries.MetadataDiscordFile;

public class TextBasedFilesystemFTPIO implements IFileSystem<String>
{
	private String cwd;
	private final String FOLDER_INDICATOR = "/.";
	private DiscordFolder currentFolder;
	private AbstractChunkManager acm;
	
	public TextBasedFilesystemFTPIO(AbstractChunkManager acm)
	{
		cwd = getRoot();
		this.acm = acm;
		this.currentFolder = new DiscordFolder(acm,new EntityID(9));
		this.currentFolder.setFilename(getRoot());
		this.currentFolder.setLastModified(System.currentTimeMillis());
		this.currentFolder.setLength(2048);
		this.currentFolder.flush();
	}
	
	@Override
	public String getRoot()
	{
		return "Discord";
	}

	@Override
	public String getPath(String file)
	{
		
		String thePath = URI.create(getRoot()).relativize(URI.create(file)).getPath();
		if (thePath.isEmpty())
		{
			return file;
		}
		else
		{
			return thePath;
		}
	}

	@Override
	public boolean exists(String file)
	{
		System.out.println("exists "+file);
		return false;
	}

	@Override
	public boolean isDirectory(String file)
	{
		System.out.println(file);
		if (file.equals(getRoot()+FOLDER_INDICATOR))
		{
			return true;
		}
		return false;
	}

	@Override
	public int getPermissions(String file)
	{
		return 777;
	}

	@Override
	public long getSize(String file)
	{
		System.out.println("size "+file);

		return 0;
	}

	@Override
	public long getLastModified(String file)
	{
		System.out.println("lastmodified "+file);

		return 0;
	}

	@Override
	public int getHardLinks(String file)
	{
		if (isDirectory(file))
		{
			return 3;
		}
		else
		{
			return 1;
		}
	}

	@Override
	public String getName(String file)
	{
		System.out.println("name "+file);

		return null;
	}

	@Override
	public String getOwner(String file)
	{
		return "-";
	}

	@Override
	public String getGroup(String file)
	{
		return "-";
	}

	@Override
	public String getParent(String file) throws IOException
	{
		System.out.println("parent "+file);
		return null;
	}

	@Override
	public String[] listFiles(String dir) throws IOException
	{
		ArrayList<MetadataDiscordFile> mdfList = currentFolder.listFiles();
		String[] toReturn = new String[mdfList.size()];
		int i=0;
		for (MetadataDiscordFile mdf:mdfList)
		{
			System.out.println(mdf);
			toReturn[i] = mdf.getFilename();
		}
		return toReturn;
	}

	@Override
	public String findFile(String path) throws IOException
	{
		System.out.println("Find "+path);
		return null;
	}

	@Override
	public String findFile(String cwd, String path) throws IOException
	{
		if (path.isEmpty())
		{
			path = ".";
		}
		return getPath(cwd+"/"+path);
	}

	@Override
	public InputStream readFile(String file, long start) throws IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream writeFile(String file, long start) throws IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void mkdirs(String file) throws IOException
	{
//		DiscordFolder newFolder = new DiscordFolder(acm);
//		newFolder.setFilename(file);
//		currentFolder.addFile(newFolder);
		
	}

	@Override
	public void delete(String file) throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rename(String from, String to) throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void chmod(String file, int perms) throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void touch(String file, long time) throws IOException
	{
		System.out.println("touch "+file);
		
	}
}
