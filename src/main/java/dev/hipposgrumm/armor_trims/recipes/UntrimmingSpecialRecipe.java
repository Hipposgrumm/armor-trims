package dev.hipposgrumm.armor_trims.recipes;

import com.google.gson.JsonObject;
import dev.hipposgrumm.armor_trims.Armortrims;
import dev.hipposgrumm.armor_trims.config.Config;
import dev.hipposgrumm.armor_trims.api.trimming.TrimGetter;
import dev.hipposgrumm.armor_trims.util.ArmortrimsInternalUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class UntrimmingSpecialRecipe extends CustomRecipe {
    public static final ResourceLocation ID = new ResourceLocation(Armortrims.MODID,"crafting_special_untrimming");

    public UntrimmingSpecialRecipe(ResourceLocation location) {
        super(location);
    }

    public boolean matches(CraftingContainer crafting, Level level) {
        boolean hasTrimmedItem = false;
        boolean hasShearsItem = false;

        for(int i = 0; i < crafting.getContainerSize(); i++) {
            ItemStack item = crafting.getItem(i);
            if (!item.isEmpty()) {
                if (!hasTrimmedItem && TrimGetter.isTrimmed(item) ) {
                    hasTrimmedItem = true;
                } else if (item/*? if <1.18.2 {*//*.getItem()*//*?}*/.is(ArmortrimsInternalUtils.SHEARS_TAG)) {
                    hasShearsItem = true;
                }
            }
        }

        return hasTrimmedItem && hasShearsItem;
    }

    public ItemStack assemble(CraftingContainer crafting) {
        ItemStack trimmedItem = ItemStack.EMPTY;
        ItemStack shearsItem = ItemStack.EMPTY;

        for(int i = 0; i < crafting.getContainerSize(); ++i) {
            ItemStack testTrimmedItem = crafting.getItem(i);
            if (!testTrimmedItem.isEmpty() && TrimGetter.isTrimmed(testTrimmedItem)) {
                trimmedItem = testTrimmedItem;
                break;
            }
        }

        for(int i = 0; i < crafting.getContainerSize(); ++i) {
            ItemStack testShearsItem = crafting.getItem(i);
            if (!testShearsItem.isEmpty() && testShearsItem/*? if <1.18.2 {*//*.getItem()*//*?}*/.is(ArmortrimsInternalUtils.SHEARS_TAG)) {
                shearsItem = testShearsItem;
                break;
            }
        }

        ItemStack finalItem = trimmedItem.copy();
        TrimGetter.clearTrim(finalItem);
        if (shearsItem.getDamageValue()>=shearsItem.getMaxDamage()) shearsItem.shrink(1);

        return !Config.enableUntrimming?ItemStack.EMPTY:finalItem;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer crafting) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(crafting.getContainerSize(), ItemStack.EMPTY);
        boolean hasTrimmedItem = false;

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = crafting.getItem(i);
            if (!itemstack.isEmpty()) {
                if (!hasTrimmedItem && TrimGetter.isTrimmed(itemstack)) {
                    hasTrimmedItem = true;
                } else if (itemstack/*? if <1.18.2 {*//*.getItem()*//*?}*/.is(ArmortrimsInternalUtils.SHEARS_TAG)) {
                    ItemStack shearsItem = itemstack.copy();
                    shearsItem.setDamageValue(itemstack.getDamageValue()-1);
                    nonnulllist.set(i, shearsItem);
                }
            }
        }

        return nonnulllist;
    }

    public boolean canCraftInDimensions(int width, int height) {
        return (width > 0 && height > 0) && (width > 1 || height > 1); // Two items.
    }

    public RecipeSerializer<?> getSerializer() {
        return Armortrims.UNTRIMMING_RECIPE.get();
    }

    public static class Serializer implements RecipeSerializer<UntrimmingSpecialRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public UntrimmingSpecialRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new UntrimmingSpecialRecipe(id);
        }

        @Override
        public UntrimmingSpecialRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return new UntrimmingSpecialRecipe(id);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, UntrimmingSpecialRecipe recipe) {}

        //? if forge {
        public RecipeSerializer<?> setRegistryName(ResourceLocation name) {
            return INSTANCE;
        }

        public ResourceLocation getRegistryName() {
            return UntrimmingSpecialRecipe.ID;
        }

        public Class<RecipeSerializer<?>> getRegistryType() {
            return Serializer.castClass(RecipeSerializer.class);
        }

        @SuppressWarnings("unchecked") // Need this wrapper, because generics.
        private static <G> Class<G> castClass(Class<?> cls) {
            return (Class<G>)cls;
        }
        //?}
    }
}