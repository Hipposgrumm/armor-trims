package dev.hipposgrumm.armor_trims.api.jei;

//? if fabric {
/*//? if >=1.17 {
import  me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
//?} else {
/^import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.TransferRecipeDisplay;
import me.shedaniel.rei.server.ContainerInfo;
^///?}
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ArmortrimsRecipeDisplay
        //? if >=1.17 {
        extends BasicDisplay
        //?} else {
        /^implements TransferRecipeDisplay
        ^///?}
{
    //? if >=1.17 {
    private final CategoryIdentifier<?> UID;

    public ArmortrimsRecipeDisplay(CategoryIdentifier<?> id, ArmortrimsRecipe recipe) {
        super(List.of(
                EntryIngredients.ofIngredient(recipe.baseInput()),
                EntryIngredients.ofIngredient(recipe.additionalInput()),
                EntryIngredients.ofIngredient(recipe.materialInput())
                ),
                Collections.singletonList(EntryIngredients.of(recipe.output()))
        );
        this.UID = id;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return UID;
    }
    //?} else {
    /^private final ResourceLocation UID;
    private final Ingredient base;
    private final Ingredient additional;
    private final Ingredient material;
    private final List<List<EntryStack>> inputs;
    private final List<List<EntryStack>> output;


    public ArmortrimsRecipeDisplay(ResourceLocation id, ArmortrimsRecipe recipe) {
        this.UID = id;
        this.base = recipe.baseInput();
        this.additional = recipe.additionalInput();
        this.material = recipe.materialInput();
        this.inputs = new ArrayList<>();
        inputs.add(Arrays.stream(base      .getItems()).map(EntryStack::create).collect(Collectors.toList()));
        inputs.add(Arrays.stream(additional.getItems()).map(EntryStack::create).collect(Collectors.toList()));
        inputs.add(Arrays.stream(material  .getItems()).map(EntryStack::create).collect(Collectors.toList()));
        this.output = Collections.singletonList(Collections.singletonList(EntryStack.create(recipe.output())));
    }

    @Override
    public @NotNull ResourceLocation getRecipeCategory() {
        return UID;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }


    @Override
    public @NotNull List<List<EntryStack>> getInputEntries() {
        return inputs;
    }

    @Override
    public @NotNull List<List<EntryStack>> getResultingEntries() {
        return output;
    }

    @Override
    public List<List<EntryStack>> getOrganisedInputEntries(ContainerInfo<AbstractContainerMenu> containerInfo, AbstractContainerMenu container) {
        return inputs;
    }
    ^///?}
}
*///?}