package ruiseki.omoshiroikamo.core.integration.waila;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import cpw.mods.fml.common.registry.GameData;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.SpecialChars;
import ruiseki.omoshiroikamo.api.block.ICraftingTile;
import ruiseki.omoshiroikamo.api.block.ISidedIO;
import ruiseki.omoshiroikamo.api.client.IProgressTile;
import ruiseki.omoshiroikamo.api.energy.IOKEnergyTile;
import ruiseki.omoshiroikamo.api.gas.GasTankInfo;
import ruiseki.omoshiroikamo.api.gas.IGasHandler;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import vazkii.botania.api.mana.IManaBlock;

public class WailaUtils {

    public static String getProgress(IProgressTile handler) {
        float progress = handler.getProgress();
        return LibMisc.LANG.localize("gui.progress", progress * 100);
    }

    public static String getCraftingState(ICraftingTile handler) {
        return LibMisc.LANG.localize(
            "gui.craftingState." + handler.getCraftingState()
                .getName());
    }

    public static String getEnergyTransfer(IOKEnergyTile handler) {
        return LibMisc.LANG.localize("gui.energy_transfer", handler.getEnergyTransfer());
    }

    public static List<String> getFluidTooltip(IFluidHandler handler) {
        if (handler == null) return new ArrayList<>();

        List<String> list = new ArrayList<>();
        FluidTankInfo[] tanks = handler.getTankInfo(ForgeDirection.UNKNOWN);

        if (tanks == null) return list;

        for (FluidTankInfo tank : tanks) {
            if (tank == null) continue;

            boolean empty = tank.fluid == null;

            list.add(
                SpecialChars.getRenderString(
                    "waila.fluid",
                    empty ? "EMPTYFLUID"
                        : tank.fluid.getFluid()
                            .getName(),
                    empty ? "EMPTYFLUID" : tank.fluid.getLocalizedName(),
                    String.valueOf(empty ? 0 : tank.fluid.amount),
                    String.valueOf(tank.capacity)));
        }
        return list;
    }

    public static List<String> getGasTooltip(IGasHandler handler) {
        if (handler == null) return new ArrayList<>();

        List<String> list = new ArrayList<>();
        GasTankInfo[] tanks = handler.getTankInfo(ForgeDirection.UNKNOWN);

        if (tanks == null) return list;

        for (GasTankInfo tank : tanks) {
            if (tank == null) continue;

            boolean empty = tank.gas == null;

            list.add(
                SpecialChars.getRenderString(
                    "waila.fluid",
                    empty ? "EMPTYFLUID"
                        : tank.gas.getGas()
                            .getName(),
                    empty ? "EMPTYFLUID"
                        : tank.gas.getGas()
                            .getLocalizedName(),
                    String.valueOf(empty ? 0 : tank.gas.amount),
                    String.valueOf(tank.capacity)));
        }
        return list;
    }

    public static String getManaToolTip(IManaBlock handler) {
        return LibMisc.LANG.localize("gui.mana_info", handler.getCurrentMana());
    }

    public static String getInventoryTooltip(IInventory inv) {
        String renderStr = "";
        if (inv == null) return null;

        int index = 1;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);

            if (stack == null || stack.getItem() == null) {
                continue;
            }
            String name = GameData.getItemRegistry()
                .getNameForObject(stack.getItem());
            renderStr += SpecialChars.getRenderString(
                "waila.stack",
                String.valueOf(index),
                name,
                String.valueOf(stack.stackSize),
                String.valueOf(stack.getItemDamage()));

        }

        return renderStr;
    }

    public static String getSideIOTooltip(ISidedIO handler, ForgeDirection direction) {
        if (handler == null) return null;
        ISidedIO.IO io = handler.getSideIO(direction);
        return LibMisc.LANG.localize(io.getName());
    }

    public static Vec3 getLocalHit(IWailaDataAccessor accessor) {
        if (accessor == null) return null;

        MovingObjectPosition mop = accessor.getPosition();
        if (mop == null || mop.hitVec == null) return null;

        return Vec3.createVectorHelper(
            mop.hitVec.xCoord - mop.blockX,
            mop.hitVec.yCoord - mop.blockY,
            mop.hitVec.zCoord - mop.blockZ);
    }

}
