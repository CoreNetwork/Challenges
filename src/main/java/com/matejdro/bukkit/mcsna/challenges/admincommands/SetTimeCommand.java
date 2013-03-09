package com.matejdro.bukkit.mcsna.challenges.admincommands;

import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.mcsna.challenges.MCSNAChallenges;
import com.matejdro.bukkit.mcsna.challenges.WeekUtil;

public class SetTimeCommand extends BaseAdminCommand {
	
	public SetTimeCommand()
	{
		desc = "[DEBUG] Set current time";
		needPlayer = false;
	}


	public Boolean run(CommandSender sender, String[] args) {
		Integer offset = Integer.parseInt(args[0]);
		if (offset == 0)
			WeekUtil.customTimeOffset = 0;
		else
			WeekUtil.customTimeOffset = (int) (System.currentTimeMillis() / 1000 - offset);
		MCSNAChallenges.scheduleTimer();
		return true;
	}
	

}
