package us.corenetwork.challenges;

import java.util.logging.Level;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.CuboidRegionSelector;
import com.sk89q.worldedit.regions.RegionSelector;

public class WorldEditHandler {
	 public static Block[] getWorldEditRegion(Player bplayer, boolean autoExpand)
	    {
	    	Plugin plugin = Challenges.instance.getServer().getPluginManager().getPlugin("WorldEdit");
			if (plugin != null)
			{
				WorldEditPlugin we = (WorldEditPlugin) plugin;
				BukkitPlayer player = new BukkitPlayer(we, we.getServerInterface(), bplayer);
				LocalSession session = we.getWorldEdit().getSessionManager().get(player);
			
				RegionSelector selector = session.getRegionSelector(player.getWorld());
				if (!(selector instanceof CuboidRegionSelector))
				{
					Util.Message("You must select cuboid region!", bplayer);
					return null;
				}
				CuboidRegionSelector cuboidSelector = (CuboidRegionSelector) selector;
		
				try {
					CuboidRegion region = (CuboidRegion) cuboidSelector.getRegion();
					
					if (autoExpand)
					{
						//region.expand(new Vector(0, 1, 0));
						region.expand(new Vector(0, -1, 0));
						region.expand(new Vector(1, 0, 0));
						region.expand(new Vector(-1, 0, 0));
						region.expand(new Vector(0, 0, 1));
						region.expand(new Vector(0, 0, -1));						
					}
					session.dispatchCUISelection(player);
		            
					Block[] corners = new Block[2];
					
					Vector v1 = region.getPos1();
					corners[0] = bplayer.getWorld().getBlockAt(v1.getBlockX(), v1.getBlockY(), v1.getBlockZ());
					
					Vector v2 = region.getPos2();
					corners[1] = bplayer.getWorld().getBlockAt(v2.getBlockX(), v2.getBlockY(), v2.getBlockZ());
					
					return corners;

				} catch (Exception e) {
					
					if (e instanceof IncompleteRegionException)
						Util.Message("WorldEdit region is not fully selected!", bplayer);
					else
					{
						Challenges.log.log(Level.SEVERE, "[Jail] Error while retreiving WorldEdit region! - {0}", e.getMessage());
						e.printStackTrace();
					}
					return null;
				}
			}
			else
			{
				Util.Message("WorldEdit is not installed!", bplayer);
			}
			
			return null;
	    }

	 public static void clearSelection(Player bplayer)
	 {
		 Plugin plugin = Challenges.instance.getServer().getPluginManager().getPlugin("WorldEdit");
		 if (plugin == null)
		 {
			Util.Message("WorldEdit is not installed!", bplayer);
			return;
		 }
		 
		 WorldEditPlugin we = (WorldEditPlugin) plugin;
		 LocalPlayer player = new BukkitPlayer(we, we.getServerInterface(), bplayer);
		 LocalSession session = we.getWorldEdit().getSession(player);
		 RegionSelector selector = session.getRegionSelector(player.getWorld());
		 selector.clear();
		 session.dispatchCUISelection(player);
	 }
}
