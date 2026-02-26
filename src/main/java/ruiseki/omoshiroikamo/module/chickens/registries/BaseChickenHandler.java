package ruiseki.omoshiroikamo.module.chickens.registries;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.common.Loader;
import lombok.Getter;
import ruiseki.omoshiroikamo.api.condition.ICondition;
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickenRecipe;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistry;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.config.ConfigUpdater;
import ruiseki.omoshiroikamo.config.backport.ChickenConfig;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.integration.ModCompatInformation;
import ruiseki.omoshiroikamo.core.json.ItemJson;
import ruiseki.omoshiroikamo.core.json.ItemMaterial;
import ruiseki.omoshiroikamo.core.json.JsonUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.chickens.common.init.ChickensItems;

public abstract class BaseChickenHandler {

    @Getter
    protected String modID;
    @Getter
    protected String modName;
    protected String texturesLocation;
    private int startID = 0;
    private int id = 0;
    protected String configFileName;
    private boolean needsMod = true;

    public BaseChickenHandler(String modID, String modName, String texturesLocation) {
        this.modID = modID;
        this.modName = modName;
        this.texturesLocation = texturesLocation;
        this.configFileName = modID.toLowerCase() + "_chickens.json";
    }

    public void setStartID(int startID) {
        this.startID = startID;
        this.id = startID;
    }

    public void setNeedsModPresent(boolean bool) {
        this.needsMod = bool;
    }

    private List<ChickenMaterial> loadedCustomMaterials;

