# External Port Proxy システム

## 概要

External Port Proxy は、Modular Machineryシステムにおいて、**外部ブロック（チェスト、タンク、エネルギーストレージ等）を機械の一部として統合**するための設計パターンです。

このシステムは **Adapter + Proxy パターン**の融合により、以下を実現しています：

- 外部TileEntityをIModularPortインターフェースに適応（Adapter）
- アクセス制御とエラーハンドリング（Proxy）
- 遅延ロードとキャッシング（Virtual Proxy）
- 統一的なコード構造（Template Method）

## アーキテクチャ

### クラス階層

```
IModularPort (インターフェース)
    ↑
IExternalPortProxy (マーカーインターフェース)
    ↑
AbstractExternalProxy (抽象基底クラス)
    ↑
    ├── ExternalItemProxy (IInventory実装)
    ├── ExternalFluidProxy (IFluidHandler実装)
    ├── ExternalEnergyProxy (IOKEnergyIO実装)
    ├── ExternalGasProxy (IGasHandler実装)
    ├── ExternalEssentiaProxy (IAspectContainer実装)
    └── ExternalManaProxy (IManaPool実装)
```

### デザインパターン

#### 1. **Adapter Pattern（主要パターン）**
外部インターフェース（IInventory, IFluidHandler等）を IModularPort に変換

```java
public class ExternalItemProxy extends AbstractExternalProxy implements IInventory {
    // IInventory → IModularPort への適応
}
```

#### 2. **Proxy Pattern（アクセス制御）**
外部TileEntityへのアクセスを制御し、エラーハンドリングを提供

```java
protected <T, R> R delegate(Class<T> targetType, Function<T, R> operation, R defaultValue) {
    try {
        T target = getTargetAs(targetType);  // 型チェック
        return target != null ? operation.apply(target) : defaultValue;
    } catch (Exception e) {
        notifyError();  // エラー通知
        return defaultValue;
    }
}
```

#### 3. **Virtual Proxy（遅延ロード）**
TileEntityの取得を遅延し、キャッシュで最適化

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

#### 4. **Template Method Pattern（共通化）**
`delegate()` メソッドで統一的なデリゲーションパターンを提供

```java
// 全てのプロキシが同じパターンで実装
@Override
public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    return delegate(IFluidHandler.class, h -> h.fill(from, resource, doFill), 0);
}
```

#### 5. **Factory Pattern（生成）**
`MachineryIntegration` でプロキシファクトリを登録

```java
BlockResolver.registerProxyFactory(IPortType.Type.FLUID, (controller, coords, tile, io) -> {
    if (tile instanceof IFluidHandler) {
        return new ExternalFluidProxy(controller, coords, io);
    }
    return null;
});
```

---

## 実装されたプロキシ

### 1. ExternalItemProxy
- **インターフェース**: `IInventory` (Minecraft標準)
- **用途**: チェスト、樽、Mod追加のインベントリ等
- **メソッド数**: 13個
- **行数**: 93行

**実装例**:
```java
@Override
public ItemStack getStackInSlot(int slot) {
    return delegate(IInventory.class, inv -> inv.getStackInSlot(slot), null);
}
```

---

### 2. ExternalFluidProxy
- **インターフェース**: `IFluidHandler` (Forge標準)
- **用途**: タンク、ドラム缶、流体ストレージ等
- **メソッド数**: 6個
- **行数**: 61行

**実装例**:
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
- **インターフェース**: `IOKEnergyIO` (カスタム統一エネルギーシステム)
- **用途**: RF/EU対応のエネルギーストレージ
- **メソッド数**: 7個
- **行数**: 71行

**特徴**:
- `IOKEnergyIO` = `IOKEnergySink` + `IOKEnergySource`
- RF（Redstone Flux）とEU（IndustrialCraft2）の両対応
- CoFH API（`IEnergyHandler`）も自動的に実装

**実装例**:
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
- **インターフェース**: `IGasHandler` + `ITubeConnection` (Mekanism)
- **用途**: Mekanismのガスタンク
- **メソッド数**: 6個
- **行数**: 65行

