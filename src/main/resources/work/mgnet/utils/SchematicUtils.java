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
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import com.sk89q.worldedit.sponge.SpongeWorldEdit;

@SuppressWarnings("deprecation")
public class SchematicUtils {

	public static void pasteSchematic(File schemFile) throws MaxChangedBlocksException, DataException, IOException {
		if (!schemFile.exists()) return;
		EditSession sess = WorldEdit.getInstance().getEditSessionFactory().getEditSession(SpongeWorldEdit.inst().getWorld(Sponge.getServer().getWorlds().iterator().next()), -1);
		
		CuboidClipboard cl = MCEditSchematicFormat.getFormat(schemFile).load(schemFile);
		cl.paste(sess, new Vector(0, 0, 0), false, true);
	}
	
	public static void tryPasteSchematic(File schemFile) {
		try {
			pasteSchematic(schemFile);
		} catch (Exception e) {
			System.out.println("[SchematicUtils] Couldn't paste Schematic");
		}
	}
	
}
