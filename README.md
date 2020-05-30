# DiscordStorageBot

A proof-of-concept project that uses Discord (or any text-based platform) to store files and provide a FTP server with live access to it.

Files are stored in "Chunks", which are represented as encoded pieces of strings. It can accurately limit the resulting string size, so that it fits on Discord, for example, where the limit is 2000 characters for a normal message, and 6000 for an embedded one.

Speeds top out at about 12 kb/s write, 70 kb/s read using embedded messages. As of today (31.05.2020)

There is no size limit, but there is a limit for how many things fit in a folder.

See: https://github.com/Guichaguri/MinimalFTP

You may (in fact, definitely) need to disable the timout function of your FTP client as this thing is as slow as it can get.
