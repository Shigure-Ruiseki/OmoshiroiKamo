# Structure System: Overview

The Structure JSON System in OmoshiroiKamo is designed to be highly flexible, type-safe, and decoupled from the core machine logic.

## 1. Design Philosophy

The system follows two primary design patterns to ensure scalability:

- **Separation of Data and Logic (Visitor Pattern)**:
  Structure definitions (`IStructureEntry`) only hold the data (shape, mappings, requirements). The logic for validation, rendering, and block position tracking is implemented via `IStructureVisitor`. This allows adding new functionality (like a hologram previewer or a tier scanner) without changing the data structure.
  
- **Dynamic Extensibility (Registry Pattern)**:
  Instead of hardcoding what a "requirement" is (e.g., item ports, fuel), we use the `RequirementRegistry`. New types of requirements can be registered at runtime, allowing modules (like Thaumcraft or Mekanism) to add their own structural needs seamlessly.

## 2. Core Components

- **`IStructureEntry`**: The core data interface. It represents a single multiblock definition.
- **`StructureJsonReader`**: Converts JSON files into `IStructureEntry` objects. It handles default mappings and hierarchical definitions.
- **`StructureManager`**: The central registry that stores all loaded structures and provides lookup services.
- **`StructureAgent`**: A bridge component used by TileEntities (like `TEMachineController`) to manage their specific structure instance, tracking formed states and validating the world against the definition.
- **`PortManager`**: Specifically handles the `requirements` section of a structure, checking if the current machine has enough of the required I/O types.

## 3. Module Relationship

- **Multiblock Module**: Uses fixed, predefined structure names. It often provides a hardcoded fallback if the JSON is missing.
- **Modular Module**: Uses dynamically loaded custom structures. It relies entirely on the JSON system for flexibility.

Both modules share the same underlying API, ensuring a consistent experience for both developers and modpack creators.
