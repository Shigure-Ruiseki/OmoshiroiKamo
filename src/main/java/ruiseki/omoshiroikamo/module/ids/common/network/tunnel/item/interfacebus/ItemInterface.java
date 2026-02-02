package ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.interfacebus;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.BlockChest;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.factory.SidedPosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.ids.ICableNode;
import ruiseki.omoshiroikamo.api.item.ItemStackKeyPool;
import ruiseki.omoshiroikamo.api.item.ItemUtils;
import ruiseki.omoshiroikamo.core.common.util.RenderUtils;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsItems;
import ruiseki.omoshiroikamo.module.ids.common.network.AbstractPart;
import ruiseki.omoshiroikamo.module.ids.common.network.PartSettingPanel;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.ILogicNet;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.IItemNet;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.IItemPart;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.IItemQueryable;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.ItemIndex;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.ItemNetwork;

public class ItemInterface extends AbstractPart implements IItemPart, IItemQueryable, IItemInterface {

    private static final float WIDTH = 6f / 16f; // 6px
    private static final float DEPTH = 4f / 16f; // 4px

    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    private static final IModelCustom model = AdvancedModelLoader
        .loadModel(new ResourceLocation(LibResources.PREFIX_MODEL + "ids/base_bus.obj"));
    private static final ResourceLocation texture = new ResourceLocation(
        LibResources.PREFIX_ITEM + "ids/item_interface.png");

    private int lastHash = 0;

    public ItemInterface() {
        setTickInterval(100);
    }

    @Override
    public String getId() {
        return "item_interface";
    }

    @Override
    public List<Class<? extends ICableNode>> getBaseNodeTypes() {
        return Arrays.asList(IItemNet.class, ILogicNet.class);
    }

    @Override
    public void doUpdate() {
        if (!shouldTickNow()) return;

        int hash = calcInventoryHash();
        if (hash != lastHash) {
            lastHash = hash;
            markNetworkDirty();
        }
    }

    @Override
    public ItemStack getItemStack() {
        return IDsItems.ITEM_INTERFACE.newItemStack();
    }

    private int calcInventoryHash() {
        IInventory inv = getInventory();
        if (inv == null) return 0;

        int hash = 1;
        int[] slots = getSlots();

        for (int slot : slots) {
            ItemStack s = inv.getStackInSlot(slot);
            if (s != null && s.stackSize > 0) {
                hash = 31 * hash + ItemStackKeyPool.get(s).hash;
                hash = 31 * hash + s.stackSize;
            }
        }
        return hash;
    }

    private void markNetworkDirty() {
        ItemNetwork net = getItemNetwork();
        if (net != null) net.markDirty(getChannel());
    }

    @Override
    public void collectItems(ItemIndex index) {
        IInventory inv = getInventory();
        if (inv == null) return;

        int[] slots = getSlots();

        for (int slot : slots) {
            ItemStack s = inv.getStackInSlot(slot);
            if (s != null && s.stackSize > 0) {
                index.add(s);
            }
        }
    }

    @Override
    public @NotNull ModularPanel partPanel(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return PartSettingPanel.build(this);
    }

    @Override
    public EnumIO getIO() {
        return EnumIO.BOTH;
    }

    @Override
    public int getTransferLimit() {
        return Integer.MAX_VALUE;
    }

    @Override
    public ItemStack extract(ItemStack required, int amount) {
        if (amount <= 0) return null;

        IInventory inv = getInventory();
        if (inv == null) return null;

        ItemStack result = null;
        int remaining = amount;

        for (int slot : getSlots()) {
            if (remaining <= 0) break;

            ItemStack s = inv.getStackInSlot(slot);
            if (s == null || s.stackSize <= 0) continue;
            if (!ItemUtils.areStacksEqual(required, s)) continue;

            int take = Math.min(remaining, s.stackSize);
            ItemStack taken = inv.decrStackSize(slot, take);

            if (taken != null) {
                if (result == null) {
                    result = taken;
                } else {
                    result.stackSize += taken.stackSize;
                }
                remaining -= taken.stackSize;
            }
        }

        if (result != null) {
            inv.markDirty();
        }

        return result;
    }

    @Override
    public ItemStack insert(ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) return null;

