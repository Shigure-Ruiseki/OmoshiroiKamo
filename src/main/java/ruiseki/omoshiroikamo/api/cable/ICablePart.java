package ruiseki.omoshiroikamo.api.cable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.module.cable.client.gui.data.PosSideGuiData;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractCableNetwork;

public interface ICablePart {

    String getId();

    ICable getCable();

    Class<? extends ICablePart> getBasePartType();

    ForgeDirection getSide();

    void setSide(ForgeDirection side);

    void setCable(ICable cable, ForgeDirection side);

    void onAttached();

    void onDetached();

    void doUpdate();

    void onChunkUnload();

    ItemStack getItemStack();

    void writeToNBT(NBTTagCompound tag);

    void readFromNBT(NBTTagCompound tag);

    @NotNull
    ModularPanel partPanel(PosSideGuiData data, PanelSyncManager syncManager, UISettings settings);

    EnumIO getIO();

    TileEntity getTargetTE();

    int getTickInterval();

    void setTickInterval(int tickInterval);

    int getPriority();

    void setPriority(int priority);

    int getChannel();

    void setChannel(int chanel);

    AxisAlignedBB getCollisionBox();

    @SideOnly(Side.CLIENT)
    ResourceLocation getIcon();

    @SideOnly(Side.CLIENT)
    default ResourceLocation getBackIcon() {return getIcon();}

    default AbstractCableNetwork<?> getNetwork() {
        ICable cable = getCable();
        return cable != null ? cable.getNetwork(getBasePartType()) : null;
    }
}
