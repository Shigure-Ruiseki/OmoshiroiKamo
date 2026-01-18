package ruiseki.omoshiroikamo.core.client.gui.factory;

import java.util.Objects;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.IMuiScreen;
import com.cleanroommc.modularui.factory.AbstractUIFactory;
import com.cleanroommc.modularui.factory.GuiData;
import com.cleanroommc.modularui.factory.GuiManager;
import com.cleanroommc.modularui.factory.SidedPosGuiData;
import com.cleanroommc.modularui.network.NetworkUtils;
import com.cleanroommc.modularui.screen.GuiContainerWrapper;
import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.screen.ModularScreen;

import ruiseki.omoshiroikamo.api.block.IOKGuiHolder;
import ruiseki.omoshiroikamo.core.lib.LibResources;

public class TESideFactory extends AbstractUIFactory<SidedPosGuiData> {

    public static final TESideFactory INSTANCE = new TESideFactory();

    private TESideFactory() {
        super(LibResources.PREFIX_MOD + "side_tile");
    }

    public void open(EntityPlayer player, int x, int y, int z, ForgeDirection side) {
        Objects.requireNonNull(player);
        SidedPosGuiData data = new SidedPosGuiData(player, x, y, z, side);
        GuiManager.open(this, data, (EntityPlayerMP) player);
    }

    @Override
    public @NotNull IGuiHolder<SidedPosGuiData> getGuiHolder(SidedPosGuiData data) {
        return Objects.requireNonNull(castGuiHolder(data.getTileEntity()), "Found TileEntity is not a gui holder!");
    }

    @Override
    public boolean canInteractWith(EntityPlayer player, SidedPosGuiData guiData) {
        return player == guiData.getPlayer() && guiData.getTileEntity() != null
            && guiData.getSquaredDistance(player) <= 64;
    }

    @Override
    public void writeGuiData(SidedPosGuiData guiData, PacketBuffer buffer) {
        buffer.writeVarIntToBuffer(guiData.getX());
        buffer.writeVarIntToBuffer(guiData.getY());
        buffer.writeVarIntToBuffer(guiData.getZ());
        NetworkUtils.writeEnumValue(buffer, guiData.getSide());
    }

    @Override
    public @NotNull SidedPosGuiData readGuiData(EntityPlayer player, PacketBuffer buffer) {
        return new SidedPosGuiData(
            player,
            buffer.readVarIntFromBuffer(),
            buffer.readVarIntFromBuffer(),
            buffer.readVarIntFromBuffer(),
            NetworkUtils.readEnumValue(buffer, ForgeDirection.class));
    }

    @Override
    public IMuiScreen createScreenWrapper(ModularContainer container, ModularScreen screen) {
        GuiData data = container.getGuiData();

        if (data instanceof IOKGuiHolder<?>okHolder) {
            return okHolder.createGuiContainer(container, screen);
        }

        return new GuiContainerWrapper(container, screen);
    }

}
