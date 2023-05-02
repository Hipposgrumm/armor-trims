package gg.hipposgrumm.armor_trims.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import gg.hipposgrumm.armor_trims.Armortrims;
import gg.hipposgrumm.armor_trims.item.SmithingTemplate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

import static net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventory;

@OnlyIn(Dist.CLIENT)
public class SmithingScreenNew extends AbstractContainerScreen<SmithingMenuNew> implements ContainerListener {
    public static final ResourceLocation SMITHING = new ResourceLocation(Armortrims.MODID, "textures/gui/container/smithing_new.png");
    public static final ResourceLocation SMITHING_CLEAN = new ResourceLocation(Armortrims.MODID, "textures/gui/container/smithing_new_clean.png");

    private ResourceLocation GUI_SMITHING;
    private LivingEntity preview;

    public SmithingScreenNew(SmithingMenuNew p_99290_, Inventory p_99291_, Component p_99292_) {
        super(p_99290_, p_99291_, p_99292_);
        this.titleLabelX = 60;
        this.titleLabelY = 18;
        this.imageHeight = 179;
        this.inventoryLabelY = this.imageHeight - 93;
    }

    protected void subInit() {}

    protected void init() {
        super.init();
        this.subInit();
        this.menu.addSlotListener(this);
        GUI_SMITHING = SMITHING;
        spawnPreview();
    }

    private void spawnPreview() {
        if (Minecraft.getInstance().level == null) return;
        preview = new ArmorStand(Minecraft.getInstance().level, 0, 0, 0);
        preview.setNoGravity(true);
        CompoundTag extraNBT = new CompoundTag();
        extraNBT.putBoolean("ShowArms", true);
        extraNBT.putBoolean("NoBasePlate", true);
        preview.load(extraNBT);
    }

    public void removed() {
        super.removed();
        this.menu.removeSlotListener(this);
    }

    protected void renderLabels(PoseStack p_99294_, int p_99295_, int p_99296_) {
        RenderSystem.disableBlend();
        super.renderLabels(p_99294_, p_99295_, p_99296_);
    }

    public void render(PoseStack p_98922_, int p_98923_, int p_98924_, float p_98925_) {
        this.renderBackground(p_98922_);
        super.render(p_98922_, p_98923_, p_98924_, p_98925_);
        RenderSystem.disableBlend();
        this.renderFg(p_98922_, p_98923_, p_98924_, p_98925_);
        this.renderTooltip(p_98922_, p_98923_, p_98924_);
    }

    protected void renderFg(PoseStack p_98927_, int p_98928_, int p_98929_, float p_98930_) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, SmithingScreenNew.this.GUI_SMITHING);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        if (!(this.menu.getSlot(1).hasItem() && this.menu.getSlot(1).getItem().getItem() instanceof SmithingTemplate)) {
            this.blit(p_98927_, i + 8, j + 65, 8, 67, 18, 18);
        }
    }

    protected void renderBg(PoseStack p_98917_, float p_98918_, int p_98919_, int p_98920_) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, SmithingScreenNew.this.GUI_SMITHING);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(p_98917_, i, j, 0, 0, this.imageWidth, this.imageHeight);
        if (determineCraftingIncomplete()) {
            this.blit(p_98917_, i + 72, j + 45, this.imageWidth, 0, 28, 21);
        }
        if (this.menu.getSlot(1).hasItem() && this.menu.getSlot(1).getItem().getItem() instanceof SmithingTemplate) {
            this.blit(p_98917_, i + 7, j + 65, 0, this.imageHeight, 18, 18);
        }
        renderEntityInInventory(i + 145, j + 75, 30, 50, -50, preview);
    }

    private boolean determineCraftingIncomplete() {
        if (this.menu.getSlot(0).hasItem() && !this.menu.getSlot(1).hasItem()) {
            return true;
        } else if (!this.menu.getSlot(0).hasItem() && this.menu.getSlot(1).hasItem()) {
            return true;
        } else if (this.menu.getSlot(1).getItem().getItem() instanceof SmithingTemplate && (!this.menu.getSlot(0).hasItem() || !this.menu.getSlot(2).hasItem())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        if (preview != null) {
            preview.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    public void dataChanged(AbstractContainerMenu p_169759_, int p_169760_, int p_169761_) {}
    public void slotChanged(AbstractContainerMenu p_98910_, int p_98911_, ItemStack p_98912_) {
        if (p_98911_ == 3) {
            try {
                CompoundTag extraNBT = new CompoundTag();
                EquipmentSlot equipmentSlot = preview.getEquipmentSlotForItem(p_98912_);
                EquipmentSlot[] slot = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.OFFHAND};
                for (EquipmentSlot equiptment : slot) {
                    preview.setItemSlot(equiptment, ItemStack.EMPTY);
                }
                if (equipmentSlot == EquipmentSlot.MAINHAND) {
                    equipmentSlot = EquipmentSlot.OFFHAND;
                }
                if (equipmentSlot == EquipmentSlot.OFFHAND) {
                    extraNBT.put("LeftArm", new Rotations(-50.0f, 50.0f, 0.0f).save());
                } else {
                    extraNBT.put("LeftArm", new Rotations(0.0f, 0.0f, 0.0f).save());
                }
                preview.setItemSlot(preview.getEquipmentSlotForItem(p_98912_), p_98912_);
                extraNBT.putBoolean("ShowArms", true);
                extraNBT.putBoolean("NoBasePlate", true);
                preview.load(extraNBT);
            } catch (NullPointerException ignored) {}
        }
    }
}