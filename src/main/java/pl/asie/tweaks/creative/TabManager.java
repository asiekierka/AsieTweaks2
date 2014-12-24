package pl.asie.tweaks.creative;

import pl.asie.tweaks.AsieTweaks;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class TabManager
{
    private HashMap<String, CreativeTabs> nameToTab;
    private HashMap<String, CreativeTabs> nameToTabWithHidden;

    public TabManager() {
        super();
        this.nameToTab = new HashMap<String, CreativeTabs>();
        this.nameToTabWithHidden = new HashMap<String, CreativeTabs>();
    }
    
    public CreativeTabs get(final String name) {
        if (name.equals("none")) {
            return null;
        }
        return this.nameToTab.get(name);
    }
    
    private void addTabToList(final List<CreativeTabs> newList, final CreativeTabs tab, final boolean changeIndex) {
        newList.add(tab);
        if (changeIndex) {
            for (final Field f : CreativeTabs.class.getDeclaredFields()) {
                if (f.getName().equals("tabIndex") || f.getName().equals("field_78033_n") || (f.getName().equals("n") && f.getGenericType().equals(Integer.TYPE))) {
                    f.setAccessible(true);
                    try {
                        f.set(tab, newList.indexOf(tab));
                    }
                    catch (Exception e) {
                        AsieTweaks.log.error("Failed setting tab position for tab " + tab.getTabLabel() + "!");
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    public void loadTabs(final Configuration config, final Configuration tabConfig) {
        for (final String tabID : tabConfig.getCategoryNames()) {
            final String ccn = tabID;
            final String tabName = tabConfig.get(ccn, "name", tabID).getString();
            final String itemName = tabConfig.get(ccn, "iconItem", 1).getString();
            final Object item = Item.itemRegistry.getObject(itemName);
            final int itemDamage = tabConfig.get(ccn, "iconItemDamage", 0).getInt();
            if (item != null) {
                LanguageRegistry.instance().addStringLocalization("itemGroup." + tabID, tabName);
                new CreativeTabCustom(tabID, new ItemStack((Item) item, 1, itemDamage));
            } else {
                AsieTweaks.log.warn("Invalid item name: " + itemName);
            }
        }
        final CreativeTabs[] tabs = CreativeTabs.creativeTabArray.clone();
        final ArrayList<CreativeTabs> newList = new ArrayList<CreativeTabs>(tabs.length);
        for (final CreativeTabs tab : tabs) {
            if (tab != null) {
                if (!newList.contains(tab)) {
                    if (tab.getTabIndex() != 5 && tab.getTabIndex() != 11) {
                        if (newList.size() == 5) {
                            this.addTabToList(newList, CreativeTabs.tabAllSearch, false);
                        }
                        if (newList.size() == 11) {
                            this.addTabToList(newList, CreativeTabs.tabInventory, false);
                        }
                        final Property p = config.get("hiddenTabs", tab.getTabLabel(), false);
                        if (!p.getBoolean(false)) {
                            this.addTabToList(newList, tab, true);
                        }
                    }
                }
            }
        }
        if (!newList.contains(CreativeTabs.tabAllSearch)) {
            while (newList.size() < 5) {
                newList.add(null);
            }
            this.addTabToList(newList, CreativeTabs.tabAllSearch, false);
        }
        if (!newList.contains(CreativeTabs.tabInventory)) {
            while (newList.size() < 11) {
                newList.add(null);
            }
            this.addTabToList(newList, CreativeTabs.tabInventory, false);
        }
        for (final CreativeTabs tab : CreativeTabs.creativeTabArray) {
            if (tab != null) {
                this.nameToTabWithHidden.put(tab.getTabLabel(), tab);
            }
        }
        CreativeTabs.creativeTabArray = newList.toArray(new CreativeTabs[newList.size()]);
        for (final CreativeTabs tab : CreativeTabs.creativeTabArray) {
            if (tab != null) {
                this.nameToTabWithHidden.put(tab.getTabLabel(), tab);
                this.nameToTab.put(tab.getTabLabel(), tab);
            }
        }
        final ConfigCategory redirCat = config.getCategory("redirections");
        redirCat.setComment("Format: S:source=target, moves all items in tab SOURCE to tab TARGET.");
        for (final Property redirection : redirCat.getValues().values()) {
            if (this.nameToTabWithHidden.containsKey(redirection.getName())) {
                this.nameToTab.put(redirection.getName(), this.nameToTab.get(redirection.getString()));
            } else {
                AsieTweaks.log.warn("No such tab: " + redirection.getName());
            }
        }
    }
    
    public void arrangeItems(final Configuration config) {
        Iterator it = Block.blockRegistry.iterator();
        while (it.hasNext()) {
            Block b = (Block) it.next();
            if (b != null) {
                String name = Block.blockRegistry.getNameForObject(b);
                if (b.getCreativeTabToDisplayOn() == null) {
                    final Property p = config.get("blocks", name, "none");
                    b.setCreativeTab(this.get(p.getString()));
                }
                else {
                    final Property p = config.get("blocks", name, b.getCreativeTabToDisplayOn().getTabLabel());
                    b.setCreativeTab(this.get(p.getString()));
                }
            }
        }
        it = Item.itemRegistry.iterator();
        while (it.hasNext()) {
            Item i = (Item) it.next();
            if (i != null && !(i instanceof ItemBlock)) {
                String name = Item.itemRegistry.getNameForObject(i);
                if (i.getCreativeTabs().length <= 1) {
                    if (i.getCreativeTab() == null) {
                        final Property p = config.get("items", name, "none");
                        i.setCreativeTab(this.get(p.getString()));
                    }
                    else {
                        final Property p = config.get("items", name, i.getCreativeTab().getTabLabel());
                        i.setCreativeTab(this.get(p.getString()));
                    }
                }
            }
        }
    }
}
