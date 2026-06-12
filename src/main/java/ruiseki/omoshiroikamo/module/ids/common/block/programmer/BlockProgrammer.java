package ruiseki.omoshiroikamo.module.ids.common.block.programmer;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.Reference;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.block.AbstractBlock;

public class BlockProgrammer extends AbstractBlock<TEProgrammer> {

    @SideOnly(Side.CLIENT)
    private IIcon top, bottom, side;

    public BlockProgrammer() {
        super(ModObject.PROGRAMMER.name, TEProgrammer.class);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        top = reg.registerIcon(Reference.PREFIX_MOD + "ids/logic_programmer_top");
        side = reg.registerIcon(Reference.PREFIX_MOD + "ids/logic_programmer_side");
        bottom = reg.registerIcon(Reference.PREFIX_MOD + "ids/crystalized_menril_block");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int sideId, int meta) {
        switch (sideId) {
            case 0:
                return bottom;
            case 1:
                return top;
            default:
                return side;
        }
    }
}
