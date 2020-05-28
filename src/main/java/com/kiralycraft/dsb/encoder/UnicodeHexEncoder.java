package com.kiralycraft.dsb.encoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnicodeHexEncoder implements EncoderInterface
{
	private final int REPRESENTATION_SIZE = 4;
	private EncoderInterface baseEncoder;
	private HashMap<Integer,Integer> unicodeMemory;
	private HashMap<Integer,Integer> unicodeMemoryRev;
	
	private int unicodeCount;
	private Pattern unicodePattern = Pattern.compile("([0-9A-F]*)\\s+.*");
	
	public UnicodeHexEncoder()
	{
		this.baseEncoder = new HexEncoder();
		this.unicodeMemory = new HashMap<Integer,Integer>();
		this.unicodeMemoryRev = new HashMap<Integer,Integer>();
		
		System.out.println("Reading unicode database...");
		try
		{
			Scanner scan = new Scanner(new FileInputStream(new File("unicodeDatabase.txt")));
			unicodeCount = 0;
			while(scan.hasNext())
			{
				String line = scan.nextLine();
				Matcher matcher;
				if ((matcher = unicodePattern.matcher(line)).matches())
				{
					String unicodeCodePoint = matcher.group(1);
					
					int intUnicodeCodePoint = Integer.valueOf(unicodeCodePoint, 16);
					
//					String assertionCharacter = new String(Character.toChars(intUnicodeCodePoint)); //no more assertion, it's borked
					
					unicodeMemory.put(intUnicodeCodePoint,unicodeCount);
					unicodeMemoryRev.put(unicodeCount,intUnicodeCodePoint);
					unicodeCount++;
				}
			}
			System.out.println("Loaded "+unicodeMemory.size()+" unicodes");
		} 
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public int getMaxChunkBytes(int targetRepresentationSize)
	{
		return this.baseEncoder.getMaxChunkBytes(REPRESENTATION_SIZE*targetRepresentationSize);
	}

	@Override
	public String encodeData(byte[] toEncodeRaw) //hex
	{
		String encodedString = baseEncoder.encodeData(toEncodeRaw);
//		//System.out.println(encodedString);
		byte[] toEncode = encodedString.getBytes(Charset.forName("UTF-8"));
		
		//System.out.println("From upstream encoder:");
		for (byte b:toEncode)
		{
			//System.out.print(b+" ");
		}
		//System.out.println();

		
		int incomingLength = toEncode.length;
		
		if (incomingLength%REPRESENTATION_SIZE != 0)
		{
			throw new RuntimeException("Incoming Unicode encoder length not a multiple of the REPRESENTATION_SIZE: "+REPRESENTATION_SIZE);
		}
		
		StringBuilder newString = new StringBuilder();
		for (int i=0;i<incomingLength;i+=REPRESENTATION_SIZE)
		{
			//System.out.println("Encoded indexes:");
			int finalNumber = 0;
			for (int j = 0;j<REPRESENTATION_SIZE;j++)
			{
				int characterIndex = toEncode[i+j];
				
				if (characterIndex < 'A')
				{
					characterIndex-='0';
				}
				else if (characterIndex < 'F'+1)
				{
					characterIndex-='A';
					characterIndex+=10;
				}
				
				//System.out.print(characterIndex+" ; ");
				finalNumber += characterIndex << (4*j);
			}			
			//System.out.println("\nUnicode index write: "+finalNumber);
			newString.append(getUnicodeNth(finalNumber));
		}
		String toReturn = newString.toString();
		//System.out.println("Write string length: "+toReturn.length());
		return toReturn;
	}
	
	@Override
	public byte[] decodeData(String toDecode)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int ch = 0;
		int i = 0;
		do
		{
			ch = toDecode.codePointAt(i);
			try
			{
				int unicodeIndex = getUnicodeIndex(toDecode.codePointAt(i));
				//System.out.println("Unicode index read: "+unicodeIndex);
				baos.write(getByteArray(unicodeIndex));
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			i+=Character.charCount(ch);
			//System.out.println("Current decoding: "+i+"/"+toDecode.length());
		}
		while(i<toDecode.length());
		
		//System.out.println("To upstream encoder:");
		byte[] decodedBytes = baos.toByteArray();
		for (byte b:decodedBytes)
		{
			//System.out.print(b+" ");
		}
		//System.out.println();
		//System.out.println();
		 
		return baseEncoder.decodeData(new String(baos.toByteArray(),Charset.forName("UTF-8")));
	}

	private byte[] getByteArray(int unicodeIndex)
	{
		byte[] toEncode =  new byte[] {
	            (byte)(unicodeIndex >>> 24),
	            (byte)(unicodeIndex >>> 16),
	            (byte)(unicodeIndex >>> 8),
	            (byte)(unicodeIndex >>> 0)};
		
		int[] hexChars = new int[toEncode.length * 2];
		for (int j = 0; j<toEncode.length; j++)
		{
			int v = toEncode[j] & 0xFF;
			hexChars[j * 2] = v >>> 4;
			hexChars[j * 2+1] = v & 0x0F;
		}
		
		byte[] resultingArray = new byte[REPRESENTATION_SIZE];
		

		//System.out.println("Decoded indexes:");
		for (int i=REPRESENTATION_SIZE-1;i>=0;i--)
		{
			//System.out.print(hexChars[i+4]+" ; ");
			resultingArray[(REPRESENTATION_SIZE-1)-i] = (byte) HexEncoder.HEX_ARRAY[hexChars[i+(8-REPRESENTATION_SIZE)]];//cand era repr 4, aici era 4, cand e 5 e 3
		}
		//System.out.println();
		return resultingArray;
	}
	
	public int getUnicodeIndex(int codepoint)
	{
//		System.out.print("Fetching unicode index from codepoint "+codepoint+"; Exists: "+unicodeMemory.containsKey(codepoint)+", index ");
		int index = unicodeMemory.get(codepoint);
		if (index >= unicodeCount)
		{
			new RuntimeException("Decoded Unicode index: "+index+" is larger than the current alphabe size: "+unicodeCount);
		}
		return index;
	}
	
	private String getUnicodeNth(int index)
	{
		if (index >= unicodeCount)
		{
			throw new RuntimeException("Requested Unicode index: "+index+" is larger than the current alphabe size: "+unicodeCount);
		}
		else
		{
//			System.out.print("Fetching unicode codepoint from index "+index+"; Exists: "+unicodeMemoryRev.containsKey(index)+", codepoint ");
			String resultingUnicode = new String(Character.toChars(unicodeMemoryRev.get(index)));
//			System.out.println(resultingUnicode.codePointAt(0));
			return resultingUnicode;
		}
	}
}
