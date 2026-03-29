# Expression Parser Variable & Function Reference

A comprehensive reference for variables, functions, and advanced queries available in OmoshiroiKamo's `ExpressionParser`.
These can be used in the `amount` field of JSON recipes, the `expression` key in `Condition` objects, and machine property configurations.

## 📚 Related Documentation

- [JSON Format](./JSON_FORMAT.md) - Basic JSON syntax
- [Practical Examples](./EXPRESSION_EXAMPLES.md) - Real-world recipe patterns

---

## 1. Basic Operations

Standard arithmetic and logical operations are supported.

- **Arithmetic**: `+`, `-`, `*`, `/`, `%` (modulo)
- **Comparison**: `==`, `!=`, `>`, `<`, `>=`, `<=`
- **Logical**: `&&` (AND), `||` (OR), `!` (NOT)
- **Grouping**: `()`, `{}`

> [!TIP]
> Conditional expressions (e.g., `1 == 1`) are treated as `1` (true) or `0` (false) in numerical calculations.
> Example: `10 + (day > 100) * 5` (15 after day 100, otherwise 10)

## 2. Built-in Variables

### World Variables
Retrieve information about the world where the machine is located.

#### Time & Calendar
- `day` / `total_days`: Cumulative elapsed days
- `time`: Current time of day (0 - 23999)
- `tick`: Total world time in raw ticks
- `moon` / `moon_phase`: Current moon phase (0 - 7; 0 = Full Moon, 4 = New Moon)

#### Coordinates & Dimension
- `x` / `y` / `z`: Coordinates of the machine controller
- `dimension`: Current dimension ID (Overworld = 0, Nether = -1, End = 1)

#### Weather & Environment
- `is_day`: Whether it is daytime (1 or 0)
- `is_night`: Whether it is nighttime (1 or 0)
- `is_raining`: Whether it is raining (1 or 0)
- `is_thundering`: Whether it is thundering (1 or 0)
- `temp`: Biome temperature at the controller's location (0.0 to 2.0)
- `humidity`: Biome humidity at the controller's location (0.0 to 1.0)

#### Light & Space
- `light`: Block light level at the controller (0 - 15; combined sky and block light)
- `light_block`: Raw block light level (0 - 15)
- `light_sky`: Raw sky light level (0 - 15)
- `can_see_sky`: Whether the controller can see the sky (1 or 0)
- `can_see_void`: Whether there's a void directly below the controller to Y=0 (1 or 0)

#### Recipe Progress
- `recipe_tick`: Elapsed time since the current recipe started (ticks)
- `progress_tick`: Raw tick progress value of the current recipe

#### Miscellaneous
- `redstone`: Redstone signal strength received by the controller (0 - 15)
- `random_seed`: Seed value for the recipe evaluation session (used for reproducible `random()` / `chance()`)
- `world_seed`: Seed of the world generation
- `facing`: Direction the machine is facing (0:Down, 1:Up, 2:North, 3:South, 4:West, 5:East)

### Constants
- `pi`: Pi (π ≒ 3.14159)
- `e`: Napier's constant (e ≒ 2.71828)

## 3. Machine Properties

Retrieve the current state of the machine.

### Item
- **Variables (Global)**
    - `item` / `item_total`: Total number of items stored.
    - `item_in` / `item_out`: Total items in input / output ports.
    - `item_max` / `item_capacity`: Maximum item slot capacity.
    - `item_f` / `item_free` / `item_space`: Total free capacity for items.
    - `item_p` / `item_percent`: Item fill percentage (0.0 ~ 1.0).
- **Functions (Filtering)**
    - `item("id")`: Current amount of a specific item ID or OreDict entry.
    - `item_in("id")` / `item_out("id")`: Amount in input / output ports.
    - `item_f("id")` / `item_f_in("id")` / `item_f_out("id")`: Acceptable amount for a specific item.
- **Slot Info (Functions)**
    - `item_slot()`: Total slot count.
    - `item_slot_in()` / `item_slot_out()`: Slot count for input / output.
    - `item_slot_empty()`: Number of empty slots.

### Energy
- `energy` / `energy_stored` / `energy_total`: Currently stored energy.
- `energy_max` / `energy_capacity`: Maximum energy capacity.
- `energy_f` / `energy_free`: Available energy space (`max - stored`).
- `energy_p` / `energy_percent`: Energy fill percentage (0.0 ~ 1.0).
- `energy_per_tick`: Energy consumption/generation per tick (supported machines only).

### Fluid
- **Variables (Global)**
    - `fluid` / `fluid_stored` / `fluid_total`: Total amount of fluids stored.
    - `fluid_in` / `fluid_out`: Total fluid in input / output ports.
    - `fluid_max` / `fluid_capacity`: Maximum fluid tank capacity.
    - `fluid_p` / `fluid_percent`: Fluid fill percentage (0.0 ~ 1.0).
- **Functions (Filtering)**
    - `fluid("name")`: Amount of a specific fluid.
    - `fluid_in("name")` / `fluid_out("name")`: Amount in input / output ports.

### Other Resources
- **Mana**: `mana`, `mana_max`, `mana_p`
- **Gas**: `gas`, `gas_max`, `gas_p`
- **Essentia**: `essentia("aspectName")`
- **Vis**: `vis("aspectName")`

### Statistics & State
- `recipe_count`: Cumulative number of recipes processed by the machine.
- `progress` / `progress_percent`: Current recipe progress (0.0 ~ 1.0).
- `tier`: Current machine Tier.
- `is_running`: Whether the machine is running (1 or 0).
- `timeplaced`: Cumulative time since the machine was placed (ticks).
- `timecontinue`: Continuous uptime of the machine (ticks).

### Structural Properties
Performance multipliers provided by the structure definition.

- `batch`: Current batch size.
- `speed_multi`: Speed multiplier.
- `energy_multi`: Energy multiplier.

---

## 4. Function Reference

### Math Functions
- `abs(x)`, `sqrt(x)`, `pow(base, exp)`
- `min(a, b...)`, `max(a, b...)`
- `sin(x)`, `cos(x)`, `tan(x)` (Input in **radians**)
- `asin(x)`, `acos(x)`, `atan(x)` (Output in radians)
- `rad(deg)`, `deg(rad)` (Convert degrees/radians)
- `floor(x)`, `ceil(x)`, `round(x)`
- `clamp(val, min, max)`
- `random()`: Random number between 0 and 1
- `chance(x)`: Returns 1 or 0 based on probability `x` (0.0 - 1.0)

### Advanced Queries
- `can_see_sky(filter...)`: Check sky visibility. Specify IDs to treat blocks like glass as transparent.
- `can_see_void(filter...)`: Check if there is void directly below.
- `count_blocks(distance, filter...)`: Count specific blocks within a range.
    - Example: `count_blocks(1, "minecraft:iron_block")`
- `nbt('key')`: Retrieve NBT from the machine itself.
- `nbt('symbol', 'key')`: Retrieve NBT from a block at a specific symbol position.

---

## 5. Design Tips

### Common Pitfalls
- **Quotes in JSON**: Expressions themselves must be strings in JSON, e.g., `"amount": "tier * 2"`.
- **Case Sensitivity**: Variable names are all **lowercase** (e.g., `tier`, not `Tier`).
- **Characters**: Do not use full-width or non-standard characters for operators or variable names.

### Performance
Since expressions may be evaluated every tick, avoid extremely complex logic or excessive use of wide-range `count_blocks` queries.
