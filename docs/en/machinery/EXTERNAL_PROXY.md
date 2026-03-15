# External Port Proxy System

## Overview

The External Port Proxy is a design pattern in the Modular Machinery system that **integrates external blocks (chests, tanks, energy storage, etc.) as part of the machine**.

This system achieves the following through a **fusion of Adapter + Proxy patterns**:

- Adapts external TileEntities to the IModularPort interface (Adapter)
- Access control and error handling (Proxy)
- Lazy loading and caching (Virtual Proxy)
- Unified code structure (Template Method)

## Architecture

### Class Hierarchy

```
IModularPort (interface)
    ↑
IExternalPortProxy (marker interface)
    ↑
AbstractExternalProxy (abstract base class)
    ↑
    ├── ExternalItemProxy (IInventory implementation)
    ├── ExternalFluidProxy (IFluidHandler implementation)
    ├── ExternalEnergyProxy (IOKEnergyIO implementation)
    ├── ExternalGasProxy (IGasHandler implementation)
    ├── ExternalEssentiaProxy (IAspectContainer implementation)
    └── ExternalManaProxy (IManaPool implementation)
```

### Design Patterns

#### 1. **Adapter Pattern (Primary)**
Converts external interfaces (IInventory, IFluidHandler, etc.) to IModularPort

```java
public class ExternalItemProxy extends AbstractExternalProxy implements IInventory {
    // Adapts IInventory → IModularPort
}
```

#### 2. **Proxy Pattern (Access Control)**
Controls access to external TileEntities and provides error handling

```java
protected <T, R> R delegate(Class<T> targetType, Function<T, R> operation, R defaultValue) {
    try {
        T target = getTargetAs(targetType);  // Type check
        return target != null ? operation.apply(target) : defaultValue;
    } catch (Exception e) {
        notifyError();  // Error notification
        return defaultValue;
    }
}
```

#### 3. **Virtual Proxy (Lazy Loading)**
Delays TileEntity retrieval and optimizes with caching

```java
@Override
public TileEntity getTargetTileEntity() {
    if (targetTileEntity == null || targetTileEntity.isInvalid()) {
        if (getWorld() != null) {
            targetTileEntity = getWorld().getTileEntity(getX(), getY(), getZ());
        }
    }
    return targetTileEntity;
}
```

#### 4. **Template Method Pattern (Unification)**
Provides unified delegation pattern via `delegate()` method

```java
// All proxies implement using the same pattern
@Override
public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    return delegate(IFluidHandler.class, h -> h.fill(from, resource, doFill), 0);
}
```

#### 5. **Factory Pattern (Creation)**
Registers proxy factories in `MachineryIntegration`

```java
BlockResolver.registerProxyFactory(IPortType.Type.FLUID, (controller, coords, tile, io) -> {
    if (tile instanceof IFluidHandler) {
        return new ExternalFluidProxy(controller, coords, io);
    }
    return null;
});
```

---

## Implemented Proxies

### 1. ExternalItemProxy
- **Interface**: `IInventory` (Minecraft standard)
- **Usage**: Chests, barrels, mod-added inventories, etc.
- **Methods**: 13
- **Lines of Code**: 93

**Implementation Example**:
```java
@Override
public ItemStack getStackInSlot(int slot) {
    return delegate(IInventory.class, inv -> inv.getStackInSlot(slot), null);
}
```

---

### 2. ExternalFluidProxy
- **Interface**: `IFluidHandler` (Forge standard)
- **Usage**: Tanks, drums, fluid storage, etc.
- **Methods**: 6
- **Lines of Code**: 61

**Implementation Example**:
```java
@Override
public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    return delegate(IFluidHandler.class, h -> h.fill(from, resource, doFill), 0);
}

@Override
public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
    return delegate(IFluidHandler.class, h -> h.drain(from, maxDrain, doDrain), null);
}
```

---

### 3. ExternalEnergyProxy
- **Interface**: `IOKEnergyIO` (Custom unified energy system)
- **Usage**: RF/EU compatible energy storage
- **Methods**: 7
- **Lines of Code**: 71

**Features**:
- `IOKEnergyIO` = `IOKEnergySink` + `IOKEnergySource`
- Supports both RF (Redstone Flux) and EU (IndustrialCraft2)
- Automatically implements CoFH API (`IEnergyHandler`)

