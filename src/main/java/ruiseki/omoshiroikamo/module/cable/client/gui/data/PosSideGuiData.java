package ruiseki.omoshiroikamo.module.cable.client.gui.data;

import java.util.Objects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.factory.GuiData;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public class PosSideGuiData extends GuiData {

    private final int x, y, z;
    private final ForgeDirection side;

    public PosSideGuiData(@NotNull EntityPlayer player, int x, int y, int z, ForgeDirection side) {
        super(player);
        this.x = x;
        this.y = y;
        this.z = z;
        this.side = side;
    }

    public PosSideGuiData(@NotNull EntityPlayer player, @NotNull BlockPos pos, ForgeDirection side) {
        super(player);
        Objects.requireNonNull(pos);
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.side = side;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public ForgeDirection getSide() {
        return side;
    }

    public double getSquaredDistance(double x, double y, double z) {
        double dx = this.x + 0.5 - x;
        double dy = this.y + 0.5 - y;
        double dz = this.z + 0.5 - z;
        return dx * dx + dy * dy + dz * dz;
    }

    public double getDistance(double x, double y, double z) {
        return Math.sqrt(getSquaredDistance(x, y, z));
    }

    public double getSquaredDistance(Entity entity) {
        return getSquaredDistance(entity.posX, entity.posY, entity.posZ);
    }

    public double getDistance(Entity entity) {
        return Math.sqrt(getSquaredDistance(entity));
    }

    public TileEntity getTileEntity() {
        // no blockpos needed
        return getWorld().getTileEntity(this.x, this.y, this.z);
    }

}
