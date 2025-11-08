package ruiseki.omoshiroikamo.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;

import com.enderio.core.common.BlockEnder;
import com.enderio.core.common.TileEntityEnder;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.OKCreativeTab;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class BlockOK extends BlockEnder {

    protected String textureName;

    public BlockOK(ModObject modObject) {
        this(modObject, null, Material.iron);
    }

    public BlockOK(ModObject modObject, Material material) {
        this(modObject, null, material);
    }

    public BlockOK(ModObject modObject, Class<? extends TileEntityEnder> teClass) {
        this(modObject, teClass, Material.iron);
    }

    public BlockOK(ModObject modObject, Class<? extends TileEntityEnder> teClass, Material material) {
        super(modObject.unlocalisedName, teClass, material);
        OKCreativeTab.addToTab(this);
    }

    public BlockOK setTextureName(String texture) {
        this.textureName = texture;
        return this;
    }

    @Override
    public String getTextureName() {
        return textureName;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        if (getTextureName() != null) {
            blockIcon = reg.registerIcon(LibResources.PREFIX_MOD + getTextureName());
        } else {
            blockIcon = reg.registerIcon(LibResources.PREFIX_MOD + name);
        }
    }

    @Override
    public void init() {
        super.init();
    }
}
