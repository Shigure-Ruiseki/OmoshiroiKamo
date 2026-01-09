package ruiseki.omoshiroikamo.module.machinery.common.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.recipe.EnergyInput;
import ruiseki.omoshiroikamo.api.modular.recipe.EssentiaOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.FluidOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.GasOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.IRecipeInput;
import ruiseki.omoshiroikamo.api.modular.recipe.IRecipeOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.ItemOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.ManaOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.ModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.VisOutput;

/**
 * Manages the processing of a single recipe.
 * Handles tick-by-tick progression, energy consumption, and output generation.
 */
public class ProcessAgent {

    private int progress;
    private int maxProgress;
    private int energyPerTick;
    private boolean running;
    private boolean waitingForOutput;

    // Cached outputs for NBT persistence
    private List<ItemStack> cachedItemOutputs = new ArrayList<>();
    private List<FluidStack> cachedFluidOutputs = new ArrayList<>();
    // Simple outputs (amount only, no complex data)
    private List<Integer> cachedManaOutputs = new ArrayList<>();
    private List<String[]> cachedGasOutputs = new ArrayList<>(); // [gasName, amount]
    private List<String[]> cachedEssentiaOutputs = new ArrayList<>(); // [aspectTag, amount]
    private List<String[]> cachedVisOutputs = new ArrayList<>(); // [aspectTag, amountCentiVis]

    // Transient reference to current recipe
    private transient ModularRecipe currentRecipe;

    public ProcessAgent() {
        reset();
    }

    /**
     * Attempt to start processing a recipe.
     * Consumes inputs immediately if successful.
     * Caches outputs in NBT.
     */
    public boolean start(ModularRecipe recipe, List<IModularPort> inputPorts, List<IModularPort> energyPorts) {
        if (running) return false;

        // Check if all non-energy inputs are available
        for (IRecipeInput input : recipe.getInputs()) {
            if (input instanceof EnergyInput) {
                EnergyInput energyInput = (EnergyInput) input;
                if (energyInput.isPerTick()) continue;
                if (!energyInput.process(energyPorts, true)) {
                    return false;
                }
            } else if (!input.process(inputPorts, true)) {
                return false;
            }
        }

        // Actually consume inputs
        energyPerTick = 0;
        for (IRecipeInput input : recipe.getInputs()) {
            if (input instanceof EnergyInput) {
                EnergyInput energyInput = (EnergyInput) input;
                if (energyInput.isPerTick()) {
                    energyPerTick += energyInput.getAmount();
                } else {
                    energyInput.process(energyPorts, false);
                }
            } else {
                input.process(inputPorts, false);
            }
        }

        // Cache outputs in NBT
        cachedItemOutputs.clear();
        cachedFluidOutputs.clear();
        cachedManaOutputs.clear();
        cachedGasOutputs.clear();
        cachedEssentiaOutputs.clear();
        cachedVisOutputs.clear();
        for (IRecipeOutput output : recipe.getOutputs()) {
            if (output instanceof ItemOutput) {
                ItemStack stack = ((ItemOutput) output).getOutput();
                cachedItemOutputs.add(stack.copy());
            } else if (output instanceof FluidOutput) {
                FluidStack stack = ((FluidOutput) output).getOutput();
                cachedFluidOutputs.add(stack.copy());
            } else if (output instanceof ManaOutput) {
                cachedManaOutputs.add(((ManaOutput) output).getAmount());
            } else if (output instanceof GasOutput) {
                GasOutput gasOut = (GasOutput) output;
                cachedGasOutputs.add(new String[] { gasOut.getGasName(), String.valueOf(gasOut.getAmount()) });
            } else if (output instanceof EssentiaOutput) {
                EssentiaOutput essentiaOut = (EssentiaOutput) output;
                cachedEssentiaOutputs
                    .add(new String[] { essentiaOut.getAspectTag(), String.valueOf(essentiaOut.getAmount()) });
            } else if (output instanceof VisOutput) {
                VisOutput visOut = (VisOutput) output;
                cachedVisOutputs.add(new String[] { visOut.getAspectTag(), String.valueOf(visOut.getAmount()) });
            }
        }

        currentRecipe = recipe;
        maxProgress = recipe.getDuration();
        progress = 0;
        running = true;
        waitingForOutput = false;

        return true;
    }

    /**
     * Process one tick.
     */
    public TickResult tick(List<IModularPort> energyPorts) {
        if (!running) return TickResult.IDLE;

        if (waitingForOutput) {
            return TickResult.WAITING_OUTPUT;
        }

        // Consume energy per tick
        if (energyPerTick > 0) {
            // Create temporary EnergyInput for consumption
            EnergyInput energyReq = new EnergyInput(energyPerTick, true);
            if (!energyReq.process(energyPorts, false)) {
                return TickResult.NO_ENERGY;
            }
        }

        progress++;

        if (progress >= maxProgress) {
            waitingForOutput = true;
            return TickResult.READY_OUTPUT;
        }

        return TickResult.CONTINUE;
    }

