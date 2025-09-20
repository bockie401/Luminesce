package net.bockie.luminesce.item;

import net.bockie.luminesce.Luminesce;
import net.bockie.luminesce.item.custom.GlimmetalIngotItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static void registerModItems() {
        Luminesce.LOGGER.info("Registering items for " + Luminesce.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemsToIngredientGroup);

    }
    public static final Item GLIMMETAL_INGOT = registerItem("glimmetal_ingot", new GlimmetalIngotItem(new FabricItemSettings()));
    public static final Item LIVENED_GLIMMETAL_INGOT = registerItem("livened_glimmetal_ingot", new Item(new FabricItemSettings()));


    private static void addItemsToIngredientGroup(FabricItemGroupEntries entries) {
        entries.add(GLIMMETAL_INGOT);
        entries.add(LIVENED_GLIMMETAL_INGOT);
    }
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(Luminesce.MOD_ID , name), item);
    }
}
