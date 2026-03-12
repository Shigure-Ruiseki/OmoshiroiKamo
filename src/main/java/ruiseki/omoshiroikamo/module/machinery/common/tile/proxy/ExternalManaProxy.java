package ruiseki.omoshiroikamo.module.machinery.common.tile.proxy;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;

/**
 * External Mana Port Proxy.
 * Adapts an external mana pool (Botania) to be used as a modular port.
 *
 * Design Pattern: Adapter Pattern
 * - Implements IManaPool and ISparkAttachable by delegating to the target TileEntity
 * - Uses AbstractExternalProxy for common proxy functionality
 */
public class ExternalManaProxy extends AbstractExternalProxy implements IManaPool, ISparkAttachable {

    public ExternalManaProxy(TEMachineController controller, ChunkCoordinates targetPosition, EnumIO ioMode) {
        super(controller, targetPosition, ioMode);
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.MANA;
    }

    // ========== IManaPool Implementation (Delegated) ==========

    @Override
    public int getCurrentMana() {
        return delegate(IManaPool.class, IManaPool::getCurrentMana, 0);
    }

    @Override
    public boolean isFull() {
        return delegate(IManaPool.class, IManaPool::isFull, false);
    }

    @Override
    public void recieveMana(int mana) {
        delegateVoid(IManaPool.class, pool -> pool.recieveMana(mana));
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return delegate(IManaPool.class, IManaPool::canRecieveManaFromBursts, false);
    }

    @Override
    public boolean isOutputtingPower() {
        return delegate(IManaPool.class, IManaPool::isOutputtingPower, false);
    }

    // ========== ISparkAttachable Implementation (Delegated) ==========

    @Override
    public boolean canAttachSpark(ItemStack stack) {
        return delegate(ISparkAttachable.class, s -> s.canAttachSpark(stack), false);
    }

    @Override
    public void attachSpark(ISparkEntity entity) {
        delegateVoid(ISparkAttachable.class, s -> s.attachSpark(entity));
    }

    @Override
    public ISparkEntity getAttachedSpark() {
        return delegate(ISparkAttachable.class, ISparkAttachable::getAttachedSpark, null);
    }

    @Override
    public boolean areIncomingTranfersDone() {
        return delegate(ISparkAttachable.class, ISparkAttachable::areIncomingTranfersDone, true);
    }

    @Override
    public int getAvailableSpaceForMana() {
        return delegate(ISparkAttachable.class, ISparkAttachable::getAvailableSpaceForMana, 0);
    }
}
