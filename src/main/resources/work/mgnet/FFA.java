package work.mgnet;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.plugin.Plugin;

import com.google.inject.Inject;

import work.mgnet.commands.FFAConfigCommand;
import work.mgnet.commands.ForceendCommand;
import work.mgnet.commands.ForcestartCommand;
import work.mgnet.commands.ItemsCommand;
import work.mgnet.commands.ReadyCommand;
import work.mgnet.commands.ReloadmapCommand;
import work.mgnet.commands.SetItemsCommand;
import work.mgnet.commands.StatisticsCommand;
import work.mgnet.utils.ConfigurationUtils;
import work.mgnet.utils.KitUtils;
import work.mgnet.utils.StatsUtils;

@Plugin(id = "ffa", name = "FFA", version = "1.0", description = "Adds FFA")
public class FFA {

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path privateConfigDir;

	@Inject
	public static Logger logger;
	
	private static Path configDir;
	private static File mapFile;
	
	public static ConfigurationUtils configUtils;
	public static StatsUtils statsUtils;
	
	public static File getMapFile() {
		return mapFile;
	}
	public static Path getConfigDir() {
		return configDir;
	}
	public static ArrayList<String> edit=new ArrayList<String>();
	public static HashMap<String, Inventory> inves = new HashMap<String, Inventory>();
	@Listener
	public void onServer(GameStartedServerEvent e) {
		try {
			configUtils = new ConfigurationUtils(privateConfigDir.toFile());
			
			if (configUtils.getString("map") == null) configUtils.setString("map", "map");
			if (configUtils.getLocation("spawn").getBlockX() == 0) configUtils.setLocation("spawn", 0, 101, 0);
			if (configUtils.getFloat("tickrate") == 0f) configUtils.setFloat("tickrate", 10);
			if (configUtils.getFloat("spreadPlayerDistance") == 0f) configUtils.setFloat("spreadPlayerDistance", 25);
			if (configUtils.getFloat("spreadPlayerRadius") == 0f) configUtils.setFloat("spreadPlayerRadius", 130);
			
			mapFile = new File(privateConfigDir.toFile(), configUtils.getString("map"));
			statsUtils = new StatsUtils();
			try {
				statsUtils.loadStats(privateConfigDir.toFile());
			} catch (Exception nothinghappend) {
				logger.error("Nothing happend lmao");
			}
		} catch (Exception e1) {
			logger.error("[FFA] Couldn't load Configuration!");
		}
		configDir=privateConfigDir;
		
		Sponge.getCommandManager().register(this, new ReadyCommand(), "ready");
		Sponge.getCommandManager().register(this, new ForceendCommand(), "forceend");
		Sponge.getCommandManager().register(this, new ForcestartCommand(), "forcestart"); 
		Sponge.getCommandManager().register(this, new ReloadmapCommand(), "reloadmap");
		Sponge.getCommandManager().register(this, new StatisticsCommand(), "statistics");
		Sponge.getCommandManager().register(this, new FFAConfigCommand(), "ffa");
		Sponge.getCommandManager().register(this, new ItemsCommand(), "items");
		Sponge.getCommandManager().register(this, new SetItemsCommand(), "setitems");
	}
	
	@Listener
	public void onLogin(ClientConnectionEvent.Join e) {
		e.setMessageCancelled(true);
		Game.playerJoin(e.getTargetEntity());
	}
	
	@Listener
	public void onDrop(DropItemEvent e) {
		e.getCause().first(Player.class).ifPresent((p) -> {
			if (!Game.isRunning && !p.hasPermission("mgw.bypasslobby")) e.setCancelled(true);
		});
	}
	
	@Listener
	public void onPvP(DamageEntityEvent e) {
		try {
			e.getCause().first(Player.class).ifPresent((killer) -> {
				if (Game.isRunning && e.willCauseDeath() && e.getTargetEntity().getType() == EntityTypes.PLAYER) {
					for (Player p : Sponge.getServer().getOnlinePlayers()) {
						if (e.getCause().getContext().toString().contains(p.getName())) {
							FFA.statsUtils.updateStats(p.getUniqueId(), 1, 0, 0, 0);
							break;
						}
					}
					FFA.statsUtils.updateStats((Player) e.getTargetEntity(), 0, 1, 1, 0);
				}
			});
			Optional<DamageSource> source = e.getCause().first(DamageSource.class);
			if (!Game.isRunning && source.get() != DamageSources.VOID) e.setCancelled(true);
		} catch (Exception f) {
			
		}
	}
	
	@Listener
	public void onLeave(ClientConnectionEvent.Disconnect e) throws CommandException {
		Game.playerOut(e.getTargetEntity());
	}
	
	@Listener
	public void onDeath(DestructEntityEvent.Death e) {
		if (e.getTargetEntity().getType() == EntityTypes.PLAYER) {
			Game.playerOut((Player) e.getTargetEntity());
		}
	}
	
	@Listener
	public void onInv(InteractInventoryEvent.Close e) throws Exception {
		if (edit.contains(((Player) e.getSource()).getName())) {
			KitUtils.saveKit("test", e.getTargetInventory(), privateConfigDir);
			edit.remove(((Player) e.getSource()).getName());
		}
	}
}
