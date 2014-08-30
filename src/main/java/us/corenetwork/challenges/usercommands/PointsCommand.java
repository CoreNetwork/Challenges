package us.corenetwork.challenges.usercommands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.corenetwork.challenges.PlayerPoints;
import us.corenetwork.challenges.PlayerRank;
import us.corenetwork.challenges.Setting;
import us.corenetwork.challenges.Settings;
import us.corenetwork.challenges.Util;

import java.util.UUID;


public class PointsCommand extends BaseUserCommand {
	
	public PointsCommand()
	{
		desc = "See your points";
		needPlayer = true;
		permission = "points";
	}


	public Boolean run(CommandSender sender, String[] args) {
		printPoints(sender, ((Player) sender).getUniqueId());

		return true;
	}

	public static void printPoints(CommandSender sender, UUID player)
	{
		int points = PlayerPoints.getPoints(player);
		PlayerRank curRank = PlayerPoints.getRank(points);
		PlayerRank nextRank = PlayerPoints.getNextRank(curRank);
		String message;
		boolean needPlayer = !((Player) sender).getUniqueId().equals(player);
		if (nextRank == null)
		{
			if (needPlayer)
			{
				message = Settings.getString(Setting.MESSAGE_FLATPOINTS_PLAYER);
			} else {
				message = Settings.getString(Setting.MESSAGE_FLATPOINTS);
			}
		}
		else
		{
			if (needPlayer)
			{
				message = Settings.getString(Setting.MESSAGE_FLATPOINTS_NEXT_RANK_PLAYER);
			}
			else
			{
				message = Settings.getString(Setting.MESSAGE_FLATPOINTS_NEXT_RANK);
			}
			message = message.replace("<NewRank>", nextRank.rank);
			message = message.replace("<PointsLeft>", Integer.toString(nextRank.neededPoints - points));
		}
		message = message.replace("<Points>", Integer.toString(points));
		message = message.replace("<Rank>", curRank.rank);

		Util.Message(message, sender);
		
		return true;
	}
}
