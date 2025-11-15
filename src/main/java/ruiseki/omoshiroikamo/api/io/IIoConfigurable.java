package ruiseki.omoshiroikamo.api.io;

import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public interface IIoConfigurable {

    public IoMode toggleIoModeForFace(ForgeDirection faceHit, IoType type);

    public boolean supportsMode(ForgeDirection faceHit, IoMode mode);

    public void setIoMode(ForgeDirection faceHit, IoMode mode, IoType type);

    public IoMode getIoMode(ForgeDirection face, IoType type);

    public void clearAllIoModes(IoType type);

    BlockPos getLocation();
}
