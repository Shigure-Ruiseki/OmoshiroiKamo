package ruiseki.omoshiroikamo.module.backpack.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.AdvancedVoidUpgradeWrapper;

public class ItemAdvancedVoidUpgrade extends ItemUpgrade<AdvancedVoidUpgradeWrapper> {

    public ItemAdvancedVoidUpgrade() {
        super(ModObject.itemAdvancedVoidUpgrade.unlocalisedName);
        setMaxStackSize(1);
        setTextureName("advanced_void_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "backpack.advanced_void_upgrade"));
    }

    @Override
    public AdvancedVoidUpgradeWrapper createWrapper(ItemStack stack) {
        return new AdvancedVoidUpgradeWrapper(stack);
    }
}
