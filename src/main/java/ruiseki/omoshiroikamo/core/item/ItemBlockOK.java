package ruiseki.omoshiroikamo.core.item;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.core.block.IBlockRarityProvider;

public class ItemBlockOK extends ItemBlockWithMetadata {

    protected IBlockRarityProvider rarityProvider = null;

    public ItemBlockOK(Block block) {
        super(block, block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        if (block instanceof IBlockRarityProvider) {
            this.rarityProvider = (IBlockRarityProvider) this.field_150939_a;
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack itemStack) {
        if (rarityProvider != null) {
            return rarityProvider.getRarity(itemStack);
        }
        return super.getRarity(itemStack);
    }
}
