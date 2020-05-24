package com.kiralycraft.dsb.transfer;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SupplyStreamV2 extends InputStream
{
	private class Chunk
	{
		private byte[] chunkData;
		private boolean lastChunk;
		public Chunk(byte[] chunkData, boolean lastChunk)
		{
			this.chunkData = chunkData;
			this.lastChunk = lastChunk;
		}
	}
	private BlockingQueue<Chunk> queue;
	private Chunk currentChunk = null;
	private int posInCurrentChunk = 0;
	private boolean streamDead = false;
	public SupplyStreamV2()
	{
		queue = new LinkedBlockingQueue<Chunk>(2); //Max two chunks in memory
	}
	@Override
	public int read() throws IOException
	{
		try
		{
			if (!streamDead) //If a previous read returned a number of bytes, and the EOF is reached, this method WILL be called again
			{
				if (currentChunk == null)
				{
					currentChunk = queue.take();
					/*
					 * Last chunk has no byte data
					 */
					if (currentChunk.lastChunk) 
					{
						streamDead = true;
						return -1;
					}
					else
					{
						posInCurrentChunk = 0;
					}
				}
				int toReturn = currentChunk.chunkData[posInCurrentChunk];
				if (toReturn < 0)
				{
					toReturn+=256;
				}
				posInCurrentChunk++;
				
				if (posInCurrentChunk >= currentChunk.chunkData.length)
				{
					currentChunk = null;
				}
				return toReturn;
			}
			else
			{
				return -1;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			IOException toThrow = new IOException(e.getMessage());
			toThrow.setStackTrace(e.getStackTrace());
			throw toThrow;
		}
	}
	@Override
	public void close() throws IOException
	{
		try
		{
			queue.put(new Chunk(null,true));
		} 
		catch (InterruptedException e)
		{
			throw new IOException();
		}
	}
	public void addChunk(byte[] data,int len) throws InterruptedException
	{
		if (data.length != 0)
		{
			Chunk c = new Chunk(data,false);
			queue.put(c);
		}
	}
	@Override
	public long skip(long n) throws IOException
	{
		if (n<0)
		{
			return 0;
		}
		else
		{
			for (long l=0;l<n;l++)
			{
				int readByte = read();
				if (readByte == -1)
				{
					return l;
				}
			}
			return n;
		}
	}
	@Override
	public int available() throws IOException
	{
		if (!streamDead)
		{
			if (currentChunk!=null)
			{
				return currentChunk.chunkData.length - posInCurrentChunk;
			}
		}
		return 0;
	}
	@Override
	public synchronized void reset() throws IOException
	{
		throw new RuntimeException("Reset NOT supported by SupplyStreamV2!");
	}
	public void clearQueue()
	{
		queue.clear();
	}
}
