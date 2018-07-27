import net.dv8tion.jda.core.hooks.*;
import net.dv8tion.jda.core.utils.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.*;
import net.dv8tion.jda.core.EmbedBuilder;
import java.awt.Color;
public class MessageListener extends ListenerAdapter{
	public static void main(String[] args ) throws LoginException, InterruptedException, FileNotFoundException {
		final String VERSION = "1.0.0";
		String token = null;
		if (args.length != 0) {
			token = args[0];
		}
		else {
			File config = new File("./config.txt");
			Scanner reader = new Scanner(config);
			token = reader.nextLine();
		}
		JDA api = new JDABuilder(AccountType.BOT).setToken(token).buildBlocking();
		api.addEventListener(new MessageListener());
		api.getPresence().setGame(Game.playing(VERSION));


	}
	//rewriting the message sender
	public static void HsendMessage(MessageChannel channel, String message) 
	{
		channel.sendMessage(message).queue();
	}
	//rewriting the pm sender
	public static void sendPrivateMessage(User user, String content)
	{
		user.openPrivateChannel().queue( (channel) -> channel.sendMessage(content).queue() );
	}


	@Override
	//what to do when the bot sees a message, either on a server or in a pm.
	public void onMessageReceived(MessageReceivedEvent event) {
		//check whether or not the sender is a bot
		boolean bot = event.getAuthor().isBot();
		if (!bot) {


			if (event.isFromType(ChannelType.PRIVATE)) {
				System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(), event.getMessage().getContentDisplay());
				MessageChannel channel = event.getChannel();
				HsendMessage(channel, "hey there " + event.getAuthor());
			}
			else 
			{
				//Help screen, listing all commands and their usages
				if (event.getMessage().getContentRaw().equalsIgnoreCase(".help")) {
					//sendPrivateMessage(event.getAuthor(),"This will probably do something one day.");
					try {
						sendHelp(event.getChannel(),event.getAuthor());
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
				if (event.getMessage().getContentRaw().equalsIgnoreCase(".server")) {
					//no arguments settings
					String ip = "hershcraft.ddns.net";
					int port = 25565;

					String status = "something happened";
					try {
						status = ServerPinger.checkServer(ip,port);
					} catch (IOException e) {
						e.printStackTrace();
					}
					HsendMessage(event.getChannel(),status);
				}

				if (event.getMessage().getContentRaw().contains(".server") && event.getMessage().getContentRaw().length() > 7) {
					String message = event.getMessage().getContentRaw();
					String ip = message.substring(8);
					String portString = ip.substring(ip.indexOf(" ") +1);
					ip = ip.substring(0, ip.indexOf(" "));
					int port = Integer.parseInt(portString);
					try {
						String status = ServerPinger.checkServer(ip,port);
						HsendMessage(event.getChannel(),status);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	private static void sendHelp(MessageChannel channel,User author) throws FileNotFoundException {
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

		//channel.sendMessage(out).queue();

	}


}
