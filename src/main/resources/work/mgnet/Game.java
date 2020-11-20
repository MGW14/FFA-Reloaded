package work.mgnet;

import java.util.ArrayList;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;

import work.mgnet.utils.CommandUtils;
import work.mgnet.utils.SchematicUtils;

public class Game {

	public static ArrayList<String> players;
	public static boolean isRunning = false;
	
	public static void playerJoin(Player p) {
		CommandUtils.runCommand("tickrate " + p.getName() + " " + FFA.configUtils.getDouble("tickrate"));
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
		
		SchematicUtils.tryPasteSchematic(FFA.getMapFile());
		
		isRunning = false;
	}
	
}
