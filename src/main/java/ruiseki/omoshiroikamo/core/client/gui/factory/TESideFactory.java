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
import com.cleanroommc.modularui.factory.GuiManager;
import com.cleanroommc.modularui.network.NetworkUtils;
import com.cleanroommc.modularui.screen.GuiContainerWrapper;
import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.screen.ModularScreen;

import ruiseki.omoshiroikamo.core.client.gui.data.PosSideGuiData;
import ruiseki.omoshiroikamo.core.lib.LibResources;

public class TESideFactory extends AbstractUIFactory<PosSideGuiData> {

    public static final TESideFactory INSTANCE = new TESideFactory();
    private Class<? extends IMuiScreen> customScreenClass;

    private TESideFactory() {
        super(LibResources.PREFIX_MOD + "side_tile");
    }

    public void open(EntityPlayer player, int x, int y, int z, ForgeDirection side) {
        Objects.requireNonNull(player);
        PosSideGuiData data = new PosSideGuiData(player, x, y, z, side);
        GuiManager.open(this, data, (EntityPlayerMP) player);
    }

    @Override
    public @NotNull IGuiHolder<PosSideGuiData> getGuiHolder(PosSideGuiData data) {
        return Objects.requireNonNull(castGuiHolder(data.getTileEntity()), "Found TileEntity is not a gui holder!");
    }

    @Override
    public boolean canInteractWith(EntityPlayer player, PosSideGuiData guiData) {
        return player == guiData.getPlayer() && guiData.getTileEntity() != null
            && guiData.getSquaredDistance(player) <= 64;
    }

    @Override
    public void writeGuiData(PosSideGuiData guiData, PacketBuffer buffer) {
        buffer.writeVarIntToBuffer(guiData.getX());
        buffer.writeVarIntToBuffer(guiData.getY());
        buffer.writeVarIntToBuffer(guiData.getZ());
        NetworkUtils.writeEnumValue(buffer, guiData.getSide());
    }

    @Override
    public @NotNull PosSideGuiData readGuiData(EntityPlayer player, PacketBuffer buffer) {
        return new PosSideGuiData(
            player,
            buffer.readVarIntFromBuffer(),
            buffer.readVarIntFromBuffer(),
            buffer.readVarIntFromBuffer(),
            NetworkUtils.readEnumValue(buffer, ForgeDirection.class));
    }

    @Override
    public IMuiScreen createScreenWrapper(ModularContainer container, ModularScreen screen) {
        if (customScreenClass != null) {
            try {
                return customScreenClass.getConstructor(ModularContainer.class, ModularScreen.class)
                    .newInstance(container, screen);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create custom screen", e);
            }
        }
        return new GuiContainerWrapper(container, screen);
    }

    public TESideFactory setGuiContainer(Class<? extends IMuiScreen> clazz) {
        this.customScreenClass = clazz;
        return this;
    }

}
