package com.matejdro.bukkit.mcsna.challenges;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.matejdro.bukkit.mcsna.challenges.admincommands.AdminHelpCommand;
import com.matejdro.bukkit.mcsna.challenges.admincommands.BaseAdminCommand;
import com.matejdro.bukkit.mcsna.challenges.admincommands.DeleteLevelCommand;
import com.matejdro.bukkit.mcsna.challenges.admincommands.EditLevelCommand;
import com.matejdro.bukkit.mcsna.challenges.admincommands.EditWeekCommand;
import com.matejdro.bukkit.mcsna.challenges.admincommands.ExitCommand;
import com.matejdro.bukkit.mcsna.challenges.admincommands.GetWeekCommand;
import com.matejdro.bukkit.mcsna.challenges.admincommands.ImportCommand;
import com.matejdro.bukkit.mcsna.challenges.admincommands.ListCommand;
import com.matejdro.bukkit.mcsna.challenges.admincommands.ReloadCommand;
import com.matejdro.bukkit.mcsna.challenges.admincommands.SetTimeCommand;
import com.matejdro.bukkit.mcsna.challenges.modcommands.BaseModCommand;
import com.matejdro.bukkit.mcsna.challenges.modcommands.CompleteCommand;
import com.matejdro.bukkit.mcsna.challenges.modcommands.CompletedListCommand;
import com.matejdro.bukkit.mcsna.challenges.modcommands.DenyCommand;
import com.matejdro.bukkit.mcsna.challenges.modcommands.LockCommand;
import com.matejdro.bukkit.mcsna.challenges.modcommands.ModHelpCommand;
import com.matejdro.bukkit.mcsna.challenges.modcommands.ModPointsCommand;
import com.matejdro.bukkit.mcsna.challenges.modcommands.TpCommand;
import com.matejdro.bukkit.mcsna.challenges.usercommands.AllCommand;
import com.matejdro.bukkit.mcsna.challenges.usercommands.BaseUserCommand;
import com.matejdro.bukkit.mcsna.challenges.usercommands.ChCommand;
import com.matejdro.bukkit.mcsna.challenges.usercommands.DoneCommand;
import com.matejdro.bukkit.mcsna.challenges.usercommands.PointsCommand;
import com.matejdro.bukkit.mcsna.challenges.usercommands.UserHelpCommand;

public class MCSNAChallenges extends JavaPlugin {
	public static Logger log = Logger.getLogger("Minecraft");

	private ChallengesListener listener;
	
	public static MCSNAChallenges instance;

	public static Plugin permissions = null;
	
	public static HashMap<String, BaseModCommand> modCommands = new HashMap<String, BaseModCommand>();
	public static HashMap<String, BaseAdminCommand> adminCommands = new HashMap<String, BaseAdminCommand>();
	public static  HashMap<String, BaseUserCommand> userCommands = new HashMap<String, BaseUserCommand>();
	public static BaseUserCommand chCommand = new ChCommand();
	
	public static HashMap<String, Integer> lastTeleport = new HashMap<String, Integer>();
		
	@Override
	public void onDisable() {
		IO.freeConnection();
	}