**Implementation Example**:
```java
@Override
public int receiveEnergy(ForgeDirection side, int amount, boolean simulate) {
    return delegate(IOKEnergySink.class, sink -> sink.receiveEnergy(side, amount, simulate), 0);
}

@Override
public int extractEnergy(ForgeDirection side, int amount, boolean simulate) {
    return delegate(IOKEnergySource.class, source -> source.extractEnergy(side, amount, simulate), 0);
}
```

---

### 4. ExternalGasProxy
- **Interface**: `IGasHandler` + `ITubeConnection` (Mekanism)
- **Usage**: Mekanism gas tanks
- **Methods**: 6
- **Lines of Code**: 65

**Implementation Example**:
```java
@Override
public int receiveGas(ForgeDirection from, GasStack stack, boolean doTransfer) {
    return delegate(IGasHandler.class, h -> h.receiveGas(from, stack, doTransfer), 0);
}

@Override
public boolean canTubeConnect(ForgeDirection side) {
    return delegate(ITubeConnection.class, tube -> tube.canTubeConnect(side), false);
}
```

---

### 5. ExternalEssentiaProxy
- **Interface**: `IAspectContainer` (Thaumcraft)
- **Usage**: Essentia jars, wands, etc.
- **Methods**: 9
- **Lines of Code**: 75

**Implementation Example**:
```java
@Override
public int addToContainer(Aspect tag, int amount) {
    return delegate(IAspectContainer.class, c -> c.addToContainer(tag, amount), 0);
}

@Override
public AspectList getAspects() {
    return delegate(IAspectContainer.class, IAspectContainer::getAspects, new AspectList());
}
```

---

### 6. ExternalManaProxy
- **Interface**: `IManaPool` + `ISparkAttachable` (Botania)
- **Usage**: Mana pools, mana tablets, etc.
- **Methods**: 10
- **Lines of Code**: 84

**Features**:
- Implements two interfaces simultaneously
- Supports both mana transmission/reception and spark attachment

**Implementation Example**:
```java
@Override
public int getCurrentMana() {
    return delegate(IManaPool.class, IManaPool::getCurrentMana, 0);
}

@Override
public void recieveMana(int mana) {
    delegateVoid(IManaPool.class, pool -> pool.recieveMana(mana));
}

@Override
public ISparkEntity getAttachedSpark() {
    return delegate(ISparkAttachable.class, ISparkAttachable::getAttachedSpark, null);
}
```

---

## AbstractExternalProxy (Base Class)

Abstract base class inherited by all proxies.

### Key Fields

```java
protected final TEMachineController controller;  // Controller managing this proxy
protected final ChunkCoordinates targetPosition; // Coordinates of target
protected TileEntity targetTileEntity;           // Cached target
protected EnumIO ioMode;                         // I/O mode (INPUT/OUTPUT/BOTH)
protected boolean errorNotified = false;         // Error notification flag
```

### Key Methods

#### 1. **getTargetTileEntity()** - Virtual Proxy
```java
@Override
public TileEntity getTargetTileEntity() {
    if (targetTileEntity == null || targetTileEntity.isInvalid()) {
        if (getWorld() != null) {
            targetTileEntity = getWorld().getTileEntity(getX(), getY(), getZ());
        }
    }
    return targetTileEntity;
}
```
- **Lazy Initialization**: Retrieves TileEntity on first access
- **Caching**: Reuses on subsequent accesses
- **Invalidation Check**: Re-retrieves if TileEntity is removed

#### 2. **delegate()** - Template Method
```java
protected <T, R> R delegate(Class<T> targetType, Function<T, R> operation, R defaultValue) {
    try {
        T target = getTargetAs(targetType);
        return target != null ? operation.apply(target) : defaultValue;
    } catch (Exception e) {
        notifyError();
        return defaultValue;
    }
}
```
- **Type Safety**: Type checking with generics
- **Error Handling**: Automatically catches exceptions
- **Default Values**: Returns safe value on failure

#### 3. **delegateVoid()** - Void Return Version
```java
protected <T> void delegateVoid(Class<T> targetType, Consumer<T> operation) {
    try {
        T target = getTargetAs(targetType);
        if (target != null) {
            operation.accept(target);
        }
    } catch (Exception e) {
        notifyError();
    }
}
```

