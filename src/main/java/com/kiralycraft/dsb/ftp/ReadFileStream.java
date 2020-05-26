package com.kiralycraft.dsb.ftp;

import com.kiralycraft.dsb.filesystem.TextBasedFilesystem;
import com.kiralycraft.dsb.filesystem.entries.MetadataDiscordFile;

import java.io.IOException;
import java.io.InputStream;

public class ReadFileStream extends InputStream {
    private MetadataDiscordFile newFile;

    public ReadFileStream(String filename, TextBasedFilesystem tbf) {
        System.out.println("Reading the file " + filename + " from folder " + tbf.getCurrentPath());
        this.newFile = tbf.getFile(filename);
    }

    @Override
    public int read() throws IOException {
    	int toReturn = this.newFile.read();
        return toReturn;
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
