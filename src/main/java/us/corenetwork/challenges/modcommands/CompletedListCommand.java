package us.corenetwork.challenges.modcommands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import us.corenetwork.challenges.*;


public class CompletedListCommand extends BaseModCommand {

	public static final Pattern PAGINATION_PATTERN = Pattern.compile("^\\d+$");
	
	public CompletedListCommand()
	{
		desc = "List all completed levels needing inspection, or all submitted challenge entries per player";
		needPlayer = false;
		permission = "list";
	}


	public Boolean run(CommandSender sender, String[] args) {
		if (args.length == 0 || PAGINATION_PATTERN.matcher(args[0]).matches()) {
			return sendAllSubmitted(sender, args);
		} else if (args.length <= 2) {
			return sendAllPlayer(sender, args);
		}
		return false;
	}

	private Boolean sendAllSubmitted(CommandSender sender, String[] args)
	{
		int count = 0;
		try {
			PreparedStatement statement = IO.getConnection().prepareStatement("SELECT COUNT(count) as count FROM (SELECT COUNT(*) as count FROM weekly_completed WHERE State = 0 GROUP BY Player)");
			ResultSet set = statement.executeQuery();
			set.next();
			count = set.getInt("count");
			set.close();
			statement.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		int maxPage = (int) Math.ceil((double) count / Settings.getInt(Setting.ITEMS_PER_PAGE));
		int page = 1;

		if (args.length > 0 && Util.isInteger(args[0]))
				page = Integer.parseInt(args[0]);

		if (page > maxPage)
			page = maxPage;

		int start = (page - 1) * (Settings.getInt(Setting.ITEMS_PER_PAGE));


		if (count == 0)
		{
			Util.Message(Settings.getString(Setting.MESSAGE_COMPLETED_ALL_DONE), sender);
		}
		else
		{
			String header = Settings.getString(Setting.MESSAGE_COMPLETED_HEADER);

			header = header.replace("<Current>", Integer.toString(page));
			header = header.replace("<Max>", Integer.toString(maxPage));
			Util.Message(header, sender);

			try {
				PreparedStatement statement = IO.getConnection().prepareStatement("SELECT Max(ID) as ID,Player,ClaimedBy,Max(Level) As Level FROM weekly_completed WHERE State = 0 GROUP BY Player ORDER BY Level ASC, ID ASC LIMIT ?,?");
				statement.setInt(1, start);
				statement.setInt(2, Settings.getInt(Setting.ITEMS_PER_PAGE));

				ResultSet set = statement.executeQuery();
				while (set.next())
				{
					String line = Settings.getString(Setting.MESSAGE_COMPLETED_ENTRY);
					
					String playerName = set.getString("Player");
					if (Challenges.instance.getServer().getPlayerExact(playerName) != null) 
						playerName = "&2"+playerName;
					
					line = line.replace("<ID>", Integer.toString(set.getInt("ID")));
					line = line.replace("<Player>", playerName);
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
	            Challenges.log.log(Level.SEVERE, "[Challenges]: Error while running list command! - " + e.getMessage());
				e.printStackTrace();
			}

			String footer;
			if (maxPage > 1)
			{
				footer = Settings.getString(Setting.MESSAGE_COMPLETED_FOOTER_PAGES);
				footer = footer.replace("<Current>", Integer.toString(page));
				footer = footer.replace("<Max>", Integer.toString(maxPage));
			}
			else
			{
				footer = Settings.getString(Setting.MESSAGE_COMPLETED_FOOTER);
			}

			Util.Message(footer, sender);
		}

		return true;
	}

	public Boolean sendAllPlayer(CommandSender sender, String[] args)
	{
		if (args.length == 0) {
			sender.sendMessage("Usage: /chm list <playername>");
			return false;
		} else {
			String player = args[0];
			int week = WeekUtil.getCurrentWeek();
			if (args.length >= 2 && Util.isInteger(args[1])) {
				week = Integer.valueOf(args[1]);
			}
			try
			{
				PreparedStatement statement = IO.getConnection().prepareStatement("SELECT weekly_levels.WeekID, weekly_levels.Level, weekly_completed.ID, weekly_completed.State FROM weekly_levels LEFT JOIN weekly_completed ON weekly_levels.WeekID == weekly_completed.WeekID AND weekly_levels.Level == weekly_completed.Level AND LOWER(weekly_completed.Player) = LOWER(?) WHERE weekly_levels.WeekID=? ORDER BY weekly_levels.Level ASC");
				statement.setString(1, player);
				statement.setInt(2, week);
				ResultSet resultSet = statement.executeQuery();
				boolean first = true;
				StringBuilder week_entries = new StringBuilder();
                String title = Settings.getString(Setting.MESSAGE_MOD_LIST_ENTRIES);
                title = title.replaceAll("<Player>", player);

                sender.sendMessage(title);
				while(resultSet.next()) {
					if (!first) {
						week_entries.append(ChatColor.GRAY + ", ");
					}
					first = false;
					String entry = Settings.getString(Setting.MESSAGE_MOD_LIST_ENTRIES_ENTRY);
					ChallengeState state = ChallengeState.getByCode(resultSet.getInt("State"));

					String ID = resultSet.getString("ID");
					if (ID == null)
					{
						ID = "-";
						state = ChallengeState.NOT_SUBMITTED;
					}
					String level = resultSet.getString("Level");
					if (level == null) {
						level = "-";
					}

					entry = entry.replaceAll("<Color>", state.color().toString());
					entry = entry.replaceAll("<ID>", ID);
					entry = entry.replaceAll("<Level>", level);
					week_entries.append(entry);
				}
				if (first) {
					sender.sendMessage(Settings.getString(Setting.MESSAGE_NO_CHALLENGES_THAT_WEEK));
				} else {
					String week_line = Settings.getString(Setting.MESSAGE_MOD_LIST_ENTRIES_WEEK);
					week_line = week_line.replaceAll("<Week>", ""+ week);
					week_line = week_line.replaceAll("<Entries>", week_entries.toString());
					sender.sendMessage(week_line);
				}
				return true;
			}
			catch (SQLException e)
			{
				Challenges.log.log(Level.SEVERE, "Error while executing sql command", e);
				return false;
			}
		}
	}
}
