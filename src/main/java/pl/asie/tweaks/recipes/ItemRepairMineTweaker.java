package pl.asie.tweaks.recipes;

import cpw.mods.fml.common.registry.GameRegistry;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
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

@ZenClass("mods.asietweaks.Repair")
public class ItemRepairMineTweaker {
	public static void init() {
		MineTweakerAPI.registerClass(ItemRepairMineTweaker.class);
	}

	@ZenMethod
	public static void addRepair(IItemStack repairable, IIngredient ingredient, double amount) {
		if(repairable == null || ingredient == null) {
			MineTweakerAPI.logError("No input given for Repair recipe");
			return;
		}
		ItemStack repairableItem = MineTweakerMC.getItemStack(repairable);
		if(!(repairableItem != null && repairableItem.getItem().isRepairable() && repairableItem.getMaxStackSize() == 1)) {
			MineTweakerAPI.logError("No repairable input given for Repair recipe");
		} else {
			MineTweakerAPI.apply(new RepairAction(repairable, ingredient, amount));
		}
	}

	public static class RepairRecipe implements IRecipe {
		public final ItemStack repairable;
		public final IIngredient ingredient;
		public final float amount;

		public RepairRecipe(IItemStack repairable, IIngredient ingredient, double amount) {
			this.repairable = MineTweakerMC.getItemStack(repairable);
			this.ingredient = ingredient;
			this.amount = (float) Math.max(0, Math.min(1, amount));
		}

		@Override
		public boolean matches(InventoryCrafting inventory, World world) {

			boolean ingredientMatch = false;
			boolean repairableMatch = false;

			for(int i = 0; i < 3; ++i) {
				for(int j = 0; j < 3; ++j) {
					ItemStack craftingstack = inventory.getStackInRowAndColumn(j, i);
					if(craftingstack != null) {
						{
							IItemStack icraftingstack = MineTweakerMC.getIItemStackWildcardSize(craftingstack);
							if(ingredient.matches(icraftingstack)) {
								ingredientMatch = true;
								continue;
							}
						}
						if(craftingstack.getItem() == repairable.getItem() && (repairable.getItemDamage() == 32767 || craftingstack.getItemDamage() > 0)) {
							if(repairableMatch) {
								return false;
							}
							repairableMatch = true;
							continue;
						}
						return false;
					}
				}
				if(ingredientMatch && repairableMatch) {
					break;
				}
			}
			return ingredientMatch && repairableMatch;
		}

		@Override
		public ItemStack getCraftingResult(InventoryCrafting inventory) {

			ItemStack output = null;
			int ingredientAmount = 0;
			for(int i = 0; i < 3; ++i) {
				for(int j = 0; j < 3; ++j) {
					ItemStack craftingstack = inventory.getStackInRowAndColumn(j, i);
					if(craftingstack != null) {
						{
							IItemStack icraftingstack = MineTweakerMC.getIItemStackWildcardSize(craftingstack);
							if(ingredient.matches(icraftingstack)) {
								ingredientAmount++;
								continue;
							}
						}
						if(craftingstack.getItem() == repairable.getItem() && (repairable.getItemDamage() == 32767 || craftingstack.getItemDamage() > 0)) {
							output = craftingstack.copy();
						}
					}
				}
			}
			if(output == null) {
				return null;
			}
			int canRepair = output.getItemDamage();
			int maxRepair = Math.round(output.getMaxDamage() * ingredientAmount * amount);
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

		public RepairAction(IItemStack repairable, IIngredient ingredient, double amount) {
			this.repairable = repairable.getName();
			this.ingredient = ingredient.getMark();
			this.amount = amount;
			this.recipe = new RepairRecipe(repairable, ingredient, amount);
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
