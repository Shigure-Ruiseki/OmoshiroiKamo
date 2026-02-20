package ruiseki.omoshiroikamo.module.backpack.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.item.ItemOK;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.IUpgradeWrapperFactory;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.UpgradeWrapper;

public class ItemUpgrade<T extends UpgradeWrapper> extends ItemOK implements IUpgradeWrapperFactory<T> {

    public ItemUpgrade(String name) {
        super(name);
        setNoRepair();
        setTextureName("backpack/upgrade_base");
    }

    public ItemUpgrade() {
        this(ModObject.itemUpgrade.unlocalisedName);
    }

    public boolean hasTab() {
        return false;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "backpack.upgrade_base"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public T createWrapper(ItemStack stack) {
        return (T) new UpgradeWrapper(stack);
    }
}
