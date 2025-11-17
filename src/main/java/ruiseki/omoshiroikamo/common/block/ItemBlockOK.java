package ruiseki.omoshiroikamo.common.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockOK extends ItemBlock {

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
}
