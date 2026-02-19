package ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.node;

import net.minecraftforge.common.util.ForgeDirection;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record ReaderRef(int x, int y, int z, ForgeDirection side) {}
