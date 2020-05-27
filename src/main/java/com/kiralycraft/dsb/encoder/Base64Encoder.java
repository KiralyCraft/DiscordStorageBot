package com.kiralycraft.dsb.encoder;

import java.util.Base64;

public class Base64Encoder implements EncoderInterface
{

	@Override
	public int getMaxChunkBytes(int targetRepresentationSize)
	{
		return (3 * (targetRepresentationSize / 4)) - 2; //2 is the max number of padding = put the at end of any base64 string. There can be at most 2
	}

	@Override
	public String encodeData(byte[] toEncode)
	{
		return Base64.getEncoder().encodeToString(toEncode);
	}

	@Override
	public byte[] decodeData(String toDecode)
	{
		 return Base64.getDecoder().decode(toDecode);	
	}

}
