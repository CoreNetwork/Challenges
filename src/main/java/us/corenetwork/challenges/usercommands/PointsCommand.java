package us.corenetwork.challenges.usercommands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.corenetwork.challenges.*;

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
		Message message;
		boolean needPlayer = !((Player) sender).getUniqueId().equals(player);
		if (nextRank == null)
		{
			if (needPlayer)
			{
				message = Message.from(Setting.MESSAGE_FLATPOINTS_PLAYER);
			} else {
				message = Message.from(Setting.MESSAGE_FLATPOINTS);
			}
		}
		else
		{
			if (needPlayer)
			{
				message = Message.from(Setting.MESSAGE_FLATPOINTS_NEXT_RANK_PLAYER);
			}
			else
			{
				message = Message.from(Setting.MESSAGE_FLATPOINTS_NEXT_RANK);
			}
			message.variable("NewRank", nextRank.rank);
			message.variable("PointsLeft", nextRank.neededPoints - points);
		}
		message.variable("Player", Util.getPlayerNameFromUUID(player));
		message.variable("PointsPending", PlayerPoints.getPending(player));
		message.variable("Points", points);
		message.variable("Rank", curRank.rank);

		message.send(sender);
	}
}
