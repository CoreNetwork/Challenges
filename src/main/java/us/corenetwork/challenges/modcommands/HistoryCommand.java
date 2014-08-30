package us.corenetwork.challenges.modcommands;

import org.bukkit.command.CommandSender;
import us.corenetwork.challenges.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class HistoryCommand extends BaseModCommand
{
	public HistoryCommand()
	{
		needPlayer = false;
		desc = "Shows a list of recent edits (either global or for a specific moderator, if given)";
		permission = "history";
	}

	@Override
	public Boolean run(CommandSender sender, String[] args)
	{
		int page = 1;
		UUID player = null;

		// init args
		if (args.length > 0)
		{
			if (RegexUtils.PAGINATION_PATTERN.matcher(args[0]).matches())
			{
				page = Integer.valueOf(args[0]);
			} else {
                player = Util.getPlayerUUIDFromName(args[0]);
            }
        }
		if (args.length > 1)
		{
			if (RegexUtils.PAGINATION_PATTERN.matcher(args[1]).matches())
			{
				page = Integer.valueOf(args[1]);
			}
		}
		int start = (page - 1) * (Settings.getInt(Setting.ITEMS_PER_PAGE));

        String playerName = Util.getPlayerNameFromUUID(player);

		try
		{
			PreparedStatement statement = null;
			if (player == null)
			{
				statement = IO.getConnection().prepareStatement("SELECT * FROM weekly_completed WHERE moderator IS NOT NULL ORDER BY lastUpdate DESC LIMIT ?,?");
				statement.setInt(1, start);
				statement.setInt(2, Settings.getInt(Setting.ITEMS_PER_PAGE));
			} else {
				statement = IO.getConnection().prepareStatement("SELECT * FROM weekly_completed WHERE moderator = ? ORDER BY lastUpdate DESC LIMIT ?,?");
				statement.setString(1, player.toString());
				statement.setInt(2, start);
				statement.setInt(3, Settings.getInt(Setting.ITEMS_PER_PAGE));
			}

			ResultSet set = statement.executeQuery();

			String header = Settings.getString(player == null ? Setting.MESSAGE_MOD_HISTORY_HEADER : Setting.MESSAGE_MOD_HISTORY_HEADER_PLAYER);
			if (player != null)
			{
				header = header.replaceAll("<Player>", playerName);
			}
			Util.Message(header, sender);

			while (set.next()) {
				String entry = getChallengeBlame(player, set);

				Util.Message(entry, sender);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return true;
	}

	public static String getChallengeBlame(UUID player, ResultSet set) throws SQLException
	{
		int ID = set.getInt("ID");
		ChallengeState state = ChallengeState.getByCode(set.getInt("State"));
        UUID moderator = Util.getUUIDFromString(set.getString("moderator"));

		if (moderator == null) {
            moderator = Util.getUUIDFromString(set.getString("ClaimedBy"));
        }
        if (moderator == null) {
			moderator = null;
		}
		int lastUpdate = set.getInt("lastUpdate");

        String modName = Util.getPlayerNameFromUUID(moderator);

		String time = TimePrint.formatSekunde(System.currentTimeMillis() / 1000 - lastUpdate);
        int level = set.getInt("Level");

		String entry = Settings.getString(player == null ? Setting.MESSAGE_MOD_HISTORY_ENTRY_PLAYER : Setting.MESSAGE_MOD_HISTORY_ENTRY);
		entry = entry.replaceAll("<ID>", ID + "");
		entry = entry.replaceAll("<moderator>", modName);
		entry = entry.replaceAll("<State>", state.getPrint());
		entry = entry.replaceAll("<time>", time);
        entry = entry.replaceAll("<Level>", level + "");
		return entry;
	}
}
