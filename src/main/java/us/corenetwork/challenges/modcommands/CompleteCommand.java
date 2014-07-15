package us.corenetwork.challenges.modcommands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.corenetwork.challenges.*;


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
			id = Challenges.lastTeleport.get(((Player) sender).getName());
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
		try {
            UUID playerUUID = null;
			PreparedStatement statement = IO.getConnection().prepareStatement("SELECT l.WeekID, l.Level, l.Player FROM weekly_completed l INNER JOIN weekly_levels ON weekly_levels.level = l.level AND weekly_levels.weekid = l.weekid WHERE l.ID = ? LIMIT 1");
			statement.setInt(1, id);
			ResultSet set = statement.executeQuery();
			if (set.next())
			{
				week = set.getInt("WeekID");
				level = set.getInt("Level");
				String player = set.getString("Player");
                playerUUID = Util.getUUIDFromString(player);
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

			statement = IO.getConnection().prepareStatement("Select * FROM weekly_levels LEFT JOIN weekly_completed ON weekly_levels.weekID = weekly_completed.weekID AND weekly_levels.level = weekly_completed.level AND weekly_completed.player = ? WHERE weekly_levels.WeekID = ? AND weekly_levels.level <= ? ORDER BY weekly_levels.level DESC");
			statement.setString(1, playerUUID.toString());
			statement.setInt(2, week);
			statement.setInt(3, level);
			set = statement.executeQuery();
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

            if (week != WeekUtil.getCurrentWeek() && sender instanceof Player) {
                // clear WorldEdit selection so it doesn't accidentally get locked.
                WorldEditHandler.clearSelection((Player) sender);
            }

            if (points == 0)
            {
                Util.Message(Settings.getString(Setting.MESSAGE_COMPLETION_DONE_NO_POINTS), sender);
                return true;
            }

            String reason = Settings.getString(Setting.MESSAGE_REASON_COMPLETE);
            reason = reason.replace("<Week>", Integer.toString(week));
            reason = reason.replace("<Level>", Integer.toString(level));

            PlayerPoints.addPoints(playerUUID, points, reason);

			statement = IO.getConnection().prepareStatement("UPDATE weekly_completed SET State = ?, lastUpdate = ?, moderator = ? WHERE player = ? AND level <= ? AND weekID = ? AND state = ?");
			statement.setInt(1, ChallengeState.DONE.code());
			statement.setInt(2, (int) (System.currentTimeMillis() / 1000));
			statement.setString(3, ((Player) sender).getUniqueId().toString());
			statement.setString(4, playerUUID.toString());
			statement.setInt(5, level);
			statement.setInt(6, week);
			statement.setInt(7, ChallengeState.SUBMITTED.code());
			
			statement.executeUpdate();
						
			statement.close();
			
			IO.getConnection().commit();

            String message = Settings.getString(Setting.MESSAGE_COMPLETION_DONE);
            message = message.replace("<Player>", Util.getPlayerNameFromUUID(playerUUID));
            message = message.replace("<Points>", Integer.toString(points));
            message = message.replace("<ID>", Integer.toString(id));
            Util.Message(message, sender);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		return true;
	}

}
