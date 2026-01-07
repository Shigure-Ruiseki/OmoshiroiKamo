package ruiseki.omoshiroikamo.module.machinery.common.recipe;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.recipe.EnergyInput;
import ruiseki.omoshiroikamo.api.modular.recipe.IRecipeInput;
import ruiseki.omoshiroikamo.api.modular.recipe.IRecipeOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.ModularRecipe;

/**
 * Manages the processing of a single recipe.
 * Handles tick-by-tick progression, energy consumption, and output generation.
 */
public class ProcessAgent {

    private ModularRecipe currentRecipe;
    private int progress;
    private boolean running;
    private boolean waitingForOutput;

    // Energy consumption tracking
    private int energyPerTick;

    public ProcessAgent() {
        reset();
    }

    /**
     * Attempt to start processing a recipe.
     * Consumes inputs immediately if successful.
     * 
     * @return true if recipe started successfully
     */
    public boolean start(ModularRecipe recipe, List<IModularPort> inputPorts) {
        if (running) return false;

        // Check if all inputs are available (simulate first)
        for (IRecipeInput input : recipe.getInputs()) {
            if (input instanceof EnergyInput) {
                // Energy is consumed per-tick, not at start
                continue;
            }
            if (!input.process(inputPorts, true)) {
                return false;
            }
        }

        // Actually consume inputs
        for (IRecipeInput input : recipe.getInputs()) {
            if (input instanceof EnergyInput) {
                continue;
            }
            input.process(inputPorts, false);
        }

        // Calculate energy per tick
        energyPerTick = 0;
        for (IRecipeInput input : recipe.getInputs()) {
            if (input instanceof EnergyInput) {
                EnergyInput energyInput = (EnergyInput) input;
                energyPerTick += energyInput.getAmount();
            }
        }

        currentRecipe = recipe;
        progress = 0;
        running = true;
        waitingForOutput = false;

        return true;
    }

    /**
     * Process one tick.
     * 
     * @return the result of this tick
     */
    public TickResult tick(List<IModularPort> energyPorts) {
        if (!running) return TickResult.IDLE;

        // If waiting for output space, don't consume energy
        if (waitingForOutput) {
            return TickResult.WAITING_OUTPUT;
        }

        // Try to consume energy
        if (energyPerTick > 0) {
            int remaining = energyPerTick;
            for (IModularPort port : energyPorts) {
                if (remaining <= 0) break;
                // Check available energy first
                // For now, use the EnergyInput.process with simulate
                // This is simplified; actual implementation may need direct access
            }

            // Simplified: use EnergyInput to consume
            for (IRecipeInput input : currentRecipe.getInputs()) {
                if (input instanceof EnergyInput) {
                    if (!input.process(energyPorts, false)) {
                        return TickResult.NO_ENERGY;
                    }
                }
            }
        }

        // Advance progress
        progress++;

        // Check if completed
        if (progress >= currentRecipe.getDuration()) {
            waitingForOutput = true;
            return TickResult.READY_OUTPUT;
        }

        return TickResult.CONTINUE;
    }

    /**
     * Attempt to output the results.
     * 
     * @return true if outputs were successfully placed
     */
    public boolean tryOutput(List<IModularPort> outputPorts) {
        if (!waitingForOutput) return false;

        // Check if all outputs can be placed (simulate)
        for (IRecipeOutput output : currentRecipe.getOutputs()) {
            if (!output.process(outputPorts, true)) {
                return false;
            }
        }

        // Actually place outputs
        for (IRecipeOutput output : currentRecipe.getOutputs()) {
            output.process(outputPorts, false);
        }

        // Reset for next recipe
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
        running = false;
        waitingForOutput = false;
        energyPerTick = 0;
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

    public int getDuration() {
        return currentRecipe != null ? currentRecipe.getDuration() : 0;
    }

    public ModularRecipe getCurrentRecipe() {
        return currentRecipe;
    }

    public float getProgressPercent() {
        if (currentRecipe == null || currentRecipe.getDuration() == 0) return 0;
        return (float) progress / currentRecipe.getDuration();
    }

    // NBT persistence
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("progress", progress);
        nbt.setBoolean("running", running);
        nbt.setBoolean("waitingForOutput", waitingForOutput);
        nbt.setInteger("energyPerTick", energyPerTick);
        // Note: Recipe reference needs to be restored separately by Controller
    }

    public void readFromNBT(NBTTagCompound nbt) {
        progress = nbt.getInteger("progress");
        running = nbt.getBoolean("running");
        waitingForOutput = nbt.getBoolean("waitingForOutput");
        energyPerTick = nbt.getInteger("energyPerTick");
    }

    /**
     * Result of a tick operation.
     */
    public enum TickResult {
        IDLE, // Not running
        CONTINUE, // Processing continues
        NO_ENERGY, // Paused due to insufficient energy
        READY_OUTPUT, // Completed, waiting for output space
        WAITING_OUTPUT // Still waiting for output space
    }
}
