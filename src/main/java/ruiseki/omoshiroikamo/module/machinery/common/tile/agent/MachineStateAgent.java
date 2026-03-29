package ruiseki.omoshiroikamo.module.machinery.common.tile.agent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.ToLongFunction;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.oredict.OreDictionary;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.core.IMachineState;
import ruiseki.omoshiroikamo.api.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.core.gas.GasTankInfo;
import ruiseki.omoshiroikamo.core.gas.IGasHandler;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;
import ruiseki.omoshiroikamo.module.machinery.common.tile.essentia.AbstractEssentiaPortTE;
import ruiseki.omoshiroikamo.module.machinery.common.tile.vis.AbstractVisPortTE;
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
    public long getProgress() {
        return controller.getProcessAgent()
            .getProgress();
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

    // --- Generic Helper Methods ---

    /**
     * Generic helper to sum a property from all ports of a specific type.
     *
     * @param portType  The port type to query
     * @param portClass The expected port interface class
     * @param extractor Function to extract the value from each port
     * @param <T>       The port type
     * @return The sum of all extracted values
     */
    private <T> long sumPortProperty(IPortType.Type portType, Class<T> portClass, ToLongFunction<T> extractor) {
        return controller.getPortManager()
            .getInputPorts(portType)
            .stream()
            .filter(portClass::isInstance)
            .map(portClass::cast)
            .mapToLong(extractor)
            .sum();
    }

    /**
     * Generic helper for named resources (gas, fluid, essentia, vis).
     *
     * @param portType  The port type to query
     * @param portClass The expected port interface class
     * @param name      The resource name
     * @param extractor Function to extract amount for the specific name
     * @param <T>       The port type
     * @return The sum of amounts for the named resource
     */
    private <T> long sumNamedResource(IPortType.Type portType, Class<T> portClass, String name,
        BiFunction<T, String, Long> extractor) {
        if (name == null || name.isEmpty()) return 0;
        return controller.getPortManager()
            .getInputPorts(portType)
            .stream()
            .filter(portClass::isInstance)
            .map(portClass::cast)
            .mapToLong(port -> extractor.apply(port, name))
            .sum();
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
    public long getFluidInput(String name) {
        return getStoredFluid(name);
    }

    @Override
    public long getFluidOutputSpace(String name) {
        if (name == null || name.isEmpty()) return 0;
        long total = 0;
        for (IModularPort port : controller.getPortManager()
            .getOutputPorts(IPortType.Type.FLUID)) {
            if (!(port instanceof IFluidHandler fluidPort)) continue;
            FluidTankInfo[] tanks = fluidPort.getTankInfo(ForgeDirection.UNKNOWN);
            if (tanks == null) continue;
            for (FluidTankInfo tank : tanks) {
                if (tank.fluid == null || tank.fluid.getFluid()
                    .getName()
                    .equals(name)) {
                    total += Math.max(0, tank.capacity - (tank.fluid != null ? tank.fluid.amount : 0));
                }
            }
        }
        return total;
    }

    @Override
    public long getStoredMana() {
        return sumPortProperty(IPortType.Type.MANA, IManaPool.class, p -> (long) p.getCurrentMana());
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
        return sumNamedResource(IPortType.Type.GAS, IGasHandler.class, name, (port, gasName) -> {
            var drawn = port.drawGas(ForgeDirection.UNKNOWN, Integer.MAX_VALUE, false);
            return (drawn != null && drawn.getGas()
                .getName()
                .equals(gasName)) ? (long) drawn.amount : 0L;
        });
    }

    @Override
    public long getTotalStoredGas() {
        return sumPortProperty(IPortType.Type.GAS, IGasHandler.class, p -> {
            var drawn = p.drawGas(ForgeDirection.UNKNOWN, Integer.MAX_VALUE, false);
            return drawn != null ? drawn.amount : 0;
        });
    }

    @Override
    public long getGasCapacity() {
        long total = 0;
        for (IModularPort port : controller.getPortManager()
            .getInputPorts(IPortType.Type.GAS)) {
            if (!(port instanceof IGasHandler gasPort)) continue;
            GasTankInfo[] tankInfo = gasPort.getTankInfo(ForgeDirection.UNKNOWN);
            if (tankInfo != null) {
                for (GasTankInfo info : tankInfo) {
                    total += info.capacity;
                }
            }
        }
        return total;
    }

    @Override
    public long getStoredEssentia(String aspectName) {
        Aspect aspect = Aspect.getAspect(aspectName);
        if (aspect == null) return 0;
        return sumPortProperty(
            IPortType.Type.ESSENTIA,
            IAspectContainer.class,
            p -> (long) p.containerContains(aspect));
    }

    @Override
    public long getEssentiaCapacity() {
        return sumPortProperty(
            IPortType.Type.ESSENTIA,
            AbstractEssentiaPortTE.class,
            p -> (long) p.getMaxCapacityPerAspect());
    }

    @Override
    public long getStoredVis(String aspectName) {
        Aspect aspect = Aspect.getAspect(aspectName);
        if (aspect == null) return 0;
        return sumPortProperty(IPortType.Type.VIS, IAspectContainer.class, p -> (long) p.containerContains(aspect));
    }

    @Override
    public long getVisCapacity() {
        return sumPortProperty(IPortType.Type.VIS, AbstractVisPortTE.class, p -> (long) p.getMaxVisPerAspect());
    }

    @Override
    public int getBatchSize() {
        return controller.getBatchSize();
    }

    @Override
    public double getSpeedMultiplier() {
        return controller.getSpeedMultiplier();
    }

    @Override
    public double getEnergyMultiplier() {
        return controller.getEnergyMultiplier();
    }

    @Override
    public long getRecipeStartTick() {
        return controller.getRecipeStartTick();
    }

    @Override
    public long getItemCount(IPortType.Direction direction, String itemName) {
        long total = 0;
        for (IModularPort port : getPorts(direction)) {
            if (!(port instanceof IInventory inv)) continue;
            for (int i = 0; i < inv.getSizeInventory(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack != null && matches(stack, itemName)) {
                    total += stack.stackSize;
                }
            }
        }
        return total;
    }

    @Override
    public long getItemSpace(IPortType.Direction direction, String itemName) {
        long total = 0;
        ItemStack filterStack = null;
        if (itemName != null && !itemName.isEmpty() && !itemName.startsWith("ore:")) {
            Item item = (Item) Item.itemRegistry.getObject(itemName);
            if (item != null) filterStack = new ItemStack(item);
        }

        for (IModularPort port : getPorts(direction)) {
            if (!(port instanceof IInventory inv)) continue;
            for (int i = 0; i < inv.getSizeInventory(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack == null) {
                    // Empty slot: if specific item, use its max stack size, else use 64
                    total += (filterStack != null) ? filterStack.getMaxStackSize() : 64;
                } else if (filterStack != null && stack.isItemEqual(filterStack)
                    && ItemStack.areItemStackTagsEqual(stack, filterStack)) {
                        // Same item: add remaining space in stack
                        total += Math.max(0, stack.getMaxStackSize() - stack.stackSize);
                    }
            }
        }
        return total;
    }

    @Override
    public int getItemSlotCount(IPortType.Direction direction, boolean emptyOnly) {
        int total = 0;
        for (IModularPort port : getPorts(direction)) {
            if (!(port instanceof IInventory inv)) continue;
            if (emptyOnly) {
                for (int i = 0; i < inv.getSizeInventory(); i++) {
                    if (inv.getStackInSlot(i) == null) total++;
                }
            } else {
                total += inv.getSizeInventory();
            }
        }
        return total;
    }

    private List<IModularPort> getPorts(IPortType.Direction direction) {
        if (direction == IPortType.Direction.INPUT) {
            return controller.getPortManager()
                .getInputPorts(IPortType.Type.ITEM);
        } else if (direction == IPortType.Direction.OUTPUT) {
            return controller.getPortManager()
                .getOutputPorts(IPortType.Type.ITEM);
        } else {
            List<IModularPort> all = new ArrayList<>();
            all.addAll(
                controller.getPortManager()
                    .getInputPorts(IPortType.Type.ITEM));
            all.addAll(
                controller.getPortManager()
                    .getOutputPorts(IPortType.Type.ITEM));
            return all;
        }
    }

    private boolean matches(ItemStack stack, String filter) {
        if (filter == null || filter.isEmpty()) return true;

        if (filter.startsWith("ore:")) {
            String oreName = filter.substring(4);
            int oreId = OreDictionary.getOreID(oreName);
            if (oreId < 0) return false;
            int[] ids = OreDictionary.getOreIDs(stack);
            for (int id : ids) if (id == oreId) return true;
            return false;
        } else {
            String name = Item.itemRegistry.getNameForObject(stack.getItem());
            return filter.equals(name);
        }
    }
}
