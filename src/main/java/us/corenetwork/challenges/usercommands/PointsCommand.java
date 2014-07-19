package us.corenetwork.challenges.usercommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.corenetwork.challenges.PlayerPoints;
import us.corenetwork.challenges.PlayerRank;
import us.corenetwork.challenges.Setting;
import us.corenetwork.challenges.Settings;
import us.corenetwork.challenges.Util;


public class PointsCommand extends BaseUserCommand {
	
	public PointsCommand()
	{
		desc = "See your points";
		needPlayer = true;
		permission = "points";
	}


	public Boolean run(CommandSender sender, String[] args) {
		printPoints(sender, sender.getName());

		return true;
	}

	public static void printPoints(CommandSender sender, String player)
	{
		int points = PlayerPoints.getPoints(player);
		PlayerRank curRank = PlayerPoints.getRank(points);
		PlayerRank nextRank = PlayerPoints.getNextRank(curRank);
		String message;
		boolean needPlayer = !sender.getName().equals(player);
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
		message = message.replace("<Player>", player);
		message = message.replace("<Points>", Integer.toString(points));
		message = message.replace("<Rank>", curRank.rank);
		message = message.replace("<PointsPending>", Integer.toString(PlayerPoints.getPending(player)));

		Util.Message(message, sender);
	}
}
