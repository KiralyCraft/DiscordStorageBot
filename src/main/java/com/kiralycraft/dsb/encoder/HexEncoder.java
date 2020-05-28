package com.kiralycraft.dsb.encoder;

public class HexEncoder implements EncoderInterface
{
	public static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	@Override
	public int getMaxChunkBytes(int targetRepresentationSize)
	{
		return targetRepresentationSize / 2;
	}

	@Override
	public String encodeData(byte[] toEncode)
	{
		char[] hexChars = new char[toEncode.length * 2];
		for (int j = 0; j < toEncode.length; j++)
		{
			int v = toEncode[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}

	@Override
	public byte[] decodeData(String toDecode)
	{
		byte[] val = new byte[toDecode.length() / 2];
		for (int i = 0; i < val.length; i++)
		{
			int index = i * 2;
			int j = Integer.parseInt(toDecode.substring(index, index + 2), 16);
			val[i] = (byte) j;
		}
		return val;
	}

}
