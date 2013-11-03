package us.corenetwork.challenges.admincommands;

import org.bukkit.command.CommandSender;

import us.corenetwork.challenges.WeekUtil;


public class SetTimeCommand extends BaseAdminCommand {
	
	public SetTimeCommand()
	{
		desc = "[DEBUG] Set current time";
		needPlayer = false;
		permission = "settime";
	}


	public Boolean run(CommandSender sender, String[] args) {
		Integer offset = Integer.parseInt(args[0]);
		if (offset == 0)
			WeekUtil.customTimeOffset = 0;
		else
			WeekUtil.customTimeOffset = (int) (System.currentTimeMillis() / 1000 - offset);
		return true;
	}
	

}
