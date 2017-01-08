package io.ph.bot;

import io.ph.bot.commands.CommandHandler;

/**
 * Main entry point
 * @author Paul
 */
public class Launcher {
	public static void main(String[] args) {
		CommandHandler.initCommands();
		Bot.getInstance().start(args);
	}
}
