package com.kiralycraft.dsb.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import com.guichaguri.minimalftp.api.IFileSystem;
import com.kiralycraft.dsb.chunks.AbstractChunkManager;

public class TextBasedFilesystemFTPIO implements IFileSystem<String>
{
	private String cwd;
	private final String FOLDER_INDICATOR = "/.";
	
	
	public TextBasedFilesystemFTPIO(AbstractChunkManager acm)
	{
		cwd = getRoot();
	}
	
	@Override
	public String getRoot()
	{
		return "Discord";
	}

	@Override
	public String getPath(String file)
	{
		
		String thePath = URI.create(file).relativize(URI.create(getRoot())).getPath();
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
		if (file.equals(getRoot()))
		{
			return true;
		}
		return false;
	}

	@Override
	public int getPermissions(String file)
	{
		System.out.println("perms "+file);

		return 0;
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
		// TODO Auto-generated method stub
		return 0;
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
		System.out.println("parent "+file);
		return null;
	}

	@Override
	public String[] listFiles(String dir) throws IOException
	{
		System.out.println("list "+dir);
		return new String[] {"/pisatf"};
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
