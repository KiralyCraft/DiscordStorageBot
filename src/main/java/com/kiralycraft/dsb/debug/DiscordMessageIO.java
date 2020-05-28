package com.kiralycraft.dsb.debug;

import com.kiralycraft.dsb.entities.EntityID;
import com.kiralycraft.dsb.filesystem.FileIOInterface;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.internal.utils.Helpers;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class DiscordMessageIO implements FileIOInterface {

    int currJDA = 0;

    public DiscordMessageIO() {

    }

    public JDA getJDA() {
        JDA jda = Debug.jdaList.get(currJDA++);
        if (currJDA > Debug.jdaList.size() - 1) {
            currJDA = 0;
        }
        return jda;
    }
    private long getNewSnowflake()
  	{
      	return (System.currentTimeMillis()-1420070400000L) << 22;
  	}
    public JDA getJDA(long snowflakeID) {
        int ID = (int) ((getMillisFromID(snowflakeID) / 1000) % Debug.jdaList.size());
        JDA jda = Debug.jdaList.get(ID);
        return jda;
    }

    @Override
    public int getChunkSize() {
        return 1998;
    }

    public long getMillisFromID(long snowflake) {
        return (snowflake >> 22) + 1420070400000L;
    }

    @Override
    public String getRawChunkData(EntityID eid) throws IOException {
        Message message = getChannel(getJDA()).retrieveMessageById(eid.getEntityID()).complete();
        if (message != null) {
        	String rawContent = message.getContentRaw();
            return rawContent.substring(1,rawContent.length()-1);
        }
        throw new IOException("Message not found " + eid.getLoggableID());
    }

    @Override
    public boolean updateRawChunkData(EntityID eid, String newData) throws IOException 
    {
    	System.out.println("Updating size "+newData.length()+"\""+newData+"\"");
    	try
    	{
    		getChannel(getJDA(eid.getEntityID())).editMessageById(eid.getEntityID(), "G"+newData+"G").complete();
    		return true;
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		System.err.println("Expected JDA for messageID: "+eid.getEntityID()+" ("+Debug.jdaList.indexOf(getJDA(eid.getEntityID()))+") does not correspond to the actual JDA! Attempting recover");
    		
    		JDA actualJDA = null;
    		int jdaCount = Debug.jdaList.size();
    		for (int currentIndex=0;currentIndex<jdaCount;currentIndex++)
    		{
    			JDA correctionJDA = Debug.jdaList.get(currentIndex);
    			try
    			{
    				System.err.println("Trying JDA "+(currentIndex+1)+"/"+jdaCount);
    				getChannel(getJDA(eid.getEntityID())).editMessageById(eid.getEntityID(), "G"+newData+"G").complete(); //Complete, pentru ca vrem try catch
    				actualJDA = correctionJDA;
    				break;
    			}
    			catch(Exception e2)
    			{
    				;//Nothing, ignore error
    			}
    		}
    		
    		if (actualJDA!=null)
    		{
    			System.err.println("Actual JDA was "+Debug.jdaList.indexOf(actualJDA));
    		}
    		else
    		{
    			System.err.println("Could not find the actual JDA!");
    		}
    	}
        return false;
    }

    @Override
    public EntityID createEmptyChunk(String emptyChunkDate) throws IOException 
    {
    	EntityID chosenID;
    	System.out.println("Creating empty chunk");
    	int attemptCount = 0;
    	while(true)
    	{
    		long predictedSnowFlake = getNewSnowflake();
	    	JDA predictedJDA = getJDA(predictedSnowFlake);
	    	
//	    	StringBuilder testingBuilder = new StringBuilder();
//	    	testingBuilder.append(emptyChunkDate);
	    	
//	    	if (Helpers.isEmpty(testingBuilder))
//	            System.out.println("true");
//	        for (int i = 0; i < testingBuilder.length(); i++)
//	        {
//	            if (!Character.isWhitespace(testingBuilder.charAt(i)))
//	               System.out.println("not whitespace");
//	        }
	        
	    	
	        Message message = getChannel(predictedJDA).sendMessage("G"+emptyChunkDate+"G").complete();
	        if (getJDA(message.getIdLong()).equals(predictedJDA))
	        {
	        	System.out.println("PredictedJDA corresponds to actual JDA. Attempt #"+attemptCount);
	        	chosenID = new EntityID(Debug.getGuildID(), Debug.getChannelID(), message.getIdLong());
	        	break;
	        }
	        else
	        {
	        	System.out.println("Predicted JDA does not correspond to actual JDA. Attempt #"+attemptCount);
	        	getChannel(predictedJDA).deleteMessageById(message.getIdLong()).queue();
	        }
	        attemptCount++;
    	}
        return chosenID;
    }


  

	public static TextChannel getChannel(JDA jdacurr) 
    {
        return jdacurr.getGuildById(Debug.guildID).getTextChannelById(Debug.channelID);
    }
}
