package ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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
    public static final int SET_FLOAT_LITERAL = 103;
    public static final int SET_DOUBLE_LITERAL = 104;
    public static final int SET_STRING_LITERAL = 105;

    public static final int SET_IF_LOGIC = 200;
    public static final int SET_AND_LOGIC = 201;
    public static final int SET_NAND_LOGIC = 202;

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

        if (id == SET_FLOAT_LITERAL) {
            writeFloatLiteral(buf);
        }

        if (id == SET_DOUBLE_LITERAL) {
            writeDoubleLiteral(buf);
        }

        if (id == SET_STRING_LITERAL) {
            writeStringLiteral(buf);
        }

        if (id == SET_IF_LOGIC) {
            writeIfLogic();
        }

        if (id == SET_AND_LOGIC) {
            writeAndLogic();
        }

        if (id == SET_NAND_LOGIC) {
            writeNAndLogic();
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

    private void writeFloatLiteral(PacketBuffer buffer) {
        float value = buffer.readFloat();
        ItemStack stack = handler.getHandler()
            .getStackInSlot(0);
        if (stack == null) return;
        NBTTagCompound logic = new NBTTagCompound();

        logic.setString("Type", "LITERAL");
        logic.setString("ValueType", LogicTypes.FLOAT.getId());
        logic.setFloat("Value", value);
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

    private void writeIfLogic() {
        ItemStack target = handler.getHandler()
            .getStackInSlot(0);
        if (target == null) return;

        ItemStack condS = handler.getHandler()
            .getStackInSlot(1);
        ItemStack thenS = handler.getHandler()
            .getStackInSlot(2);
        ItemStack elseS = handler.getHandler()
            .getStackInSlot(3);

        if (condS == null || thenS == null) return;

        NBTTagCompound condLogic = ItemNBTUtils.getCompound(condS, "Logic", false);
        NBTTagCompound thenLogic = ItemNBTUtils.getCompound(thenS, "Logic", false);
        NBTTagCompound elseLogic = elseS != null ? ItemNBTUtils.getCompound(elseS, "Logic", false) : null;

        if (condLogic == null || thenLogic == null) return;

        NBTTagCompound logic = new NBTTagCompound();
        logic.setString("Type", "OP");
        logic.setString("Op", "IF");

        NBTTagList children = new NBTTagList();
        children.appendTag(condLogic.copy());
        children.appendTag(thenLogic.copy());

        if (elseLogic != null) {
            children.appendTag(elseLogic.copy());
        }

        logic.setTag("Children", children);

        ItemNBTUtils.setCompound(target, "Logic", logic);
        handler.getHandler()
            .setStackInSlot(0, target);
    }

    private void writeAndLogic() {
        ItemStack target = handler.getHandler()
            .getStackInSlot(0);
        if (target == null) return;

        ItemStack condS = handler.getHandler()
            .getStackInSlot(1);
        ItemStack thenS = handler.getHandler()
            .getStackInSlot(2);

        if (condS == null || thenS == null) return;

        NBTTagCompound condLogic = ItemNBTUtils.getCompound(condS, "Logic", false);
        NBTTagCompound thenLogic = ItemNBTUtils.getCompound(thenS, "Logic", false);

        if (condLogic == null || thenLogic == null) return;

        NBTTagCompound logic = new NBTTagCompound();
        logic.setString("Type", "OP");
        logic.setString("Op", "AND");

        NBTTagList children = new NBTTagList();
        children.appendTag(condLogic.copy());
        children.appendTag(thenLogic.copy());

        logic.setTag("Children", children);

        ItemNBTUtils.setCompound(target, "Logic", logic);
        handler.getHandler()
            .setStackInSlot(0, target);
    }

    private void writeNAndLogic() {
        ItemStack target = handler.getHandler()
            .getStackInSlot(0);
        if (target == null) return;

        ItemStack condS = handler.getHandler()
            .getStackInSlot(1);
        ItemStack thenS = handler.getHandler()
            .getStackInSlot(2);

        if (condS == null || thenS == null) return;

        NBTTagCompound condLogic = ItemNBTUtils.getCompound(condS, "Logic", false);
        NBTTagCompound thenLogic = ItemNBTUtils.getCompound(thenS, "Logic", false);

        if (condLogic == null || thenLogic == null) return;

        NBTTagCompound logic = new NBTTagCompound();
        logic.setString("Type", "OP");
        logic.setString("Op", "NAND");

        NBTTagList children = new NBTTagList();
        children.appendTag(condLogic.copy());
        children.appendTag(thenLogic.copy());

        logic.setTag("Children", children);

        ItemNBTUtils.setCompound(target, "Logic", logic);
        handler.getHandler()
            .setStackInSlot(0, target);
    }

}
