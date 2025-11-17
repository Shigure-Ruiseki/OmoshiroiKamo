package ruiseki.omoshiroikamo.api.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.client.IRenderUpgrade;

public interface IAnvilUpgrade {

    String getUnlocalizedName();

    int getLevelCost();

    boolean isUpgradeItem(ItemStack stack);

    boolean canAddToItem(ItemStack stack);

    boolean hasUpgrade(ItemStack stack);

    void writeToItem(ItemStack stack);

    void removeFromItem(ItemStack stack);

    ItemStack getUpgradeItem();

    String getUpgradeItemName();

    @Nullable
    @SideOnly(Side.CLIENT)
    IRenderUpgrade getRender();

    void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag);
}
