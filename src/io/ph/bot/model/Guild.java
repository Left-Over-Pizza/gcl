package io.ph.bot.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import io.ph.bot.Bot;
import sx.blah.discord.handle.obj.IGuild;

public class Guild {
	public static Map<String, Guild> guildMap = new HashMap<String, Guild>();
	private PropertiesConfiguration config;
	private ServerConfiguration guildConfig;

	/**
	 * Initialize the Guild object and add it to the hashmap
	 * Note: No checks to see if the guild is already added, so initializing
	 * a guild again will overwrite
	 * @param g Guild to initialize
	 */
	public Guild(IGuild g) {
		try {
			// Read data from this file
			this.config = new PropertiesConfiguration("resources/guilds/" + g.getID() + "/GuildProperties.properties");
			this.config.setAutoSave(true);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		this.guildConfig = new ServerConfiguration(config.getString("ServerCommandPrefix"), config.getBoolean("FirstTime"));
		Guild.guildMap.put(g.getID(), this);
		Bot.getInstance().getLogger().info("Guild {} initialized - {}", g.getID(), g.getName());
	}

	public Configuration getPropertyConfig() {
		return config;
	}

	public ServerConfiguration getGuildConfig() {
		return guildConfig;
	}

	public class ServerConfiguration {
		private String commandPrefix;
		private boolean firstTime;

		ServerConfiguration(String commandPrefix, boolean firstTime) {
			this.commandPrefix = commandPrefix;
			this.firstTime = firstTime;
		}

		public String getCommandPrefix() {
			return commandPrefix;
		}
		public void setCommandPrefix(String commandPrefix) {
			this.commandPrefix = commandPrefix;
			config.setProperty("ServerCommandPrefix", commandPrefix);
		}
		public boolean isFirstTime() {
			return firstTime;
		}
		public void setFirstTime(boolean firstTime) {
			this.firstTime = firstTime;
			config.setProperty("FirstTime", firstTime);
		}
	}

}
