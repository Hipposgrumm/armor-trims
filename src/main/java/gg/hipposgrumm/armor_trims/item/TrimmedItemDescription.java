package gg.hipposgrumm.armor_trims.item;

import com.mojang.blaze3d.platform.MacosUtil;
import com.mojang.logging.LogUtils;
import gg.hipposgrumm.armor_trims.trimming.TrimmableItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;

public class TrimmedItemDescription {
    @SubscribeEvent
    public static void addTrimLabel(ItemTooltipEvent event) {
        List<Component> list = event.getToolTip();
        ItemStack itemstack = event.getItemStack();
        int index=1;
        if (TrimmableItem.isTrimmed(itemstack)) {
            list.add(index, Component.translatable("tooltip.armor_trims.trim").withStyle(ChatFormatting.GRAY));
            TextColor color = TextColor.fromRgb(TrimmableItem.getMaterialColor(itemstack));
            MutableComponent trimName = Component.translatable("trims."+TrimmableItem.getTrim(itemstack).getNamespace()+"." + TrimmableItem.getTrim(itemstack).getPath());
            trimName.withStyle(trimName.getStyle().withColor(color));
            list.add(index+1, Component.literal(" ").append(trimName));
            MutableComponent materialName = Component.translatable(ForgeRegistries.ITEMS.getValue(TrimmableItem.getMaterial(itemstack)).getDescriptionId());
            materialName.withStyle(materialName.getStyle().withColor(color));
            list.add(index+2, Component.literal(" ").append(materialName));
        }
    }
}
