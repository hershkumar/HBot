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

public class MessageListener extends ListenerAdapter{
	public static void main(String[] args ) throws LoginException, InterruptedException, FileNotFoundException {
		final String VERSION = "1.0.0";
		File config = new File("./config.txt");
		Scanner reader = new Scanner(config);
		String token = reader.nextLine();
		JDA api = new JDABuilder(AccountType.BOT).setToken(token).buildBlocking();
		api.addEventListener(new MessageListener());
		api.getPresence().setGame(Game.playing(VERSION));
	}
	//rewriting the message sender
	public void sendMessage(MessageChannel channel, String message) 
	{
		channel.sendMessage(message).queue();
	}
	//rewriting the pm sender
	public void sendPrivateMessage(User user, String content)
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
				sendMessage(channel, "hey there " + event.getAuthor());
			}
			else 
			{
				//Help screen, listing all commands and their usages
				if (event.getMessage().getContentRaw().equalsIgnoreCase(".help")) {
					sendPrivateMessage(event.getAuthor(),"This will probably do something one day.");
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
					sendMessage(event.getChannel(),status);
				}

				if (event.getMessage().getContentRaw().contains(".server") && event.getMessage().getContentRaw().length() > 7) {
					String message = event.getMessage().getContentRaw();
					String ip = message.substring(8);
					String portString = ip.substring(ip.indexOf(" ") +1);
					ip = ip.substring(0, ip.indexOf(" "));
					int port = Integer.parseInt(portString);
					try {
						String status = ServerPinger.checkServer(ip,port);
						sendMessage(event.getChannel(),status);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}


}
