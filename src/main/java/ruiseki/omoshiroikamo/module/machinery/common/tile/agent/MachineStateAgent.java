package ruiseki.omoshiroikamo.module.machinery.common.tile.agent;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.core.IMachineState;
import ruiseki.omoshiroikamo.api.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.core.gas.IGasHandler;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.spark.ISparkAttachable;

/**
 * Agent responsible for tracking and reporting the machine's state and
 * statistics.
 * Delegates dynamic state (energy, progress) to the controller, but owns
 * statistical data.
 */
public class MachineStateAgent implements IMachineState {

    private final TEMachineController controller;

    private int recipeProcessedCount = 0;
    private final Set<String> processedRecipeTypes = new HashSet<>();

    public MachineStateAgent(TEMachineController controller) {
        this.controller = controller;
    }

    // --- IMachineState Implementation ---

    @Override
    public long getStoredEnergy() {
        return controller.getEnergyStored();
    }

    @Override
    public long getEnergyCapacity() {
        return controller.getMaxEnergyStored();
    }

    @Override
    public int getEnergyPerTick() {
        return controller.getEnergyPerTick();
    }

    @Override
    public double getProgressPercent() {
        return controller.getProgressPercent();
    }

    @Override
    public boolean isRunning() {
        return controller.isRunning();
    }

    @Override
    public boolean isWaitingForOutput() {
        return controller.isWaitingForOutput();
    }

    @Override
    public int getTier() {
        return controller.getTier();
    }

    @Override
    public long getTimePlaced() {
        return controller.getTimePlaced();
    }

    @Override
    public long getTimeContinuous() {
        return controller.getTimeContinuous();
    }

    @Override
    public int getRecipeProcessedCount() {
        return recipeProcessedCount;
    }

    @Override
    public int getRecipeProcessedTypesCount() {
        return processedRecipeTypes.size();
    }

    // --- Statistics Recording ---

    /**
     * Records the completion of a recipe to update statistics.
     */
    public void recordRecipeCompletion(IModularRecipe recipe) {
        if (recipe == null) return;
        recipeProcessedCount++;
        if (recipe.getRecipeGroup() != null) {
            processedRecipeTypes.add(recipe.getRecipeGroup());
        }
    }

