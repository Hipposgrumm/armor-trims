package gg.hipposgrumm.armor_trims.recipes;

import com.google.gson.JsonObject;
import gg.hipposgrumm.armor_trims.Armortrims;
import gg.hipposgrumm.armor_trims.config.Config;
import gg.hipposgrumm.armor_trims.trimming.TrimmableItem;
import gg.hipposgrumm.armor_trims.trimming.Trims;
import gg.hipposgrumm.armor_trims.util.LargeItemLists;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class UntrimmingSpecialRecipe extends CustomRecipe {
    private Item armor;
    private Trims trim;
    private Item material;
    private ItemStack result;

    public UntrimmingSpecialRecipe(ResourceLocation p_44487_) {
        super(p_44487_);
    }

    public boolean matches(CraftingContainer crafting, Level p_44500_) {
        boolean isArmor = false;
        boolean isShears = false;

        for(int i = 0; i < crafting.getContainerSize(); ++i) {
            ItemStack itemstack = crafting.getItem(i);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() instanceof ArmorItem && !isArmor) {
                    isArmor = true;
                } else {
                    if (!itemstack.is(Tags.Items.SHEARS) || isShears) {
                        return false;
                    }
                    isShears = true;
                }
            }
        }

        return isArmor && isShears;
    }

    public ItemStack assemble(CraftingContainer crafting) {
        ItemStack armorItem = ItemStack.EMPTY;
        ItemStack shearsItem = ItemStack.EMPTY;

        for(int i = 0; i < crafting.getContainerSize(); ++i) {
            ItemStack itemstack1 = crafting.getItem(i);
            if (!itemstack1.isEmpty() && itemstack1.getItem() instanceof ArmorItem) {
                armorItem = itemstack1;
                break;
            }
        }

        for(int i = 0; i < crafting.getContainerSize(); ++i) {
            ItemStack itemstack1 = crafting.getItem(i);
            if (!itemstack1.isEmpty() && itemstack1.is(Tags.Items.SHEARS)) {
                shearsItem = itemstack1;
                break;
            }
        }

        ItemStack finalItem = ItemStack.EMPTY;
        if (armorItem.getItem() instanceof ArmorItem && TrimmableItem.isTrimmed(armorItem)) {
            finalItem = new ItemStack(armorItem.getItem());
            TrimmableItem.clearTrim(finalItem);
        }
        if (!Config.enableUntrimming()) {
            return ItemStack.EMPTY;
        } else {
            try {
                trim = Trims.valueOf(TrimmableItem.getTrim(armorItem));
            } catch (IllegalArgumentException e) {
                trim = null;
            }
            material = ForgeRegistries.ITEMS.getValue(TrimmableItem.getMaterial(armorItem));
            return finalItem;
        }
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer p_43820_) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(p_43820_.getContainerSize(), ItemStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = p_43820_.getItem(i);
            if (itemstack.hasContainerItem()) {
                nonnulllist.set(i, itemstack.getContainerItem());
            } else if (itemstack.is(Tags.Items.SHEARS)) {
                if (itemstack.isDamageableItem()) itemstack.setDamageValue(itemstack.getDamageValue()-1);
                break;
            }
        }

        for (int i=0;i<p_43820_.getContainerSize();++i) {
            if (p_43820_.getItem(i).isEmpty()) {
                p_43820_.setItem(i, new ItemStack(material));
            }
        }

        for (int i=0;i<p_43820_.getContainerSize();++i) {
            if (p_43820_.getItem(i).isEmpty()) {
                p_43820_.setItem(i, new ItemStack(LargeItemLists.getTemplateFromTrim(trim)));
            }
        }

        return nonnulllist;
    }

    public boolean canCraftInDimensions(int p_44489_, int p_44490_) {
        return p_44489_ >= 2 && p_44490_ >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return Armortrims.UNTRIMMING_RECIPE.get();
    }


    public static class Serializer implements RecipeSerializer<UntrimmingSpecialRecipe> {
        public static final UntrimmingSpecialRecipe.Serializer INSTANCE = new UntrimmingSpecialRecipe.Serializer();
        public static final ResourceLocation ID = new ResourceLocation(Armortrims.MODID,"crafting_special_untrimming");

        @Override
        public UntrimmingSpecialRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new UntrimmingSpecialRecipe(id);
        }

        @Override
        public UntrimmingSpecialRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return new UntrimmingSpecialRecipe(id);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, UntrimmingSpecialRecipe recipe) {
            buf.writeItem(recipe.result);
        }

        @Override
        public RecipeSerializer<?> setRegistryName(ResourceLocation name) {
            return INSTANCE;
        }

        @Nullable
        @Override
        public ResourceLocation getRegistryName() {
            return ID;
        }

        @Override
        public Class<RecipeSerializer<?>> getRegistryType() {
            return UntrimmingSpecialRecipe.Serializer.castClass(RecipeSerializer.class);
        }

        @SuppressWarnings("unchecked") // Need this wrapper, because generics
        private static <G> Class<G> castClass(Class<?> cls) {
            return (Class<G>)cls;
        }
    }

}