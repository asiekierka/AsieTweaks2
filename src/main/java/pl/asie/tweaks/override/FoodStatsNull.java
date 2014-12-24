package pl.asie.tweaks.override;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FoodStatsNull extends FoodStats {
	@Override
	public void onUpdate(EntityPlayer p_75118_1_) { }

	@Override
	public int getFoodLevel()
	{
		return 10;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getPrevFoodLevel()
	{
		return 10;
	}

	@Override
	public boolean needFood()
	{
		return false;
	}

	@Override
	public float getSaturationLevel()
	{
		return 0.0F;
	}
}
