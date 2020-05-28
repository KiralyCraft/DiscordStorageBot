package com.kiralycraft.dsb.debug;

import com.guichaguri.minimalftp.FTPServer;
import com.guichaguri.minimalftp.impl.NoOpAuthenticator;
import com.kiralycraft.dsb.chunks.AbstractChunkManager;
import com.kiralycraft.dsb.chunks.SingleThreadedChunkManager;
import com.kiralycraft.dsb.encoder.EncoderInterface;
import com.kiralycraft.dsb.encoder.UnicodeHexEncoder;
import com.kiralycraft.dsb.filesystem.FileIOInterface;
import com.kiralycraft.dsb.filesystem.TextBasedFilesystem;
import com.kiralycraft.dsb.ftp.TextBasedFilesystemFTPIO;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Debug extends ListenerAdapter {
    public static final long guildID = 713822278307282984L;
    public static final long channelID = 715584864165953577L;
    public static int BOT_LINGER = 1000;

    public static int bufferSize = 4;
    public static double BOT_LINGER_TIMEFRAME_SYNC = 0.5;
    public static List<JDA> jdaList = new ArrayList<JDA>();

    public Debug() throws Exception {
        buildJDAList(jdaList, this);
    }

    public static void buildJDAList(List<JDA> jdaList, ListenerAdapter la) throws Exception {
        buildJDAList(jdaList, la, 2);
    }

    public static void buildJDAList(List<JDA> jdaList, ListenerAdapter la, int botCount) throws Exception {
        Scanner scan = new Scanner(new File("discordtokens.txt"));

        ArrayList<JDABuilder> pendingBuilders = new ArrayList<JDABuilder>();

        int currentBotCount = 0;

        while (scan.hasNext()) {
            String readLine = scan.nextLine();
            if (!readLine.isEmpty() && !readLine.startsWith("#")) {
                pendingBuilders.add(getNewJDA(readLine));
                Thread.sleep(500);
                currentBotCount++;

                if (botCount != -1) {
                    if (currentBotCount == botCount) {
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < pendingBuilders.size() - 1; i++) {
            jdaList.add(pendingBuilders.get(i).build());
        }
        Thread.sleep(2000);
        jdaList.add(pendingBuilders.get(pendingBuilders.size() - 1).addEventListeners(la).build());
        scan.close();

    }

    public static void main(String[] args) throws Exception {
        if (args.length == 2) {
            BOT_LINGER = Integer.parseInt(args[0]);
            BOT_LINGER_TIMEFRAME_SYNC = Double.parseDouble(args[1]);
        }
        new Debug();
    }

    public static long getGuildID() {
        return guildID;
    }

    public static long getChannelID() {
        return channelID;
    }

    private static JDABuilder getNewJDA(String token) {
        return JDABuilder.create(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS);
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        try {
            //FileBasedIO fbio = new FileBasedIO();
            FileIOInterface fileInterface = new DiscordEmbedIO();
            EncoderInterface encoderInterface = new UnicodeHexEncoder();
            AbstractChunkManager acm = new SingleThreadedChunkManager(fileInterface, encoderInterface);
//                TextBasedFilesystem tbf = new TextBasedFilesystem(acm, null);
            TextBasedFilesystem tbf = new TextBasedFilesystem(acm, null);

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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
