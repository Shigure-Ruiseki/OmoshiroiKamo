package ruiseki.omoshiroikamo.core.block.orientable;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.core.tileentity.IOrientable;

public class MetaRotation implements IOrientable {

    private final IBlockAccess world;
    private final int x;
    private final int y;
    private final int z;

    public MetaRotation(final IBlockAccess world, final int x, final int y, final int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean canBeRotated() {
        return true;
    }

    @Override
    public ForgeDirection getForward() {
        int meta = this.world.getBlockMetadata(this.x, this.y, this.z);
        return ForgeDirection.getOrientation(meta & 3);
    }

    @Override
    public ForgeDirection getUp() {
        return ForgeDirection.UP;
    }

    @Override
    public void setOrientation(final ForgeDirection forward, final ForgeDirection up) {
        if (this.world instanceof World) {
            ((World) this.world).setBlockMetadataWithNotify(this.x, this.y, this.z, up.ordinal(), 1 + 2);
        } else {
            throw new IllegalStateException(
                this.world.getClass()
                    .getName() + " received, expected World");
        }
    }
}
