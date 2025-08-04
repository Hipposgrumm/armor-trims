package dev.hipposgrumm.armor_trims.api.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.hipposgrumm.armor_trims.gui.SmithingScreenNew;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
//? if <1.19
/*import net.minecraft.network.chat.TranslatableComponent;*/
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.Consumer;

//? if forge {
//? if <1.17 {
/*import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import mezz.jei.api.ingredients.IIngredients;
*///?}
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.world.item.crafting.Ingredient;
//? if >=1.17 {
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import org.jetbrains.annotations.ApiStatus;
//?}
//?} else {
/*import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
//? if >=1.17 {
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
//?} else {
/^import it.unimi.dsi.fastutil.ints.IntList;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.TransferRecipeCategory;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.EntryWidget;
import me.shedaniel.rei.gui.widget.Widget;
^///?}
*///?}

/// Abstracted Recipe Category for the Armor Trims UI
public class ArmortrimsRecipeCategory implements
        //? if forge {
        IRecipeCategory<ArmortrimsRecipe>
        //?} elif >=1.17 {
        /*DisplayCategory<ArmortrimsRecipeDisplay>
        *///?} else {
        /*TransferRecipeCategory<ArmortrimsRecipeDisplay>
        *///?}
{
    //? if fabric && >=1.17 {
    /*private final CategoryIdentifier<? extends ArmortrimsRecipeDisplay> UID;
    *///?} else {
    private final ResourceLocation UID;
    //?}
    //? if forge
    private final IDrawable background;
    private final Component title;
    //? if forge {
    private final IDrawable icon;
    //?} else {
    /*private final EntryStack/^? if >=17 {^//^<?>^//^?}^/ icon;
    *///?}

    /**
     *  Constructor with some parameters set.
     *  @param id    - UID of your Category
     *  @param title - Component used to name the Category
     *  @param item  - Item to use for the Icon
     */
    public ArmortrimsRecipeCategory(/*? if forge {*/IGuiHelper helper,/*?}*/ /*? if fabric && >=1.17 {*//*CategoryIdentifier<? extends ArmortrimsRecipeDisplay>*//*?} else {*/ResourceLocation/*?}*/ id, Component title, ItemStack item) {
        this.UID = id;
        //? if forge
        this.background = helper.createDrawable(SmithingScreenNew.GUI_SMITHING, 4, 4, 168, 85);
        this.title = title;
        //? if forge {
        this.icon = helper.createDrawableIngredient(/*? if >=1.17 {*/VanillaTypes.ITEM_STACK,/*?}*/ item);
        //?} else {
        /*this.icon = /^? if >=1.17 {^/EntryStacks.of/^?} else {^//^EntryStack.create^//^?}^/(item);
        *///?}
    }

    //? if forge {
    @SuppressWarnings("removal")
    public ResourceLocation getUid() {
        return UID;
    }
    @SuppressWarnings("removal")
    public Class<? extends ArmortrimsRecipe> getRecipeClass() {
        return ArmortrimsRecipe.class;
    }

    //? if >=1.17 {
    /// @apiNote No need to override this, override the methods above instead.
    @Override
    @ApiStatus.Internal
    public RecipeType<ArmortrimsRecipe> getRecipeType() {
        return new RecipeType<>(getUid(), getRecipeClass());
    }
    //?}
    //?} else {
    /*//? if >=1.17 {
    @Override
    public CategoryIdentifier<? extends ArmortrimsRecipeDisplay> getCategoryIdentifier() {
        return UID;
    }
    //?} else {
    /^@Override
    public ResourceLocation getIdentifier() {
        return UID;
    }
    ^///?}

    @Override
    public int getDisplayHeight() {
        return 90;
    }

    @Override
    public int getDisplayWidth(ArmortrimsRecipeDisplay display) {
        return 175;
    }
    *///?}

    //? if forge || >=1.17 {
    //? if <1.17
    /*public String getTitle() {return "";}*/

    @Override
    public Component /*? if >=1.17 {*/getTitle/*?} else {*//*getTitleAsTextComponent*//*?}*/() {
        return title;
    }
    //?} else {
    /*@Override
    public String getCategoryName() {
        if (title instanceof TranslatableComponent) {
            return Language.getInstance().getOrDefault(((TranslatableComponent) title).getKey());
        } else {
            return title.getContents();
        }
    }
    *///?}

    //? if forge {
    @Override
    public IDrawable getBackground() {
        return this.background;
    }
    //?}

    @Override
    public /*? if forge {*/IDrawable getIcon/*?} elif >=1.17 {*//*Renderer getIcon*//*?} else {*//*EntryStack getLogo*//*?}*/() {
        return this.icon;
    }

    //? if forge {
    private ArmorstandWidget preview;
    //? if <1.17
    /*private final Map<ArmortrimsRecipe, Map<Integer, ? extends IGuiIngredient<ItemStack>>> resultSlots = new HashMap<>();*/

    /// Set your recipe data here. Don't forget to call super (unless you don't want to render the armorstand).
    @Override
    public void setRecipe(/*? if >=1.17 {*/IRecipeLayoutBuilder/*?} else {*//*IRecipeLayout*//*?}*/ builder, ArmortrimsRecipe recipe, /*? if >=1.17 {*/IFocusGroup/*?} else {*//*IIngredients*//*?}*/ focuses) {
        preview = new ArmorstandWidget(145, 75, 30, null);

        //? if >=1.17
        IRecipeSlotBuilder base =
                SmithingSlot.BASE.make(builder);
        //? if >=1.17
        IRecipeSlotBuilder additional =
                SmithingSlot.ADDITIONAL.make(builder);
        //? if >=1.17
        IRecipeSlotBuilder material =
                SmithingSlot.MATERIAL.make(builder);
        //? if >=1.17
        IRecipeSlotBuilder output =
                SmithingSlot.OUTPUT.make(builder);

        //? if >=1.17 {
        if (!recipe.baseInput().isEmpty()) base.addIngredients(recipe.baseInput());
        if (!recipe.additionalInput().isEmpty()) additional.addIngredients(recipe.additionalInput());
        if (!recipe.materialInput().isEmpty()) material.addIngredients(recipe.materialInput());
        if (!recipe.output().isEmpty()) output.addItemStack(recipe.output());

        onSlotsCreated(base, additional, material, output);
        //?} else {
        /*builder.getItemStacks().set(SmithingSlot.BASE.index, Arrays.asList(recipe.baseInput().getItems()));
        builder.getItemStacks().set(SmithingSlot.ADDITIONAL.index, Arrays.asList(recipe.additionalInput().getItems()));
        builder.getItemStacks().set(SmithingSlot.MATERIAL.index, Arrays.asList(recipe.materialInput().getItems()));
        builder.getItemStacks().set(SmithingSlot.OUTPUT.index, recipe.output());

        this.resultSlots.put(recipe, builder.getItemStacks().getGuiIngredients());
        *///?}
    }

    //? if <1.17 {
    /*@Override
    public void setIngredients(ArmortrimsRecipe recipe, IIngredients ingredients) {
        List<Ingredient> ing = new ArrayList<>();
        ing.add(recipe.baseInput());
        ing.add(recipe.additionalInput());
        ing.add(recipe.materialInput());
        ingredients.setInputIngredients(ing);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.output());
    }
    *///?}

    /// If overriding, don't forget to call super (unless you don't want the armorstand)
    public void draw(ArmortrimsRecipe recipe, /*? if >=1.17 {*/IRecipeSlotsView recipeSlotsView,/*?}*/ PoseStack poseStack, double mouseX, double mouseY) {
        //? if >=1.17 {
        IRecipeSlotView slot = recipeSlotsView.findSlotByName("output").orElse(null);
        //?} else {
        /*IGuiIngredient<ItemStack> slot = resultSlots.get(recipe).get(SmithingSlot.OUTPUT.index);
        *///?}
        preview.update(slot);
        preview.render(poseStack);
    }

    //?} else {
    /*/^*
     * {@inheritDoc}
     * @param bounds {@inheritDoc}
     ^/
    @Override
    public List<Widget> setupDisplay(ArmortrimsRecipeDisplay display, Rectangle bounds) {
        Slot base       = (Slot) SmithingSlot.BASE      .make(bounds).entries(display.getInputEntries().get(0));
        Slot additional = (Slot) SmithingSlot.ADDITIONAL.make(bounds).entries(display.getInputEntries().get(1));
        Slot material   = (Slot) SmithingSlot.MATERIAL  .make(bounds).entries(display.getInputEntries().get(2));
        Slot out        = (Slot) SmithingSlot.OUTPUT    .make(bounds).entries(display./^? if >=1.17 {^/getOutputEntries/^?} else {^//^getResultingEntries^//^?}^/().get(0));
        onSlotsCreated(base, additional, material, out);

        List<Widget> widgets = new LinkedList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(SmithingScreenNew.GUI_SMITHING, new Rectangle(bounds.getMinX(), bounds.getMinY(), 170, 82)));
        widgets.add(new ArmorstandWidget(bounds.getMinX()+145, bounds.getMinY()+75, 30, out));
        widgets.add(base);
        widgets.add(additional);
        widgets.add(material);
        widgets.add(out);

        return widgets;
    }
    *///?}

    //? if fabric || >=1.17 {
    /**
     * Use to assign listeners and such to slots.
     * @param base       - Base Item (eg Armor)
     * @param additional - Additional Item (usually Smithing Template)
     * @param material   - Material Slot
     * @param out        - Output Slot
     */
    //? if fabric {
    /*protected void onSlotsCreated(Slot base, Slot additional, Slot material, Slot out) {
    *///?} else {
    protected void onSlotsCreated(IRecipeSlotBuilder base, IRecipeSlotBuilder additional, IRecipeSlotBuilder material, IRecipeSlotBuilder out) {
    //?}

    }
    //?}

    //? if fabric && <1.17 {
    /*@Override
    public void renderRedSlots(PoseStack matrices, List<Widget> widgets, Rectangle bounds, ArmortrimsRecipeDisplay display, IntList redSlots) {}
    *///?}

    /// Abstraction enum for creating the slots
    protected enum SmithingSlot {
        BASE      (0, false, 4,   44),
        ADDITIONAL(1, false, 4,   62),
        MATERIAL  (2, false, 48,  44),
        OUTPUT    (3, true,  102, 44);

        public final int index;
        public final boolean isOut;
        public final int x;
        public final int y;

        SmithingSlot(int index, boolean isOut, int x, int y) {
            this.index = index;
            this.isOut = isOut;
            this.x = x;
            this.y = y;
        }

        /// Create a slot of the predetermined type in the predetermined position
        //? if forge {
        //? if >=1.17 {
        public IRecipeSlotBuilder make(IRecipeLayoutBuilder builder) {
            IRecipeSlotBuilder slot = builder.addSlot(isOut ? RecipeIngredientRole.OUTPUT : RecipeIngredientRole.INPUT, x, y);
            if (this == OUTPUT) slot.setSlotName("output");
            return slot;
        }
        //?} else {
        /*public void make(IRecipeLayout builder) {
            builder.getItemStacks().init(index, !isOut, x-1, y-1);
        }
        *///?}
        //?} else {
        /*public Slot make(Rectangle bounds) {
            Slot slot =
                    //? if >=1.17 {
                    Widgets.createSlot
                    //?} else {
                    /^new Slot
                    ^///?}
                    (new Point(bounds.getMinX()+4+x, bounds.getMinY()+4+y));
            if (isOut) {
                slot.markOutput();
            } else {
                slot.markInput();
            }
            return slot;
        }
        *///?}
    }

    //? if fabric && <1.17 {
    /*// Extension of REI5 Slot class to add needed functionality.
    public static class Slot extends EntryWidget {
        protected List<Consumer<Slot>> listeners = new ArrayList<>();
        private EntryStack last;

        public Slot(Point point) {
            super(point);
        }

        // Make Public
        @Override
        /// Get the current item in this slot.
        public EntryStack getCurrentEntry() {
            return super.getCurrentEntry();
        }

        @Override
        protected void drawCurrentEntry(PoseStack poseStack, int mouseX, int mouseY, float deltaTime) {
            EntryStack curr = getCurrentEntry();
            if (curr != last) {
                for (Consumer<Slot> l:listeners) l.accept(this);
                last = curr;
            }
            super.drawCurrentEntry(poseStack, mouseX, mouseY, deltaTime);
        }

        /^*
         * Add a listener when the slot cycles to a different item.
         * @param listener - Listener function for when the slot is updated.
         ^/
        public void withEntriesListener(Consumer<Slot> listener) {
            listeners.add(listener);
        }
    }
    *///?}
}