        IInventory inv = getInventory();
        if (inv == null) return stack;

        ItemStack remaining = stack.copy();
        boolean changed = false;

        for (int slot : getAccessibleSlots(inv)) {
            if (remaining.stackSize <= 0) break;

            ItemStack target = inv.getStackInSlot(slot);

            if (target == null) {
                int add = Math.min(remaining.getMaxStackSize(), remaining.stackSize);
                ItemStack placed = remaining.splitStack(add);
                inv.setInventorySlotContents(slot, placed);
                changed = true;
            } else if (ItemUtils.areStacksEqual(target, remaining)) {
                int space = target.getMaxStackSize() - target.stackSize;
                if (space > 0) {
                    int add = Math.min(space, remaining.stackSize);
                    target.stackSize += add;
                    remaining.stackSize -= add;
                    changed = true;
                }
            }
        }

        if (changed) {
            inv.markDirty();
        }

        return remaining.stackSize > 0 ? remaining : null;
    }

    private int[] getAccessibleSlots(IInventory inv) {
        if (inv instanceof ISidedInventory sided) {
            return sided.getAccessibleSlotsFromSide(getSide().ordinal());
        }

        int size = inv.getSizeInventory();
        int[] slots = new int[size];
        for (int i = 0; i < size; i++) slots[i] = i;
        return slots;
    }

    @Override
    public AxisAlignedBB getCollisionBox() {
        return switch (getSide()) {
            case WEST -> AxisAlignedBB.getBoundingBox(0f, W_MIN, W_MIN, DEPTH, W_MAX, W_MAX);
            case EAST -> AxisAlignedBB.getBoundingBox(1f - DEPTH, W_MIN, W_MIN, 1f, W_MAX, W_MAX);
            case DOWN -> AxisAlignedBB.getBoundingBox(W_MIN, 0f, W_MIN, W_MAX, DEPTH, W_MAX);
            case UP -> AxisAlignedBB.getBoundingBox(W_MIN, 1f - DEPTH, W_MIN, W_MAX, 1f, W_MAX);
            case NORTH -> AxisAlignedBB.getBoundingBox(W_MIN, W_MIN, 0f, W_MAX, W_MAX, DEPTH);
            case SOUTH -> AxisAlignedBB.getBoundingBox(W_MIN, W_MIN, 1f - DEPTH, W_MAX, W_MAX, 1f);
            default -> null;
        };
    }

    @Override
    public void renderPart(Tessellator tess, float partialTicks) {
        GL11.glPushMatrix();

        RenderUtils.bindTexture(texture);

        rotateForSide(getSide());

        model.renderAll();

        GL11.glPopMatrix();
    }

    @Override
    public void renderItemPart(IItemRenderer.ItemRenderType type, ItemStack stack, Tessellator tess) {
        GL11.glPushMatrix();

        switch (type) {
            case ENTITY:
                GL11.glTranslatef(0f, 0f, -0.5f);
                break;
            case EQUIPPED, EQUIPPED_FIRST_PERSON:
                GL11.glTranslatef(0.25f, 0.5f, 0.25f);
                break;
            case INVENTORY:
                GL11.glTranslatef(0.5f, 0f, 0f);
                break;
            default:
                GL11.glTranslatef(0f, 0f, 0f);
                break;
        }

        rotateForSide(getSide());

        RenderUtils.bindTexture(texture);
        model.renderAll();

        GL11.glPopMatrix();
    }

    @Override
    public IInventory getInventory() {
        TileEntity te = getTargetTE();

        if (te instanceof TileEntityChest) {
            if (getWorld().getBlock(targetX(), targetY(), targetZ()) instanceof BlockChest chest) {
                return chest.func_149951_m(getWorld(), targetX(), targetY(), targetZ());
            }
            return null;
        }

        return te instanceof IInventory ? (IInventory) te : null;
    }

    @Override
    public int[] getSlots() {
        IInventory inv = getInventory();
        if (inv == null) return new int[0];

        if (inv instanceof ISidedInventory sided) {
            return sided.getAccessibleSlotsFromSide(getSide().ordinal());
        }

        int size = inv.getSizeInventory();
        int[] slots = new int[size];
        for (int i = 0; i < size; i++) slots[i] = i;
        return slots;
    }
}
