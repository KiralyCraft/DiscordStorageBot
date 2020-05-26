package com.kiralycraft.dsb.debug;

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

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Debug extends ListenerAdapter {

    private static final long guildID = 713822278307282984L;
    private static final long channelID = 714569408890273802L;
    public static List<JDA> jdaList = new ArrayList<>();

    public static int bufferSize = 4;

    public Debug() throws LoginException, InterruptedException {
        jdaList.add(getNewJDA("NzE0NjE3OTQzNjg1NzI2MjIz.XsxUCA.mSDo5-aLLZ4ZBSE6KYNuLT4MuzM").build());
        jdaList.add(getNewJDA("NzE0NjE3ODY0MDk4Njc2ODk4.XsxT_Q.6Mg5iXb9djb2x4KZdG3SpH2asBM").build());
        jdaList.add(getNewJDA("NzE0NjE3NzU1NTAzOTUxOTMz.XsxT7g.kiFLWKnkd-hZwc2fiFq5y9FXP_Q").build());
        jdaList.add(getNewJDA("NzE0NjA2MTg0Mzk2NDg4NzY0.XsxT5A.OQWPL878TUIYBkglLRKdOTMI6zc").build());
        jdaList.add(getNewJDA("NzE0NjA1ODQwMjY2NTU5NTI4.XsxT1g.XbpNd0C0z_Ti17OGQQknd3QGsDQ").build());
        Thread.sleep(2000L);
        jdaList.add(getNewJDA("NzEzODIzMzA1MDI2NjMzODE5.XsxTxA.q8QkfVH3R_eUjPBcr0ADG2m6zlo").addEventListeners(this).build());
    }

    public static void main(String[] args) throws LoginException, InterruptedException {
        new Debug();
    }

    public static TextChannel getChannel(JDA jdacurr) {
        return jdacurr.getGuildById(guildID).getTextChannelById(channelID);
    }

    public static long getGuildID() {
        return guildID;
    }

    public static long getChannelID() {
        return channelID;
    }

    private JDABuilder getNewJDA(String token) {
        return JDABuilder.create(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS);
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        new Thread("Broccoli") {
            public void run() {
                //FileBasedIO fbio = new FileBasedIO();
                DiscordBasedIO discordBasedIO = new DiscordBasedIO();
                AbstractChunkManager acm = new SingleThreadedChunkManager(discordBasedIO);
                TextBasedFilesystem tbf = new TextBasedFilesystem(acm, new EntityID(guildID, channelID, 714857705382084678L));

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
        }.start();
    }
}
