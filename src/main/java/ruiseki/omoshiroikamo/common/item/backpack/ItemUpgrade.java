package ruiseki.omoshiroikamo.common.item.backpack;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.backpack.capabilities.IUpgrade;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.util.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class ItemUpgrade extends ItemOK implements IUpgrade {

    public ItemUpgrade(String name) {
        super(name);
        setNoRepair();
        setTextureName("upgrade_base");
    }

    public ItemUpgrade(ModObject modObject) {
        this(modObject.unlocalisedName);
    }

    public ItemUpgrade() {
        this(ModObject.itemUpgrade);
    }

    public boolean hasTab() {
        return false;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "upgrade_base"));
        list.add(
            ItemNBTUtils.getNBT(itemstack)
                .toString());
    }

    @Override
    public void setTabOpened(ItemStack upgrade, boolean opened) {
        NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
        tag.setBoolean(TAB_STATE_TAG, opened);
    }

    @Override
    public boolean getTabOpened(ItemStack upgrade) {
        NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
        return tag.hasKey(TAB_STATE_TAG) && tag.getBoolean(TAB_STATE_TAG);
    }
}
