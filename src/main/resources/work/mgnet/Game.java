package work.mgnet;

import java.util.ArrayList;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import work.mgnet.utils.CommandUtils;

public class Game {

	public static ArrayList<String> players = new ArrayList<String>();
	public static boolean isRunning = false;
	
	public static void startGame() {
		isRunning = true;
		
		CommandUtils.runCommand("tickrate " + FFA.configUtils.getFloat("tickrate"));
		CommandUtils.runCommand("difficulty 1");
		CommandUtils.runCommand("effect @a clear");
		
		Location<World> pvpLocation = FFA.configUtils.getLocation("pvp");
		double spreadPlayerDistance = FFA.configUtils.getFloat("spreadPlayerDistance");
		double spreadPlayerRadius = FFA.configUtils.getFloat("spreadPlayerRadius");
		CommandUtils.runCommand("spreadplayers " + pvpLocation.getBlockX() + " " + pvpLocation.getBlockZ() + " "+spreadPlayerDistance+" " + spreadPlayerRadius + " false @a");
		
		for (Player player : Sponge.getGame().getServer().getOnlinePlayers()) {
			player.offer(Keys.GAME_MODE, GameModes.SURVIVAL);
			player.offer(Keys.HEALTH, 20D);
			player.offer(Keys.FOOD_LEVEL, 20);
			player.offer(Keys.EXPERIENCE_LEVEL, 0);
			player.sendMessage(Text.of("§b»§7 The Game has begun. Kill everyone to win"));
		}
	}
	
	public static void playerJoin(Player p) {
		CommandUtils.runCommand("tickrate " + FFA.configUtils.getDouble("tickrate") + " " + p.getName());
		p.getInventory().clear();
		p.setLocation(FFA.configUtils.getLocation("spawn"));
		CommandUtils.runCommand("spawnpoint " + p.getName());
		if (isRunning) {
			p.offer(Keys.GAME_MODE, GameModes.SPECTATOR);
			p.sendMessage(Text.of("§b»§7 A game is already running, after the round you will participate"));
		} else {
			p.offer(Keys.GAME_MODE, GameModes.ADVENTURE);
			p.sendMessage(Text.of("§b»§7 Type §a/items §7to see all the items you can get. When you are ready, type §a/ready§7."));
		}
	}
	
	public static void playerOut(Player p) {
		if (!players.contains(p.getName())) return;
		players.remove(p.getName());
		p.offer(Keys.GAME_MODE, GameModes.SPECTATOR);
		if (players.size() == 1) {
			Player winner = Sponge.getServer().getPlayer(players.get(0)).get();
			for (Player player : Sponge.getGame().getServer().getOnlinePlayers()) {
				player.sendTitle(Title.of(Text.of(winner.getName() + " won!")));
			}
			FFA.statsUtils.updateStats(winner, 0, 0, 1, 1);
			endGame();
		}
	}
	
	public static void endGame() {
		if (!isRunning) return;
		players.clear();
		
		for (Player player : Sponge.getGame().getServer().getOnlinePlayers()) {
			player.setLocation(FFA.configUtils.getLocation("spawn"));
			player.getInventory().clear();
			player.offer(Keys.HEALTH, 20D);
			player.offer(Keys.FOOD_LEVEL, 20);
			player.offer(Keys.EXPERIENCE_LEVEL, 0);
			player.sendMessage(Text.of("§b»§e The Game has ended"));
			player.sendMessage(Text.of("§b»§7 Type §a/items §7to see all the items you can get. When you are ready, type §a/ready§7."));
			player.offer(Keys.GAME_MODE, GameModes.ADVENTURE);
		}
		CommandUtils.runCommand("difficulty 0");
		CommandUtils.runCommand("effect @a clear");
		CommandUtils.runCommand("tickrate 20");
		CommandUtils.runCommand("kill @e[type=!player]");
	
		isRunning = false;
	}
	
}
