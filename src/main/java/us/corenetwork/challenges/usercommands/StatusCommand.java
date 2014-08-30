package us.corenetwork.challenges.usercommands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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


public class StatusCommand extends BaseUserCommand {
	public StatusCommand()
	{
		desc = "Show status of your challenges";
		needPlayer = true;
		permission = "status";
	}


	public Boolean run(CommandSender sender, String[] args) {	
		Player player = (Player) sender;

		int week;
		if (args.length > 0 && Util.isInteger(args[0])) {
			week = Integer.parseInt(args[0]);
        } else {
            week = WeekUtil.getCurrentWeek();
            try {
                PreparedStatement statement = IO.getConnection().prepareStatement("SELECT WeekID FROM weekly_completed WHERE State = 0 AND Player = ? GROUP BY WeekID ORDER BY WeekID ASC");
                statement.setString(1, player.getUniqueId().toString());
                ResultSet set = statement.executeQuery();

                while(set.next()) {
                    int week2 = set.getInt("WeekID");
                    if (week2 != week) {
                        printWeekStatusReport(sender, player, week2);
                        Util.Message("\n", player);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        printWeekStatusReport(sender, player, week);

		return true;		
	}

    private void printWeekStatusReport(CommandSender sender, Player player, int week) {
        String[] levels = null;
        List<Integer> levelValue = new ArrayList<Integer>(10);

        try {
            PreparedStatement statement = IO.getConnection().prepareStatement("SELECT Points FROM weekly_levels WHERE weekID = ? ORDER BY Level ASC");
            statement.setInt(1, week);
            ResultSet set = statement.executeQuery();

            int size = 0;
            while (set.next())
            {
                size++;
                levelValue.add(set.getInt(1));
            }

            statement.close();

            levels = new String[size];

            if (size > 0)
            {
                statement = IO.getConnection().prepareStatement("SELECT * FROM weekly_completed WHERE weekID = ? AND Player = ? ORDER BY Level ASC");
                statement.setInt(1, week);
                statement.setString(2, player.getUniqueId().toString());

                set = statement.executeQuery();

                while (set.next())
                {
                    int level = set.getInt("level");
                    int state = set.getInt("State");

                    String timeString = null;
                    int time;
                    int timeDiff;


                    if (state > 0)
                    {
                        time = set.getInt("lastUpdate");
                        timeDiff = (int) (System.currentTimeMillis() / 1000 - time);

                        if (timeDiff > 31536000)
                            timeString = "days ago";
                        else
                            timeString = TimePrint.formatSekunde(timeDiff);

                    }

                    for (int i = 0; i < level; i++)
                    {
                        if (levels[i] != null)
                            continue;

                        switch (state)
                        {
                        case 0:
                            levels[i] = Settings.getString(Setting.MESSAGE_STATUS_WAITING_REVIEW);
                            break;
                        case 1:
                            String status = Settings.getString(Setting.MESSAGE_STATUS_APPROVED);
                            status = status.replace("<Time>", timeString);
                            status = status.replace("<Points>", TimePrint.formatPoints(levelValue.get(i)));

                            levels[i] = status;
                            break;
                        case 2:
                        case 3:
                            String comment = set.getString("ModResponse");
                            if (comment == null || comment.trim().length() == 0)
                            {
                                status = Settings.getString(Setting.MESSAGE_STATUS_REJECTED);
                            }
                            else
                            {
                                 status = Settings.getString(Setting.MESSAGE_STATUS_REJECTED_COMMENT);
                                 status = status.replace("<Comment>", comment);
                            }
                            status = status.replace("<Time>", timeString);

                            levels[i] = status;

                            break;
                        }
                    }
                }
            }


            long start = WeekUtil.getWeekStart(week);
            long end = WeekUtil.getWeekStart(week + 1);
            long left = end - WeekUtil.getCurrentTime();

            String header = Settings.getString(end >= WeekUtil.getCurrentTime() ? Setting.MESSAGE_STATUS_HEADER : Setting.MESSAGE_STATUS_HEADER_PAST);

            header = header.replace("<ID>", Integer.toString(week));
            header = header.replace("<From>", TimePrint.formatDate(start));
            header = header.replace("<To>", TimePrint.formatDate(end));
            header = header.replace("<Left>", TimePrint.formatSekunde(left));
            Util.Message(header, sender);


            for (int i = 0; i < levels.length; i++)
            {
                String row = Settings.getString(Setting.MESSAGE_STATUS_ENTRY);

                row = row.replace("<Level>", Integer.toString(i + 1));

                String status = levels[i];
                if (status == null)
                    status = Settings.getString(Setting.MESSAGE_STATUS_NOT_SUBMITTED);

                row = row.replace("<Status>", status);

                Util.Message(row, sender);
            }


        }
        catch (SQLException e) {
            Challenges.log.log(Level.SEVERE, "[Challenges]: Error while running list command! - " + e.getMessage());
            e.printStackTrace();
        }
    }

}
