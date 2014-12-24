package pl.asie.tweaks.creative;

import net.minecraft.creativetab.*;
import net.minecraft.item.*;

public class CreativeTabCustom extends CreativeTabs
{
    private ItemStack stack;
    
    public CreativeTabCustom(String name, ItemStack stack) {
        super(name);
        this.stack = stack;
    }

    @Override
    public ItemStack getIconItemStack() { return this.stack; }
    
    @Override
    public Item getTabIconItem() {
        return this.stack.getItem();
    }

    @Override
    public int func_151243_f() {
        return this.stack.getItemDamage();
    }
}