    // --- NBT Persistence ---

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("recipeProcessedCount", recipeProcessedCount);
        if (!processedRecipeTypes.isEmpty()) {
            NBTTagList typeList = new NBTTagList();
            for (String type : processedRecipeTypes) {
                typeList.appendTag(new NBTTagString(type));
            }
            nbt.setTag("processedRecipeTypes", typeList);
        }
    }

    public void readFromNBT(NBTTagCompound nbt) {
        recipeProcessedCount = nbt.getInteger("recipeProcessedCount");
        processedRecipeTypes.clear();
        if (nbt.hasKey("processedRecipeTypes", 9)) {
            NBTTagList typeList = nbt.getTagList("processedRecipeTypes", 8);
            for (int i = 0; i < typeList.tagCount(); i++) {
                processedRecipeTypes.add(typeList.getStringTagAt(i));
            }
        }
    }

    @Override
    public long getStoredFluid() {
        long total = 0;
        for (IModularPort port : controller.getPortManager()
            .getInputPorts(IPortType.Type.FLUID)) {
            if (!(port instanceof IFluidHandler fluidPort)) continue;
            FluidTankInfo[] tanks = fluidPort.getTankInfo(ForgeDirection.UNKNOWN);
            if (tanks == null) continue;
            for (FluidTankInfo tank : tanks) {
                if (tank.fluid != null) total += tank.fluid.amount;
            }
        }
        return total;
    }

    @Override
    public long getFluidCapacity() {
        long total = 0;
        for (IModularPort port : controller.getPortManager()
            .getInputPorts(IPortType.Type.FLUID)) {
            if (!(port instanceof IFluidHandler fluidPort)) continue;
            FluidTankInfo[] tanks = fluidPort.getTankInfo(ForgeDirection.UNKNOWN);
            if (tanks == null) continue;
            for (FluidTankInfo tank : tanks) {
                total += tank.capacity;
            }
        }
        return total;
    }

    @Override
    public long getStoredFluid(String name) {
        if (name == null || name.isEmpty()) return 0;
        long total = 0;
        for (IModularPort port : controller.getPortManager()
            .getInputPorts(IPortType.Type.FLUID)) {
            if (!(port instanceof IFluidHandler fluidPort)) continue;
            FluidTankInfo[] tanks = fluidPort.getTankInfo(ForgeDirection.UNKNOWN);
            if (tanks == null) continue;
            for (FluidTankInfo tank : tanks) {
                if (tank.fluid != null && tank.fluid.getFluid()
                    .getName()
                    .equals(name)) {
                    total += tank.fluid.amount;
                }
            }
        }
        return total;
    }

    @Override
    public long getStoredMana() {
        return controller.getPortManager()
            .getInputPorts(IPortType.Type.MANA)
            .stream()
            .filter(p -> p instanceof IManaPool)
            .map(p -> (IManaPool) p)
            .mapToLong(p -> (long) p.getCurrentMana())
            .sum();
    }

    @Override
    public long getManaCapacity() {
        return controller.getPortManager()
            .getInputPorts(IPortType.Type.MANA)
            .stream()
            .filter(p -> p instanceof IManaPool)
            .mapToLong(p -> {
                long current = (long) ((IManaPool) p).getCurrentMana();
                if (p instanceof ISparkAttachable) {
                    return current + (long) ((ISparkAttachable) p).getAvailableSpaceForMana();
                }
                return current;
            })
            .sum();
    }

    @Override
    public long getStoredGas(String name) {
        if (name == null || name.isEmpty()) return 0;
        return controller.getPortManager()
            .getInputPorts(IPortType.Type.GAS)
            .stream()
            .filter(p -> p instanceof IGasHandler)
            .map(p -> (IGasHandler) p)
            .mapToLong(p -> {
                var drawn = p.drawGas(ForgeDirection.UNKNOWN, Integer.MAX_VALUE, false);
                if (drawn != null && drawn.getGas()
                    .getName()
                    .equals(name)) {
                    return drawn.amount;
                }
                return 0;
            })
            .sum();
    }

    @Override
    public long getTotalStoredGas() {
        return controller.getPortManager()
            .getInputPorts(IPortType.Type.GAS)
            .stream()
            .filter(p -> p instanceof IGasHandler)
            .map(p -> (IGasHandler) p)
            .mapToLong(p -> {
                var drawn = p.drawGas(ForgeDirection.UNKNOWN, Integer.MAX_VALUE, false);
                return drawn != null ? drawn.amount : 0;
            })
            .sum();
    }

    @Override
    public long getGasCapacity() {
        // IGasHandler might not have capacity getter, checking...
        // Assuming some limit or just return total stored + free space if possible.
        // For simplicity, sum up amounts for now.
        return getTotalStoredGas(); // TODO: Need capacity if available in IGasHandler
    }

    @Override
    public long getStoredEssentia(String aspectName) {
        Aspect aspect = Aspect.getAspect(aspectName);
        if (aspect == null) return 0;
        return controller.getPortManager()
            .getInputPorts(IPortType.Type.ESSENTIA)
            .stream()
            .filter(p -> p instanceof IAspectContainer)
            .map(p -> (IAspectContainer) p)
            .mapToLong(p -> (long) p.containerContains(aspect))
            .sum();
    }

    @Override
    public long getEssentiaCapacity() {
        // IAspectContainer doesn't have a direct capacity getter in all versions,
        // but we can sum up if known.
        return 0; // TODO: Implement if needed
    }

    @Override
    public long getStoredVis(String aspectName) {
        Aspect aspect = Aspect.getAspect(aspectName);
        if (aspect == null) return 0;
        return controller.getPortManager()
            .getInputPorts(IPortType.Type.VIS)
            .stream()
            .filter(p -> p instanceof IAspectContainer)
            .map(p -> (IAspectContainer) p)
            .mapToLong(p -> (long) p.containerContains(aspect))
            .sum();
    }

    @Override
    public long getVisCapacity() {
        return 0;
    }
}
