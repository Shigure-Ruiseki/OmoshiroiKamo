package ruiseki.omoshiroikamo.module.machinery.common.item;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.core.common.block.ItemBlockOK;

public abstract class AbstractPortItemBlock extends ItemBlockOK {

    public AbstractPortItemBlock(Block block) {
        this(block, block);
    }

    public AbstractPortItemBlock(Block blockA, Block blockB) {
        super(blockA, blockB);
        setHasSubtypes(true);
    }

    public abstract IIcon getOverlayIcon(int tier);

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderPasses(int metadata) {
        return 2;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int meta, int pass) {
        if (pass == 0) {
            return Block.getBlockFromItem(this)
                .getIcon(0, meta);
        }
        return getOverlayIcon(meta + 1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return getIconFromDamageForRenderPass(meta, 0);
    }
}
