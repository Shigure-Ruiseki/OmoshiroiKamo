package ruiseki.omoshiroikamo.common.block.abstractClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import com.enderio.core.api.common.util.IProgressTile;

import ruiseki.omoshiroikamo.api.io.SlotDefinition;
import ruiseki.omoshiroikamo.common.recipe.chance.ChanceFluidStack;
import ruiseki.omoshiroikamo.common.recipe.chance.ChanceItemStack;
import ruiseki.omoshiroikamo.common.recipe.machine.IPoweredTask;
import ruiseki.omoshiroikamo.common.recipe.machine.MachineRecipe;
import ruiseki.omoshiroikamo.common.recipe.machine.MachineRecipeRegistry;
import ruiseki.omoshiroikamo.common.recipe.machine.PoweredTask;
import ruiseki.omoshiroikamo.common.recipe.machine.PoweredTaskProgress;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.OreDictUtils;

public abstract class AbstractTaskTE extends AbstractIOTE implements IProgressTile {

    protected final Random random = new Random();
    protected IPoweredTask currentTask = null;
    protected MachineRecipe lastCompletedRecipe;
    protected MachineRecipe cachedNextRecipe;
    protected int ticksSinceCheckedRecipe = 0;
    protected boolean startFailed = false;
    protected float nextChance = Float.NaN;
    protected float stage = 0f;

    public AbstractTaskTE(SlotDefinition slotDefinition) {
        super(slotDefinition);
    }

    public float getStage() {
        return stage;
    }

    public void addStage(float amount) {
        this.stage += amount;
    }

    @Override
    public TileEntity getTileEntity() {
        return this;
    }

    @Override
    protected boolean isMachineItemValidForSlot(int slot, ItemStack itemstack) {
        return true;
    }

    @Override
    public boolean isActive() {
        return currentTask != null && currentTask.getProgress() >= 0 && redstoneCheckPassed;
    }

    @Override
    public float getProgress() {
        return currentTask == null ? -1 : currentTask.getProgress();
    }

    @Override
    public void setProgress(float progress) {
        this.currentTask = progress < 0 ? null : new PoweredTaskProgress(progress);
    }

    public IPoweredTask getCurrentTask() {
        return currentTask;
    }

    public boolean getRedstoneChecksPassed() {
        return redstoneCheckPassed;
    }

    @Override
    protected boolean processTasks(boolean redstoneChecksPassed) {

        if (!redstoneChecksPassed) {
            return false;
        }

        boolean requiresClientSync = false;
        // Process any current items
        requiresClientSync |= checkProgress(redstoneChecksPassed);

        if (currentTask != null || !hasValidInputsForRecipe()) {
            return requiresClientSync;
        }

        if (startFailed) {
            ticksSinceCheckedRecipe++;
            if (ticksSinceCheckedRecipe < 20) {
                return false;
            }
        }
        ticksSinceCheckedRecipe = 0;

        // Get a new chance when we don't have one yet
        // If a recipe could not be started we will try with the same chance next time
        if (Float.isNaN(nextChance)) {
            nextChance = random.nextFloat();
        }

        // Then see if we need to start a new one
        MachineRecipe nextRecipe = canStartNextTask(nextChance);
        if (nextRecipe != null) {
            boolean started = startNextTask(nextRecipe, nextChance);
            if (started) {
                // this chance value has been used up
                nextChance = Float.NaN;
            }
            startFailed = !started;
        } else {
            startFailed = true;
        }
        sendTaskProgressPacket();

        return requiresClientSync;
    }

    protected boolean checkProgress(boolean redstoneChecksPassed) {
        if (currentTask == null) {
            return false;
        }
        if (redstoneChecksPassed && !currentTask.isComplete()) {
            useStage();
        }
        // then check if we are done
        if (currentTask.isComplete()) {
            taskComplete();
            return false;
        }

        return false;
    }

    protected float useStage() {
        return useStage(stage);
    }

    public float useStage(float amount) {

        float used = Math.min(stage, amount);
        if (currentTask != null) {
            currentTask.update(used);
        }
        return used;
    }

    protected void taskComplete() {
        if (currentTask != null) {
            lastCompletedRecipe = currentTask.getRecipe();
            List<ItemStack> itemOutputs = currentTask.getItemOutputs();
            List<FluidStack> fluidOutputs = currentTask.getFluidOutputs();
            mergeResults(itemOutputs, fluidOutputs);
        }
        markDirty();
        currentTask = null;
        lastProgressScaled = 0;
    }

