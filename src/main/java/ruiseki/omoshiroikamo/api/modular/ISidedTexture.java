package ruiseki.omoshiroikamo.api.modular;

import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public interface ISidedTexture {

    IIcon getTexture(ForgeDirection side, int renderPass);
}
