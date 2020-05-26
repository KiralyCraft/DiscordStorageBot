package com.kiralycraft.dsb.chunks;

import com.kiralycraft.dsb.filesystem.FileIOInterface;

public class SingleThreadedChunkManager extends AbstractChunkManager
{

	public SingleThreadedChunkManager(FileIOInterface fioi)
	{
		super(fioi);
	}

}
