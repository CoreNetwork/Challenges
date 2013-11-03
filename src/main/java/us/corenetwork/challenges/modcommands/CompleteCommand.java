package us.corenetwork.challenges.modcommands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.corenetwork.challenges.IO;
import us.corenetwork.challenges.MCNSAChallenges;
import us.corenetwork.challenges.PlayerPoints;
import us.corenetwork.challenges.Setting;
import us.corenetwork.challenges.Settings;
import us.corenetwork.challenges.Util;


public class CompleteCommand extends BaseModCommand {
	
	public CompleteCommand()
	{
		desc = "Mark level as completed and award points";
		needPlayer = true;
		permission = "complete";
	}


	public Boolean run(CommandSender sender, String[] args) {
		Integer id = null;
		if (args.length < 1 || !Util.isInteger(args[0]))
		{
			id = MCNSAChallenges.lastTeleport.get(((Player) sender).getName());
			if (id == null)
			{
				Util.Message("Usage: /chm complete [ID]", sender);
				return true;
			}
			else
			{
				Util.Message(Settings.getString(Setting.MESSAGE_USING_PREVIOUS_ID).replace("<ID>", Integer.toString(id)), sender);
			}
			
		}
		else
			id = Integer.parseInt(args[0]);
		
		
		int points = 0;
		int level = 0;
		int week = 0;
		String player = "";
		try {
			PreparedStatement statement = IO.getConnection().prepareStatement("SELECT l.WeekID, l.Level, l.Player FROM weekly_completed l INNER JOIN weekly_levels ON weekly_levels.level = l.level AND weekly_levels.weekid = l.weekid WHERE l.ID = ? LIMIT 1");
			statement.setInt(1, id);
			ResultSet set = statement.executeQuery();
			if (set.next())
			{
				week = set.getInt("WeekID");
				level = set.getInt("Level");
				player = set.getString("Player");
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
		
		try {
			PreparedStatement statement = IO.getConnection().prepareStatement("Select * FROM weekly_levels LEFT JOIN weekly_completed ON weekly_levels.weekID = weekly_completed.weekID AND weekly_levels.level = weekly_completed.level AND weekly_completed.player = ? WHERE weekly_levels.WeekID = ? AND weekly_levels.level <= ? ORDER BY weekly_levels.level DESC");
			statement.setString(1, player);
			statement.setInt(2, week);
			statement.setInt(3, level);
			ResultSet set = statement.executeQuery();
			while (set.next())
			{
				int state = set.getInt("State");
				if (set.wasNull() || state == 0)
				{
					points += set.getInt("Points");
				}
				else
					break;
					
			}
			
			set.close();
			statement.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		String reason = Settings.getString(Setting.MESSAGE_REASON_COMPLETE);
		reason = reason.replace("<Week>", Integer.toString(week));
		reason = reason.replace("<Level>", Integer.toString(level));
		
		PlayerPoints.addPoints(player, points, reason);
		
		try {
			PreparedStatement statement = IO.getConnection().prepareStatement("UPDATE weekly_completed SET State = 1, lastUpdate = ? WHERE player = ? AND level <= ? AND weekID = ? AND state = 0");
			statement.setInt(1, (int) (System.currentTimeMillis() / 1000));
			statement.setString(2, player);
			statement.setInt(3, level);
			statement.setInt(4, week);
			
			statement.executeUpdate();
						
			statement.close();
			
			IO.getConnection().commit();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		String message = Settings.getString(Setting.MESSAGE_COMPLETION_DONE);
		message = message.replace("<Player>", player);
		message = message.replace("<Points>", Integer.toString(points));
		message = message.replace("<ID>", Integer.toString(id));
		Util.Message(message, sender);
		
		return true;
	}

}
