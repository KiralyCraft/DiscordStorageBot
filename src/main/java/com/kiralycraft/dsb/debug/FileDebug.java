package com.kiralycraft.dsb.debug;

import java.io.IOException;

import com.guichaguri.minimalftp.FTPServer;
import com.guichaguri.minimalftp.impl.NoOpAuthenticator;
import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.chunks.SingleThreadedChunkManager;
import com.kiralycraft.dsb.encoder.EncoderInterface;
import com.kiralycraft.dsb.encoder.UnicodeHexEncoder;
import com.kiralycraft.dsb.filesystem.TextBasedFilesystem;
import com.kiralycraft.dsb.ftp.TextBasedFilesystemFTPIO;

public class FileDebug
{
   

    public static void main(String[] args) throws Exception
    {
    	FileBasedIO fbio = new FileBasedIO();
    	EncoderInterface encoderInterface = new UnicodeHexEncoder();
    	
        AbstractChunkManager acm = new SingleThreadedChunkManager(fbio,encoderInterface);
        TextBasedFilesystem tbf = new TextBasedFilesystem(acm, null);
//        TextBasedFilesystem tbf = new TextBasedFilesystem(acm, new EntityID(guildID,channelID,714919794779881573L));

        TextBasedFilesystemFTPIO fs = new TextBasedFilesystemFTPIO(acm, tbf);
        // Creates a noop authenticator, which allows anonymous authentication
        NoOpAuthenticator auth = new NoOpAuthenticator(fs);

        // Creates the server with the authenticator
        FTPServer server = new FTPServer(auth);
        server.setTimeout(2147483647);
        server.setBufferSize(1);

        // Start listening synchronously
        try {
            server.listenSync(21);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

  
}