#### 4. **getTargetAs()** - Type Checking
```java
protected <T> T getTargetAs(Class<T> type) {
    TileEntity te = getTargetTileEntity();
    if (type.isInstance(te)) {
        return type.cast(te);
    }
    notifyError();
    return null;
}
```
- **Prevents ClassCastException**: Pre-checks with instanceof
- **Error Notification**: Notifies player on type mismatch

#### 5. **notifyError()** - Error Notification
```java
protected void notifyError() {
    if (!errorNotified && getWorld() != null && !getWorld().isRemote) {
        errorNotified = true;
        String msg = buildErrorMessage();
        notifyNearbyPlayers(msg);
    }
}
```
- **Single Notification**: Prevents spam
- **Server-Side Only**: No client-side notifications
- **Proximity-Based**: Only players within 32 blocks

---

## Integration with Self-Validation Pattern

External Port Proxy combines with the **Self-Validation Pattern** to achieve type-safe validity checking.

### IModularPort Interface
```java
public interface IModularPort extends IPortType, ISidedIO, ISidedTexture {

    /**
     * Check if this port is currently valid and usable.
     * Template Method Pattern: each implementation defines its own validation logic.
     */
    default boolean isPortValid() {
        // Default implementation: check isInvalid for TileEntity
        if (this instanceof TileEntity) {
            return !((TileEntity) this).isInvalid();
        }
        return true;
    }
}
```

### IExternalPortProxy Interface
```java
public interface IExternalPortProxy extends IModularPort {

    /**
     * Proxy validation: check if the target TileEntity is valid.
     * Overrides the default IModularPort implementation.
     */
    @Override
    default boolean isPortValid() {
        TileEntity target = getTargetTileEntity();
        return target != null && !target.isInvalid();
    }
}
```

### Usage in PortManager
```java
public <T extends IModularPort> List<T> validPorts(List<T> ports) {
    return ports.stream()
        .filter(p -> p != null && p.isPortValid())  // ← Simple!
        .collect(Collectors.toList());
}
```

**Benefits**:
- ✅ **Type Safe**: No casting required
- ✅ **Open-Closed Principle**: validPorts() doesn't need modification for new port types
- ✅ **Single Responsibility**: Each port determines its own validity

---

## Proxy Factory Registration

Proxy factories are registered in `MachineryIntegration.java` to automatically detect external blocks.

### Base Proxies (Always Active)

```java
private static void registerBaseProxies() {
    // Item Proxy
    BlockResolver.registerProxyFactory(IPortType.Type.ITEM, (controller, coords, tile, io) -> {
        if (tile instanceof IInventory) {
            return new ExternalItemProxy(controller, coords, io);
        }
        return null;
    });

    // Fluid Proxy (Forge standard)
    BlockResolver.registerProxyFactory(IPortType.Type.FLUID, (controller, coords, tile, io) -> {
        if (tile instanceof IFluidHandler) {
            return new ExternalFluidProxy(controller, coords, io);
        }
        return null;
    });

    // Energy Proxy (unified OKEnergy system)
    BlockResolver.registerProxyFactory(IPortType.Type.ENERGY, (controller, coords, tile, io) -> {
        if (tile instanceof IOKEnergyTile) {
            return new ExternalEnergyProxy(controller, coords, io);
        }
        return null;
    });
}
```

### Mod Integration Proxies (Only When Mod is Loaded)

```java
private static class MekanismIntegration {
    static void init() {
        MachineryBlocks.GAS_INPUT_PORT.setBlock(BlockGasInputPort.create());
        MachineryBlocks.GAS_OUTPUT_PORT.setBlock(BlockGasOutputPort.create());

        // Gas Proxy (Mekanism integration)
        BlockResolver.registerProxyFactory(IPortType.Type.GAS, (controller, coords, tile, io) -> {
            if (tile instanceof IGasHandler) {
                return new ExternalGasProxy(controller, coords, io);
            }
            return null;
        });
    }
}

private static class BotaniaIntegration {
    static void init() {
        // Mana Proxy (Botania integration)
        BlockResolver.registerProxyFactory(IPortType.Type.MANA, (controller, coords, tile, io) -> {
            if (tile instanceof vazkii.botania.api.mana.IManaPool) {
                return new ExternalManaProxy(controller, coords, io);
            }
            return null;
        });
    }
}

private static class ThaumcraftIntegration {
    static void init() {
        // Essentia Proxy (Thaumcraft integration)
        BlockResolver.registerProxyFactory(IPortType.Type.ESSENTIA, (controller, coords, tile, io) -> {
            if (tile instanceof thaumcraft.api.aspects.IAspectContainer) {
                return new ExternalEssentiaProxy(controller, coords, io);
            }
            return null;
        });
    }
}
```

