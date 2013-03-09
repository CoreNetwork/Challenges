package com.matejdro.bukkit.flatcoreweekly.modcommands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.flatcoreweekly.IO;
import com.matejdro.bukkit.flatcoreweekly.PlayerPoints;
import com.matejdro.bukkit.flatcoreweekly.Setting;
import com.matejdro.bukkit.flatcoreweekly.Settings;
import com.matejdro.bukkit.flatcoreweekly.Util;
import com.matejdro.bukkit.flatcoreweekly.WeekUtil;
import com.matejdro.bukkit.flatcoreweekly.WorldEditHandler;
import com.matejdro.bukkit.flatcoreweekly.WorldGuardManager;

public class PointsCommand extends BaseModCommand {
	
	public PointsCommand()
	{
		desc = "Manage player points";
		needPlayer = false;
	}


	public Boolean run(CommandSender sender, String[] args) {
		if (args.length < 1 || (args.length > 1 && !Util.isInteger(args[1])))
		{
			Util.Message("Usage: /chm points [Name] (Change) (Reason)", sender);
			return true;
		}
		
		if (args.length < 2)
			checkPoints(sender, args);
		else
			modifyPoints(sender, args);
		
		return true;
	}
	
	private void checkPoints(CommandSender sender, String[] args)
	{
		int points = PlayerPoints.getPoints(args[0]);
		
		String message = Settings.getString(Setting.MESSAGE_PLAYER_POINTS);
		message = message.replace("<Player>", args[0]);
		message = message.replace("<Points>", Integer.toString(points));
		
		Util.Message(message, sender);
	}
	
	private void modifyPoints(CommandSender sender, String[] args)
	{
		int curPoints = PlayerPoints.getPoints(args[0]);
		int change = Integer.parseInt(args[1]);
		
		String message = Settings.getString(Setting.MESSAGE_PLAYER_POINTS_ALTERED);
		message = message.replace("<Player>", args[0]);
		message = message.replace("<Change>", Integer.toString(change));
		Util.Message(message, sender);
		
		PlayerPoints.addPoints(args[0], change, args.length > 2 ? args[2] : null);

	}

}
