package pl.asie.tweaks.override;

import net.minecraft.world.gen.structure.MapGenStronghold;

public class MapGenStrongholdNull extends MapGenStronghold {
	protected boolean canSpawnStructureAtCoords(int p_75047_1_, int p_75047_2_) {
		return false;
	}
}