**Design Points**:
- ✅ **Lazy Class Loading**: Mod integration classes implemented as inner classes to avoid NoClassDefFoundError
- ✅ **Type-Safe Factory**: Generates appropriate proxy with `instanceof` check
- ✅ **Flexible Extension**: New mod proxies just require adding new integration class

---

## Usage Example

### In-Game Usage

1. **Build Machine Structure**
   - Place controller block
   - Place required blocks around it

2. **Place External Blocks**
   - Place chests, tanks, energy storage, etc. next to the machine
   - Proxies are automatically created

3. **Machine Operation**
   - External blocks are recognized as part of the machine
   - Items/fluids/energy automatically transferred with external blocks

### Code Example

```java
// StructureAgent detects external blocks
public void scanStructure() {
    for (BlockPos pos : structurePositions) {
        TileEntity tile = world.getTileEntity(pos.x, pos.y, pos.z);

        // Proxy factory generates appropriate proxy
        IModularPort port = BlockResolver.createProxy(
            IPortType.Type.ITEM,  // Expected port type
            controller,           // Controller
            pos.toChunkCoords(),  // Coordinates
            tile,                 // Target TileEntity
            EnumIO.BOTH           // I/O mode
        );

        if (port != null) {
            // Register with PortManager if proxy created
            portManager.addPort(port, true);
        }
    }
}
```

---

## Code Statistics

| Type | Proxy Class | LOC | Methods | Mod |
|------|------------|-----|---------|-----|
| Item | ExternalItemProxy | 93 | 13 | Vanilla |
| Fluid | ExternalFluidProxy | 61 | 6 | Forge |
| Energy | ExternalEnergyProxy | 71 | 7 | Internal |
| Gas | ExternalGasProxy | 65 | 6 | Mekanism |
| Essentia | ExternalEssentiaProxy | 75 | 9 | Thaumcraft |
| Mana | ExternalManaProxy | 84 | 10 | Botania |
| **Common Base** | **AbstractExternalProxy** | **289** | - | - |

**Totals**:
- ✅ Implemented Proxies: 6 types
- 📝 Concrete Implementation Code: ~449 lines
- ♻️ Common Base Code: 289 lines (reused by all)
- 🎯 Total Methods: 51
- 💪 Code Reduction: ~74% (ExternalItemProxy: 355 → 93 lines)

---

## Design Benefits

### 1. **Improved Maintainability**
- Reduced boilerplate code (74% reduction)
- Centralized error handling
- Reuse of common logic

### 2. **Extensibility**
- New proxies can be implemented in 40-80 lines
- Just inherit from AbstractExternalProxy
- Works with proxy factory registration only

### 3. **Type Safety**
- Type checking with generics
- Complete prevention of ClassCastException
- Reliable type guarantee with instanceof checks

### 4. **Error Handling**
- Automatic exception catching
- User-friendly error messages
- Spam prevention (single notification)

### 5. **Performance**
- TileEntity caching
- Lazy initialization
- Invalidation checks

---

## Pattern Combination Effect

The External Port Proxy system combines five patterns:

1. **Adapter Pattern**: Converts external interfaces to IModularPort
2. **Proxy Pattern**: Access control and error handling
3. **Virtual Proxy**: Lazy initialization and caching
4. **Template Method Pattern**: Unified delegation pattern
5. **Factory Pattern**: Dynamic proxy generation

By combining these:
- ✅ Type safety (Proxy)
- ✅ Interface conversion (Adapter)
- ✅ Code reuse (Template Method)
- ✅ Flexible generation (Factory)
- ✅ Performance (Virtual Proxy)

All achieved simultaneously.

---

## References

- [DesignPattern.md - Proxy Pattern](../../run/DesignPattern.md#21-proxy-pattern)
- [AbstractExternalProxy.java](../../src/main/java/ruiseki/omoshiroikamo/module/machinery/common/tile/proxy/AbstractExternalProxy.java)
- [MachineryIntegration.java](../../src/main/java/ruiseki/omoshiroikamo/module/machinery/common/integration/MachineryIntegration.java)

---

*This document was created on March 12, 2026.*
