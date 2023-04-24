package gg.hipposgrumm.armor_trims.mixin;

import com.mojang.logging.LogUtils;
import gg.hipposgrumm.armor_trims.trimming.TrimmableItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;

public class TrimmedItemDescription {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void addTrimLabel(ItemTooltipEvent event) {
        List<Component> list = event.getToolTip();
        ItemStack itemstack = event.getItemStack();
        int index=1;
        if (TrimmableItem.isTrimmed(itemstack)) {
            list.add(index, new TranslatableComponent("tooltip.armor_trims.trim").withStyle(ChatFormatting.GRAY));
            TextColor color = TextColor.fromRgb(TrimmableItem.getMaterialColor(itemstack));
            TranslatableComponent trimName = new TranslatableComponent("trims.armor_trims." + TrimmableItem.getTrim(itemstack));
            trimName.withStyle(trimName.getStyle().withColor(color));
            list.add(index+1, new TextComponent(" ").append(trimName.getContents().startsWith("trims.armor_trims.")?new TranslatableComponent("trims.armor_trims.unknown").withStyle(ChatFormatting.ITALIC, ChatFormatting.UNDERLINE):trimName));
            TranslatableComponent materialName = new TranslatableComponent(ForgeRegistries.ITEMS.getValue(TrimmableItem.getMaterial(itemstack)).getDescriptionId());
            materialName.withStyle(materialName.getStyle().withColor(color));
            list.add(index+2, new TextComponent(" ").append(materialName.getContents().equals("Air")?new TextComponent("missingno").withStyle(ChatFormatting.ITALIC, ChatFormatting.UNDERLINE):materialName));
        }
    }
}
