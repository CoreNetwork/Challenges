package us.corenetwork.challenges.modcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.corenetwork.challenges.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class UncompleteCommand extends BaseModCommand
{

	public UncompleteCommand()
	{
		desc = "Denies a submission already marked as completed";
		permission = "undo";
		needPlayer = false;
	}

	@Override
	public Boolean run(CommandSender sender, String[] args)
	{
		if (args.length == 0 || !Util.isInteger(args[0]))
		{
			sender.sendMessage("Usage: /chm uncomplete [ID] (Reason)");
			return true;
		} else
		{
			int id = Integer.valueOf(args[0]);
			String reason = "Challenge rejected";
			if (args.length > 1) {
				StringBuilder reasonBuilder = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					reasonBuilder.append(args[i] + " ");
				}
				reason = reasonBuilder.toString().trim();
			}
			try
			{
				PreparedStatement statement = IO.getConnection().prepareStatement("SELECT Level, WeekID, Player FROM weekly_completed WHERE ID=?");
				statement.setInt(1, id);
				ResultSet resultSet = statement.executeQuery();
				int baseLevel = 0;
				int week = 0;
				UUID player = null;
				if (resultSet.next()) {
					baseLevel = resultSet.getInt("Level");
					week = resultSet.getInt("WeekID");
                    player = Util.getUUIDFromString(resultSet.getString("Player"));
                } else {
					sender.sendMessage(Settings.getString(Setting.MESSAGE_MOD_UNDO_NOT_FOUND));
					return true;
				}
				statement = IO.getConnection().prepareStatement("SELECT weekly_levels.Level AS Level, weekly_levels.WeekID AS WeekID, weekly_levels.Points AS Points, weekly_completed.ID AS ID, Player, WGRegion, WGWorld, State " +
						"FROM weekly_completed  " +
						"JOIN weekly_levels ON weekly_levels.WeekID=weekly_completed.WeekID AND weekly_levels.Level=weekly_completed.Level " +
						"WHERE weekly_levels.Level>=? AND weekly_levels.WeekID=? AND weekly_completed.Player=? AND weekly_completed.State=1 " +
						"ORDER BY weekly_levels.Level ASC");
				statement.setInt(1, baseLevel);
				statement.setInt(2, week);
				statement.setString(3, player.toString());
				resultSet = statement.executeQuery();
				int points = 0;
				int levels = 0;
				boolean first = true;
				while (resultSet.next())
				{
					int state = resultSet.getInt("State");
					if (state == 1) {
						points += resultSet.getInt("Points");
						levels ++;
						String region, world;
						region = resultSet.getString("WGRegion");
						world = resultSet.getString("WGWorld");
						String level = resultSet.getString("Level");
						if (region != null) {
							WorldGuardManager.deleteRegion(Bukkit.getWorld(world), region);
						}
						int currentID = resultSet.getInt("ID");
						String message = first ? reason : "Undone because earlier level has been undone";

						Player player1 = Bukkit.getServer().getPlayer(player);
						ChallengeState newState = ChallengeState.REJECTED;
						if (player1 != null)
						{
							newState = ChallengeState.REJECTED_MESSAGE_SENT;
							if (reason == null)
								Util.Message(Settings.getString(Setting.MESSAGE_SUBMISSION_UNDONE).replace("<Level>", level), player1);
							else
								Util.Message(Settings.getString(Setting.MESSAGE_SUBMISSION_UNDONE_MESSAGE).replace("<Message>", message).replace("<Level>", level), player1);
						}
						statement = IO.getConnection().prepareStatement("UPDATE weekly_completed SET State=?, lastUpdate=?, ModResponse=?, moderator=? WHERE ID=?");
						statement.setInt(1, newState.code());
						statement.setInt(2, (int) (System.currentTimeMillis()/1000));
						statement.setString(3, message);
						statement.setInt(5, currentID);
						statement.setString(4, ((Player) sender).getUniqueId().toString());
						statement.execute();
						first = false;
					}
				}

				// unlock area
				PlayerPoints.addPoints(player, -points, reason);

				String summary = Settings.getString(Setting.MESSAGE_MOD_UNDO_SUMMARY);
				summary = summary.replaceAll("<Points>", "" + points);
				summary = summary.replaceAll("<Player>", Util.getPlayerNameFromUUID(player));
				summary = summary.replaceAll("<Levels>", "" + levels);
				sender.sendMessage(summary);
			}
			catch (SQLException e)
			{
				Challenges.log.log(Level.SEVERE, "Could not execute SQL Statement", e);
			}


			return true;
		}
	}
}
