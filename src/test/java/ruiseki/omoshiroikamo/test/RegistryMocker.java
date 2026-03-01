package ruiseki.omoshiroikamo.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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
            Class<?> frClass = Class.forName("net.minecraftforge.fluids.FluidRegistry");

            Field fluidsField = frClass.getDeclaredField("fluids");
            fluidsField.setAccessible(true);
            Map<String, Fluid> fluids = (Map<String, Fluid>) fluidsField.get(null);

            if (fluids == null) {
                fluids = new HashMap<>();
                fluidsField.set(null, fluids);
            }

            try {
                Field masterTableField = frClass.getDeclaredField("masterFluidTable");
                masterTableField.setAccessible(true);
                if (masterTableField.get(null) == null) {
                    masterTableField.set(null, new HashMap<Fluid, Integer>());
                }
            } catch (NoSuchFieldException e) {}

            // water/lava の実体作成
            if (!fluids.containsKey("water")) {
                Fluid water = new Fluid("water");
                fluids.put("water", water);
            }
            if (!fluids.containsKey("lava")) {
                Fluid lava = new Fluid("lava");
                fluids.put("lava", lava);
            }

            Fluid water = fluids.get("water");
            Fluid lava = fluids.get("lava");

            // FluidRegistry.WATER/LAVA をセット
            setStaticFinalField(frClass, "WATER", water);
            setStaticFinalField(frClass, "LAVA", lava);

            // Fluid クラス自体の静的フィールドも必要なら
            // 但し通常は FluidRegistry を経由する
        } catch (ClassNotFoundException e) {
            Logger.warn("RegistryMocker: FluidRegistry not found.");
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
        setStaticFinalField(Items.class, "iron_ingot", createMockItem("iron_ingot"));
        setStaticFinalField(Items.class, "gold_ingot", createMockItem("gold_ingot"));
        setStaticFinalField(Items.class, "diamond", createMockItem("diamond"));
        setStaticFinalField(Items.class, "coal", createMockItem("coal"));
        setStaticFinalField(Items.class, "redstone", createMockItem("redstone"));
        setStaticFinalField(Items.class, "dye", createMockItem("dye"));
        setStaticFinalField(Items.class, "quartz", createMockItem("quartz"));
        setStaticFinalField(Items.class, "ender_pearl", createMockItem("ender_pearl"));
        setStaticFinalField(Items.class, "sapling", createMockItem("sapling"));
        setStaticFinalField(Items.class, "log", createMockItem("log"));
        setStaticFinalField(Items.class, "gold_nugget", createMockItem("gold_nugget"));
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

            // static final フィールドの modifiers を書き換え
            // Java 12+ では modifiers は書き換えられないが、1.7.10 (Java 8 or 7) なら可能
            try {
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            } catch (NoSuchFieldException e) {
                // Java 12+ の場合は別の方法が必要だが、1.7.10 環境ならこれで十分なはず
            }

            field.set(null, value);
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
                if (obj.toString().equals("CLIENT")) {
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
