package com.kiralycraft.dsb.debug;

import com.kiralycraft.dsb.entities.EntityID;
import com.kiralycraft.dsb.filesystem.FileIOInterface;

import java.io.IOException;

public class TelegramIO implements FileIOInterface {

    @Override
    public int getChunkSize() {
        return 0;
    }

    @Override
    public String getRawChunkData(EntityID eid) throws IOException {
        return null;
    }

    @Override
    public boolean updateRawChunkData(EntityID eid, String newData) throws IOException {
        return false;
    }

    @Override
    public EntityID createEmptyChunk(String emptyChunkData) throws IOException {
        return null;
    }
}
