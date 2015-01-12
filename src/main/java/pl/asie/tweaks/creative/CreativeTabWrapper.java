package pl.asie.tweaks.creative;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class CreativeTabWrapper extends CreativeTabs {
	public static HashSet<ItemStack> blacklist = new HashSet<ItemStack>();

	private int tabIndex;
	private final CreativeTabs original;
	private List items;

	public CreativeTabWrapper(CreativeTabs original, int tabIndex) {
		super(original.getTabLabel());
		this.original = original;
		this.tabIndex = tabIndex;
	}

	@Override
	public String getTranslatedTabLabel() {
		return original.getTranslatedTabLabel();
	}

	@Override
	public ItemStack getIconItemStack() {
		return original.getIconItemStack();
	}

	@Override
	public Item getTabIconItem() {
		return original.getTabIconItem();
	}

	@Override
	public int func_151243_f() {
		return original.func_151243_f();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getBackgroundImageName() {
		return original.getBackgroundImageName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean drawInForegroundOfTab() {
		return original.drawInForegroundOfTab();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldHidePlayerInventory() {
		return original.shouldHidePlayerInventory();
	}

	@Override
	public boolean hasSearchBar() {
		return tabIndex == 5 || original.hasSearchBar();
	}

	@Override
	public int getSearchbarWidth()
	{
		return original.getSearchbarWidth();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumEnchantmentType[] func_111225_m() {
		return original.func_111225_m();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean func_111226_a(EnumEnchantmentType p_111226_1_) {
		return original.func_111226_a(p_111226_1_);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addEnchantmentBooksToList(List p_92116_1_, EnumEnchantmentType... p_92116_2_) {
		original.addEnchantmentBooksToList(p_92116_1_, p_92116_2_);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void displayAllReleventItems(List p_78018_1_) {
		if (items == null) {
			items = new ArrayList<Object>();
			if (original != CreativeTabs.tabAllSearch) {
				super.displayAllReleventItems(items);
			} else {
				// Add items
				Iterator iterator = Item.itemRegistry.iterator();

				while (iterator.hasNext()) {
					Item item = (Item)iterator.next();

					if (item != null && item.getCreativeTab() != null) {
						item.getSubItems(item, null, items);
					}
				}

				// Add enchanted books
				Enchantment[] aenchantment = Enchantment.enchantmentsList;

				for (int i = 0; i < aenchantment.length; ++i) {
					Enchantment enchantment = aenchantment[i];

					if (enchantment != null && enchantment.type != null) {
						Items.enchanted_book.func_92113_a(enchantment, items);
					}
				}
			}
		}
		p_78018_1_.addAll(items);
	}

	//

	/**
	 * returns index % 6
	 */
	@SideOnly(Side.CLIENT)
	public int getTabColumn()
	{
		if (tabIndex > 11)
		{
			return ((tabIndex - 12) % 10) % 5;
		}
		return this.tabIndex % 6;
	}

	@SideOnly(Side.CLIENT)
	public int getTabIndex()
	{
		return this.tabIndex;
	}

	/**
	 * returns tabIndex < 6
	 */
	@SideOnly(Side.CLIENT)
	public boolean isTabInFirstRow()
	{
		if (tabIndex > 11)
		{
			return ((tabIndex - 12) % 10) < 5;
		}
		return this.tabIndex < 6;
	}

	public int getTabPage()
	{
		if (tabIndex > 11)
		{
			return ((tabIndex - 12) / 10) + 1;
		}
		return 0;
	}

	protected CreativeTabs getOriginalTab() {
		return original;
	}
}
