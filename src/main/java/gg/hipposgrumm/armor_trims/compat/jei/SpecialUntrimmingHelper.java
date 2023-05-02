package gg.hipposgrumm.armor_trims.compat.jei;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import gg.hipposgrumm.armor_trims.Armortrims;
import gg.hipposgrumm.armor_trims.item.SmithingTemplate;
import gg.hipposgrumm.armor_trims.recipes.UntrimmingSpecialRecipe;
import gg.hipposgrumm.armor_trims.trimming.TrimmableItem;
import gg.hipposgrumm.armor_trims.util.LargeItemLists;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

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
        List<ItemStack> inputItems = new ArrayList<>();
        List<ItemStack> outputItems = new ArrayList<>();
        for (Item armorItem:LargeItemLists.getAllArmors()) {
            for (Item templateItem:new ArrayList<Item>(Arrays.asList( // Limited list of templates so that Minecraft doesn't (completely) freeze when looking up uses for Shears.
                Armortrims.COAST_ARMOR_TRIM.get(),
                Armortrims.EYE_ARMOR_TRIM.get(),
                Armortrims.RIB_ARMOR_TRIM.get(),
                Armortrims.VEX_ARMOR_TRIM.get(),
                Armortrims.WARD_ARMOR_TRIM.get()
        ))) {
                for (Item materialItem:new ArrayList<Item>(Arrays.asList( // Limited list of templates so that Minecraft doesn't (completely) freeze when looking up uses for Shears.
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
        LogUtils.getLogger().info("Inputs: "+inputItems+" \nOutputs: "+outputItems);
        List<List<ItemStack>> inputs = List.of(inputItems.stream().toList(), Collections.singletonList(new ItemStack(Items.SHEARS)));
        List<ItemStack> outputs = outputItems.stream().toList();

        helper.setInputs(builder, VanillaTypes.ITEM_STACK, inputs, 0, 0);
        helper.setOutputs(builder, VanillaTypes.ITEM_STACK, outputs);
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return name;
    }
}
