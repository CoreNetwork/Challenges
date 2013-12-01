package us.corenetwork.challenges.modcommands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;

import us.corenetwork.challenges.IO;
import us.corenetwork.challenges.Challenges;
import us.corenetwork.challenges.Setting;
import us.corenetwork.challenges.Settings;
import us.corenetwork.challenges.Util;


public class CompletedListCommand extends BaseModCommand {
	
	public CompletedListCommand()
	{
		desc = "List all completed levels needing inspection";
		needPlayer = false;
		permission = "list";
	}


	public Boolean run(CommandSender sender, String[] args) {		
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
				PreparedStatement statement = IO.getConnection().prepareStatement("SELECT Max(ID) as ID,Player,ClaimedBy,Max(Level) As Level FROM weekly_completed WHERE State = 0 GROUP BY Player ORDER BY ID ASC LIMIT ?,?");
				statement.setInt(1, start);
				statement.setInt(2, Settings.getInt(Setting.ITEMS_PER_PAGE));
				
				ResultSet set = statement.executeQuery();
				while (set.next())
				{
					String line = Settings.getString(Setting.MESSAGE_COMPLETED_ENTRY);
					
					line = line.replace("<ID>", Integer.toString(set.getInt("ID")));
					line = line.replace("<Player>", set.getString("Player"));
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
}
