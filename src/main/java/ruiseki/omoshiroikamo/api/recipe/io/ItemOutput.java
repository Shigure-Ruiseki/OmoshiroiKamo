package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.Collections;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.registry.GameData;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.json.ItemJson;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.AbstractItemIOPortTE;

public class ItemOutput extends AbstractRecipeOutput {

    private ItemStack output;
    private int count = 0;

    public ItemOutput(ItemStack output) {
        this.output = output != null ? output.copy() : null;
        if (this.output != null) this.count = this.output.stackSize;
    }

    public ItemOutput(Item item, int count) {
        this(new ItemStack(item, count));
    }

    public ItemOutput(Item item, int count, int meta) {
        this(new ItemStack(item, count, meta));
    }

    public ItemStack getOutput() {
        return output != null ? output.copy() : null;
    }

    public List<ItemStack> getItems() {
        return output != null ? Collections.singletonList(output) : Collections.emptyList();
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.ITEM;
    }

    @Override
    public void apply(List<IModularPort> ports, int multiplier) {
        if (output == null) return;
        int remaining = output.stackSize * multiplier;
        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.ITEM) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT
                && port.getPortDirection() != IPortType.Direction.BOTH) continue;

            if (!(port instanceof AbstractItemIOPortTE)) {
                throw new IllegalStateException(
                    "ITEM OUTPUT port must be AbstractItemIOPortTE, got: " + port.getClass()
                        .getName());
            }

            AbstractItemIOPortTE itemPort = (AbstractItemIOPortTE) port;

            for (int i = 0; i < itemPort.getSizeInventory() && remaining > 0; i++) {
                ItemStack stack = itemPort.getStackInSlot(i);
                if (stack == null) {
                    int insert = Math.min(remaining, output.getMaxStackSize());
                    ItemStack newStack = output.copy();
                    newStack.stackSize = insert;
                    itemPort.setInventorySlotContents(i, newStack);
                    remaining -= insert;
                } else if (stacksMatch(stack, output)) {
                    int space = stack.getMaxStackSize() - stack.stackSize;
                    int insert = Math.min(remaining, space);
                    stack.stackSize += insert;
                    remaining -= insert;
                }
            }

            if (remaining <= 0) break;
        }
    }

    private boolean stacksMatch(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false;
        if (a.getItem() != b.getItem()) return false;
        if (a.getItemDamage() != b.getItemDamage()) return false;
        return ItemStack.areItemStackTagsEqual(a, b);
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.ITEM && port instanceof AbstractItemIOPortTE
            && (port.getPortDirection() == IPortType.Direction.OUTPUT
                || port.getPortDirection() == IPortType.Direction.BOTH);
    }

    @Override
    protected long getPortCapacity(IModularPort port) {
        if (output == null) return 0;
        AbstractItemIOPortTE itemPort = (AbstractItemIOPortTE) port;
        int maxStackSize = output.getMaxStackSize();
        int invLimit = itemPort.getInventoryStackLimit();
        int limit = Math.min(invLimit, maxStackSize);
        int min = itemPort.getSlotDefinition()
            .getMinItemOutput();
        int max = itemPort.getSlotDefinition()
            .getMaxItemOutput();

        long available = 0;
        for (int i = min; i < max; i++) {
            ItemStack stack = itemPort.getStackInSlot(i);
            if (stack == null) {
                available += limit;
            } else if (stacksMatch(stack, output)) {
                int space = limit - stack.stackSize;
                if (space > 0) available += space;
            }
        }
        return available;
    }

    @Override
    public long getRequiredAmount() {
        return output != null ? output.stackSize : count;
    }

    @Override
    public void read(JsonObject json) {
        ItemJson itemJson = new ItemJson();
        itemJson.read(json);
        this.count = itemJson.amount;
        this.output = ItemJson.resolveItemStack(itemJson);
    }

    @Override
    public void write(JsonObject json) {
        if (output != null) {
            json.addProperty(
                "item",
                GameData.getItemRegistry()
                    .getNameForObject(output.getItem()));
            if (output.stackSize != 1) json.addProperty("amount", output.stackSize);
            if (output.getItemDamage() != 0) json.addProperty("meta", output.getItemDamage());
        }
    }

    @Override
    public boolean validate() {
        return output != null;
    }

    @Override
    public void set(String key, Object value) {}

    @Override
    public Object get(String key) {
        return null;
    }

    public static ItemOutput fromJson(JsonObject json) {
        ItemOutput out = new ItemOutput((ItemStack) null);
        out.read(json);
        return out;
    }

    @Override
    public IRecipeOutput copy() {
        return copy(1);
    }

    @Override
    public IRecipeOutput copy(int multiplier) {
        if (output == null) return new ItemOutput((ItemStack) null);
        ItemStack copy = output.copy();
        copy.stackSize *= multiplier;
        return new ItemOutput(copy);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "item");
        if (output != null) {
            NBTTagCompound stackTag = new NBTTagCompound();
            output.writeToNBT(stackTag);
            nbt.setTag("output", stackTag);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("output")) {
            this.output = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("output"));
        }
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }
}
