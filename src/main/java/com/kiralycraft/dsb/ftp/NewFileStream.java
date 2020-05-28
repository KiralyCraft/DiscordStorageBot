package com.kiralycraft.dsb.ftp;

import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.filesystem.TextBasedFilesystem;
import com.kiralycraft.dsb.filesystem.entries.MetadataDiscordFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

public class NewFileStream extends OutputStream {
    protected long bytesTransferred = 0;
    long lastTime = System.currentTimeMillis();
    long bytesSent = 0;
    private MetadataDiscordFile newFile;
    private TextBasedFilesystem tbf;

    public NewFileStream(AbstractChunkManager acm, String filename, TextBasedFilesystem tbf) {
        System.out.println("Writing the file " + filename + " to folder " + tbf.getCurrentPath());
        this.newFile = new MetadataDiscordFile(acm, filename, false, 0); //Initial size is 0 bytes
        this.tbf = tbf;
    }

    @Override
    public void write(@NotNull byte[] bytes, int i, int i1) throws IOException {
        super.write(bytes, i, i1);
        long timeMeasurementNow = System.currentTimeMillis();
        bytesSent += i1 - i;
        bytesTransferred += i1 - i;
        if (timeMeasurementNow - lastTime >= 1000) {
            double temp = (bytesSent / 1000d) / ((timeMeasurementNow - lastTime) / 1000d);
            System.out.println("Speed: " + temp + " - BytesSent: " + bytesTransferred);
            lastTime = timeMeasurementNow;
            bytesSent = 0;
        }
    }

    @Override
    public void write(int b) throws IOException {
        this.newFile.write(b);
    }

    @Override
    public void flush() throws IOException {
        this.newFile.flush(true);
    }

    @Override
    public void close() throws IOException {
        flush(); //Flushes the data in memory to the disk

        System.out.println("Written the file " + newFile.getFilename() + " to folder " + tbf.getCurrentPath());
        if (!tbf.addFileRaw(newFile)) {
            throw new IOException("Could not add the file to the filesystem!");
        }
    }

}
