package ruiseki.omoshiroikamo.common.item.backpack;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.CraftingUpgradeWrapper;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class ItemCraftingUpgrade extends ItemUpgrade<CraftingUpgradeWrapper> {

    public ItemCraftingUpgrade() {
        super(ModObject.itemCraftingUpgrade.unlocalisedName);
        setMaxStackSize(1);
        setTextureName("crafting_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "crafting_upgrade"));
    }

    @Override
    public CraftingUpgradeWrapper createWrapper(ItemStack stack) {
        return new CraftingUpgradeWrapper(stack);
    }
}
