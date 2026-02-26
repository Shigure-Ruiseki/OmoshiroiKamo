package ruiseki.omoshiroikamo.module.machinery.common.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.item.ItemBlockOK;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;

public abstract class AbstractPortItemBlock extends ItemBlockOK {

    public AbstractPortItemBlock(Block block) {
        super(block);
        hasSubtypes = true;
    }

    public IIcon getOverlayIcon(int tier) {
        if (field_150939_a instanceof AbstractPortBlock) {
            return IconRegistry.getIcon(((AbstractPortBlock<?>) field_150939_a).getOverlayPrefix() + tier);
        }
        return null;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int tier = stack.getItemDamage() + 1;
        return super.getUnlocalizedName() + ".tier_" + tier;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        if (field_150939_a instanceof AbstractPortBlock) {
            ((AbstractPortBlock<?>) field_150939_a).addTooltip(list, stack.getItemDamage() + 1);
        }
    }
}
