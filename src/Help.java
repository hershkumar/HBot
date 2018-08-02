import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class Help {
	static void sendHelp(MessageChannel channel,User author) throws IOException {
		EmbedBuilder eb = new EmbedBuilder();
		BufferedReader br = new BufferedReader(new FileReader("help.txt"));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			String everything = sb.toString();
			eb.setDescription(everything);
		}
		finally{
			br.close();
		}
		
		channel.sendMessage(eb.build()).queue();
	}



	static void changelog(MessageChannel channel,User author) throws IOException {
		EmbedBuilder eb = new EmbedBuilder();
		BufferedReader br = new BufferedReader(new FileReader("changelog.txt"));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			String everything = sb.toString();
			eb.setDescription(everything);
		}
		finally{
			br.close();
		}
		
		channel.sendMessage(eb.build()).queue();
	}
}
