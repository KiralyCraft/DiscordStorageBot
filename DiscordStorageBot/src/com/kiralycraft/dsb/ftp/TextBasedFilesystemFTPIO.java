package com.kiralycraft.dsb.ftp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.guichaguri.minimalftp.api.IFileSystem;
import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.filesystem.TextBasedFilesystem;
import com.kiralycraft.dsb.filesystem.entries.MetadataDiscordFile;

public class TextBasedFilesystemFTPIO implements IFileSystem<String>
{
	private final String FOLDER_INDICATOR = "/.";
	private AbstractChunkManager acm;
	private TextBasedFilesystem tbf;
	
	public TextBasedFilesystemFTPIO(AbstractChunkManager acm,TextBasedFilesystem tbf)
	{
		this.acm = acm;
		System.out.println("INIT");
		this.tbf = tbf;
	}
	
	@Override
	public String getRoot()
	{
		System.out.println("Get root");
		return "";
	}

	@Override
	public String getPath(String file)
	{
		System.out.println("Get path \""+file+"\"");
		
		if (file.equals(getRoot()))
		{
			return "/";
		}
		else
		{
			return file;
		}
	}

	@Override
	public boolean exists(String file)
	{
		return true;
	}

	@Override
	public boolean isDirectory(String file)
	{
		System.out.println("Checking if directory: \""+file+"\"");
		boolean toReturn = false;
		if (file != null)
		{
			if (file.equals(tbf.getCurrentPath()))
			{
				toReturn = tbf.isFolder();
			}
			else
			{
				if (file.equals(getRoot()+FOLDER_INDICATOR) || file.equals(getRoot()))
				{
					toReturn = true;
				}
				else
				{
					MetadataDiscordFile mdf;
					if (!file.contains("/"))
					{
						mdf = tbf.getFile(file);
						if (mdf!=null)
						{
							toReturn = mdf.isFolder();
						}
					}
					else
					{
						String parent = file.substring(0,file.lastIndexOf("/"));
						String fileName = file.substring(file.lastIndexOf("/")+1);
						
						if (tbf.chdir(parent))
						{
							mdf = tbf.getFile(fileName);
							if (mdf!=null)
							{
								toReturn = mdf.isFolder();
							}
							else
							{
								System.out.println("Could nto find file \""+fileName+"\" in current dir: \""+tbf.getCurrentPath()+"\". Exists: "+tbf.exists(fileName));
							}
						}
						else
						{
							System.out.println("Could not chdir to "+parent);
							toReturn = false;
						}
					}
				}
			}
		}

		System.out.println("Is directory: \""+file+"\": "+toReturn);
		return toReturn;
	}

	@Override
	public int getPermissions(String file)
	{
		return 777;
	}

	@Override
	public long getSize(String file)
	{
		long toReturn = 0;
		if (!file.equals(getRoot()+FOLDER_INDICATOR) && !file.equals(getRoot()))
		{
			MetadataDiscordFile mdf;
			if (!file.contains("/"))
			{
				mdf = tbf.getFile(file);
				if (mdf!=null)
				{
					toReturn = mdf.length();
				}
			}
			else
			{
				String parent = file.substring(0,file.lastIndexOf("/"));
				String fileName = file.substring(file.lastIndexOf("/")+1);
				
				if (tbf.chdir(parent))
				{
					mdf = tbf.getFile(fileName);
					if (mdf!=null)
					{
						toReturn = mdf.length();
					}
					else
					{
						System.out.println("Could not find file size of \""+fileName+"\" in current dir: \""+tbf.getCurrentPath()+"\". Exists: "+tbf.exists(fileName));
					}
				}
			}
		}
		System.out.println("Get size of \""+file+"\" returned "+toReturn);
		return toReturn;
	}

	@Override
	public long getLastModified(String file)
	{
		System.out.println("lastmodified \""+file+"\"");

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
		if (!file.contains("/"))
		{
			return file;
		}
		else
		{
			return file.substring(file.lastIndexOf("/")+1);
		}
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
		System.out.println("Get parent of: \""+file+"\"");
		if (file.lastIndexOf("/") == file.indexOf("/"))
		{
			return getRoot();
		}
		else
		{
			return file.substring(0,file.lastIndexOf("/"));
		}
	}

	@Override
	public String[] listFiles(String dir) throws IOException
	{
		System.out.println("Listing files inside: \""+dir+"\"");
		if (tbf.chdir(dir))
		{
			ArrayList<MetadataDiscordFile> mdfList = tbf.listFiles();
			String[] toReturn = new String[mdfList.size()];
			int i=0;
			for (MetadataDiscordFile mdf:mdfList)
			{
				toReturn[i++] = mdf.getFilename();
				
			}
			System.out.println("List done");
			return toReturn;
		}
		else
		{
			throw new IOException("Not a directory");
		}
	}

	@Override
	public String findFile(String path) throws IOException
	{
		System.out.println("Find file path: \""+path+"\"");
		return path;
	}

	@Override
	public String findFile(String cwd, String path) throws IOException
	{
		System.out.println("Find file: \""+cwd+"\", \""+path+"\"");
		if (path.isEmpty())
		{
			return cwd;
		}
		if (cwd.isEmpty())
		{
			return path;
		}
		
		String strippedCWD = cwd.replace(getRoot(), "/");
		if (tbf.chdir(strippedCWD))
		{
			return cwd+"/"+path;
		}
		throw new IOException("CWD does not exist");
	}

	@Override
	public InputStream readFile(String file, long start) throws IOException
	{
		String actualFilename;
		
		if (!file.contains("/"))
		{
			actualFilename = file;
		}
		else
		{
			String parent = file.substring(0,file.lastIndexOf("/"));
			String fileName = file.substring(file.lastIndexOf("/")+1);
			
			if (tbf.chdir(parent))
			{
				actualFilename = fileName;
			}
			else
			{
				throw new IOException("Could not chdir to the source folder");				
			}
		}
		
		return new ReadFileStream(actualFilename,tbf);
	}

	@Override
	public OutputStream writeFile(String file, long start) throws IOException
	{
		String actualFilename;
		
		if (!file.contains("/"))
		{
			actualFilename = file;
		}
		else
		{
			String parent = file.substring(0,file.lastIndexOf("/"));
			String fileName = file.substring(file.lastIndexOf("/")+1);
			
			if (tbf.chdir(parent))
			{
				actualFilename = fileName;
			}
			else
			{
				throw new IOException("Could not chdir to the destination folder");				
			}
		}
		
		System.out.println("Writing \""+actualFilename+"\" to folder "+tbf.getCurrentPath());
		return new NewFileStream(acm, actualFilename, tbf);
	}

	@Override
	public void mkdirs(String file) throws IOException
	{
		System.out.println("MKDIR: \""+file+"\"");
		if (file.contains("/"))
		{
			String currentPath = tbf.getCurrentPath();
			
			String[] pathEntires = file.split("/");
			
			StringBuilder currentMKDIRPath = new StringBuilder("/");
			for (int i=0;i<pathEntires.length;i++)
			{
				String currentFolder = pathEntires[i];
				if (!currentFolder.isEmpty())
				{
					if (!tbf.exists(currentFolder))
					{
						tbf.mkdir(currentFolder);
					}

					System.out.println("Creating dir \""+currentFolder+"\" inside \""+currentMKDIRPath.toString()+"\"");
					currentMKDIRPath.append(currentFolder);
					tbf.chdir(currentMKDIRPath.toString());
					
					currentMKDIRPath.append("/");
				}
			}
			
			tbf.chdir(currentPath);
		}
		else
		{
			tbf.mkdir(file);
		}
		
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
