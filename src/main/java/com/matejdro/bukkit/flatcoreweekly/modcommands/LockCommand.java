package com.matejdro.bukkit.flatcoreweekly.modcommands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.flatcoreweekly.FlatcoreWeekly;
import com.matejdro.bukkit.flatcoreweekly.IO;
import com.matejdro.bukkit.flatcoreweekly.Setting;
import com.matejdro.bukkit.flatcoreweekly.Settings;
import com.matejdro.bukkit.flatcoreweekly.Util;
import com.matejdro.bukkit.flatcoreweekly.WeekUtil;
import com.matejdro.bukkit.flatcoreweekly.WorldEditHandler;
import com.matejdro.bukkit.flatcoreweekly.WorldGuardManager;

public class LockCommand extends BaseModCommand {
	
	public LockCommand()
	{
		desc = "Lock region to prevent editing";
		needPlayer = true;
	}


	public Boolean run(CommandSender sender, String[] args) {
		Integer id = null;
		if (args.length < 1 || !Util.isInteger(args[0]))
		{
			id = FlatcoreWeekly.lastTeleport.get(((Player) sender).getName());
			if (id == null)
			{
				Util.Message("Usage: /chm lock [ID]", sender);
				return true;
			}
			else
			{
				Util.Message(Settings.getString(Setting.MESSAGE_USING_PREVIOUS_ID).replace("<ID>", Integer.toString(id)), sender);
			}
			
		}
		else
			id = Integer.parseInt(args[0]);
		
		boolean autoExpand = false;
		if (args.length > 1 || (args.length > 0 &&!Util.isInteger(args[0])))
			autoExpand = true;
		
		Player player = (Player) sender;
		
		String author = null;
		int week = 0;
		int level = 0;
		try {
			PreparedStatement statement = IO.getConnection().prepareStatement("SELECT Player,State, WeekID,Level FROM weekly_completed WHERE ID = ? LIMIT 1");
			statement.setInt(1, id);
			ResultSet set = statement.executeQuery();
			if (set.next())
			{
				author = set.getString("Player");
				level = set.getInt("Level");
				int state = set.getInt("State");
				week = set.getInt("WeekID");
				
				if (state != 1)
				{
					
					Util.Message(Settings.getString(Setting.MESSAGE_LOCK_ONLY_APPROVED).replace("<ID>", Integer.toString(id)), sender);
					set.close();
					statement.close();
					return true;
				}
				
				if (week != WeekUtil.getCurrentWeek())
				{
					Util.Message(Settings.getString(Setting.MESSAGE_LOCK_FUTURE_ONLY), sender);
					set.close();
					statement.close();
					return true;
				}
				
			}
			else
			{
				Util.Message(Settings.getString(Setting.MESSAGE_INVALID_ID), sender);
				set.close();
				statement.close();
				return true;
			}
			
			set.close();
			statement.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		Block[] points = WorldEditHandler.getWorldEditRegion(player, autoExpand);
		if (points == null)
			return true;
		
		String regionName = "w" + week + "t" + level + "-" + author;
		WorldGuardManager.createRegion(points[0], points[1], regionName);
		try
		{
			PreparedStatement statement = IO.getConnection().prepareStatement("UPDATE weekly_completed SET WGRegion = ? WHERE ID = ?");
			statement.setString(1, regionName);
			statement.setInt(2, id);
			statement.executeUpdate();
			statement.close();
			
			IO.getConnection().commit();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		Util.Message(Settings.getString(Setting.MESSAGE_LOCKED), sender);
		return true;
	}

}
