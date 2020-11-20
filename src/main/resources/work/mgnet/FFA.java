package work.mgnet;

import java.nio.file.Path;

import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.plugin.Plugin;

import com.google.inject.Inject;

import de.schlichtherle.io.File;
import work.mgnet.utils.ConfigurationUtils;

@Plugin(id = "ffa", name = "FFA", version = "1.0", description = "Adds FFA")
public class FFA {

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path privateConfigDir;

	public static ConfigurationUtils configUtils;

	public static File getMapFile() {
		return null;
	}
	
}
