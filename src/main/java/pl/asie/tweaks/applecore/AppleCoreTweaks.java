package pl.asie.tweaks.applecore;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import squeek.applecore.api.hunger.StarvationEvent;

public class AppleCoreTweaks {
	public static final AppleCoreTweaks INSTANCE = new AppleCoreTweaks();

	private int starvationTickMultiplier = 100;

	public void preInit(Configuration config) {
		starvationTickMultiplier = config.getInt("starvationTimeMultiplier", "applecore", 100, 0, 8000, "Set the multiplier for starvation time (0 disables starvation)");
	}

	public void postInit() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void starveEvent(StarvationEvent.GetStarveTickPeriod event) {
		event.starveTickPeriod = event.starveTickPeriod * 100 / starvationTickMultiplier;
	}
}
