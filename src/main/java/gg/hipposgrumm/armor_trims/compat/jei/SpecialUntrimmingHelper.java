package gg.hipposgrumm.armor_trims.compat.jei;

import com.mojang.logging.LogUtils;
import gg.hipposgrumm.armor_trims.Armortrims;
import gg.hipposgrumm.armor_trims.api.ArmortrimsApi;
import gg.hipposgrumm.armor_trims.item.SmithingTemplate;
import gg.hipposgrumm.armor_trims.recipes.UntrimmingSpecialRecipe;
import gg.hipposgrumm.armor_trims.trimming.TrimmableItem;
import gg.hipposgrumm.armor_trims.util.AssociateTagsWithItems;
import gg.hipposgrumm.armor_trims.util.LargeItemLists;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.*;

public class SpecialUntrimmingHelper implements ICraftingCategoryExtension {
    private final ResourceLocation name;

    public SpecialUntrimmingHelper(UntrimmingSpecialRecipe recipe) {
        this.name = recipe.getId();
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull ICraftingGridHelper helper, @Nonnull IFocusGroup focuses) {
        List<Item> focus = focuses.getFocuses(VanillaTypes.ITEM_STACK, RecipeIngredientRole.INPUT)
                .map(f -> f.getTypedValue().getIngredient().getItem())
                .toList();

        List<ItemStack> inputItems = new ArrayList<>();
        List<ItemStack> outputItems = new ArrayList<>();
        if (Collections.disjoint(focus, List.of(new AssociateTagsWithItems(Tags.Items.SHEARS.location().toString()).getItems()))) {
            for (Item armorItem : LargeItemLists.getAllArmors()) {
                for (Item templateItem : new ArrayList<>(Arrays.asList( // Limited list of templates so that Minecraft doesn't (completely) freeze when looking up uses for Shears.
                        ArmortrimsApi.getItem(new ResourceLocation(Armortrims.MODID, "coast_armor_trim_smithing_template")),
                        ArmortrimsApi.getItem(new ResourceLocation(Armortrims.MODID, "eye_armor_trim_smithing_template")),
                        ArmortrimsApi.getItem(new ResourceLocation(Armortrims.MODID, "rib_armor_trim_smithing_template")),
                        ArmortrimsApi.getItem(new ResourceLocation(Armortrims.MODID, "vex_armor_trim_smithing_template")),
                        ArmortrimsApi.getItem(new ResourceLocation(Armortrims.MODID, "ward_armor_trim_smithing_template"))
                ))) {
                    for (Item materialItem : new ArrayList<>(Arrays.asList( // Limited list of templates so that Minecraft doesn't (completely) freeze when looking up uses for Shears.
                            Items.EMERALD,
                            Items.LAPIS_LAZULI,
                            Items.AMETHYST_SHARD,
                            Items.DIAMOND,
                            Items.GOLD_INGOT
                    ))) {
                        inputItems.add(TrimmableItem.applyTrim(new ItemStack(armorItem), ((SmithingTemplate) templateItem).getTrim(), materialItem.getDefaultInstance(), true));
                        outputItems.add(armorItem.getDefaultInstance());
                    }
                }
            }
            List<List<ItemStack>> inputs = List.of(inputItems.stream().toList(), Collections.singletonList(Items.SHEARS.getDefaultInstance()));
            List<ItemStack> outputs = outputItems.stream().toList();
            helper.setInputs(builder, VanillaTypes.ITEM_STACK, inputs, 0, 0);
            helper.setOutputs(builder, VanillaTypes.ITEM_STACK, outputs);
        }

    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return name;
    }
}
