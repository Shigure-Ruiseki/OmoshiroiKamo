package ruiseki.omoshiroikamo.module.storage.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;
import com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry;

import ruiseki.omoshiroikamo.core.block.BlockOK;
import ruiseki.omoshiroikamo.core.block.property.BlockPropertyReg;
import ruiseki.omoshiroikamo.core.block.property.BlockStateProperties;
import ruiseki.omoshiroikamo.core.block.property.BooleanProperty;
import ruiseki.omoshiroikamo.core.block.property.DirectionProperty;
import ruiseki.omoshiroikamo.core.helper.DirectionHelpers;
import ruiseki.omoshiroikamo.module.storage.common.tileentity.TEBarrel;

public class BlockBarrel extends BlockOK {

    @BlockPropertyReg
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    @BlockPropertyReg
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    public BlockBarrel(String name) {
        super(name, TEBarrel.class, Material.wood);
        this.rotatable = true;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, entity, stack);
        BlockState state = BlockPropertyRegistry.getBlockState(world, x, y, z);
        state.setPropertyValue(
            FACING,
            DirectionHelpers.yawToDirection6(entity)
                .getOpposite());
        state.place(world, x, y, z);
        state.close();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        BlockState state = BlockPropertyRegistry.getBlockState(world, x, y, z);
        state.setPropertyValue(OPEN, !state.getPropertyValue(OPEN));
        state.place(world, x, y, z);
        state.close();

        return true;
    }
}
