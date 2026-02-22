package ruiseki.omoshiroikamo.core.item;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;

import ruiseki.omoshiroikamo.core.block.BlockOK;
import ruiseki.omoshiroikamo.core.block.IBlockRarityProvider;
import ruiseki.omoshiroikamo.core.client.render.item.ItemRenderer;
import ruiseki.omoshiroikamo.core.helper.MinecraftHelpers;

public class ItemBlockOK extends ItemBlockWithMetadata {

    protected IBlockRarityProvider rarityProvider = null;

    private final BlockOK blockType;

    public ItemBlockOK(Block block) {
        super(block, block);

        this.blockType = (BlockOK) block;
        this.hasSubtypes = this.blockType.hasSubtypes;
        this.setHasSubtypes(hasSubtypes);
        if (block instanceof IBlockRarityProvider) {
            this.rarityProvider = (IBlockRarityProvider) this.field_150939_a;
        }

        if (MinecraftHelpers.isClientSide()) {
            MinecraftForgeClient.registerItemRenderer(this, ItemRenderer.INSTANCE);
        }
    }

    @Override
    public int getMetadata(final int meta) {
        if (this.hasSubtypes) {
            return meta;
        }
        return 0;
    }

    @Override
    public EnumRarity getRarity(ItemStack itemStack) {
        if (rarityProvider != null) {
            return rarityProvider.getRarity(itemStack);
        }
        return super.getRarity(itemStack);
    }
}
