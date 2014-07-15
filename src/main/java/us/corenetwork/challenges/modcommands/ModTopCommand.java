package us.corenetwork.challenges.modcommands;

import org.bukkit.command.CommandSender;
import us.corenetwork.challenges.IO;
import us.corenetwork.challenges.Setting;
import us.corenetwork.challenges.Settings;
import us.corenetwork.challenges.Util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class ModTopCommand extends BaseModCommand
{
	public ModTopCommand()
	{
		needPlayer = false;
		desc = "Shows a ranked list of moderators, sorted by amount of edits";
		permission = "top";
	}

	@Override
	public Boolean run(CommandSender sender, String[] args)
	{
		try
		{
			PreparedStatement statement = IO.getConnection().prepareStatement("SELECT COUNT(*) AS edits, moderator FROM weekly_completed GROUP BY moderator ORDER BY edits DESC");
			ResultSet set = statement.executeQuery();
			int rank = 1;

			Util.Message(Settings.getString(Setting.MESSAGE_MOD_TOP_HEADER), sender);

			while (set.next()) {
                UUID player = Util.getUUIDFromString(set.getString("moderator"));
                int edits = set.getInt("edits");

                if (player == null) {
                    continue;
                }

                String entry = Settings.getString(Setting.MESSAGE_MOD_TOP_ENTRY);
                entry = entry.replaceAll("<Rank>", rank + "");
                entry = entry.replaceAll("<Player>", Util.getPlayerNameFromUUID(player));
                entry = entry.replaceAll("<Edits>", edits + "");

                Util.Message(entry, sender);

                rank++;
            }
        }
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return true;
	}
}
