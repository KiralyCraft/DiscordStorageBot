package com.kiralycraft.dsb.filesystem.entries;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.entities.Chunk;
import com.kiralycraft.dsb.entities.EntityID;

public class DiscordFile extends RandomAccessFile
{
	private int posInCurrentChunk;
	private long passedChunks = 0;
	
	private boolean currentChunkTainted;

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
		this.baseChunk = acm.allocateChunk();
		this.currentChunk = acm.getChunk(baseChunk.getNext());
	}

	private int getMaxChunkBytesExcludingMeta()
	{
		return acm.getMaxChunkByteSize() - Chunk.getDataOffset();
	}
	@Override
	public int read()
	{
		boolean isEOF = currentChunk.getNext() == null && posInCurrentChunk == acm.getMaxChunkByteSize();
		if (!isEOF) // If a previous read returned a number of bytes, and the EOF is reached, this
					// method WILL be called again
		{
			int toReturn = currentChunk.getChunkData()[posInCurrentChunk];
			if (toReturn < 0)
			{
				toReturn += 256;
			}
			posInCurrentChunk++;

			if (posInCurrentChunk >= getMaxChunkBytesExcludingMeta())
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
		this.currentChunkTainted = true;
		this.currentChunk.getChunkData()[posInCurrentChunk] = (byte) b;
		posInCurrentChunk++;
		if (posInCurrentChunk >= acm.getMaxChunkByteSize())
		{
			flush();
			EntityID nextChunk = currentChunk.getNext();

			if (nextChunk == null)
			{
				Chunk newChunk = acm.allocateChunk();
				currentChunk.setNext(newChunk.getID());
				currentChunk = newChunk;
			} else
			{
				currentChunk = acm.getChunk(nextChunk);
			}
			passedChunks++;
			posInCurrentChunk = Chunk.getDataOffset();
		}
	}

	@Override
	public long getFilePointer() throws IOException
	{
		return passedChunks * getMaxChunkBytesExcludingMeta() + posInCurrentChunk;
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
				currentChunk = acm.getChunk(currentChunk.getNext());
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
		return baseChunk.getLong(Chunk.getDataOffset());
	}

	@Override
	public void setLength(long newLength) throws IOException
	{
		baseChunk.setLong(Chunk.getDataOffset(), newLength);
		acm.flushChunk(currentChunk);
	}

	public boolean flush()
	{
		if (currentChunkTainted)
		{
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
