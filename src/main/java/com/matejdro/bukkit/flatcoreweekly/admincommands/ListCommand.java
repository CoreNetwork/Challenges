package com.matejdro.bukkit.flatcoreweekly.admincommands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.flatcoreweekly.FlatcoreWeekly;
import com.matejdro.bukkit.flatcoreweekly.IO;
import com.matejdro.bukkit.flatcoreweekly.Setting;
import com.matejdro.bukkit.flatcoreweekly.Settings;
import com.matejdro.bukkit.flatcoreweekly.TimePrint;
import com.matejdro.bukkit.flatcoreweekly.Util;
import com.matejdro.bukkit.flatcoreweekly.WeekUtil;

public class ListCommand extends BaseAdminCommand {
	public ListCommand()
	{
		desc = "List all challenges or list all levels in a challenge";
		needPlayer = false;
	}


	public Boolean run(CommandSender sender, String[] args) {	
		if (args.length < 1 || !Util.isInteger(args[0]))
		{
			listAll(sender);
			return true;
		}
		else
		{
			int week = Integer.parseInt(args[0]);

			String header = Settings.getString(Setting.MESSAGE_LIST_LEVELS_HEADER);
			header = header.replace("<ID>", Integer.toString(week));
			header = header.replace("<Start>", TimePrint.formatDate(WeekUtil.getWeekStart(week)));
			header = header.replace("<End>", TimePrint.formatDate(WeekUtil.getWeekStart(week + 1)));	
			Util.Message(header, sender);

			listLevels(week, sender);
		}
		return true;
	}
	
	public static void listLevels(int week, CommandSender sender)
	{
		try {
			PreparedStatement statement = IO.getConnection().prepareStatement("SELECT Level, Description, Points FROM weekly_levels WHERE WeekID = ? ORDER BY Level ASC");
			statement.setInt(1, week);
			ResultSet set = statement.executeQuery();
			while (set.next())
			{
				String line = Settings.getString(Setting.MESSAGE_LIST_LEVELS_ENTRY);
				
				line = line.replace("<Level>", Integer.toString(set.getInt("Level")));
				line = line.replace("<Desc>", set.getString("Description"));
				line = line.replace("<Points>", Integer.toString(set.getInt("Points")));				

				Util.Message(line, sender);
			}
			
			set.close();
			statement.close();
		}
		catch (SQLException e) {
            FlatcoreWeekly.log.log(Level.SEVERE, "[FlatcoreWeekly]: Error while running list command! - " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void listAll(CommandSender sender)
	{
		Util.Message(Settings.getString(Setting.MESSAGE_LIST_WEEKS_HEADER), sender);
		try {
			int curWeek = WeekUtil.getCurrentWeek();
			PreparedStatement statement = IO.getConnection().prepareStatement("SELECT WeekID, Count(*) AS Num FROM weekly_levels WHERE WeekID >= ? GROUP BY WeekID ORDER BY Level ASC");
			statement.setInt(1, curWeek - 2);
			ResultSet set = statement.executeQuery();
			while (set.next())
			{
				String line = Settings.getString(Setting.MESSAGE_LIST_WEEKS_ENTRY);
				int weekID = set.getInt("WeekID");
				
				line = line.replace("<ID>", Integer.toString(weekID));
				line = line.replace("<Date>", TimePrint.formatDate(WeekUtil.getWeekStart(weekID)));
				
				String status;
				if (curWeek == weekID)
					status = Settings.getString(Setting.MESSAGE_CHALLENGE_IN_PROGRESS);
				else if (curWeek > weekID)
					status = Settings.getString(Setting.MESSAGE_CHALLENGE_ENDED);
				else
					status = Settings.getString(Setting.MESSAGE_LEVELS_CREATED).replace("<Number>", Integer.toString(set.getInt("Num")));
				line = line.replace("<Status>", status);
				
				Util.Message(line, sender);
			}
			
			set.close();
			statement.close();
		}
		catch (SQLException e) {
            FlatcoreWeekly.log.log(Level.SEVERE, "[FlatcoreWeekly]: Error while running list command! - " + e.getMessage());
			e.printStackTrace();
		}
		
	}
	

}
