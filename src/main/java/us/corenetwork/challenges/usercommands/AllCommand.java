package us.corenetwork.challenges.usercommands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.corenetwork.challenges.*;


public class AllCommand extends BaseUserCommand {
	public AllCommand()
	{
		desc = "Show all this week's challenges";
		needPlayer = true;
		permission = "all";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		int curWeek = WeekUtil.getCurrentWeek();
		String header = Settings.getString(Setting.MESSAGE_CH_HEADER);
		header = header.replace("<ID>", Integer.toString(curWeek));
		
		long start = WeekUtil.getWeekStart(curWeek);
		long end = WeekUtil.getWeekStart(curWeek + 1);
		header = header.replace("<From>", TimePrint.formatDate(start));
		header = header.replace("<To>", TimePrint.formatDate(end));
		header = header.replace("<Left>", TimePrint.formatSekunde(end - WeekUtil.getCurrentTime()));
		Util.Message(header, sender);
		
		try {
			PreparedStatement statement = IO.getConnection().prepareStatement("SELECT *, IFNULL((Select State FROM weekly_completed WHERE weekly_completed.WeekID = l.WeekID AND weekly_completed.Level >= l.level AND weekly_completed.player = ? ORDER BY Level ASC LIMIT 1), -1) AS Status FROM weekly_levels l WHERE weekID = ? ORDER BY level");
			statement.setString(1, ((Player)sender).getUniqueId().toString());
			statement.setInt(2, curWeek);
			ResultSet set = statement.executeQuery();
			while (set.next())
			{
				String line = Settings.getString(Setting.MESSAGE_CH_ENTRY);
				
				line = line.replace("<Level>", Integer.toString(set.getInt("Level")));
				line = line.replace("<Desc>", set.getString("Description"));
				
				String status;
				ChallengeState state = ChallengeState.getByCode(set.getInt("Status"));
				if (state == ChallengeState.DONE)
					status = Settings.getString(Setting.MESSAGE_COMPLETED);
				else if (state == ChallengeState.SUBMITTED)
					status = Settings.getString(Setting.MESSAGE_WAITING_INSPECTION);
				else
				{
					int points = set.getInt("Points");
					if (points == 1)
						status = Settings.getString(Setting.MESSAGE_POINT).replace("<Points>", Integer.toString(points));
					else
						status = Settings.getString(Setting.MESSAGE_POINTS).replace("<Points>", Integer.toString(points));
						
				}
				line = line.replace("<Status>", status);
				
				Util.Message(line, sender);
			}
			
			set.close();
			statement.close();
		}
		catch (SQLException e) {
            Challenges.log.log(Level.SEVERE, "[Challenges]: Error while running list command! - " + e.getMessage());
			e.printStackTrace();
		}
		
		Util.Message(Settings.getString(Setting.MESSAGE_CH_ALL_FOOTER), sender);
		return true;		
	}
	
}
