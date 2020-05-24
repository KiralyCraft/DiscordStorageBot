package com.kiralycraft.dsb.debug;

import java.io.File;
import java.io.IOException;

import com.guichaguri.minimalftp.FTPServer;
import com.guichaguri.minimalftp.impl.NativeFileSystem;
import com.guichaguri.minimalftp.impl.NoOpAuthenticator;

public class Debug {

	public static void main(String[] args) throws IOException
	{
		// Uses the current working directory as the root
		File root = new File(".");

		// Creates a native file system
		NativeFileSystem fs = new NativeFileSystem(root);

		// Creates a noop authenticator, which allows anonymous authentication
		NoOpAuthenticator auth = new NoOpAuthenticator(fs);

		// Creates the server with the authenticator
		FTPServer server = new FTPServer(auth);

		// Start listening synchronously
		server.listenSync(21);
	}

}
