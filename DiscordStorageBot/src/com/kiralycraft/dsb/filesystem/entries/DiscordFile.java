package com.kiralycraft.dsb.filesystem.entries;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.entities.Chunk;
import com.kiralycraft.dsb.entities.EntityID;
import com.kiralycraft.dsb.log.Logger;

public class DiscordFile extends RandomAccessFile
{
	private int posInCurrentChunk;
	private long passedChunks = 0;
	
	private boolean currentChunkTainted;
	private long length;
	
	private Chunk currentChunk;
	private Chunk baseChunk;
	private AbstractChunkManager acm;

	public DiscordFile(AbstractChunkManager acm) throws FileNotFoundException
	{
		super("./f.txt", "rw");
		this.acm = acm;
		this.posInCurrentChunk = Chunk.getDataOffset();
		this.baseChunk = acm.allocateChunk();
		this.currentChunk = acm.allocateChunk();
		this.baseChunk.setNext(this.currentChunk.getID());	
		acm.flushChunk(baseChunk);
	}

	public DiscordFile(AbstractChunkManager acm, EntityID baseID) throws FileNotFoundException
	{
		super("./f.txt", "rw");
		this.acm = acm;
		this.posInCurrentChunk = Chunk.getDataOffset();
		this.baseChunk = acm.getChunk(baseID);
		this.currentChunk = acm.getChunk(baseChunk.getNext());
		this.length = baseChunk.getLong(Chunk.getDataOffset());
	}

	private int getMaxChunkBytesExcludingMeta()
	{
		return acm.getMaxChunkByteSize() - Chunk.getDataOffset();
	}
	@Override
	public int read()
	{
		boolean isEOF = (currentChunk.getNext() == null && posInCurrentChunk == acm.getMaxChunkByteSize()) || getErrorlessFilePointer()==length;
		if (!isEOF) // If a previous read returned a number of bytes, and the EOF is reached, this
					// method WILL be called again
		{
			int toReturn = currentChunk.getChunkData()[posInCurrentChunk];
			if (toReturn < 0)
			{
				toReturn += 256;
			}
			posInCurrentChunk++;

			if (posInCurrentChunk >= acm.getMaxChunkByteSize())
			{
				currentChunk = acm.getChunk(currentChunk.getNext());
				passedChunks++;
				posInCurrentChunk = Chunk.getDataOffset();
				currentChunkTainted = false;
			}
			return toReturn;
		} else
		{
			return -1;
		}
	}

	@Override
	public void write(int b) throws IOException
	{
		this.length = Math.max(length, passedChunks * getMaxChunkBytesExcludingMeta() + posInCurrentChunk);
		this.currentChunkTainted = true;
		this.currentChunk.getChunkData()[posInCurrentChunk] = (byte) b;
		posInCurrentChunk++;
		if (posInCurrentChunk >= acm.getMaxChunkByteSize())
		{
			flush();
			moveToNextChunk();
			passedChunks++;
			posInCurrentChunk = Chunk.getDataOffset();
		}
	}

	/**
	 * This helper method moves to the next {@link Chunk}, and if it does not exist, it creates it.
	 */
	private void moveToNextChunk()
	{
		EntityID nextChunk = currentChunk.getNext();

		if (nextChunk == null)
		{
			Chunk newChunk = acm.allocateChunk();
			currentChunk.setNext(newChunk.getID());
			newChunk.setPrevious(currentChunk.getID());
			acm.flushChunk(currentChunk);
			acm.flushChunk(newChunk);
			
			currentChunk = newChunk;
		} 
		else
		{
			currentChunk = acm.getChunk(nextChunk);
		}
	}
	/**
	 * Helper method for fetching the current position.
	 * @return
	 */
	
	private long getErrorlessFilePointer()
	{
		return passedChunks * getMaxChunkBytesExcludingMeta() + (posInCurrentChunk-Chunk.getDataOffset());
	}
	@Override
	public long getFilePointer() throws IOException
	{
		return getErrorlessFilePointer();
	}

	@Override
	public void seek(long pos) throws IOException
	{
		long oldPassedChunks = passedChunks;
		passedChunks = pos / getMaxChunkBytesExcludingMeta();
		posInCurrentChunk = (int) pos % getMaxChunkBytesExcludingMeta()+Chunk.getDataOffset();
		
		flush();	
		
		if (passedChunks > oldPassedChunks)
		{
			for (int i = 0; i < (passedChunks - oldPassedChunks); i++)
			{
				moveToNextChunk();
			}
		} else
		{
			for (int i = 0; i < (oldPassedChunks - passedChunks); i++)
			{
				currentChunk = acm.getChunk(currentChunk.getPrevious());
			}
		}
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		if (b == null)
		{
			throw new NullPointerException();
		} else if (off < 0 || len < 0 || len > b.length - off)
		{
			throw new IndexOutOfBoundsException();
		} else if (len == 0)
		{
			return 0;
		}

		int c = read();
		if (c == -1)
		{
			return -1;
		}
		b[off] = (byte) c;

		int i = 1;
		for (; i < len; i++)
		{
			c = read();
			if (c == -1)
			{
				break;
			}
			b[off + i] = (byte) c;
		}
		return i;
	}

	@Override
	public int read(byte[] b) throws IOException
	{
		return read(b, 0, b.length);
	}

	@Override
	public void write(byte[] b) throws IOException
	{
		write(b, 0, b.length);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		
		if (off < 0 || len < 0 || off + len > b.length)
			throw new ArrayIndexOutOfBoundsException();
		for (int i = 0; i < len; ++i)
			write (b[off + i]);
		
	}

	@Override
	public long length() throws IOException
	{
		return length;
	}

	@Override
	public void setLength(long newLength) throws IOException
	{
		baseChunk.setLong(Chunk.getDataOffset(), newLength);
		acm.flushChunk(baseChunk);
	}

	public boolean flush()
	{
		if (currentChunkTainted)
		{
			acm.flushChunk(baseChunk);
			
			boolean flushResult = acm.flushChunk(currentChunk);
			if (flushResult)
			{
				currentChunkTainted = false;
			}
			return flushResult;
		}
		return true;
	}
}
