package com.kiralycraft.dsb.ftp;

import com.kiralycraft.dsb.filesystem.TextBasedFilesystem;
import com.kiralycraft.dsb.filesystem.entries.MetadataDiscordFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public class ReadFileStream extends InputStream {
    protected long bytesTransferred = 0;
    long lastTime = System.currentTimeMillis();
    long bytesSent = 0;
    private MetadataDiscordFile newFile;
	private int speedMeasurepoints;
	private double averageSpeed;

    public ReadFileStream(String filename, TextBasedFilesystem tbf) {
        System.out.println("Reading the file " + filename + " from folder " + tbf.getCurrentPath());
        this.newFile = tbf.getFile(filename);
    }

    @Override
    public int read() throws IOException {
        int toReturn = this.newFile.read();
        return toReturn;
    }

    @Override
    public int read(@NotNull byte[] bytes, int i, int i1) throws IOException {
        int len = super.read(bytes, i, i1);
        long timeMeasurementNow = System.currentTimeMillis();
        bytesSent += len;
        bytesTransferred += len;
        if (timeMeasurementNow - lastTime >= 1000) {
            double temp = (bytesSent / 1000d) / ((timeMeasurementNow - lastTime) / 1000d);
            speedMeasurepoints++;
            averageSpeed = averageSpeed + (temp-averageSpeed)/speedMeasurepoints;
            
            System.out.println("Avg read speed: " + averageSpeed + " kb/s - BytesSent: " + bytesTransferred);
            lastTime = timeMeasurementNow;
            bytesSent = 0;
        }
        return len;
    }

    //    @Override
//    public int read(byte[] b) throws IOException {
//        return this.newFile.read(b);
//    }
//
//    @Override
//    public int read(byte[] b, int off, int len) throws IOException {
//        return this.newFile.read(b, off, len);
//    }
}
