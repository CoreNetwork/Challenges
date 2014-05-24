package us.corenetwork.challenges.admincommands;

import org.bukkit.command.CommandSender;

import org.bukkit.configuration.file.YamlConfiguration;
import us.corenetwork.challenges.*;


public class ResumeCommand extends BaseAdminCommand {
	
	public ResumeCommand()
	{
		desc = "Stop challenge submissions";
		needPlayer = false;
		permission = "resume";
	}


	public Boolean run(CommandSender sender, String[] args) {
		int curWeek = WeekUtil.getCurrentWeek();
		int week = curWeek;
		
		if (args.length > 0 && Util.isInteger(args[0]))
			week = Integer.parseInt(args[0]);

		YamlConfiguration config = SettingType.STORAGE.getConfig();
		config.set(Setting.STOPPED.getString(), false);
		
		if (curWeek != week)
		{
			config.set(Setting.CURRENT_WEEK.getString(), week);
		}
		
		IO.saveConfig();
		
		Util.Message(Settings.getString(Setting.MESSAGE_RESUMED_ADMIN).replace("<Level>", Integer.toString(week)), sender);
		return true;
	}
	

}
