package ruiseki.omoshiroikamo.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockOK extends ItemBlock implements IAdvancedTooltipProvider {

    private final Block field_150939_b;

    public ItemBlockOK(Block blockA, Block blockB) {
        super(blockA);
        this.field_150939_b = blockB;
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    public ItemBlockOK(Block blockA) {
        super(blockA);
        this.field_150939_b = null;
        this.setHasSubtypes(true);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int meta) {
        if (this.field_150939_b != null) {
            return this.field_150939_b.getIcon(2, meta);
        }
        return this.field_150939_a.getIcon(2, meta);
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

    @Override
    public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {

    }

    @Override
    public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {

    }

    @Override
    public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {

    }
}
