package com.matejdro.bukkit.mcnsa.challenges.usercommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.mcnsa.challenges.PlayerPoints;
import com.matejdro.bukkit.mcnsa.challenges.PlayerRank;
import com.matejdro.bukkit.mcnsa.challenges.Setting;
import com.matejdro.bukkit.mcnsa.challenges.Settings;
import com.matejdro.bukkit.mcnsa.challenges.Util;

public class PointsCommand extends BaseUserCommand {
	
	public PointsCommand()
	{
		desc = "See your points";
		needPlayer = true;
		permission = "points";
	}


	public Boolean run(CommandSender sender, String[] args) {
		int points = PlayerPoints.getPoints(((Player) sender).getName());
		PlayerRank curRank = PlayerPoints.getRank(points);
		PlayerRank nextRank = PlayerPoints.getNextRank(curRank);
		String message;
		if (nextRank == null)
			message = Settings.getString(Setting.MESSAGE_FLATPOINTS);
		else
		{
			message = Settings.getString(Setting.MESSAGE_FLATPOINTS_NEXT_RANK);
			message = message.replace("<NewRank>", nextRank.rank);
			message = message.replace("<PointsLeft>", Integer.toString(nextRank.neededPoints - points));
		}
		message = message.replace("<Points>", Integer.toString(points));
		message = message.replace("<Rank>", curRank.rank);

		Util.Message(message, sender);
		
		return true;
	}
}
