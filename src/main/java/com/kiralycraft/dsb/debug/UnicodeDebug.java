package com.kiralycraft.dsb.debug;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UnicodeDebug extends ListenerAdapter
{
	private static ArrayList<JDA> jdaList = new ArrayList<JDA>();
	public static void main(String args[]) throws Exception
	{
		final char[] chars = Character.toChars(0x1F4A9);
		final String s = new String(chars);
		final byte[] asBytes = s.getBytes(StandardCharsets.UTF_8);
		
		int cp = 0x10400;
	    String text = "test \uD801\uDC00";
	    System.out.println("cp:    " + cp);
//	    text.code
	    System.out.println("found: " + text.codePointAt(5));
	    System.out.println("len:   " + text.length());
	    
	    String cc2 = "2202";
	    String text2 = String.valueOf(Character.toChars(Integer.parseInt(cc2, 16)));
	    System.out.println(hexToDoubleHex(Integer.toHexString(2)));
//	    System.out.println(Integer.toHexString(255));
		Debug.buildJDAList(jdaList,new UnicodeDebug(),2);
		
	}
	
	@Override
    public void onReady(@Nonnull ReadyEvent event) {
        new Thread("Broccoli") {
            public void run() 
            {
            	
            	DiscordBasedIO dbio = new DiscordBasedIO();
            	
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
