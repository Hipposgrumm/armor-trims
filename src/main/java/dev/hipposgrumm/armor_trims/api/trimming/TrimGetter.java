package dev.hipposgrumm.armor_trims.api.trimming;

import dev.hipposgrumm.armor_trims.Armortrims;
import dev.hipposgrumm.armor_trims.api.trimming.trim_pattern.TrimPattern;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
//? if >=1.18
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.TagType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

//? if forge {
import net.minecraftforge.registries.ForgeRegistries;
//?}

public class TrimGetter {
    private static final ResourceLocation EMPTY_TRIM = new ResourceLocation("empty");

    private static final String TAG_TRIM_MATERIAL = "Material";
    private static final String TAG_TRIM_PATTERN = "Pattern";
    private static final String TAG_TRIM = "Trim";

    private static CompoundTag getTrimTag(ItemStack targetItem) {
        CompoundTag tag = targetItem.getTag();
        if (tag==null) return new CompoundTag();
        //? if >=1.18 {
        ReaderWithLegacySupport reader = new ReaderWithLegacySupport(TAG_TRIM);
        if (tag.accept(reader) == StreamTagVisitor.ValueResult.CONTINUE) return new CompoundTag();
        return targetItem.getTagElement(reader.get());
        //?} else {
        /*return tag.getCompound(TAG_TRIM);
        *///?}
    }

    public static boolean isTrimmed(ItemStack targetItem) {
        return !getTrimTag(targetItem).isEmpty();
    }

    public static ResourceLocation getMaterial(ItemStack targetItem) {
        CompoundTag tag = getTrimTag(targetItem);
        if (tag==null) return MissingTextureAtlasSprite.getLocation();
        //? if >=1.18 {
        ReaderWithLegacySupport reader = new ReaderWithLegacySupport(TAG_TRIM_MATERIAL);
        if (tag.accept(reader) == StreamTagVisitor.ValueResult.CONTINUE) return MissingTextureAtlasSprite.getLocation();
        return new ResourceLocation(reader.get());
        //?} else {
        /*return new ResourceLocation(tag.getString(TAG_TRIM_MATERIAL));
        *///?}
    }

    public static ResourceLocation getPattern(ItemStack targetItem) {
        CompoundTag tag = getTrimTag(targetItem);
        if (tag==null) return EMPTY_TRIM;
        //? if >=1.18 {
        ReaderWithLegacySupport reader = new ReaderWithLegacySupport(TAG_TRIM_PATTERN);
        if (tag.accept(reader) == StreamTagVisitor.ValueResult.CONTINUE) return MissingTextureAtlasSprite.getLocation();
        ResourceLocation pattern = new ResourceLocation(reader.get());
        //?} else {
        /*ResourceLocation pattern = new ResourceLocation(tag.getString(TAG_TRIM_PATTERN));
        *///?}
        if (pattern.getNamespace().equals("minecraft")) pattern = new ResourceLocation(Armortrims.MODID, pattern.getPath());
        return pattern;
    }

    public static void clearTrim(ItemStack targetItem) {
        targetItem.removeTagKey("trim");
        targetItem.removeTagKey(TAG_TRIM);
    }

    public static void setMaterial(ItemStack targetItem, ItemStack material) {
        //? if forge {
        ResourceLocation location = ForgeRegistries.ITEMS.getKey(material.getItem());
        //?} else {
        /*ResourceLocation location = Registry.ITEM.getKey(material.getItem());
        *///?}

        targetItem.getOrCreateTagElement(TAG_TRIM).putString(TAG_TRIM_MATERIAL, location.toString());
    }

    public static void setPattern(ItemStack targetItem, TrimPattern trim) {
        ResourceLocation patternLoc = trim.getId();
        if (patternLoc == null) return;
        String pattern = patternLoc.toString();
        if (patternLoc.getNamespace().equals(Armortrims.MODID)) pattern = patternLoc.getPath();
        targetItem.getOrCreateTagElement(TAG_TRIM).putString(TAG_TRIM_PATTERN, pattern);
    }

    /**
     *  Returns a copy of the provided itemstack with the trim added.
     */
    public static ItemStack applyTrim(ItemStack targetItem, TrimPattern trim, ItemStack material) {
        ItemStack armorItem;
        if (trim != null) {
            armorItem = targetItem.copy();

            setPattern(armorItem, trim);
            setMaterial(armorItem, material);
            return armorItem;
        } else {
            return ItemStack.EMPTY;
        }
    }

    //? if >=1.18 {
    // Only necessary in previously supported versions.
    private static class ReaderWithLegacySupport implements StreamTagVisitor {
        private final String target;
        private String found;

        public ReaderWithLegacySupport(String target) {
            this.target = target.toLowerCase();
        }

        public String get() {
            return found;
        }

        @Override
        public ValueResult visitEnd() {
            return ValueResult.BREAK;
        }

        @Override
        public ValueResult visit(String value) {
            found = value;
            return ValueResult.BREAK;
        }

        @Override
        public ValueResult visit(byte value) {
            return ValueResult.BREAK;
        }

        @Override
        public ValueResult visit(short value) {
            return ValueResult.BREAK;
        }

        @Override
        public ValueResult visit(int value) {
            return ValueResult.BREAK;
        }

        @Override
        public ValueResult visit(long value) {
            return ValueResult.BREAK;
        }

        @Override
        public ValueResult visit(float value) {
            return ValueResult.BREAK;
        }

        @Override
        public ValueResult visit(double value) {
            return ValueResult.BREAK;
        }

        @Override
        public ValueResult visit(byte[] value) {
            return ValueResult.BREAK;
        }

        @Override
        public ValueResult visit(int[] value) {
            return ValueResult.BREAK;
        }

        @Override
        public ValueResult visit(long[] value) {
            return ValueResult.BREAK;
        }

        @Override
        public ValueResult visitList(TagType<?> type, int i) {
            return ValueResult.BREAK;
        }

        @Override
        public EntryResult visitEntry(TagType<?> type) {
            return EntryResult.ENTER;
        }

        @Override
        public EntryResult visitEntry(TagType<?> type, String key) {
            if (key.toLowerCase().equals(target)) {
                found = key;
                return EntryResult.ENTER;
            }
            return EntryResult.SKIP;
        }

        @Override
        public EntryResult visitElement(TagType<?> type, int i) {
            return EntryResult.SKIP;
        }

        @Override
        public ValueResult visitContainerEnd() {
            return found != null ? ValueResult.BREAK : ValueResult.CONTINUE;
        }

        @Override
        public ValueResult visitRootEntry(TagType<?> type) {
            return ValueResult.BREAK;
        }
    }
    //?}
}