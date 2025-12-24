package ruiseki.omoshiroikamo.module.backpack.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.VoidUpgradeWrapper;

public class ItemVoidUpgrade extends ItemUpgrade<VoidUpgradeWrapper> {

    public ItemVoidUpgrade() {
        super(ModObject.itemVoidUpgrade.unlocalisedName);
        setMaxStackSize(1);
        setTextureName("void_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "backpack.void_upgrade"));
    }

    @Override
    public VoidUpgradeWrapper createWrapper(ItemStack stack) {
        return new VoidUpgradeWrapper(stack);
    }
}
