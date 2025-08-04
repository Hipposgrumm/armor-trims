package dev.hipposgrumm.armor_trims.gui;

import java.util.List;

import com.mojang.datafixers.util.Pair;
import dev.hipposgrumm.armor_trims.Armortrims;
import dev.hipposgrumm.armor_trims.api.item.ArmorTrimSmithingTemplate;
import dev.hipposgrumm.armor_trims.config.Config;
import dev.hipposgrumm.armor_trims.api.item.SmithingTemplate;
import dev.hipposgrumm.armor_trims.api.trimming.TrimGetter;
import dev.hipposgrumm.armor_trims.util.ArmortrimsInternalUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

public class SmithingMenuNew extends AbstractContainerMenu {
    public static final int INPUT_SLOT = 0;
    public static final int ADDITIONAL_SLOT = 1;
    public static final int RESULT_SLOT = 3;
    public static final int MATERIAL_SLOT = 2;
    private static final int INV_SLOT_START = 4;
    private static final int INV_SLOT_END = 31;
    private static final int USE_ROW_SLOT_START = 31;
    private static final int USE_ROW_SLOT_END = 40;

    protected final ResultContainer resultSlots = new ResultContainer();
    protected final Container inputSlots = new SimpleContainer(3) {
        public void setChanged() {
            super.setChanged();
            SmithingMenuNew.this.slotsChanged(this);
        }
    };
    protected final ContainerLevelAccess access;
    protected final Player player;
    private final Level level;

