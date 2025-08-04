package dev.hipposgrumm.armor_trims.compat.jei;

import dev.hipposgrumm.armor_trims.api.item.UpgradeSmithingTemplate;
import dev.hipposgrumm.armor_trims.api.jei.ArmortrimsRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

//? if forge {
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraftforge.registries.ForgeRegistries;
//?}

public class ItemUpgradeRecipeMaker {
    public static List<ArmortrimsRecipe> getUpgradingRecipes(/*? if forge {*/IIngredientManager ingredientManager/*?}*/) {
        // We need level.
        Level level = Minecraft.getInstance().level;
        if (level == null) return Collections.emptyList();

        // Creating a stream this way preserves the original order, while using values().stream() does not.
        List<UpgradeSmithingTemplate> upgradeTemplates = StreamSupport.stream(
                //? if forge {
                ForgeRegistries.ITEMS.spliterator(),
                //?} else {
                /*Registry.ITEM.spliterator(),
                *///?}
                false).filter(
                item -> item instanceof UpgradeSmithingTemplate
        ).map(item -> (UpgradeSmithingTemplate) item).collect(Collectors.toList());

        //? if forge {
        Stream<ItemStack> itemstream = ingredientManager
                .getAllIngredients(VanillaTypes./*? if >=1.17 {*/ITEM_STACK/*?} else {*//*ITEM*//*?}*/).stream();
        //?} else {
        /*Stream<ItemStack> itemstream = StreamSupport
                .stream(Registry.ITEM.spliterator(), false)
                .map(Item::getDefaultInstance);
        *///?}
        return itemstream.flatMap(baseItem -> {
            List<ArmortrimsRecipe> recipes = new ArrayList<>();
            for (UpgradeSmithingTemplate templateItem : upgradeTemplates) {
                ItemStack upgradedItem = getUpgradedItem(level, baseItem.copy(), templateItem);
                if (upgradedItem != null && !upgradedItem.isEmpty()) {
                    recipes.add(new ArmortrimsRecipe(
                            Ingredient.of(baseItem),
                            Ingredient.of(templateItem),
                            templateItem.materials(),
                            upgradedItem
                    ));
                }
            }
            return recipes.stream();
        }).collect(Collectors.toList());
    }

    public static ItemStack getUpgradedItem(Level level, ItemStack baseItem, UpgradeSmithingTemplate templateItem) {
        ItemStack[] items = templateItem.materials().getItems();
        if (items.length==0) return ItemStack.EMPTY;

        return templateItem.getResult(baseItem, templateItem.getDefaultInstance(), items[0], level);
    }
}