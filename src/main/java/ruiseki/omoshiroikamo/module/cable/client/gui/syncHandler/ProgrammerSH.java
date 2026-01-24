package ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.value.sync.SyncHandler;

import ruiseki.omoshiroikamo.api.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicTypes;
import ruiseki.omoshiroikamo.module.cable.common.programmer.ProgrammerHandler;
import ruiseki.omoshiroikamo.module.cable.common.programmer.ProgrammerPanel;

public class ProgrammerSH extends SyncHandler {

    private final ProgrammerHandler handler;
    private final ProgrammerPanel panel;

    public static final int CLEAR_VARIABLE_SLOT = 0;

    public static final int SET_BOOLEAN_LITERAL = 100;
    public static final int SET_INT_LITERAL = 101;
    public static final int SET_LONG_LITERAL = 102;
    public static final int SET_DOUBLE_LITERAL = 103;
    public static final int SET_STRING_LITERAL = 104;

    public ProgrammerSH(ProgrammerHandler handler, ProgrammerPanel panel) {
        this.handler = handler;
        this.panel = panel;
    }

    @Override
    public void readOnClient(int id, PacketBuffer buf) throws IOException {

    }

    @Override
    public void readOnServer(int id, PacketBuffer buf) throws IOException {

        if (id == CLEAR_VARIABLE_SLOT) {
            handleClearVariableSlot();
        }

        if (id == SET_BOOLEAN_LITERAL) {
            writeBooleanLiteral(buf);
        }

        if (id == SET_INT_LITERAL) {
            writeIntLiteral(buf);
        }

        if (id == SET_LONG_LITERAL) {
            writeLongLiteral(buf);
        }

        if (id == SET_DOUBLE_LITERAL) {
            writeDoubleLiteral(buf);
        }

        if (id == SET_STRING_LITERAL) {
            writeStringLiteral(buf);
        }
    }

    public void clearVariableSlot() {
        syncToServer(CLEAR_VARIABLE_SLOT, buffer -> {});
    }

    private void handleClearVariableSlot() {
        EntityPlayer player = getSyncManager().getPlayer();
        for (int i = 0; i < handler.getHandler()
            .getSlots(); i++) {
            ItemStack s = handler.getHandler()
                .extractItem(i, Integer.MAX_VALUE, false);
            if (s == null) continue;
            if (!player.inventory.addItemStackToInventory(s)) {
                player.dropPlayerItemWithRandomChoice(s, false);
            }
        }
    }

    private void writeBooleanLiteral(PacketBuffer buffer) {
        boolean value = buffer.readBoolean();
        ItemStack stack = handler.getHandler()
            .getStackInSlot(0);
        if (stack == null) return;
        NBTTagCompound logic = new NBTTagCompound();

        logic.setString("Type", "LITERAL");
        logic.setString("ValueType", LogicTypes.BOOLEAN.getId());
        logic.setBoolean("Value", value);
        ItemNBTUtils.setCompound(stack, "Logic", logic);

        handler.getHandler()
            .setStackInSlot(0, stack);
    }

    private void writeIntLiteral(PacketBuffer buffer) {
        int value = buffer.readInt();
        ItemStack stack = handler.getHandler()
            .getStackInSlot(0);
        if (stack == null) return;
        NBTTagCompound logic = new NBTTagCompound();

        logic.setString("Type", "LITERAL");
        logic.setString("ValueType", LogicTypes.INT.getId());
        logic.setInteger("Value", value);
        ItemNBTUtils.setCompound(stack, "Logic", logic);

        handler.getHandler()
            .setStackInSlot(0, stack);
    }

    private void writeLongLiteral(PacketBuffer buffer) {
        long value = buffer.readLong();
        ItemStack stack = handler.getHandler()
            .getStackInSlot(0);
        if (stack == null) return;
        NBTTagCompound logic = new NBTTagCompound();

        logic.setString("Type", "LITERAL");
        logic.setString("ValueType", LogicTypes.LONG.getId());
        logic.setLong("Value", value);
        ItemNBTUtils.setCompound(stack, "Logic", logic);

        handler.getHandler()
            .setStackInSlot(0, stack);
    }

    private void writeDoubleLiteral(PacketBuffer buffer) {
        boolean value = buffer.readBoolean();
        ItemStack stack = handler.getHandler()
            .getStackInSlot(0);
        if (stack == null) return;
        NBTTagCompound logic = new NBTTagCompound();

        logic.setString("Type", "LITERAL");
        logic.setString("ValueType", LogicTypes.DOUBLE.getId());
        logic.setBoolean("Value", value);
        ItemNBTUtils.setCompound(stack, "Logic", logic);

        handler.getHandler()
            .setStackInSlot(0, stack);
    }

    private void writeStringLiteral(PacketBuffer buffer) throws IOException {
        String value = buffer.readStringFromBuffer(36);
        ItemStack stack = handler.getHandler()
            .getStackInSlot(0);
        if (stack == null) return;
        NBTTagCompound logic = new NBTTagCompound();

        logic.setString("Type", "LITERAL");
        logic.setString("ValueType", LogicTypes.DOUBLE.getId());
        logic.setString("Value", value);
        ItemNBTUtils.setCompound(stack, "Logic", logic);

        handler.getHandler()
            .setStackInSlot(0, stack);
    }

}
