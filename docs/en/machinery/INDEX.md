# Modular Machinery Documentation

Technical documentation for the Modular Machinery module.

## 📚 Documentation

### System Design

#### [External Port Proxy System](./EXTERNAL_PROXY.md)
Design documentation for the proxy system that integrates external blocks (chests, tanks, energy storage, etc.) as part of the machine.

**Contents**:
- Fusion of Adapter + Proxy patterns
- 6 types of proxy implementations (Item, Fluid, Energy, Gas, Essentia, Mana)
- AbstractExternalProxy base class details
- Integration with Self-Validation Pattern
- Proxy factory registration methods
- Code examples and usage

**Target Audience**: Developers, Design Pattern Learners

---

---

## 💡 New Features Guide

### Dynamic Amount System (Expression System)
An expression system for dynamically changing recipe input/output amounts. Enables flexible recipe design based on machine state and world environment.

**Main Features**:
- **Machine State Reference**: Energy, fluids, mana, gas, Tier, progress, etc.
- **World Environment Reference**: Time, weather, moon phase, biome, elapsed days, etc.
- **Mathematical Functions**: Trigonometric functions, logarithms, exponentiation, random numbers, etc.
- **Conditional Branching**: Complex control via ternary operators and logical operators

**Usage Example**:
```json
{
  "inputs": [
    { "item": "minecraft:iron_ingot", "amount": "tier * 10 + 5" }
  ],
  "outputs": [
    { "fluid": "steam", "amount": "energy_p * 1000" }
  ]
}
```

**Related Documentation**:
- [JSON Format: Dynamic Amount](../recipes/JSON_FORMAT.md#31-dynamic-amount) - Basic usage
- [Practical Examples](../recipes/EXPRESSION_EXAMPLES.md) - Detailed usage examples by pattern

**Target Audience**: Recipe creators, Modpack developers

---

## 🔗 Related Documentation

### Recipe System
- [Overview](../recipes/OVERVIEW.md)
- [JSON Format](../recipes/JSON_FORMAT.md)
- [Practical Examples](../recipes/EXPRESSION_EXAMPLES.md) 🆕
- [Developer Guide](../recipes/DEVELOPER_GUIDE.md)

### Structure System
- [Overview](../structures/OVERVIEW.md)
- [JSON Format](../structures/JSON_FORMAT.md)
- [Developer Guide](../structures/DEVELOPER_GUIDE.md)

---

*This documentation is updated regularly.*
