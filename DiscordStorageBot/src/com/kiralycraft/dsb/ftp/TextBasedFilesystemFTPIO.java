package com.kiralycraft.dsb.ftp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.guichaguri.minimalftp.api.IFileSystem;

public class TextBasedFilesystemFTPIO implements IFileSystem<String>
{
	
	@Override
	public String getRoot()
	{
		return ".";
	}

	@Override
	public String getPath(String file)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(String file)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDirectory(String file)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getPermissions(String file)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getSize(String file)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLastModified(String file)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHardLinks(String file)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName(String file)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOwner(String file)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getGroup(String file)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParent(String file) throws IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] listFiles(String dir) throws IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findFile(String path) throws IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findFile(String cwd, String path) throws IOException
	{
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}
	
}
