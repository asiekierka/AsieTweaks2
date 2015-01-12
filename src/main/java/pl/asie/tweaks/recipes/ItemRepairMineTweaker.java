package pl.asie.tweaks.recipes;

import minetweaker.MineTweakerAPI;
import stanhebben.zenscript.annotations.ZenClass;

@ZenClass("mods.asietweaks.Repair")
public class ItemRepairMineTweaker {
	public static void init() {
		MineTweakerAPI.registerClass(ItemRepairMineTweaker.class);
	}
}
