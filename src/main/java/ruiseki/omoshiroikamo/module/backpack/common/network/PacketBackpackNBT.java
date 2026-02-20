package ruiseki.omoshiroikamo.module.backpack.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.cleanroommc.modularui.factory.inventory.InventoryType;
import com.cleanroommc.modularui.factory.inventory.InventoryTypes;

import ruiseki.omoshiroikamo.core.item.BaublesUtils;
import ruiseki.omoshiroikamo.core.lib.LibMods;
import ruiseki.omoshiroikamo.core.network.CodecField;
import ruiseki.omoshiroikamo.core.network.PacketCodec;

public class PacketBackpackNBT extends PacketCodec {

    @CodecField
    private int slot;
    @CodecField
    private NBTTagCompound nbt;
    @CodecField
    private String typeId;

    public PacketBackpackNBT() {}

    public PacketBackpackNBT(int slot, NBTTagCompound nbt, InventoryType type) {
        this.slot = slot;
        this.nbt = nbt;
        this.typeId = type.getId();
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {

    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        InventoryType type = InventoryType.getFromId(typeId);
        if (type == null || nbt == null) return;

        ItemStack stack = null;
        if (type == InventoryTypes.PLAYER) {
            stack = player.inventory.getStackInSlot(slot);
        }

        if (type == InventoryTypes.BAUBLES && LibMods.Baubles.isLoaded()) {
            IInventory baublesInventory = BaublesUtils.instance()
                .getBaubles(player);
            stack = baublesInventory.getStackInSlot(slot);
        }
        if (stack != null) {
            stack.setTagCompound(nbt);
        }
    }
}
