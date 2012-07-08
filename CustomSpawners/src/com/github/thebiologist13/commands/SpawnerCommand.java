package com.github.thebiologist13.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface SpawnerCommand {
	
	/*
	 * Note:
	 * 
	 * Sub-commands extending this class do not need
	 * to check for the length of the argument (at 
	 * least for command validity, they do if it can
	 * be used in multiple ways). They only need to
	 * check if valid values were put in, check for
	 * thrown exceptions, check permissions, and
	 * carry out the command.
	 */
	
	//Strings for errors
	final String NO_CONSOLE = ChatColor.RED + "This command can only be used in-game";
	final String NO_ID = ChatColor.RED + "Spawner ID does not exist.";
	final String ID_NOT_NUMBER = ChatColor.RED + "IDs must be a number.";
	final String LESS_ARGS = ChatColor.RED + "Not enough arguments.";
	final String MORE_ARGS = ChatColor.RED + "Too many arguments.";
	final String INVALID_ENTITY = ChatColor.RED + "Can not parse entity type. Using default entity from conifg.";
	final String NOT_ALLOWED_ENTITY = ChatColor.RED + "This is not an allowed entity. Using default entity from config.";
	final String INVALID_BLOCK = ChatColor.RED + "You must be looking a a block.";
	final String NEEDS_SELECTION = ChatColor.RED + "To use this command without defining an ID, you must have a spawner selected.";
	final String MUST_BE_BOOLEAN = ChatColor.RED + "You must enter a boolean value (true or false).";
	final String INVALID_VALUES = ChatColor.RED + "You have entered invalid values for this command. It may be too big/small or be negative.";
	final String SPECIFY_NUMBER = ChatColor.RED + "You must specify a number for this command.";
	final String GENERAL_ERROR = ChatColor.RED + "An error has occured with this command. Did you type everything right?";
	final String NO_PERMISSION = ChatColor.RED + "You do not have permission!";
	
	public void run(CommandSender arg0, Command arg1, String arg2, String[] arg3);
}