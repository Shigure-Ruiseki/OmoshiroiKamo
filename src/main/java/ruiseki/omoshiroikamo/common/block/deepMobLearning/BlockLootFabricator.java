package ruiseki.omoshiroikamo.common.block.deepMobLearning;

import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractBlock;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractTE;

public class BlockLootFabricator extends AbstractBlock<AbstractTE> {

    protected BlockLootFabricator() {
        super(ModObject.blockLootFabricator.unlocalisedName, null);
    }

    public static BlockLootFabricator create() {
        return new BlockLootFabricator();
    }

    @Override
    public int getRenderType() {
        return ModelISBRH.JSON_ISBRH_ID;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

}