	@Override
	public void onEnable() {
		instance = this;
		listener = new ChallengesListener();

		IO.LoadSettings();
		IO.PrepareDB();

		getServer().getPluginManager().registerEvents(listener, this);
		
		//Init timers
		//getServer().getScheduler().scheduleSyncRepeatingTask(this, new TimeEvent(), 20, 20);

		//User commands
		userCommands.put("help", new UserHelpCommand());
		userCommands.put("all", new AllCommand());
		userCommands.put("done", new DoneCommand());
		userCommands.put("points", new PointsCommand());
		
		//Admin commands
		adminCommands.put("help", new AdminHelpCommand());
		adminCommands.put("list", new ListCommand());
		adminCommands.put("edit", new EditWeekCommand());
		adminCommands.put("create", new EditWeekCommand());
		adminCommands.put("editlevel", new EditLevelCommand());
		adminCommands.put("createlevel", new EditLevelCommand());
		adminCommands.put("deletelevel", new DeleteLevelCommand());
		adminCommands.put("exit", new ExitCommand());
		adminCommands.put("save", new ExitCommand());
		adminCommands.put("import", new ImportCommand());
		adminCommands.put("reload", new ReloadCommand());

		//Mod Commands
		modCommands.put("help", new ModHelpCommand());
		modCommands.put("list", new CompletedListCommand());
		modCommands.put("tp", new TpCommand());
		modCommands.put("complete", new CompleteCommand());
		modCommands.put("deny", new DenyCommand());
		modCommands.put("lock", new LockCommand());
		modCommands.put("points", new ModPointsCommand());

		//DEBUG COMMANDS
		
		if (Settings.getBoolean(Setting.DEBUG_MODE))
		{
			adminCommands.put("settime", new SetTimeCommand());
			adminCommands.put("getweek", new GetWeekCommand());
		}

		log.info("[MCSNAChallenges] " + getDescription().getFullName() + " loaded!");
		
		oldWeek = WeekUtil.getCurrentWeek();
		scheduleTimer();
		
		try
		{
			PreparedStatement statement2 = IO.getConnection().prepareStatement("UPDATE weekly_completed SET ClaimedBy=NULL");
			statement2.executeUpdate();
			statement2.close();
			
			IO.getConnection().commit();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void scheduleTimer()
	{
		Bukkit.getServer().getScheduler().cancelTasks(MCSNAChallenges.instance);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(MCSNAChallenges.instance, new WeekAnnouncer(), WeekAnnouncer.getNextTime());
	}
	

	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (command.getName().equals("ch")) //User command
		{
			if (args.length < 1 || Util.isInteger(args[0]))
				return chCommand.execute(sender, args);
			
			BaseUserCommand cmd = userCommands.get(args[0]);
			if (cmd != null)
				return cmd.execute(sender, args);
			else
				return userCommands.get("help").execute(sender, args);

		}
		else if (command.getName().equals("chm")) //Mod command
		{
			if (args.length < 1 || Util.isInteger(args[0]))
				return modCommands.get("help").execute(sender, args);
			
			BaseModCommand cmd = modCommands.get(args[0]);
			if (cmd != null)
				return cmd.execute(sender, args);
			else
				return modCommands.get("help").execute(sender, args);
				
		}
		else if (command.getName().equals("cha")) //Admin command
		{
			if (args.length < 1 || Util.isInteger(args[0]))
				return adminCommands.get("help").execute(sender, args);
			
			BaseAdminCommand cmd = adminCommands.get(args[0]);
			if (cmd != null)
				return cmd.execute(sender, args);
			else
				return adminCommands.get("help").execute(sender, args);
				
		}
		else //flatpoints command
		{
			BaseUserCommand cmd = userCommands.get("points");
			return cmd.execute(sender, args);
		}
	}
	
	private static int oldWeek;
	private static class WeekAnnouncer implements Runnable
	{

		@Override
		public void run() {
			if (WeekUtil.getCurrentWeek() > oldWeek)
			{
				oldWeek = WeekUtil.getCurrentWeek();

				for (Player p : Bukkit.getServer().getOnlinePlayers())
					Util.Message(Settings.getString(Setting.MESSAGE_NEW_CHALLENGE_ANNOUNCEMENT), p);
						
				try {
					PreparedStatement statement = IO.getConnection().prepareStatement("SELECT World, WGRegion FROM weekly_completed WHERE WGRegion IS NOT NULL AND WeekID < ?");
					statement.setInt(1, oldWeek);
					ResultSet set = statement.executeQuery();
					while (set.next())
					{
						String region = set.getString("WGRegion");
						if (region != null && !region.trim().equals(""))
						{
							World world = Bukkit.getServer().getWorld(set.getString("World"));
							WorldGuardManager.deleteRegion(world, region);
						}
					}
					
					set.close();
					statement.close();
					
					statement = IO.getConnection().prepareStatement("UPDATE weekly_completed SET WGRegion = NULL WHERE WeekID < ?");
					statement.setInt(1, oldWeek);
					statement.executeUpdate();
					
					statement.close();
					IO.getConnection().commit();
				}
				catch (SQLException e) {
		            MCSNAChallenges.log.log(Level.SEVERE, "[FlatcoreWeekly]: Error while running list command! - " + e.getMessage());
					e.printStackTrace();
				}

			}
			
			scheduleTimer();
			
		}
		
		private static long getNextTime()
		{
			long timeLeft = WeekUtil.getWeekStart(oldWeek + 1) - WeekUtil.getCurrentTime();
			if (timeLeft < 5)
				return 0;
			else if (timeLeft < 120)
				return 200;
			else if (timeLeft < 600)
				return 1200;
			else if (timeLeft < 3600)
				return 12000;
			else if (timeLeft < 7200)
				return 36000;
			else if (timeLeft < 18000)
				return 72000;
			else if (timeLeft < 36000)
				return 216000;
			else
				return 360000;
		}
		
	}
}
