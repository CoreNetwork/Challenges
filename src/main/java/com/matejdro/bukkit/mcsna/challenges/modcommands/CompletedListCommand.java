package com.matejdro.bukkit.mcsna.challenges.modcommands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.mcsna.challenges.MCSNAChallenges;
import com.matejdro.bukkit.mcsna.challenges.IO;
import com.matejdro.bukkit.mcsna.challenges.Setting;
import com.matejdro.bukkit.mcsna.challenges.Settings;
import com.matejdro.bukkit.mcsna.challenges.Util;

public class CompletedListCommand extends BaseModCommand {
	
	public CompletedListCommand()
	{
		desc = "List all completed levels needing inspection";
		needPlayer = false;
	}


	public Boolean run(CommandSender sender, String[] args) {
		int count = 0;
		try {
			PreparedStatement statement = IO.getConnection().prepareStatement("SELECT SUM(count) as count FROM (SELECT COUNT(*) as count FROM weekly_completed WHERE State = 0 GROUP BY Player)");
			ResultSet set = statement.executeQuery();
			set.next();
			count = set.getInt("count");
			set.close();
			statement.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		int maxPage = (int) Math.ceil(count / Settings.getInt(Setting.ITEMS_PER_PAGE));
		int page = 1;
		
		if (args.length > 0 && Util.isInteger(args[0]))
				page = Integer.parseInt(args[0]);
		
		if (page > maxPage)
			page = maxPage;
		
		int start = (page - 1) * (Settings.getInt(Setting.ITEMS_PER_PAGE));
		
		String header = Settings.getString(Setting.MESSAGE_COMPLETED_HEADER);
		
		header = header.replace("<Current>", Integer.toString(page));
		header = header.replace("<Max>", Integer.toString(maxPage));
		Util.Message(header, sender);
		
		try {
			PreparedStatement statement = IO.getConnection().prepareStatement("SELECT Max(ID) as ID,Player,ClaimedBy,Max(Level) As Level FROM weekly_completed WHERE State = 0 GROUP BY Player ORDER BY ID ASC LIMIT ?,?");
			statement.setInt(1, start);
			statement.setInt(2, Settings.getInt(Setting.ITEMS_PER_PAGE));
			
			ResultSet set = statement.executeQuery();
			while (set.next())
			{
				String line = Settings.getString(Setting.MESSAGE_COMPLETED_ENTRY);
				
				line = line.replace("<ID>", Integer.toString(set.getInt("ID")));
				line = line.replace("<Player>", set.getString("Player"));
				line = line.replace("<Level>", Integer.toString(set.getInt("Level")));
				
				String handledBy = set.getString("ClaimedBy");
				if (handledBy != null)
				{
					line = line.replace("<HandledBy>", Settings.getString(Setting.MESSAGE_HANDLED).replace("<Mod>", handledBy));
				}
				else
				{
					line = line.replace("<HandledBy>", "");
				}

				Util.Message(line, sender);
			}
			
			set.close();
			statement.close();
		}
		catch (SQLException e) {
            MCSNAChallenges.log.log(Level.SEVERE, "[FlatcoreWeekly]: Error while running list command! - " + e.getMessage());
			e.printStackTrace();
		}
		Util.Message(Settings.getString(Setting.MESSAGE_COMPLETED_FOOTER), sender);

		return true;
	}
}
