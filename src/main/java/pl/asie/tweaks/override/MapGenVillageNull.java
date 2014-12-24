package pl.asie.tweaks.override;

import net.minecraft.world.gen.structure.MapGenVillage;

/**
 * Created by asie on 12/20/14.
 */
public class MapGenVillageNull extends MapGenVillage {
	protected boolean canSpawnStructureAtCoords(int p_75047_1_, int p_75047_2_) {
		return false;
	}
}
