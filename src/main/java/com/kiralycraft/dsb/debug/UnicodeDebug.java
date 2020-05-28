package com.kiralycraft.dsb.debug;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import com.kiralycraft.dsb.encoder.EncoderInterface;
import com.kiralycraft.dsb.encoder.HexEncoder;
import com.kiralycraft.dsb.encoder.UnicodeHexEncoder;
import com.kiralycraft.dsb.entities.EntityID;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UnicodeDebug extends ListenerAdapter
{
	public static void main(String args[]) throws Exception
	{
		//149,192
		
		Debug.buildJDAList(Debug.jdaList,new UnicodeDebug(),5);
		
		
		
		
//		
//		int progress = 0;
//		for (int i=0;i<256;i++)
//		{
//			for (int j=0;j<256;j++)
//			{
//				byte[] expectedData = new byte[] {(byte) i,(byte) j};
//				byte[] receivedData = uhe.decodeData(uhe.encodeData(expectedData));
//				
//				for (int check=0;check<expectedData.length;check++)
//				{
//					if (expectedData[check]!=receivedData[check])
//					{
//						System.out.println("Check failed! Expected "+i+","+j+" but got "+receivedData[0]+", "+receivedData[1]);
//						return;
//					}
//				}
//				progress++;
//				
//				if (progress%1000 == 0)
//				{
//					System.out.println("Progress: "+(progress/(256*256d))*100d+" %");
//				}
//			}
//		}
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
		
	}
	
	@Override
    public void onReady(@Nonnull ReadyEvent event) {
		
		try
		{
//			UnicodeHexEncoder uhe = new UnicodeHexEncoder();
//			
//			byte[] expectedData = new byte[] {-107,-63};
//			System.out.println("Supplied "+expectedData.length+" "+expectedData[0]+" "+expectedData[1]);
//			DiscordMessageIO dbio = new DiscordMessageIO();
//			EntityID ourID = dbio.createEmptyChunk("CHARACTER TEST");
//			dbio.updateRawChunkData(ourID,uhe.encodeData(expectedData));
//			
//			byte[] receivedData = uhe.decodeData(dbio.getRawChunkData(ourID));
//			
//			System.out.println(receivedData.length+" "+receivedData[0]+" "+receivedData[1]);
			
			
			//TESTING ABOVE
			//TESTING ABOVE
			//TESTING ABOVE
			//TESTING ABOVE
			//TESTING ABOVE
			
			
	    	EncoderInterface uhe = new UnicodeHexEncoder();
	    	DiscordMessageIO dbio = new DiscordMessageIO();
	    	int charactersAtOnce = 128; //128
	    	int checkPoint = 59200-10; //590
	    	int progress=0;
			dbio.createEmptyChunk("```CHARACTER TEST IN PROGRESS AT "+System.currentTimeMillis()+"```");
			
			ArrayList<EntityID> testingIDS = new ArrayList<EntityID>();
			for(int i=0;i<Debug.jdaList.size();i++)
			{
				System.out.println("Adding test message for JDA "+i);
				testingIDS.add(dbio.createEmptyChunk("CHARACTER TEST"));
			}
			
			for (int i=0;i<=255;i++)
			{
				System.out.println("Checking i ="+i);
				for (int j=0;j<=255;j+=charactersAtOnce/2)
				{
					if (progress < checkPoint-1 - charactersAtOnce/2)
					{
						progress+=charactersAtOnce/2;
					}
					else
					{
	//					(byte) i,(byte) j
						byte[] expectedData = new byte[charactersAtOnce];
						for (int k=0;k<charactersAtOnce;k+=2)
						{
							expectedData[k] = (byte) i;
							expectedData[k+1] = (byte) (j+k/2);
						}
//						System.out.println("Sending message...");
						EntityID ourID = testingIDS.get(progress%Debug.jdaList.size());
						
						dbio.updateRawChunkData(ourID,uhe.encodeData(expectedData));
		//    					dbio.createEmptyChunk(uhe.encodeData(expectedData));
						
						byte[] receivedData = uhe.decodeData(dbio.getRawChunkData(ourID));
						
						for (int check=0;check<expectedData.length;check++)
						{
							if (expectedData[check]!=receivedData[check])
							{
								System.out.println("Check failed! Expected "+i+","+j+" but got "+receivedData[0]+", "+receivedData[1]+". Lenghts: "+expectedData.length+","+receivedData.length);
								
								for (int p = 0;p<expectedData.length;p++)
								{
									System.out.println(expectedData[p]+";"+receivedData[p]);
								}
								return;
							}
						}
						progress+=charactersAtOnce/2;
//						System.out.println("Done!");
						if (progress%10 == 0)
						{
							System.out.println("Progress: "+(progress/(256*256d))*100d+" % - Checkpoint "+progress+". Sleeping 50 ms for heartbeat; "+i+" "+j);
							Thread.sleep(50);
						}
					}
					
					
				}
			}
			System.out.println("All done!");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
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
