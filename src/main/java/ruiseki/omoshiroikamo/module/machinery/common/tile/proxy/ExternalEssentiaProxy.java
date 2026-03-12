package ruiseki.omoshiroikamo.module.machinery.common.tile.proxy;

import net.minecraft.util.ChunkCoordinates;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;

/**
 * External Essentia Port Proxy.
 * Adapts an external IAspectContainer (Thaumcraft) to be used as a modular port.
 *
 * Design Pattern: Adapter Pattern
 * - Implements IAspectContainer by delegating all calls to the target TileEntity
 * - Uses AbstractExternalProxy for common proxy functionality
 */
public class ExternalEssentiaProxy extends AbstractExternalProxy implements IAspectContainer {

    public ExternalEssentiaProxy(TEMachineController controller, ChunkCoordinates targetPosition, EnumIO ioMode) {
        super(controller, targetPosition, ioMode);
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.ESSENTIA;
    }

    // ========== IAspectContainer Implementation (Delegated) ==========

    @Override
    public AspectList getAspects() {
        return delegate(IAspectContainer.class, IAspectContainer::getAspects, new AspectList());
    }

    @Override
    public void setAspects(AspectList aspects) {
        delegateVoid(IAspectContainer.class, c -> c.setAspects(aspects));
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return delegate(IAspectContainer.class, c -> c.doesContainerAccept(tag), false);
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        return delegate(IAspectContainer.class, c -> c.addToContainer(tag, amount), 0);
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        return delegate(IAspectContainer.class, c -> c.takeFromContainer(tag, amount), false);
    }

    @Override
    public boolean takeFromContainer(AspectList ot) {
        return delegate(IAspectContainer.class, c -> c.takeFromContainer(ot), false);
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return delegate(IAspectContainer.class, c -> c.doesContainerContainAmount(tag, amount), false);
    }

    @Override
    public boolean doesContainerContain(AspectList ot) {
        return delegate(IAspectContainer.class, c -> c.doesContainerContain(ot), false);
    }

    @Override
    public int containerContains(Aspect tag) {
        return delegate(IAspectContainer.class, c -> c.containerContains(tag), 0);
    }
}
