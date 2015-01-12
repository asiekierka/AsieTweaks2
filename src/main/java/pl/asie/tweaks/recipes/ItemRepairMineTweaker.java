package pl.asie.tweaks.recipes;

import cpw.mods.fml.common.registry.GameRegistry;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.Collections;

@ZenClass("mods.asietweaks.Repair")
public class ItemRepairMineTweaker {
	public static void init() {
		MineTweakerAPI.registerClass(ItemRepairMineTweaker.class);
	}

	@ZenMethod
	public static void addRepair(IItemStack repairable, IItemStack ingredient, double amount) {
		MineTweakerAPI.apply(new RepairAction(repairable, ingredient, amount));
	}

	public static class RepairRecipe implements IRecipe {
		public final ItemStack repairable;
		public final ItemStack ingredient;
		public final float amount;

		public RepairRecipe(ItemStack repairable, ItemStack ingredient, double amount) {
			this.repairable = repairable;
			this.ingredient = ingredient;
			this.amount = (float) Math.max(0, Math.min(1, amount));
		}

		@Override
		public boolean matches(InventoryCrafting inventory, World world) {
			ArrayList<ItemStack> stacklist = new ArrayList<ItemStack>();
			Collections.addAll(stacklist, repairable, ingredient);

			for(int i = 0; i < 3; ++i) {
				for(int j = 0; j < 3; ++j) {
					ItemStack craftingstack = inventory.getStackInRowAndColumn(j, i);

					if(craftingstack != null) {
						boolean flag = false;

						for(ItemStack stack : stacklist) {

							if(craftingstack.getItem() == stack.getItem() && (stack.getItemDamage() == 32767 || craftingstack.getItemDamage() == stack.getItemDamage())) {
								flag = true;
								stacklist.remove(stack);
								break;
							}
						}

						if(!flag) {
							return false;
						}
					}
				}
			}
			return stacklist.isEmpty();
		}

		@Override
		public ItemStack getCraftingResult(InventoryCrafting inventory) {
			ItemStack output = this.repairable.copy();
			int canRepair = output.getMaxDamage() - output.getItemDamage();
			int maxRepair = Math.round(output.getMaxDamage() * amount);
			if(canRepair > maxRepair) {
				output.setItemDamage(output.getItemDamage() - maxRepair);
			} else {
				output.setItemDamage(0);
			}
			return output;
		}

		@Override
		public int getRecipeSize() {
			return 2;
		}

		@Override
		public ItemStack getRecipeOutput() {
			return this.repairable;
		}
	}

	public static class RepairAction implements IUndoableAction {

		public ArrayList<RepairRecipe> repairRecipes = new ArrayList<RepairRecipe>();
		private RepairRecipe recipe;
		private String repairable;
		private String ingredient;
		private double amount;

		public RepairAction(IItemStack repairable, IItemStack ingredient, double amount) {
			this.repairable = repairable.getName();
			this.ingredient = ingredient.getName();
			this.amount = amount;
			ItemStack repairableItem = MineTweakerMC.getItemStack(repairable);
			ItemStack ingredientItem = MineTweakerMC.getItemStack(ingredient);
			this.recipe = new RepairRecipe(repairableItem, ingredientItem, amount);
		}

		@Override
		public void apply() {
			repairRecipes.add(this.recipe);
			GameRegistry.addRecipe(this.recipe);
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public void undo() {
			repairRecipes.remove(this.recipe);
			CraftingManager.getInstance().getRecipeList().remove(this.recipe);
		}

		@Override
		public String describe() {
			return "Adding Repair recipe: " + repairable + " + " + ingredient + " repairs " + amount;
		}

		@Override
		public String describeUndo() {
			return "Removing Repair recipe: " + repairable + " + " + ingredient + " repairs " + amount;
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}
	}
}