**実装例**:
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
- **インターフェース**: `IAspectContainer` (Thaumcraft)
- **用途**: エッセンシアジャー、ワンド等
- **メソッド数**: 9個
- **行数**: 75行

**実装例**:
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
- **インターフェース**: `IManaPool` + `ISparkAttachable` (Botania)
- **用途**: マナプール、マナタブレット等
- **メソッド数**: 10個
- **行数**: 84行

**特徴**:
- 2つのインターフェースを同時実装
- マナの送受信 + スパーク接続の両対応

**実装例**:
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

## AbstractExternalProxy（基底クラス）

全てのプロキシが継承する抽象基底クラスです。

### 主要フィールド

```java
protected final TEMachineController controller;  // プロキシを管理するコントローラー
protected final ChunkCoordinates targetPosition; // ターゲットの座標
protected TileEntity targetTileEntity;           // キャッシュされたターゲット
protected EnumIO ioMode;                         // I/Oモード（INPUT/OUTPUT/BOTH）
protected boolean errorNotified = false;         // エラー通知フラグ
```

### 主要メソッド

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
- **遅延初期化**: 初回アクセス時にTileEntityを取得
- **キャッシング**: 2回目以降は再利用
- **無効化チェック**: TileEntityが削除されたら再取得

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
- **型安全**: ジェネリクスで型チェック
- **エラーハンドリング**: 例外を自動的にキャッチ
- **デフォルト値**: 失敗時の安全な値を返す

#### 3. **delegateVoid()** - 戻り値なし版
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

#### 4. **getTargetAs()** - 型チェック
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
- **ClassCastException防止**: instanceofで事前チェック
- **エラー通知**: 型不一致時にプレイヤーに通知

#### 5. **notifyError()** - エラー通知
```java
protected void notifyError() {
    if (!errorNotified && getWorld() != null && !getWorld().isRemote) {
        errorNotified = true;
        String msg = buildErrorMessage();
        notifyNearbyPlayers(msg);
    }
}
```
- **1回のみ通知**: スパム防止
- **サーバー側のみ**: クライアント側では通知しない
- **近距離プレイヤーのみ**: 32ブロック以内

---

## Self-Validation Pattern との統合

External Port Proxy は **Self-Validation Pattern** と組み合わせることで、型安全な有効性チェックを実現しています。

### IModularPort インターフェース
```java
public interface IModularPort extends IPortType, ISidedIO, ISidedTexture {

    /**
     * Check if this port is currently valid and usable.
     * Template Method Pattern: each implementation defines its own validation logic.
     */
    default boolean isPortValid() {
        // デフォルト実装: TileEntityの場合はisInvalidをチェック
        if (this instanceof TileEntity) {
            return !((TileEntity) this).isInvalid();
        }
        return true;
    }
}
```

### IExternalPortProxy インターフェース
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

### PortManager での使用
```java
public <T extends IModularPort> List<T> validPorts(List<T> ports) {
    return ports.stream()
        .filter(p -> p != null && p.isPortValid())  // ← シンプル！
        .collect(Collectors.toList());
}
```

**利点**:
- ✅ **型安全**: キャスト不要
- ✅ **Open-Closed Principle**: 新しいポートタイプを追加してもvalidPorts()は変更不要
- ✅ **Single Responsibility**: 各ポートが自分の有効性を判断

---

## プロキシファクトリの登録

`MachineryIntegration.java` でプロキシファクトリを登録し、外部ブロックを自動的に検出します。

### 基本プロキシ（常に有効）

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

### Mod統合プロキシ（Modロード時のみ）

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

**設計のポイント**:
- ✅ **遅延クラスロード**: Mod統合クラスは内部クラスで実装し、NoClassDefFoundErrorを回避
- ✅ **型安全なファクトリ**: `instanceof` チェックで適切なプロキシを生成
- ✅ **柔軟な拡張**: 新しいModのプロキシは新しい統合クラスを追加するだけ

---

## 使用例

### ゲーム内での使用

1. **機械構造を構築**
   - コントローラーブロックを配置
   - 周囲に必要なブロックを配置

