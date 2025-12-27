package ruiseki.omoshiroikamo.core.common.block.abstractClass;

import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;

public abstract class AbstractTieredMBBlock<T extends AbstractMBModifierTE> extends AbstractMBBlock<T> {

    private final Class<? extends TileEntity>[] teClasses;

    @SafeVarargs
    @SuppressWarnings("unchecked")
    protected AbstractTieredMBBlock(String name, Class<? extends TileEntity>... teClasses) {
        super(name, (Class<T>) teClasses[0]);
        this.teClasses = teClasses;
    }

    @Override
    public void init() {
        registerBlock();

        for (Class<? extends TileEntity> teClass : teClasses) {
            // Naming convention: ClassSimpleName + "TileEntity"
            // Example: "TEQuantumOreExtractorT1TileEntity"
            GameRegistry.registerTileEntity(teClass, teClass.getSimpleName() + "TileEntity");
        }

        registerBlockColor();
    }

    protected void registerBlock() {
        GameRegistry.registerBlock(this, getItemBlockClass(), name);
    }

    protected abstract Class<? extends ItemBlock> getItemBlockClass();

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        if (meta >= 0 && meta < teClasses.length) {
            try {
                return teClasses[meta].newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
