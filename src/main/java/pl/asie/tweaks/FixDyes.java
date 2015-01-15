package pl.asie.tweaks;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixDyes {
	public static final String[] dyes = new String[] {
		"dyeBlack", "dyeRed", "dyeGreen", "dyeBrown",
		"dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray",
		"dyeGray", "dyePink", "dyeLime", "dyeYellow",
		"dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite"
	};

	public static void run() {
		Map<ItemStack, String> replacements = new HashMap<ItemStack, String>();
		for (int i = 0; i < 16; i++) {
			replacements.put(new ItemStack(Items.dye, 1, i), dyes[i]);
		}

		List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
		List<IRecipe> removed = new ArrayList<IRecipe>();
		List<IRecipe> added = new ArrayList<IRecipe>();

		for (IRecipe obj : recipes) {
			if (obj == null || obj.getRecipeOutput() == null || obj.getRecipeOutput().getItem() == Item.getItemFromBlock(Blocks.lapis_block)) {
				continue;
			}

			if (obj instanceof ShapedRecipes) {
				ShapedRecipes recipe = (ShapedRecipes) obj;
				for (ItemStack i: recipe.recipeItems) {
					if (i != null && i.getItem() == Items.dye && i.getItemDamage() != 4) {
						removed.add(recipe);

						Object[] recipeData = new Object[recipe.recipeItems.length];
						for (int j = 0; j < recipeData.length; j++) {
							if (recipe.recipeItems[j] != null && recipe.recipeItems[j].getItem() == Items.dye && recipe.recipeItems[j].getItemDamage() != 4) {
								recipeData[j] = dyes[recipe.recipeItems[j].getItemDamage() & 15];
							} else {
								recipeData[j] = recipe.recipeItems[j];
							}
						}

						added.add(new ShapedOreRecipe(recipe.getRecipeOutput(), toRecipeFormatShaped(recipeData, recipe.recipeWidth, recipe.recipeHeight)));

						break;
					}
				}
			} else if (obj instanceof ShapelessRecipes) {
				ShapelessRecipes recipe = (ShapelessRecipes) obj;
				for (ItemStack i: (List<ItemStack>) recipe.recipeItems) {
					if (i != null && i.getItem() == Items.dye && i.getItemDamage() != 4) {
						removed.add(recipe);

						ItemStack[] recipeItems = ((List<ItemStack>) recipe.recipeItems).toArray(new ItemStack[recipe.recipeItems.size()]);

						Object[] recipeData = new Object[recipeItems.length];
						for (int j = 0; j < recipeData.length; j++) {
							if (recipeItems[j] != null && recipeItems[j].getItem() == Items.dye && recipeItems[j].getItemDamage() != 4) {
								recipeData[j] = dyes[recipeItems[j].getItemDamage() & 15];
							} else {
								recipeData[j] = recipeItems[j];
							}
						}

						added.add(new ShapelessOreRecipe(recipe.getRecipeOutput(), toRecipeFormatShapeless(recipeData)));

						break;
					}
				}
			} else if (obj instanceof ShapedOreRecipe) {
				ShapedOreRecipe recipe = (ShapedOreRecipe) obj;
				Object[] data = recipe.getInput();
				for (int i = 0; i < data.length; i++) {
					if (data[i] instanceof ItemStack && ((ItemStack) data[i]).getItem() == Items.dye && ((ItemStack) data[i]).getItemDamage() != 4) {
						data[i] = OreDictionary.getOres(dyes[((ItemStack) data[i]).getItemDamage() & 15]);
					}
				}
			} else if (obj instanceof ShapelessOreRecipe) {
				ShapelessOreRecipe recipe = (ShapelessOreRecipe) obj;
				ArrayList<Object> data = recipe.getInput();
				for (int i = 0; i < data.size(); i++) {
					if (data.get(i) instanceof ItemStack && ((ItemStack) data.get(i)).getItem() == Items.dye && ((ItemStack) data.get(i)).getItemDamage() != 4) {
						data.set(i, OreDictionary.getOres(dyes[((ItemStack) data.get(i)).getItemDamage() & 15]));
					}
				}
			}
		}
		recipes.removeAll(removed);
	}

	private static int[] intersection(int[] a, int[] b) {
		int[] c = new int[Math.min(a.length, b.length)];
		int z = 0;
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < b.length; j++) {
				if (a[i] == b[j]) {
					c[z++] = a[i];
					break;
				}
			}
		}
		int[] d = new int[z];
		System.arraycopy(c, 0, d, 0, z);
		return d;
	}

	private static String listToOreName(ArrayList<ItemStack> items) {
		int i = 1;
		int[] oreIDs = OreDictionary.getOreIDs(items.get(0));
		int[] oreIDsOrig = oreIDs;
		while(oreIDs.length > 1 && i < items.size()) {
			oreIDs = intersection(oreIDs, OreDictionary.getOreIDs(items.get(i++)));
		}
		if (oreIDs.length == 0) {
			return oreIDsOrig.length > 0 ? OreDictionary.getOreName(oreIDsOrig[0]) : "wat";
		}
		return OreDictionary.getOreName(oreIDs[0]);
	}

	private static Object[] toRecipeFormatShapeless(Object[] in) {
		for (int i = 0; i < in.length; i++) {
			if (in[i] instanceof List) {
				in[i] = listToOreName((ArrayList<ItemStack>) in[i]);
			}
		}
		return in;
	}

	private static Object[] toRecipeFormatShaped(Object[] in, int width, int height) {
		int nonNullInputs = 0;
		for (int i = 0; i < in.length; i++) {
			if (in[i] != null) nonNullInputs++;
		}
		Object[] out = new Object[nonNullInputs * 2 + height];
		int k = 0;
		for (int i = 0; i < height; i++) {
			out[i] = "";
			for (int j = 0; j < width; j++) {
				out[i] = ((String) out[i]) + (k++);
			}
		}
		k = 0;
		for (int i = 0; i < in.length; i++) {
			if (in[i] == null) {
				continue;
			}
			if (in[i] instanceof List) {
				in[i] = listToOreName((ArrayList<ItemStack>) in[i]);
			}
			out[height + (k * 2)] = ("" + i).charAt(0);
			out[height + (k * 2) + 1] = in[i];
			k++;
		}
		return out;
	}
}
