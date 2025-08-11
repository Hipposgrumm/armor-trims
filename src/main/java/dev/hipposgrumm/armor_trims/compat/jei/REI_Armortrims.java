package dev.hipposgrumm.armor_trims.compat.jei;

//? if fabric {
/*import dev.hipposgrumm.armor_trims.Armortrims;
import dev.hipposgrumm.armor_trims.api.TrimRegistry;
import dev.hipposgrumm.armor_trims.api.item.ArmorTrimSmithingTemplate;
import dev.hipposgrumm.armor_trims.api.jei.ArmortrimsRecipe;
import dev.hipposgrumm.armor_trims.api.jei.ArmortrimsRecipeCategory;
import dev.hipposgrumm.armor_trims.api.jei.ArmortrimsRecipeDisplay;
import dev.hipposgrumm.armor_trims.api.trimming.TrimGetter;
import dev.hipposgrumm.armor_trims.config.Config;
//? <1.19
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

//? if >=1.17 {
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
//?} else {
/^import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
^///?}

public class REI_Armortrims implements
        //? if >=1.17 {
        REIClientPlugin
        //?} else {
        /^REIPluginV0
        ^///?}
{
    public static final /^? if >=1.17 {^/CategoryIdentifier<ArmortrimsRecipeDisplay>/^?} else {^//^ResourceLocation^//^?}^/ TRIMMING = /^? if >=1.17 {^/CategoryIdentifier.of/^?} else {^//^new ResourceLocation^//^?}^/(Armortrims.MODID, "armor_trimming");
    public static final /^? if >=1.17 {^/CategoryIdentifier<ArmortrimsRecipeDisplay>/^?} else {^//^ResourceLocation^//^?}^/ UPGRADING = /^? if >=1.17 {^/CategoryIdentifier.of/^?} else {^//^new ResourceLocation^//^?}^/(Armortrims.MODID, "item_upgrading");

    //? if <1.17 {
    /^@Override
    public ResourceLocation getPluginIdentifier() {
        return new ResourceLocation(Armortrims.MODID, "armortrims");
    }
    ^///?}

    @Override
    //? if >=1.17 {
    public void registerCategories(CategoryRegistry registry) {
    //?} else {
    /^public void registerPluginCategories(RecipeHelper registry) {
    ^///?}
        if (Config.enableJei) {
            registry./^? if >=1.17 {^/add/^?} else {^//^registerCategory^//^?}^/(new ArmortrimsRecipeCategory(TRIMMING, /^? if >=1.19 {^//^Component.translatable^//^?} else {^/new TranslatableComponent/^?}^/("jei.armor_trimming"), TrimGetter.applyTrim(new ItemStack(Items.IRON_CHESTPLATE), TrimRegistry.defaultTrim(), new ItemStack(Items.EMERALD))) {
                @Override
                protected void onSlotsCreated(Slot base, Slot additional, Slot materials, Slot out) {
                    out.clearEntries();
                    if (valid(base, additional, materials))
                        out.entry(/^? if >=1.17 {^/EntryStacks.of/^?} else {^//^EntryStack.create^//^?}^/(
                                TrimGetter.applyTrim(
                                        (ItemStack) base.getCurrentEntry()./^? if >=1.17 {^/getValue/^?} else {^//^getObject^//^?}^/(),
                                        ((ArmorTrimSmithingTemplate) ((ItemStack) additional.getCurrentEntry()./^? if >=1.17 {^/getValue/^?} else {^//^getObject^//^?}^/()).getItem()).trim(),
                                        (ItemStack) materials.getCurrentEntry()./^? if >=1.17 {^/getValue/^?} else {^//^getObject^//^?}^/())
                        ));

                    materials.withEntriesListener(material -> {
                        out.clearEntries();
                        if (valid(base, additional, material))
                            out.entry(/^? if >=1.17 {^/EntryStacks.of/^?} else {^//^EntryStack.create^//^?}^/(
                                    TrimGetter.applyTrim(
                                            (ItemStack) base.getCurrentEntry()./^? if >=1.17 {^/getValue/^?} else {^//^getObject^//^?}^/(),
                                            ((ArmorTrimSmithingTemplate) ((ItemStack) additional.getCurrentEntry()./^? if >=1.17 {^/getValue/^?} else {^//^getObject^//^?}^/()).getItem()).trim(),
                                            (ItemStack) material.getCurrentEntry()./^? if >=1.17 {^/getValue/^?} else {^//^getObject^//^?}^/()
                                    )
                            ));
                    });
                }

                boolean valid(Slot base, Slot additional, Slot material) {
                    Object a = additional.getCurrentEntry()./^? if >=1.17 {^/getValue/^?} else {^//^getObject^//^?}^/();
                    if (base.getCurrentEntry()./^? if >=1.17 {^/getValue/^?} else {^//^getObject^//^?}^/() instanceof ItemStack &&
                            a instanceof ItemStack &&
                            material.getCurrentEntry()./^? if >=1.17 {^/getValue/^?} else {^//^getObject^//^?}^/() instanceof ItemStack) {
                        ItemStack add = (ItemStack) a;
                        return add.getItem() instanceof ArmorTrimSmithingTemplate;
                    }
                    return false;
                }
            });
            registry./^? if >=1.17 {^/add/^?} else {^//^registerCategory^//^?}^/(new ArmortrimsRecipeCategory(UPGRADING, /^? if >=1.19 {^//^Component.translatable^//^?} else {^/new TranslatableComponent/^?}^/("jei.item_upgrading"), Items.NETHERITE_CHESTPLATE.getDefaultInstance()));
    //? if <1.17 {
        /^}
    }
    @Override
    public void registerOthers(RecipeHelper registry) {
        if (Config.enableJei) {
    ^///?}
            registry./^? if >=1.17 {^/addWorkstations/^?} else {^//^registerWorkingStations^//^?}^/(TRIMMING, /^? if >=1.17 {^/EntryStacks.of/^?} else {^//^EntryStack.create^//^?}^/(Items.SMITHING_TABLE));
            registry./^? if >=1.17 {^/addWorkstations/^?} else {^//^registerWorkingStations^//^?}^/(UPGRADING, /^? if >=1.17 {^/EntryStacks.of/^?} else {^//^EntryStack.create^//^?}^/(Items.SMITHING_TABLE));
        }
    }



    @Override
    //? if >=1.17 {
    public void registerDisplays(DisplayRegistry registry) {
    //?} else {
    /^public void registerRecipeDisplays(RecipeHelper registry) {
    ^///?}
        if (Config.enableJei) {
            for (ArmortrimsRecipe recipe:ItemTrimRecipeMaker.getTrimmingRecipes()) {
                registry./^? if >=1.17 {^/add/^?} else {^//^registerDisplay^//^?}^/(new ArmortrimsRecipeDisplay(TRIMMING, recipe));
            }
            for (ArmortrimsRecipe recipe:ItemUpgradeRecipeMaker.getUpgradingRecipes()) {
                registry./^? if >=1.17 {^/add/^?} else {^//^registerDisplay^//^?}^/(new ArmortrimsRecipeDisplay(UPGRADING, recipe));
            }
        }
    }

    /^@Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        registry.register(SimpleTransferHandler.create(SmithingMenuNew.class, TRIMMING, new SimpleTransferHandler.IntRange(0, 2)));
    }^/
}
*///?}