    protected void mergeResults(List<ItemStack> itemStacks, List<FluidStack> fluidStacks) {
        List<ItemStack> outputStacks = new ArrayList<ItemStack>();
        for (int i = slotDefinition.minItemOutputSlot; i <= slotDefinition.maxItemOutputSlot; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            outputStacks.add(stack != null ? stack.copy() : null);
        }

        for (ItemStack output : itemStacks) {
            if (output == null) {
                continue;
            }

            ItemStack copy = output.copy();
            int remaining = copy.stackSize;

            // Merge vào stack đã có (ưu tiên oredict)
            for (int i = 0; i < outputStacks.size() && remaining > 0; i++) {
                ItemStack current = outputStacks.get(i);
                if (current != null && current.isItemEqual(copy) && ItemStack.areItemStackTagsEqual(current, copy)) {
                    int canMerge = Math.min(remaining, current.getMaxStackSize() - current.stackSize);
                    if (canMerge > 0) {
                        current.stackSize += canMerge;
                        remaining -= canMerge;
                        outputStacks.set(i, current);
                    }
                }
            }

            // Tìm slot trống
            for (int i = 0; i < outputStacks.size() && remaining > 0; i++) {
                if (outputStacks.get(i) == null) {
                    ItemStack newStack = copy.copy();
                    newStack.stackSize = remaining;
                    outputStacks.set(i, newStack);
                    remaining = 0;
                }
            }

            if (remaining > 0) {
                Logger.info("[mergeResults] Không đủ chỗ chứa ItemStack: " + copy);
            }
        }

        // Ghi lại vào inv
        int listIndex = 0;
        for (int i = slotDefinition.minItemOutputSlot; i <= slotDefinition.maxItemOutputSlot; i++) {
            inv.setStackInSlot(i, outputStacks.get(listIndex++));
        }

        for (FluidStack output : fluidStacks) {
            if (output == null) {
                continue;
            }

            int remaining = output.amount;

            for (int i = slotDefinition.minFluidOutputSlot; i <= slotDefinition.maxFluidOutputSlot
                && remaining > 0; i++) {
                FluidStack current = fluidTanks[i].getFluid();
                if (current != null && current.isFluidEqual(output)) {
                    int filled = fluidTanks[i].fill(new FluidStack(output.getFluid(), remaining), true);
                    remaining -= filled;
                }
            }

            for (int i = slotDefinition.minFluidOutputSlot; i <= slotDefinition.maxFluidOutputSlot
                && remaining > 0; i++) {
                FluidStack current = fluidTanks[i].getFluid();
                if (current == null || current.amount == 0) {
                    int filled = fluidTanks[i].fill(new FluidStack(output.getFluid(), remaining), true);
                    remaining -= filled;
                }
            }

            if (remaining > 0) {}
        }

        cachedNextRecipe = null;
    }

    protected MachineRecipe getNextRecipe() {
        if (cachedNextRecipe == null) {
            cachedNextRecipe = MachineRecipeRegistry
                .findMatchingRecipe(getMachineName(), getItemInputs(), getFluidInputs());
        }
        return cachedNextRecipe;
    }

    protected MachineRecipe canStartNextTask(float chance) {
        MachineRecipe nextRecipe = getNextRecipe();
        if (nextRecipe == null) {
            return null;
        }
        return canOutput(nextRecipe) ? nextRecipe : null;
    }

    protected boolean canOutput(MachineRecipe recipe) {
        // Kiểm tra item outputs
        for (ChanceItemStack out : recipe.getItemOutputs()) {
            if (out == null) {
                continue;
            }
            boolean canInsert = false;

            for (int i = slotDefinition.minItemOutputSlot; i <= slotDefinition.maxItemOutputSlot; i++) {
                ItemStack target = inv.getStackInSlot(i);
                if (target == null || (ItemStack.areItemStacksEqual(target, out.stack)
                    && target.stackSize + out.stack.stackSize <= target.getMaxStackSize())) {
                    canInsert = true;
                    break;
                }
            }
            if (!canInsert) {
                return false;
            }
        }

        // Kiểm tra fluid outputs
        for (ChanceFluidStack out : recipe.getFluidOutputs()) {
            if (out == null) {
                continue;
            }
            boolean canInsert = false;

            for (int i = slotDefinition.minFluidOutputSlot; i <= slotDefinition.maxFluidOutputSlot; i++) {
                FluidTank tank = fluidTanks[i];
                if (tank.fill(out.stack, false) == out.stack.amount) {
                    canInsert = true;
                    break;
                }
            }

            if (!canInsert) {
                return false;
            }
        }

        return true;
    }

