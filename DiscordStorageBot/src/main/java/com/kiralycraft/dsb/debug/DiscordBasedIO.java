package com.kiralycraft.dsb.debug;

import com.kiralycraft.dsb.entities.EntityID;
import com.kiralycraft.dsb.filesystem.FileIOInterface;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class DiscordBasedIO implements FileIOInterface {

    int currJDA = 0;

    public DiscordBasedIO() {

    }

    public JDA getJDA() {
        JDA jda = Debug.jdaList.get(currJDA++);
        if (currJDA > Debug.jdaList.size() - 1) {
            currJDA = 0;
        }
        return jda;
    }

    public JDA getJDA(long snowflakeID) {
        int ID = (int) ((getMillisFromID(snowflakeID) / 2500) % Debug.jdaList.size());
        JDA jda = Debug.jdaList.get(ID);
        System.out.println("ID: " + ID + "\nSnowflakeID: " + snowflakeID);
        return jda;
    }

    @Override
    public int getChunkSize() {
        return 2000;
    }

    public long getMillisFromID(long snowflake) {
        return (snowflake >> 22) + 1420070400000L;
    }

    @Override
    public String getRawChunkData(EntityID eid) throws IOException {
        Message message = Debug.getChannel(getJDA()).retrieveMessageById(eid.getEntityID()).complete();
        if (message != null) {
            return message.getContentRaw();
        }
        throw new IOException("Message not found " + eid.getLoggableID());
    }

    @Override
    public boolean updateRawChunkData(EntityID eid, String newData) throws IOException {
        String temp = !newData.isEmpty() ? newData : ".";
        System.out.println("Updating chunk with ID: " + eid.getEntityID());
        Debug.getChannel(getJDA(eid.getEntityID())).editMessageById(eid.getEntityID(), temp).queue();
        return true;
    }

    @Override
    public EntityID createEmptyChunk(String emptyChunkDate) throws IOException {
        AtomicLong entityID = new AtomicLong();
        //Message message1 = Debug.getChannel(getJDA(System.currentTimeMillis() << 22)).sendMessage(".").complete();
        Message message = Debug.getChannel(getJDA((System.currentTimeMillis() + 350) << 22)).sendMessage(emptyChunkDate).complete();
        System.out.println("Creating chunk with ID: " + message.getIdLong());
        entityID.set(message.getIdLong());
        EntityID eid = new EntityID(Debug.getGuildID(), Debug.getChannelID(), entityID.get());

        //updateRawChunkData(eid, "");
        return eid;
    }

    @Override
    public boolean checkChunkExists(EntityID eid) throws IOException {
        throw new IOException("Not implemented ");
    }
}
