package dev.hipposgrumm.armor_trims.config;

import dev.hipposgrumm.armor_trims.Armortrims;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
//? if <1.19 {
/*import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
*///?}

import java.io.IOException;

public class ConfigScreen {
    private static boolean[] vals = new boolean[6];

    public static Screen create(Screen parent) {
        vals = new boolean[] {
                Config.enableNewSmithingGUI,
                Config.dontConsumeSmithingTemplates,
                Config.disableVanillaNetheriteUpgrade,
                Config.disableNetheriteUpgrade,
                Config.enableUntrimming,
                Config.enableJei
        };

        ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(/*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("gui.armor_trims.config.title"));

        if (Config.file.exists()) {
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            builder.getOrCreateCategory(/*? if >=1.19 {*/Component.literal/*?} else {*//*new TextComponent*//*?}*/(""))
                    .addEntry(entryBuilder
                            .startBooleanToggle(/*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("gui.armor_trims.config.option.newgui"), Config.enableNewSmithingGUI)
                            .setTooltip(/*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("gui.armor_trims.config.option.newgui.desc"))
                            .setDefaultValue(true)
                            .setSaveConsumer(val -> {
                                vals[0] = val;
                                Config.enableNewSmithingGUI = val;
                                save();
                            }).build()
                    ).addEntry(entryBuilder
                            .startBooleanToggle(/*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("gui.armor_trims.config.option.consume_templates"), Config.dontConsumeSmithingTemplates)
                            .setTooltip(/*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("gui.armor_trims.config.option.consume_templates.desc"))
                            .setDefaultValue(false)
                            .setSaveConsumer(val -> {
                                vals[1] = val;
                                Config.dontConsumeSmithingTemplates = val;
                                save();
                            }).build()
                    ).addEntry(entryBuilder
                            .startBooleanToggle(/*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("gui.armor_trims.config.option.disable_vanilla"), Config.disableVanillaNetheriteUpgrade)
                            .setTooltip(/*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("gui.armor_trims.config.option.disable_vanilla.desc"))
                            .setDefaultValue(true)
                            .setSaveConsumer(val -> {
                                vals[2] = val;
                                Config.disableVanillaNetheriteUpgrade = val;
                                save();
                            }).build()
                    ).addEntry(entryBuilder
                            .startBooleanToggle(/*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("gui.armor_trims.config.option.disable_modded"), Config.disableNetheriteUpgrade)
                            .setTooltip(/*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("gui.armor_trims.config.option.disable_modded.desc"))
                            .setDefaultValue(false)
                            .setSaveConsumer(val -> {
                                vals[3] = val;
                                Config.disableNetheriteUpgrade = val;
                                save();
                            }).build()
                    ).addEntry(entryBuilder
                            .startBooleanToggle(/*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("gui.armor_trims.config.option.untrimming"), Config.enableUntrimming)
                            .setTooltip(/*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("gui.armor_trims.config.option.untrimming.desc"))
                            .setDefaultValue(true)
                            .setSaveConsumer(val -> {
                                vals[4] = val;
                                Config.enableUntrimming = val;
                                save();
                            }).build()
                    ).addEntry(
                            entryBuilder.startBooleanToggle(
                                            /*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("gui.armor_trims.config.option.jei"),
                                            Config.enableJei
                                    )
                                    .setTooltip(/*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("gui.armor_trims.config.option.jei.desc"))
                                    .setDefaultValue(true)
                                    .setSaveConsumer(val -> {
                                        vals[5] = val;
                                        Config.enableJei = val;
                                        save();
                                    }).build()
                    );
        } else {
            builder.alwaysShowTabs().getOrCreateCategory(/*? if >=1.19 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/("gui.armor_trims.config.absent"));
        }

        return builder.build();
    }

    private static void save() {
        try {
            String contents = Config.writeConfig(vals);
            Config.watcher.skipUpdate();
            Config.changeConfig(contents);
        } catch (IOException e) {
            Armortrims.LOGGER.error("Error saving to config file {}.toml", Armortrims.LOGGER, e);
        }
    }
}
