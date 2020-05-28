package com.kiralycraft.dsb.debug;

import com.kiralycraft.dsb.entities.EntityID;
import com.kiralycraft.dsb.filesystem.FileIOInterface;
import com.kiralycraft.dsb.utils.UnicodeMeasurement;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.IOException;

;

public class DiscordEmbedIO implements FileIOInterface {

    //FIXED VALUES
    private final int TITLE_LENGHT = 256 - 2;
    private final int VALUE_LENGHT = 1024 - 2;
    private final int DATA_MAX_LENGHT = TITLE_LENGHT + VALUE_LENGHT;
    int currJDA = 0;

    public DiscordEmbedIO() {

    }

    public static TextChannel getChannel(JDA jdacurr) {
        return jdacurr.getGuildById(Debug.guildID).getTextChannelById(Debug.channelID);
    }

    public JDA getJDA() {
        JDA jda = Debug.jdaList.get(currJDA++);
        if (currJDA > Debug.jdaList.size() - 1) {
            currJDA = 0;
        }
        return jda;
    }

    private long getNewSnowflake() {
        return (System.currentTimeMillis() - 1420070400000L) << 22;
    }

    public JDA getJDA(long snowflakeID) {
        int ID = (int) ((getMillisFromID(snowflakeID) / Debug.BOT_LINGER) % Debug.jdaList.size());
        JDA jda = Debug.jdaList.get(ID);
        return jda;
    }

    @Override
    public int getChunkSize() {
        return 6000 - 20;
    }

    public long getMillisFromID(long snowflake) {
        return (snowflake >> 22) + 1420070400000L;
    }

    @Override
    public String getRawChunkData(EntityID eid) throws IOException {
        Message message = getChannel(getJDA()).retrieveMessageById(eid.getEntityID()).complete();
        if (message != null) {
            StringBuilder s = new StringBuilder();
            for (MessageEmbed.Field field : message.getEmbeds().get(0).getFields()) {
                s.append(getContentMessage(field.getName())).append(getContentMessage(field.getValue()));
            }
            return s.toString();
        }
        throw new IOException("Message not found " + eid.getLoggableID());
    }

    @Override
    public boolean updateRawChunkData(EntityID eid, String newData) throws IOException {
        try {
            MessageEmbed buildEmbed = buildEmbedMessage(newData);
            StringBuilder s = new StringBuilder();
            for (MessageEmbed.Field field : buildEmbed.getFields()) {
                s.append(getContentMessage(field.getName())).append(getContentMessage(field.getValue()));
            }
            String builtMessage = s.toString();
            getChannel(getJDA(eid.getEntityID())).editMessageById(eid.getEntityID(), buildEmbed).complete();
            String rawChunkData = getRawChunkData(eid);
            int rawLenght = UnicodeMeasurement.getActualLength(rawChunkData);
            int newDataLenght = UnicodeMeasurement.getActualLength(newData);
            if (UnicodeMeasurement.getActualLength(builtMessage) != newDataLenght) {
                //System.out.println("Invalid something here with lenght");
                System.out.println("[" + eid.getEntityID() + "]" + newData + "\n" + builtMessage);
                //throw new Exception("Invalid lenght " + rawLenght + " - " + newDataLenght);
                return true;
            }
            return true;
        } catch (Exception e) {
            System.err.println("Expected JDA for messageID: " + eid.getEntityID() + " (" + Debug.jdaList.indexOf(getJDA(eid.getEntityID())) + ") does not correspond to the actual JDA! Attempting recover");
            e.printStackTrace();
            JDA actualJDA = null;
            int jdaCount = Debug.jdaList.size();
            for (int currentIndex = 0; currentIndex < jdaCount; currentIndex++) {
                JDA correctionJDA = Debug.jdaList.get(currentIndex);
                try {
                    System.err.println("Trying JDA " + (currentIndex + 1) + "/" + jdaCount);
                    getChannel(getJDA(eid.getEntityID())).editMessageById(eid.getEntityID(), buildEmbedMessage(newData)).complete(); //Complete, pentru ca vrem try catch
                    actualJDA = correctionJDA;
                    break;
                } //get JDA with random and checked list
                catch (Exception e2) {
                    ;//Nothing, ignore error
                }
            }

            if (actualJDA != null) {
                System.err.println("Actual JDA was " + Debug.jdaList.indexOf(actualJDA));
            } else {
                System.err.println("Could not find the actual JDA!");
            }
        }
        return false;
    }

