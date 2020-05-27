package com.kiralycraft.dsb.encoder;

public interface EncoderInterface
{
	/**
	 * The maximum number of bytes a {@link Chunk} may contain to stay at or below the expected size.
	 * @return
	 */
	public int getMaxChunkBytes(int targetRepresentationSize);
	
	/**
	 * Returns the encoded String representing the given data.
	 * @param toEncode
	 * @return
	 */
	public String encodeData(byte[] toEncode);
	
	/**
	 * Returns the decoded byte array from the encoded source.
	 * @param toDecode
	 * @return
	 */
	public byte[] decodeData(String toDecode);
}
