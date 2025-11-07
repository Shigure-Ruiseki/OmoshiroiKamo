package ruiseki.omoshiroikamo.common.item;

import java.util.List;
import java.util.Set;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.TargetingInfo;
import ruiseki.omoshiroikamo.api.energy.wire.IWireCoil;
import ruiseki.omoshiroikamo.api.energy.wire.IWireConnectable;
import ruiseki.omoshiroikamo.api.energy.wire.MaterialWireType;
import ruiseki.omoshiroikamo.api.energy.wire.WireNetHandler;
import ruiseki.omoshiroikamo.api.energy.wire.WireNetHandler.Connection;
import ruiseki.omoshiroikamo.api.energy.wire.WireType;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.material.MaterialEntry;
import ruiseki.omoshiroikamo.api.material.MaterialRegistry;
import ruiseki.omoshiroikamo.common.util.ItemNBTHelper;
import ruiseki.omoshiroikamo.common.util.WireUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.common.world.WireNetSaveData;

/*
 * This file contains code adapted from Immersive Engineering by BluSunrize.
 * Original project: https://github.com/BluSunrize/ImmersiveEngineering
 * License: "Blu's License of Common Sense" (https://github.com/BluSunrize/ImmersiveEngineering/blob/master/LICENSE)
 * Modifications made by: [Louis]
 * This adapted version complies with the original license terms.
 * It is intended for use in a standalone mod inspired by Immersive Engineering.
 */

public class ItemWireCoil extends ItemOK implements IWireCoil, IAdvancedTooltipProvider {

    @SideOnly(Side.CLIENT)
    protected IIcon baseIcon, overlayIcon;

