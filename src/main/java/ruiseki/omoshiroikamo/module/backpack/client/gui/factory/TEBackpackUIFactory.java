package ruiseki.omoshiroikamo.module.backpack.client.gui.factory;

import java.util.Objects;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.IMuiScreen;
import com.cleanroommc.modularui.factory.AbstractUIFactory;
import com.cleanroommc.modularui.factory.GuiManager;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.screen.ModularScreen;

import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.backpack.client.gui.container.BackpackGuiContainer;

public class TEBackpackUIFactory extends AbstractUIFactory<PosGuiData> {

    public static final TEBackpackUIFactory INSTANCE = new TEBackpackUIFactory();

    private TEBackpackUIFactory() {
        super(LibResources.PREFIX_MOD + "tile_entity");
    }

    public void open(EntityPlayer player, int x, int y, int z) {
        Objects.requireNonNull(player);
        PosGuiData data = new PosGuiData(player, x, y, z);
        GuiManager.open(this, data, (EntityPlayerMP) player);
    }

    @Override
    public @NotNull IGuiHolder<PosGuiData> getGuiHolder(PosGuiData data) {
        return Objects.requireNonNull(castGuiHolder(data.getTileEntity()), "Found TileEntity is not a gui holder!");
    }

    @Override
    public boolean canInteractWith(EntityPlayer player, PosGuiData guiData) {
        return player == guiData.getPlayer() && guiData.getTileEntity() != null
            && guiData.getSquaredDistance(player) <= 64;
    }

    @Override
    public void writeGuiData(PosGuiData guiData, PacketBuffer buffer) {
        buffer.writeVarIntToBuffer(guiData.getX());
        buffer.writeVarIntToBuffer(guiData.getY());
        buffer.writeVarIntToBuffer(guiData.getZ());
    }

    @Override
    public @NotNull PosGuiData readGuiData(EntityPlayer player, PacketBuffer buffer) {
        return new PosGuiData(
            player,
            buffer.readVarIntFromBuffer(),
            buffer.readVarIntFromBuffer(),
            buffer.readVarIntFromBuffer());
    }

    @Override
    public IMuiScreen createScreenWrapper(ModularContainer container, ModularScreen screen) {
        return new BackpackGuiContainer(container, screen);
    }
}
