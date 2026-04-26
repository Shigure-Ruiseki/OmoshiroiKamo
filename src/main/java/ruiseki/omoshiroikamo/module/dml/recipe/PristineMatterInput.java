package ruiseki.omoshiroikamo.module.dml.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistry;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistryItem;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.io.AbstractModularRecipeInput;
import ruiseki.omoshiroikamo.api.recipe.io.IRecipeInput;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;

public class PristineMatterInput extends AbstractModularRecipeInput {

    private int modelId = -1;

    public PristineMatterInput() {}

    public PristineMatterInput(int modelId) {
        this.modelId = modelId;
    }

    public static PristineMatterInput fromJson(JsonObject json) {
        PristineMatterInput input = new PristineMatterInput();
        input.read(json);
        return input;
    }

    @Override
    public void read(JsonObject json) {
        this.modelId = getInt(json, "modelId", -1);
        if (this.modelId == -1) {
            String name = getString(json, "modelName", null);
            if (name != null) {
                ModelRegistryItem item = ModelRegistry.INSTANCE.getByName(name);
                if (item != null) {
                    this.modelId = item.getId();
                }
            }
        }
    }

    @Override
    public boolean validate() {
        if (modelId == -1) {
            logValidationError("PristineMatterInput must have a valid modelId or modelName");
            return false;
        }
        return true;
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("modelId", modelId);
        ModelRegistryItem item = ModelRegistry.INSTANCE.getByType(modelId);
        if (item != null) {
            json.addProperty("modelName", item.getDisplayName());
        }
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit((IRecipeInput) this);
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.ITEM;
    }

    @Override
    public long getRequiredAmount() {
        return 1;
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.ITEM;
    }

    @Override
    protected long consume(IModularPort port, long remaining, boolean simulate, ConditionContext context) {
        if (!(port instanceof IInventory)) return 0;
        IInventory inv = (IInventory) port;

        ModelRegistryItem target = ModelRegistry.INSTANCE.getByType(modelId);
        if (target == null) return 0;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null && target.getPristineMatter() != null
                && stack.getItem() == target.getPristineMatter()
                    .getItem()
                && stack.getItemDamage() == target.getPristineMatter()
                    .getItemDamage()) {
                if (!simulate) {
                    stack.stackSize--;
                    if (stack.stackSize <= 0) {
                        inv.setInventorySlotContents(i, null);
                    }
                    inv.markDirty();
                }
                return 1;
            }
        }
        return 0;
    }

    @Override
    public IRecipeInput copy() {
        return copy(1);
    }

    @Override
    public IRecipeInput copy(int multiplier) {
        PristineMatterInput result = new PristineMatterInput(modelId);
        result.consume = this.consume;
        result.interval = this.interval;
        return result;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "pristinematter");
        nbt.setInteger("modelId", modelId);
        nbt.setInteger("interval", interval);
        nbt.setBoolean("consume", consume);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.modelId = nbt.getInteger("modelId");
        this.interval = nbt.getInteger("interval");
        this.consume = nbt.getBoolean("consume");
    }
}
