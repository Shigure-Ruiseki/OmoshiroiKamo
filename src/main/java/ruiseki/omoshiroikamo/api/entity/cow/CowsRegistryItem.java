package ruiseki.omoshiroikamo.api.entity.cow;

import javax.annotation.Nullable;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.omoshiroikamo.api.entity.BaseRegistryItem;

/**
 * Represents a single cow type registered in {@link CowsRegistry}.
 *
 * <p>
 * A cow registry item defines:
 * <ul>
 * <li>Unique ID and registry/display name</li>
 * <li>Entity texture</li>
 * <li>Primary milk fluid produced by this cow</li>
 * <li>Optional parents (used to determine tier and breeding)</li>
 * <li>Colors for GUI/JEI</li>
 * </ul>
 *
 * <p>
 * Provides helper methods to construct milk fluid stacks and customize drops.
 */
public class CowsRegistryItem extends BaseRegistryItem<CowsRegistryItem> {

    /**
     * The milk fluid this cow produces.
     */
    private FluidStack fluid;

    /**
     * Creates a new cow registry entry without parents (tier = 1).
     *
     * @param id         unique numeric ID
     * @param entityName registry and localization name
     * @param texture    texture used by the cow
     * @param fluid      base milk fluid produced by this cow
     * @param bgColor    background color for GUIs/JEI
     * @param fgColor    foreground color for GUIs/JEI
     */
    public CowsRegistryItem(int id, String entityName, ResourceLocation texture, FluidStack fluid, int bgColor,
        int fgColor) {
        this(id, entityName, texture, fluid, bgColor, fgColor, null, null);
    }

    /**
     * Full constructor with optional parents for genetic breeding.
     *
     * @param parent1 optional first parent
     * @param parent2 optional second parent
     */
    public CowsRegistryItem(int id, String entityName, ResourceLocation texture, FluidStack fluid, int bgColor,
        int fgColor, @Nullable CowsRegistryItem parent1, @Nullable CowsRegistryItem parent2) {
        super(id, entityName, texture, bgColor, fgColor, parent1, parent2);
        this.fluid = fluid;
    }

    /**
     * Creates the item dropped by this cow.
     *
     * <p>
     * By default cows drop leather unless overridden by {@link #dropItem}.
     *
     * @return new ItemStack instance of the drop item
     */
    @Override
    public ItemStack createDropItem() {
        return dropItem != null ? dropItem.copy() : new ItemStack(Items.leather);
    }

    /**
     * Sets the milk fluid this cow produces.
     *
     * @param fluidStack the base milk fluid
     * @return this item (for chaining)
     */
    public CowsRegistryItem setMilkFluid(FluidStack fluidStack) {
        this.fluid = fluidStack;
        return this;
    }

    /**
     * Creates a fresh copy of the milk fluid.
     *
     * <p>
     * Used every time a cow generates milk to prevent shared references.
     *
     * @return new FluidStack representing the milk output
     */
    public FluidStack createMilkFluid() {
        return fluid.copy();
    }
}
