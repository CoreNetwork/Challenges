package us.corenetwork.challenges.modcommands;

import org.bukkit.command.CommandSender;
import us.corenetwork.challenges.IO;
import us.corenetwork.challenges.Util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BlameCommand extends BaseModCommand
{
	public BlameCommand()
	{
		needPlayer = false;
		desc = "Shows the moderator who edited the challenge with the given ID";
		permission = "blame";
	}

	@Override
	public Boolean run(CommandSender sender, String[] args)
	{
		if (args.length > 0)
		{
			int ID = Integer.valueOf(args[0]);
			try
			{
				PreparedStatement statement = IO.getConnection().prepareStatement("SELECT * FROM weekly_completed WHERE ID=?");
				statement.setInt(1, ID);
				ResultSet set = statement.executeQuery();
				set.next();
				Util.Message(HistoryCommand.getChallengeBlame(null, set), sender);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		} else
		{
			Util.Message("No ID given", sender);
		}
		return true;
	}
}
