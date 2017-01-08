package io.ph.bot.listener;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import io.ph.bot.Bot;
import io.ph.bot.State;
import io.ph.bot.commands.CommandHandler;
import io.ph.bot.model.Guild;
import io.ph.bot.procedural.ProceduralListener;
import io.ph.bot.scheduler.JobScheduler;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;

public class Listeners {
	@EventSubscriber
	public void onReadyEvent(ReadyEvent e) {
		JobScheduler.initializeEventSchedule();
		State.changeBotAvatar(new File("resources/avatar/" + Bot.getInstance().getAvatar()));
		Bot.getInstance().getLogger().info("Bot is now online");
	}

	@EventSubscriber
	public void onMessageRecievedEvent(MessageReceivedEvent e) {
		if(e.getMessage().getChannel().isPrivate()) {
			// TODO: Private message
			return;
		}
		// Check if this is a command
		Guild g = Guild.guildMap.get(e.getMessage().getGuild().getID());
		if(e.getMessage().getContent().startsWith(g.getGuildConfig().getCommandPrefix())) {
			CommandHandler.processCommand(e.getMessage());
			return;
		}
		// Procedural command
		ProceduralListener.getInstance().update(e.getMessage());
	}

	/**
	 * Guild create event fires when a guild connects or the bot joins the guild
	 * @param e The event
	 */
	@EventSubscriber
	public void onGuildCreateEvent(GuildCreateEvent e) {
		File f;
		if(!(f = new File("resources/guilds/" + e.getGuild().getID() +"/")).exists()) {
			try {
				FileUtils.forceMkdir(f);
				FileUtils.copyFile(new File("resources/guilds/template.properties"), 
						new File("resources/guilds/" + e.getGuild().getID()+"/GuildProperties.properties"));
				FileUtils.copyFile(new File("resources/guilds/template.db"), 
						new File("resources/guilds/" + e.getGuild().getID()+"/Data.db"));
				Bot.getInstance().getLogger().info("Guild has joined: {}", e.getGuild().getName());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		Guild g = new Guild(e.getGuild());
		if(g.getGuildConfig().isFirstTime()) {
			// TODO: First time a guild is introduced to the bot
			Guild.guildMap.get(e.getGuild().getID()).getGuildConfig().setFirstTime(false);
		}
	}

	/**
	 * When a guild leaves, destroy its reference in the guildmap and delete its directory
	 * TODO: Make your own decisions on longevity of a guild's settings
	 * @param e The event
	 */
	@EventSubscriber
	public void onGuildLeaveEvent(GuildLeaveEvent e) {
		try {
			FileUtils.deleteDirectory(new File("resources/guilds/" + e.getGuild().getID() + "/"));
			Bot.getInstance().getLogger().info("Guild has left: {}", e.getGuild().getName());
			Guild.guildMap.remove(e.getGuild().getID());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	/**
	 * When a new user joins a guild
	 * TODO: Implement this
	 * @param e The event
	 */
	@EventSubscriber
	public void onUserJoinListener(UserJoinEvent e) {

	}

	/**
	 * When a user leaves a guild
	 * TODO: Implement this
	 * @param e The event
	 */
	@EventSubscriber
	public void onUserLeaveListener(UserLeaveEvent e) {

	}
}
