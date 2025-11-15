package ruiseki.omoshiroikamo.api.item;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.item.InventoryIterator;
import com.gtnewhorizon.gtnhlib.item.SimpleItemIO;
import com.gtnewhorizon.gtnhlib.item.StandardInventoryIterator;

import ruiseki.omoshiroikamo.api.io.SlotDefinition;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.TEQuantumExtractor;

public class OKItemIO extends SimpleItemIO {

    private final TEQuantumExtractor inv;
    private final ForgeDirection side;
    private final int[] allSlots;

    public OKItemIO(TEQuantumExtractor inv, ForgeDirection side, int[] allSlots) {
        this.inv = inv;
        this.side = side;
        this.allSlots = allSlots;
    }

    public OKItemIO(TEQuantumExtractor inv, int[] allSlots) {
        this(inv, ForgeDirection.UNKNOWN, allSlots);
    }

    public OKItemIO(TEQuantumExtractor inv, ForgeDirection side, SlotDefinition definition) {
        this(inv, side, definition.getAllItemSlots());
    }

    public OKItemIO(TEQuantumExtractor inv, SlotDefinition definition) {
        this(inv, ForgeDirection.UNKNOWN, definition);
    }

    @Override
    protected @NotNull InventoryIterator iterator(int[] allowedSlots) {
        return new StandardInventoryIterator(inv, side, allSlots != null ? allSlots : new int[0]);
    }
}
