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
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import query.*;
public class MessageListener extends ListenerAdapter{
	String defaultServer = null;
	int defaultPort = 0;
	static TextChannel defaultChannel;
	String prefix = ".";
	static long general = 472193024844365826l;
	final static String VERSION = "1.0.3";
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
		defaultChannel = api.getTextChannelById(general);
		api.getPresence().setGame(Game.playing(VERSION));
		HsendMessage((MessageChannel) defaultChannel,"HBot v"+VERSION+" starting up");


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
				if (event.getMessage().getContentRaw().contains(prefix +"server") && event.getMessage().getContentRaw().length() > 7) {
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
				if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix +"help")) {
					try {
						Help.sendHelp(event.getChannel(),event.getAuthor());
					} catch (IOException e) {
						System.out.println("You dun goofed");
					}
				}

				if (message.length() >= 17 && message.subSequence(0, 17).equals(prefix +"setDefaultServer")) {
					String msg = event.getMessage().getContentRaw();
					msg = msg.substring(18);
					String ip = msg;
					defaultServer = ip;
					System.out.println("Default server set to " + defaultServer);
				}

				if (message.length() >= 15 && message.subSequence(0,15).equals(prefix +"setDefaultPort")) {
					String port = message.substring(16);
					defaultPort = Integer.parseInt(port);
					System.out.println("Default port set to " + defaultPort);
				}


				if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "server")) {

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

				if (message.length() >= 12 && message.subSequence(0, 11).equals(prefix + "getPlayers")) {
					String msg = message.substring(12);
					String ip = msg.substring(0,msg.indexOf(" "));
					String portString = msg.substring(msg.indexOf(" ") + 1);
					int port = Integer.parseInt(portString);
					int numPlayers = getPlayers(ip,port);
					String players = Integer.toString(numPlayers);
					HsendMessage(channel, players);
					HsendMessage(channel, getNames(ip,port));
				} 
				if(message.equals(prefix + "getPlayers")) {
					String ip = defaultServer;
					int port = defaultPort;
					int numPlayers = getPlayers(ip,port);
					String players = Integer.toString(numPlayers);
					HsendMessage(channel, players);
					HsendMessage(channel, getNames(ip,port));
				}

				if (message.length() >= 11 &&message.subSequence(prefix.length()-1, 10).equals(prefix + "setPrefix")) {
					String prefixNew = message.substring(10 + prefix.length());

					if (prefixNew.length()==1) {
						prefix = prefixNew;
						System.out.println("set prefix to "+ prefix);
					}
				}

				if (message.equals("prefix?")) {
					HsendMessage(channel,prefix);
				}
			}
		}
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
