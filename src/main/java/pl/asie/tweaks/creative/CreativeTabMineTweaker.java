package pl.asie.tweaks.creative;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

@ZenClass("mods.asietweaks.CreativeTab")
public class CreativeTabMineTweaker {
	public static void init() {
		MineTweakerAPI.registerClass(CreativeTabMineTweaker.class);
	}

	@ZenMethod
	public static void hide(IItemStack ingredient) {
		MineTweakerAPI.apply(new HideAction(ingredient));
	}

	private static class HideAction implements IUndoableAction {
		private IItemStack ing;
		private HashMap<Item, CreativeTabs> oldTabsItem = new HashMap<Item, CreativeTabs>();
		private HashMap<Block, CreativeTabs> oldTabsBlock = new HashMap<Block, CreativeTabs>();

		public HideAction(IItemStack ing) {
			this.ing = ing;
		}

		@Override
		public void apply() {
			ItemStack stack = MineTweakerMC.getItemStack(ing);
			if (stack.getItem() != null) {
				oldTabsItem.put(stack.getItem(), stack.getItem().getCreativeTab());
				stack.getItem().setCreativeTab(null);
				if (stack.getItem() instanceof ItemBlock) {
					oldTabsBlock.put(Block.getBlockFromItem(stack.getItem()), Block.getBlockFromItem(stack.getItem()).getCreativeTabToDisplayOn());
					Block.getBlockFromItem(stack.getItem()).setCreativeTab(null);
				}
			}
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public void undo() {
			for (Block b : oldTabsBlock.keySet()) {
				b.setCreativeTab(oldTabsBlock.get(b));
			}
			for (Item b : oldTabsItem.keySet()) {
				b.setCreativeTab(oldTabsItem.get(b));
			}
			oldTabsBlock.clear();
			oldTabsItem.clear();
		}

		@Override
		public String describe() {
			return "Hiding from Creative Tabs " + ing.getName();
		}

		@Override
		public String describeUndo() {
			return "Unhiding from Creative Tabs " + ing.getName();
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}
	}
}
