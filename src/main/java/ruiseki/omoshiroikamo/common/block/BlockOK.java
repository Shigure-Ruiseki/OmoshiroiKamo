package ruiseki.omoshiroikamo.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;

import com.enderio.core.common.BlockEnder;
import com.enderio.core.common.TileEntityEnder;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class BlockOK extends BlockEnder {

    protected String name;
    protected String textureName;

    public BlockOK(ModObject modObject) {
        super(modObject.unlocalisedName, null, Material.iron);
        this.name = modObject.unlocalisedName;
    }

    public BlockOK(ModObject modObject, Material material) {
        super(modObject.unlocalisedName, null, material);
        this.name = modObject.unlocalisedName;
    }

    public BlockOK(ModObject modObject, Class<? extends TileEntityEnder> teClass) {
        super(modObject.unlocalisedName, teClass, Material.iron);
        this.name = modObject.unlocalisedName;
    }

    public BlockOK(ModObject modObject, Class<? extends TileEntityEnder> teClass, Material material) {
        super(modObject.unlocalisedName, teClass, material);
        this.name = modObject.unlocalisedName;
    }

    public String getName() {
        return name;
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
