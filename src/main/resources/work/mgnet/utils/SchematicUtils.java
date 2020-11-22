package work.mgnet.utils;

import java.io.File;
import java.io.IOException;

import org.spongepowered.api.Sponge;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.sponge.SpongeWorldEdit;

@SuppressWarnings("deprecation")
public class SchematicUtils {

	/**
	 * Paste a Schematic
	 * @param Schematic File
	 * @throws MaxChangedBlocksException
	 * @throws DataException
	 * @throws IOException
	 */
	public static void pasteSchematic(File schemFile) throws MaxChangedBlocksException, DataException, IOException {
		if (!schemFile.exists()) return; // Return if it doesn't exist
		
		// Get Infinit Edit Session
		EditSession sess = WorldEdit.getInstance().getEditSessionFactory().getEditSession(SpongeWorldEdit.inst().getWorld(Sponge.getServer().getWorlds().iterator().next()), -1);
		
		CuboidClipboard cl = SchematicFormat.getFormat(schemFile).load(schemFile); // Load Clipboard with Schematic
		cl.paste(sess, new Vector(0, 0, 0), false, true); // Paste Clipboard
	}
	
	/**
	 * Quick Method for Trying to Paste A Schematic
	 * @param Schematic File
	 */
	public static void tryPasteSchematic(File schemFile) {
		try {
			pasteSchematic(schemFile);
		} catch (Exception e) {
			System.out.println("[SchematicUtils] Couldn't paste Schematic");
		}
	}
	
}
