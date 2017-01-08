package io.ph.bot;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ph.bot.exception.NoAPIKeyException;
import io.ph.bot.listener.Listeners;
import io.ph.bot.scheduler.JobScheduler;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageList;
import sx.blah.discord.util.MessageList.EfficiencyLevel;

/**
 * Bot singleton instance. The meat of the stuff
 * Fill out the properties file (Bot.properties) to setup
 */
public class Bot {
	public static final String BOT_VERSION = "BOT-BASE";
	private static final Bot instance;
	private static IDiscordClient bot;
	private final Logger logger = LoggerFactory.getLogger(Bot.class);

	private String secret, botOwnerId, avatar;
	private APIKeys apiKeys = new APIKeys();

	private static final boolean DEBUG = false;

	static {
		instance = new Bot();
	}

	void start(String[] args) {
		if(!loadProperties()) {
			logger.error("Error loading properties. Make sure Bot.properties exists");
			System.exit(0);
		}
		bot = getClient(secret);
		MessageList.setEfficiency(bot, EfficiencyLevel.NONE);
		EventDispatcher dispatcher = bot.getDispatcher();
		dispatcher.registerListener(new Listeners());
		JobScheduler.initializeScheduler();
	}

	private IDiscordClient getClient(String token) {
		try {
			return new ClientBuilder().withToken(token).login();
		} catch (DiscordException e) {
			logger.error("Bot could not connect. Is your bot token correct?");
			System.exit(0);
		}
		return null;
	}

	private boolean loadProperties() {
		try {
			PropertiesConfiguration config = new PropertiesConfiguration("resources/Bot.properties");
			secret = config.getString("BotToken");
			avatar = config.getString("Avatar");
			botOwnerId = config.getString("BotOwnerId");

			Configuration subset = config.subset("apikey");
			Iterator<String> iter = subset.getKeys();
			while(iter.hasNext()) {
				String key = iter.next();
				String val = subset.getString(key);
				if(val.length() > 0) {
					this.apiKeys.put(key, val);
					logger.info("Added API key for: {}", key);
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public IDiscordClient getBot() {
		return bot;
	}

	public static Bot getInstance() {
		return instance;
	}

	public Logger getLogger() {
		return logger;
	}

	public String getAvatar() {
		return avatar;
	}

	public String getBotOwnerId() {
		return botOwnerId;
	}

	public APIKeys getApiKeys() {
		return apiKeys;
	}

	public boolean isDebug() {
		return DEBUG;
	}

	public class APIKeys {
		private HashMap<String, String> keys = new HashMap<String, String>();

		/**
		 * Get API key for given key
		 * @param key Key to get
		 * @return String of value, null if not found
		 */
		public String get(String key) throws NoAPIKeyException {
			if(keys.get(key) == null)
				throw new NoAPIKeyException(key);
			return keys.get(key);
		}

		void put(String key, String val) {
			this.keys.put(key, val);
		}
	}
}
