package com.matejdro.bukkit.mcsna.challenges.admincommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.mcsna.challenges.EditWizard;
import com.matejdro.bukkit.mcsna.challenges.Setting;
import com.matejdro.bukkit.mcsna.challenges.Settings;
import com.matejdro.bukkit.mcsna.challenges.TimePrint;
import com.matejdro.bukkit.mcsna.challenges.Util;
import com.matejdro.bukkit.mcsna.challenges.WeekUtil;

public class EditWeekCommand extends BaseAdminCommand {
	public EditWeekCommand()
	{
		desc = "Edit/Create challenge";
		needPlayer = true;
	}


	public Boolean run(CommandSender sender, String[] args) {
		int week = 1;
		if (args.length < 1 || !Util.isInteger(args[0]))
			week = WeekUtil.getCurrentWeek() + 1;
		else
			week = WeekUtil.getCurrentWeek();
		
//		if (week <= WeekUtil.getCurrentWeek())
//		{
//			Util.Message(Settings.getString(Setting.MESSAGE_FUTURE_ONLY), sender);
//			return true;
//		}
		
		String header = Settings.getString(Setting.MESSAGE_CREATE_COMMAND_RESPONSE);
		header = header.replace("<ID>", Integer.toString(week));
		header = header.replace("<Start>", TimePrint.formatDate(WeekUtil.getWeekStart(week)));
		header = header.replace("<End>", TimePrint.formatDate(WeekUtil.getWeekStart(week + 1)));	
		Util.Message(header, sender);
		ListCommand.listLevels(week, sender);
		
		EditWizard.startWeek(((Player) sender).getName(), week);
		
		Util.Message(Settings.getString(Setting.MESSAGE_CREATE_COMMAND_INSTRUCTIONS), sender);
		return true;
	}
	
}