    /**
     * Attempt to output the cached results.
     * Uses cached outputs.
     */
    public boolean tryOutput(List<IModularPort> outputPorts) {
        if (!waitingForOutput) return false;

        // Create temporary output objects from cached data
        List<IRecipeOutput> outputs = new ArrayList<>();
        for (ItemStack stack : cachedItemOutputs) {
            outputs.add(new ItemOutput(stack.copy()));
        }
        for (FluidStack stack : cachedFluidOutputs) {
            outputs.add(new FluidOutput(stack.copy()));
        }
        for (Integer manaAmount : cachedManaOutputs) {
            outputs.add(new ManaOutput(manaAmount));
        }
        for (String[] gasData : cachedGasOutputs) {
            outputs.add(new GasOutput(gasData[0], Integer.parseInt(gasData[1])));
        }
        for (String[] essentiaData : cachedEssentiaOutputs) {
            outputs.add(new EssentiaOutput(essentiaData[0], Integer.parseInt(essentiaData[1])));
        }
        for (String[] visData : cachedVisOutputs) {
            outputs.add(new VisOutput(visData[0], Integer.parseInt(visData[1])));
        }

        // Check if all outputs can be placed (simulate)
        for (IRecipeOutput output : outputs) {
            if (!output.process(outputPorts, true)) {
                return false;
            }
        }

        // Actually place outputs
        for (IRecipeOutput output : outputs) {
            output.process(outputPorts, false);
        }

        reset();
        return true;
    }

    /**
     * Abort the current recipe.
     * Note: Consumed inputs are not refunded.
     */
    public void abort() {
        reset();
    }

    private void reset() {
        currentRecipe = null;
        progress = 0;
        maxProgress = 0;
        running = false;
        waitingForOutput = false;
        energyPerTick = 0;
        cachedItemOutputs.clear();
        cachedFluidOutputs.clear();
        cachedManaOutputs.clear();
        cachedGasOutputs.clear();
        cachedEssentiaOutputs.clear();
        cachedVisOutputs.clear();
    }

    // Getters
    public boolean isRunning() {
        return running;
    }

    public boolean isWaitingForOutput() {
        return waitingForOutput;
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public ModularRecipe getCurrentRecipe() {
        return currentRecipe;
    }

    public float getProgressPercent() {
        if (maxProgress == 0) return 0;
        return (float) progress / maxProgress;
    }

    // NBT persistence
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("progress", progress);
        nbt.setInteger("maxProgress", maxProgress);
        nbt.setInteger("energyPerTick", energyPerTick);
        nbt.setBoolean("running", running);
        nbt.setBoolean("waitingForOutput", waitingForOutput);

        if (running) {
            NBTTagList itemList = new NBTTagList();
            for (ItemStack stack : cachedItemOutputs) {
                itemList.appendTag(stack.writeToNBT(new NBTTagCompound()));
            }
            nbt.setTag("itemOutputs", itemList);

            NBTTagList fluidList = new NBTTagList();
            for (FluidStack stack : cachedFluidOutputs) {
                fluidList.appendTag(stack.writeToNBT(new NBTTagCompound()));
            }
            nbt.setTag("fluidOutputs", fluidList);
        }
    }

    public void readFromNBT(NBTTagCompound nbt) {
        progress = nbt.getInteger("progress");
        maxProgress = nbt.getInteger("maxProgress");
        energyPerTick = nbt.getInteger("energyPerTick");
        running = nbt.getBoolean("running");
        waitingForOutput = nbt.getBoolean("waitingForOutput");

        cachedItemOutputs.clear();
        cachedFluidOutputs.clear();

        if (running) {
            NBTTagList itemList = nbt.getTagList("itemOutputs", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < itemList.tagCount(); i++) {
                cachedItemOutputs.add(ItemStack.loadItemStackFromNBT(itemList.getCompoundTagAt(i)));
            }

            NBTTagList fluidList = nbt.getTagList("fluidOutputs", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < fluidList.tagCount(); i++) {
                cachedFluidOutputs.add(FluidStack.loadFluidStackFromNBT(fluidList.getCompoundTagAt(i)));
            }
        }
    }

    public enum TickResult {
        IDLE,
        CONTINUE,
        NO_ENERGY,
        READY_OUTPUT,
        WAITING_OUTPUT
    }
}
