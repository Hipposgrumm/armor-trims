package dev.hipposgrumm.armor_trims.api.base.item;

import dev.hipposgrumm.armor_trims.api.item.UpgradeSmithingTemplate;
import dev.hipposgrumm.armor_trims.util.ArmortrimsInternalUtils;
import dev.hipposgrumm.armor_trims.util.color.ColorPalette;
import dev.hipposgrumm.armor_trims.util.color.ColorPaletteManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
//? if >=1.19 {
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
//?} else {
/*import net.minecraft.network.chat.TranslatableComponent;
*///?}
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;

//? if forge {
import net.minecraftforge.registries.ForgeRegistries;
//?}

import java.lang.ref.WeakReference;
import java.util.List;

public class NetheriteUpgradeSmithingTemplate extends UpgradeSmithingTemplate {
    public NetheriteUpgradeSmithingTemplate(ResourceLocation trimIdentifier, Properties properties) {
        super(trimIdentifier, () -> Ingredient.of(ArmortrimsInternalUtils.NETHERITE_TAG), /*? if >=1.19 {*/DiamondColored.create/*?} else {*//*new DiamondColored*//*?}*/("tooltip.armor_trims.applyTo.diamond_equipment"), properties);
    }

    public ItemStack getResult(ItemStack baseItem, ItemStack templateItem, ItemStack materialItem, Level level) {
        // Make sure the material matches.
        if (!materials().test(materialItem)) return ItemStack.EMPTY;

        Container vanillaRecipeContainer = new SimpleContainer(2);
        vanillaRecipeContainer.setItem(0, baseItem);
        vanillaRecipeContainer.setItem(1, materialItem);

        List<UpgradeRecipe> list = level.getRecipeManager().getRecipesFor(RecipeType.SMITHING, vanillaRecipeContainer, level);
        if (list.isEmpty()) return ItemStack.EMPTY;

        UpgradeRecipe selectedRecipe = list.get(0);
        return selectedRecipe.assemble(vanillaRecipeContainer);
    }

    /// This class allows for the diamond color of the tooltip. A bit unnecessary, but it was fun to write so who cares.
    private static final class DiamondColored /*? if >=1.19 {*/implements Component/*?} else {*//*extends TranslatableComponent*//*?}*/ {
        private static WeakReference<ColorPalette> diamondColor = new WeakReference<>(null);
        //? if >=1.19
        private final MutableComponent base;

        public DiamondColored(String key) {
            //? if >=1.19 {
            base = Component.translatable(key);
            //?} else {
            /*super(key);
            *///?}
        }

        //? if >=1.19 {
        public static MutableComponent create(String key) {
            return Component.empty().append(new DiamondColored(key));
        }
        //?}

        public Style getStyle() {
            // Get the original Style.
            Style style = /*? if >=1.19 {*/base/*?} else {*//*super*//*?}*/.getStyle();
            // Make sure a level is loaded before trying to do tag stuff.
            if (Minecraft.getInstance().level == null) return style;
            // Get the color.
            // This color is a WeakReference, meaning this class won't stop it from getting munched by the Garbage Collector.
            ColorPalette color = diamondColor.get();
            // If the color doesn't exist (maybe it got munched by the Garbage Collector).
            if (color == null || color.discarded()) {
                // We need diamond.
                //? if forge {
                ResourceLocation diamond = ForgeRegistries.ITEMS.getKey(Items.DIAMOND);
                //?} else {
                /*ResourceLocation diamond = Registry.ITEM.getKey(Items.DIAMOND);
                *///?}

                // Get the color.
                color = ColorPaletteManager.get(diamond);
                if (color == null) return style; // It is very possible for it to not be there.

                // Set the color again. Again, there is nothing here stopping it from being munched by the Garbage Collector.
                // But don't set the color if it's the default one.
                if (color != ColorPalette.DEFAULT) diamondColor = new WeakReference<>(color);
            }
            return style.withColor(color.textColor());
        }

        //? if >=1.19 {
        @Override
        public ComponentContents getContents() {
            return base.getContents();
        }

        @Override
        public List<Component> getSiblings() {
            return base.getSiblings();
        }

        @Override
        public FormattedCharSequence getVisualOrderText() {
            return base.getVisualOrderText();
        }
        //?}
    }
}
