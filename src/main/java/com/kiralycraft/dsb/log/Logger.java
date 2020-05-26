package com.kiralycraft.dsb.log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Logger
{
	private static FileOutputStream loggingFileOS;
	private static File loggingFile;
	private static boolean verbooseDebugging=false;
	private static final int ZIP_COUNT_TRESH = 50;
	static
	{
		if (System.getProperty("rpclogger.file", "true").equalsIgnoreCase("true"))
		{
			File loggingFolder = new File("rpclogs");
			loggingFolder.mkdirs();
			try
			{
				loggingFile = new File(loggingFolder,System.currentTimeMillis()+".log");
				loggingFileOS = new FileOutputStream(loggingFile);
				
				packOtherLogs(loggingFolder,loggingFile);
				
				log("Logging to file is active. Set the system property \"rpclogger.file = false\" to disable it.");
				log("All lines prepended with an asterisk (*) have been saved to file.");
			}
			catch (FileNotFoundException e)
			{
				log("Could not instantiate file logging: "+loggingFile.getAbsolutePath());
			}
		}
		
		
		if (System.getProperty("rpclogger.verbose","false").equals("true"))
		{
			verbooseDebugging = true;
			log("Verbose logging is enabled. Will print the calling class and method for each log entry.");
		}
	}
	public static synchronized void log(String s, Level logLevel)
	{
		
		String message = "["+System.currentTimeMillis()/1000+"] ["+logLevel+"] "+s;
		
		if (verbooseDebugging)
		{
			String callerID;
			StackTraceElement[] ste = new Exception().getStackTrace();
			if (ste.length>=3)
			{
				callerID = "["+ste[2].getClassName()+":"+ste[2].getMethodName()+"]";
			}
			else
			{
				callerID = "[?:?]";
			}
			message=callerID+message;
		}
		
		if (loggingFileOS != null)
		{
			try
			{
				loggingFileOS.write((message+"\n").getBytes(Charset.forName("UTF-8")));
				message="*"+message;
			} 
			catch (IOException e)
			{
				;
			}
		}
		
		if (logLevel.intValue() > Level.INFO.intValue())
		{
			System.err.println(message);
		}
		else
		{
			System.out.println(message);
		}
	}
	
	private static void packOtherLogs(File loggingFolder, File currentLog)
	{
		try 
		{
			log("Attempting to zip old logs to memory.");
			FilenameFilter logFilter = new FilenameFilter() 
			{
			    @Override
			    public boolean accept(File dir, String name) 
			    {
			    	if (!name.equals(currentLog.getName()))
			    	{
				        if (name.endsWith("log"))
				        {
				        	return true;
				        }
			    	}
			    	return false;
			    }
			};
			
			byte[] readBuffer = new byte[1000 * 1000];
			int readLen;
			
			NumberFormat memUsageFormat = new DecimalFormat("##.##");
			
			File[] toCompress = loggingFolder.listFiles(logFilter);
			
            Arrays.sort(toCompress, new Comparator<File>()
            {
				@Override
				public int compare(File o1, File o2)
				{
					return (int) Math.signum(getSafeDoubleName(o1) - getSafeDoubleName(o2));
				}
            });
			
			if (toCompress.length >= ZIP_COUNT_TRESH)
			{
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ZipOutputStream zos = new ZipOutputStream(baos);
				zos.setLevel(9);
				
	            for (File toZip : toCompress) 
				{
	            	String toZipName = toZip.getName();
	     			zos.putNextEntry(new ZipEntry(toZipName));
	     			
	     			FileInputStream fis = new FileInputStream(toZip);
	     			while ((readLen = fis.read(readBuffer)) > 0)
	     			{
	     				zos.write(readBuffer, 0, readLen);
	     			}
	     			fis.close();
	                zos.closeEntry();
	                log("Zipped \""+toZipName+"\". Memory usage: "+memUsageFormat.format(baos.size()/1000d)+" KB");
	            }
	            zos.close();
	            
	            String outputFilename = getSafeDoubleName(toCompress[0])+"-"+getSafeDoubleName(toCompress[toCompress.length-1])+".zip";
	            log("Dumping memory to: \""+outputFilename+"\"");
	            File outputFile = new File(loggingFolder,outputFilename);
	            FileOutputStream output = new FileOutputStream(outputFile);
	            byte[] baosArray = baos.toByteArray();
	            baos.close();
	            output.write(baosArray,0,baosArray.length);
	            output.close();
	            log("Zipping ended. Removing old files.");
	            
	            for (File toZip : toCompress) 
				{
	            	toZip.delete();
				}
			}
			else
			{
				log("Not enough logs to zip. There are currently "+toCompress.length+", but will compress when there are "+ZIP_COUNT_TRESH+".");
			}
        } 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public static void log(Exception e, Level logLevel)
	{
		log(e.getMessage(),logLevel);
		for (StackTraceElement ste:e.getStackTrace())
		{
			log(ste.toString(),logLevel);
		}
	}
	public static void log(String string)
	{
		log(string,Level.INFO);
	}
	/**
	 * Attempts to the number from a file with the name "System.currenttimemillis().extension"
	 * Returns 0 if something went wrong.
	 * @param fil
	 * @return
	 */
	private static long getSafeDoubleName(File fil)
	{
		String filenameNoExtension = fil.getName();
		if (filenameNoExtension.contains("."))
		{
			filenameNoExtension = filenameNoExtension.substring(0,filenameNoExtension.lastIndexOf("."));
		}
		try
		{
			return Long.parseLong(filenameNoExtension);	
		}
		catch(Exception e)
		{
			return 0;
		}
	}
}

