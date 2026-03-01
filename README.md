# Omoshiroi Kamo

[![](https://cf.way2muchnoise.eu/full_1382289_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/omoshiroi-kamo)
[![](https://cf.way2muchnoise.eu/versions/For%20MC_1382289_all.svg)](https://www.curseforge.com/minecraft/mc-mods/omoshiroi-kamo)

## About This Mod

Omoshiroi Kamo is an open-source collection of modern mod backports for Minecraft 1.7.10.
It brings newer mod from later Minecraft versions back into legacy modpacks.

Prefer for 1.7.10 tech packs, or any old-version modpack that wants modern features.
Being open-source, contributions are always welcome — if you have ideas, fixes, or improvements,
I’d be thrilled to see them!

## Backports

Each backport module can be individually enabled or disabled in the config.

- Environmental Tech (Clean-Room Rewrite)
- Chickens, More Chickens and Roost (Backport)
- Fluid Cows (Backport WIP)
- RetroSophisticated Backpacks (Backport)
- Modular Machinery (Basic function only for now)
- Deep Mob Learning
- Cable system from Integrated Dynamics

![Backport Config](https://media.forgecdn.net/attachments/1389/517/z7214707883928_63d047db4be5142074322b35573e34d4.jpg)

## Required Dependencies:

- [StructureLib (GTNH)](https://github.com/GTNewHorizons/StructureLib)
- [ModularUI2 (GTNH)](https://github.com/GTNewHorizons/ModularUI2)
- [GTNHLib (GTNH)](https://github.com/GTNewHorizons/GTNHLib)

## Features:

### *I will add as much compatibility as I can.

### Environmental Tech Clean-Room Rewrite (Open Source)

A clean-room rewrite and backport of the modern Environmental Tech mod.
All code has been rewritten, and all textures are original.

- Quantum Extractor (Mine ores, resources, crystals from bedrock!)
- Solar Array
- Quantum Beacon (Give Multieffect by Energy)
- Extractor NEI integration (Original GUI with lens, dimensions, blocks view)
- Colored laser (like beacon)
- Dimension-specific ore mining
- Customizable structures

### Custom Structure System
Advanced JSON-based multiblock structure system with in-game reloading and scanning tools.
- **English**: [Overview](./docs/en/structures/OVERVIEW.md) | [JSON Format](./docs/en/structures/JSON_FORMAT.md) | [Developer Guide](./docs/en/structures/DEVELOPER_GUIDE.md)
- **日本語 (Japanese)**: [概要](./docs/ja/structures/OVERVIEW.md) | [JSONフォーマット](./docs/ja/structures/JSON_FORMAT.md) | [開発者ガイド](./docs/ja/structures/DEVELOPER_GUIDE.md)

### Modular Recipe System
Decoupled and extensible recipe engine supporting multiple resource types and dynamic logic.
- **English**: [Overview](./docs/en/recipes/OVERVIEW.md) | [JSON Format](./docs/en/recipes/JSON_FORMAT.md) | [Developer Guide](./docs/en/recipes/DEVELOPER_GUIDE.md)
- **日本語 (Japanese)**: [概要](./docs/ja/recipes/OVERVIEW.md) | [JSONフォーマット](./docs/ja/recipes/JSON_FORMAT.md) | [開発者ガイド](./docs/ja/recipes/DEVELOPER_GUIDE.md)


![Multiblock](https://media.forgecdn.net/attachments/1410/44/2025-12-01_12-32-24-png.png)

### Modular Machinery Backport (Actively Developing)

A backport and enhancement of the original Modular Machinery mod.

- **Multiblock System**: 
    - Create custom machines via JSON configuration.
    - Rotation and flip structures
- **IO Ports**:
    - Items (WIP ME output port)
    - Fluids (WIP ME output port)
    - Energy (RF/EU)
    - Gas (Mekanism)
    - Mana (Botania)
    - Vis & Essentia (Thaumcraft, WIP ME Essentia IO)
- **NEI Integration**: 
    - Support for viewing recipes and structure previews. (WIP enhanced view)
    - Structure preview
- **Dynamic Reload**: Reload recipes and structures via `/ok modular reload`.
- **Customizable Logic**: Detailed recipe control and processing via JSON. (tons of future plans https://github.com/Shigure-Ruiseki/OmoshiroiKamo/issues/101)


### Chickens, More Chickens & Roost (Backport)

- Over 50 Chicken Breeds Producing Ores, Materials, And Modded Items
- Breeding System With Stats (Growth, Gain, Strength)
- Breeding / Roost Automation
- Supports most modded resources.
- Breeding trees and NEI integration
- Can Have Trait It will Give Buff/Debuff (Can Turn Off In Config)
- Max Growth/Gain/Strength caps configurable (defaults 10/10/10)

![Chickens](https://media.forgecdn.net/attachments/1388/697/2025-11-11_13-12-17-png.png)
![Chickens](https://media.forgecdn.net/attachments/1393/409/2025-11-15_17-58-35-png.png)

### Fluid Cows (Backport)

- Cows Generate Modded Fluids — Lava, Oil, Molten Metals, Mana, etc.
- Like Chickens Have Stats (Growth, Gain, Strength)
- Mob Info Compat & Most Resources from Other Mods.
- Stall Automation
- Breeding trees and NEI integration
- Can Have Trait It will Give Buff/Debuff (Can Turn Off In Config)
- Max Growth/Gain/Strength caps configurable (defaults 10/10/10)

![Cows](https://media.forgecdn.net/attachments/1388/696/2025-11-11_12-45-23-png.png)
![Cows](https://media.forgecdn.net/attachments/1397/915/2025-11-19_19-23-22-png.png)

### RetroSophisticated Backpacks (Backport)

#### Backpack

- [X] Sorting system
- [X] Custom name
- [X] Memory slot
- [X] Lockable by player
- [X] Searching System
- [X] Backpack model render on player's back when equipped
- [X] Custom Backpack main color and accent color
- [X] Modifier each backpack and upgrade slot size

#### Upgrades

- [X] Stack upgrade — extended backpacks max slot stack
- [X] Inception upgrade — backpacks inside backpacks
- [X] Pickup upgrade — auto-pickup items
- [X] Feeding upgrade — auto-feed player
- [X] Filter upgrade — filter in/out to access backpacks block
- [X] Magnet upgrade — collect item and experient around player
- [X] Everlasting upgrade — make backpack immortal
- [X] Void upgrade — filter item to remove
- [X] Crafting upgrade — crafting table

### Traits (WIP)

- Level Up Like Enchantment
- Buff/Debuff Trait

## License

This mod is primarily released under the MIT License (see LICENSE file).

### Third-party components

Some parts of this mod (assets or code) are derived from other mods and retain
their original licenses:

- This mod IS NOT OFFICIAL WORK of original author P3pp3rF1y & CleanroomMC,
  please do not report any issue to original author.
- RetroSophisticated Backpacks (textures/models) — original license applies.
- Any GPLv3/LGPL-3.0 components used in this project remain under GPLv3/LGPL-3.0 and must be
respected accordingly, all assets are forked before Sophisticated Backpack changes license to All Right Reserve.

You are free to use, modify, and distribute the MIT-licensed portions of this
project, but must comply with the original licenses of the included third-party
components.

