import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import query.*;
public class MessageListener extends ListenerAdapter{
	String defaultServer = null;
	int defaultPort = 0;
	final static String VERSION = "1.0.2";
	public static void main(String[] args ) throws LoginException, InterruptedException, FileNotFoundException {

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

				String message = event.getMessage().getContentRaw();
				MessageChannel channel = event.getTextChannel();
				if (event.getMessage().getContentRaw().contains(".server") && event.getMessage().getContentRaw().length() > 7) {
					try {
						String ip = message.substring(8);
						String portString = ip.substring(ip.indexOf(" ") +1);
						ip = ip.substring(0, ip.indexOf(" "));
						int port = Integer.parseInt(portString);
						System.out.println("trying server " + ip +" on port " + port);
						String status = ServerPinger.checkServer(ip,port);
						HsendMessage(event.getChannel(),status);
					}
					catch (IOException e) {
						System.out.println("Stop trying to mess with me >-:(");
					}
				}
				//Help screen, listing all commands and their usages
				if (event.getMessage().getContentRaw().equalsIgnoreCase(".help")) {
					//sendPrivateMessage(event.getAuthor(),"This will probably do something one day.");
					try {
						sendHelp(event.getChannel(),event.getAuthor());
					} catch (FileNotFoundException e) {
						System.out.println("You dun goofed");
					}
				}
				if (message.length() >= 17 && message.subSequence(0, 17).equals(".setDefaultServer")) {
					String msg = event.getMessage().getContentRaw();
					msg = msg.substring(18);
					String ip = msg;
					defaultServer = ip;
					System.out.println("Default server set to " + defaultServer);
				}
				if (message.length() >= 15 && message.subSequence(0,15).equals(".setDefaultPort")) {
					String port = message.substring(16);
					defaultPort = Integer.parseInt(port);
					System.out.println("Default port set to " + defaultPort);
				}


				if (event.getMessage().getContentRaw().equalsIgnoreCase(".server")) {

					//no arguments settings
					String ip = defaultServer;
					int port = defaultPort;

					String status = "something happened";
					try {
						status = ServerPinger.checkServer(ip,port);
					} catch (IOException e) {
						HsendMessage(channel,"Server Port Out of Bounds");
					}
					HsendMessage(event.getChannel(),status);
				}
				
				if (message.length() >= 12 && message.subSequence(0, 11).equals(".getPlayers")) {
					String msg = message.substring(12);
					String ip = msg.substring(0,msg.indexOf(" "));
					String portString = msg.substring(msg.indexOf(" ") + 1);
					int port = Integer.parseInt(portString);
					int numPlayers = getPlayers(ip,port);
					String players = Integer.toString(numPlayers);
					HsendMessage(channel, players);
					HsendMessage(channel, getNames(ip,port));
				} 
				if(message.equals(".getPlayers")) {
					String ip = defaultServer;
					int port = defaultPort;
					int numPlayers = getPlayers(ip,port);
					String players = Integer.toString(numPlayers);
					HsendMessage(channel, players);
					HsendMessage(channel, getNames(ip,port));
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
	}

	private int getPlayers(String ip,int port) {
		MCQuery query = new MCQuery(ip,port);
		QueryResponse resp = query.basicStat();
		int players = resp.getOnlinePlayers();
		System.out.println("giving player list");
		return players;
	}
	private String getNames(String ip, int port) {
		MCQuery query = new MCQuery(ip,port);
		QueryResponse resp = query.fullStat();
		List players = resp.getPlayerList();
		String ret = "";
		for (int i= 0; i < players.size(); i++) {
			ret += players.get(i) + ", ";
		}
		System.out.println("giving player name list");
		return ret;
	}

}
