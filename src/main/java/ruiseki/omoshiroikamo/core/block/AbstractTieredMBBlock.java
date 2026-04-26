package ruiseki.omoshiroikamo.core.block;

import static com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry.registerProperty;

import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.apache.logging.log4j.Level;

import com.gtnewhorizon.gtnhlib.blockstate.properties.IntegerBlockProperty;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.api.client.render.IJsonModelBlock;
import ruiseki.omoshiroikamo.core.tileentity.AbstractMBModifierTE;

public abstract class AbstractTieredMBBlock<T extends AbstractMBModifierTE> extends AbstractMBBlock<T>
    implements IJsonModelBlock {

    private final Class<? extends TileEntity>[] teClasses;

    @SafeVarargs
    @SuppressWarnings("unchecked")
    protected AbstractTieredMBBlock(String name, Class<? extends TileEntity>... teClasses) {
        super(name, (Class<T>) teClasses[0]);
        this.teClasses = teClasses;
        this.hasSubtypes = true;
    }

    public static final IntegerBlockProperty TIER = IntegerBlockProperty.meta("tier", 0b0111, 0);

    @Override
    public void registerProperties() {
        super.registerProperties();
        registerProperty(this, TIER);
        registerProperty(Item.getItemFromBlock(this), TIER);
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
    public String getTextureName() {
        return "multiblock/" + name;
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
