package ruiseki.omoshiroikamo.module.machinery.common.recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.modular.recipe.EnergyInput;
import ruiseki.omoshiroikamo.api.modular.recipe.EnergyOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.EssentiaOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.FluidOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.GasOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.IRecipeInput;
import ruiseki.omoshiroikamo.api.modular.recipe.IRecipeOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.ItemOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.ManaOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.ModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.VisOutput;

public class ProcessAgent {

    private int progress;
    private int maxProgress;
    private int energyPerTick;
    private int energyOutputPerTick;
    private boolean running;
    private boolean waitingForOutput;

    private List<ItemStack> cachedItemOutputs = new ArrayList<>();
    private List<FluidStack> cachedFluidOutputs = new ArrayList<>();

    private List<Integer> cachedManaOutputs = new ArrayList<>();
    private List<String[]> cachedGasOutputs = new ArrayList<>(); // [gasName, amount]
    private List<String[]> cachedEssentiaOutputs = new ArrayList<>(); // [aspectTag, amount]
    private List<String[]> cachedVisOutputs = new ArrayList<>(); // [aspectTag, amountCentiVis]
    private List<Integer> cachedEnergyOutputs = new ArrayList<>(); // [amount]

    private String currentRecipeName;
    private transient ModularRecipe currentRecipe;

    public ProcessAgent() {
        reset();
    }

