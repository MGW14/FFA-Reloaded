package work.mgnet.utils;

import java.io.File;
import java.io.IOException;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class ConfigurationUtils {
	
	private ConfigurationNode node;
	private @NonNull HoconConfigurationLoader manager;
	
	public ConfigurationUtils(File configDir) throws IOException {
		if (!configDir.exists()) configDir.createNewFile();
		File configFile = new File(configDir, "config.yml");
		if (!configFile.exists()) configFile.createNewFile();
		manager = HoconConfigurationLoader.builder().setPath(configFile.toPath()).build();
		node = manager.createEmptyNode();
	}
	
	public String getString(String key) {
		return node.getNode(key).getString();
	}
	
	public int getInteger(String key) {
		return node.getNode(key).getInt();
	}
	
	public double getDouble(String key) {
		return node.getNode(key).getDouble();
	}
	
	public float getFloat(String key) {
		return node.getNode(key).getFloat();
	}
	
	public Location<World> getLocation(String key) {
		double x = node.getNode(key + "X").getDouble();
		double y = node.getNode(key + "Y").getDouble();
		double z = node.getNode(key + "Z").getDouble();
		return new Location<World>(Sponge.getServer().getWorlds().iterator().next(), x, y, z);
	}
	
	public void saveConfiguration() {
		try {
			manager.save(node);
		} catch (IOException e) {
			System.out.println("[ConfigurationManager] Couldn't save configuration!");
		}
	}
	
}
