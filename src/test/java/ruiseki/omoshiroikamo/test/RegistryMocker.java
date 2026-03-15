package ruiseki.omoshiroikamo.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fluids.Fluid;

import ruiseki.omoshiroikamo.core.common.util.Logger;

/**
 * テスト環境において Forge/Minecraft のレジストリをリフレクションで偽装するためのユーティリティ
 */
public class RegistryMocker {

    private static final Map<String, Item> mockedItems = new HashMap<>();

    public static void mockAll() {
        System.out.println("RegistryMocker: Starting mockAll...");
        try {
            mockFML();
            mockItems();
            mockBlocks();
            mockFluids();
            mockOreDict();
            System.out.println("RegistryMocker: Successfully mocked registries.");
        } catch (Throwable e) {
            System.err.println("RegistryMocker: Failed to mock registries: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void mockFluids() throws Exception {
        try {
            // FluidRegistry class might trigger static initialization, so be careful
            Class<?> frClass = Class.forName("net.minecraftforge.fluids.FluidRegistry");

            // Try to set up the fluids map - may fail if FluidRegistry is initializing
            try {
                Field fluidsField = frClass.getDeclaredField("fluids");
                fluidsField.setAccessible(true);
                Map<String, Fluid> fluids = (Map<String, Fluid>) fluidsField.get(null);

                if (fluids == null) {
                    fluids = new HashMap<>();
                    setStaticFinalField(frClass, "fluids", fluids);
                }

                try {
                    Field masterTableField = frClass.getDeclaredField("masterFluidTable");
                    masterTableField.setAccessible(true);
                    if (masterTableField.get(null) == null) {
                        setStaticFinalField(frClass, "masterFluidTable", new HashMap<Fluid, Integer>());
                    }
                } catch (NoSuchFieldException e) {}

                // water/lava の実体作成
                if (!fluids.containsKey("water")) {
                    Fluid water = new Fluid("water");
                    fluids.put("water", water);
                    setStaticFinalField(frClass, "WATER", water);
                }
                if (!fluids.containsKey("lava")) {
                    Fluid lava = new Fluid("lava");
                    fluids.put("lava", lava);
                    setStaticFinalField(frClass, "LAVA", lava);
                }

                System.out.println("RegistryMocker: Successfully mocked FluidRegistry");
            } catch (Exception e) {
                // If FluidRegistry initialization fails, that's okay - tests will skip
                Logger.warn("RegistryMocker: Could not fully mock FluidRegistry: " + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            Logger.warn("RegistryMocker: FluidRegistry not found.");
        } catch (Throwable t) {
            // Catch any other errors to prevent test setup from failing completely
            Logger.warn("RegistryMocker: FluidRegistry mock failed: " + t.getMessage());
        }
    }

    private static void mockOreDict() throws Exception {
        try {
            Class<?> odClass = Class.forName("net.minecraftforge.oredict.OreDictionary");
            for (Field field : odClass.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    Object current = field.get(null);
                    if (current == null) {
                        Logger.info(
                            "RegistryMocker: Mocking OreDictionary field: " + field.getName()
                                + " ("
                                + field.getType()
                                    .getSimpleName()
                                + ")");
                        if (List.class.isAssignableFrom(field.getType())) {
                            setStaticFinalField(odClass, field.getName(), new ArrayList<>());
                        } else if (Map.class.isAssignableFrom(field.getType())) {
                            setStaticFinalField(odClass, field.getName(), new HashMap<>());
                        } else if (Set.class.isAssignableFrom(field.getType())) {
                            setStaticFinalField(odClass, field.getName(), new HashSet<>());
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            Logger.warn("RegistryMocker: OreDictionary not found.");
        } catch (Throwable t) {
            Logger.warn("RegistryMocker: Failed to fully mock OreDictionary: " + t.getMessage());
        }
    }

    private static void mockItems() throws Exception {
        // Items クラスの主要なアイテムをダミーで埋める
        createAndSetMockItem("iron_ingot");
        createAndSetMockItem("gold_ingot");
        createAndSetMockItem("diamond");
        createAndSetMockItem("coal");
        createAndSetMockItem("redstone");
        createAndSetMockItem("dye");
        createAndSetMockItem("quartz");
        createAndSetMockItem("ender_pearl");
        createAndSetMockItem("sapling");
        createAndSetMockItem("log");
        createAndSetMockItem("gold_nugget");

        // Add weapons and tools for testing
        createAndSetMockItem("diamond_sword");
        createAndSetMockItem("iron_sword");
        createAndSetMockItem("name_tag");
        // Skipping enchanted_book - requires ItemEnchantedBook subclass
        // createAndSetMockItem("enchanted_book");
        createAndSetMockItem("glowstone_dust");
        createAndSetMockItem("water_bucket");

        // Mock Item.itemRegistry for string-to-item resolution
        mockItemRegistry();
    }

    private static void createAndSetMockItem(String name) throws Exception {
        Item item = createMockItem(name);
        mockedItems.put(name, item);
        setStaticFinalField(Items.class, name, item);
    }

    private static void mockItemRegistry() throws Exception {
        try {
            // Create FMLControlledNamespacedRegistry<Item> instead of RegistryNamespaced
            Class<?> fmlRegistryClass = Class.forName("cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry");
            Constructor<?> constructor = fmlRegistryClass
                .getDeclaredConstructor(String.class, int.class, int.class, Class.class, char.class);
            constructor.setAccessible(true);

            // Create registry with parameters: optionalDefault, maxId, minId, type, discriminator
            // For items: maxId=31999, minId=256, type=Item.class, discriminator='\u0002' (items)
            Object itemRegistry = constructor.newInstance(null, 31999, 256, Item.class, '\u0002');

            // Set Item.itemRegistry (deprecated field, but still used by some code)
            setStaticFinalField(Item.class, "itemRegistry", itemRegistry);

            // Register items using reflection (try-catch individually to not break GameData mocking)
            try {
                // Use private addObjectRaw method - putObject is deprecated and doesn't actually add items
                Method addObjectRawMethod = fmlRegistryClass
                    .getDeclaredMethod("addObjectRaw", int.class, String.class, Object.class);
                addObjectRawMethod.setAccessible(true);

                int itemId = 256; // Items start at ID 256

                // Use the mockedItems map instead of trying to read fields
                for (Map.Entry<String, Item> entry : mockedItems.entrySet()) {
                    String itemName = entry.getKey();
                    Item item = entry.getValue();
                    if (item != null) {
                        // Register with ID, "minecraft:itemName", and item object
                        addObjectRawMethod.invoke(itemRegistry, itemId++, "minecraft:" + itemName, item);
                    }
                }

                System.out.println(
                    "RegistryMocker: Successfully registered " + (itemId - 256)
                        + " items in registry using addObjectRaw");
            } catch (java.lang.reflect.InvocationTargetException ite) {
                Logger.warn("RegistryMocker: Failed to register items using addObject (InvocationTargetException)");
                if (ite.getCause() != null) {
                    Logger.warn(
                        "  Cause: " + ite.getCause()
                            .getMessage());
                    ite.getCause()
                        .printStackTrace();
                } else {
                    ite.printStackTrace();
                }
                // Continue anyway - GameData mocking is more important
            } catch (Exception regEx) {
                Logger.warn("RegistryMocker: Failed to register items using addObject: " + regEx.getMessage());
                regEx.printStackTrace();
                // Continue anyway - GameData mocking is more important
            }

            // Also mock GameData.getItemRegistry() for ItemJson.resolveItemStack()
            // GameData.getItemRegistry() returns getMain().iItemRegistry where getMain() returns mainData
            try {
                Class<?> gameDataClass = Class.forName("cpw.mods.fml.common.registry.GameData");

                // Get the mainData field
                Field mainDataField = gameDataClass.getDeclaredField("mainData");
                mainDataField.setAccessible(true);
                Object mainData = mainDataField.get(null);

                if (mainData == null) {
                    // Create a GameData instance if it doesn't exist
                    Constructor<?> gameDataConstructor = gameDataClass.getDeclaredConstructor();
                    gameDataConstructor.setAccessible(true);
                    mainData = gameDataConstructor.newInstance();
                    setStaticFinalField(gameDataClass, "mainData", mainData);
                    System.err.println("Created new GameData instance for mainData");
                }

                // Set iItemRegistry field in the GameData instance using reflection hack for final fields
                // We need to use the same technique as setStaticFinalField but for instance fields
                Field iItemRegistryField = gameDataClass.getDeclaredField("iItemRegistry");
                iItemRegistryField.setAccessible(true);

                try {
                    // Try Java 8-11 approach first
                    Field modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    modifiersField.setInt(iItemRegistryField, iItemRegistryField.getModifiers() & ~Modifier.FINAL);
                    iItemRegistryField.set(mainData, itemRegistry);
                } catch (NoSuchFieldException e) {
                    // Java 12+: Use Unsafe
                    Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                    Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
                    unsafeField.setAccessible(true);
                    Object unsafe = unsafeField.get(null);

                    Method objectFieldOffsetMethod = unsafeClass.getMethod("objectFieldOffset", Field.class);
                    Method putObjectMethod = unsafeClass.getMethod("putObject", Object.class, long.class, Object.class);

                    long offset = (Long) objectFieldOffsetMethod.invoke(unsafe, iItemRegistryField);
                    putObjectMethod.invoke(unsafe, mainData, offset, itemRegistry);
                }

                System.err.println("========== SUCCESS: Mocked mainData.iItemRegistry ==========");

                // Also set the deprecated static itemRegistry field in GameData
                // Some code might still use GameData.itemRegistry directly
                try {
                    setStaticFinalField(gameDataClass, "itemRegistry", itemRegistry);
                    System.err.println("Also set GameData.itemRegistry (deprecated field)");
                } catch (Exception e3) {
                    System.err.println("Failed to set GameData.itemRegistry: " + e3.getMessage());
                }

                // Verify that getItemRegistry() works and can find items
                try {
                    Method getItemRegistryMethod = gameDataClass.getMethod("getItemRegistry");
                    Object registry = getItemRegistryMethod.invoke(null);
                    if (registry != null) {
                        Method getObjectMethod = registry.getClass()
                            .getMethod("getObject", String.class);
                        Object diamondSword = getObjectMethod.invoke(registry, "minecraft:diamond_sword");
                        System.err.println(
                            "Verification: GameData.getItemRegistry().getObject(\"minecraft:diamond_sword\") = "
                                + diamondSword);
                    } else {
                        System.err.println("WARNING: GameData.getItemRegistry() returned null!");
                    }
                } catch (Exception e4) {
                    System.err.println("Failed to verify item registry: " + e4.getMessage());
                    e4.printStackTrace();
                }
            } catch (Exception e2) {
                System.err.println("========== ERROR: Failed to mock GameData.mainData.iItemRegistry ==========");
                e2.printStackTrace();
            }

            System.out.println("RegistryMocker: Successfully mocked Item.itemRegistry");
        } catch (Exception e) {
            Logger.warn("RegistryMocker: Failed to mock Item.itemRegistry: " + e.getMessage());
        }
    }

    private static void mockBlocks() throws Exception {
        try {
            Class<?> blocksClass = Class.forName("net.minecraft.init.Blocks");
            setStaticFinalField(blocksClass, "wool", createMockBlock("wool"));
            setStaticFinalField(blocksClass, "stone", createMockBlock("stone"));
            setStaticFinalField(blocksClass, "grass", createMockBlock("grass"));
            setStaticFinalField(blocksClass, "dirt", createMockBlock("dirt"));
            setStaticFinalField(blocksClass, "cobblestone", createMockBlock("cobblestone"));
            setStaticFinalField(blocksClass, "obsidian", createMockBlock("obsidian"));
            setStaticFinalField(blocksClass, "bedrock", createMockBlock("bedrock"));
        } catch (ClassNotFoundException e) {
            Logger.warn("RegistryMocker: Blocks class not found.");
        }
    }

    private static void setStaticFinalField(Class<?> clazz, String fieldName, Object value) throws Exception {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);

            // Try the old way first (works on Java 8-11)
            try {
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                field.set(null, value);
                return;
            } catch (NoSuchFieldException e) {
                // Java 12+: modifiers field doesn't exist, fall through to Unsafe
            }

            // Java 12+: Use Unsafe to set the field
            try {
                Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
                unsafeField.setAccessible(true);
                Object unsafe = unsafeField.get(null);

                // Get the field offset
                Method staticFieldOffsetMethod = unsafeClass.getMethod("staticFieldOffset", Field.class);
                Method staticFieldBaseMethod = unsafeClass.getMethod("staticFieldBase", Field.class);
                Method putObjectMethod = unsafeClass.getMethod("putObject", Object.class, long.class, Object.class);

                Object base = staticFieldBaseMethod.invoke(unsafe, field);
                long offset = (Long) staticFieldOffsetMethod.invoke(unsafe, field);
                putObjectMethod.invoke(unsafe, base, offset, value);

                System.out.println("RegistryMocker: Successfully set " + fieldName + " using Unsafe");
            } catch (Exception unsafeEx) {
                Logger.warn(
                    "RegistryMocker: Failed to set field using Unsafe: " + fieldName + " - " + unsafeEx.getMessage());
                throw unsafeEx;
            }
        } catch (NoSuchFieldException e) {
            Logger.warn("RegistryMocker: Field not found: " + fieldName);
        }
    }

    private static Item createMockItem(String name) {
        try {
            Constructor<Item> constructor = Item.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Item item = constructor.newInstance();
            return item;
        } catch (Exception e) {
            return new Item() {

                @Override
                public String getUnlocalizedName() {
                    return "item." + name;
                }
            };
        }
    }

    private static void mockFML() {
        System.out.println("RegistryMocker: Mocking FML...");
        try {
            Class<?> sideClass = Class.forName("cpw.mods.fml.relauncher.Side");
            Object sideClient = null;
            for (Object obj : sideClass.getEnumConstants()) {
                if (obj.toString()
                    .equals("CLIENT")) {
                    sideClient = obj;
                    break;
                }
            }

            Class<?> logClass = Class.forName("cpw.mods.fml.relauncher.FMLRelaunchLog");
            Field sideField = logClass.getDeclaredField("side");
            sideField.setAccessible(true);

            // final を外す試み
            try {
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(sideField, sideField.getModifiers() & ~Modifier.FINAL);
            } catch (Throwable t) {}

            if (sideField.get(null) == null) {
                sideField.set(null, sideClient);
                System.out.println("RegistryMocker: FMLRelaunchLog.side set to CLIENT");
            } else {
                System.out.println("RegistryMocker: FMLRelaunchLog.side already set to " + sideField.get(null));
            }
        } catch (Throwable t) {
            System.out.println("RegistryMocker: FML mock failed: " + t.getMessage());
        }
    }

    private static Object createMockBlock(String name) {
        try {
            Class<?> blockClass = Class.forName("net.minecraft.block.Block");
            Class<?> materialClass = Class.forName("net.minecraft.block.material.Material");
            Field airField = materialClass.getDeclaredField("air");
            airField.setAccessible(true);
            Object airMaterial = airField.get(null);

            Constructor<?> constructor = blockClass.getDeclaredConstructor(materialClass);
            constructor.setAccessible(true);
            return constructor.newInstance(airMaterial);
        } catch (Exception e) {
            Logger.warn("RegistryMocker: Failed to create mock block for " + name + ": " + e.getMessage());
            return null;
        }
    }
}
