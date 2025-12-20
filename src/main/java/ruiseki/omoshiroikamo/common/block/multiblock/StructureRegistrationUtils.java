package ruiseki.omoshiroikamo.common.block.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;

import java.util.function.Consumer;

import net.minecraft.block.Block;

import com.gtnewhorizon.structurelib.alignment.constructable.IMultiblockInfoContainer;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractMBModifierTE;
import ruiseki.omoshiroikamo.common.init.ModBlocks;

public class StructureRegistrationUtils {

    /**
     * Registers a single tier of a multiblock structure.
     * <p>
     * This method creates a builder, adds the shape, adds common elements like the
     * controller ('Q')
     * and structure frames ('F'), allows adding custom elements, builds the
     * definition,
     * and registers it with the TieredMultiblockInfoContainer.
     *
     * @param <T>             The TileEntity type.
     * @param tileClass       The class of the TileEntity.
     * @param shape           The shape array (before transposition).
     * @param shapeName       The name of the shape (e.g.,
     *                        SolarArrayShapes.STRUCTURE_TIER_1).
     * @param controllerBlock The block used for the controller ('Q').
     * @param tier            The tier number (1-6). Used for metadata of controller
     *                        and frame blocks.
     * @param elementAdder    A consumer to add structure-specific elements (e.g.,
     *                        'P', 'A', 'L') to the builder.
     * @return The built IStructureDefinition.
     */
    public static <T extends AbstractMBModifierTE> IStructureDefinition<T> registerTier(Class<T> tileClass,
        String[][] shape, String shapeName, Block controllerBlock, int tier,
        Consumer<StructureDefinition.Builder<T>> elementAdder) {

        StructureDefinition.Builder<T> builder = StructureDefinition.builder();

        // Add Shape
        builder.addShape(shapeName, transpose(shape));

        // Add Controller ('Q') - Meta is usually tier - 1
        builder.addElement('Q', ofBlock(controllerBlock, tier - 1));

        // Add Structure Frames ('F') - Meta is usually tier - 1
        // Used by all 4 structures: Basalt, Hardened, Alabaster
        builder.addElement(
            'F',
            ofChain(
                ofBlock(ModBlocks.BASALT_STRUCTURE.get(), tier - 1),
                ofBlock(ModBlocks.HARDENED_STRUCTURE.get(), tier - 1),
                ofBlock(ModBlocks.ALABASTER_STRUCTURE.get(), tier - 1)));

        // Add Custom Elements
        if (elementAdder != null) {
            elementAdder.accept(builder);
        }

        // Build Definition
        IStructureDefinition<T> definition = builder.build();

        // Register with Container
        IMultiblockInfoContainer.registerTileClass(tileClass, new TieredMultiblockInfoContainer<>(definition));

        return definition;
    }
}
