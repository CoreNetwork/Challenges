package us.corenetwork.challenges.usercommands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;

import us.corenetwork.challenges.IO;
import us.corenetwork.challenges.Challenges;
import us.corenetwork.challenges.Setting;
import us.corenetwork.challenges.Settings;
//import us.corenetwork.challenges.TimePrint;
import us.corenetwork.challenges.Util;

//import us.corenetwork.challenges.WeekUtil;

public class TopCommand extends BaseUserCommand {
	public TopCommand() {
		desc = "Show all this week's challenges";
		needPlayer = false;
		permission = "top";
	}

	public Boolean run(CommandSender sender, String[] args) {

		try {
			PreparedStatement statement = IO
					.getConnection()
					.prepareStatement(
							"SELECT player_points.Player, Points, SUM(IFNULL(Amount,0)) AS PendingPoints FROM player_points LEFT JOIN point_changes on player_points.player = point_changes.player group by player_points.player order by points desc, player_points.player");
			ResultSet set = statement.executeQuery();
			int page = 1;
			int records = 0;
			if (args.length > 0) {
				try {
					page = Integer.parseInt(args[0]);
				} catch (Exception ex) {
					Util.Message("Invalid parameter " + ex.getMessage(), sender);
					return false;
				}
			}
			while (records < (page - 1) * 10) {
				set.next();
				records++;
			}

			Util.Message(Settings.getString(Setting.MESSAGE_TOP_HEADER), sender);

			while (set.next() && records < (page) * 10) {
				records++;
				String player = set.getString("Player") + "                   ";
				String point = set.getString("Points") + "                   ";
				String pendingpoint = set.getString("PendingPoints");
				String line = player.substring(0, 20) + point.substring(0, 8)
						+ pendingpoint;

				Util.Message(line, sender);
			}
			while (set.next()) {
				records++;
			}
			int pages = (int) Math.ceil(records / 10.0);
			if (pages > 1) {
				Util.Message("Page " + page + "/" + pages, sender);
			}
			set.close();
			statement.close();
		} catch (SQLException e) {
			Challenges.log.log(
					Level.SEVERE,
					"[Challenges]: Error while running top command! - "
							+ e.getMessage());
			e.printStackTrace();
		}

		return true;
	}

}
