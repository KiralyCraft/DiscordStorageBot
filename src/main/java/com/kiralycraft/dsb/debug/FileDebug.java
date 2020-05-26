package com.kiralycraft.dsb.debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

import com.guichaguri.minimalftp.FTPServer;
import com.guichaguri.minimalftp.impl.NoOpAuthenticator;
import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.chunks.SingleThreadedChunkManager;
import com.kiralycraft.dsb.entities.EntityID;
import com.kiralycraft.dsb.filesystem.TextBasedFilesystem;
import com.kiralycraft.dsb.ftp.TextBasedFilesystemFTPIO;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class FileDebug
{
   

    public static void main(String[] args) throws Exception
    {
    	//FileBasedIO fbio = new FileBasedIO();
        FileBasedIO discordBasedIO = new FileBasedIO();
        AbstractChunkManager acm = new SingleThreadedChunkManager(discordBasedIO);
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