    private void consumeInputs(MachineRecipe recipe, float chance) {
        for (ChanceItemStack input : recipe.getItemInputs()) {

            int remaining = input.stack.stackSize;

            for (int i = slotDefinition.minItemInputSlot; i <= slotDefinition.maxItemInputSlot && remaining > 0; i++) {
                ItemStack target = inv.getStackInSlot(i);
                if (target == null) {
                    continue;
                }

                boolean matches = OreDictUtils.isOreDictMatch(input.stack, target);

                if (matches) {
                    if (input.chance < chance) {
                        continue;
                    }

                    int consumed = Math.min(remaining, target.stackSize);
                    target.stackSize -= consumed;
                    remaining -= consumed;

                    // Nếu là slot input thật sự -> reset cached recipe
                    if (slotDefinition.isInputSlot(i)) {
                        cachedNextRecipe = null;
                    }

                    // Cập nhật lại slot
                    if (target.stackSize <= 0) {
                        inv.setStackInSlot(i, null);
                    } else {
                        inv.setStackInSlot(i, target);
                    }
                }
            }

            if (remaining > 0) {
                Logger.info("[consumeInputs] Không đủ item để tiêu thụ: " + input.stack.getDisplayName());
            }
        }

        // Tiêu thụ fluid inputs
        for (ChanceFluidStack input : recipe.getFluidInputs()) {
            int remaining = input.stack.amount;

            for (int i = slotDefinition.minFluidInputSlot; i <= slotDefinition.maxFluidInputSlot
                && remaining > 0; i++) {
                FluidTank tank = fluidTanks[i];
                FluidStack contained = tank.getFluid();

                if (contained != null && contained.isFluidEqual(input.stack)) {
                    int drained = Math.min(remaining, contained.amount);
                    tank.drain(drained, true);
                    remaining -= drained;
                }
            }

            if (remaining > 0) {
                Logger.info(
                    "[consumeInputs] Không đủ fluid để tiêu thụ: " + input.stack.amount
                        + "L of "
                        + input.stack.getFluid()
                            .getName());
            }
        }
    }

    protected boolean startNextTask(MachineRecipe nextRecipe, float chance) {
        if (nextRecipe
            == MachineRecipeRegistry.findMatchingRecipe(getMachineName(), getItemInputs(), getFluidInputs())) {
            consumeInputs(nextRecipe, chance);
            currentTask = createTask(nextRecipe, chance);
            return true;
        }
        return false;
    }

    protected IPoweredTask createTask(MachineRecipe nextRecipe, float chance) {
        return new PoweredTask(nextRecipe, chance, getItemInputs(), getFluidInputs());
    }

    protected IPoweredTask createTask(NBTTagCompound taskTagCompound) {
        return PoweredTask.readFromNBT(taskTagCompound);
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);
        if (currentTask != null) {
            NBTTagCompound currentTaskNBT = new NBTTagCompound();
            currentTask.writeToNBT(currentTaskNBT);
            root.setTag("currentTask", currentTaskNBT);
        }
        if (lastCompletedRecipe != null) {
            root.setString("lastCompletedRecipe", lastCompletedRecipe.getUid());
        }
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);

        currentTask = root.hasKey("currentTask") ? createTask(root.getCompoundTag("currentTask")) : null;
        String uid = root.getString("lastCompletedRecipe");
        lastCompletedRecipe = MachineRecipeRegistry.getRecipeForUid(uid);

        cachedNextRecipe = null;
    }

    protected boolean hasValidInputsForRecipe() {
        MachineRecipe recipe = MachineRecipeRegistry
            .findMatchingRecipe(getMachineName(), getItemInputs(), getFluidInputs());
        return recipe != null;
    }

    private List<ItemStack> getItemInputs() {
        List<ItemStack> list = new ArrayList<>();
        for (int i = slotDefinition.minItemInputSlot; i <= slotDefinition.maxItemInputSlot; i++) {
            ItemStack stack = this.inv.getStackInSlot(i);
            if (stack != null) {
                list.add(stack.copy());
            }
        }
        return list;
    }

    private List<FluidStack> getFluidInputs() {
        List<FluidStack> list = new ArrayList<>();
        for (int i = slotDefinition.minFluidInputSlot; i <= slotDefinition.maxFluidInputSlot; i++) {
            FluidTank tank = this.fluidTanks[i];
            if (tank.getFluid() != null && tank.getFluidAmount() > 0) {
                list.add(
                    tank.getFluid()
                        .copy());
            }
        }
        return list;
    }

    public List<ChanceItemStack> getItemOutput() {
        if (currentTask == null) {
            return Collections.emptyList();
        }
        MachineRecipe recipe = currentTask.getRecipe();
        if (recipe == null) {
            return Collections.emptyList();
        }

        List<ChanceItemStack> result = new ArrayList<>();
        for (ChanceItemStack is : recipe.getItemOutputs()) {
            if (is != null && is.stack != null) {
                ItemStack oreRep = OreDictUtils.getOreDictRepresentative(is.stack);
                result.add(new ChanceItemStack(oreRep, is.chance));
            }
        }
        return result;
    }

    public List<ChanceFluidStack> getFluidOutput() {
        if (currentTask == null) {
            return Collections.emptyList();
        }
        MachineRecipe recipe = currentTask.getRecipe();
        if (recipe == null) {
            return Collections.emptyList();
        }

        List<ChanceFluidStack> result = new ArrayList<>();
        for (ChanceFluidStack fs : recipe.getFluidOutputs()) {
            if (fs != null && fs.stack != null) {
                result.add(new ChanceFluidStack(fs.stack.copy(), fs.chance));
            }
        }
        return result;
    }
}
