package us.corenetwork.challenges.modcommands;

import org.bukkit.command.CommandSender;
import us.corenetwork.challenges.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tux on 19.07.14.
 */
public class PrintAllSubmissionsCommand extends BaseModCommand
{

	public PrintAllSubmissionsCommand()
	{
		this.desc = "Prints all submissions from a given level";
		this.permission = "allsubmissions";
		this.needPlayer = false;
	}

	@Override
	public Boolean run(CommandSender sender, String[] args)
	{
		int level = Integer.parseInt(args[0]);
		int week;
		if (args.length > 1) {
			week = Integer.parseInt(args[1]);
		} else {
			week = WeekUtil.getCurrentWeek();
		}
		printAllSubmissions(sender, week, level);
		return true;
	}

	private void printAllSubmissions(CommandSender sender, int week, int level) {
		try
		{
			PreparedStatement stm = IO.getConnection().prepareStatement("SELECT ID FROM weekly_completed WHERE WeekID=? AND Level=? AND State=? ORDER BY lastUpdate ASC");
			stm.setInt(1, week);
			stm.setInt(2, level);
			stm.setInt(3, ChallengeState.DONE.code());
			stm.execute();
			ResultSet set = stm.getResultSet();
			StringBuilder sb = new StringBuilder();
			List<Integer> ids = new ArrayList<Integer>();
			String message;
			while (set.next())
			{
				ids.add(set.getInt("ID"));
			}
			if (ids.size() > 0)
			{
				message = Settings.getString(Setting.MESSAGE_MOD_ALL_SUBMISSIONS);
				sb.append("&7");
				sb.append(ids.get(0));

				for (int i = 1; i < ids.size(); i++)
				{
					sb.append("&6, &7");
					sb.append(ids.get(i));
				}
				message = message.replaceAll("<List>", sb.toString());
			} else
			{
				message = Settings.getString(Setting.MESSAGE_MOD_ALL_SUBMISSIONS_NONE);
			}
			message = message.replace("<Week>", Integer.toString(week));
			message = message.replace("<Level>", Integer.toString(level));
			Util.Message(message, sender);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

	}
}
