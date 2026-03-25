package ruiseki.omoshiroikamo.module.backpack.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.cleanroommc.modularui.factory.inventory.InventoryType;

import baubles.api.BaublesApi;
import ruiseki.omoshiroikamo.core.lib.LibMods;
import ruiseki.omoshiroikamo.core.network.CodecField;
import ruiseki.omoshiroikamo.core.network.PacketCodec;
import ruiseki.omoshiroikamo.module.backpack.common.block.BlockBackpack;
import ruiseki.omoshiroikamo.module.backpack.common.handler.BackpackWrapper;

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
        InventoryType type = InventoryType.getFromId(typeId);
        if (type == null || nbt == null) return;

        // Try to find the backpack by UUID in client inventory
        String uuid = nbt.getString("UUID");
        if (uuid == null || uuid.isEmpty()) return;

        ItemStack stack = findStackByUUID(player, uuid);
        if (stack != null) {
            stack.setTagCompound(nbt);
        }
    }

    private ItemStack findStackByUUID(EntityPlayer player, String uuid) {
        // Check held item
        ItemStack held = player.getHeldItem();
        if (isMatch(held, uuid)) return held;

        // Check inventory
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack s = player.inventory.getStackInSlot(i);
            if (isMatch(s, uuid)) return s;
        }

        // Check Baubles
        if (LibMods.Baubles.isLoaded()) {
            IInventory baubles = BaublesApi.getBaubles(player);
            if (baubles != null) {
                for (int i = 0; i < baubles.getSizeInventory(); i++) {
                    ItemStack s = baubles.getStackInSlot(i);
                    if (isMatch(s, uuid)) return s;
                }
            }
        }
        return null;
    }

    private boolean isMatch(ItemStack stack, String uuid) {
        if (stack == null || !(stack.getItem() instanceof BlockBackpack.ItemBackpack)) return false;
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && uuid.equals(tag.getString(BackpackWrapper.UUID_TAG));
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        InventoryType type = InventoryType.getFromId(typeId);
        if (type == null || nbt == null) return;

        // Use UUID tracking to find the correct backpack (not slot index!)
        String uuid = nbt.getString("UUID");
        if (uuid == null || uuid.isEmpty()) return;

        ItemStack stack = findStackByUUID(player, uuid);
        if (stack != null) {
            stack.setTagCompound(nbt);
        }
    }
}
