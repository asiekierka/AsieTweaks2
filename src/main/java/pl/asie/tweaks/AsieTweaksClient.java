package pl.asie.tweaks;

import pl.asie.tweaks.override.GuiAchievementHidden;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class AsieTweaksClient {
	@SubscribeEvent
	public void overrideRendering(RenderGameOverlayEvent.Pre event) {
		if (AsieTweaks.disableXp) {
			GuiIngameForge.left_height = event.type == RenderGameOverlayEvent.ElementType.ARMOR ? 43 : 33;
			GuiIngameForge.right_height = event.type == RenderGameOverlayEvent.ElementType.AIR ? 43 : 33;
			if (event.type == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
				event.setCanceled(true);
			}
		}
		if (AsieTweaks.disableHunger) {
			if (event.type == RenderGameOverlayEvent.ElementType.FOOD) {
				event.setCanceled(true);
			}
		}
	}

	protected void init() {
		if (AsieTweaks.disableAchievements) {
			Minecraft.getMinecraft().guiAchievement = new GuiAchievementHidden(Minecraft.getMinecraft());
		}
	}
}
