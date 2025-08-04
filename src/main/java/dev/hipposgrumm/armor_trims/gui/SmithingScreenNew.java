package dev.hipposgrumm.armor_trims.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.hipposgrumm.armor_trims.Armortrims;
import dev.hipposgrumm.armor_trims.api.item.SmithingTemplate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventory;

public class SmithingScreenNew extends AbstractContainerScreen<SmithingMenuNew> implements ContainerListener {
    public static final ResourceLocation GUI_SMITHING = new ResourceLocation(Armortrims.MODID, "textures/gui/container/smithing_new.png");

    private LivingEntity preview;

    public SmithingScreenNew(SmithingMenuNew menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.titleLabelX = 60;
        this.titleLabelY = 18;
        this.imageHeight = 179;
        this.inventoryLabelY = this.imageHeight - 93;
    }

    protected void init() {
        super.init();
        this.menu.addSlotListener(this);
        this.preview = createPreview();
    }

    public static LivingEntity createPreview() {
        if (Minecraft.getInstance().level == null) return null;
        LivingEntity preview = new ArmorStand(Minecraft.getInstance().level, 0, 0, 0);
        preview.setNoGravity(true);
        CompoundTag extraNBT = new CompoundTag();
        extraNBT.putBoolean("ShowArms", true);
        extraNBT.putBoolean("NoBasePlate", true);
        preview.load(extraNBT);
        return preview;
    }

    public void removed() {
        super.removed();
        this.menu.removeSlotListener(this);
    }

    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, delta);
        RenderSystem.disableBlend();
        this.renderTooltip(poseStack, mouseX, mouseY);
    }

    protected void renderBg(PoseStack poseStack, float delta, int mouseX, int mouseY) {
        //? if >=1.17 {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, SmithingScreenNew.this.GUI_SMITHING);
        //?} else {
        /*RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(GUI_SMITHING);
        *///?}
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.blit(poseStack, x, y, 0, 0, this.imageWidth, this.imageHeight);
        if (determineCraftingIncomplete()) {
            this.blit(poseStack, x + 72, y + 45, this.imageWidth, 0, 28, 21);
        }
        renderEntityInInventory(x + 145, y + 75, 30, 50, -50, preview);
    }

    private boolean determineCraftingIncomplete() {
        if (this.menu.getSlot(0).hasItem() && !this.menu.getSlot(1).hasItem()) {
            return true;
        } else if (!this.menu.getSlot(0).hasItem() && this.menu.getSlot(1).hasItem()) {
            return true;
        } else if (this.menu.getSlot(1).getItem().getItem() instanceof SmithingTemplate && (!this.menu.getSlot(0).hasItem() || !this.menu.getSlot(2).hasItem())) {
            return true;
        } else if (this.menu.getSlot(0).hasItem() && this.menu.getSlot(1).hasItem() && !this.menu.getSlot(3).hasItem()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        if (preview != null) {
            preview.remove(/*? if >=1.18 {*/Entity.RemovalReason.DISCARDED/*?}*/);
        }
    }

    @Override
    //? if >=1.18 {
    public void dataChanged(AbstractContainerMenu menu, int index, int value) {}
    //?} else {
    /*public void setContainerData(AbstractContainerMenu menu, int index, int value) {}

    @Override
    public void refreshContainer(AbstractContainerMenu menu, NonNullList<ItemStack> nonNullList) {}
    *///?}

    @Override
    public void slotChanged(AbstractContainerMenu menu, int index, ItemStack stack) {
        if (index==3) updatePreview(this.preview,stack);
    }

    private static final EquipmentSlot[] previewSlots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.OFFHAND};
    public static void updatePreview(@Nullable LivingEntity preview, ItemStack item) {
        if (preview != null) {
            CompoundTag data = new CompoundTag();
            for (EquipmentSlot equipment : previewSlots) {
                preview.setItemSlot(equipment, ItemStack.EMPTY);
            }

            if (!item.isEmpty()) {
                EquipmentSlot slot = /*? if >=1.17 {*/LivingEntity/*?} else {*//*Mob*//*?}*/.getEquipmentSlotForItem(item);
                if (slot == EquipmentSlot.MAINHAND) slot = EquipmentSlot.OFFHAND;
                data.put("LeftArm", new Rotations(slot == EquipmentSlot.OFFHAND ? -50.0f : 0.0f, 50.0f, 0.0f).save());
                preview.setItemSlot(slot, item);
            }

            data.putBoolean("ShowArms", true);
            data.putBoolean("NoBasePlate", true);
            preview.load(data);
        }
    }
}