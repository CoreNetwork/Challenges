package us.corenetwork.challenges.usercommands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;

import us.corenetwork.challenges.Challenges;
import us.corenetwork.challenges.IO;
import us.corenetwork.challenges.Setting;
import us.corenetwork.challenges.Settings;
import us.corenetwork.challenges.Util;


public class TopCommand extends BaseUserCommand {
	
	int page;
	int pagesAll;
	int perPageLimit;
	int offset;
	int maxPointLength;
	
	public TopCommand()
	{
		desc = "Show top scores.";
		needPlayer = false;
		permission = "top";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		
		page = 1;
		maxPointLength = 0;
		if (args.length > 0)
		{
			if (Util.isInteger(args[0]))
				page = Integer.parseInt(args[0]);
			else
			{
				Util.Message("Usage: /ch top [page_number]", sender);
				return true;
			}
		}
		perPageLimit = Settings.getInt(Setting.TOP_PER_PAGE);
		
		try {
			PreparedStatement countStatement = IO.getConnection().prepareStatement("SELECT COUNT(*) AS CountAll FROM player_points");
			ResultSet countSet = countStatement.executeQuery();
			int countAll = countSet.getInt("CountAll");
			pagesAll = (int)Math.ceil((double)countAll/perPageLimit);
			
			if (page > pagesAll)
				page = pagesAll;
			if (page < 1)
				page = 1;
			
			offset = (page-1)*perPageLimit;
			
			PreparedStatement statement = IO.getConnection().prepareStatement(
					  "SELECT player_points.Player, Points + SUM(IFNULL(Amount,0)) AS Points "
					+ "FROM player_points LEFT JOIN point_changes on player_points.player = point_changes.player "
					+ "GROUP BY player_points.player "
					+ "ORDER BY points desc, player_points.player "
					+ "LIMIT ?, ?");
			statement.setInt(1, offset);
			statement.setInt(2, perPageLimit);
			
			ResultSet set = statement.executeQuery();

			displayHeader(sender);
			int counter = 1;
			while (set.next())
			{
				String place = getPlaceString(counter++);
				String points = getPointsString(set.getString("Points"));
				String name = getNameString(Util.getPlayerNameFromUUID(Util.getUUIDFromString(set.getString("Player"))));
				
				String line = place + points + name;
				Util.Message(line, sender);
			}
			
			if (pagesAll > 1)
			{
				Util.Message(Settings.getString(Setting.TOP_FOOTNOTE_COLOR) + "Page " + page + "/" + pagesAll, sender);
			}
			
			set.close();
			statement.close();
		}
		catch (SQLException e) {
            Challenges.log.log(Level.SEVERE, "[Challenges]: Error while running top command! - " + e.getMessage());
			e.printStackTrace();
		}	
		
		return true;		
	}
	
	private void displayHeader(CommandSender sender)
	{
		String header = Settings.getString(Setting.TOP_PLACE_COLUMN_HEADER) + " " 
				+ Settings.getString(Setting.TOP_POINTS_COLUMN_HEADER) + " "
				+ Settings.getString(Setting.TOP_NAME_COLUMN_HEADER);
		Util.Message(header, sender);
	}
	
	private String getPlaceString(int counter)
	{
		int place = offset + counter;
		String color = Settings.getString(Setting.TOP_PLACE_COLUMN_COLOR);
		String zeroColor = Settings.getString(Setting.TOP_PLACE_COLUMN_LEADING_ZERO_COLOR);
		String placeString = padString(color+String.valueOf(place), String.valueOf(offset + perPageLimit).length() + color.length(), zeroColor+ "0"); 
		placeString = Settings.getString(Setting.TOP_PLACE_COLUMN_DISPLAY).replace("<Place>", placeString);
		return color + placeString + ". ";
	}
	
	private String getPointsString(String points)
	{
		if(points.length() > maxPointLength)
			maxPointLength = points.length();
		String color = Settings.getString(Setting.TOP_POINTS_COLUMN_COLOR);
		String zeroColor = Settings.getString(Setting.TOP_POINTS_COLUMN_LEADING_ZERO_COLOR);
		String pointsString = padString(color + points, maxPointLength + color.length(), zeroColor + "0");
		pointsString = Settings.getString(Setting.TOP_POINTS_COLUMN_DISPLAY).replace("<Points>", pointsString);
		return color + pointsString + " ";
	}
	
	private String getNameString(String name)
	{
		String nameString = Settings.getString(Setting.TOP_NAME_COLUMN_DISPLAY).replace("<Player>", name);
		return Settings.getString(Setting.TOP_NAME_COLUMN_COLOR) + nameString + " ";
	}
	
	
	private String padString(String string, int length, String padding)
	{
		int currentLength = string.length();
		for(int i = 0;i<length-currentLength;i++)
			string = padding + string;
		return string;
	}
}