    public List<ChickensRegistryItem> tryRegisterChickens(List<ChickensRegistryItem> allChickens) {
        Logger.info("Looking for {} chickens...", modName);
        if (needsMod && !Loader.isModLoaded(modID)) {
            Logger.info("Skipped {} chickens → required mod \"{}\" is not loaded.", modName, modID);
            return allChickens;
        }
        Logger.info("Loading {} chickens...", modName);
        File configDir = new File("config/" + LibMisc.MOD_ID + "/chicken/");
        File configFile = new File(configDir, configFileName);
        if (!configFile.exists()) {
            List<ChickensRegistryItem> defaultChickens = registerChickens();
            registerAllParents(defaultChickens);
            createDefaultConfig(configFile, defaultChickens);
        }
        if (ChickenConfig.updateMissing) {
            updateConfigWithMissing(configFile, registerChickens());
            ConfigUpdater.updateBoolean(ChickenConfig.class, "updateMissing", false);
        }
        this.id = startID;
        try {
            ChickenJsonReader reader = new ChickenJsonReader(configFile);
            List<ChickenMaterial> materials = reader.read();
            if (materials == null) {
                Logger.info("{} is empty or invalid.", configFileName);
                return allChickens;
            }
            this.loadedCustomMaterials = materials;
            boolean migrated = false;
            for (ChickenMaterial data : materials) {
                try {
                    if (!data.validate()) continue;
                    ItemStack layItem = ItemMaterial.resolveItemStack(data.layItem);
                    if (layItem == null) {
                        Logger.error("Failed to resolve lay item for custom chicken: {}", data.name);
                        continue;
                    }
                    ItemStack dropItem = ItemMaterial.resolveItemStack(data.dropItem);
                    int bgColor = JsonUtils.resolveColor(data.bgColor, 0xFFFFFF);
                    int fgColor = JsonUtils.resolveColor(data.fgColor, 0xFF0000);
                    int tint = JsonUtils.resolveColor(data.tintColor, 0xFFFFFF);
                    SpawnType type = SpawnType.NORMAL;
                    try {
                        if (data.spawnType != null && !data.spawnType.isEmpty())
                            type = SpawnType.valueOf(data.spawnType.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        Logger.error("Invalid spawn type for {}: {}", data.name, data.spawnType);
                    }
                    if (data.id == null || data.id < 0) {
                        data.id = fixedID(data.name);
                        migrated = true;
                    }
                    ChickensRegistryItem chicken = addChicken(data.name, data.id, data.texture, bgColor, fgColor, type);
                    if (chicken == null) continue;
                    Logger.debug(
                        "Registering ({}) Chicken: '{}':{}:{}",
                        this.modID,
                        chicken.getEntityName(),
                        chicken.getId(),
                        layItem.getDisplayName());
                    chicken.setEnabled(data.enabled);
                    chicken.setCoefficient(data.coefficient);
                    chicken.setLayItem(layItem);
                    if (dropItem != null) chicken.setDropItem(dropItem);
                    if (data.foods != null && !data.foods.isEmpty()) {
                        for (ItemMaterial foodMat : data.foods) {
                            ItemStack foodStack = ItemMaterial.resolveItemStack(foodMat);
                            if (foodStack != null) chicken.addRecipe(foodStack, layItem);
                        }
                    } else {
                        ItemStack defaultFood = ChickensItems.CHICKEN_FOOD.newItemStack(1, chicken.getId());
                        chicken.addRecipe(defaultFood, layItem);
                    }
                    if (data.mutationChance != null) chicken.setMutationChance(data.mutationChance);
                    if (data.lang != null) JsonUtils.registerLang("entity." + data.name + ".name", data.lang);
                    if (data.textureOverlay != null && !data.textureOverlay.isEmpty()) {
                        chicken.setTintColor(tint);
                        chicken.setTextureOverlay(
                            new ResourceLocation(LibMisc.MOD_ID, this.texturesLocation + data.textureOverlay + ".png"));
                    }
                    if (data.texture != null && !data.texture.isEmpty()) {
                        String itemSubPath = this.texturesLocation.replace("textures/entity/", "")
                            .replaceAll("/$", "");
                        chicken.setIconName(LibResources.PREFIX_MOD + itemSubPath + "/" + data.texture);
                    }
                    if (data.textureOverlay != null && !data.textureOverlay.isEmpty()) {
                        String itemSubPath = this.texturesLocation.replace("textures/entity/", "")
                            .replaceAll("/$", "");
                        chicken.setIconOverlayName(LibResources.PREFIX_MOD + itemSubPath + "/" + data.textureOverlay);
                    }
                    if (!data.realConditions.isEmpty()) {
                        for (ICondition condition : data.realConditions) chicken.addCondition(condition);
                    }
                    ModCompatInformation.addInformation(
                        chicken.getId(),
                        new ModCompatInformation(this.getModID(), "", this.getModName()));
                    allChickens.add(chicken);
                } catch (Exception e) {
                    Logger.error("Error registering custom chicken {}", data.name, e);
                }
            }
            if (migrated) {
                new ChickenJsonWriter(configFile).write(materials);
                Logger.info("Migrated config with new IDs: {}", configFile.getName());
            }
        } catch (IOException e) {
            Logger.error("Failed to read {}: {}", configFileName, e.getMessage());
        }
        return allChickens;
    }

    public void loadParents(List<ChickensRegistryItem> allChickens) {
        if (loadedCustomMaterials == null) return;
        for (ChickenMaterial data : loadedCustomMaterials) {
            if (data.parent1 == null || data.parent1.isEmpty() || data.parent2 == null || data.parent2.isEmpty())
                continue;
            ChickensRegistryItem child = findChicken(allChickens, data.name);
            if (child == null) continue;
            ChickensRegistryItem p1 = findChicken(allChickens, data.parent1);
            ChickensRegistryItem p2 = findChicken(allChickens, data.parent2);
            if (p1 != null && p2 != null) child.setParents(p1, p2);
            else Logger
                .error("Could not find parents for custom chicken {}: {}, {}", data.name, data.parent1, data.parent2);
        }
        loadedCustomMaterials = null;
    }

    public abstract List<ChickensRegistryItem> registerChickens();

    public abstract void registerAllParents(List<ChickensRegistryItem> allChickens);

    protected int nextID() {
        return this.id++;
    }

    protected int fixedID(String name) {
        int hash = (modID + ":" + name).toLowerCase()
            .hashCode();
        return startID + Math.abs(hash % (30000 - startID));
    }

    protected ChickensRegistryItem addChicken(String entityName, int id, String texture, int bgColor, int fgColor,
        SpawnType spawnType) {
        ChickensRegistryItem chicken = new ChickensRegistryItem(
            id,
            entityName,
            new ResourceLocation(LibMisc.MOD_ID, texturesLocation + texture + ".png"),
            bgColor,
            fgColor);
        chicken.setSpawnType(spawnType);
        chicken.addRecipe(ChickensItems.CHICKEN_FOOD.newItemStack(1, id), null);
        return chicken;
    }

    protected void setParents(ChickensRegistryItem child, Object parent1, Object parent2) {
        ChickensRegistryItem pChicken1 = null;
        ChickensRegistryItem pChicken2 = null;
        if (child == null || parent1 == null || parent2 == null) return;
        if (parent1 instanceof String) pChicken1 = findChickenChickensMod((String) parent1);
        else if (parent1 instanceof ChickensRegistryItem) pChicken1 = (ChickensRegistryItem) parent1;
        if (parent2 instanceof String) pChicken2 = findChickenChickensMod((String) parent2);
        else if (parent2 instanceof ChickensRegistryItem) pChicken2 = (ChickensRegistryItem) parent2;
        if (pChicken1 != null && pChicken2 != null) child.setParents(pChicken1, pChicken2);
    }

    public static ChickensRegistryItem findChicken(Collection<ChickensRegistryItem> chickens, String name) {
        for (ChickensRegistryItem chicken : chickens) {
            if (chicken.getEntityName()
                .compareToIgnoreCase(name) == 0) return chicken;
        }
        return findChickenChickensMod(name);
    }

    public static ChickensRegistryItem findChickenChickensMod(String name) {
        for (ChickensRegistryItem chicken : ChickensRegistry.INSTANCE.getItems()) {
            if (chicken.getEntityName()
                .compareToIgnoreCase(name) == 0) return chicken;
        }
        return null;
    }

    @Nullable
    public ItemStack getFirstOreDictionary(String oreID) {
        List<ItemStack> itemstacks = OreDictionary.getOres(oreID);
        return !itemstacks.isEmpty() ? itemstacks.get(0) : null;
    }

    private ChickenMaterial toChickenMaterial(ChickensRegistryItem chicken) {
        if (chicken == null) return null;
        ChickenMaterial mat = new ChickenMaterial();
        mat.id = chicken.getId();
        mat.name = chicken.getEntityName();
        mat.enabled = true;
        ResourceLocation tex = chicken.getTexture();
        mat.texture = JsonUtils.stripPng(
            tex.getResourcePath()
                .substring(
                    tex.getResourcePath()
                        .lastIndexOf("/") + 1));
        if (chicken.getTextureOverlay() != null) {
            mat.textureOverlay = JsonUtils.stripPng(
                chicken.getTextureOverlay()
                    .getResourcePath()
                    .substring(
                        chicken.getTextureOverlay()
                            .getResourcePath()
                            .lastIndexOf("/") + 1));
        }
        mat.tintColor = JsonUtils.parseColor(chicken.getTintColor());
        mat.bgColor = JsonUtils.parseColor(chicken.getBgColor());
        mat.fgColor = JsonUtils.parseColor(chicken.getFgColor());
        mat.parent1 = chicken.getParent1() != null ? chicken.getParent1()
            .getEntityName() : null;
        mat.parent2 = chicken.getParent2() != null ? chicken.getParent2()
            .getEntityName() : null;
        mat.spawnType = chicken.getSpawnType()
            .name();
        mat.coefficient = chicken.getCoefficient();

        // Items
        if (chicken.getLayItem() != null) {
            mat.layItem = ItemMaterial.parseItemStack(chicken.getLayItem());
        } else if (chicken.getLayString() != null) {
            mat.layItem = new ItemMaterial();
            ItemJson parsed = ItemJson.parseItemString(chicken.getLayString());
            if (parsed != null) {
                mat.layItem.name = parsed.name;
                mat.layItem.ore = parsed.ore;
                mat.layItem.amount = parsed.amount;
                mat.layItem.meta = parsed.meta;
            }
        }

        if (chicken.getDropItem() != null) {
            mat.dropItem = ItemMaterial.parseItemStack(chicken.getDropItem());
        }

        // Foods from recipes
        if (chicken.getRecipes() != null && !chicken.getRecipes()
            .isEmpty()) {
            for (ChickenRecipe recipe : chicken.getRecipes()) {
                if (recipe.getInput() != null) {
                    mat.foods.add(ItemMaterial.parseItemStack(recipe.getInput()));
                }
            }
        }

        mat.mutationChance = chicken.getMutationChance();
        mat.lang = chicken.getLang();
        return mat;
    }

    public void createDefaultConfig(File file, List<ChickensRegistryItem> chickens) {
        try {
            List<ChickenMaterial> materials = new ArrayList<>();
            for (ChickensRegistryItem chicken : chickens) {
                ChickenMaterial mat = toChickenMaterial(chicken);
                if (mat != null) materials.add(mat);
            }
            new ChickenJsonWriter(file).write(materials);
            Logger.info("Created default {}", configFileName);
        } catch (Exception e) {
            Logger.error("Failed to create default config: {} ({})", file.getPath(), e);
        }
    }

    private void updateConfigWithMissing(File file, List<ChickensRegistryItem> allChickens) {
        List<ChickenMaterial> existing = new ArrayList<>();
        if (file.exists()) {
            try {
                existing.addAll(new ChickenJsonReader(file).read());
            } catch (Exception e) {
                Logger.error("Failed to read existing chicken config: {}", e);
            }
        }
        boolean updated = false;
        for (ChickensRegistryItem chicken : allChickens) {
            if (chicken == null) continue;
            boolean exists = existing.stream()
                .anyMatch(c -> c.name.equalsIgnoreCase(chicken.getEntityName()));
            if (!exists) {
                ChickenMaterial mat = toChickenMaterial(chicken);
                if (mat != null) {
                    existing.add(mat);
                    updated = true;
                }
            }
        }
        if (updated) {
            try {
                new ChickenJsonWriter(file).write(existing);
                Logger.info("Updated chicken config with missing chickens: {}", file.getName());
            } catch (IOException e) {
                Logger.error("Failed to update chicken config: {}", e);
            }
        }
    }
}
