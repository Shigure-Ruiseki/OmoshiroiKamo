package ruiseki.omoshiroikamo.core.client.gui.factory;

import java.util.Objects;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.ModularUI;
import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.IMuiScreen;
import com.cleanroommc.modularui.factory.AbstractUIFactory;
import com.cleanroommc.modularui.factory.GuiManager;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.factory.inventory.InventoryType;
import com.cleanroommc.modularui.factory.inventory.InventoryTypes;
import com.cleanroommc.modularui.screen.GuiContainerWrapper;
import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.utils.Platform;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.core.lib.LibResources;

public class ItemFactory extends AbstractUIFactory<PlayerInventoryGuiData> {

    public static final ItemFactory INSTANCE = new ItemFactory();
    private Class<? extends IMuiScreen> customScreenClass;

    public void openFromPlayerInventory(EntityPlayer player, int index) {
        GuiManager
            .open(this, new PlayerInventoryGuiData(player, InventoryTypes.PLAYER, index), verifyServerSide(player));
    }

    public void openFromMainHand(EntityPlayer player) {
        openFromPlayerInventory(player, player.inventory.currentItem);
    }

    @SideOnly(Side.CLIENT)
    public void openFromPlayerInventoryClient(int index) {
        GuiManager
            .openFromClient(this, new PlayerInventoryGuiData(Platform.getClientPlayer(), InventoryTypes.PLAYER, index));
    }

    @SideOnly(Side.CLIENT)
    public void openFromBaublesClient(int index) {
        if (!ModularUI.Mods.BAUBLES.isLoaded()) {
            throw new IllegalArgumentException("Can't open UI for baubles item when bauble is not loaded!");
        }
        GuiManager.openFromClient(
            this,
            new PlayerInventoryGuiData(Platform.getClientPlayer(), InventoryTypes.BAUBLES, index));
    }

    private ItemFactory() {
        super(LibResources.PREFIX_MOD + "player_inv");
    }

    @Override
    public @NotNull IGuiHolder<PlayerInventoryGuiData> getGuiHolder(PlayerInventoryGuiData data) {
        return Objects.requireNonNull(
            castGuiHolder(
                data.getUsedItemStack()
                    .getItem()),
            "Item was not a gui holder!");
    }

    @Override
    public void writeGuiData(PlayerInventoryGuiData guiData, PacketBuffer buffer) {
        guiData.getInventoryType()
            .write(buffer);
        buffer.writeVarIntToBuffer(guiData.getSlotIndex());
    }

    @Override
    public @NotNull PlayerInventoryGuiData readGuiData(EntityPlayer player, PacketBuffer buffer) {
        return new PlayerInventoryGuiData(player, InventoryType.read(buffer), buffer.readVarIntFromBuffer());
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

    public ItemFactory setGuiContainer(Class<? extends IMuiScreen> clazz) {
        this.customScreenClass = clazz;
        return this;
    }
}