    public ItemWireCoil() {
        super(ModObject.itemWireCoil);
        setMaxDamage(0);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = stack.getItemDamage();
        MaterialEntry material = MaterialRegistry.fromMeta(meta);
        return super.getUnlocalizedName(stack) + "." + material.getUnlocalizedName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
        for (MaterialEntry materialEntry : MaterialRegistry.all()) {
            int meta = materialEntry.meta;
            list.add(new ItemStack(this, 1, meta));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderPasses(int metadata) {
        return 2;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        return pass == 0 ? baseIcon : overlayIcon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage) {
        return baseIcon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        if (renderPass == 1) {
            MaterialEntry mat = MaterialRegistry.fromMeta(stack.getItemDamage() % LibResources.META1);
            return mat.getColor();
        }
        return 0xFFFFFF;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister) {
        baseIcon = iconRegister.registerIcon(LibResources.PREFIX_MOD + "wireCoil");
        overlayIcon = iconRegister.registerIcon(LibResources.PREFIX_MOD + "wireCoil_color");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean adv) {
        if (stack.getTagCompound() != null && stack.getTagCompound()
            .hasKey("linkingPos")) {
            int[] link = stack.getTagCompound()
                .getIntArray("linkingPos");
            if (link != null && link.length > 3) {
                list.add(
                    StatCollector.translateToLocalFormatted(
                        LibResources.DESC_INFO + "attachedToDim",
                        link[1],
                        link[2],
                        link[3],
                        link[0]));
            }
        }
    }

    @Override
    public WireType getWireType(ItemStack stack) {
        return MaterialWireType.get(stack.getItemDamage() % LibResources.META1);
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return false;
        }

        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (!(tileEntity instanceof IWireConnectable) || !((IWireConnectable) tileEntity).canConnect()) {
            return false;
        }

        TargetingInfo target = new TargetingInfo(side, hitX, hitY, hitZ);
        WireType type = getWireType(stack);

        if (!((IWireConnectable) tileEntity).canConnectCable(type, target)) {
            player.addChatMessage(new ChatComponentTranslation(LibResources.CHAT_WARN + "wrongCable"));
            return false;
        }

        if (!ItemNBTHelper.verifyExistance(stack, "linkingPos")) {
            // Lưu điểm kết nối đầu tiên
            ItemNBTHelper.setIntArray(stack, "linkingPos", new int[] { world.provider.dimensionId, x, y, z });
            if (stack.getTagCompound() == null) {
                stack.setTagCompound(new NBTTagCompound());
            }
            target.writeToNBT(stack.getTagCompound());
        } else {
            int[] pos = ItemNBTHelper.getIntArray(stack, "linkingPos", 0);
            if (pos.length != 4) {
                return false;
            }

            if (pos[0] != world.provider.dimensionId) {
                player.addChatMessage(new ChatComponentTranslation(LibResources.CHAT_WARN + "wrongDimension"));
            } else if (pos[1] == x && pos[2] == y && pos[3] == z) {
                player.addChatMessage(new ChatComponentTranslation(LibResources.CHAT_WARN + "sameConnection"));
            } else {
                TileEntity tileEntityLink = world.getTileEntity(pos[1], pos[2], pos[3]);
                if (!(tileEntityLink instanceof IWireConnectable)) {
                    player.addChatMessage(new ChatComponentTranslation(LibResources.CHAT_WARN + "invalidPoint"));
                } else {
                    int distance = (int) Math
                        .ceil(Math.sqrt(Math.pow(pos[1] - x, 2) + Math.pow(pos[2] - y, 2) + Math.pow(pos[3] - z, 2)));

                    if (distance > type.getMaxLength()) {
                        player.addChatMessage(new ChatComponentTranslation(LibResources.CHAT_WARN + "tooFar"));
                    } else {
                        IWireConnectable nodeHere = (IWireConnectable) tileEntity;
                        IWireConnectable nodeLink = (IWireConnectable) tileEntityLink;

                        if (!nodeLink.canConnectCable(type, target)) {
                            player
                                .addChatMessage(new ChatComponentTranslation(LibResources.CHAT_WARN + "invalidPoint"));
                        } else {
                            boolean exists = false;
                            Set<Connection> conns = WireNetHandler.INSTANCE
                                .getConnections(world, WireUtils.toCC(nodeHere));
                            if (conns != null) {
                                for (Connection con : conns) {
                                    if (con.end.equals(WireUtils.toCC(nodeLink))) {
                                        exists = true;
                                        break;
                                    }
                                }
                            }

                            if (exists) {
                                player.addChatMessage(
                                    new ChatComponentTranslation(LibResources.CHAT_WARN + "connectionExists"));
                            } else {
                                Vec3 vecHere = nodeHere.getRaytraceOffset(nodeLink)
                                    .addVector(x, y, z);
                                Vec3 vecLink = nodeLink.getRaytraceOffset(nodeHere)
                                    .addVector(pos[1], pos[2], pos[3]);

                                if (WireUtils.canBlocksSeeOther(
                                    world,
                                    new ChunkCoordinates(x, y, z),
                                    new ChunkCoordinates(pos[1], pos[2], pos[3]),
                                    vecHere,
                                    vecLink)) {
                                    TargetingInfo targetLink = TargetingInfo.readFromNBT(stack.getTagCompound());

                                    WireNetHandler.INSTANCE.addConnection(
                                        world,
                                        WireUtils.toCC(nodeHere),
                                        WireUtils.toCC(nodeLink),
                                        distance,
                                        type);
                                    nodeHere.connectCable(type, target);
                                    nodeLink.connectCable(type, targetLink);
                                    WireNetSaveData.setDirty(world.provider.dimensionId);

                                    if (!player.capabilities.isCreativeMode) {
                                        stack.stackSize--;
                                    }

                                    ((TileEntity) nodeHere).markDirty();
                                    world.addBlockEvent(x, y, z, tileEntity.getBlockType(), -1, 0);
                                    world.markBlockForUpdate(x, y, z);

                                    ((TileEntity) nodeLink).markDirty();
                                    world.addBlockEvent(pos[1], pos[2], pos[3], tileEntityLink.getBlockType(), -1, 0);
                                    world.markBlockForUpdate(pos[1], pos[2], pos[3]);
                                } else {
                                    player.addChatMessage(
                                        new ChatComponentTranslation(LibResources.CHAT_WARN + "cantSee"));
                                }
                            }
                        }
                    }
                }
            }
            ItemNBTHelper.remove(stack, "linkingPos");
            ItemNBTHelper.remove(stack, "side");
            ItemNBTHelper.remove(stack, "hitX");
            ItemNBTHelper.remove(stack, "hitY");
            ItemNBTHelper.remove(stack, "hitZ");
        }

        return true;
    }

    @Override
    public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {

    }

    @Override
    public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        int meta = itemstack.getItemDamage();
        MaterialEntry material = MaterialRegistry.fromMeta(meta);
        MaterialWireType materialWireType = MaterialWireType.get(meta);
        list.add(String.format("§7Material:§f %s", material.getName()));
        list.add(
            String.format(
                "§7Voltage Tier:§f %s",
                material.getVoltageTier()
                    .getDisplayName()));
        list.add(String.format("§7Max Transfer:§f %,d RF/t", materialWireType.getTransferRate() / 10));
        list.add(String.format("§7Max Length:§f %,d", materialWireType.getMaxLength()));
        list.add(String.format("§7Loss Ratio:§f %.3f", materialWireType.getLossRatio()));
    }

    @Override
    public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {

    }
}
