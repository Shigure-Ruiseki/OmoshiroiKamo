# Recipe Expression Practical Examples

This document provides practical recipe examples using the Dynamic Amount System (Expression System).

## 📚 Related Documentation

- [JSON Format](./JSON_FORMAT.md) - Basic JSON syntax

---

## 1. Basic Patterns

### 1.1 Tier-Dependent Recipes

Recipes that adjust input/output amounts based on machine Tier.

#### Example 1: Linear Scaling
```json
{
  "group": "tier_scaling_machine",
  "recipes": [
    {
      "inputs": [
        {
          "item": "minecraft:iron_ingot",
          "amount": "tier * 8"
        }
      ],
      "outputs": [
        {
          "item": "minecraft:diamond",
          "amount": "tier"
        }
      ],
      "duration": 200
    }
  ]
}
```
- Tier 1: 8 iron → 1 diamond
- Tier 8: 64 iron → 8 diamonds

#### Example 2: Exponential Scaling
```json
{
  "inputs": [
    {
      "item": "rare_material",
      "amount": "pow(2, tier - 1)"
    }
  ],
  "outputs": [
    {
      "item": "super_material",
      "amount": "1"
    }
  ]
}
```
- Tier 1: 1 item, Tier 2: 2 items, Tier 3: 4 items, Tier 4: 8 items...
- Requirements increase exponentially at higher tiers

#### Example 3: Tier Threshold Branching
```json
{
  "outputs": [
    {
      "item": "output_item",
      "amount": "tier >= 5 ? tier * 2 : tier"
    }
  ]
}
```
- Tier 4 and below: Tier × 1
- Tier 5 and above: Tier × 2

---

### 1.2 Energy-Dependent Recipes

Recipes with efficiency that varies based on machine energy levels.

#### Example 1: Energy Fill Percentage Bonus
```json
{
  "inputs": [
    {
      "energy": 10000,
      "perTick": true
    },
    {
      "item": "minecraft:coal",
      "amount": "1"
    }
  ],
  "outputs": [
    {
      "fluid": "steam",
      "amount": "floor(1000 * (1.0 + energy_p * 0.5))"
    }
  ],
  "duration": 100
}
```
- Energy 0%: 1000 mB
- Energy 50%: 1250 mB
- Energy 100%: 1500 mB

#### Example 2: Low Energy Penalty
```json
{
  "inputs": [
    {
      "item": "raw_ore",
      "amount": "energy_p < 0.2 ? 2 : 1"
    }
  ],
  "outputs": [
    {
      "item": "processed_ore",
      "amount": "1"
    }
  ]
}
```
- Energy below 20%: requires 2 raw materials
- Energy above 20%: requires 1 raw material

#### Example 3: Energy Saving Mode
```json
{
  "inputs": [
    {
      "energy": "max(1000, floor((1.0 - energy_p) * 5000))",
      "perTick": true
    }
  ],
  "outputs": [
    {
      "item": "product",
      "amount": "1"
    }
  ]
}
```
- Full energy: 1000 RF/t
- Empty energy: 5000 RF/t (benefits from fast charging)

---

### 1.3 Time & Weather-Dependent Recipes

Recipes that vary based on world environmental conditions.

#### Example 1: Day/Night Cycle
```json
{
  "outputs": [
    {
      "item": "solar_crystal",
      "amount": "time >= 0 && time < 12000 ? 5 : 1"
    }
  ],
  "conditions": [
    {
      "weather": "clear"
    }
  ]
}
```
- Daytime (0-12000 ticks) and clear: 5 items
- Nighttime or rain: 1 item

#### Example 2: Moon Phase-Dependent Magic Recipe
```json
{
  "inputs": [
    {
      "essentia": "luna",
      "amount": "8 - moon_phase"
    }
  ],
  "outputs": [
    {
      "item": "moonstone",
      "amount": "moon_phase + 1"
    }
  ]
}
```
- Full moon (moon_phase = 0): 8 essentia, outputs 1 item
- New moon (moon_phase = 7): 1 essentia, outputs 8 items

#### Example 3: Seasonal Variation (Day-Based)
```json
{
  "outputs": [
    {
      "item": "seasonal_herb",
      "amount": "1 + floor(sin(day * 0.1) * 3)"
    }
  ]
}
```
- Output varies periodically with sine wave (1-4 items)

---

## 2. Advanced Patterns

### 2.1 Progressive Output Increase

Growth-type recipes with bonus that increases with processing count.

#### Example 1: Milestone Bonus
```json
{
  "outputs": [
    {
      "item": "reward",
      "amount": "10 + floor(recipeprocessed / 100) * 2"
    }
  ]
}
```
- 0-99 times: 10 items
- 100-199 times: 12 items
- 200-299 times: 14 items
- Cumulative +2 item bonus

#### Example 2: Experience Curve
```json
{
  "outputs": [
    {
      "item": "experience_orb",
      "amount": "min(100, floor(sqrt(recipeprocessed) * 5))"
    }
  ]
}
```
- Initially increases rapidly, later gradually (max 100 items)

---

### 2.2 Multi-Condition Combination

Complex recipes combining multiple factors.

#### Example 1: Tier × Energy × Progress
```json
{
  "outputs": [
    {
      "item": "advanced_product",
      "amount": "floor(tier * energy_p * (1.0 + recipeprocessed / 1000.0))"
    }
  ]
}
```
- Determined by 3 factors: Tier, energy fill, processing count
- Tier 8, 100% energy, 1000 processes = 16 items

