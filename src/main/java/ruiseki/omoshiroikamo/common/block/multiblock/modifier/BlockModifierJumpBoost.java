package ruiseki.omoshiroikamo.common.block.multiblock.modifier;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.AttributeEnergyCostFixed;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.common.init.ModifierAttribute;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class BlockModifierJumpBoost extends BlockModifier {

    protected BlockModifierJumpBoost() {
        super(ModObject.blockModifierJumpBoost, "jump_boost");
    }

    public static BlockModifierJumpBoost create() {
        return new BlockModifierJumpBoost();
    }

    @Override
    public void addAttributes(List<IModifierAttribute> list) {
        list.add(ModifierAttribute.P_JUMP_BOOST.getAttribute());
        list.add(new AttributeEnergyCostFixed(128));
    }

    @Override
    public void registerBlockIcons(IIconRegister iIconRegister) {
        blockIcon = iIconRegister.registerIcon(LibResources.PREFIX_MOD + "modifier_jump_boost");
    }
}