    public SmithingMenuNew(int id, Inventory inventory, ContainerLevelAccess access, Player player) {
        super(Armortrims.SMITHING_MENU_NEW.get(), id);
        this.level = inventory.player.level;
        this.access = access;
        this.player = inventory.player;
        this.addSlot(new Slot(this.inputSlots, INPUT_SLOT, 8, 48));
        this.addSlot(new Slot(this.inputSlots, ADDITIONAL_SLOT, 8, 66) {
            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(Armortrims.MODID,"item/empty_slot_smithing_template_armor_trim"));
            }

            @Override
            public boolean mayPlace(ItemStack item) {
                return item.getItem() instanceof SmithingTemplate;
            }
        });
        this.addSlot(new Slot(this.inputSlots, MATERIAL_SLOT, 52, 48));
        this.addSlot(new Slot(this.resultSlots, RESULT_SLOT, 106, 48) {
            public boolean mayPlace(ItemStack item) {
                    return false;
                }

            public boolean mayPickup(Player player) {
                return this.hasItem();
            }

            public /*? if >=1.18 {*/void/*?} else {*//*ItemStack*//*?}*/ onTake(Player player, ItemStack item) {
                SmithingMenuNew.this.onTake(player, item);
                //? if <1.18
                /*return item;*/
            }
        });
        addPlayerInventory(inventory);
    }

    //? if forge {
    /// @apiNote Used by Forge
    @ApiStatus.Internal
    public SmithingMenuNew(int i, Inventory inventory, FriendlyByteBuf friendlyByteBuf) {
        this(i, inventory, ContainerLevelAccess.NULL, inventory.player);
    }
    //?}

    public SmithingMenuNew(int i, Inventory inventory) {
        this(i, inventory, ContainerLevelAccess.NULL, inventory.player);
    }

    protected boolean isValidBlock(BlockState state) {
        return state.is(Blocks.SMITHING_TABLE);
    }

    public boolean stillValid(Player player) {
        return this.access.evaluate((level, pos) -> this.isValidBlock(level.getBlockState(pos)) && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    protected void onTake(Player player, ItemStack itemStack) {
        itemStack.onCraftedBy(player.level, player, itemStack.getCount());
        this.resultSlots.awardUsedRecipes(player);
        this.shrinkStackInSlot(INPUT_SLOT);
        this.shrinkStackInSlot(MATERIAL_SLOT);
        handleAdvancements(itemStack); // Check the advancement before shrinking the stack.
        if (!(Config.dontConsumeSmithingTemplates && this.slots.get(ADDITIONAL_SLOT).getItem().getItem() instanceof SmithingTemplate)) {
            this.shrinkStackInSlot(ADDITIONAL_SLOT);
        }
        this.access.execute((level, pos) -> level.levelEvent(1044, pos, 0));
    }

    private void handleAdvancements(ItemStack itemStack) {
        if (this.inputSlots.getItem(ADDITIONAL_SLOT).getItem() instanceof ArmorTrimSmithingTemplate && this.player instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) this.player;
            Advancement advancement = player.getLevel().getServer().getAdvancements().getAdvancement(new ResourceLocation(Armortrims.MODID,"trim_with_any_armor_pattern"));
            Advancement advancementChallenge = player.getLevel().getServer().getAdvancements().getAdvancement(new ResourceLocation(Armortrims.MODID,"trim_with_all_armor_patterns"));
            if (advancement != null && !player.getAdvancements().getOrStartProgress(advancement).isDone()) {
                player.getAdvancements().award(advancement, "code_triggered");
            }
            if (advancementChallenge != null) {
                ResourceLocation trim = TrimGetter.getPattern(itemStack);
                if (trim != null) {
                    CriterionProgress criteria = player.getAdvancements().getOrStartProgress(advancementChallenge).getCriterion("code_triggered_" + trim.getPath());
                    if (criteria != null && !criteria.isDone()) {
                        player.getAdvancements().award(advancementChallenge, "code_triggered_" + trim.getPath());
                    }
                }
            }
        }
    }

    private void shrinkStackInSlot(int slot) {
        ItemStack itemstack = this.inputSlots.getItem(slot);
        itemstack.shrink(1);
        this.inputSlots.setItem(slot, itemstack);
    }

    public void createResult() {
        ItemStack baseItem = this.inputSlots.getItem(INPUT_SLOT);
        ItemStack templateItem = this.inputSlots.getItem(ADDITIONAL_SLOT);
        ItemStack materialItem = this.inputSlots.getItem(MATERIAL_SLOT);
        if (templateItem.getItem() instanceof SmithingTemplate) {
            SmithingTemplate smithingTemplate = (SmithingTemplate) templateItem.getItem();
            this.resultSlots.setItem(0, smithingTemplate.getResult(baseItem, templateItem, materialItem, level));
        } else {
            Container vanillaRecipeContainer = new SimpleContainer(2);
            vanillaRecipeContainer.setItem(0, baseItem);
            vanillaRecipeContainer.setItem(1, materialItem);

            List<UpgradeRecipe> list = this.level.getRecipeManager().getRecipesFor(RecipeType.SMITHING, vanillaRecipeContainer, this.level);

            if (list.isEmpty() || (
                    Config.disableVanillaNetheriteUpgrade && materialItem/*? if <1.18.2 {*//*.getItem()*//*?}*/.is(ArmortrimsInternalUtils.NETHERITE_TAG)) // Special Override
            ) {
                this.resultSlots.setItem(0, ItemStack.EMPTY);
            } else {
                UpgradeRecipe selectedRecipe = list.get(0);
                ItemStack itemstack = selectedRecipe.assemble(vanillaRecipeContainer);
                this.resultSlots.setRecipeUsed(selectedRecipe);
                this.resultSlots.setItem(0, itemstack);
            }
        }
    }

    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        if (container == this.inputSlots) {
            this.createResult();
        }
    }

    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, pos) -> {
            this.clearContainer(player, /*? if <1.17 {*//*level,*//*?}*/ this.inputSlots);
        });
    }

    protected int determineSlotToMove(ItemStack item) {
        Container crafting = this.inputSlots;
        SmithingTemplate template = null;
        {
            Item t = crafting.getItem(ADDITIONAL_SLOT).getItem();
            if (t instanceof SmithingTemplate) {
                template = (SmithingTemplate) t;
            }
        }
        if (item.getItem() instanceof SmithingTemplate) {
            return ADDITIONAL_SLOT;
        } else if (template != null && template.materials().test(item)) {
            return MATERIAL_SLOT;
        } else if (crafting.getItem(INPUT_SLOT).isEmpty()) {
            return INPUT_SLOT;
        } else {
            return MATERIAL_SLOT;
        }
    }

    public boolean canTakeItemForPickAll(ItemStack item, Slot slotId) {
        return slotId.container != this.resultSlots && super.canTakeItemForPickAll(item, slotId);
    }

    public ItemStack quickMoveStack(Player player, int slotId) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotId);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (slotId == RESULT_SLOT) {
                if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (slotId != INPUT_SLOT && slotId != ADDITIONAL_SLOT && slotId != MATERIAL_SLOT) {
                if (slotId >= INV_SLOT_START && slotId < USE_ROW_SLOT_END) {
                    int i = determineSlotToMove(itemstack1);
                    if (!this.moveItemStackTo(itemstack1, i, inputSlots.getContainerSize(), false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemstack1);
        }
        return itemstack;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 97 + i * 18));
            }
        }
        addPlayerHotbar(playerInventory);
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 155));
        }
    }
}
