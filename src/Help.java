import java.io.FileNotFoundException;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class Help {
	static void sendHelp(MessageChannel channel,User author) throws FileNotFoundException {
		EmbedBuilder eb = new EmbedBuilder();
		String out ="\n" + 
				"HBot Version 1.0.0 Help \n" + 
				"#- - - - - - - - - - - -# \n" + 
				"HBot is a bot that can be used to check whether or not a game server is up. \n" + 
				"#- - - - - - - - - - - -# \n" + 
				"Commands: \n" + 
				"`.help`  \n" + 
				"Sends this message to you in PMs.\n" + 
				"--\n" + 
				"`.server`\n" + 
				"Checks whether a server is up via opening a port.\n" + 
				"Usage:\n" + 
				"`.server` checks the default set server and port combination\n" + 
				"`.server <IP address or domain> <port>` will check the given ip for whether or not the port is open.\n" + 
				"--\n" + 
				"`.setDefaultServer`\n" + 
				"Sets the default server for the `.server` command\n" + 
				"Usage:\n" + 
				"`.setDefaultServer <IP address or domain>`\n" + 
				"--\n" + 
				"";
		eb.setDescription(out);
		channel.sendMessage(eb.build()).queue();
	}
}
