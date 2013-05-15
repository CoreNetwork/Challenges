package com.matejdro.bukkit.mcnsa.challenges.usercommands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.mcnsa.challenges.IO;
import com.matejdro.bukkit.mcnsa.challenges.Setting;
import com.matejdro.bukkit.mcnsa.challenges.Settings;
import com.matejdro.bukkit.mcnsa.challenges.Util;
import com.matejdro.bukkit.mcnsa.challenges.WeekUtil;

public class DoneCommand extends BaseUserCommand {
	public DoneCommand()
	{
		desc = "Submit your completed challenge";
		needPlayer = true;
	}


	public Boolean run(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		Integer level = 0;

		if (args.length < 1 || !Util.isInteger(args[0]))
		{
			try {
				PreparedStatement statement = IO.getConnection().prepareStatement("SELECT Level FROM weekly_levels WHERE Level > (SELECT IFNULL(MAX(Level), 0) FROM weekly_completed WHERE Player = ? AND WeekID = ?) AND WeekID = ? ORDER BY Level ASC LIMIT 1");
				statement.setString(1, player.getName());
				statement.setInt(2, WeekUtil.getCurrentWeek());
				statement.setInt(3, WeekUtil.getCurrentWeek());

				ResultSet set = statement.executeQuery();
				
				if (set.next())
				{
					level = set.getInt("Level");
				}
				else
				{
					Util.Message(Settings.getString(Setting.MESSAGE_CH_ALL_COMPELETED), sender);
					statement.close();
					return true;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			level = Integer.parseInt(args[0]);
		
		
		
		PreparedStatement statement = null;
		ResultSet set = null;
		try
		{
			statement = IO.getConnection().prepareStatement("SELECT weekly_levels.Level, weekly_completed.State FROM weekly_levels LEFT JOIN weekly_completed ON weekly_levels.WeekID = weekly_completed.WeekID AND weekly_levels.Level  <= weekly_completed.level AND weekly_completed.player = ? WHERE weekly_levels.level = ? AND weekly_levels.weekID = ? LIMIT 1");
			statement.setString(1, player.getName());
			statement.setInt(2, level);
			statement.setInt(3, WeekUtil.getCurrentWeek());
			
			set = statement.executeQuery();			
			if (!set.next())
			{
				Util.Message(Settings.getString(Setting.MESSAGE_INVALID_LEVEL).replace("<Level>", Integer.toString(level)), sender);
				return true;
			}
			
			set.getInt("State");
			if (!set.wasNull())
			{
				Util.Message(Settings.getString(Setting.MESSAGE_ALREADY_COMPLETED), sender);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally
		{
			try {
				statement.close();
				set.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		int maxlevel = 0;
		
		try {
			statement = IO.getConnection().prepareStatement("SELECT MAX(Level) AS MaxLevel FROM weekly_levels WHERE WeekID = ? LIMIT 1");
			statement.setInt(1, WeekUtil.getCurrentWeek());
			
			set = statement.executeQuery();
			set.next();
			maxlevel = set.getInt(1);
			
			statement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
						
		if (maxlevel == level)
			Util.Message(Settings.getString(Setting.MESSAGE_CHALLENGE_SENT_MAX), sender);
		else
			Util.Message(Settings.getString(Setting.MESSAGE_CHALLENGE_SENT), sender);
		
		int id = 0;
		try {
			statement = IO.getConnection().prepareStatement("INSERT INTO weekly_completed (WeekID, Level, Player, State, X, Y, Z, World) VALUES (?,?,?,0,?,?,?,?)");
			statement.setInt(1, WeekUtil.getCurrentWeek());
			statement.setInt(2, level);
			statement.setString(3, player.getName());
			statement.setInt(4, player.getLocation().getBlockX());
			statement.setInt(5, player.getLocation().getBlockY());
			statement.setInt(6, player.getLocation().getBlockZ());
			statement.setString(7, player.getLocation().getWorld().getName());

			statement.executeUpdate();
			
			set = statement.getGeneratedKeys();
			set.next();
			id = set.getInt(1);
			
			IO.getConnection().commit();
			statement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String message = Settings.getString(Setting.MESSAGE_MOD_SUBMISSION_NOTICE);
		message = message.replace("<Player>", player.getName());
		message = message.replace("<Level>", Integer.toString(level));
		message = message.replace("<ID>", Integer.toString(id));
		Util.MessagePermissions(message, "mcnsachallenges.notify");
		
		
		
		
		return true;
	}
	
}