2. **外部ブロックを配置**
   - チェスト、タンク、エネルギーストレージ等を機械の隣に配置
   - プロキシが自動的に作成される

3. **機械が動作**
   - 外部ブロックが機械の一部として認識される
   - アイテム/流体/エネルギーが外部ブロックと自動的にやり取りされる

### コード例

```java
// StructureAgent が外部ブロックを検出
public void scanStructure() {
    for (BlockPos pos : structurePositions) {
        TileEntity tile = world.getTileEntity(pos.x, pos.y, pos.z);

        // プロキシファクトリが適切なプロキシを生成
        IModularPort port = BlockResolver.createProxy(
            IPortType.Type.ITEM,  // 期待するポートタイプ
            controller,           // コントローラー
            pos.toChunkCoords(),  // 座標
            tile,                 // ターゲットTileEntity
            EnumIO.BOTH           // I/Oモード
        );

        if (port != null) {
            // プロキシが作成されたらPortManagerに登録
            portManager.addPort(port, true);
        }
    }
}
```

---

## コード統計

| タイプ | プロキシクラス | 行数 | メソッド数 | 対応Mod |
|--------|--------------|------|-----------|---------|
| Item | ExternalItemProxy | 93 | 13 | Vanilla |
| Fluid | ExternalFluidProxy | 61 | 6 | Forge |
| Energy | ExternalEnergyProxy | 71 | 7 | 内部システム |
| Gas | ExternalGasProxy | 65 | 6 | Mekanism |
| Essentia | ExternalEssentiaProxy | 75 | 9 | Thaumcraft |
| Mana | ExternalManaProxy | 84 | 10 | Botania |
| **共通基底** | **AbstractExternalProxy** | **289** | - | - |

**総計**:
- ✅ 実装済みプロキシ: 6タイプ
- 📝 具体実装コード: ~449行
- ♻️ 共通基底コード: 289行（全てで再利用）
- 🎯 総メソッド数: 51個
- 💪 コード削減率: 約74%（ExternalItemProxy: 355行 → 93行）

---

## 設計の利点

### 1. **保守性の向上**
- ボイラープレートコードの削減（74%減）
- エラーハンドリングの一元化
- 共通ロジックの再利用

### 2. **拡張性**
- 新しいプロキシは40-80行で実装可能
- AbstractExternalProxyを継承するだけ
- プロキシファクトリの登録のみで動作

### 3. **型安全性**
- ジェネリクスによる型チェック
- ClassCastException の完全防止
- instanceofチェックで確実な型保証

### 4. **エラーハンドリング**
- 例外の自動キャッチ
- ユーザーフレンドリーなエラーメッセージ
- スパム防止（1回のみ通知）

### 5. **パフォーマンス**
- TileEntityのキャッシング
- 遅延初期化
- 無効化チェック

---

## パターンの組み合わせ効果

External Port Proxy システムは、以下の5つのパターンを組み合わせています：

1. **Adapter Pattern**: 外部インターフェースをIModularPortに変換
2. **Proxy Pattern**: アクセス制御とエラーハンドリング
3. **Virtual Proxy**: 遅延初期化とキャッシング
4. **Template Method Pattern**: 統一的なデリゲーションパターン
5. **Factory Pattern**: 動的なプロキシ生成

これらを組み合わせることで：
- ✅ 型安全性（Proxy）
- ✅ インターフェース変換（Adapter）
- ✅ コード再利用（Template Method）
- ✅ 柔軟な生成（Factory）
- ✅ パフォーマンス（Virtual Proxy）

すべてを同時に実現しています。

---

## 参照

- [DesignPattern.md - Proxyパターン](../../run/DesignPattern.md#21-proxyパターン)
- [AbstractExternalProxy.java](../../src/main/java/ruiseki/omoshiroikamo/module/machinery/common/tile/proxy/AbstractExternalProxy.java)
- [MachineryIntegration.java](../../src/main/java/ruiseki/omoshiroikamo/module/machinery/common/integration/MachineryIntegration.java)

---

*このドキュメントは2026年3月12日に作成されました。*
