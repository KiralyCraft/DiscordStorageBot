package com.kiralycraft.dsb.debug;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.annotation.Nonnull;

import com.kiralycraft.dsb.encoder.UnicodeHexEncoder;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UnicodeDebug extends ListenerAdapter
{
	private static ArrayList<JDA> jdaList = new ArrayList<JDA>();
	public static void main(String args[]) throws Exception
	{
		UnicodeHexEncoder uhe = new UnicodeHexEncoder();
		
		int progress = 0;
		for (int i=0;i<256;i++)
		{
			for (int j=0;j<256;j++)
			{
				byte[] expectedData = new byte[] {(byte) i,(byte) j};
				byte[] receivedData = uhe.decodeData(uhe.encodeData(expectedData));
				
				for (int check=0;check<expectedData.length;check++)
				{
					if (expectedData[check]!=receivedData[check])
					{
						System.out.println("Check failed! Expected "+i+","+j+" but got "+receivedData[0]+", "+receivedData[1]);
						return;
					}
				}
				progress++;
				
				if (progress%1000 == 0)
				{
					System.out.println("Progress: "+(progress/(256*256d))*100d+" %");
				}
			}
		}
		System.out.println();
//		final char[] chars = Character.toChars(0x20000);
//		final char[] chars = Character.toChars(0x2A6D6);
//		System.out.println(new String(chars).length());
////		final String s = new String(chars);
//		String sequence = new String(chars);
//		 int count = 0;
//		    for (int i = 0, len = sequence.length(); i < len; i++) {
//		      char ch = sequence.charAt(i);
//		      if (ch <= 0x7F) {
//		        count++;
//		      } else if (ch <= 0x7FF) {
//		        count += 2;
//		      } else if (Character.isHighSurrogate(ch)) {
//		        count += 4;
//		        ++i;
//		      } else {
//		        count += 3;
//		      }
//		    }
//		   System.out.println(count);
		
		
//		System.out.println(new String(uhe.decodeData(uhe.encodeData(new String("00112233445566778899AABBCCDDEEFF").getBytes()))));
		
		
		
		
		
		
////		System.out.println(5 << 4);
//		
//		System.out.println(s);
//		System.out.println(s.length());
//		
//		
//		System.out.println(s.codePointAt(0)+" "+0x1F4A9+" "+Integer.valueOf("1F4A9", 16));
//		
//		
//		final byte[] asBytes = s.getBytes(StandardCharsets.UTF_8);
//		
//		int value = 65535;
//		
//		byte[] toEncode =  new byte[] {
//	            (byte)(value >>> 24),
//	            (byte)(value >>> 16),
//	            (byte)(value >>> 8),
//	            (byte)(value >>> 0)};
//		
////		for (byte b:toEncode)
////		{
////			System.out.println(b);
////		}
//		int[] hexChars = new int[toEncode.length * 2];
//		for (int j = 0; j<toEncode.length; j++)
//		{
//			int v = toEncode[j] & 0xFF;
//			hexChars[j * 2] = v >>> 4;
//			hexChars[j * 2+1] = v & 0x0F;
//
//			System.out.println(hexChars[j * 2] +" "+hexChars[j * 2+1]);
//		}
//		
//		int cp = 0x10400;
//	    String text = "test \uD801\uDC00";
//	    System.out.println("cp:    " + cp);
////	    text.code
//	    System.out.println("found: " + text.codePointAt(5));
//	    System.out.println("len:   " + text.length());
//	    
//	    String cc2 = "2202";
//	    String text2 = String.valueOf(Character.toChars(Integer.parseInt(cc2, 16)));
//	    System.out.println(hexToDoubleHex(Integer.toHexString(2)));
	    
//		UnicodeHexEncoder uhe = new UnicodeHexEncoder();
//		for( byte b:uhe.getCombinationN(5, 1))
//		{
//			System.out.print((char)b+" ");
//		}
//		System.out.println();
	   
//		String nth = "";
//		permNum--;
//		int N = numbers.size();
//
//		for (int i = 1; i < N; ++i)
//		{
//			int j = permNum / Factorial(N - i);
//			permNum = permNum % Factorial(N - i);
//			nth = nth + numbers.get(j);
//			numbers.remove(j);
//
//			if (permNum == 0)
//				break;
//		}
//
//		for (int i = 0; i < numbers.size(); i++)
//			nth = nth + numbers.get(i);
	   
	   
//	    System.out.println(Integer.toHexString(255));
//		Debug.buildJDAList(jdaList,new UnicodeDebug(),2);
		
	}
	
	@Override
    public void onReady(@Nonnull ReadyEvent event) {
        new Thread("Broccoli") {
            public void run() 
            {
            	
            	DiscordMessageIO dbio = new DiscordMessageIO();
            	
            	final char[] chars = Character.toChars(0xFE01);
        		final String s = new String(chars);
        		final byte[] asBytes = s.getBytes(StandardCharsets.UTF_8);
        		
        		int codePoint = Character.codePointAt(s, 0);
        		System.out.println(Integer.toHexString(codePoint));
        		
//    			for (int i=0;i<=255;i++)
//    			{
//    				for (int j=0;j<=255;j++)
//    				{
//    					String hexString = hexToDoubleHex(Integer.toHexString(i))+hexToDoubleHex(Integer.toHexString(j));
//    					System.out.println("Trying "+hexString);
    					try
						{
//							dbio.createEmptyChunk(String.valueOf(Character.toChars(Integer.parseInt(hexString, 16))));
    						byte[] retreivedBytes = dbio.getRawChunkData(dbio.createEmptyChunk(s)).getBytes(Charset.forName("UTF-8"));
    						
    						if (asBytes.length == retreivedBytes.length)
    						{
    							for (int i=0;i<asBytes.length;i++)
    							{
    								if (asBytes[i] != retreivedBytes[i])
    								{
    									System.out.println("Discrepancy detected!");
    								}
    							}
    							
    							System.out.println("Check done");
    						}
    						else
    						{
    							System.out.println("Different length: "+asBytes.length+" - "+retreivedBytes.length);
    						}
    						
						} 
    					catch (Exception e)
    					{
    						e.printStackTrace();
    					}
//    				}
//    			}
            }
        }.start();
    }
	
	public static String hexToDoubleHex(String hex)
	{
		if (hex.length()==1)
		{
			return "0"+hex;
		}
		else
		{
			return hex;
		}
	}
}
