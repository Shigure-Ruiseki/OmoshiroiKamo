package ruiseki.omoshiroikamo.module.backpack.common.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.cleanroommc.modularui.factory.inventory.InventoryType;
import com.cleanroommc.modularui.factory.inventory.InventoryTypes;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import ruiseki.omoshiroikamo.api.item.BaublesUtils;
import ruiseki.omoshiroikamo.core.common.network.PacketUtil;
import ruiseki.omoshiroikamo.core.lib.LibMods;

public class PacketBackpackNBT implements IMessage, IMessageHandler<PacketBackpackNBT, IMessage> {

    private int slot;
    private NBTTagCompound nbt;
    private String typeId;

    public PacketBackpackNBT() {}

    public PacketBackpackNBT(int slot, NBTTagCompound nbt, InventoryType type) {
        this.slot = slot;
        this.nbt = nbt;
        this.typeId = type.getId();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slot = buf.readInt();
        nbt = PacketUtil.readNBTTagCompound(buf);
        typeId = PacketUtil.readStringSafe(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slot);
        PacketUtil.writeNBTTagCompound(nbt, buf);
        PacketUtil.writeStringSafe(buf, typeId);
    }

    @Override
    public IMessage onMessage(PacketBackpackNBT message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        InventoryType type = InventoryType.getFromId(message.typeId);
        if (type == null || message.nbt == null) return null;

        ItemStack stack = null;
        if (type == InventoryTypes.PLAYER) {
            stack = player.inventory.getStackInSlot(message.slot);
        }

        if (type == InventoryTypes.BAUBLES && LibMods.Baubles.isLoaded()) {
            IInventory baublesInventory = BaublesUtils.instance()
                .getBaubles(player);
            stack = baublesInventory.getStackInSlot(message.slot);
        }
        if (stack != null) {
            stack.setTagCompound(message.nbt);
        }
        return null;
    }
}
