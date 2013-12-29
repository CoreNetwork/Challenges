package us.corenetwork.challenges;

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
import org.bukkit.scheduler.BukkitRunnable;

import us.corenetwork.challenges.admincommands.AdminHelpCommand;
import us.corenetwork.challenges.admincommands.BaseAdminCommand;
import us.corenetwork.challenges.admincommands.DeleteLevelCommand;
import us.corenetwork.challenges.admincommands.EditLevelCommand;
import us.corenetwork.challenges.admincommands.EditWeekCommand;
import us.corenetwork.challenges.admincommands.ExitCommand;
import us.corenetwork.challenges.admincommands.ListCommand;
import us.corenetwork.challenges.admincommands.ReloadCommand;
import us.corenetwork.challenges.admincommands.ResumeCommand;
import us.corenetwork.challenges.admincommands.SetTimeCommand;
import us.corenetwork.challenges.admincommands.StopCommand;
import us.corenetwork.challenges.modcommands.BaseModCommand;
import us.corenetwork.challenges.modcommands.CompleteCommand;
import us.corenetwork.challenges.modcommands.CompletedListCommand;
import us.corenetwork.challenges.modcommands.DenyCommand;
import us.corenetwork.challenges.modcommands.ExplodeCommand;
import us.corenetwork.challenges.modcommands.LockCommand;
import us.corenetwork.challenges.modcommands.ModHelpCommand;
import us.corenetwork.challenges.modcommands.ModPointsCommand;
import us.corenetwork.challenges.modcommands.TpCommand;
import us.corenetwork.challenges.modcommands.UnclaimCommand;
import us.corenetwork.challenges.usercommands.AllCommand;
import us.corenetwork.challenges.usercommands.BaseUserCommand;
import us.corenetwork.challenges.usercommands.ChCommand;
import us.corenetwork.challenges.usercommands.DoneCommand;
import us.corenetwork.challenges.usercommands.PointsCommand;
import us.corenetwork.challenges.usercommands.StatusCommand;
import us.corenetwork.challenges.usercommands.UserHelpCommand;


public class Challenges extends JavaPlugin {
	public static Logger log = Logger.getLogger("Minecraft");

	private ChallengesListener listener;
	
	public static Challenges instance;

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
		userCommands.put("status", new StatusCommand());

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
		adminCommands.put("reload", new ReloadCommand());
		adminCommands.put("stop", new StopCommand());
		adminCommands.put("resume", new ResumeCommand());

		//Mod Commands
		modCommands.put("help", new ModHelpCommand());
		modCommands.put("list", new CompletedListCommand());
		modCommands.put("tp", new TpCommand());
		modCommands.put("complete", new CompleteCommand());
		modCommands.put("deny", new DenyCommand());
		modCommands.put("lock", new LockCommand());
		modCommands.put("points", new ModPointsCommand());
		modCommands.put("explode", new ExplodeCommand());
		modCommands.put("unclaim", new UnclaimCommand());

		//DEBUG COMMANDS
		
		if (Settings.getBoolean(Setting.DEBUG_MODE))
		{
			adminCommands.put("settime", new SetTimeCommand());
		}
		
		log.info("[Challenges] " + getDescription().getFullName() + " loaded!");
		
		Bukkit.getServer().getScheduler().runTask(Challenges.instance, new WeekAnnouncer());
		
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
		

	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (command.getName().equals("ok"))
		{
			return EditWizard.doneEvent(sender);
		}
		else if (command.getName().equals("ch")) //User command
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
	
	private static class WeekAnnouncer extends BukkitRunnable
	{

		@Override
		public void run() {
			Bukkit.getScheduler().runTaskLater(Challenges.instance, this, getNextTime());
			
			int curWeek = WeekUtil.getCurrentWeek();
			
			if (WeekUtil.getCurrentTime() - WeekUtil.getWeekStart(curWeek) > WeekUtil.SECONDS_PER_WEEK)
			{
				curWeek++;
				Challenges.log.info("[Challenges] New week " + curWeek + "!");
				IO.config.set(Setting.CURRENT_WEEK.getString(), curWeek);
				IO.config.set(Setting.CURRENT_WEEK_START.getString(), Settings.getLong(Setting.CURRENT_WEEK_START) + WeekUtil.SECONDS_PER_WEEK);
				IO.saveConfig();
				
				for (Player p : Bukkit.getServer().getOnlinePlayers())
					Util.Message(Settings.getString(Setting.MESSAGE_NEW_CHALLENGE_ANNOUNCEMENT), p);
						
				try {
					PreparedStatement statement = IO.getConnection().prepareStatement("SELECT WGRegion, WGWorld FROM weekly_completed WHERE WGRegion IS NOT NULL AND WeekID < ?");
					statement.setInt(1, curWeek);
					ResultSet set = statement.executeQuery();
					while (set.next())
					{
						String regionString = set.getString("WGRegion");
						String[] regions = regionString.split(",");
						
						String worldsString = set.getString("WGWorld");
						String[] worlds = worldsString.split(",");

						for (int i = 0; i < regions.length; i++)
						{
							String region = regions[i];
							if (region != null && !region.trim().equals(""))
							{
								String worldName;
								
								worldName = worlds[i];
																
								World world = Bukkit.getServer().getWorld(worldName);
								WorldGuardManager.deleteRegion(world, region);
							}
						}
						
					}
					
					set.close();
					statement.close();
					
					statement = IO.getConnection().prepareStatement("UPDATE weekly_completed SET WGRegion = NULL, WGWorld = NULL WHERE WeekID < ?");
					statement.setInt(1, curWeek);
					statement.executeUpdate();
					
					statement.close();
					IO.getConnection().commit();
				}
				catch (SQLException e) {
		            Challenges.log.log(Level.SEVERE, "[Challenges]: Error while running list command! - " + e.getMessage());
					e.printStackTrace();
				}

			}
		}
		
		private static long getNextTime()
		{
			long timeLeft = WeekUtil.SECONDS_PER_WEEK - (WeekUtil.getCurrentTime() - WeekUtil.getWeekStart(WeekUtil.getCurrentWeek()));
			if (timeLeft < 5)
				return 1;
			else if (timeLeft < 20)
				return 40;
			else if (timeLeft < 120)
				return 200;
			else 
				return 600;
		}
		
	}
}
