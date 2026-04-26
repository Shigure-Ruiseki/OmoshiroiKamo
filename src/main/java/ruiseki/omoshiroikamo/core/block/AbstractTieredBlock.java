package ruiseki.omoshiroikamo.core.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.apache.logging.log4j.Level;

import com.gtnewhorizon.gtnhlib.client.model.color.IBlockColor;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.tileentity.AbstractTE;

public abstract class AbstractTieredBlock<T extends AbstractTE> extends AbstractBlock<T> implements IBlockColor {

    private final Class<? extends TileEntity>[] teClasses;

    @SafeVarargs
    @SuppressWarnings("unchecked")
    protected AbstractTieredBlock(String name, Class<? extends TileEntity>... teClasses) {
        super(name, (Class<T>) teClasses[0]);
        this.teClasses = teClasses;
    }

    @Override
    protected void registerTileEntity() {
        for (Class<? extends TileEntity> teClass : teClasses) {
            // Naming convention: ClassSimpleName + "TileEntity"
            // Example: "TEQuantumOreExtractorT1TileEntity"
            GameRegistry.registerTileEntity(teClass, teClass.getSimpleName() + "TileEntity");
        }
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        if (meta >= 0 && meta < teClasses.length) {
            try {
                return teClasses[meta].getDeclaredConstructor()
                    .newInstance();
            } catch (Exception e) {
                OmoshiroiKamo
                    .okLog(Level.ERROR, "Could not create tile entity for block " + name + " for class " + teClass);
            }
        }
        return null;
    }
}