    public boolean start(ModularRecipe recipe, List<IModularPort> inputPorts, List<IModularPort> energyPorts) {
        if (running) return false;

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

        energyPerTick = 0;
        energyOutputPerTick = 0;
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

        // Calculate per-tick energy output
        for (IRecipeOutput output : recipe.getOutputs()) {
            if (output instanceof EnergyOutput) {
                EnergyOutput eOut = (EnergyOutput) output;
                if (eOut.isPerTick()) {
                    energyOutputPerTick += eOut.getAmount();
                }
            }
        }

        cachedItemOutputs.clear();
        cachedFluidOutputs.clear();
        cachedManaOutputs.clear();
        cachedGasOutputs.clear();
        cachedEssentiaOutputs.clear();
        cachedVisOutputs.clear();
        cachedEnergyOutputs.clear();

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
                cachedVisOutputs
                    .add(new String[] { visOut.getAspectTag(), String.valueOf(visOut.getAmountCentiVis()) });
            } else if (output instanceof EnergyOutput) {
                EnergyOutput eOut = (EnergyOutput) output;
                if (!eOut.isPerTick()) {
                    cachedEnergyOutputs.add(eOut.getAmount());
                }
            }
        }

        currentRecipe = recipe;
        currentRecipeName = recipe.getName();
        maxProgress = recipe.getDuration();
        progress = 0;
        running = true;
        waitingForOutput = false;

        return true;
    }

    public TickResult tick(List<IModularPort> inputEnergyPorts, List<IModularPort> outputEnergyPorts) {
        if (!running) return TickResult.IDLE;

        if (waitingForOutput) {
            return TickResult.WAITING_OUTPUT;
        }

        if (energyPerTick > 0) {
            EnergyInput energyReq = new EnergyInput(energyPerTick, true);
            if (!energyReq.process(inputEnergyPorts, false)) {
                return TickResult.NO_ENERGY;
            }
        }

        if (energyOutputPerTick > 0) {
            EnergyOutput energyOut = new EnergyOutput(energyOutputPerTick, true);
            if (!energyOut.process(outputEnergyPorts, false)) {
                return TickResult.OUTPUT_FULL;
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
     * Diagnose why the agent is idle.
     */
    public TickResult diagnoseIdle(List<IModularPort> inputPorts) {
        if (running || waitingForOutput) return TickResult.CONTINUE; // Not idle

        // Check input ports to see if any input is missing
        if (inputPorts == null || inputPorts.isEmpty()) {
            return TickResult.NO_INPUT;
        }

        return TickResult.IDLE;
    }

    public boolean tryOutput(List<IModularPort> outputPorts) {
        if (!waitingForOutput) return false;

        List<IRecipeOutput> outputs = new ArrayList<>();
        for (ItemStack stack : cachedItemOutputs) {
            outputs.add(new ItemOutput(stack.copy()));
        }
        for (FluidStack stack : cachedFluidOutputs) {
            outputs.add(
                new FluidOutput(
                    stack.getFluid()
                        .getName(),
                    stack.amount));
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
        for (Integer energyAmount : cachedEnergyOutputs) {
            outputs.add(new EnergyOutput(energyAmount, false));
        }

        for (IRecipeOutput output : outputs) {
            if (!output.process(outputPorts, true)) {
                return false;
            }
        }

        for (IRecipeOutput output : outputs) {
            output.process(outputPorts, false);
        }

        reset();
        return true;
    }

    public void abort() {
        reset();
    }

    private void reset() {
        currentRecipe = null;
        currentRecipeName = null;
        progress = 0;
        maxProgress = 0;
        running = false;
        waitingForOutput = false;
        energyPerTick = 0;
        energyOutputPerTick = 0;
        cachedItemOutputs.clear();
        cachedFluidOutputs.clear();
        cachedManaOutputs.clear();
        cachedGasOutputs.clear();
        cachedEssentiaOutputs.clear();
        cachedVisOutputs.clear();
        cachedEnergyOutputs.clear();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isWaitingForOutput() {
        return waitingForOutput;
    }

    public void setWaitingForOutput(boolean waitingForOutput) {
        this.waitingForOutput = waitingForOutput;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = Math.max(0, progress);
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = Math.max(0, maxProgress);
    }

    public ModularRecipe getCurrentRecipe() {
        return currentRecipe;
    }

    public String getCurrentRecipeName() {
        return currentRecipeName;
    }

    public void setCurrentRecipeName(String name) {
        this.currentRecipeName = name;
    }

    /**
     * Get a list of output types that are currently cached
     */
    public Set<IPortType.Type> getCachedOutputTypes() {
        Set<IPortType.Type> types = new HashSet<>();
        if (!cachedItemOutputs.isEmpty()) types.add(IPortType.Type.ITEM);
        if (!cachedFluidOutputs.isEmpty()) types.add(IPortType.Type.FLUID);
        if (!cachedManaOutputs.isEmpty()) types.add(IPortType.Type.MANA);
        if (!cachedGasOutputs.isEmpty()) types.add(IPortType.Type.GAS);
        if (!cachedEssentiaOutputs.isEmpty()) types.add(IPortType.Type.ESSENTIA);
        if (!cachedVisOutputs.isEmpty()) types.add(IPortType.Type.VIS);
        if (!cachedEnergyOutputs.isEmpty() || energyOutputPerTick > 0) types.add(IPortType.Type.ENERGY);
        return types;
    }

    public float getProgressPercent() {
        if (maxProgress == 0) return 0;
        return (float) progress / maxProgress;
    }

    /**
     * Get a status message for GUI display.
     * Returns a human-readable string describing the current processing state.
     *
     * @param outputPorts Output ports for checking blocked outputs
     * @return Status message string
     */
    public String getStatusMessage(List<IModularPort> outputPorts) {
        if (running && !waitingForOutput) {
            if (maxProgress <= 0) {
                return "Processing 0 %";
            }
            return "Processing " + (int) ((float) progress / maxProgress * 100) + " %";
        }

        if (waitingForOutput) {
            String blocked = diagnoseBlockedOutputs(outputPorts);
            return blocked + " Output is full";
        }

        return "Idle";
    }

    /**
     * Diagnose which output types are blocked when waiting for output.
     */
    private String diagnoseBlockedOutputs(List<IModularPort> outputPorts) {
        if (currentRecipe != null) {
            StringBuilder blocked = new StringBuilder();
            for (IRecipeOutput output : currentRecipe.getOutputs()) {
                if (!output.process(outputPorts, true)) {
                    if (blocked.length() > 0) blocked.append(", ");
                    blocked.append(
                        output.getPortType()
                            .name());
                }
            }
            if (blocked.length() > 0) return blocked.toString();
        }

        // Fallback: use cached output types
        Set<IPortType.Type> cachedTypes = getCachedOutputTypes();
        if (!cachedTypes.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (IPortType.Type type : cachedTypes) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(type.name());
            }
            return sb.toString();
        }

        return "Unknown";
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("progress", progress);
        nbt.setInteger("maxProgress", maxProgress);
        nbt.setInteger("energyPerTick", energyPerTick);
        nbt.setInteger("energyOutputPerTick", energyOutputPerTick);
        nbt.setBoolean("running", running);
        nbt.setBoolean("waitingForOutput", waitingForOutput);
        if (currentRecipeName != null) {
            nbt.setString("recipeName", currentRecipeName);
        }

        if (running || waitingForOutput) {
            // Item outputs
            NBTTagList itemList = new NBTTagList();
            for (ItemStack stack : cachedItemOutputs) {
                itemList.appendTag(stack.writeToNBT(new NBTTagCompound()));
            }
            nbt.setTag("itemOutputs", itemList);

            // Fluid outputs
            NBTTagList fluidList = new NBTTagList();
            for (FluidStack stack : cachedFluidOutputs) {
                fluidList.appendTag(stack.writeToNBT(new NBTTagCompound()));
            }
            nbt.setTag("fluidOutputs", fluidList);

            // Mana outputs (just integers)
            int[] manaArray = new int[cachedManaOutputs.size()];
            for (int i = 0; i < cachedManaOutputs.size(); i++) {
                manaArray[i] = cachedManaOutputs.get(i);
            }
            nbt.setIntArray("manaOutputs", manaArray);

            // Gas outputs [gasName, amount]
            NBTTagList gasList = new NBTTagList();
            for (String[] gas : cachedGasOutputs) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("gas", gas[0]);
                tag.setString("amount", gas[1]);
                gasList.appendTag(tag);
            }
            nbt.setTag("gasOutputs", gasList);

            // Essentia outputs [aspectTag, amount]
            NBTTagList essentiaList = new NBTTagList();
            for (String[] essentia : cachedEssentiaOutputs) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("aspect", essentia[0]);
                tag.setString("amount", essentia[1]);
                essentiaList.appendTag(tag);
            }
            nbt.setTag("essentiaOutputs", essentiaList);

            // Vis outputs [aspectTag, amountCentiVis]
            NBTTagList visList = new NBTTagList();
            for (String[] vis : cachedVisOutputs) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("aspect", vis[0]);
                tag.setString("amount", vis[1]);
                visList.appendTag(tag);
            }
            nbt.setTag("visOutputs", visList);

            // Energy outputs
            int[] energyArray = new int[cachedEnergyOutputs.size()];
            for (int i = 0; i < cachedEnergyOutputs.size(); i++) {
                energyArray[i] = cachedEnergyOutputs.get(i);
            }
            nbt.setIntArray("energyOutputs", energyArray);
        }
    }

    public void readFromNBT(NBTTagCompound nbt) {
        progress = nbt.getInteger("progress");
        maxProgress = nbt.getInteger("maxProgress");
        energyPerTick = nbt.getInteger("energyPerTick");
        energyOutputPerTick = nbt.getInteger("energyOutputPerTick");
        running = nbt.getBoolean("running");
        waitingForOutput = nbt.getBoolean("waitingForOutput");
        currentRecipeName = nbt.hasKey("recipeName") ? nbt.getString("recipeName") : null;

        // Clear all caches
        cachedItemOutputs.clear();
        cachedFluidOutputs.clear();
        cachedManaOutputs.clear();
        cachedGasOutputs.clear();
        cachedEssentiaOutputs.clear();
        cachedVisOutputs.clear();
        cachedEnergyOutputs.clear();

        if (running || waitingForOutput) {
            // Item outputs
            NBTTagList itemList = nbt.getTagList("itemOutputs", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < itemList.tagCount(); i++) {
                cachedItemOutputs.add(ItemStack.loadItemStackFromNBT(itemList.getCompoundTagAt(i)));
            }

            // Fluid outputs
            NBTTagList fluidList = nbt.getTagList("fluidOutputs", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < fluidList.tagCount(); i++) {
                cachedFluidOutputs.add(FluidStack.loadFluidStackFromNBT(fluidList.getCompoundTagAt(i)));
            }

            // Mana outputs
            int[] manaArray = nbt.getIntArray("manaOutputs");
            for (int mana : manaArray) {
                cachedManaOutputs.add(mana);
            }

            // Gas outputs
            NBTTagList gasList = nbt.getTagList("gasOutputs", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < gasList.tagCount(); i++) {
                NBTTagCompound tag = gasList.getCompoundTagAt(i);
                cachedGasOutputs.add(new String[] { tag.getString("gas"), tag.getString("amount") });
            }

            // Essentia outputs
            NBTTagList essentiaList = nbt.getTagList("essentiaOutputs", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < essentiaList.tagCount(); i++) {
                NBTTagCompound tag = essentiaList.getCompoundTagAt(i);
                cachedEssentiaOutputs.add(new String[] { tag.getString("aspect"), tag.getString("amount") });
            }

            // Vis outputs
            NBTTagList visList = nbt.getTagList("visOutputs", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < visList.tagCount(); i++) {
                NBTTagCompound tag = visList.getCompoundTagAt(i);
                cachedVisOutputs.add(new String[] { tag.getString("aspect"), tag.getString("amount") });
            }

            // Energy outputs
            if (nbt.hasKey("energyOutputs")) {
                int[] energyArray = nbt.getIntArray("energyOutputs");
                for (int e : energyArray) {
                    cachedEnergyOutputs.add(e);
                }
            }
        }
    }

    public enum TickResult {
        IDLE,
        CONTINUE,
        NO_ENERGY,
        READY_OUTPUT,
        WAITING_OUTPUT,
        NO_INPUT,
        NO_MATCHING_RECIPE,
        OUTPUT_FULL,
        PAUSED
    }
}
