package ruiseki.omoshiroikamo.core.integration.nei.modular;

import net.minecraft.item.ItemStack;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * IConstructable implementation for each custom structure.
 * Used by the ModularMachine NEI handler to display structure previews.
 */
public class CustomStructureConstructable implements ISurvivalConstructable {

    private final String structureName;
    private final IStructureDefinition<TEMachineController> definition;
    private final int[] offset;

    public CustomStructureConstructable(String name, IStructureDefinition<TEMachineController> def, int[] offset) {
        this.structureName = name;
        this.definition = def;
        this.offset = offset != null ? offset : new int[] { 0, 0, 0 };
    }

    public String getStructureName() {
        return structureName;
    }

    public IStructureDefinition<TEMachineController> getDefinition() {
        return definition;
    }

    public int[] getOffset() {
        return offset;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        // This is called by the GuiHandler during preview rendering.
        // The actual block placement is handled by
        // ModularMachineGuiHandler.placeMultiblock()
    }

    @Override
    public IStructureDefinition<?> getStructureDefinition() {
        return definition;
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        // NEI preview doesn't need survival construction
        return -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String[] getStructureDescription(ItemStack stackSize) {
        return new String[] { "Structure: " + structureName };
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CustomStructureConstructable other)) return false;
        return structureName.equals(other.structureName);
    }

    @Override
    public int hashCode() {
        return structureName.hashCode();
    }
}
