package gg.hipposgrumm.armor_trims.gui;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;

import com.mojang.logging.LogUtils;
import gg.hipposgrumm.armor_trims.Armortrims;
import gg.hipposgrumm.armor_trims.config.Config;
import gg.hipposgrumm.armor_trims.item.SmithingTemplate;
import gg.hipposgrumm.armor_trims.trimming.TrimmableItem;
import gg.hipposgrumm.armor_trims.trimming.Trims;
import gg.hipposgrumm.armor_trims.util.LargeItemLists;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
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
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;

public class SmithingMenuNew extends AbstractContainerMenu {
    private static final Logger LOGGER = LogUtils.getLogger();

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
    @Nullable
    private UpgradeRecipe selectedRecipe;
    private final List<UpgradeRecipe> recipes;

    public SmithingMenuNew(int p_40248_, Inventory p_40249_, ContainerLevelAccess access, @Nullable Player player) {
        super(Armortrims.SMITHING_MENU_NEW.get(), p_40248_);
        this.level = p_40249_.player.level;
        this.access = access;
        this.player = p_40249_.player;
        this.addSlot(new Slot(this.inputSlots, INPUT_SLOT, 8, 48));
        this.addSlot(new Slot(this.inputSlots, ADDITIONAL_SLOT, 52, 48));
        this.addSlot(new Slot(this.inputSlots, MATERIAL_SLOT, 8, 66) {
            @Override
            public boolean mayPlace(ItemStack p_40231_) {
                return this.container.getItem(ADDITIONAL_SLOT).getItem() instanceof SmithingTemplate;
            }
        });
        this.addSlot(new Slot(this.resultSlots, RESULT_SLOT, 106, 48) {
            public boolean mayPlace(ItemStack p_39818_) {
                    return false;
                }

            public boolean mayPickup(Player p_39813_) {
                return SmithingMenuNew.this.mayPickup(p_39813_, this.hasItem());
            }

            public void onTake(Player p_150604_, ItemStack p_150605_) {
                SmithingMenuNew.this.onTake(p_150604_, p_150605_);
            }
        });
        addPlayerInventory(p_40249_);
        this.recipes = this.level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING);
    }

    public SmithingMenuNew(int i, Inventory inventory, @Nullable FriendlyByteBuf friendlyByteBuf) {
        this(i,inventory,ContainerLevelAccess.NULL, inventory.player);
    }

    protected boolean isValidBlock(BlockState p_40266_) {
        return p_40266_.is(Blocks.SMITHING_TABLE);
    }

    public boolean stillValid(Player p_39780_) {
        return this.access.evaluate((p_39785_, p_39786_) -> {
            return !this.isValidBlock(p_39785_.getBlockState(p_39786_)) ? false : p_39780_.distanceToSqr((double)p_39786_.getX() + 0.5D, (double)p_39786_.getY() + 0.5D, (double)p_39786_.getZ() + 0.5D) <= 64.0D;
        }, true);
    }

    protected boolean mayPickup(Player p_40268_, boolean p_40269_) {
        return true;
    }

    protected void onTake(Player p_150663_, ItemStack p_150664_) {
        p_150664_.onCraftedBy(p_150663_.level, p_150663_, p_150664_.getCount());
        boolean wasTrim = this.inputSlots.getItem(ADDITIONAL_SLOT).getItem() instanceof SmithingTemplate template && template.getTrim() != Trims.NETHERITE_UPGRADE;
        this.resultSlots.awardUsedRecipes(p_150663_);
        this.shrinkStackInSlot(INPUT_SLOT);
        this.shrinkStackInSlot(MATERIAL_SLOT);
        if (!(Config.dontConsumeSmithingTemplates() && this.slots.get(ADDITIONAL_SLOT).getItem().getItem() instanceof SmithingTemplate)) {
            this.shrinkStackInSlot(ADDITIONAL_SLOT);
        }
        this.access.execute((p_40263_, p_40264_) -> {
            p_40263_.levelEvent(1044, p_40264_, 0);
        });
        if (wasTrim && p_150663_ instanceof ServerPlayer sPlayer) {
            Advancement advancement = sPlayer.getLevel().getServer().getAdvancements().getAdvancement(new ResourceLocation("armor_trims:trim_with_any_armor_pattern"));
            Advancement advancementChallenge = sPlayer.getLevel().getServer().getAdvancements().getAdvancement(new ResourceLocation("armor_trims:trim_with_all_armor_patterns"));
            if (advancement != null && !sPlayer.getAdvancements().getOrStartProgress(advancement).isDone()) {
                sPlayer.getAdvancements().award(advancement, "code_triggered");
            }
            try {
                if (advancementChallenge != null && !Objects.requireNonNull(sPlayer.getAdvancements().getOrStartProgress(advancementChallenge).getCriterion("code_triggered_" + TrimmableItem.getTrim(p_150664_))).isDone()) {
                    sPlayer.getAdvancements().award(advancementChallenge, "code_triggered_" + TrimmableItem.getTrim(p_150664_));
                }
            } catch (NullPointerException ignored) {}
        }
    }

    private void shrinkStackInSlot(int p_40271_) {
        ItemStack itemstack = this.inputSlots.getItem(p_40271_);
        itemstack.shrink(1);
        this.inputSlots.setItem(p_40271_, itemstack);
    }

    public void createResult() {
        ItemStack baseItem = this.inputSlots.getItem(INPUT_SLOT);
        ItemStack upgradeItem = this.inputSlots.getItem(ADDITIONAL_SLOT);
        ItemStack materialItem = this.inputSlots.getItem(MATERIAL_SLOT);
        if (upgradeItem.getItem() instanceof SmithingTemplate templateItem) {
            if (templateItem.getTrim() == Trims.NETHERITE_UPGRADE && materialItem.is(ItemTags.create(new ResourceLocation("forge:ingots/netherite")))) {
                Container vanillaRecipeContainer = new SimpleContainer(2);
                vanillaRecipeContainer.setItem(0, baseItem);
                vanillaRecipeContainer.setItem(1, materialItem);
                List<UpgradeRecipe> list = this.level.getRecipeManager().getRecipesFor(RecipeType.SMITHING, vanillaRecipeContainer, this.level);
                if (list.isEmpty()) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                } else {
                    this.selectedRecipe = list.get(0);
                    ItemStack itemstack = this.selectedRecipe.assemble(vanillaRecipeContainer);
                    this.resultSlots.setRecipeUsed(this.selectedRecipe);
                    this.resultSlots.setItem(0, itemstack);
                }
            } else {
                this.resultSlots.setItem(0, LargeItemLists.getAllMaterials().contains(materialItem.getItem()) ? TrimmableItem.applyTrim(baseItem, templateItem.getTrim(), materialItem) : ItemStack.EMPTY);
            }
        } else {
            Container vanillaRecipeContainer = new SimpleContainer(2);
            vanillaRecipeContainer.setItem(0, baseItem);
            vanillaRecipeContainer.setItem(1, upgradeItem);
            List<UpgradeRecipe> list = this.level.getRecipeManager().getRecipesFor(RecipeType.SMITHING, vanillaRecipeContainer, this.level);
            if (list.isEmpty()) {
                this.resultSlots.setItem(0, ItemStack.EMPTY);
            } else {
                this.selectedRecipe = list.get(0);
                ItemStack itemstack = this.selectedRecipe.assemble(vanillaRecipeContainer);
                this.resultSlots.setRecipeUsed(this.selectedRecipe);
                this.resultSlots.setItem(0, itemstack);
            }
        }
    }

    public void slotsChanged(Container p_39778_) {
        super.slotsChanged(p_39778_);
        if (p_39778_ == this.inputSlots) {
            this.createResult();
        }
        if (!(p_39778_.getItem(ADDITIONAL_SLOT).getItem() instanceof SmithingTemplate)) {
            if (player.getInventory().getSlotWithRemainingSpace(p_39778_.getItem(MATERIAL_SLOT).copy()) == -1) {
                player.drop(p_39778_.getItem(MATERIAL_SLOT), true);
            }
            this.quickMoveStack(player, MATERIAL_SLOT);
        }
    }

    public void removed(Player p_39790_) {
        super.removed(p_39790_);
        this.access.execute((p_39796_, p_39797_) -> {
            this.clearContainer(p_39790_, this.inputSlots);
        });
    }

    protected int determineSlotToMove(ItemStack item) {
        Container crafting = this.inputSlots;
        if (item.getItem() instanceof SmithingTemplate && crafting.getItem(ADDITIONAL_SLOT).isEmpty()) {
            return ADDITIONAL_SLOT;
        } else if (crafting.getItem(INPUT_SLOT).isEmpty()) {
            return INPUT_SLOT;
        } else if (crafting.getItem(ADDITIONAL_SLOT).isEmpty()) {
            return ADDITIONAL_SLOT;
        } else {
            return MATERIAL_SLOT;
        }
    }

    public boolean canTakeItemForPickAll(ItemStack p_40257_, Slot p_40258_) {
        return p_40258_.container != this.resultSlots && super.canTakeItemForPickAll(p_40257_, p_40258_);
    }

    public ItemStack quickMoveStack(Player p_39792_, int p_39793_) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(p_39793_);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (p_39793_ == RESULT_SLOT) {
                if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (p_39793_ != INPUT_SLOT && p_39793_ != ADDITIONAL_SLOT && p_39793_ != MATERIAL_SLOT) {
                if (p_39793_ >= INV_SLOT_START && p_39793_ < USE_ROW_SLOT_END) {
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
            slot.onTake(p_39792_, itemstack1);
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
