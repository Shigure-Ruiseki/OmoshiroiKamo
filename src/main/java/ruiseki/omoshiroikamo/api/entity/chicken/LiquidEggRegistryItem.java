package ruiseki.omoshiroikamo.api.entity.chicken;

import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;

/**
 * Represents a single liquid-egg type in the {@link LiquidEggRegistry}.
 *
 * <p>
 * A liquid egg defines:
 * <ul>
 * <li>A unique numeric ID</li>
 * <li>The {@link Block} associated with the liquid form of this egg</li>
 * <li>The color to render the egg with</li>
 * <li>The {@link Fluid} contained inside the egg</li>
 * </ul>
 *
 * <p>
 * This data is used when:
 * <ul>
 * <li>Rendering special liquid egg items</li>
 * <li>Spawning fluid blocks when an egg breaks</li>
 * <li>Lookup for crafting/JEI integrations</li>
 * </ul>
 */
public class LiquidEggRegistryItem {

    /**
     * Unique ID of this liquid egg entry.
     */
    private final int id;

    /**
     * The block representing the liquid created by this egg.
     */
    private final Block liquid;

    /**
     * Display/render color of the egg.
     */
    private final int eggColor;

    /**
     * The fluid stored or represented by this egg.
     */
    private final Fluid fluid;

    /**
     * Creates a new liquid egg registry item.
     *
     * @param id       unique identifier
     * @param liquid   block form of the egg's liquid
     * @param eggColor color used when rendering the egg item
     * @param fluid    fluid contained or produced by this egg
     */
    public LiquidEggRegistryItem(int id, Block liquid, int eggColor, Fluid fluid) {
        this.id = id;
        this.liquid = liquid;
        this.eggColor = eggColor;
        this.fluid = fluid;
    }

    /**
     * @return unique ID of this registry item
     */
    public int getId() {
        return id;
    }

    /**
     * @return the block representing the egg's liquid form
     */
    public Block getLiquid() {
        return liquid;
    }

    /**
     * @return the render color used for this egg
     */
    public int getEggColor() {
        return eggColor;
    }

    /**
     * @return the fluid contained or produced by this egg
     */
    public Fluid getFluid() {
        return fluid;
    }
}
