package ruiseki.omoshiroikamo.client.gui.modularui2;

import net.minecraft.entity.player.EntityPlayer;

import com.cleanroommc.modularui.factory.GuiFactories;

import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractTE;

public class MGuis {

    public static void open(EntityPlayer player, AbstractTE entity) {
        GuiFactories.tileEntity()
            .open(player, entity.xCoord, entity.yCoord, entity.zCoord);
    }
}
