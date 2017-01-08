package io.ph.util;

import java.awt.Color;

import io.ph.bot.Bot;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

public class MessageUtils {
	/**
	 * Send a message to a channel
	 * @param channel Channel target
	 * @param content Content of message
	 * @return 
	 */
	public static void sendMessage(IChannel channel, String content) {
		sendMessage(channel, content, null, false);
	}
	public static void sendMessage(IChannel channel, EmbedObject embed) {
		sendMessage(channel, "", embed, false);
	}

	/**
	 * Send a message to a channel
	 * @param channel Channel target
	 * @param content Content of message
	 * @param embed EmbedObject to format with
	 * @param bypass Bypass the mention formatter
	 */
	private static void sendMessage(IChannel channel, String content, EmbedObject embed, boolean bypass) {
		if(!bypass)
			content = content.replaceAll("@", "\\\\@");
		if(content.equals("") && embed == null)
			return;
		final String s = content;
		RequestBuffer.request(() -> {
			try {
				new MessageBuilder(Bot.getInstance().getBot()).withChannel(channel).withContent(s).withEmbed(embed).build();
			} catch (MissingPermissionsException | DiscordException e) {
				e.printStackTrace();
				return;
			}
		});
	}
	/**
	 * Send a message that shows mentions (for user welcomes)
	 * @param channel Channel to send in
	 * @param content Message to send
	 * @param bypass Bypass to true
	 */
	public static void sendMessage(IChannel channel, String content, boolean bypass) {
		sendMessage(channel, content, null, true);
	}
	/**
	 * Premade template for generic, red colored error messages
	 * @param channel Channel to send to
	 * @param title Title of the embed
	 * @param description Description to be included
	 */
	public static void sendErrorEmbed(IChannel channel, String title, String description) {
		EmbedBuilder em = new EmbedBuilder().withColor(Color.red).withTitle(title).withDesc(description)
				.withTimestamp(System.currentTimeMillis());
		sendMessage(channel, em.build());
	}

	/**
	 * If you need to delete a temporary message and need the return
	 * @param channel Channel to send to
	 * @param embed Embedded object
	 * @return Message if successful, null if not
	 */
	public static IMessage buildAndReturn(IChannel channel, EmbedObject embed) {
		RequestBuffer.request(() -> {
			return new MessageBuilder(Bot.getInstance().getBot()).withChannel(channel).withEmbed(embed);
		});
		return null;

	}
	/**
	 * Send a private message to target user
	 * @param target User to send to
	 * @param content Content of message
	 */
	public static void sendPrivateMessage(IUser target, String content) {
		RequestBuffer.request(() -> {
			try {
				IPrivateChannel privChannel = Bot.getInstance().getBot().getOrCreatePMChannel(target);
				privChannel.sendMessage(content);
			} catch (MissingPermissionsException | DiscordException e) {
				e.printStackTrace();
				return;
			}
		});
	}
	/**
	 * Send a private message to target user with an embed
	 * @param target User to send to
	 * @param embed Embed
	 */
	public static void sendPrivateMessage(IUser target, EmbedObject embed) {
		RequestBuffer.request(() -> {
			try {
				IPrivateChannel privChannel = Bot.getInstance().getBot().getOrCreatePMChannel(target);
				privChannel.sendMessage("", embed, false);
			} catch (MissingPermissionsException | DiscordException e) {
				e.printStackTrace();
				return;
			}
		});
	}
}
