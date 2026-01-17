package ruiseki.omoshiroikamo.api.cable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.api.IMuiScreen;
import com.cleanroommc.modularui.factory.SidedPosGuiData;
import com.cleanroommc.modularui.screen.GuiContainerWrapper;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface ICablePart extends ICableNode {

    void doUpdate();

    void onChunkUnload();

    ItemStack getItemStack();

    void writeToNBT(NBTTagCompound tag);

    void readFromNBT(NBTTagCompound tag);

    @NotNull
    ModularPanel partPanel(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings);

    default Class<? extends IMuiScreen> getGuiContainer() {
        return GuiContainerWrapper.class;
    }

    AxisAlignedBB getCollisionBox();

    @SideOnly(Side.CLIENT)
    ResourceLocation getIcon();

    @SideOnly(Side.CLIENT)
    default ResourceLocation getBackIcon() {
        return getIcon();
    }
}
