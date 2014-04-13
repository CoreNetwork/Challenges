package us.corenetwork.challenges.usercommands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.corenetwork.challenges.IO;
import us.corenetwork.challenges.Challenges;
import us.corenetwork.challenges.Setting;
import us.corenetwork.challenges.Settings;
import us.corenetwork.challenges.TimePrint;
import us.corenetwork.challenges.Util;
import us.corenetwork.challenges.WeekUtil;


public class ChCommand extends BaseUserCommand {
	public ChCommand()
	{
		desc = "Show your next challenge";
		needPlayer = true;
		permission = "ch";
	}


	public Boolean run(CommandSender sender, String[] args) {
		
		if (args.length > 0 && Util.isInteger(args[0]))
		{
			printOneChallenge(sender, args);
			return true;
		}
		
		int curWeek = WeekUtil.getCurrentWeek();
		String header = Settings.getString(Setting.MESSAGE_CH_HEADER);
		header = header.replace("<ID>", Integer.toString(curWeek));
		
		long start = WeekUtil.getWeekStart(curWeek);
		long end = start + WeekUtil.SECONDS_PER_WEEK - 1;
		header = header.replace("<From>", TimePrint.formatDate(start));
		header = header.replace("<To>", TimePrint.formatDate(end));
		header = header.replace("<Left>", TimePrint.formatSekunde(end - WeekUtil.getCurrentTime()));
		Util.Message(header, sender);
		
		try {
			PreparedStatement statement = IO.getConnection().prepareStatement("SELECT Level, Description, Points FROM weekly_levels WHERE Level > (SELECT IFNULL(MAX(Level), 0) FROM weekly_completed WHERE Player = ? AND WeekID = ? AND State < 2) AND WeekID = ? ORDER BY Level ASC LIMIT 1");
			statement.setString(1, ((Player)sender).getName());
			statement.setInt(2, curWeek);
			statement.setInt(3, curWeek);

			ResultSet set = statement.executeQuery();
			if (set.next())
			{
				String line = Settings.getString(Setting.MESSAGE_CH_ENTRY);
				
				line = line.replace("<Level>", Integer.toString(set.getInt("Level")));
				line = line.replace("<Desc>", set.getString("Description"));
				int points = set.getInt("Points");
				if (points == 1)
					line = line.replace("<Status>", Settings.getString(Setting.MESSAGE_POINT).replace("<Points>", Integer.toString(points)));
				else
					line = line.replace("<Status>", Settings.getString(Setting.MESSAGE_POINTS).replace("<Points>", Integer.toString(points)));

				line = line.replace("<Status>", Settings.getString(Setting.MESSAGE_POINTS).replace("<Points>", Integer.toString(set.getInt("Points"))));
				
				Util.Message(line, sender);
				Util.Message(Settings.getString(Setting.MESSAGE_CH_FOOTER), sender);
			}
			else
			{
				Util.Message(Settings.getString(Setting.MESSAGE_CH_ALL_COMPELETED), sender);
			}
			
			set.close();
			statement.close();
		}
		catch (SQLException e) {
            Challenges.log.log(Level.SEVERE, "[Challenges]: Error while running list command! - " + e.getMessage());
			e.printStackTrace();
		}
		
		return true;		
	}
	
	public void printOneChallenge(CommandSender sender, String[] args)
	{
		int level = Integer.parseInt(args[0]);
		int curWeek = WeekUtil.getCurrentWeek();
		String header = Settings.getString(Setting.MESSAGE_CH_HEADER);
		header = header.replace("<ID>", Integer.toString(curWeek));
		
		long start = WeekUtil.getWeekStart(curWeek);
		long end = start + WeekUtil.SECONDS_PER_WEEK - 1;
		header = header.replace("<From>", TimePrint.formatDate(start));
		header = header.replace("<To>", TimePrint.formatDate(end));
		header = header.replace("<Left>", TimePrint.formatSekunde(end - WeekUtil.getCurrentTime()));
		Util.Message(header, sender);
		
		try {
			PreparedStatement statement = IO.getConnection().prepareStatement("SELECT *, IFNULL((Select State FROM weekly_completed WHERE weekly_completed.WeekID = l.WeekID AND weekly_completed.Level >= l.level AND weekly_completed.player = ? ORDER BY Level ASC LIMIT 1), -1) AS Status FROM weekly_levels l WHERE weekID = ? AND level = ?");
			statement.setString(1, ((Player)sender).getName());	
			statement.setInt(2, curWeek);
			statement.setInt(3, level);
			ResultSet set = statement.executeQuery();
			if (set.next())
			{
				String line = Settings.getString(Setting.MESSAGE_CH_ENTRY);
				
				
				line = line.replace("<Level>", Integer.toString(set.getInt("Level")));
				line = line.replace("<Desc>", set.getString("Description"));
				
				boolean completed = true;
				String status;
				int state = set.getInt("Status");
				if (state == 1)
					status = Settings.getString(Setting.MESSAGE_COMPLETED);
				else if (state == 0)
					status = Settings.getString(Setting.MESSAGE_WAITING_INSPECTION);
				else
				{
					int points = set.getInt("Points");
					if (points == 1)
						status = Settings.getString(Setting.MESSAGE_POINT).replace("<Points>", Integer.toString(points));
					else
						status = Settings.getString(Setting.MESSAGE_POINTS).replace("<Points>", Integer.toString(points));
					completed = false;
				}
				line = line.replace("<Status>", status);
								
				Util.Message(line, sender);
				
				if (!completed)
					Util.Message(Settings.getString(Setting.MESSAGE_CH_NUM_FOOTER).replace("<Number>", Integer.toString(level)), sender);

				
			}
			else
			{
				Util.Message(Settings.getString(Setting.MESSAGE_INVALID_LEVEL).replace("<Level>", Integer.toString(level)), sender);

			}
			
			set.close();
			statement.close();
		}
		catch (SQLException e) {
            Challenges.log.log(Level.SEVERE, "[Challenges]: Error while running list command! - " + e.getMessage());
			e.printStackTrace();
		}
		
		return;
	}
	
}
