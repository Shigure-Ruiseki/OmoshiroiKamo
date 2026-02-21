package ruiseki.omoshiroikamo.module.multiblock.common.block;

import net.minecraft.item.ItemStack;

import com.gtnewhorizon.structurelib.alignment.constructable.IMultiblockInfoContainer;
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;

import ruiseki.omoshiroikamo.core.tileentity.AbstractMBModifierTE;

public class TieredMultiblockInfoContainer<T extends AbstractMBModifierTE> implements IMultiblockInfoContainer<T> {

    private final IStructureDefinition<T> structure;

    public TieredMultiblockInfoContainer(IStructureDefinition<T> structure) {
        this.structure = structure;
    }

    @Override
    public void construct(ItemStack triggerStack, boolean hintsOnly, T ctx, ExtendedFacing aSide) {
        int tier = ctx.getTier();
        int[][] offsets = ctx.getOffSet();

        // Safety check to prevent IndexOutOfBoundsException if tier is incorrectly set
        if (tier < 1 || tier > offsets.length) {
            return;
        }

        this.structure.buildOrHints(
            ctx,
            triggerStack,
            ctx.getStructurePieceName(),
            ctx.getWorldObj(),
            ExtendedFacing.DEFAULT,
            ctx.xCoord,
            ctx.yCoord,
            ctx.zCoord,
            offsets[tier - 1][0],
            offsets[tier - 1][1],
            offsets[tier - 1][2],
            hintsOnly);
    }

    @Override
    public int survivalConstruct(ItemStack triggerStack, int elementBudget, ISurvivalBuildEnvironment env, T ctx,
        ExtendedFacing aSide) {
        int tier = ctx.getTier();
        int[][] offsets = ctx.getOffSet();

        if (tier < 1 || tier > offsets.length) {
            return 0;
        }

        return this.structure.survivalBuild(
            ctx,
            triggerStack,
            ctx.getStructurePieceName(),
            ctx.getWorldObj(),
            ExtendedFacing.DEFAULT,
            ctx.xCoord,
            ctx.yCoord,
            ctx.zCoord,
            offsets[tier - 1][0],
            offsets[tier - 1][1],
            offsets[tier - 1][2],
            elementBudget,
            env,
            false);
    }

    @Override
    public String[] getDescription(ItemStack stackSize) {
        return new String[0];
    }
}