    @Override
    public EntityID createEmptyChunk(String emptyChunkDate) throws IOException {
        EntityID chosenID;
        System.out.println("Creating empty chunk");
        int attemptCount = 0;
        while (true) {
            long predictedSnowFlake;
            long synchronizationTime = System.currentTimeMillis();
            while (true) {
                predictedSnowFlake = getNewSnowflake();
                if (getMillisFromID(predictedSnowFlake) % Debug.BOT_LINGER < (Debug.BOT_LINGER * Debug.BOT_LINGER_TIMEFRAME_SYNC)) {
                    break;
                } else {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                    }
                }
            }
            long currentTimeNow = getMillisFromID(predictedSnowFlake);

            JDA predictedJDA = getJDA(predictedSnowFlake);
            Message message = getChannel(predictedJDA).sendMessage(buildEmbedMessage(emptyChunkDate)).complete();
            if (getJDA(message.getIdLong()).equals(predictedJDA)) {
//	        	System.out.println("Creating chunk ID: "+message.getIdLong()+" with JDA "+getJDAIndex(predictedJDA));
                long creationTime = getMillisFromID(message.getIdLong());

                System.out.println("PredictedJDA corresponds to actual JDA. Attempt #" + attemptCount + ", linger: " + Debug.BOT_LINGER + ", calculated: " + (creationTime - currentTimeNow) + ". Sync: " + (currentTimeNow - synchronizationTime));
                chosenID = new EntityID(Debug.getGuildID(), Debug.getChannelID(), message.getIdLong());
                break;
            } else {
                System.out.println("Predicted JDA does not correspond to actual JDA. Attempt #" + attemptCount + ", linger: " + Debug.BOT_LINGER + " ms" + ". Sync: " + (currentTimeNow - synchronizationTime));
                getChannel(predictedJDA).deleteMessageById(message.getIdLong()).queue();
            }
            attemptCount++;
        }
        return chosenID;
    }

    public MessageEmbed buildEmbedMessage(String chunkData) {
        EmbedBuilder builder = new EmbedBuilder();
        int currentIndex = 0;
        for (int i = 0; i < UnicodeMeasurement.getActualLength(chunkData) - DATA_MAX_LENGHT; i += DATA_MAX_LENGHT) {
            String chunkPart = subString(chunkData, i, DATA_MAX_LENGHT);
            String fieldTitle = subString(chunkPart, 0, TITLE_LENGHT);
            String fieldValue = subString(chunkPart, UnicodeMeasurement.getActualLength(fieldTitle), DATA_MAX_LENGHT - UnicodeMeasurement.getActualLength(fieldTitle));
            //System.out.println("buildEmbed#forloop Title lenght > " + UnicodeMeasurement.getActualLength(fieldTitle) + " - Value lenght > " + UnicodeMeasurement.getActualLength(fieldValue));
            builder.addField("K" + fieldTitle + "K", "K" + fieldValue + "K", false);
            currentIndex += UnicodeMeasurement.getActualLength(chunkPart);
        }
        if (currentIndex < UnicodeMeasurement.getActualLength(chunkData)) {
            String fieldTitle = subString(chunkData, currentIndex, TITLE_LENGHT);
            String fieldValue = subString(chunkData, UnicodeMeasurement.getActualLength(fieldTitle) + currentIndex, UnicodeMeasurement.getActualLength(chunkData) - UnicodeMeasurement.getActualLength(fieldTitle) - currentIndex);
            //System.out.println("buildEmbed#last Title lenght > " + UnicodeMeasurement.getActualLength(fieldTitle) + " - Value lenght > " + UnicodeMeasurement.getActualLength(fieldValue));
            builder.addField("K" + fieldTitle + "K", "K" + fieldValue + "K", false);
        }
        return builder.build();
    }

    public String getContentMessage(String input) {
        return input.substring(1, input.length() - 1);
    }

    public String subString(String input, int index, int lenght) {
        return input.substring(index, input.offsetByCodePoints(index, lenght));
    }
}