#### Example 2: Comprehensive Environmental Evaluation
```json
{
  "inputs": [
    {
      "item": "catalyst",
      "amount": "max(1, 10 - tier - floor(energy_p * 3) - (time < 6000 ? 2 : 0))"
    }
  ]
}
```
- High tier, high energy, morning = less catalyst needed (min 1)

---

### 2.3 Efficiency Optimization Recipes

Recipes tuned for optimal efficiency under specific conditions.

#### Example 1: Sweet Spot Design
```json
{
  "outputs": [
    {
      "item": "optimized_product",
      "amount": "floor(10 * (1.0 - abs(energy_p - 0.5) * 2))"
    }
  ]
}
```
- 50% energy = maximum efficiency (10 items)
- 0% or 100% energy = minimum efficiency (0 items)
- Optimal to maintain energy around 50%

#### Example 2: Balanced Type
```json
{
  "inputs": [
    {
      "item": "resource_a",
      "amount": "max(1, tier - floor(fluid_p * 5))"
    },
    {
      "item": "resource_b",
      "amount": "max(1, tier - floor(mana_p * 5))"
    }
  ]
}
```
- Better fluid and mana balance = fewer resources required

---

## 3. Advanced Techniques

### 3.1 State Machine Behavior

Staged operations combined with NBT.

```json
{
  "inputs": [
    {
      "type": "block_nbt",
      "symbol": "C",
      "key": "stage",
      "operation": "add",
      "value": "1"
    }
  ],
  "outputs": [
    {
      "item": "stage_reward",
      "amount": "min(10, nbt('C', 'stage'))"
    }
  ]
}
```
- Stage increments by +1 each execution
- Reward gradually increases (max 10 items)

---

### 3.2 Probabilistic Output

Probabilistic output using `chance()` function.

```json
{
  "outputs": [
    {
      "item": "rare_drop",
      "amount": "chance(0.1 + tier * 0.05) ? 1 : 0"
    }
  ]
}
```
- Tier 1: 15% chance for 1 item
- Tier 8: 50% chance for 1 item

---

### 3.3 Dynamic Processing Time

Example when `duration` field can use expressions.

```json
{
  "duration": "max(20, floor(200 / (1.0 + tier * 0.2)))"
}
```
- Higher tier = shorter processing time (minimum 20 ticks)

---

## 4. Troubleshooting

### 4.1 Common Errors

#### Error 1: Expression Not Evaluated
```json
// ❌ Wrong
{
  "amount": tier * 10
}

// ✅ Correct
{
  "amount": "tier * 10"
}
```
**Cause**: Expressions must be written as strings.

---

#### Error 2: Variable Name Mistake
```json
// ❌ Wrong
{
  "amount": "Tier * 10"  // Uppercase
}

// ✅ Correct
{
  "amount": "tier * 10"  // Lowercase
}
```
**Cause**: Variable names must be lowercase.

---

#### Error 3: Full-Width Characters
```json
// ❌ Wrong
{
  "amount": "tier × 10"  // Full-width multiplication
}

// ✅ Correct
{
  "amount": "tier * 10"  // Half-width *
}
```
**Cause**: Operators must be half-width ASCII.

---

#### Error 4: Division by Zero
```json
// ❌ Dangerous
{
  "amount": "1000 / energy_p"
}

// ✅ Safe
{
  "amount": "energy_p > 0 ? floor(1000 / energy_p) : 0"
}
```
**Cause**: Division by zero error when energy is 0.

---

### 4.2 Debug Methods

#### Step 1: Start with Simple Expression
```json
// First, test with basic variables only
{
  "amount": "tier"
}
```

#### Step 2: Gradually Add Complexity
```json
// Add operations
{
  "amount": "tier * 2"
}
```

#### Step 3: Add Conditional Branching
```json
// Use ternary operator
{
  "amount": "tier > 5 ? tier * 2 : tier"
}
```

#### Step 4: Check Log Files
- Check `logs/latest.log` for error messages
- Detailed errors are output when parsing fails

---

### 4.3 Performance Tips

#### Tip 1: Avoid Heavy Calculations
```json
// ⚠️ Heavy
{
  "amount": "pow(pow(tier, 2), 2)"
}

// ✅ Light
{
  "amount": "pow(tier, 4)"
}
```

#### Tip 2: Avoid Unnecessary Re-calculation
- Expressions are evaluated each time, so avoid overly complex expressions
- Store pre-calculated values in NBT if possible

---

## 5. Recipe Design Best Practices

### 5.1 Balance Tuning

1. **Set Appropriate Initial Values**
   - Ensure practical output even at Tier 1
   - Be cautious with exponential growth

2. **Set Upper Limits**
   ```json
   {
     "amount": "min(1000, tier * 100)"
   }
   ```

3. **Guarantee Lower Limits**
   ```json
   {
     "amount": "max(1, floor(energy_p * 10))"
   }
   ```

---

### 5.2 Usability

1. **Intuitive Behavior**
   - Higher tier = higher efficiency (easy to understand)
   - Avoid counterintuitive behavior

2. **Provide Feedback**
   - Expressions displayed in JEI (planned)
   - Tooltip shows current values (planned)

3. **Documentation**
   - Add comments for special expressions
   - Prepare explanations for modpack creators

---

## 6. Reference Links

- [Complete JSON Format](./JSON_FORMAT.md)
- [Developer Guide](./DEVELOPER_GUIDE.md)

---

*This document is continuously updated based on real use cases.*
