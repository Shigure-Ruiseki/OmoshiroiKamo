package ruiseki.omoshiroikamo.module.cable.common.network.logic.node;

import net.minecraftforge.common.util.ForgeDirection;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record ReaderRef(int x, int y, int z, ForgeDirection side) {}
