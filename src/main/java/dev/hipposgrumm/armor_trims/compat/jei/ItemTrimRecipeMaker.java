package dev.hipposgrumm.armor_trims.compat.jei;

import dev.hipposgrumm.armor_trims.Armortrims;
import dev.hipposgrumm.armor_trims.api.jei.ArmortrimsRecipe;
import dev.hipposgrumm.armor_trims.api.item.ArmorTrimSmithingTemplate;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

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

public class ItemTrimRecipeMaker {
    public static List<ArmortrimsRecipe> getTrimmingRecipes(/*? if forge {*/IIngredientManager ingredientManager/*?}*/) {
        // Creating a stream this way preserves the original order, while using values().stream() does not.
        List<ItemStack> armorItems = StreamSupport.stream(
                //? if forge {
                ForgeRegistries.ITEMS.spliterator(),
                //?} else {
                /*Registry.ITEM.spliterator(),
                *///?}
                false).filter(
                item -> item instanceof ArmorItem
        ).map(Item::getDefaultInstance).filter(item ->
                !item/*? if <1.18.2 {*//*.getItem()*//*?}*/.is(Armortrims.NON_TRIMMABLE_ITEMS_TAG)
        ).collect(Collectors.toList());

        //? if forge {
        Stream<Item> itemstream = ingredientManager
                .getAllIngredients(VanillaTypes./*? if >=1.17 {*/ITEM_STACK/*?} else {*//*ITEM*//*?}*/)
                .stream().map(ItemStack::getItem);
        //?} else {
        /*Stream<Item> itemstream = StreamSupport
                .stream(Registry.ITEM.spliterator(), false);
        *///?}
        Ingredient materials = Ingredient.of(Armortrims.TRIM_MATERIALS_TAG);
        if (materials.getItems().length == 0) return Collections.emptyList();
        return itemstream.flatMap(templateItem -> {
            List<ArmortrimsRecipe> recipes = new ArrayList<>();
            if (templateItem instanceof ArmorTrimSmithingTemplate) {
                Ingredient template = Ingredient.of(templateItem);
                for (ItemStack armorItem : armorItems) {
                    recipes.add(new ArmortrimsRecipe(
                            Ingredient.of(armorItem),
                            template,
                            materials,
                            //? if >=1.17 {
                            ItemStack.EMPTY
                            //?} else {
                            /*armorItem
                            *///?}
                    ));
                }
            }
            return recipes.stream();
        }).collect(Collectors.toList());
    }
}