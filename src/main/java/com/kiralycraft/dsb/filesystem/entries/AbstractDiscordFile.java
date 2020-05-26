package com.kiralycraft.dsb.filesystem.entries;

import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.entities.Chunk;
import com.kiralycraft.dsb.entities.EntityID;

public abstract class AbstractDiscordFile
{
	private int posInCurrentChunk;
	private long passedChunks = 0;

	private boolean currentChunkTainted;
	private long length;

	private Chunk currentChunk;
	private Chunk baseChunk;
	private AbstractChunkManager acm;

	public AbstractDiscordFile(AbstractChunkManager acm)
	{
		this(acm,null);
//		this.posInCurrentChunk = Chunk.getDataOffset();
//		this.baseChunk = acm.allocateChunk();
//		this.currentChunk = acm.allocateChunk();
//		this.baseChunk.setNext(this.currentChunk.getID());	
//		flushBaseChunk();
	}

	public AbstractDiscordFile(AbstractChunkManager acm, EntityID baseID)
	{
		this.acm = acm;
		this.posInCurrentChunk = Chunk.getDataOffset();

		if (baseID != null)
		{
			this.baseChunk = acm.getChunk(baseID);
			this.currentChunk = acm.getChunk(baseChunk.getNext());
			this.length = baseChunk.getLong(Chunk.getDataOffset());
		}
		else
		{
			this.posInCurrentChunk = Chunk.getDataOffset();
			this.baseChunk = acm.allocateChunk();
			this.currentChunk = acm.allocateChunk();
			this.baseChunk.setNext(this.currentChunk.getID());
		}
	}

	/**
	 * This method should be implemented by subclasses that do some initialization to the base {@link Chunk}.
	 * The chunk is NOT automatically flushed!
	 */
	public abstract void initializeBaseChunk();

	private int getMaxChunkBytesExcludingMeta()
	{
		return acm.getMaxChunkByteSize() - Chunk.getDataOffset();
	}

	public int read()
	{
		boolean isEOF = (currentChunk.getNext() == null && posInCurrentChunk == acm.getMaxChunkByteSize()) || getErrorlessFilePointer()==length();
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


	public void write(int b)
	{
		setLength(Math.max(length, passedChunks * getMaxChunkBytesExcludingMeta() + posInCurrentChunk));
		this.currentChunkTainted = true;
		this.currentChunk.getChunkData()[posInCurrentChunk] = (byte) b;
		posInCurrentChunk++;
		if (posInCurrentChunk >= acm.getMaxChunkByteSize())
		{
			flushBaseChunk(); //To update the length
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

	public long getFilePointer()
	{
		return getErrorlessFilePointer();
	}


	public void seek(long pos)
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


	public int read(byte[] b, int off, int len)
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


	public int read(byte[] b)
	{
		return read(b, 0, b.length);
	}


	public void write(byte[] b)
	{
		write(b, 0, b.length);
	}


	public void write(byte[] b, int off, int len)
	{

		if (off < 0 || len < 0 || off + len > b.length)
			throw new ArrayIndexOutOfBoundsException();
		for (int i = 0; i < len; ++i)
			write (b[off + i]);

	}


	public long length()
	{
		return length-Chunk.getDataOffset()+1;
	}

	/**
	 * Sets the new length. Does not flush the chunk!
	 * @param newLength
	 */
	public void setLength(long newLength)
	{
		length = newLength;
		baseChunk.setLong(Chunk.getDataOffset(), newLength);
	}

	public boolean flush()
	{
		//flushBaseChunk();
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

	/**
	 * Returns the base {@link Chunk} of this file. It usually contains metadata.
	 * @return
	 */
	protected Chunk getBaseChunk()
	{
		return baseChunk;
	}

	public EntityID getID()
	{
		return getBaseChunk().getID();
	}
	protected void flushBaseChunk()
	{
		acm.flushChunk(baseChunk);
	}
	protected AbstractChunkManager getACM()
	{
		return acm;
	}
}
